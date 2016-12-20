package com.coremedia.livecontext.ibm.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.impl.ContentStructRemoteBeanImpl;
import com.coremedia.cap.content.impl.ContentTypeImpl;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkField;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkPropertyField;
import com.coremedia.ecommerce.studio.components.link.CategoryAndProductLinksPropertyField;
import com.coremedia.ecommerce.studio.model.CategoryImpl;
import com.coremedia.ecommerce.studio.model.MarketingImpl;
import com.coremedia.ecommerce.studio.model.MarketingSpotImpl;
import com.coremedia.ecommerce.studio.model.ProductImpl;
import com.coremedia.ecommerce.studio.model.ProductVariantImpl;
import com.coremedia.ecommerce.studio.model.StoreImpl;
import com.coremedia.livecontext.ibm.studio.library.EcommerceIbmCollectionViewActionsPlugin;
import com.coremedia.livecontext.ibm.studio.mgmtcenter.ManagementCenterUtil;
import com.coremedia.livecontext.studio.components.link.*;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.util.QtipUtil;
import com.coremedia.ui.util.TableUtil;

import ext.Component;
import ext.ComponentManager;
import ext.button.Button;
import ext.container.Viewport;
import ext.menu.Item;

import js.HTMLElement;

public class EcommerceIbmCategoryAndProductLinksPropertyFieldTest extends AbstractCatalogTest {
  private var picture:Content;
  private var link:CatalogLinkField;
  private var openButton:Button;
  private var openMenuItem:Item;
  private var viewPort:Viewport;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var createReadOnlyValueExpression:Function;


  override public function setUp():void {
    super.setUp();

    resetCatalogHelper();

    BeanFactoryImpl(beanFactory).registerRemoteBeanClasses(ContentTypeImpl, ContentImpl, ContentStructRemoteBeanImpl,
            StoreImpl, CategoryImpl, MarketingImpl, ProductImpl, MarketingSpotImpl, ProductVariantImpl);

    new EcommerceIbmCollectionViewActionsPlugin();
    picture = beanFactory.getRemoteBean('content/200') as Content;
    //we need to mock the write access
    picture.getRepository().getAccessControl().mayWrite = function ():Boolean {
      return true;
    };
    var bindTo:ValueExpression = ValueExpressionFactory.createFromValue(picture);
    var conf:CategoryAndProductLinksPropertyFieldTestView = CategoryAndProductLinksPropertyFieldTestView({});
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
      checkOpenButtonDisabled(),
      openContextMenu(), //this selects the link
      checkOpenButtonEnabled(),
      checkOpenContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link

      //valid selected link can be always opened
      checkOpenButtonEnabled(),
      checkOpenContextMenuEnabled()
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
            return (empty ? TableUtil.getMainBody(link) : TableUtil.getCell(link, 0, 1)).getXY();
          },
          preventDefault : function():void{
            //do nothing
          },
          getTarget: function():HTMLElement {
            return TableUtil.getCellAsDom(link, 0, 1);
          }
        };
        if (empty) {
          link.fireEvent("contextmenu", event);
        } else {
          link.fireEvent("rowcontextmenu", link, null, null, 0, event);
        }
      }
    );
  }

  private function checkOpenButtonDisabled():Step {
    return new Step("check open button disabled",
      function ():Boolean {
        return openButton.disabled;
      }
    );
  }

  private function checkOpenButtonEnabled():Step {
    return new Step("check open button enabled",
      function ():Boolean {
        return !ManagementCenterUtil.isSupportedBrowser() || !openButton.disabled;
      }
    );
  }

  private function checkOpenContextMenuEnabled():Step {
    return new Step("check open context menu enabled",
    function ():Boolean {
      return !ManagementCenterUtil.isSupportedBrowser() || !openMenuItem.disabled;
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
  private function createTestling(config:CategoryAndProductLinksPropertyFieldTestView):void {
    viewPort = new CategoryAndProductLinksPropertyFieldTestView(config);
    var testling:CategoryAndProductLinksPropertyField =
            viewPort.getComponent(CategoryAndProductLinksPropertyFieldTestView.TESTLING_ITEM_ID) as CategoryAndProductLinksPropertyField;
    link = testling.find('itemId', CatalogLinkPropertyField.CATALOG_LINK_FIELD_ITEM_ID)[0] as CatalogLinkField;
    openButton = Button(link.getTopToolbar().find('itemId', EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID)[0]);
  }

  private function findCatalogLinkContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentManager.getAll().filter(function (component:Component):Boolean {
      return !component.up() && !component.hidden && component.isXType(CatalogLinkContextMenu.xtype);
    })[0] as CatalogLinkContextMenu;
    if (contextMenu) {
      openMenuItem = contextMenu.getComponent(EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }


}
}