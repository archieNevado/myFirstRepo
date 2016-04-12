package com.coremedia.livecontext.studio.components.link {
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkField;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.catalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.livecontext.studio.AbstractEcommerceIbmProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.testhelper.config.catalogLinkPropertyFieldTestView;
import com.coremedia.livecontext.studio.config.ecommerceIbmStudioPlugin;
import com.coremedia.livecontext.studio.mgmtcenter.ManagementCenterUtil;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.util.QtipUtil;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Ext;
import ext.Viewport;
import ext.menu.Item;

import js.HTMLElement;

public class EcommerceIbmCatalogLinkPropertyFieldTest extends AbstractEcommerceIbmProductTeaserComponentsTest {
  private var link:CatalogLinkField;
  private var openWcsButton:Button;
  private var openInTabMenuItem:Item;
  private var openWcsMenuItem:Item;
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
      //still nothing selected
      checkOpenWcsButtonDisabled(),
      openContextMenu(), //this selects the link
      checkOpenWcsButtonEnabled(),
      checkOpenWcsContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link
      //valid selected link can be always opened
      checkOpenWcsButtonEnabled(),
      checkOpenWcsContextMenuEnabled(),
      setLink(ORANGES_ID + '503'),
      setLink(ORANGES_ID + '404'),
      openContextMenu(), //this selects the link
      //invalid link --> cannot open
      checkOpenWcsButtonDisabled(),
      //invalid link --> cannot open
      checkOpenWcsContextMenuDisabled(),
      setForceReadOnly(false),
      openContextMenu(), //this selects the link
      //invalid link --> cannot open
      checkOpenWcsButtonDisabled(),
      //invalid link --> cannot open
      checkOpenWcsContextMenuDisabled(),
      setLink(ORANGES_SKU_ID),
      openContextMenu(), //this selects the link
      checkOpenWcsButtonEnabled(),
      checkOpenWcsContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link
      //valid selected link can be always opened
      checkOpenWcsButtonEnabled(),
      checkOpenWcsContextMenuEnabled(),
      setForceReadOnly(false),
      setLink(null),
      checkOpenWcsButtonDisabled()
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


  private function checkOpenWcsButtonDisabled():Step {
    return new Step("check open Wcs button disabled",
      function ():Boolean {
        return openWcsButton.disabled;
      }
    );
  }

  private function checkOpenWcsButtonEnabled():Step {
    return new Step("check open Wcs button enabled",
      function ():Boolean {
        return !ManagementCenterUtil.isSupportedBrowser() || !openWcsButton.disabled;
      }
    );
  }

  private function checkOpenWcsContextMenuDisabled():Step {
    return new Step("check open Wcs context menu disabled",
      function ():Boolean {
        return !ManagementCenterUtil.isSupportedBrowser() || openWcsMenuItem.disabled;
      }
    );
  }

  private function checkOpenWcsContextMenuEnabled():Step {
    return new Step("check open Wcs context menu enabled",
    function ():Boolean {
      return !ManagementCenterUtil.isSupportedBrowser() || !openWcsMenuItem.disabled;
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
    openWcsButton = link.getTopToolbar().find('itemId', ecommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID)[0];
  }

  private function findCatalogLinkContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
      return !component.ownerCt && !component.hidden && component.isXType(catalogLinkContextMenu.xtype);
    }) as CatalogLinkContextMenu;
    if (contextMenu) {
      openInTabMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID) as Item;
      //we cannot and don't want test the open in tab action as it needs the workarea.
      contextMenu.remove(openInTabMenuItem);
      openWcsMenuItem = contextMenu.getComponent(ecommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }
}
}