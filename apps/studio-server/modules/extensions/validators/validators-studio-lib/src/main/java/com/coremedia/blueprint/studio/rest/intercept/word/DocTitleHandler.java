package com.coremedia.blueprint.studio.rest.intercept.word;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;


/**
 * ContentHandlerDecorator for extracting the content title property from a paragraph.
 */
public class DocTitleHandler extends ContentHandlerDecorator {

  public static final String CLASS_ATTRIBUTE = "class";

  private boolean titleMode = false;
  private String title = null;
  private final String defaultTitle;
  private final List<String> titleTags = new ArrayList<>();

  public DocTitleHandler(String defaultTitle) {
    this.defaultTitle = defaultTitle;

    titleTags.add("p-titel");
    titleTags.add("p-title");
    titleTags.add("p-header");
    titleTags.add("h1-titel");
    titleTags.add("h1-title");
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) {
    String lastStyle = (atts.getValue(CLASS_ATTRIBUTE) != null) ? "-" + atts.getValue(CLASS_ATTRIBUTE) : "";
    String tagAndStyle = (localName + lastStyle).toLowerCase();

    if (titleTags.contains(tagAndStyle)) {
      titleMode = true;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    String s = new String(ch);

    if (StringUtils.isEmpty(title) && titleMode && !StringUtils.isEmpty(s)) {
      title = s;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) {
    this.titleMode = false;
  }


  @Override
  public void startDocument() {
  }


  @Override
  public void endDocument() {
  }

  public String getTitle() {
    if (StringUtils.isEmpty(title)) {
      return defaultTitle;
    }
    return title;
  }
}
