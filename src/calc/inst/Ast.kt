package calc.inst

import calc.inst.Ast.Binary.*
import java.lang.StringBuilder

sealed class Ast {
  data class Lit(val num: Number): Ast()
  sealed class Binary: Ast() {
    abstract val l: Ast
    abstract val r: Ast
    data class Add(override val l: Ast, override val r: Ast): Binary()
    data class Sub(override val l: Ast, override val r: Ast): Binary()
    data class Mul(override val l: Ast, override val r: Ast): Binary()
    data class Div(override val l: Ast, override val r: Ast): Binary()
  }
  interface Visitor<out R> {
    fun see(t: Lit): R
    fun see(t: Add): R fun see(t: Sub): R
    fun see(t: Mul): R fun see(t: Div): R
  }
  fun <R> accept(vis: Visitor<R>): R = when (this) {
    is Lit -> vis.see(this)
    is Add -> vis.see(this)
    is Sub -> vis.see(this)
    is Mul -> vis.see(this)
    is Div -> vis.see(this)
  }

  class ShowVisitor(private val shown: StringBuilder = StringBuilder()): Visitor<StringBuilder> {
    override fun see(t: Lit) = showPrec(t)
    override fun see(t: Add) = showPrec(t)
    override fun see(t: Sub) = showPrec(t)
    override fun see(t: Mul) = showPrec(t)
    override fun see(t: Div) = showPrec(t)

    private fun prec(t: Ast): OpAssoc = when (t) {
      is Add -> Ops.InfixOp.Add
      is Sub -> Ops.InfixOp.Sub
      is Mul -> Ops.InfixOp.Mul
      is Div -> Ops.InfixOp.Div
      is Lit -> throw Error()
    }

    private fun showPrec(tree: Ast): StringBuilder {
      if (tree !is Binary) { return shown.append((tree as Lit).num) }
      val invL = if (tree.l !is Lit) prec(tree.l).prec_r < prec(tree).prec_l else false
      val invR = if (tree.r !is Lit) prec(tree.r).prec_l < prec(tree).prec_r else false

      if (invL) shown.append('(')
        showPrec(tree.l)
      if (invL) shown.append(')')
      shown.append(prec(tree).notation)
      if (invR) shown.append('(')
        showPrec(tree.r)
      if (invR) shown.append(')')

      return shown
    }
  }
}
