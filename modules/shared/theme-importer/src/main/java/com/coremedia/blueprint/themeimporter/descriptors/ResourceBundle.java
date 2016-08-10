package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlAttribute;

public class ResourceBundle extends Resource {
  private boolean linkIntoTheme = true;
  private String master;

  public boolean isLinkIntoTheme() {
    return linkIntoTheme;
  }

  @XmlAttribute
  public void setLinkIntoTheme(boolean linkIntoTheme) {
    this.linkIntoTheme = linkIntoTheme;
  }

  public String getMaster() {
    return master;
  }

  @XmlAttribute
  public void setMaster(String master) {
    this.master = master;
  }
}
