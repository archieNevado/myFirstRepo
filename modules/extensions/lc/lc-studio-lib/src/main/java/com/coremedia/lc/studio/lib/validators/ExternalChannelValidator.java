package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.util.CatalogRootHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

public class ExternalChannelValidator extends CatalogLinkValidator {
  private static final String CODE_ISSUE_CATEGORY_EMPTY = "EmptyCategory";

  @Override
  protected void emptyPropertyValue(Content content, Issues issues) {
    // the property externalId can be empty for the site root page and the catalog root
    if (!CatalogRootHelper.isCatalogRoot(content)) {
      addIssue(issues, Severity.ERROR, CODE_ISSUE_CATEGORY_EMPTY);
    }
  }

}
