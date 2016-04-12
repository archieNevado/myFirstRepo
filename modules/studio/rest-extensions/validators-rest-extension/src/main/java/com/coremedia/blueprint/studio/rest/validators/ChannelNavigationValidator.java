package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Validates if the navigation tree node is not in a cycle.
 */
public class ChannelNavigationValidator extends ContentTypeValidatorBase {
  private static final String PROPERTY_CHILDREN = "children";
  private static final String IS_IN_PRODUCTION = "isInProduction";
  private String channelLoopCode = "channel_loop";

  @Override
  public void validate(Content content, Issues issues) {
    Collection<Content> parents = parentsOf(content);
    searchDuplicates(content, new ArrayList<Content>(), parents, issues);
  }

  public void setChannelLoopCode(String channelLoopCode) {
    this.channelLoopCode = channelLoopCode;
  }

  private static Collection<Content> parentsOf(Content content) {
    return content.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
  }

  private void searchDuplicates(Content content, List<Content> visited, Collection<Content> parents, Issues issues) {
    if (parents.contains(content)) {
      issues.addIssue(Severity.ERROR, null, channelLoopCode);
    } else {
      for (Content parent : parents) {
        if (!visited.contains(parent)) {
          visited.add(parent);
          Collection<Content> parentParents = parentsOf(parent);
          searchDuplicates(content, visited, parentParents, issues);
        }
      }
    }
  }
}
