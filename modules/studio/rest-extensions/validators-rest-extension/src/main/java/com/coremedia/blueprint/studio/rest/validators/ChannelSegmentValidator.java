package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

/**
 * Validates if the navigation tree node's segment path is unique.
 */
public class ChannelSegmentValidator extends ContentTypeValidatorBase {
  private static final String PROPERTY_SEGMENT = "segment";
  private static final String PROPERTY_CHILDREN = "children";
  private static final String IS_IN_PRODUCTION = "isInProduction";

  private UrlPathFormattingHelper urlPathFormattingHelper;

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

  @Override
  public void validate(Content content, Issues issues) {
    String segment = urlPathFormattingHelper.getVanityName(content);
    if (null != segment) {
      Collection<Content> referrers = content.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
      for (Content ref : referrers) {
        List<Content> children = ref.getLinks(PROPERTY_CHILDREN);
        for (Content child : children) {
          if (!content.equals(child) && segment.equalsIgnoreCase(urlPathFormattingHelper.getVanityName(child))) {
            issues.addIssue(Severity.ERROR, PROPERTY_SEGMENT, "duplicate_segment", child);
            return;
          }
        }
      }
    }
  }
}
