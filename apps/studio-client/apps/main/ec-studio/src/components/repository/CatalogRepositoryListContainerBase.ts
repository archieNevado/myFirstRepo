import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Config from "@jangaroo/runtime/Config";
import CatalogRepositoryListContainer from "./CatalogRepositoryListContainer";

interface CatalogRepositoryListContainerBaseConfig extends Config<SwitchingContainer> {
}

class CatalogRepositoryListContainerBase extends SwitchingContainer {
  declare Config: CatalogRepositoryListContainerBaseConfig;

  constructor(config: Config<CatalogRepositoryListContainer> = null) {
    super(config);
  }

  getActiveViewExpression(): ValueExpression {
    const collectionViewModel = editorContext._.getCollectionViewModel();
    return ValueExpressionFactory.create(CollectionViewModel.VIEW_PROPERTY, collectionViewModel.getMainStateBean());
  }
}

export default CatalogRepositoryListContainerBase;
