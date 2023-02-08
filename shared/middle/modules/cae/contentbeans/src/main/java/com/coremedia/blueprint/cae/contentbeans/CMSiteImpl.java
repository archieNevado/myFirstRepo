package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.services.validation.ValidationService;

/**
 * Generated extension class for immutable beans of document type "CMSite".
 */
public class CMSiteImpl extends CMSiteBase {

  private ValidationService<CMLinkable> validationService;

  protected ValidationService<CMLinkable> getValidationService() {
    return validationService;
  }

  public void setValidationService(ValidationService<CMLinkable> validationService) {
    if(validationService == null) {
      throw new IllegalArgumentException("supplied 'validationService' must not be null");
    }
    this.validationService = validationService;
  }

  @Override
  protected void initialize() {
    super.initialize();
    if (validationService == null) {
      throw new IllegalStateException("Required property not set: validationService");
    }
  }

  @Override
  public CMNavigation getRoot() {
    return getValidationService().validate(super.getRoot()) ? super.getRoot() : null;
  }
}
