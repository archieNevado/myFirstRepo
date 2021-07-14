package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.common.util.pagination.PagingRuleType;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import com.coremedia.xml.MarkupUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ParagraphHelperTest {
  private Markup markup;

  @Before
  public void setUp() {
    markup = MarkupFactory.fromString("<div xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.coremedia.com/2003/richtext-1.0'><p>I am markup</p><p>Me too</p></div>");
  }


  @Test
  public void testCreateParagraphs1() {
    List<Markup> markups = ParagraphHelper.createParagraphs(markup);
    Assert.assertEquals(2, markups.size());
    Assert.assertEquals("I am markup", MarkupUtil.asPlainText(markups.get(0)).trim());
    Assert.assertEquals("Me too", MarkupUtil.asPlainText(markups.get(1)).trim());
  }

  @Test
  public void testCreateParagraphs2() {
    List<Markup> markups = ParagraphHelper.createParagraphs(markup, 2);
    Assert.assertEquals(1, markups.size());
    Assert.assertEquals("I am markup\n\nMe too", MarkupUtil.asPlainText(markups.get(0)).trim());
  }

  @Test
  public void testCreateParagraphs3() {
    List<Markup> markups = ParagraphHelper.createParagraphs(markup, 1, "CharactersCountAndNextParagraphRule");
    Assert.assertEquals(2, markups.size());
    Assert.assertEquals("I am markup", MarkupUtil.asPlainText(markups.get(0)).trim());
    Assert.assertEquals("Me too", MarkupUtil.asPlainText(markups.get(1)).trim());
  }

  @Test
  public void testCreateParagraphs4() {
    List<Markup> markups = ParagraphHelper.createParagraphs(markup, 1, PagingRuleType.DelimitingBlockCountRule);
    Assert.assertEquals(2, markups.size());
    Assert.assertEquals("I am markup", MarkupUtil.asPlainText(markups.get(0)).trim());
    Assert.assertEquals("Me too", MarkupUtil.asPlainText(markups.get(1)).trim());
  }

  @Test
  public void testCreateParagraphsWithDelimiter() {
    String openDiv = "<div xmlns:xlink='http://www.w3.org/1999/xlink' xmlns='http://www.coremedia.com/2003/richtext-1.0'>";
    Markup markup1 = MarkupFactory.fromString(openDiv + "<p>foo</p><p class=\"p--heading-3\">headline</p><p>bar</p></div>");
    List<Markup> markups = ParagraphHelper.createParagraphs(markup1, 5, PagingRuleType.DelimitingBlockCountRule);
    Assert.assertEquals(2, markups.size());
    Assert.assertEquals("foo", MarkupUtil.asPlainText(markups.get(0)).trim());
    Assert.assertEquals("headline\n\nbar", MarkupUtil.asPlainText(markups.get(1)).trim());
  }
}
