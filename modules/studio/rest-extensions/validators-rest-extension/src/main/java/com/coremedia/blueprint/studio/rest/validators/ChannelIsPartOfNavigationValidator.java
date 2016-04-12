package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.Collection;

/**
 * Validates if link list properties of the given content do reference themselves.
 */
public class ChannelIsPartOfNavigationValidator extends ContentTypeValidatorBase {

  private static final String IS_IN_PRODUCTION = "isInProduction";

  @Override
  public void validate(Content content, Issues issues) {
    if (isNotInNavigation(content)) {
      issues.addIssue(Severity.ERROR, null, "not_in_navigation");
    }
  }

  protected boolean isNotInNavigation(Content content) {
    // check if content is explicitly linked by some parent channel
    Collection<Content> referrers = content.getReferrersWithDescriptorFulfilling("CMChannel", "children", IS_IN_PRODUCTION);
    boolean isNotInNavigation = referrers.isEmpty();
    if (isNotInNavigation) {
      // check if it is the root navigation
      referrers = content.getReferrersWithDescriptorFulfilling("CMSite", "root", IS_IN_PRODUCTION);
      isNotInNavigation = referrers.isEmpty();
    }
    return isNotInNavigation;
  }
}
