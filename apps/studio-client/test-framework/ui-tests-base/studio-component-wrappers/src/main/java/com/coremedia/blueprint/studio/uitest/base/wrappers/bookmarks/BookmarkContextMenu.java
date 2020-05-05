package com.coremedia.blueprint.studio.uitest.base.wrappers.bookmarks;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.menu.Item;
import com.coremedia.uitesting.ext3.wrappers.menu.Menu;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class BookmarkContextMenu extends Menu {

  public static final String RENAME_BOOKMARK_ITEM_ID = "renameBookmarkItemId";
  public static final String REMOVE_BOOKMARK_ITEM_ID = "removeBookmarkItemId";

  public static final String XTYPE = "com.coremedia.cms.editor.sdk.config.bookmarkContextMenu";

  @FindByExtJS(itemId = RENAME_BOOKMARK_ITEM_ID)
  private Item renameBookmarkItem;

  @FindByExtJS(itemId = REMOVE_BOOKMARK_ITEM_ID)
  private Item removeBookmarkItem;

  public Item getRenameBookmarkItem() {
    return renameBookmarkItem;
  }

  public Item getRemoveBookmarkItem() {
    return removeBookmarkItem;
  }
}
