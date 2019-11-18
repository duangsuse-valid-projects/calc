package calc.inst

interface OpAssoc {
  val prec_l: Int
  val prec_r: Int
  val notation: String
}