import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import SortableSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SortableSwitchingContainer";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Config from "@jangaroo/runtime/Config";
import AssetSearchListContainer from "./AssetSearchListContainer";

interface AssetSearchListContainerBaseConfig extends Config<SortableSwitchingContainer> {
}

class AssetSearchListContainerBase extends SortableSwitchingContainer {
  declare Config: AssetSearchListContainerBaseConfig;

  constructor(config: Config<AssetSearchListContainer> = null) {
    super(config);
  }

  getActiveViewExpression(): ValueExpression {
    const collectionViewModel = editorContext._.getCollectionViewModel();
    return ValueExpressionFactory.create(CollectionViewModel.VIEW_PROPERTY, collectionViewModel.getMainStateBean());
  }
}

export default AssetSearchListContainerBase;
