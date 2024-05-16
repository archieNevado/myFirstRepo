package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.viewtype.ViewtypeService;

/**
 * Generated extension class for immutable beans of document type "CMViewtype".
 */
public class CMViewtypeImpl extends CMViewtypeBase {
  private ViewtypeService viewtypeService;

  public void setViewtypeService(ViewtypeService viewtypeService) {
    this.viewtypeService = viewtypeService;
  }

  @Override
  protected void initialize() {
    super.initialize();
    if (viewtypeService == null) {
      throw new IllegalStateException("Required property not set: viewtypeService");
    }
  }

  @Override
  public String getLayout() {
    return viewtypeService.getLayout(getContent());
  }
}
