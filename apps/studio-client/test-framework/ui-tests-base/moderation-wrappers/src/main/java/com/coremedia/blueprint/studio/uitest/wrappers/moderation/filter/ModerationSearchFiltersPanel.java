package com.coremedia.blueprint.studio.uitest.wrappers.moderation.filter;

import com.coremedia.uitesting.cms.editor.components.collectionview.search.SearchFiltersPanel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationSearchFiltersPanel extends SearchFiltersPanel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(xtype = ContributionSearchFilterPanel.XTYPE, global = false)
  private ContributionSearchFilterPanel contributionSearchFilterPanel;

  public ContributionSearchFilterPanel getContributionSearchFilterPanel() {
    return contributionSearchFilterPanel;
  }
}
