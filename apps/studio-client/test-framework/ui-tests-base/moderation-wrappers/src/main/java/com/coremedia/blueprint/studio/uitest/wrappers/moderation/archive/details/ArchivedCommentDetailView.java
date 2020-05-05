package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ModerationDetailViewBase;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components.AttachmentPanel;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.comment.ModerationCommentExtensionTabPanel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ui.ckeditor.RichTextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ArchivedCommentDetailView extends ModerationDetailViewBase {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-archive-comment-richtext-area", global = false)
  private RichTextArea richTextArea;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-view-attachment-container", global = false)
  private AttachmentPanel attachmentContainer;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "extension-tab-panel-comment", global = false)
  private ModerationCommentExtensionTabPanel extensionTabPanel;

  public RichTextArea getCommentTextArea() {
    return richTextArea;
  }

  public AttachmentPanel getAttachmentContainer() {
    return attachmentContainer;
  }

  public ModerationCommentExtensionTabPanel getExtensionTabPanel() {
    return extensionTabPanel;
  }
}
