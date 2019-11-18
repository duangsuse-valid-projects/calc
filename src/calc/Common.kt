package calc

internal inline fun <reified T: Any> equality(crossinline predicate: T.(T) -> Boolean)
    : T.(Any?) -> Boolean = eql@{ other ->
  if (this === other) return@eql true
  if (this.javaClass != other?.javaClass) return@eql false
  other as T
  this.predicate(other)
}

internal inline val IntRange.stop: Int get() = last +1

internal fun impossible(): Nothing = throw IllegalStateException()
