package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components.UserNotesContainer;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.tab.TabPanel;

public abstract class ExtensionTabPanel extends TabPanel {
  public static final String TAB_TITLE_NAME_ANNOTATION = "Annotation";
  public static final String TAB_TITLE_NAME_INFO = "Info";
  public static final String TAB_TITLE_NAME_CURATED_CONTENT = "Curated Content";

  public abstract ModerationBaseMetaDataTab getMetaDataTab();

  public enum ActivatableTab {
    META_DATA_PANEL,
    USER_NOTES_PANEL,
    CURATED_CONTENTS_PANEL
  }

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "userAnnotationContainer", global = false)
  private UserNotesContainer userNotesContainer;

  /**
   * Returns the UserNotesContainer and activates the tab if necessary.
   *
   * @return UserNotesContainer
   */
  public UserNotesContainer getUserNotesContainer() {
    activateTabIfNecessary(ActivatableTab.USER_NOTES_PANEL);
    return userNotesContainer;
  }

  /**
   * Activate the tab that needs to be in front (only if it's not already activated).
   *
   * @param tab ACTIVATABLE_TABS
   */
  public void activateTabIfNecessary(final ActivatableTab tab) {
    if (tab.equals(ActivatableTab.META_DATA_PANEL) && !getActiveTabName().equals(ActivatableTab.META_DATA_PANEL)) {
      this.setActiveTab(0);
    } else if (tab.equals(ActivatableTab.USER_NOTES_PANEL) && !getActiveTabName().equals(ActivatableTab.USER_NOTES_PANEL)) {
      this.setActiveTab(1);
    } else if (tab.equals(ActivatableTab.CURATED_CONTENTS_PANEL) && !getActiveTabName().equals(ActivatableTab.CURATED_CONTENTS_PANEL)) {
      this.setActiveTab(2);
    }
  }

  public ActivatableTab getActiveTabName() {
    final String title = this.activeTabTitle().await();
    assert title != null : "Active Tab Title must not be null.";
    if (title.equals(TAB_TITLE_NAME_INFO)) {
      return ActivatableTab.META_DATA_PANEL;
    } else if (title.equals(TAB_TITLE_NAME_ANNOTATION)) {
      return ActivatableTab.USER_NOTES_PANEL;
    } else if (title.equals(TAB_TITLE_NAME_CURATED_CONTENT)) {
      return ActivatableTab.CURATED_CONTENTS_PANEL;
    }
    return null;
  }
}
