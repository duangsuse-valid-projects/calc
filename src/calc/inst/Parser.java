package calc.inst;

import calc.SourceLocation;
import calc.inst.Ops.*;

import java.util.EnumSet;

import static calc.inst.Ops.*;

// TODO Lexer should be a stream of tokens
// TODO StreamFeeder
public class Parser extends Lexer {
    public Parser(String file, CharSequence s) { super(file, s); }

    // Expr = Atom | InfixChain
    public Ast expr() {
        Ast zero = scanAtom();
        InfixOp left_op;
        try {
            left_op = scanInfixOp();
        } catch (Error _e) { return zero; }
        return chainPlus(zero, left_op);
    }
    Ast chainPlus(Ast base, InfixOp left) {
        Ast rhs = null; // base `left` 1
        InfixOp rhs_op = null; // base `left` 1 right
        try {
            rhs = scanAtom();
            if(rhs==null) return base;
            mark(); rhs_op = scanInfixOp();
        } catch (Error _e) { reset(); return left.fold.concat(base, rhs); }
        switch (compare(left.prec_l, rhs_op.prec_r)) {
            case EQ: case LT:
                return chainPlus(left.fold.concat(base, rhs), rhs_op);
            case GT:
                Ast right = chainPlus(rhs, rhs_op);
                return left.fold.concat(base, right);
        }
        return impossible();
    }

    public InfixOp scanInfixOp() {
        TokenType tt = token();
        if (EnumSet.of(TokenType.Literal, TokenType.ParenL, TokenType.ParenR).contains(tt))
            { return fail("unexpected literal"); }
        return infixMap.get(tt);
    }

    public Ast scanAtom() {
        TokenType tt = token();
        if (tt == TokenType.ParenL)
            { return scanParenExpr(); }
        if (tt != TokenType.Literal) { return fail("unexpected operator"); }
        return new Ast.Lit(literalValue);
    }

    public Ast scanParenExpr() {
        SourceLocation slocBegin = sloc.clone();
        Ast inner = expr();
        if (getPeek() != ')') return fail("unterminated paren from " +slocBegin.getTag());
        return inner;
    }
}
