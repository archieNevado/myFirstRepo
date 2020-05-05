package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.comment;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ExtensionTabPanel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationCommentExtensionTabPanel extends ExtensionTabPanel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-view-meta-data", global = false)
  private ModerationCommentMetaDataTab metaDataTab;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-view-curated-content", global = false)
  private CuratedContentTab curatedContentTab;

  @Override
  public ModerationCommentMetaDataTab getMetaDataTab() {
    activateTabIfNecessary(ActivatableTab.META_DATA_PANEL);
    return metaDataTab;
  }

  public CuratedContentTab getCuratedContentTab() {
    activateTabIfNecessary(ActivatableTab.CURATED_CONTENTS_PANEL);
    return curatedContentTab;
  }
}
