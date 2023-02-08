package com.coremedia.blueprint.cae.layout;

import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.common.layout.HasPageGrid;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.viewtype.ViewtypeService;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.PostConstruct;

public class PageGridServiceImpl implements PageGridService {

  private ContentBackedPageGridService contentBackedPageGridService;
  private ValidationService<Linkable> validationService;
  private ValidityPeriodValidator visibilityValidator;
  private ViewtypeService viewtypeService;

  public void setContentBackedPageGridService(ContentBackedPageGridService contentBackedPageGridService) {
    this.contentBackedPageGridService = contentBackedPageGridService;
  }

  public void setValidationService(ValidationService<Linkable> validationService) {
    this.validationService = validationService;
  }

  public void setViewtypeService(ViewtypeService viewtypeService) {
    this.viewtypeService = viewtypeService;
  }

  public void setVisibilityValidator(ValidityPeriodValidator visibilityValidator) {
    this.visibilityValidator = visibilityValidator;
  }

  @PostConstruct
  void initialize() {
    if (contentBackedPageGridService == null) {
      throw new IllegalStateException("Required property not set: contentBackedPageGridService");
    }
    if (validationService == null) {
      throw new IllegalStateException("Required property not set: validationService");
    }
    if (viewtypeService == null) {
      throw new IllegalStateException("Required property not set: viewtypeService");
    }
    if (visibilityValidator == null) {
      throw new IllegalStateException("Required property not set: visibilityValidator");
    }
  }

  @NonNull
  @Override
  public PageGrid getContentBackedPageGrid(HasPageGrid bean) {
    return new PageGridImpl(bean, contentBackedPageGridService, validationService, visibilityValidator, viewtypeService);
  }
}
