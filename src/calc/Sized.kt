package calc

typealias Cnt = Int
typealias Idx = Int

interface Sized { val size: Cnt }
val Sized.lastIndex: Idx get() = size - 1
val Sized.isEmpty: Boolean get() = size == 0
val Sized.isNotEmpty get() = !isEmpty
