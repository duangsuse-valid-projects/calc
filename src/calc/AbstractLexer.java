package calc;

import java.util.function.Predicate;

public abstract class AbstractLexer extends Feeder<Character> {
    protected final SourceLocation sloc;
    public AbstractLexer(String file, Slice<? extends Character> s) { super(s); sloc = new SourceLocation(file); }

    @Override
    protected void onItem(Character item) {
        sloc.setPosition(sloc.getPosition()+1);
        if (isLineSep(item)) {
            sloc.setLine(sloc.getLine()+1);
            sloc.setColumn(0); // last line break: column 0
            drop(LINE_SEP.length - 1);
        } else {
            sloc.setColumn(sloc.getColumn() + 1);
        }
    }

    protected void drop(int n) {
        assert n >= 0;
        while (n != 0) { --n; try { consume(); } catch (EndOfStream _eos) { break; } }
    }
    protected void dropWhile(Predicate<Character> predicate) {
        while (predicate.test(getPeek()))
            try { consume(); }
            catch (EndOfStream _eos) { break; }
    }
    protected <R> R take(int n, Monoid<R, Character> f) {
        assert n >= 0;
        R res = f.mzero();
        while (n != 0) { --n;
            try { res = f.mplus(res, consume()); }
            catch (EndOfStream _eos) { break; } }
        return res;
    }
    protected <R> R takeWhile(Predicate<Character> predicate, Monoid<R, Character> f) {
        R res = f.mzero();
        while (predicate.test(getPeek()))
            try { res = f.mplus(res, consume()); }
            catch (EndOfStream _eos) { break; }
        return res;
    }

    private static boolean isLineSep(char c) {
        for (char sep : LINE_SEP) if (c == sep) { return true; }
        return false;
    }
    private static final char[] LINE_SEP = System.lineSeparator().toCharArray();
}
