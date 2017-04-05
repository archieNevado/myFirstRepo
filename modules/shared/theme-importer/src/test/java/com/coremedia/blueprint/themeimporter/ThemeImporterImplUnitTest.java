package com.coremedia.blueprint.themeimporter;

import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ThemeImporterImplUnitTest {

  // --- Tests ------------------------------------------------------

  @Test
  public void testNameOnly() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background: url(bigplay.svg) no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testQuotes() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background: url('bigplay.svg') no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testDoubleQuotes() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background: url(\"bigplay.svg\") no-repeat;");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testNewlines() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("foo\nbackground: url(bigplay.svg) no-repeat;\nbar");
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testMultipleMatches() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background: url(bigplay.svg) no-repeat;\nforeground: url('bigplay.svg') no-repeat;\nunderground: url(\"bigplay.svg\") no-repeat;");
    checkNameOnly(matcher);
    checkNameOnly(matcher);
    checkNameOnly(matcher);
    assertFalse(matcher.find());
  }

  @Test
  public void testRelativeUrl() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background-image: url(../img/button_video_play.png);");
    assertTrue(matcher.find());
    assertEquals("../img/button_video_play.png", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../img/button_video_play.png", matcher.group(4));
    assertTrue(matcher.group(5).isEmpty());
    assertFalse(matcher.find());
  }

  @Test
  public void testQuerySuffix() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("src: url(\"../fonts/bootstrap/glyphicons-halflings-regular.eot?#iefix\") format(\"embedded-opentype\"),");
    assertTrue(matcher.find());
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot?#iefix", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot", matcher.group(4));
    assertEquals("?#iefix", matcher.group(5));
    assertFalse(matcher.find());
  }

  @Test
  public void testFragmentSuffix() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("src: url(\"../fonts/bootstrap/glyphicons-halflings-regular.eot#bla\") format(\"embedded-opentype\"),");
    assertTrue(matcher.find());
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot#bla", matcher.group(1));
    assertNull(matcher.group(3));
    assertEquals("../fonts/bootstrap/glyphicons-halflings-regular.eot", matcher.group(4));
    assertEquals("#bla", matcher.group(5));
    assertFalse(matcher.find());
  }

  @Test
  public void testBase64Encoding() {
    Matcher matcher = ThemeImporterImpl.URL_PATTERN.matcher("background: url(data:image/svg+xml;base64,PD94bWwgBlaBlaBla8L3N2Zz4=);");
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
    ThemeImporterImpl testling = new ThemeImporterImpl(null, null, null);  // NOSONAR nulls are sufficient for this test
    String actual = testling.urlsToXlinks(css, null, null);
    assertEquals(expected, actual);
  }

  @Test
  public void testFormatDescription() {
    assertEquals("single line", ThemeImporterImpl.formatDescription("single line", 512));
    assertEquals("One line Another line", ThemeImporterImpl.formatDescription("One line\n  Another line", 512));
    assertEquals("First paragraph", ThemeImporterImpl.formatDescription("First paragraph\n\nAnother paragraph", 512));
    assertEquals("That's it", ThemeImporterImpl.formatDescription("That's \nit\n\nbla bla\nblub\n\nfoo\nbar", 512));
    assertEquals("Shortened at blank ...", ThemeImporterImpl.formatDescription("Shortened\n   at blank exactly", 22));
    assertEquals("Shortened at blank ...", ThemeImporterImpl.formatDescription("Shortened\n   at blank inexactly", 23));
    assertEquals("desparatelyshortened...", ThemeImporterImpl.formatDescription("desparatelyshortenedhere", 23));
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
