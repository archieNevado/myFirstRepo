package com.coremedia.livecontext.studio.action {
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessorFactory;
import com.coremedia.cap.common.session;
import com.coremedia.cms.editor.sdk.MessageServiceImpl;
import com.coremedia.cms.editor.sdk.components.breadcrumb.Breadcrumb;
import com.coremedia.cms.editor.sdk.config.breadcrumbElement;
import com.coremedia.cms.editor.sdk.config.innerPreviewPanel;
import com.coremedia.cms.editor.sdk.config.previewPanel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.PreviewFocusForwarder;
import com.coremedia.cms.editor.sdk.preview.InnerPreviewPanel;
import com.coremedia.cms.editor.sdk.preview.InnerPreviewPanelBase;
import com.coremedia.cms.editor.sdk.preview.PreviewContextMenu;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.livecontext.studio.AbstractCatalogStudioTest;
import com.coremedia.livecontext.studio.CMExternalPageExtension;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.livecontext.studio.config.openCreateExternalPageDialogActionTestView;
import com.coremedia.livecontext.studio.config.openCreateExternalPageMenuItem;
import com.coremedia.livecontext.studio.config.shopPageShowInLibraryMenuItem;
import com.coremedia.livecontext.studio.pbe.DisableStoreNodePlugin;
import com.coremedia.livecontext.studio.pbe.StoreNodeRenderer;
import com.coremedia.ui.PluginRulesMgr;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.MockAjax;
import com.coremedia.ui.data.test.Step;

import ext.Component;
import ext.ComponentMgr;
import ext.Container;
import ext.Ext;
import ext.QuickTips;
import ext.Viewport;
import ext.menu.Item;
import ext.util.MixedCollection;

import flexunit.framework.TestSuite;

public class OpenCreateExternalPageDialogActionTest extends AbstractCatalogStudioTest {

  private var _viewPort:Viewport;
  private var _previewContextMenu:PreviewContextMenu;
  private var _previewPanel:PreviewPanel;
  private var _breadCrump:Breadcrumb;
  private var _processingData:ProcessingData;

  override public function setUp():void {
    super.setUp();
    MessageServiceImpl.initMessageService();
    session.getConnection().getContentRepository()['getPreviewControllerUriPattern'] = function ():String {
      // no preview uri pattern
      return "";
    };

    editorContext['getPreferences'] = function ():Bean {
      return beanFactory.createLocalBean();
    };
    editorContext['getMetadataNodeRendererRegistry']().register(new StoreNodeRenderer());
    editorContext.getWorkArea().getActiveTab = function ():* {
      return null;
    };

    PluginRulesMgr.registerForXtype(breadcrumbElement.xtype, new DisableStoreNodePlugin());
    CMExternalPageExtension.register(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE);

    QuickTips.init(true);

    ProcessorFactory.process = function(data:ProcessingData):void {
      trace("[INFO] processing data received: ", data.toJson());
      _processingData = data;
    };
    _processingData = null;

    MockAjax.addMockCalls([
      {
        "request": {
          "uri": "livecontext/urlService", "method": "POST",
          "contentType": "application/json",
          "body": {
            "shopUrl": "https://shop-preview-helios.toko-test-03.coremedia.vm/webapp/wcs/preview/servlet/en/auroraesite/corporate-info",
            "siteId": "HeliosSiteId"
          }
        },
        "response": {
          "body": {
            "bean": {
              "$Ref": "content/500"
            }
          }
        }
      },
      {
        "request": {
          "uri": "livecontext/urlService", "method": "POST",
          "contentType": "application/json",
          "body": {
            "shopUrl": "https://shop-preview-helios.toko-test-03.coremedia.vm/webapp/wcs/preview/servlet/en/auroraesite/not_augmented",
            "siteId": "HeliosSiteId"
          }
        },
        "response": {
          "body": {
            "bean": null
          }
        }
      },
      {
        "request": {
          "uri": "livecontext/urlService", "method": "POST",
          "contentType": "application/json",
          "body": {
            "shopUrl": "https://shop-preview-helios.toko-test-03.coremedia.vm/webapp/wcs/preview/servlet/AdvancedSearchDisplay?catalogId=10152&langId=-1&storeId=10851",
            "siteId": "HeliosSiteId"
          }
        },
        "response": {
          "body": {
            "bean": null
          }
        }
      },
      {
        "request": {"uri": "content/38", "method": "GET"},
        "response": {
          "body": {
            "name": "CSS",
            "id": "coremedia:///cap/content/38",
            "type": {
              "$Ref": "content/type/Content_"
            }
          }
        }
      }, {
        "request": {"uri": "content/5784", "method": "GET"},
        "response": {
          "body": {
            "name": "5784",
            "id": "coremedia:///cap/content/5784",
            "type": {
              "$Ref": "content/type/CMArticle"
            }
          }
        }
      }, {
        "request": {"uri": "content/5786", "method": "GET"},
        "response": {
          "body": {
            "name": "5786",
            "id": "coremedia:///cap/content/5786",
            "type": {
              "$Ref": "content/type/CMArticle"
            }
          }
        }
      }, {
        "request": {"uri": "content/6070", "method": "GET"},
        "response": {
          "body": {
            "name": "6070",
            "id": "coremedia:///cap/content/6070",
            "type": {
              "$Ref": "content/type/CMTeaser"
            }
          }
        }
      }, {
        "request": {"uri": "content/6462", "method": "GET"},
        "response": {
          "body": {
            "name": "6462",
            "id": "coremedia:///cap/content/6462",
            "type": {
              "$Ref": "content/type/CMPicture"
            }
          }
        }
      }
    ])
  }

  private function createTestling():void {
    var config:openCreateExternalPageDialogActionTestView = new openCreateExternalPageDialogActionTestView();
    var testContent:Object = beanFactory.getRemoteBean("content/500");
    testContent.getPreviewUrl = function ():String {
      return 'AuroraPbeExample.html';
    };

    config.bindTo = ValueExpressionFactory.createFromValue(testContent);
    config.contextInfoValueExpression = ValueExpressionFactory.createFromValue(new PreviewFocusForwarder(Ext.emptyFn));
    config.metadataTreeValueExpression = ValueExpressionFactory.createFromValue();
    config.selectedNodeValueExpression = ValueExpressionFactory.createFromValue();

    _viewPort = new OpenCreateExternalPageDialogActionTestView(config);
    _previewContextMenu = Ext.getCmp('previewContextMenu') as PreviewContextMenu;
    _previewPanel = _viewPort.get('previewPanel') as PreviewPanel;
    _breadCrump = _viewPort.get('breadcrumb') as Breadcrumb;

    var innerPreviewPanelCmp:InnerPreviewPanel = _previewPanel.findByType(innerPreviewPanel.xtype)[0] as InnerPreviewPanel;
    _previewPanel.mon(innerPreviewPanelCmp, "afterlayout", increaseIframeSize);
    _previewPanel.addListener(previewPanel.CURRENT_PREVIEW_METADATA_SELECTION_VARIABLE_NAME, function (e:PropertyChangeEvent):void {
      config.selectedNodeValueExpression.setValue(e.newValue);
    });
    _previewPanel.addListener(previewPanel.CURRENT_PREVIEW_METADATA_TREE_VARIABLE_NAME, function (e:PropertyChangeEvent):void {
      config.metadataTreeValueExpression.setValue(e.newValue);
    });
  }

  private function increaseIframeSize():void {
    // make preview iframe visible
    var component:Container = getPreviewIFrame();
    var dom:* = component.el.dom;
    dom.style.height = "100vh";
    dom.style.position = "fixed";
    dom.style.top = "60px";
  }

  private function getPreviewIFrame():Container {
    var iframes:Array = _previewPanel.find("itemId", InnerPreviewPanelBase.IFRAME_ITEM_ID);
    var component:Container = iframes[0];
    return component;
  }

  public function testPageIsAugmented():* {
    chain(
            loadContentRepository(),
            new Step("create testling",
                    function ():Boolean {
                      return true;
                    },
                    createTestling
            ),

            //open menu for augmented page
            new Step("wait for preview HTML",
                    isPreviewLoaded,
                    function ():void {
                      openContextMenu('#cm_pbe1')
                    }),
            new Step("wait for context menu", function ():Boolean {
              return _previewContextMenu.isVisible();
            }, Ext.emptyFn),
            new Step("wait for open in library to be enabled", function ():Boolean {
              return !isMenuItemDisabled(shopPageShowInLibraryMenuItem.LC_OPEN_IN_LIBRARY_MENU_ITEM_ID);
            }, function ():void {
              assertTrue(isMenuItemDisabled(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID));
              checkStoreBreadcrumbElement();
            }),

            //open menu for non augmented page
            new Step("open context menu for non augmented page", function ():Boolean {
              return true;
            }, function ():void {
              openContextMenu('#cm_pbe2')
            }),
            new Step("wait for open in library action to be disabled", function ():Boolean {
              return isMenuItemDisabled(shopPageShowInLibraryMenuItem.LC_OPEN_IN_LIBRARY_MENU_ITEM_ID);
            }, Ext.emptyFn),
            new Step("wait for augment shop action to be enabled", function ():Boolean {
              return !isMenuItemDisabled(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID)
            }, function ():void {
              checkStoreBreadcrumbElement();
            }),

            //open menu for content
            new Step("open context menu for content", function ():Boolean {
              return true;
            }, function ():void {
              openContextMenu('#cm_pbe3')
            }),
            new Step("wait for augment shop action to be disabled", function ():Boolean {
              return isMenuItemDisabled(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID);
            }, Ext.emptyFn),

            //open menu for non augmented page (WITH SEO-URL)
            new Step("open menu for augmented page (SEO) once again", function ():Boolean {
              return true;
            }, function ():void {
              openContextMenu('#cm_pbe2')
            }),
            new Step("wait for " + openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID + " menu item", function ():Boolean {
              return !isMenuItemDisabled(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID);
            }, function ():void {
              var menuItem:Item = getMenuItem(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID);
              menuItem.fireEvent("click");
              _previewContextMenu.hide();
            }),
            new Step("wait for dialog", function ():Boolean {
              var mixedCollection:MixedCollection = ComponentMgr.all.filter('itemId', 'createBtn');
              return mixedCollection.length > 0 && mixedCollection.get(0).isVisible();
            }, function ():void {
              // invoke handler of 'createBtn'
              ComponentMgr.all.filter('itemId', 'createBtn').get(0).handler();
            }),
            new Step("wait for processing data",function():Boolean {
              return "object" === typeof _processingData;
            }, function():void {
              assertEquals("content name", "not_augmented", _processingData.getName());
              assertEquals("external id", "not_augmented", _processingData.get("externalId"));
              _processingData = null;
            }),

            //open menu for non augmented page (NO SEO-URL)
            new Step("open menu for augmented page (NO-SEO) once again",
                    isPreviewLoaded,
                    function ():void {
                      openContextMenu('#cm_pbe4')
                    }),
            new Step("wait for context menu", function ():Boolean {
              return _previewContextMenu.isVisible();
            }, Ext.emptyFn),
            new Step("wait for '" + openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID +"' menu item", function ():Boolean {
              return !isMenuItemDisabled(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID);
            }, function ():void {
              var menuItem:Item = getMenuItem(openCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID);
              menuItem.fireEvent("click");
              _previewContextMenu.hide();
            }),
            new Step("wait for dialog", function ():Boolean {
              var mixedCollection:MixedCollection = ComponentMgr.all.filter('itemId', 'createBtn');
              return mixedCollection.length > 0 && mixedCollection.get(0).isVisible();
            }, function ():void {
              // invoke handler of 'createBtn'
              ComponentMgr.all.filter('itemId', 'createBtn').get(0).handler();
            }),
            new Step("wait for processing data",function():Boolean {
              return "object" === typeof _processingData;//> != null check
            }, function():void {
              assertEquals("content name", "AdvancedSearchDisplay", _processingData.getName());
              assertEquals("external id", "AdvancedSearchDisplay", _processingData.get("externalId"));
              assertEquals("external uri", "AdvancedSearchDisplay?catalogId={catalogId}&langId={langId}&storeId={storeId}", _processingData.get("externalUriPath"));
            })

    );
  }

  internal function checkStoreBreadcrumbElement():void {
// find the store breadcrumb element
    var cmp:Component = Ext.getCmp(window.$(".preview-breadcrumb .store-icon")[0].id);
    assertTrue(cmp.isVisible());
    assertTrue(cmp.disabled);
  }

  function isPreviewLoaded():Boolean {
    var previewUrl:String = _previewPanel.getPreviewUrl();
    if (previewUrl && previewUrl.indexOf("AuroraPbeExample.html") >= 0) {
      try {
        return "function" == typeof getPreviewIFrame().getContentWindow().coremedia.preview.$;
      } catch (e:Error) {
        trace("ignoring error " + e);
      }
    }
    return false;
  }

  private function openContextMenu(nodeId:String):void {
    //simulate 'show' Event
    var items:MixedCollection = _previewContextMenu.items;
    items.each(function(item:Item):void{
      var actionAdapter:MetadataToEntitiesActionAdapter = item.baseAction as MetadataToEntitiesActionAdapter;
      if(actionAdapter){
        actionAdapter.resolvedBeanValueExpression.setValue(undefined);
      }
    });

    var previewIFrame:Object = getPreviewIFrame();
    previewIFrame.getContentWindow().coremedia.preview.$(nodeId).trigger('contextmenu');
  }

  private function isMenuItemDisabled(itemId:String):Boolean {
    return getMenuItem(itemId).disabled;
  }

  private function getMenuItem(itemId:String):Item {
    return _previewContextMenu.find("itemId", itemId)[0];
  }

  override public function tearDown():void {
    super.tearDown();
    _viewPort.destroy();
  }

  public static function suite():TestSuite {
    var suite:TestSuite = new TestSuite();
    suite.addTestSuite(OpenCreateExternalPageDialogActionTest);
    return suite;
  }
}
}