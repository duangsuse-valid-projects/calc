package calc

/** Iterable with cloneable iterator [Iterator],
 * iteration based on this kind of iterator could be saved and resumed later */
interface CloneIterable<out E>: Iterable<E> {
  interface Iterator<out E>: kotlin.collections.Iterator<E>, Cloneable
    { public override fun clone(): Iterator<E> }
  override fun iterator(): Iterator<E>
}

/** A continuous view of array-like objects [Array], [List], [CharSequence] */
interface Slice<out E>: Sized, CloneIterable<E> {
  operator fun get(index: Idx): E
  operator fun get(range: IntRange): Slice<E>

  data class OfArray<E>(private val ary: Array<E>): Slice<E> {
    override val size: Cnt get() = ary.size
    override fun get(index: Idx): E = ary[index]
    override fun get(range: IntRange): Slice<E> = this.ary.sliceArray(range).let(::OfArray)

    override fun equals(other: Any?): Boolean = equality<OfArray<*>> { ary.contentEquals(it.ary) }(other)
    override fun hashCode(): Int = ary.contentHashCode()
    override fun toString(): String = "Slice${ary.contentToString()}"
  }
  data class OfList<E>(private val list: List<E>): Slice<E> {
    override val size: Cnt get() = list.size
    override fun get(index: Idx): E = list[index]
    override fun get(range: IntRange): Slice<E> = OfList(Lists.subsequence(this.list, range.first, range.stop))

    override fun toString(): String = "Slice$list"
  }
  data class OfCharSeq(private val seq: CharSequence): Slice<Char> {
    override val size: Cnt get() = seq.length
    override fun get(index: Idx): Char = seq[index]
    override fun get(range: IntRange): Slice<Char> = this.seq.subSequence(range.first, range.stop).let(::OfCharSeq)

    override fun toString(): String = "Slice`$seq'"
  }

  override fun iterator(): SliceIterator<E> = SliceIterator(this)
  data class SliceIterator<out E>(private val slice: Slice<E>, var position: Idx = 0): CloneIterable.Iterator<E> {
    override fun hasNext(): Boolean = position != slice.size
    override fun next(): E = slice[position++]
    override fun clone(): CloneIterable.Iterator<E> = copy(slice = slice, position = position)
    override fun toString(): String = "$slice@[$position]"
  }

  companion object Factory {
    fun <E> of(vararg item: E) = OfArray(item)
    fun <E> of(list: List<E>) = OfList(list)
    fun of(str: CharSequence) = OfCharSeq(str)
  }

  fun indexInbound(index: Idx) = index.coerceAtLeast(0).takeIf { it <= lastIndex } ?: lastIndex
  fun getInbound(index: Idx) = this[indexInbound(index)]
  fun getInbound(range: IntRange) = this[this.indexInbound(range.first)..this.indexInbound(range.last)]
}
