package com.coremedia.blueprint.studio.uitest.base.wrappers.bookmarks;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class BookmarkButton extends Button {
  public static final String XTYPE = "com.coremedia.cms.editor.sdk.config.bookmarkFavouritesToolbarButton";

  @FindByExtJS(xtype = BookmarkContextMenu.XTYPE, global = true)
  private BookmarkContextMenu bookmarkContextMenu;

  public BookmarkContextMenu getBookmarkContextMenu() {
    return bookmarkContextMenu;
  }
}
