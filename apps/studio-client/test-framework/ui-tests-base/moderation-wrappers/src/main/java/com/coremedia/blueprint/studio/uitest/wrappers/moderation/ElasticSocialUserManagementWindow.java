package com.coremedia.blueprint.studio.uitest.wrappers.moderation;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement.UserDetailsPanel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement.UserListContainer;
import com.coremedia.uitesting.cms.editor.components.desktop.FavoritesToolbar;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("SpringJavaAutowiringInspection")
@ExtJSObject(id = "cm-user-management-window")
@Singleton
public class ElasticSocialUserManagementWindow extends Window {

  @Inject
  private FavoritesToolbar favoritesToolbar;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "userList", global = false)
  private UserListContainer userListContainer;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-user-details-container", global = false)
  private UserDetailsPanel userDetailsPanel;


  public UserListContainer getUserListContainer() {
    return userListContainer;
  }

public UserDetailsPanel getUserDetailsPanel() {
  return userDetailsPanel;
  }

  private void openESUserManagementIfNecessary() {
    if (!exists().get() || visibleToUser().get()) {
      final Button button = favoritesToolbar.getExtensionsButton();
      button.clickAndSelectFromMenu(ExtJSBy.itemId("userListButton"));
      visible().waitUntilTrue();
    }
  }

  public void openUserManagementWindow() {
    openESUserManagementIfNecessary();
  }

  @Override
  public void close() {
    evalVoid("self.close()");
  }
}
