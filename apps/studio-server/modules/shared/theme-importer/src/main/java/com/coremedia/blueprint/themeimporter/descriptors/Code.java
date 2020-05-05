package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class Code extends Resource {
  private String ieExpression;
  private boolean disableCompress;
  private boolean notLinked;

  public String getIeExpression() {
    return ieExpression;
  }

  @XmlAttribute
  public void setIeExpression(String ieExpression) {
    this.ieExpression = ieExpression;
  }

  public boolean isNotLinked() {
    return notLinked;
  }

  @XmlAttribute
  public void setNotLinked(boolean notLinked) {
    this.notLinked = notLinked;
  }

  /**
   * @return if compression is disabled
   */
  public boolean isDisableCompress() {
    return disableCompress;
  }

  /**
   * @param disableCompress TRUE to disable compression, otherwise FALSE
   */
  @XmlAttribute
  public void setDisableCompress(boolean disableCompress) {
    this.disableCompress = disableCompress;
  }
}
