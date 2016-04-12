package com.coremedia.livecontext.asset.studio {
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionView;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewContainer;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewExtension;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.tree.LibraryTree;
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.config.collectionViewContainer;
import com.coremedia.cms.editor.sdk.desktop.sidepanel.SidePanelStudioPlugin;
import com.coremedia.cms.editor.sdk.desktop.sidepanel.sidePanelManager;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryContextMenu;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryList;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeDragDropModel;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeModel;
import com.coremedia.ecommerce.studio.config.catalogRepositoryContextMenu;
import com.coremedia.ecommerce.studio.config.catalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.library.ECommerceCollectionViewExtension;
import com.coremedia.livecontext.asset.studio.config.livecontextAssetStudioPlugin;
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewActionsPlugin;
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewExtension;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.util.ContextMenuEventAdapter;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Container;
import ext.Ext;
import ext.Toolbar;
import ext.data.Store;
import ext.grid.GridPanel;
import ext.menu.Item;

import js.HTMLElement;

public class SearchProductImagesTest extends AbstractCatalogAssetTest {

  private var viewport:SearchProductImagesTestView;
  private var testling:CollectionView;
  private var catalogTree:LibraryTree;
  private var searchProductPicturesContextMenuItem:Item;

  private var getPreferredSite:Function;
  private var preferredSiteExpression:ValueExpression;

  override public function setUp():void {
    super.setUp();
    preferredSiteExpression = ValueExpressionFactory.create('site', beanFactory.createLocalBean({site: 'HeliosSiteId'}));
    getPreferredSite = editorContext.getSitesService().getPreferredSiteId;
    editorContext.getSitesService().getPreferredSiteId = function ():String {
      return preferredSiteExpression.getValue();
    };
    //use SidePanelStudioPlugin to register the CollectionViewContainer
    var plugin:SidePanelStudioPlugin = new SidePanelStudioPlugin({});
    plugin.init(editorContext);
    //use ECommerceStudioPlugin to add CatalogRepositoryListContainer, CatalogSearchListContainer etc.
    new ECommerceStudioPlugin();
    new LivecontextCollectionViewActionsPlugin();
  }

  private function createTestling():void {
    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);

    var catalogTreeModel:CatalogTreeModel = new CatalogTreeModel();
    collectionViewManagerInternal.addTreeModel(catalogTreeModel, new CatalogTreeDragDropModel(catalogTreeModel));

    var extension:CollectionViewExtension = new ECommerceCollectionViewExtension();
    editorContext.getCollectionViewExtender().addExtension(extension);

    var lcExtension:CollectionViewExtension = new LivecontextCollectionViewExtension();
    editorContext.getCollectionViewExtender().addExtension(lcExtension);

    viewport = new SearchProductImagesTestView();

    var cvContainer:CollectionViewContainer = viewport.get(collectionViewContainer.ID) as CollectionViewContainer;
    sidePanelManager['items$1'][collectionViewContainer.ID] = cvContainer;
    testling = cvContainer.get(collectionView.COLLECTION_VIEW_ID) as CollectionView;

    new LivecontextCollectionViewActionsPlugin();

    catalogTree = getTree();
  }

  private function getRepositoryList():CatalogRepositoryList {
    var repositorySwitchingContainer:SwitchingContainer = getRepositorySwitchingContainer();
    return CatalogRepositoryList(repositorySwitchingContainer.get(CollectionViewConstants.LIST_VIEW)) as CatalogRepositoryList;
  }



  private function getRepositorySwitchingContainer():SwitchingContainer {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:SwitchingContainer = SwitchingContainer(listViewSwitchingContainer.get(catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    return repositorySwitchingContainer;
  }

  private function getCollectionModesContainer():SwitchingContainer {
    return SwitchingContainer(testling.get(collectionView.COLLECTION_MODES_CONTAINER_ITEM_ID));
  }

  private function getActiveToolbar():Toolbar {
    var itemId:String = getCollectionModesContainer().getActiveItem().getItemId();
    if(itemId === "repository") {
      var repoContainer:Container = testling.find("itemId", "toolbarSwitchingContainer")[0].find("itemId", "catalogRepositoryToolbar")[0];
      return repoContainer.find("itemId", "commerceToolbar")[0] as Toolbar;
    }

    var searchContainer:Container = testling.find("itemId", "searchToolbar")[0].find("itemId", "searchToolbarSwitchingContainer")[0];
    return searchContainer.find("itemId", "commerceToolbar")[0] as Toolbar;
  }


  private function getTree():LibraryTree {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    return myCatalogRepositoryContainer.get(collectionView.TREE_ITEM_ID) as LibraryTree;
  }

  override public function tearDown():void {
    super.tearDown();
    editorContext.getSitesService().getPreferredSiteId = getPreferredSite;
    viewport.destroy();
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Test the product images search
   */
  public function testSearchProductImages():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            waitUntilStoreIsSelected(),
            selectNextCatalogTreeNode(),
            selectNextCatalogTreeNode(),
            //wait for the product catalog node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the Apparel node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),

            //now the Dresses node is selected
            waitUntilProductIsLoadedInRepositoryList(),
            waitUntilSearchProductPicturesToolbarButtonIsInvisible(),
            openContextMenuOnFirstItemOfRepositoryList(),
            waitUntilRepositoryListContextMenuOpened(),
            waitUntilSearchProductPicturesToolbarButtonIsEnabled(),
            waitUntilSearchProductPicturesContextMenuIsEnabled(),
            searchProductPicturesUsingContextMenu(),
            waitUntilSearchModeIsActive(),
            waitUntilSearchTextIsPartnumber(),
            waitUntilSearchTypeIsPicture(),
            waitUntilSearchFolderIsRoot()
    );
  }

  private function createTestlingStep():Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            createTestling
    );
  }

  private function initStore():Step {
    return new Step("Load Store Data",
            function ():Boolean {
              var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
              return store !== null && store !== undefined;
            },
            CatalogHelper.getInstance().getActiveStoreExpression().getValue()
    );
  }

  private function getRepositoryContainer():CatalogRepositoryList {
    var repositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var repositorySwitch:SwitchingContainer = SwitchingContainer(Container(repositoryContainer.get("listViewSwitchingContainer")));
    var repositoryListContainer:CatalogRepositoryListContainer = CatalogRepositoryListContainer(repositorySwitch.get(catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    //ensure type cast!!!! there are other list views too
    return repositoryListContainer.get(CollectionViewConstants.LIST_VIEW) as CatalogRepositoryList;
  }

  private function selectStore():Step {
    return new Step("Select Store Node",
            function ():Boolean {
              var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
              testling.setOpenPath(store);
              return getRepositoryContainer() &&  getRepositoryContainer().rendered && getRepositoryContainer().getStore();
            }
    );
  }

  private function waitUntilStoreIsSelected():Step {
    return new Step("catalog tree should select the store",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                      "PerfectChefESite" === catalogTree.getSelectionModel().getSelectedNode().text;
            },
            function ():void {
              //nothing to do
            });
  }

  private function selectNextCatalogTreeNode():Step {
    return new Step("selecting next catalog tree node",
            function ():Boolean {
              return true;

            },
            function ():void {
              catalogTree.getSelectionModel().selectNext();
            });
  }

  private function waitUntilRepositoryListContextMenuOpened():Step {
    return new Step("Wait for the context menu on the repository list to be opened",
            function ():Boolean {
              return findCatalogRepositoryContextMenu();
            }
    );
  }

  private function getProductPicturesSearchButton():Button {
    return Button(getActiveToolbar().find("itemId", livecontextAssetStudioPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID)[0]);
  }

  private function waitUntilSearchModeIsActive():Step {
    return new Step("Search Mode should be active",
            function ():Boolean {
              return getCollectionModesContainer().getActiveItemValue() === CollectionViewModel.SEARCH_MODE;
            }
    );
  }

  private function waitUntilSearchTextIsPartnumber():Step {
    return new Step("Search Text should be the part number of the product",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              return mainStateBean.get(CollectionViewModel.SEARCH_TEXT_PROPERTY) === "AuroraWMDRS-1";
            }
    );
  }

  private function waitUntilSearchTypeIsPicture():Step {
    return new Step("Search Type should be CMPicture",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              return mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY) === "CMPicture";
            }
    );
  }

  private function waitUntilSearchFolderIsRoot():Step {
    return new Step("Search Folder should be root",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              var folder:ContentImpl = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);
              return folder.getPath() === "/";
            }
    );
  }

  private function waitUntilProductIsLoadedInRepositoryList():Step {
    return new Step("Wait for the repository list to be loaded with products",
            function ():Boolean {
              return getRepositoryList().getStore().getCount() > 0 &&
                      getRepositoryList().getView().getCell(0, 0)['textContent'] === ECommerceStudioPlugin_properties.INSTANCE.Product_label;
            }
    );
  }

  private function waitUntilSelectedTreeNodeIsExpanded():Step {
    return new Step("Wait for the selected node of the catalog tree to be expanded",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode().isExpanded();
            }
    );
  }

  private function waitUntilSearchProductPicturesToolbarButtonIsInvisible():Step {
    return new Step("Wait for the product pictures search toolbar button is invisible",
            function ():Boolean {
              return getProductPicturesSearchButton().hidden;
            }
    )
  }

  private function waitUntilSearchProductPicturesToolbarButtonIsEnabled():Step {
    return new Step("Wait for the product pictures search toolbar button is enabled",
            function ():Boolean {
              return !getProductPicturesSearchButton().disabled;
            }
    )
  }

  private function waitUntilSearchProductPicturesContextMenuIsEnabled():Step {
    return new Step("Wait for the product pictures search context menu item is enabled",
            function ():Boolean {
              return !searchProductPicturesContextMenuItem.disabled;
            }
    )
  }


  private function openContextMenuOnFirstItemOfRepositoryList():Step {
    return new Step("Open Context Menu on the first item of the repository list",
            function ():Boolean {
              return true;
            },
            function ():void {
              openContextMenu(getRepositoryList(), 0);
            }
    );

  }

  private function searchProductPicturesUsingContextMenu():Step {
    return new Step("Search Product Pictures using the context menu",
            function ():Boolean {
              return true;
            },
            function ():void {
              searchProductPicturesContextMenuItem.baseAction.execute();
            }
    );

  }

  private function openContextMenu(grid:GridPanel, row:Number):void {
    var event:Object = {
      getXY: function ():Array {
        return Ext.fly(grid.getView().getCell(row, 1)).getXY();
      },
      preventDefault: function ():void {
        //do nothing
      },
      getTarget: function():HTMLElement {
        return grid.getView().getCell(row, 1);
      },
      type: ContextMenuEventAdapter.EVENT_NAME
    };
    grid.fireEvent("rowcontextmenu", grid, row, event);
  }

  private function findCatalogRepositoryContextMenu():CatalogRepositoryContextMenu {
    var contextMenu:CatalogRepositoryContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
              return !component.ownerCt && !component.hidden && component.isXType(catalogRepositoryContextMenu.xtype);
            }) as CatalogRepositoryContextMenu;
    if (contextMenu) {
      searchProductPicturesContextMenuItem = contextMenu.getComponent(livecontextAssetStudioPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }

}
}
