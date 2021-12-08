package com.coremedia.catalog.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.rest.cap.validation.ContentTypeValidator;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.PropertyValidator;
import com.coremedia.rest.validation.Severity;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class CatalogProductValidator extends ContentTypeValidator {
  @VisibleForTesting static final String CODE_ISSUE_NOT_IN_CATALOG = "productIsNotLinkedInCatalog";
  @VisibleForTesting static final String CONTEXTS_PROPERTY_NAME = "contexts";

  public CatalogProductValidator(@NonNull ContentType type,
                                 boolean isValidatingSubtypes,
                                 @NonNull List<? extends PropertyValidator> validators) {
    super(type, isValidatingSubtypes, validators);
  }

  @Override
  public void validate(Content content, Issues issues) {
    super.validate(content, issues);
    List<Content> parentCategories = content.getLinks(CONTEXTS_PROPERTY_NAME);
    if(parentCategories.isEmpty()){
      issues.addIssue(getCategories(), Severity.ERROR, CONTEXTS_PROPERTY_NAME, CODE_ISSUE_NOT_IN_CATALOG);
    }
  }
}
