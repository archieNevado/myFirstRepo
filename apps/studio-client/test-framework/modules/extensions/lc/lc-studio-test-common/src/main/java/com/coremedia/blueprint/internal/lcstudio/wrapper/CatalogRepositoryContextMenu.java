package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.cms.editor.EditorDefault;
import com.coremedia.uitesting.cms.editor.components.collectionview.CollectionRepositoryContextMenu;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.menu.Item;
import org.springframework.context.annotation.Scope;

/**
 * @since 10/28/11
 */
@ExtJSObject
@EditorDefault
@Scope("prototype")
public class CatalogRepositoryContextMenu extends CollectionRepositoryContextMenu {
  public static final String XTYPE = "com.coremedia.ecommerce.studio.config.catalogRepositoryContextMenu";
  public static final String AUGMENT_CATEGORY_MENU_ITEM_ID = "augmentCategory";
  public static final String AUGMENT_PRODUCT_MENU_ITEM_ID = "augmentProduct";

  @FindByExtJS(itemId = AUGMENT_CATEGORY_MENU_ITEM_ID)
  private Item augmentCategoryMenuItem;

  @FindByExtJS(itemId = AUGMENT_PRODUCT_MENU_ITEM_ID)
  private Item augmentProductMenuItem;

  public Item getAugmentCategoryMenuItem() {
    return augmentCategoryMenuItem;
  }

  public Item getAugmentProductMenuItem() {
    return augmentProductMenuItem;
  }

  public void isAugmentedCategoryMenu() {
    getOpenMenuItem().visible().assertTrue();
    getOpenMenuItem().enabled().assertTrue();
    isAugmentedMenu();

    //augment menu item should be hidden as the category is augmented.
    getAugmentCategoryMenuItem().visible().assertFalse();
    getAugmentProductMenuItem().visible().assertFalse();
  }

  public void isAugmentedProductMenu() {
    isAugmentedMenu();

    //augment menu item should be hidden as the category is augmented.
    getAugmentProductMenuItem().visible().assertFalse();
    getAugmentProductMenuItem().visible().assertFalse();
  }

  private void isAugmentedMenu() {

    getOpenInTabMenuItem().visible().assertTrue();
    getOpenInTabMenuItem().enabled().assertTrue();

/*
    getOpenInWCSMenuItem().visible().assertTrue();
    getOpenInWCSMenuItem().enabled().assertTrue();
*/

    getBookmarkMenuItem().visible().assertTrue();
    getBookmarkMenuItem().enabled().assertTrue();

    getCopyMenuItem().visible().assertTrue();
    getCopyMenuItem().enabled().assertTrue();

    //explicitly removed
    getPasteMenuItem().visible().assertFalse();
    getCutMenuItem().visible().assertFalse();
    getRenameMenuItem().visible().assertFalse();

    //Withdraw menu item should be disabled (as the augmented content is created and not published)
    // and have modified label.
    Item withdrawMenuItem = getWithdrawMenuItem();
    withdrawMenuItem.visible().assertTrue();
    withdrawMenuItem.disabled().assertTrue();
    withdrawMenuItem.text().assertEquals("Withdraw Augmentation");

    //Delete menu item should be enabled and have modified label.
    Item deleteMenuItem = getDeleteMenuItem();
    deleteMenuItem.visible().assertTrue();
    deleteMenuItem.enabled().assertTrue();
    deleteMenuItem.text().assertEquals("Delete Augmentation");
  }
}
