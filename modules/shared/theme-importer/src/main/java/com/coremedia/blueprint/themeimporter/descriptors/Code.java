package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class Code extends Resource {
  private String ieExpression;
  private boolean disableCompress;

  public String getIeExpression() {
    return ieExpression;
  }

  @XmlAttribute
  public void setIeExpression(String ieExpression) {
    this.ieExpression = ieExpression;
  }

  public boolean isDisableCompress() {
    return disableCompress;
  }

  @XmlAttribute
  public void setDisableCompress(boolean disableCompress) {
    this.disableCompress = disableCompress;
  }
}
