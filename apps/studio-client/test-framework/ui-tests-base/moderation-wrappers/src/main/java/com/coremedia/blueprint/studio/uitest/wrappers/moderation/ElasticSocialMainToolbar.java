package com.coremedia.blueprint.studio.uitest.wrappers.moderation;

import com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent.NewContentDialog;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;

import javax.inject.Singleton;

@ExtJSObject
@Singleton
public class ElasticSocialMainToolbar extends Container {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  public static final String XTYPE = "com.coremedia.elastic.social.studio.moderation.moderation.ModerationPanelToolbar";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createArticleBtn", global = false)
  private Button createArticleFromComments;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createPictureBtn", global = false)
  private Button createImageFromComments;



  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(xtype = NewContentDialog.XTYPE, global = true)
  private NewContentDialog newContentDialog;

  public Button getCreateArticleFromCommentsButton() {
    return createArticleFromComments;
  }

  public Button getCreateImageFromCommentsButton() {
    return createImageFromComments;
  }

  public NewContentDialog getNewContentDialog() {
    return newContentDialog;
  }

}
