package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import edu.umd.cs.findbugs.annotations.NonNull;

public class ExternalChannelValidator extends CatalogLinkValidator {

  private static final String CODE_ISSUE_CATEGORY_EMPTY = "EmptyCategory";

  @SuppressWarnings("WeakerAccess")
  public ExternalChannelValidator(@NonNull ContentType type,
                                  boolean isValidatingSubtypes,
                                  CommerceConnectionSupplier commerceConnectionSupplier,
                                  SitesService sitesService,
                                  String propertyName) {
    super(type, isValidatingSubtypes, commerceConnectionSupplier, sitesService, propertyName);
  }

  @Override
  protected void emptyPropertyValue(@NonNull Content content, @NonNull Issues issues) {
    addIssue(issues, Severity.ERROR, CODE_ISSUE_CATEGORY_EMPTY);
  }
}
