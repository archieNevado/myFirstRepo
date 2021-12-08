import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogRepositoryList from "./CatalogRepositoryList";
import CatalogRepositoryListContainerBase from "./CatalogRepositoryListContainerBase";
import CatalogRepositoryThumbnails from "./CatalogRepositoryThumbnails";

interface CatalogRepositoryListContainerConfig extends Config<CatalogRepositoryListContainerBase>, Partial<Pick<CatalogRepositoryListContainer,
  "selectedFolderValueExpression" |
  "selectedItemsValueExpression" |
  "selectedRepositoryItemsValueExpression" |
  "createdContentValueExpression" |
  "newContentDisabledValueExpression"
>> {
}

class CatalogRepositoryListContainer extends CatalogRepositoryListContainerBase {
  declare Config: CatalogRepositoryListContainerConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryListContainer";

  static readonly VIEW_CONTAINER_ITEM_ID: string = "commerceCatalogRepositoryContainer";

  /**
   * value expression for the selected folder in the library tree
   */
  selectedFolderValueExpression: ValueExpression = null;

  /**
   * value expression for the selected items, either in the list view, or - if the selection there is empty - the
   * selected folder in the tree view.
   */
  selectedItemsValueExpression: ValueExpression = null;

  /**
   * value expression evaluating to multiple selected items in the repository view.
   *
   */
  selectedRepositoryItemsValueExpression: ValueExpression = null;

  /**
   * value expression that acts as a model for informing a view of a newly created content object.
   */
  createdContentValueExpression: ValueExpression = null;

  /**
   * Value expression that indicates if a new content can be created based on the current selection in the collection view.
   */
  newContentDisabledValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryListContainer> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogRepositoryListContainer, {
      itemId: CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID,
      activeItemValueExpression: this.getActiveViewExpression(),

      items: [
        Config(CatalogRepositoryList, {
          itemId: CollectionViewConstants.LIST_VIEW,
          mySelectedItemsValueExpression: config.selectedItemsValueExpression,
          selectedItemsValueExpression: config.selectedRepositoryItemsValueExpression,
          selectedNodeValueExpression: config.selectedFolderValueExpression,
          newContentDisabledValueExpression: config.newContentDisabledValueExpression,
          createdContentValueExpression: config.createdContentValueExpression,
        }),
        Config(CatalogRepositoryThumbnails, {
          itemId: CollectionViewConstants.THUMBNAILS_VIEW,
          newContentDisabledValueExpression: config.newContentDisabledValueExpression,
          selectedItemsValueExpression: config.selectedRepositoryItemsValueExpression,
          selectedFolderValueExpression: config.selectedFolderValueExpression,
          createdContentValueExpression: config.createdContentValueExpression,
        }),
      ],

    }), config))());
  }
}

export default CatalogRepositoryListContainer;
