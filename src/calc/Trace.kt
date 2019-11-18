package calc

interface BasicCollection<E>: Sized {
  fun add(element: E)
  fun remove(): E
  operator fun contains(element: E): Boolean
}

interface Trace<E>: BasicCollection<E> {
  val position: Idx
  val head: Idx
  val thisFrame: E
  fun reset(pos: Idx)
  fun append(item: E)
  fun inc(item: E)
}

abstract class AbstractTrace<E>: Trace<E> {
  override var position: Idx = (-1)
  override var head: Idx = position
    protected set
  val isHead: Boolean get() = position == head
  override fun reset(pos: Idx) { position = pos }
  override fun inc(item: E) {
    check(isHead) {"Inc at $position (HEAD $size)"}
    append(item)
  }
  override fun append(item: E) {
    if (isHead) ++head
    ++position
  }
  override fun add(element: E) { append(element) }
}

/** (head) (position) <- ... (append/remove) */
class LinkedTrace<E>(private val history: Linked.List<E> = linkedListOf()): AbstractTrace<E>() {
  override val size: Cnt get() = history.size
  private val alloc: Int get() = lastIndex - position

  override val thisFrame: E get() = history[alloc]!!
  override fun append(item: E) {
    history.add(item)
    super.append(item)
  }
  override fun remove(): E = history.offset(alloc).remove()!!
  override fun contains(element: E): Boolean = element in history.offset(alloc)
}
