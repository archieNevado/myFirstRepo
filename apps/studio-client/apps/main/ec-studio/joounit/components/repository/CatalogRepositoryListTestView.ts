import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogRepositoryList from "../../../src/components/repository/CatalogRepositoryList";

interface CatalogRepositoryListTestViewConfig extends Config<Viewport> {
}

class CatalogRepositoryListTestView extends Viewport {
  declare Config: CatalogRepositoryListTestViewConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryListTestView";

  constructor(config: Config<CatalogRepositoryListTestView> = null) {
    super(ConfigUtils.apply(Config(CatalogRepositoryListTestView, {

      items: [
        Config(CatalogRepositoryList, {
          itemId: CollectionViewConstants.LIST_VIEW,
          selectedNodeValueExpression: ValueExpressionFactory.createFromValue(),
          selectedItemsValueExpression: ValueExpressionFactory.createFromValue(),
          mySelectedItemsValueExpression: ValueExpressionFactory.createFromValue(),
        }),
      ],

    }), config));
  }
}

export default CatalogRepositoryListTestView;
