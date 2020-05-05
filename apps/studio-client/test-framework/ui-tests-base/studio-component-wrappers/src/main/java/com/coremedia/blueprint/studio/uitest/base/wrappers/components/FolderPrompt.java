package com.coremedia.blueprint.studio.uitest.base.wrappers.components;

import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;

import javax.inject.Singleton;

/**
 * Wrapper for Create-Folder-Prompt which pops up for example when creating content and
 * entering a folder which does not exist yet. The prompt will query if to create the
 * missing folders.
 *
 * @since 2013-08-08
 */
@ExtJSObject(id = "createFolderPrompt")
@Singleton
public class FolderPrompt extends Window {
  @FindByExtJS(itemId = "okButton")
  private Button okButton;
  @FindByExtJS(itemId = "cancelButton")
  private Button cancelButton;

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getOkButton() {
    return okButton;
  }
}
