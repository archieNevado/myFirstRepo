package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class Code extends Resource {
  private String ieExpression;
  private boolean notLinked;

  /**
   * @deprecated since 2110.1, Old Internet Explorer (IE) is not supported anymore.
   */
  @Deprecated(since = "2110.1")
  public String getIeExpression() {
    return ieExpression;
  }

  /**
   * @deprecated since 2110.1, Old Internet Explorer (IE) is not supported anymore.
   */
  @Deprecated(since = "2110.1")
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
}
