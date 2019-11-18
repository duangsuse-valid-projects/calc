package calc

/** Concatenating group of objects by [mplus] (+): `1 + 2 + 3 + ...`
 * + closure: `(1 + 2)` is [Semigroup]
 * + associative: `(1 + 2) + 3 = 1 + (2 + 3)` */
interface Semigroup<B, in A> {
  fun mplus(base: B, value: A): B
}

/** Monoid: [Semigroup] with [mzero] */
interface Monoid<B, in A>: Semigroup<B, A> {
  fun id(o: B): B = o
  fun mzero(): B

  interface SideEffect<B, A>: Monoid<B, A> {
    override fun mplus(base: B, value: A): B = base.also { accept(base, value) }
    fun accept(base: B, value: A)
  }

  companion object Basic {
    fun <E> list() = object : SideEffect<MutableList<E>, E> {
      override fun mzero(): MutableList<E> = mutableListOf()
      override fun accept(base: MutableList<E>, value: E) { base.add(value) }
    }
    fun count(): Monoid<Cnt, *> = object : Monoid<Cnt, Any?> {
      override fun mzero(): Cnt = 0
      override fun mplus(base: Cnt, value: Any?): Cnt = base + 1
    }
    fun <E> hist() = object : SideEffect<MutableMap<E, Cnt>, E> {
      override fun mzero(): MutableMap<E, Cnt> = mutableMapOf()
      override fun accept(base: MutableMap<E, Cnt>, value: E) { base[value] = base.getOrDefault(value, 0) +1 }
    }
  }
}