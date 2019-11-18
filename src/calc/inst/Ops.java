package calc.inst;

import java.util.HashMap;
import java.util.Map;
import static calc.inst.Ops.Assoc.*;
import static calc.inst.Ast.Binary.*;

public final class Ops {
    enum Ordering { LT, EQ, GT }
    static Ordering compare(int a, int b) {
        if (a > b) return Ordering.GT;
        else if (a == b) return Ordering.EQ;
        else return Ordering.LT;
    }

    enum Assoc { left, right }
    interface OpPlus { Ast concat(Ast l, Ast r); }
    enum InfixOp implements OpAssoc {
        Add(left, 3, Lexer.TokenType.Add, Add::new),
        Sub(left, 3, Lexer.TokenType.Sub, Sub::new),
        Mul(left, 1, Lexer.TokenType.Mul, Mul::new),
        Div(left, 1, Lexer.TokenType.Div, Ast.Binary.Div::new);

        InfixOp(Assoc assoc, int precedence, Lexer.TokenType id, OpPlus monoid) {
            if (assoc == Assoc.left) { prec_l = precedence-1; prec_r = precedence; }
            else if (assoc == Assoc.right) { prec_l = precedence; prec_r = precedence-1; }
            this.id = id; this.fold = monoid; };
        int prec_l, prec_r;
        final Lexer.TokenType id; final OpPlus fold;

        @Override
        public int getPrec_l() { return prec_l; }
        @Override
        public int getPrec_r() { return prec_r; }
        @Override
        public String getNotation() { return String.valueOf(id.keyChar); }
    }
    static Map<Lexer.TokenType, InfixOp> infixMap = new HashMap<>();
    static { for (InfixOp op : InfixOp.values()) infixMap.put(op.id, op); }

    static <R> R impossible() { assert false; return null; }
}
