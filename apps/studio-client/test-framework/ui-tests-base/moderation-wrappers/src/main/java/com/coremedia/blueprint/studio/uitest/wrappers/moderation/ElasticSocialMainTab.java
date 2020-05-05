package com.coremedia.blueprint.studio.uitest.wrappers.moderation;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.ArchiveTabPanel;
import com.coremedia.uitesting.cms.editor.components.desktop.FavoritesToolbar;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.tab.TabPanel;
import net.joala.condition.BooleanCondition;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("SpringJavaAutowiringInspection")
@ExtJSObject(id = "cm-elastic-social-main-tab")
@Singleton
public class ElasticSocialMainTab extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  public static final String XTYPE = "com.coremedia.elastic.social.studio.moderation.ElasticSocialMainTab";

  @Inject
  private FavoritesToolbar favoritesToolbar;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "cm-elastic-social-moderation-tab-panel", global = false)
  private ModerationTabPanel moderationTabPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "cm-elastic-social-archive-tab-panel", global = false)
  private ArchiveTabPanel archiveTabPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "tab-panel", global = false)
  private TabPanel tabPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "elastic-tab-toolbar", global = false)
  private ElasticSocialMainToolbar elasticSocialMainToolbar;

  @SuppressWarnings("UnusedDeclaration")
  @FindByExtJS(itemId = "createArticleBtn", global = false)
  private Button createArticleBtn;


  private void openESMainTabIfNecessary() {
    BooleanCondition exists = exists();
    BooleanCondition visibleToUser = visibleToUser();
    if (exists == null || !exists.get() || (visibleToUser != null && visibleToUser.get())) {
      clickModerationTab();
    }
  }

  public void clickModerationTab() {
    Button button = favoritesToolbar.getExtensionsButton();
    button.visible().waitUntilTrue();
    button.clickAndSelectFromMenu(ExtJSBy.itemId("moderationButton"));
    visible().waitUntilTrue();
  }

  public void openModerationTab() {
    openESMainTabIfNecessary();
    final Boolean visibleState = moderationTabPanel.visible().await();
    assert visibleState != null : "Visible state of Moderation Tab Panel must not be null.";
    if (!visibleState) {
      tabPanel.setActiveTab("cm-elastic-social-moderation-tab-panel");
      moderationTabPanel.visible().waitUntilTrue();
    }
  }

  public void openArchiveTab() {
    openESMainTabIfNecessary();
    final Boolean visibleState = archiveTabPanel.visible().await();
    assert visibleState != null : "Visible state of Archive Tab Panel must not be null.";
    if (!visibleState) {
      tabPanel.setActiveTab("cm-elastic-social-archive-tab-panel");
      archiveTabPanel.visible().waitUntilTrue();
    }
  }

  public ModerationTabPanel getModerationTabPanel() {
    return moderationTabPanel;
  }

  public ArchiveTabPanel getArchiveTabPanel() {
    return archiveTabPanel;
  }

  public ElasticSocialMainToolbar getToolbar() {
    return elasticSocialMainToolbar;
  }

  public Button getCreateArticleBtn() {
    return createArticleBtn;
  }


}
