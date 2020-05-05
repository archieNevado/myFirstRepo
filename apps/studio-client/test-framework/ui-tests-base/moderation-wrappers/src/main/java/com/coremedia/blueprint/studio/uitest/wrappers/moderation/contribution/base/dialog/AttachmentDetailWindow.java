package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.dialog;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components.AttachmentPanel;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;

import javax.inject.Singleton;

@ExtJSObject(id = "attachmentDetailWindow")
@Singleton
public class AttachmentDetailWindow extends Window {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "fileNameLabel", global = false)
  private StringDisplayField fileNameLabel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "prevNavButton", global = false)
  private Button previousNavButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "nextNavButton", global = false)
  private Button nextNavButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "attachmentWindowSlider", global = false)
  private AttachmentPanel attachmentContainer;

  public StringDisplayField getFileNameLabel() {
    return fileNameLabel;
  }

  public Button getPreviousNavButton() {
    return previousNavButton;
  }

  public Button getNextNavButton() {
    return nextNavButton;
  }

  public AttachmentPanel getAttachmentContainer() {
    return attachmentContainer;
  }
}
