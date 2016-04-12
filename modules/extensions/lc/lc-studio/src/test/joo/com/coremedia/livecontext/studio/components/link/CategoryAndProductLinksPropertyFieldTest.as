package com.coremedia.livecontext.studio.components.link {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkField;
import com.coremedia.ecommerce.studio.components.link.CategoryAndProductLinksPropertyField;
import com.coremedia.ecommerce.studio.config.catalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.categoryAndProductLinksPropertyField;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.livecontext.studio.AbstractCatalogStudioTest;
import com.coremedia.livecontext.studio.testhelper.config.categoryAndProductLinksPropertyFieldTestView;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.util.QtipUtil;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Ext;
import ext.Viewport;
import ext.menu.Item;

import js.HTMLElement;

public class CategoryAndProductLinksPropertyFieldTest extends AbstractCatalogStudioTest {
  private var picture:Content;
  private var link:CatalogLinkField;
  private var removeButton:Button;
  private var removeMenuItem:Item;
  private var viewPort:Viewport;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var createReadOnlyValueExpression:Function;


  override public function setUp():void {
    super.setUp();
    picture = beanFactory.getRemoteBean('content/200') as Content;
    //we need to mock the write access
    picture.getRepository().getAccessControl().mayWrite = function ():Boolean {
      return true;
    };
    var bindTo:ValueExpression = ValueExpressionFactory.createFromValue(picture);
    var conf:categoryAndProductLinksPropertyField = new categoryAndProductLinksPropertyField();
    conf.bindTo = bindTo;

    forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    conf.forceReadOnlyValueExpression = forceReadOnlyValueExpression;

    //Mock PropertyEditorUtil#createReadOnlyValueExpression
    createReadOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression;
    PropertyEditorUtil.createReadOnlyValueExpression = function (contentValueExpression:ValueExpression, forceReadOnlyValueExpression:ValueExpression = undefined):ValueExpression {
      return ValueExpressionFactory.createFromFunction(function ():Boolean {
        if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
          return true;
        }
        if (!contentValueExpression) {
          return false;
        }
        var mayWrite:* = true;
        return mayWrite === undefined ? undefined : !mayWrite;
      });

    };

    QtipUtil.registerQtipFormatter();
    createTestling(conf);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort.destroy();
    PropertyEditorUtil.createReadOnlyValueExpression = createReadOnlyValueExpression;
  }

  //noinspection JSUnusedGlobalSymbols
  public function testCatalogLink():void {
    chain(
      waitForPictureToBeLoaded(),
      checkProductLinkDisplaysValue(ORANGES_NAME, 0),
      checkSkuLinkDisplaysValue(ORANGES_SKU_NAME, 1),
      //still nothing selected
      checkRemoveButtonDisabled(),
      openContextMenu(), //this selects the link
      checkContextMenuOpened(),
      checkRemoveButtonEnabled(),
      checkRemoveContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link

      checkRemoveButtonDisabled(),
      checkRemoveContextMenuDisabled()
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

  private function checkProductLinkDisplaysValue(value:String, row:Number):Step {
    return new Step("check if product is linked and data is displayed",
      function ():Boolean {
        return ORANGES_EXTERNAL_ID === link.getView().getCell(row, 2)['textContent'] &&
          value === link.getView().getCell(row, 3)['textContent'];
      }
    );
  }

  private function checkSkuLinkDisplaysValue(value:String, row:Number):Step {
    return new Step("check if sku is linked and data is displayed",
      function ():Boolean {
        return ORANGES_SKU_EXTERNAL_ID === link.getView().getCell(row, 2)['textContent'] &&
          value === link.getView().getCell(row, 3)['textContent'];
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

  private function waitForPictureToBeLoaded():Step {
    return new Step("Wait for the picture to be loaded",
            function ():Boolean {
              return picture.isLoaded();
            }
    );
  }

  private function setForceReadOnly(value:Boolean):Step {
    return new Step("set forceReadOnlyValueExpression " + value,
            function ():Boolean {
              return true;
            },
            function ():void {
              forceReadOnlyValueExpression.setValue(value);
            }
    );
  }

  /**
   * private helper method to create the container for tests
   */
  private function createTestling(config:categoryAndProductLinksPropertyField):void {
    viewPort = new CategoryAndProductLinksPropertyFieldTestView(new categoryAndProductLinksPropertyFieldTestView(config));
    var testling:CategoryAndProductLinksPropertyField =
            viewPort.get(categoryAndProductLinksPropertyFieldTestView.TESTLING_ITEM_ID) as CategoryAndProductLinksPropertyField;
    link = testling.find('itemId', catalogLinkPropertyField.CATALOG_LINK_FIELD_ITEM_ID)[0] as CatalogLinkField;
    removeButton = link.getTopToolbar().find('itemId', eCommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID)[0];
  }

  private function findCatalogLinkContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
      return !component.ownerCt && !component.hidden && component.isXType(catalogLinkContextMenu.xtype);
    }) as CatalogLinkContextMenu;
    if (contextMenu) {
      removeMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }


}
}