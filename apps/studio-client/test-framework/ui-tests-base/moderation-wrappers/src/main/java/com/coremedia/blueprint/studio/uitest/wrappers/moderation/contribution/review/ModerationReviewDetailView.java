package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.review;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.comment.ModerationCommentDetailView;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.NumberField;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationReviewDetailView extends ModerationCommentDetailView {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "titleFieldItemId", global = false)
  private TextField titleField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "ratingFieldItemId", global = false)
  private NumberField ratingField;

  public TextField getTitleField() {
    return titleField;
  }

  public NumberField getRatingField() {
    return ratingField;
  }
}
