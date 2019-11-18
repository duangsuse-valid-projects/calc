package calc.inst

import calc.inst.Ast.Binary.*

object Evaluator : Ast.Visitor<Number> {
  override fun see(t: Ast.Lit): Number = t.num
  override fun see(t: Add): Number = binEval(Number::plus, t)
  override fun see(t: Sub): Number = binEval(Number::minus, t)
  override fun see(t: Mul): Number = binEval(Number::times, t)
  override fun see(t: Div): Number = binEval(Number::div, t)

  private inline fun binEval(crossinline binOp: Number.(Number) -> Number, tree: Ast.Binary): Number
        = eval(tree.l).binOp(eval(tree.r))
  private fun eval(t: Ast): Number = t.accept(this)
}

fun Int.plus(n: Number): Number = when (n) {
  is Int -> this + n
  is Long -> this + n
  else -> coerceFail(n)
}
fun Int.minus(n: Number): Number = when (n) {
  is Int -> this - n
  is Long -> this - n
  else -> coerceFail(n)
}
fun Int.times(n: Number): Number = when (n) {
  is Int -> this * n
  is Long -> this * n
  else -> coerceFail(n)
}
fun Int.div(n: Number): Number = when (n) {
  is Int -> this / n
  is Long -> this / n
  else -> coerceFail(n)
}
fun Long.plus(n: Number): Number = when (n) {
  is Int -> this + n
  is Long -> this + n
  else -> coerceFail(n)
}
fun Long.minus(n: Number): Number = when (n) {
  is Int -> this - n
  is Long -> this - n
  else -> coerceFail(n)
}
fun Long.times(n: Number): Number = when (n) {
  is Int -> this * n
  is Long -> this * n
  else -> coerceFail(n)
}
fun Long.div(n: Number): Number = when (n) {
  is Int -> this / n
  is Long -> this / n
  else -> coerceFail(n)
}

private fun coerceFail(n: Number): Nothing = throw Error("Unknown type ${n::class}")

fun Number.plus(n: Number) = when (this) {
  is Int -> this.plus(n)
  is Long -> this.plus(n)
  else -> coerceFail(this)
}
fun Number.minus(n: Number) = when (this) {
  is Int -> this.minus(n)
  is Long -> this.minus(n)
  else -> coerceFail(this)
}
fun Number.times(n: Number) = when (this) {
  is Int -> this.times(n)
  is Long -> this.times(n)
  else -> coerceFail(this)
}
fun Number.div(n: Number) = when (this) {
  is Int -> this.div(n)
  is Long -> this.div(n)
  else -> coerceFail(this)
}
