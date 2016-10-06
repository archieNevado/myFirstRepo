package com.coremedia.blueprint.themeimporter;

import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ThemeImporterUnitTest {

  // --- Tests ------------------------------------------------------

  @Test
  public void testNameOnly() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background: url(bigplay.svg) no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testQuotes() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background: url('bigplay.svg') no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testDoubleQuotes() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background: url(\"bigplay.svg\") no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testNewlines() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("foo\nbackground: url(bigplay.svg) no-repeat;\nbar");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testMultipleMatches() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background: url(bigplay.svg) no-repeat;\nforeground: url('bigplay.svg') no-repeat;\nunderground: url(\"bigplay.svg\") no-repeat;");
    checkNameOnly(matcher);
    checkNameOnly(matcher);
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testRelativeUrl() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background-image: url(../img/button_video_play.png);");
    assertTrue(matcher.find());
    assertEquals("../img/button_video_play.png", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../img/button_video_play.png", matcher.group(4));
    assertTrue(matcher.group(5).isEmpty());
    assertFalse(matcher.find());
  }

  @Test
  public void testQuerySuffix() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("src: url(\"../fonts/bootstrap/glyphicons-halflings-regular.eot?#iefix\") format(\"embedded-opentype\"),");
    assertTrue(matcher.find());
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot?#iefix", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot", matcher.group(4));
    assertEquals("?#iefix", matcher.group(5));
    assertFalse(matcher.find());
  }

  @Test
  public void testFragmentSuffix() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("src: url(\"../fonts/bootstrap/glyphicons-halflings-regular.eot#bla\") format(\"embedded-opentype\"),");
    assertTrue(matcher.find());
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot#bla", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot", matcher.group(4));
    assertEquals("#bla", matcher.group(5));
    assertFalse(matcher.find());
  }

  @Test
  public void testBase64Encoding() {
    Matcher matcher = ThemeImporter.URL_PATTERN.matcher("background: url(data:image/svg+xml;base64,PD94bWwgBlaBlaBla8L3N2Zz4=);");
    assertTrue(matcher.find());
    assertEquals("data:image/svg+xml;base64,PD94bWwgBlaBlaBla8L3N2Zz4=", matcher.group(1));
    assertEquals("data", matcher.group(3));
    assertEquals("image/svg+xml;base64,PD94bWwgBlaBlaBla8L3N2Zz4=", matcher.group(4));
    assertTrue(matcher.group(5).isEmpty());
    assertFalse(matcher.find());
  }

  /**
   * Check that urlsToXlinks correctly concats the transformed matches with the
   * text in between.  The actual url transformation is not relevant here.
   */
  @Test
  public void testUrlsToXlinks() {
    String css = "@media screen and (min-width: 1025px) {\n" +
            "  .cm-nav-collapse__gradiant {\n" +
            "    bottom: -20px;\n" +
            "    background: url(http://example.org/path/to/pic.jpg);\n" +
            "    width: 100%;\n" +
            "    background: url(data:image/svg+xml;base64,PD94bWwgFooBarL3N2Zz4=);\n" +
            "    position: absolute;\n" +
            "  }\n" +
            "}\n";
    String expected = "@media screen and (min-width: 1025px) {\n" +
            "  .cm-nav-collapse__gradiant {\n" +
            "    bottom: -20px;\n" +
            "    background: url(<a xlink:href=\"http://example.org/path/to/pic.jpg\">http://example.org/path/to/pic.jpg</a>);\n" +
            "    width: 100%;\n" +
            "    background: url(data:image/svg+xml;base64,PD94bWwgFooBarL3N2Zz4=);\n" +
            "    position: absolute;\n" +
            "  }\n" +
            "}\n";
    ThemeImporter testling = new ThemeImporter(null, null);  // NOSONAR nulls are sufficient for this test
    String actual = testling.urlsToXlinks(css, null);
    assertEquals(expected, actual);
  }


  // --- internal ---------------------------------------------------

  private void checkNameOnly(Matcher matcher) {
    assertTrue(matcher.find());
    assertEquals("bigplay.svg", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("bigplay.svg", matcher.group(4));
    assertTrue(matcher.group(5).isEmpty());
  }
}
