package com.coremedia.blueprint.themeimporter.descriptors;


import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class JavaScripts {
  @XmlElement
  private List<JavaScript> javaScript;

  public List<JavaScript> getJavaScripts() {
    return this.javaScript;
  }


  public void setJavaScript(List<JavaScript> javaScript) {
    this.javaScript = javaScript;
  }
}