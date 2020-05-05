package com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.context.annotation.Scope;

/**
 * <p>
 * Wrapper for the New-Content Menu Button available in the favorites toolbar.
 * </p>
 * @since 6/8/12
 */
@ExtJSObject
@Scope("prototype")
public class NewContentMenuButton extends Button {
  public static final String XTYPE = "com.coremedia.cms.editor.sdk.config.newContentMenuButton";

  /**
   * Retrieve a wrapper for the given menu-item.
   * @param item menu item identifier
   * @return menu item
   */
  public NewContentMenuButtonItem getMenuItem(final MenuItem item) {
    return getMenu().getComponent(item.getItemId(), NewContentMenuButtonItem.class);
  }

  /**
   * Returns all available menu items.
   * @return menu items
   */
  public NewContentMenuButtonItem[] getMenuItems() {
    final MenuItem[] menuItems = MenuItem.values();
    final NewContentMenuButtonItem[] result = new NewContentMenuButtonItem[menuItems.length];
    for (int i = 0; i < menuItems.length; i++) {
      final MenuItem menuItem = menuItems[i];
      result[i] = getMenuItem(menuItem);
    }
    return result;
  }

  public enum MenuItem {
    CM_ARTICLE("CMArticle"),
    CM_TEASER("CMCollection"),
    CM_PICTURE("CMPicture"),
    // ---- Separator
    CREATE_FROM_TEMPLATE("createFromTemplate");

    private final String itemId;

    MenuItem(final String itemId) {
      this.itemId = itemId;
    }

    public String getItemId() {
      return itemId;
    }

    public String getBoundDocumentType() {
      return itemId;
    }
  }
}
