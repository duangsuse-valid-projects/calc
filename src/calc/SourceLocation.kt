package calc

/** Source location of (with [position]) `<file>:<line>:<column>` */
data class SourceLocation(val file: String, var position: Idx, var line: Cnt, var column: Cnt): Cloneable {
  constructor(file: String): this(file, 0, 1, 1)
  val tag: String get() = "$file:$line:$column"
  override fun toString(): String = "$tag #$position"
  public override fun clone(): SourceLocation = copy(file = file, position = position, line = line, column = column)
}