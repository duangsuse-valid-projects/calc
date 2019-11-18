package calc

import java.util.function.Supplier
typealias Producer<R> = Supplier<out R>

class EndOfStream(): Error("End of stream")
interface FiniteStream { val isEnded: Boolean }
/** When [isEnded], call [consume] results [EndOfStream] error */
interface PeekStream<out E>: FiniteStream, Iterable<E> {
  val peek: E; fun consume(): E

  interface Viewport<out E>: Slice<E> { fun consume(): Slice<E> }
  fun take(n: Cnt): Viewport<E>

  override fun iterator(): Iterator<E> = PeekIterator(this)
}
class PeekIterator<out E>(private val peek: PeekStream<E>): Iterator<E> {
  override fun hasNext(): Boolean = !peek.isEnded
  override fun next(): E = peek.consume()
}

/** Kind of object with capacity of saving/restoring its state,
 * __with not__ the capacity of multi-marking before calling [reset]. */
interface MarkReset {
  fun mark() fun reset()
  /** [MarkReset] with multi-mark capacity */
  interface Multi: MarkReset
  fun <R> positional(op: Producer<R>): R = mark().let { op.get() }.also { reset() }
}

/**
 * Left-Right, Look-head-1 input stream for _LL(1)_ parsers.
 *
 * This model comes with [peek] / [consume] feature
 * + [s] __cannot be empty__, or constructor will throw [IllegalStateException]
 * + when [isEnded], calling [consume] results [EndOfStream]
 * + [onItem] is called on [consume] for each input, [onEnd] is called when consuming
 *   last item __exactly once__ _except_ [MarkReset] is used, and when calling [onEnd], [isEnded] should be true
 */
open class Feeder<E>(private val s: Slice<E>): PeekStream<E>, MarkReset.Multi {
  private val sliders = Linked.List<Slice.SliceIterator<E>>()
  private var slider: Slice.SliceIterator<E> = s.iterator()
  private val savedPeeks = Linked.List<E>()
  private var didFinalConsume: Boolean = false
  override val isEnded: Boolean get() = didFinalConsume && !slider.hasNext()

  init { check(s.isNotEmpty) {"Empty slice"} }
  final override var peek: E = slider.next() //initial
    private set
  override fun consume(): E = peek.also {
    if (slider.hasNext()) {
      peek = slider.next().also(::onItem)
    } else if (!didFinalConsume) {
      didFinalConsume = true; onEnd()
    } else throw EndOfStream()
  }

  override fun take(n: Cnt): PeekStream.Viewport<E> {
    val newPos = slider.position+n //exclusive
    if (newPos > this@Feeder.s.size) throw EndOfStream()
    val took = this@Feeder.s[slider.position until newPos]
    return object : PeekStream.Viewport<E>, Slice<E> by took {
      override fun consume(): Slice<E> = took.also { slider.position += n }
    }
  }

  /** On every single item */
  protected open fun onItem(item: E) {}
  /** Before calling next [consume] actually throwing exception */
  protected open fun onEnd() {}

  // TODO synthesis mark/reset boilerplate
  override fun mark() {
    if (isEnded) throw EndOfStream()
    sliders.add(slider.clone() as Slice.SliceIterator<E>)
    savedPeeks.add(peek) }
  override fun reset() {
    fun error(): Nothing = throw IllegalStateException("no reset")
    didFinalConsume = false
    slider = sliders.remove() ?: error()
    peek = savedPeeks.remove() ?: impossible()
  }

  override fun toString(): String = "Feeder($s)"

  companion object Factory {
    fun of(str: CharSequence) = Feeder(Slice.of(str))
    fun <E> of(vararg item: E) = Feeder(Slice.of(*item))
    fun <E> of(list: List<E>) = Feeder(Slice.of(list))
  }
}