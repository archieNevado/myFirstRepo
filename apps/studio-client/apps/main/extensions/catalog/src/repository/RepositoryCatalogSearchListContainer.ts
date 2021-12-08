import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import SearchList from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/SearchList";
import SearchThumbnails from "@coremedia/studio-client.main.editor-components/sdk/collectionview/thumbnail/SearchThumbnails";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import RepositoryCatalogSearchListContainerBase from "./RepositoryCatalogSearchListContainerBase";

interface RepositoryCatalogSearchListContainerConfig extends Config<RepositoryCatalogSearchListContainerBase>, Partial<Pick<RepositoryCatalogSearchListContainer,
  "searchResultHitsValueExpression" |
  "selectedItemsValueExpression"
>> {
}

class RepositoryCatalogSearchListContainer extends RepositoryCatalogSearchListContainerBase {
  declare Config: RepositoryCatalogSearchListContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.repositoryCatalogSearchListContainer";

  static readonly VIEW_CONTAINER_ITEM_ID: string = "repositoryCatalogSearchContainer";

  searchResultHitsValueExpression: ValueExpression = null;

  selectedItemsValueExpression: ValueExpression = null;

  constructor(config: Config<RepositoryCatalogSearchListContainer> = null) {
    super((()=> ConfigUtils.apply(Config(RepositoryCatalogSearchListContainer, {
      itemId: RepositoryCatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID,
      activeItemValueExpression: this.getActiveViewExpression(),

      items: [
        Config(SearchList, {
          itemId: CollectionViewConstants.LIST_VIEW,
          searchResultHitsValueExpression: config.searchResultHitsValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
          instanceName: "catalog",
        }),
        Config(SearchThumbnails, {
          itemId: CollectionViewConstants.THUMBNAILS_VIEW,
          searchResultHitsValueExpression: config.searchResultHitsValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
        }),
      ],

    }), config))());
  }
}

export default RepositoryCatalogSearchListContainer;
