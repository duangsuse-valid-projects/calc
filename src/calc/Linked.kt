package calc

import calc.Linked.Nil
import calc.Linked.Cons

/** Last-in, first-out collection */
interface Stack<E>: Sized, Iterable<E> {
  fun add(element: E)
  fun remove(): E?
  override fun iterator(): MutableIterator<E>
}

/** [MutableIterator] with [secondPeek]-1 and [removeNext] */
interface PeekMutableIterator<out E>: MutableIterator<E> {
  /** Item will be returned by [next],
   * + throws [IllegalStateException] when called before first-time calling [next]
   * + Fails when [hasNext] is not true */
  val secondPeek: E
  fun removeNext()
}
object NilIterator: PeekMutableIterator<Nothing> {
  override fun hasNext(): Boolean = false
  override fun next(): Nothing = throw EndOfStream()
  override fun remove() {}
  override val secondPeek: Nothing get() = throw EndOfStream()
  override fun removeNext() {}
}

/** A list with right-hand-side-only subsequences */
interface RecurseList<E> {
  /** `null` if the sublist is empty */
  val head: E?
  fun assign(value: E)
  fun remove(): E?
  /** Get a __pretty new__ [RecurseList] at offset [n] */
  fun offset(n: Idx): RecurseList<E>
}
operator fun <E> RecurseList<E>.get(index: Idx): E = offset(index).head ?: throw IndexOutOfBoundsException("$index")
operator fun <E> RecurseList<E>.set(index: Idx, value: E) {
  offset(index).let {
    if (it.head != null) it.assign(value)
    else throw IndexOutOfBoundsException("$index") } }

/** Mutable link `(x -> xs)` */
sealed class Linked<out E> {
  data class Cons<E>(var head: E, var tail: Linked<E>): Linked<E>() {
    override fun toString(): String = "$head:$tail"
  }
  object Nil: Linked<Nothing>() {
    override fun toString(): String = "[]"
  }

  /** [Stack] collection based on [Linked] */
  data class List<E>(private var xs: Linked<E> = Nil): Stack<E>, RecurseList<E>, Cloneable {
    override var size: Int = xs.size()
      internal set

    /** `(:) :: List a -> a -> List a`, `(x : xs)` */
    override fun add(element: E) { xs = Cons(element, xs); ++size }
    override fun remove(): E? = xs.takeIf { it.isLink }?.let {
      val (x, xs) = xs.mustLink()
      this.xs = xs; --size
      return@let x
    }
    override fun iterator(): PeekMutableIterator<E> = if (isEmpty) NilIterator else LinkedListMutableIterator(this, this.xs)

    override val head: E? get() = xs.asLink()?.head
    override fun assign(value: E) { xs.mustLink().head = value }

    override fun offset(n: Idx): List<E> {
      check (n >= 0) {"Negative index for linked"}
      if (n > size) throw IndexOutOfBoundsException("max=$size")
      if (n == 0) return clone()
      val deference = clone()
        (1..n).forEach { _ -> deference.run { xs = xs.mustLink().tail } }
      deference.size -= n
      return deference
    }

    /** __NOTE:__ this function __won't__ deep copy the mutable [xs] ([Linked]) of the list */
    public override fun clone(): List<E> = copy(xs = xs)
  }
}
private inline val <E> Linked<E>.isLink get() = this is Cons<E>
private fun <E> Linked<E>.asLink(): Cons<E>? = this as? Cons<E>
private fun <E> Linked<E>.mustLink(): Cons<E> = this as Cons<E>

class LinkedListMutableIterator<E>(private val list: Linked.List<E>, xs: Linked<E>) : PeekMutableIterator<E> {
  private var predecessorM1: Linked.Cons<E>? = null
  private var predecessor: Linked.Cons<E>? = null
  private var self: Linked<E> = xs
  override fun hasNext(): Boolean = self.isLink
  override fun next(): E {
    predecessorM1 = predecessor
    predecessor = self.mustLink()
    val (item, next) = self.mustLink()
    self = next
    return item //self.item
  }
  override fun remove() {
    if (predecessorM1 == null) {
      list.remove() // (a : ^b : c) [predecessor=a], [peek=b]
    } else {
      predecessorM1!!.tail = self.mustLink()
      --list.size
    }
  }
  override val secondPeek: E get() = self.mustLink().head
  override fun removeNext() {
    if (predecessor == null) {
      list.remove()
    } else {
      predecessor!!.tail = self.mustLink().tail
      --list.size
    }
  }
}
inline val nil get() = Nil
fun <E> linkedOf(vararg item: E): Linked<E> = item.foldRight(nil, ::Cons)
fun <E> linkedListOf(vararg item: E): Linked.List<E> = Linked.List(linkedOf(*item))

fun Linked<*>.size(): Cnt = when (this) {
  is Nil -> 0
  is Cons -> 1 + tail.size()
}
infix fun <E> Linked<E>.`+l`(x: E): Linked<E> = Cons(x, this)
infix fun <E> Linked<E>.`++r`(x: E): Linked<E> = when (this) {
  is Nil -> Cons(x, Nil)
  is Cons -> tail.`++r`(x) `+l` head
}
infix fun <E> Linked<E>.`++`(xs: Linked<E>): Linked<E> = when (xs) {
  is Nil -> this
  is Cons -> (this `++r` xs.head) `++` xs.tail
}

