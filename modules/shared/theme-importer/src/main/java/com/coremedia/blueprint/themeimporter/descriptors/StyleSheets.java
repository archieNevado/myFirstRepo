package com.coremedia.blueprint.themeimporter.descriptors;


import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class StyleSheets {

  private List<Css> css;

  @XmlElement
  public List<Css> getCss() {
    return this.css;
  }

  public void setCss(List<Css> css) {
    this.css = css;
  }
}