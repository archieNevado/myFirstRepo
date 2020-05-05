package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.comment;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ModerationDetailViewBase;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.dialog.AttachmentDetailWindow;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components.AttachmentPanel;
import com.coremedia.uitesting.ext3.wrappers.Toolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ui.ckeditor.RichTextArea;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class ModerationCommentDetailView extends ModerationDetailViewBase {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-moderation-comment-richtext-area", global = false)
  private RichTextArea richTextArea;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-comment-detail-toolbar", global = false)
  private Toolbar toolbar;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-view-attachment-container", global = false)
  private AttachmentPanel attachmentContainer;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "extension-tab-panel-comment", global = false)
  private ModerationCommentExtensionTabPanel extensionTabPanel;


  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-target-button", global = false)
  private Button targetButton;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private AttachmentDetailWindow attachmentDetailWindow;

  public RichTextArea getCommentTextArea() {
    return richTextArea;
  }

  public Condition<Boolean> targetContentLinkVisible() {
    return targetButton.visible();
  }

  public Condition<String> targetLinkText() {
    return targetButton.text();
  }

  public Toolbar getRichTextAreaToolBar() {
    return toolbar;
  }

  public AttachmentPanel getAttachmentContainer() {
    return attachmentContainer;
  }

  public AttachmentDetailWindow getAttachmentDetailWindow() {
    return attachmentDetailWindow;
  }

  public ModerationCommentExtensionTabPanel getExtensionTabPanel() {
    return extensionTabPanel;
  }
}
