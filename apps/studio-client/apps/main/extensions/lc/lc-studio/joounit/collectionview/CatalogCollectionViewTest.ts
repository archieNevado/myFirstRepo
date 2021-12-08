import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import CatalogRepositoryList from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryList";
import CatalogRepositoryListContainer from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryListContainer";
import CatalogSearchContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchContextMenu";
import CatalogSearchList from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchList";
import CatalogSearchListContainer from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchListContainer";
import CatalogTreeDragDropModel from "@coremedia-blueprint/studio-client.main.ec-studio/components/tree/impl/CatalogTreeDragDropModel";
import CatalogTreeModel from "@coremedia-blueprint/studio-client.main.ec-studio/components/tree/impl/CatalogTreeModel";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ECommerceCollectionViewExtension from "@coremedia-blueprint/studio-client.main.ec-studio/library/ECommerceCollectionViewExtension";
import contentTreeRelationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTreeRelationRegistry";
import ActionStep from "@coremedia/studio-client.client-core-test-helper/ActionStep";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import ThumbnailResolverFactory from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import ContextMenuEventAdapter from "@coremedia/studio-client.ext.ui-components/util/ContextMenuEventAdapter";
import QtipUtil from "@coremedia/studio-client.ext.ui-components/util/QtipUtil";
import TableUtil from "@coremedia/studio-client.ext.ui-components/util/TableUtil";
import SearchState from "@coremedia/studio-client.library-services-api/SearchState";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CollectionViewContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewContainer";
import CollectionViewManagerInternal from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewManagerInternal";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import SearchArea from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchArea";
import ComponentBasedEntityWorkAreaTabType from "@coremedia/studio-client.main.editor-components/sdk/desktop/ComponentBasedEntityWorkAreaTabType";
import SidePanelManagerImpl from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/SidePanelManagerImpl";
import SidePanelStudioPlugin from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/SidePanelStudioPlugin";
import sidePanelManager from "@coremedia/studio-client.main.editor-components/sdk/desktop/sidepanel/sidePanelManager";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Event from "@jangaroo/ext-ts/event/Event";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Item from "@jangaroo/ext-ts/menu/Item";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import TextItem from "@jangaroo/ext-ts/toolbar/TextItem";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { as, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogTeaserThumbnailResolver from "../../src/CatalogTeaserThumbnailResolver";
import CatalogThumbnailResolver from "../../src/CatalogThumbnailResolver";
import LivecontextStudioPlugin from "../../src/LivecontextStudioPlugin";
import LivecontextStudioPluginBase from "../../src/LivecontextStudioPluginBase";
import LivecontextCollectionViewActionsPlugin from "../../src/library/LivecontextCollectionViewActionsPlugin";
import LivecontextCollectionViewExtension from "../../src/library/LivecontextCollectionViewExtension";
import LivecontextContentTreeRelation from "../../src/library/LivecontextContentTreeRelation";
import AbstractLiveContextStudioTest from "../AbstractLiveContextStudioTest";

class CatalogCollectionViewTest extends AbstractLiveContextStudioTest {
  static readonly #CATALOG_REPOSITORY_CONTAINER: string = CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID;

  static readonly #CATALOG_SEARCH_LIST_CONTAINER: string = CatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;

  #testling: CollectionView = null;

  #searchProductVariantsContextMenuItem: Item = null;

  #getPreferredSite: AnyFunction = null;

  #preferredSiteExpression: ValueExpression = null;

  override setUp(): void {
    super.setUp();
    this.#preferredSiteExpression = ValueExpressionFactory.create("site", beanFactory._.createLocalBean({ site: "HeliosSiteId" }));
    this.#getPreferredSite = editorContext._.getSitesService().getPreferredSiteId;
    editorContext._.getSitesService().getPreferredSiteId = ((): string =>
      this.#preferredSiteExpression.getValue()
    );
    //use SidePanelStudioPlugin to register the CollectionViewContainer
    const plugin: SidePanelStudioPlugin = Ext.create(SidePanelStudioPlugin, {});
    plugin.init(editorContext._);
    //use ECommerceStudioPlugin to add CatalogRepositoryListContainer, CatalogSearchListContainer etc.
    new ECommerceStudioPlugin();
    new LivecontextCollectionViewActionsPlugin();

    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT));
    editorContext._.registerThumbnailResolver(new CatalogTeaserThumbnailResolver(LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER));
    editorContext._.registerThumbnailResolver(ThumbnailResolverFactory.create(LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, "pictures"));

    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_CATEGORY));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING_SPOT));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT));
    editorContext._.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT_VARIANT));

    // For the sake of the test, let's assume everything can be opened in a tab.
    // Cleaner alternative: Register all tab types.
    ComponentBasedEntityWorkAreaTabType.canBeOpenedInTab = ((): boolean => true);

    QtipUtil.registerQtipFormatter();
  }

  testCatalogLibrary(): void {
    this.chain(
      //initialize the catalog library
      this.#initStore(),
      this.loadContentRepository(),
      this.waitForContentRepositoryLoaded(),
      this.loadContentTypes(),
      this.waitForContentTypesLoaded(),
      this.#createTestlingStep(),

      //test catalog repository thumbnail view
      this.#selectNode("livecontext/category/HeliosSiteId/catalog/Women"),
      this.#waitUntilSwitchToListButtonIsPressed(),
      this.#switchToThumbnailView(),
      this.#waitUntilSwitchToListButtonIsUnpressed(),
      this.#waitUntilThumbnailViewIsActive(),

      //test context menu on the repository list and thumbnail view
      this.#switchToListView(),
      this.#waitUntilListViewIsActive(),
      this.#selectNode("livecontext/category/HeliosSiteId/catalog/Dresses"),
      this.#waitUntilProductIsLoadedInRepositoryList(),
      this.#waitUntilSearchProductVariantToolbarButtonIsHidden(),
      this.#openContextMenuOnFirstItemOfRepositoryList(),
      this.#waitUntilRepositoryListContextMenuOpened(),
      this.#waitUntilSearchProductVariantToolbarButtonIsEnabled(),
      this.#waitUntilSearchProductVariantContextMenuIsEnabled(),
      this.#searchProductVariantsUsingContextMenu(),
      this.#waitUntilSearchModeIsActive(),
      this.#waitUntilProductVariantIsLoadedInSearchList(),
      this.#waitUntilCatalogSearchListIsLoadedAndNotEmpty(2, AbstractCatalogTest.HERMITAGE_RUCHED_BODICE_COCKTAIL_DRESS),
      //now test that the variant search is hidden on product variants themselves
      this.#selectFirstItemOfSearchList(),
      this.#openContextMenuOnFirstItemOfSearchList(),
      this.#waitUntilSearchListContextMenuOpened(),
      this.#waitUntilSearchProductVariantToolbarButtonIsHidden(),
      this.#waitUntilSearchProductVariantContextMenuIsHidden(),

      // test marketing spots
      this.#switchToRepositoryMode(),
      this.#selectNode("livecontext/marketing/HeliosSiteId"),
      this.#waitUntilSwitchToListButtonIsPressed(),
      this.#switchToThumbnailView(),
      this.#waitUntilSwitchToListButtonIsUnpressed(),
      this.#waitUntilThumbnailViewIsActive(),

      //test product search
      this.#selectStore(),
      this.#triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT),
      this.#waitUntilSearchModeIsActive(),
      this.#waitUntilSwitchToListButtonIsPressed(),
      this.#waitUntilCatalogSearchListIsLoadedAndNotEmpty(2, AbstractCatalogTest.ORANGES_NAME),
      this.#waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(2),
      this.#switchToThumbnailView(),
      this.#waitUntilSwitchToListButtonIsUnpressed(),
      this.#waitUntilThumbnailViewIsActive(),

      //test product variant search
      this.#switchToListView(),
      this.#waitUntilListViewIsActive(),
      this.#triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT_VARIANT),
      this.#waitUntilCatalogSearchListIsLoadedAndNotEmpty(3, AbstractCatalogTest.ORANGES_SKU_NAME),
      this.#waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(3),
      this.#switchToThumbnailView(),
      this.#waitUntilSwitchToListButtonIsUnpressed(),
      this.#waitUntilThumbnailViewIsActive(),
    );
  }

  override tearDown(): void {
    super.tearDown();
    editorContext._.getSitesService().getPreferredSiteId = this.#getPreferredSite;
    //we have to reset the items of the side panel manager so that it creates CollectionViewContainer anew.
    cast(SidePanelManagerImpl, sidePanelManager._).resetItems();
  }

  #createTestling(): void {
    const collectionViewManagerInternal =
            (as((editorContext._.getCollectionViewManager()), CollectionViewManagerInternal));

    const catalogTreeModel = new CatalogTreeModel();
    collectionViewManagerInternal.addTreeModel(catalogTreeModel, new CatalogTreeDragDropModel(catalogTreeModel));

    const isApplicable: AnyFunction = (): boolean => false ;
    editorContext._.getCollectionViewExtender().addExtension(new ECommerceCollectionViewExtension(), isApplicable);

    const lcContentTreeRelation = new LivecontextContentTreeRelation();
    contentTreeRelationRegistry._.addExtension(lcContentTreeRelation, LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));
    editorContext._.getCollectionViewExtender().addExtension(new LivecontextCollectionViewExtension(), LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));

    const cvContainer = as(sidePanelManager._.getOrCreateComponent(CollectionViewContainer.ID), CollectionViewContainer);
    const viewportConfig = Config(Viewport);
    viewportConfig.items = [cvContainer];
    const myViewPort = new Viewport(viewportConfig);
    myViewPort.show();
    this.#testling = as(cvContainer.getComponent(CollectionView.COLLECTION_VIEW_ID), CollectionView);
  }

  #getSearchArea(): SearchArea {
    return cast(SearchArea, this.#testling.getComponent(CollectionView.SEARCH_AREA_ITEM_ID));
  }

  #getSearchList(): CatalogSearchList {
    const catalogSearch = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.SEARCH_MODE));
    const searchList = cast(SwitchingContainer, cast(Container, catalogSearch.getComponent("searchSwitchingContainer")));
    const searchContainer = cast(CatalogSearchListContainer, searchList.getComponent(CatalogCollectionViewTest.#CATALOG_SEARCH_LIST_CONTAINER));
    //ensure type cast!!!! there are other list views too
    return as(searchContainer.getComponent(CollectionViewConstants.LIST_VIEW), CatalogSearchList);
  }

  #getRepositoryContainer(): CatalogRepositoryList {
    const repositoryContainer = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.REPOSITORY_MODE));
    const repositorySwitch = cast(SwitchingContainer, cast(Container, repositoryContainer.getComponent("listViewSwitchingContainer")));
    const repositoryListContainer = cast(CatalogRepositoryListContainer, repositorySwitch.getComponent(CatalogCollectionViewTest.#CATALOG_REPOSITORY_CONTAINER));
    //ensure type cast!!!! there are other list views too
    return as(repositoryListContainer.getComponent(CollectionViewConstants.LIST_VIEW), CatalogRepositoryList);
  }

  #getRepositorySwitchingContainer(): SwitchingContainer {
    const myCatalogRepositoryContainer = cast(Container, this.#getCollectionModesContainer().getComponent(CollectionViewModel.REPOSITORY_MODE));
    const listViewSwitchingContainer = cast(Container, myCatalogRepositoryContainer.getComponent("listViewSwitchingContainer"));
    const repositorySwitchingContainer = cast(SwitchingContainer, listViewSwitchingContainer.getComponent(CatalogCollectionViewTest.#CATALOG_REPOSITORY_CONTAINER));
    return repositorySwitchingContainer;
  }

  #getRepositoryList(): CatalogRepositoryList {
    const repositorySwitchingContainer = this.#getRepositorySwitchingContainer();
    return as(cast(CatalogRepositoryList, repositorySwitchingContainer.getComponent(CollectionViewConstants.LIST_VIEW)), CatalogRepositoryList);
  }

  #getCollectionModesContainer(): SwitchingContainer {
    return cast(SwitchingContainer, this.#testling.getComponent(CollectionView.COLLECTION_MODES_CONTAINER_ITEM_ID));
  }

  #getFooter(): Toolbar {
    return cast(Toolbar, this.#testling.getComponent(CollectionView.FOOTER_INFO_ITEM_ID));
  }

  #getFooterTotalHitsLabel(): TextItem {
    return cast(TextItem, this.#getFooter().getComponent("totalHitsLabel"));
  }

  #createTestlingStep(): Step {
    return new Step("Create the testling",
      (): boolean =>
        true
      , bind(
        this, this.#createTestling),
    );
  }

  #initStore(): Step {
    return new Step("Load Store Data",
      (): boolean => {
        const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
        return store !== null && store !== undefined;
      },
      CatalogHelper.getInstance().getActiveStoreExpression().getValue(),
    );
  }

  #selectStore(): Step {
    return new Step("Select Store Node",
      (): boolean => {
        const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
        this.#testling.setOpenPath(store);
        return !!this.#getRepositoryContainer() && this.#getRepositoryContainer().rendered && !!this.#getRepositoryContainer().getStore();
      },
    );
  }

  #selectNode(path: string): Step {
    return new ActionStep("selecting '" + path + "' tree node",
      (): void =>
        this.#testling.setOpenPath(beanFactory._.getRemoteBean(path)),
    );
  }

  #triggerSearch(searchTerm: string, searchType: string): Step {
    return new Step("trigger catalog search",
      (): boolean =>
        true
      ,
      (): void =>
        CatalogCollectionViewTest.#setSearchStateAndTriggerSearch(searchTerm, searchType),
    );
  }

  #waitUntilSwitchToListButtonIsPressed(): Step {
    return new Step("Switch to List Button should be pressed",
      (): boolean =>
        this.#getSwitchToListViewButton() && this.#getSwitchToListViewButton().pressed,
    );
  }

  #waitUntilRepositoryListContextMenuOpened(): Step {
    return new Step("Wait for the context menu on the repository list to be opened",
      (): boolean =>
        !!this.#findCatalogRepositoryContextMenu(),

    );
  }

  #waitUntilSearchListContextMenuOpened(): Step {
    return new Step("Wait for the context menu on the search list to be opened",
      (): boolean =>
        !!this.#findCatalogSearchListContextMenu(),

    );
  }

  #getSwitchToRepositoryModeButton(): Button {
    return cast(Button, this.#getSearchArea().queryById(SearchArea.SWITCH_TO_REPOSITORY_BUTTON_ITEM_ID));
  }

  #getSwitchToListViewButton(): Button {
    return cast(Button, this.#getActiveToolbarViewSwitch().queryById("list"));
  }

  #getSwitchToThumbnailViewButton(): Button {
    return cast(Button, this.#getActiveToolbarViewSwitch().queryById("thumb"));
  }

  #getProductVariantSearchButton(): Button {
    return cast(Button, this.#getActiveToolbar().queryById(LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID));
  }

  #getActiveToolbarViewSwitch(): Container {
    const itemId = this.#getCollectionModesContainer().getActiveItem().getItemId();
    let container: Container;
    if (itemId === "repository") {
      container = as(cast(Container, this.#testling.queryById("toolbarSwitchingContainer")).queryById("catalogRepositoryToolbar"), Container);
    } else {
      container = as(cast(Container, this.#testling.queryById("searchToolbar")).queryById("searchToolbarSwitchingContainer"), Container);
    }

    return as(container.queryById("switchButtonsContainer"), Container);
  }

  #getActiveToolbar(): Toolbar {
    const itemId = this.#getCollectionModesContainer().getActiveItem().getItemId();
    if (itemId === "repository") {
      const repoContainer = as(cast(Container, this.#testling.queryById("toolbarSwitchingContainer")).queryById("catalogRepositoryToolbar"), Container);
      return as(repoContainer.queryById("commerceToolbar"), Toolbar);
    }

    const searchContainer = as(cast(Container, this.#testling.queryById("searchToolbar")).queryById("searchToolbarSwitchingContainer"), Container);
    return as(searchContainer.queryById("commerceToolbar"), Toolbar);
  }

  #waitUntilSwitchToListButtonIsUnpressed(): Step {
    return new Step("Switch to List Button should be unpressed",
      (): boolean =>
        !this.#getSwitchToListViewButton().pressed
      ,
      (): void => {
        //nothing to do
      });
  }

  #waitUntilListViewIsActive(): Step {
    return new Step("List View should be active",
      (): boolean =>
        this.#getRepositorySwitchingContainer().getActiveItemValue() === CollectionViewConstants.LIST_VIEW,
    );
  }

  #waitUntilThumbnailViewIsActive(): Step {
    return new Step("Thumbnail View should be active",
      (): boolean =>
        this.#getRepositorySwitchingContainer().getActiveItemValue() === CollectionViewConstants.THUMBNAILS_VIEW,
    );
  }

  #waitUntilSearchModeIsActive(): Step {
    return new Step("Search Mode should be active",
      (): boolean =>
        this.#getCollectionModesContainer().getActiveItemValue() === CollectionViewModel.SEARCH_MODE,

    );
  }

  #waitUntilProductIsLoadedInRepositoryList(): Step {
    return new Step("Wait for the repository list to be loaded with products",
      (): boolean =>
        this.#getRepositoryList().getStore().getCount() > 0 &&
                      Ext.get(TableUtil.getCellAsDom(this.#getRepositoryList(), 0, 0)).query("[aria-label]")[0].getAttribute("aria-label") === ECommerceStudioPlugin_properties.Product_label,

    );
  }

  #waitUntilProductVariantIsLoadedInSearchList(): Step {
    return new Step("Wait for the search list to be loaded with product variants",
      (): boolean =>
        this.#getSearchList().getStore().getCount() > 0 &&
                      Ext.get(TableUtil.getCellAsDom(this.#getSearchList(), 0, 0)).query("[aria-label]")[0].getAttribute("aria-label") === ECommerceStudioPlugin_properties.ProductVariant_label,

    );
  }

  #waitUntilSearchProductVariantToolbarButtonIsHidden(): Step {
    return new Step("Wait for the product variant search toolbar button is hidden",
      (): boolean =>
        this.#getProductVariantSearchButton().hidden,

    );
  }

  #waitUntilSearchProductVariantToolbarButtonIsEnabled(): Step {
    return new Step("Wait for the product variant search toolbar button is enabled",
      (): boolean =>
        !this.#getProductVariantSearchButton().disabled,

    );
  }

  #waitUntilSearchProductVariantContextMenuIsEnabled(): Step {
    return new Step("Wait for the product variant search context menu item is enabled",
      (): boolean =>
        !this.#searchProductVariantsContextMenuItem.disabled,

    );
  }

  #waitUntilSearchProductVariantContextMenuIsHidden(): Step {
    return new Step("Wait for the product variant search context menu item is hidden",
      (): boolean =>
        this.#searchProductVariantsContextMenuItem.hidden,

    );
  }

  #switchToListView(): Step {
    return new ActionStep("Switch to list view",
      (): void => {
        const handler = this.#getSwitchToListViewButton().initialConfig.handler;
        typeof handler !== "string" && handler(this.#getSwitchToListViewButton(), null);
      });
  }

  #switchToThumbnailView(): Step {
    return new ActionStep("Switch to thumbnail view",
      (): void => {
        const handler = this.#getSwitchToThumbnailViewButton().initialConfig.handler;
        typeof handler !== "string" && handler(this.#getSwitchToThumbnailViewButton(), null);
      });
  }

  #switchToRepositoryMode(): Step {
    return new ActionStep("Switch to repository mode",
      (): void => {
        const handler = this.#getSwitchToRepositoryModeButton().initialConfig.handler;
        typeof handler !== "string" && handler(this.#getSwitchToRepositoryModeButton(), null);
      });
  }

  #waitUntilCatalogSearchListIsLoadedAndNotEmpty(expectedResultCount: int, firstItemName: string): Step {
    return new Step("Wait for the catalog search list to be loaded and the search items to be " + expectedResultCount +
            " and the first item to be " + firstItemName,
    (): boolean => {
      if (this.#getSearchList().getStore() && this.#getSearchList().getStore().getCount() <= 0) {
        return false;
      }
      const name = cast(CatalogObject, cast(BeanRecord, this.#getSearchList().getStore().getAt(0)).getBean()).getName();
      return firstItemName === name && expectedResultCount === this.#getSearchList().getStore().getCount();
    },
    );
  }

  #waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(expectedResultCount: int): Step {
    return new Step("footer and catalog search list should be loaded and must not be empty",
      (): boolean => {
        const footerTotalHitsLabel = this.#getFooterTotalHitsLabel();
        const searchList = this.#getSearchList();
        return footerTotalHitsLabel && searchList.getStore() && searchList.getStore().getCount() > 0 &&
                      footerTotalHitsLabel.html && footerTotalHitsLabel.html.indexOf(String(expectedResultCount)) === 0;

      },
      (): void => {
        //nothing to do
      });
  }

  #openContextMenuOnFirstItemOfRepositoryList(): Step {
    return new Step("Open Context Menu on the first item of the repository list",
      (): boolean =>
        true
      ,
      (): void =>
        this.#openContextMenu(this.#getRepositoryList(), 0),

    );

  }

  #selectFirstItemOfSearchList(): Step {
    return new Step("Open Context Menu on the first item of the searhc list",
      (): boolean =>
        true
      ,
      (): void => {
        const sm = as(this.#getSearchList().getSelectionModel(), RowSelectionModel);
        sm.select(0);
      },
    );

  }

  #openContextMenuOnFirstItemOfSearchList(): Step {
    return new Step("Open Context Menu on the first item of the searhc list",
      (): boolean => {
        const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
          component.isXType(CatalogSearchContextMenu.xtype),
        )[0], CatalogSearchContextMenu);
        return contextMenu.items.getRange().some((item: Item): boolean =>
          ! !item.baseAction && !item.isHidden(),
        );
      },
      (): void =>
        this.#openContextMenu(this.#getSearchList(), 0),

    );

  }

  #searchProductVariantsUsingContextMenu(): Step {
    return new Step("Search Product Variants using the context menu",
      (): boolean =>
        true
      ,
      (): void => {
        const handler = this.#searchProductVariantsContextMenuItem.initialConfig.handler;
        typeof handler !== "string" && handler(this.#searchProductVariantsContextMenuItem, null);
      },
    );

  }

  #openContextMenu(grid: GridPanel, row: number): void {
    var event = cast(Event, {
      getXY: (): Array<any> =>
        Ext.fly(event.getTarget()).getXY()
      ,
      preventDefault: (): void =>{
        //do nothing
      },
      getTarget: (): HTMLElement =>
        TableUtil.getCellAsDom(grid, row, 1)
      ,
      "type": ContextMenuEventAdapter.EVENT_NAME,
    });
    grid.fireEvent("rowcontextmenu", grid, null, null, row, event);
  }

  #findCatalogRepositoryContextMenu(): CatalogRepositoryContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogRepositoryContextMenu.xtype),
    )[0], CatalogRepositoryContextMenu);
    if (contextMenu) {
      this.#searchProductVariantsContextMenuItem = as(contextMenu.getComponent(LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }

  #findCatalogSearchListContextMenu(): CatalogSearchContextMenu {
    const contextMenu = as(ComponentManager.getAll().filter((component: Component): boolean =>
      !component.up() && !component.hidden && component.isXType(CatalogSearchContextMenu.xtype),
    )[0], CatalogSearchContextMenu);
    if (contextMenu) {
      this.#searchProductVariantsContextMenuItem = as(contextMenu.getComponent(LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID), Item);
    }

    return contextMenu;
  }

  static #setSearchStateAndTriggerSearch(searchTerm: string, searchType: string): void {
    const searchState = new SearchState();
    searchState.searchText = searchTerm;
    searchState.contentType = searchType;
    searchState.folder = CatalogHelper.getInstance().getActiveStoreExpression().getValue();

    editorContext._.getCollectionViewManager().openSearch(searchState, true, CollectionViewConstants.LIST_VIEW);
  }

}

export default CatalogCollectionViewTest;
