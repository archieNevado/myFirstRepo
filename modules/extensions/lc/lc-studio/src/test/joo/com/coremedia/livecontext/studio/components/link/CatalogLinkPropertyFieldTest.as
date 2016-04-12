package com.coremedia.livecontext.studio.components.link {
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkField;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.catalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.testhelper.config.catalogLinkPropertyFieldTestView;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.util.QtipUtil;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Ext;
import ext.Viewport;
import ext.menu.Item;

import js.HTMLElement;

public class CatalogLinkPropertyFieldTest extends AbstractProductTeaserComponentsTest {
  private var link:CatalogLinkField;
  private var removeButton:Button;
  private var openInTabMenuItem:Item;
  private var removeMenuItem:Item;
  private var viewPort:Viewport;

  override public function setUp():void {
    super.setUp();
    QtipUtil.registerQtipFormatter();
    var conf:catalogLinkPropertyField = new catalogLinkPropertyField();
    conf.bindTo = getBindTo();
    conf.forceReadOnlyValueExpression = getForceReadOnlyValueExpression();

    createTestling(conf);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort.destroy();
  }

  //noinspection JSUnusedGlobalSymbols
  public function testCatalogLink():void {
    chain(
      waitForProductTeaserToBeLoaded(),
      checkProductLinkDisplaysValue(ORANGES_NAME),
      //still nothing selected
      checkRemoveButtonDisabled(),
      openContextMenu(), //this selects the link
      checkContextMenuOpened(),
      checkRemoveButtonEnabled(),
      checkRemoveContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link
      checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      checkRemoveContextMenuDisabled(),
      setLink(ORANGES_ID + '503'),
      checkCatalogLinkDisplaysErrorValue(ORANGES_EXTERNAL_ID + '503'),
      setLink(ORANGES_ID + '404'),
      checkCatalogLinkDisplaysErrorValue(ORANGES_EXTERNAL_ID + '404'),
      openContextMenu(), //this selects the link
      //still forceReadOnly = true
      checkRemoveButtonDisabled(),
      //invalid link --> cannot open
      checkRemoveContextMenuDisabled(),
      //invalid link --> cannot open
      setForceReadOnly(false),
      openContextMenu(), //this selects the link
      checkRemoveButtonEnabled(),
      //invalid link --> cannot open
      checkRemoveContextMenuEnabled(),
      //invalid link --> cannot open
      setLink(ORANGES_SKU_ID),
      checkSkuLinkDisplaysValue(ORANGES_SKU_NAME),
      openContextMenu(), //this selects the link
      checkContextMenuOpened(),
      checkRemoveButtonEnabled(),
      checkRemoveContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link
      checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      checkRemoveContextMenuDisabled(),
      setForceReadOnly(false),
      setLink(null),
      checkCatalogLinkIsEmpty(),
      checkRemoveButtonDisabled()
    );
  }

  private function openContextMenu():Step {
    return new Step("open Context Menu",
      function ():Boolean {
        return true;
      },
      function ():void {
        var empty:Boolean = link.getView().getRow(0) === undefined;
        var event:Object = {
          type: "contextmenu",

          getXY: function():Array {
            return empty ? link.getView().mainBody.getXY() : Ext.fly(link.getView().getCell(0, 1)).getXY();
          },
          preventDefault : function():void{
            //do nothing
          },
          getTarget: function():HTMLElement {
            return link.getView().getCell(0, 1);
          }
        };
        if (empty) {
          link.fireEvent("contextmenu", event);
        } else {
          link.fireEvent("rowcontextmenu", link, 0, event);
        }
      }
    );
  }

  private function checkProductLinkDisplaysValue(value:String):Step {
    return new Step("check if product is linked and data is displayed",
      function ():Boolean {
        return link.getStore().getCount() === 1 &&
          ORANGES_EXTERNAL_ID === link.getView().getCell(0, 2)['textContent'] &&
          value === link.getView().getCell(0, 3)['textContent'];
      }
    );
  }

  private function checkSkuLinkDisplaysValue(value:String):Step {
    return new Step("check if sku is linked and data is displayed",
      function ():Boolean {
        return link.getStore().getCount() === 1 &&
          ORANGES_SKU_EXTERNAL_ID === link.getView().getCell(0, 2)['textContent'] &&
          value === link.getView().getCell(0, 3)['textContent'];
      }
    );
  }

  private function checkCatalogLinkDisplaysErrorValue(value:String):Step {
    return new Step("check if broken product is linked and fallback data '" + value + "' is displayed",
      function ():Boolean {
        return link.getStore().getCount() === 1 &&
          value === link.getView().getCell(0, 2)['textContent'];
      }
    );
  }

  private function checkCatalogLinkIsEmpty():Step {
    return new Step("check if is catalog link is empty and set product link",
      function ():Boolean {
        return link && link.getStore() && link.getStore().getCount() === 0
      }
    );
  }

  private function checkRemoveButtonDisabled():Step {
    return new Step("check remove button disabled",
      function ():Boolean {
        return removeButton.disabled;
      }
    );
  }

  private function checkRemoveButtonEnabled():Step {
    return new Step("check remove button enabled",
      function ():Boolean {
        return !removeButton.disabled;
      }
    );
  }

  private function checkRemoveContextMenuDisabled():Step {
    return new Step("check remove context menu disabled",
      function ():Boolean {
        return removeMenuItem.disabled;
      }
    );
  }

  private function checkRemoveContextMenuEnabled():Step {
    return new Step("check remove context menu enabled",
      function ():Boolean {
        return !removeMenuItem.disabled;
      }
    );
  }

  private function checkContextMenuOpened():Step {
    return new Step("check context menu opened",
      function ():Boolean {
        return findCatalogLinkContextMenu();
      }
    );
  }

  /**
   * private helper method to create the container for tests
   */
  private function createTestling(config:catalogLinkPropertyField):void {
    viewPort = new CatalogLinkPropertyFieldTestView(new catalogLinkPropertyFieldTestView(config));
    var testling:CatalogLinkPropertyField =
            viewPort.get(catalogLinkPropertyFieldTestView.CATALOG_LINK_PROPERTY_FIELD_ITEM_ID) as CatalogLinkPropertyField;
    link = testling.find('itemId', catalogLinkPropertyField.CATALOG_LINK_FIELD_ITEM_ID)[0] as CatalogLinkField;

    var openInTabButton:Button = link.getTopToolbar().find('itemId', eCommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID)[0];
    //we cannot and don't want test the open in tab action as it needs the workarea.
    link.getTopToolbar().remove(openInTabButton);
    removeButton = link.getTopToolbar().find('itemId', eCommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID)[0];
  }

  private function findCatalogLinkContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
      return !component.ownerCt && !component.hidden && component.isXType(catalogLinkContextMenu.xtype);
    }) as CatalogLinkContextMenu;
    if (contextMenu) {
      openInTabMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID) as Item;
      //we cannot and don't want test the open in tab action as it needs the workarea.
      contextMenu.remove(openInTabMenuItem);
      removeMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }
}
}