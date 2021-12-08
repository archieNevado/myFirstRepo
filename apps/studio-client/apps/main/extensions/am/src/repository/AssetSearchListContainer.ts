import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import SearchList from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/SearchList";
import SearchThumbnails from "@coremedia/studio-client.main.editor-components/sdk/collectionview/thumbnail/SearchThumbnails";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AssetCollectionViewExtension from "../AssetCollectionViewExtension";
import AssetSearchListContainerBase from "./AssetSearchListContainerBase";

interface AssetSearchListContainerConfig extends Config<AssetSearchListContainerBase>, Partial<Pick<AssetSearchListContainer,
  "searchResultHitsValueExpression" |
  "selectedItemsValueExpression"
>> {
}

class AssetSearchListContainer extends AssetSearchListContainerBase {
  declare Config: AssetSearchListContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.assetSearchListContainer";

  static readonly ITEM_ID: string = "assetSearchListContainer";

  searchResultHitsValueExpression: ValueExpression = null;

  /**
   * A value expression that specifies the selected items. This is mandatory.
   */
  selectedItemsValueExpression: ValueExpression = null;

  constructor(config: Config<AssetSearchListContainer> = null) {
    super((()=> ConfigUtils.apply(Config(AssetSearchListContainer, {
      itemId: AssetSearchListContainer.ITEM_ID,
      activeItemValueExpression: this.getActiveViewExpression(),

      items: [
        Config(SearchList, {
          instanceName: AssetCollectionViewExtension.INSTANCE_NAME,
          itemId: CollectionViewConstants.LIST_VIEW,
          searchResultHitsValueExpression: config.searchResultHitsValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
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

export default AssetSearchListContainer;
