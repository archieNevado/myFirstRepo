package com.coremedia.blueprint.themeimporter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ThemeImporterContentHelperTest {
  private ThemeImporterContentHelper testling = new ThemeImporterContentHelper(null);


  // --- property parsing -------------------------------------------

  @Test
  public void testEmptyLines() {
    assertNull(testling.parseProperty(""));
    assertNull(testling.parseProperty("  "));
    assertNull(testling.parseProperty("\u0009 "));
  }

  @Test
  public void testComments() {
    assertNull(testling.parseProperty("#"));
    assertNull(testling.parseProperty(" # foo # "));
    assertNull(testling.parseProperty("\u0009 # foo"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalLine() {
    testling.parseProperty("foo");
  }

  @Test
  public void testSimpleProperties() {
    ThemeImporterContentHelper.KeyValue result = testling.parseProperty("foo=bar");
    checkProperty(result, "foo", "bar");
    result = testling.parseProperty("foo.bar=bar");
    checkProperty(result, "foo.bar", "bar");
  }

  @Test
  public void testTrimSimpleProperties() {
    ThemeImporterContentHelper.KeyValue result = testling.parseProperty(" foo = bar ");
    checkProperty(result, "foo", "bar");
  }

  @Test
  public void testEscapedProperties() {
    // Double \\ in String literal corresponds to single \ read from InputStream
    String line = "foo=\\u00C4 \\u00D6 \\u00DC \\u00E4 \\u00F6 \\u00FC \\u00DF";
    ThemeImporterContentHelper.KeyValue result = testling.parseProperty(line);
    checkProperty(result, "foo", "Ä Ö Ü ä ö ü ß");
  }

  @Test
  public void testBastardPropertiesFromHell() {
    String line = " foo = \\n\\u0020=bar \\n\\u0020 ";
    ThemeImporterContentHelper.KeyValue result = testling.parseProperty(line);
    checkProperty(result, "foo", "\n =bar \n ");
  }


  // --- internal ---------------------------------------------------

  private void checkProperty(ThemeImporterContentHelper.KeyValue actual, String expectedKey, String expectedValue) {
    assertEquals("wrong key", expectedKey, actual.key);
    assertEquals("wrong value", expectedValue, actual.value);
  }
}
