import CollectionViewContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewContainer";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface SearchProductImagesTestViewConfig extends Config<Viewport> {
}

class SearchProductImagesTestView extends Viewport {
  declare Config: SearchProductImagesTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.asset.studio.config.searchProductImagesTestView";

  static readonly TEST_VIEW_ID: string = "viewport";

  static readonly CATALOG_COLLECTION_VIEW_ITEM_ID: string = "catalogCollectionView";

  constructor(config: Config<SearchProductImagesTestView> = null) {
    super(ConfigUtils.apply(Config(SearchProductImagesTestView, {
      id: SearchProductImagesTestView.TEST_VIEW_ID,

      items: [
        Config(CollectionViewContainer, { height: 400 }),
      ],

    }), config));
  }
}

export default SearchProductImagesTestView;
