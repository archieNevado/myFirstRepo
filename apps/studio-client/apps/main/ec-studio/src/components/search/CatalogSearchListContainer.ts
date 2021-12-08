import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogSearchList from "./CatalogSearchList";
import CatalogSearchListContainerBase from "./CatalogSearchListContainerBase";
import CatalogSearchThumbnails from "./CatalogSearchThumbnails";

interface CatalogSearchListContainerConfig extends Config<CatalogSearchListContainerBase>, Partial<Pick<CatalogSearchListContainer,
  "searchResultHitsValueExpression" |
  "selectedItemsValueExpression"
>> {
}

class CatalogSearchListContainer extends CatalogSearchListContainerBase {
  declare Config: CatalogSearchListContainerConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchListContainer";

  static readonly VIEW_CONTAINER_ITEM_ID: string = "catalogSearchListContainer";

  constructor(config: Config<CatalogSearchListContainer> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogSearchListContainer, {
      itemId: CatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID,
      activeItemValueExpression: this.getActiveItemExpression(),

      items: [
        Config(CatalogSearchList, {
          itemId: CollectionViewConstants.LIST_VIEW,
          searchResultHitsValueExpression: config.searchResultHitsValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
        }),
        Config(CatalogSearchThumbnails, {
          itemId: CollectionViewConstants.THUMBNAILS_VIEW,
          searchResultHitsValueExpression: config.searchResultHitsValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
        }),
      ],

    }), config))());
  }

  searchResultHitsValueExpression: ValueExpression = null;

  selectedItemsValueExpression: ValueExpression = null;
}

export default CatalogSearchListContainer;
