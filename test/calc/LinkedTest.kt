package calc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LinkedTest {
  @Test fun addRemove() {
    val xs = Linked.List<Int>()
    xs.add(1)
    assertTrue(xs.iterator().hasNext())
    assertEquals(1, xs.remove())
    assertFalse(xs.iterator().hasNext())

    assertNull(xs.remove())
    assertNull(xs.remove())
  }
  @Test fun addRemove_Size_Order() {
    val xs = Linked.List<String>()
    assertEquals(0, xs.size)
    listOf("a", "b", "c").forEach(xs::add)
    assertEquals(3, xs.size)
    xs.add("4")
    assertEquals(4, xs.size)
    assertEquals("a,b,c,4".split(',').reversed(), (1..4).map { xs.remove() })
    assertNull(xs.remove())
    assertEquals(0, xs.size)
  }
  @Test fun mutIterator_FirstRemove_Size() {
    val xs = Linked.List<Int>()
    val list = listOf(1,2,3)
    list.forEach(xs::add)
    assertEquals(list.reversed(), xs.toList())
    removeFirstByMut(xs)
    assertEquals(listOf(1,2).reversed(), xs.toList())
    removeFirstByMut(xs)
    assertEquals(listOf(1), xs.toList())
    assertEquals(1, xs.size)
    removeFirstByMut(xs)
    assertEquals(0, xs.size)
  }
  private fun <E> removeFirstByMut(xs: Linked.List<E>): Unit = xs.iterator().let { it.next(); it.remove() }

  @Test fun mutIteratorMidRemove_BackCollection() {
    val xs = Linked.List<String>()
    "a,b,c,d".split(',').forEach(xs::add)
    val mut = xs.iterator()
    assertEquals(mut.next(), "d")
    assertEquals(mut.next(), "c")
    mut.remove()
    assertEquals(listOf("a", "b", "d").reversed(), xs.toList())
  }
  @Test fun mutIteratorEndRemove() {
    val xs = Linked.List(nil `+l` 1 `+l` 2)
    val mut = xs.iterator()
    assertEquals(2, mut.next())
    mut.remove()
    assertEquals(1, xs.size)
    assertEquals(mut.next(), 1)
    xs.iterator().remove()
    assertEquals(0, xs.size)
  }
  @Test fun peekMutIterator() {
    val xs = Linked.List(nil `+l` 'a' `+l` 'b')
    val mut = xs.iterator()
    mut.next()
    assertEquals('a', mut.secondPeek)
    assertEquals('a', mut.secondPeek)
    mut.removeNext()
    assertEquals(mut.next(), 'a')
    assertEquals(listOf('b'), xs.toList())
  }
  @Test fun functions() {
    assertEquals(3, nil.`+l`(1).`+l`(2).`+l`(3).size())
    assertEquals(listOf(3, 4), Linked.List(nil `+l` 4 `+l` 3).toList())
    assertEquals(linkedOf(1, 2, 3), linkedOf(1, 2, 3))
    assertEquals(linkedOf(1, 2, 3), linkedOf(1, 2) `++r` 3)
    assertEquals(linkedOf('a', 'b', 'c', 'd'), linkedOf('a', 'b') `++` linkedOf('c', 'd'))
  }
}