package calc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LinkedTestRecurseList {
  @Test fun basic() {
    val ll = linkedListOf(1, 2, 3)
    assertEquals(1, ll[0])
    assertEquals(2, ll[1])
    ll[1] = -1
    assertEquals(-1, ll[1])
  }
  @Test fun indexCheck() {
    var gotExcept = false
    val ll = linkedListOf(1, 2, 3)
    assertEquals(3, ll[2])
    try { ll[3] } catch (_: IndexOutOfBoundsException) { gotExcept = true }
    assertTrue(gotExcept)
  }
  @Test fun indexedSize() {
    val ll = linkedListOf(1, 2, 3)
    val ll_ = ll.offset(0)
    assertEquals(ll_, ll)
    val ll1 = ll.offset(1)
    assertEquals(2, ll1.size)
    assertEquals(1, ll1.offset(1).size)
    assertEquals(0, ll1.offset(2).size)
  }
}