package calc;

import java.util.List;

/**
 * {@code begin} and {@code stop} in this file means <b>"inclusive start index"</b>, <b>"exclusive end index"</b>
 */
public interface Lists {
    static <E> List<E> subsequence(List<E> receiver, int begin, int stop) { return receiver.subList(begin, stop); }
}
