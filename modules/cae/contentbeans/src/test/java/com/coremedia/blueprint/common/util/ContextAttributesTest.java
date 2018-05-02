package com.coremedia.blueprint.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContextAttributesTest {
  @Test
  public void testTypedNull() {
    assertNull(ContextAttributes.typed(null, Object.class));
  }

  @Test
  public void testTypedSame() {
    String expected = "foo";
    String actual = ContextAttributes.typed(expected, String.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testTypedSuper() {
    String expected = "foo";
    Object actual = ContextAttributes.typed(expected, Object.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testTypedMismatch() {
    String unexpected = "foo";
    Integer actual = ContextAttributes.typed(unexpected, Integer.class);
    assertNull(actual);
  }
}
