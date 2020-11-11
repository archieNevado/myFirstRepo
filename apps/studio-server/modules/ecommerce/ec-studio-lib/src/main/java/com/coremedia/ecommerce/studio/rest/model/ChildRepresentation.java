package com.coremedia.ecommerce.studio.rest.model;

public class ChildRepresentation {
  private String displayName;
  private Object child;
  private boolean isVirtual = false;

  public ChildRepresentation(){
  }

  public ChildRepresentation(String displayName, Object child) {
    this.displayName = displayName;
    this.child = child;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Object getChild() {
    return child;
  }

  public boolean getIsVirtual() {
    return isVirtual;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setChild(Object child) {
    this.child = child;
  }

  public void setIsVirtual(boolean isVirtual) {
    this.isVirtual = isVirtual;
  }
}
