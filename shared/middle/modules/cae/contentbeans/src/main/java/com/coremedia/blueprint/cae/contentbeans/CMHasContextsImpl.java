package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.services.context.CurrentContextService;

/**
 * Generated extension class for immutable beans of document type "CMHasContexts".
 */
public class CMHasContextsImpl extends CMHasContextsBase {

  private CurrentContextService currentContextService;

  public void setCurrentContextService(CurrentContextService currentContextService) {
    this.currentContextService = currentContextService;
  }

  // This should be protected, since it is not meant to be a feature of
  // a contentbean, but only for internal usage in subclasses.
  // public only for compatibility reasons.
  public CurrentContextService getCurrentContextService() {
    return currentContextService;
  }

  @Override
  protected void initialize() {
    super.initialize();
    if (currentContextService == null) {
      throw new IllegalStateException("Required property not set: currentContextService");
    }
  }
}
