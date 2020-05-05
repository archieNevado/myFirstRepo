package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.profile;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ExtensionTabPanel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationProfileExtensionTabPanel extends ExtensionTabPanel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-view-meta-data", global = false)
  private ModerationProfileMetaDataTab metaDataTab;

  @Override
  public ModerationProfileMetaDataTab getMetaDataTab() {
    activateTabIfNecessary(ActivatableTab.META_DATA_PANEL);
    return metaDataTab;
  }
}
