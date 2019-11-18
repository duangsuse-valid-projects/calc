package calc

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AbstractTraceTest {

  @Test
  fun reset() {
  }

  @Test
  fun inc() {
  }

  @Test fun append() {
    val strs = LinkedTrace<String>()
    strs.add("abc")
    assertEquals(0, strs.position)
    assertEquals(0, strs.head)
    assertEquals(1, strs.size)
    assertTrue(strs.isHead)
    assertEquals("abc", strs.thisFrame)
  }

  @Test fun contains() {
    val abcs = LinkedTrace<Char>()
    "a,b,c".split(',').map { it[0] }.forEach(abcs::add)
    assertEquals('c', abcs.thisFrame)
    assertTrue('a' in abcs)
    abcs.reset(0)
    assertFalse('c' in abcs)
    assertTrue('b' in abcs)
    abcs.reset(1)
    assertFalse('b' in abcs)
  }
}