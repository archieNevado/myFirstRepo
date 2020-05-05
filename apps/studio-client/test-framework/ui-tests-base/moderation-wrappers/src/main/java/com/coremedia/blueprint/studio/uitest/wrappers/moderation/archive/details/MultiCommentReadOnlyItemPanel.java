package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ui.ckeditor.RichTextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class MultiCommentReadOnlyItemPanel extends Panel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "richtext", global = false)
  private RichTextArea richTextArea;

  public RichTextArea getRichTextArea() {
    return richTextArea;
  }

}
