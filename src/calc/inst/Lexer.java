package calc.inst;

import calc.AbstractLexer;
import calc.Monoid;
import calc.Slice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static calc.inst.Ops.impossible;

public class Lexer extends AbstractLexer {
    public Lexer(String file, CharSequence s) { super(file, new Slice.OfCharSeq(s)); }

    public Number literalValue;

    public TokenType token() {
        dropWhile(Character::isWhitespace);
        if (Character.isDigit(getPeek())) {
            scanLiteral();
            return TokenType.Literal;
        } else { if (opMap.containsKey(getPeek())) {
            return opMap.get(consume());
        } }
        return fail("unknown");
    }

    public void scanLiteral() {
        literalValue = takeWhile(Character::isDigit, new Monoid<Long, Character>() {
            @Override public Long id(Long o) { return o; }
            @Override public Long mzero() { return 0L; }
            @Override public Long mplus(Long base, Character value) { return base*10 + (value - '0'); }
        });
    }

    enum ValueKind { Name, ConstNum }
    enum TokenType {
        Add('+'), Sub('-'), Mul('*'), Div('/'), Literal,
        ParenL('('), ParenR(')');
        char keyChar;
        TokenType(char keyChar) { this.keyChar = keyChar; }
        TokenType() { this(' '); }
        boolean hasValue() { return this == Literal; }
        ValueKind valueKind() { return ValueKind.ConstNum; }
    }
    static Map<Character, TokenType> opMap = new HashMap<>();
    static { for (TokenType tt : TokenType.values()) opMap.put(tt.keyChar, tt); }
    Object getValue(ValueKind k) { switch (k) {
        case ConstNum: return literalValue;
        default: return impossible();
    } }

    <R> R fail(String extra) throws RuntimeException { throw new Error("Parser fail at " + sloc.toString() + ": `" + getPeek() + "' " + extra); }

    public List<Object> tokens() {
        List<Object> taken = new LinkedList<>();
        while (!isEnded()) {
            TokenType tok = token();
            if (tok.hasValue()) taken.add(getValue(tok.valueKind()));
            else taken.add(tok);
        }
        return taken;
    }
}
