package com.coremedia.blueprint.studio.uitest.base.wrappers.desktop;

import com.coremedia.blueprint.studio.uitest.base.wrappers.bookmarks.BookmarkButton;
import com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent.NewContentMenuButton;
import com.coremedia.uitesting.cms.editor.components.desktop.FavoritesToolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;

import javax.inject.Singleton;

/**
 * @since 6/8/12
 */
@ExtJSObject(id="favorites-toolbar")
@Singleton
public class BlueprintFavoritesToolbar extends FavoritesToolbar {
  @FindByExtJS(xtype = NewContentMenuButton.XTYPE, global = false)
  private NewContentMenuButton newContentMenuButton;

  @FindByExtJS(xtype = BookmarkButton.XTYPE, global = false)
  private BookmarkButton bookmarkButton;

  public NewContentMenuButton getNewContentMenuButton() {
    return newContentMenuButton;
  }

  public BookmarkButton getBookmarkButton() {
    return bookmarkButton;
  }
}
