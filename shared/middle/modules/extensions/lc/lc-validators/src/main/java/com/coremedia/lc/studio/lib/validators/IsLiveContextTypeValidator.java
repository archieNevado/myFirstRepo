package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.validation.AbstractContentTypeValidator;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Make sure the content type can only be used in livecontext sites
 */
public class IsLiveContextTypeValidator extends AbstractContentTypeValidator {

  private static final String CODE_ISSUE_DOC_TYPE_NOT_SUPPORTED = "DocTypeNotSupported";

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final SitesService sitesService;

  public IsLiveContextTypeValidator(@NonNull ContentType type,
                                    boolean isValidatingSubtypes,
                                    CommerceConnectionSupplier commerceConnectionSupplier,
                                    SitesService sitesService) {
    super(type, isValidatingSubtypes);
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.sitesService = sitesService;
  }

  @Override
  public void validate(Content content, Issues issues) {
    if (content == null || !content.isInProduction()) {
      return;
    }

    //check if the content belongs to a livecontext site
    boolean isLiveContextConnectionPresent = sitesService.getContentSiteAspect(content).findSite()
            .flatMap(commerceConnectionSupplier::findConnection)
            .filter(c -> !"coremedia".equals(c.getVendorName()))
            .isPresent();
    if (!isLiveContextConnectionPresent) {
      issues.addIssue(getCategories(), Severity.ERROR, null, getContentType() + "_" + CODE_ISSUE_DOC_TYPE_NOT_SUPPORTED, getContentType());
    }
  }
}
