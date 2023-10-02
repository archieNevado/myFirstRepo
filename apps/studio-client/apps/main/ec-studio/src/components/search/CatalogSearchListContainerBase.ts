import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogSearchListContainer from "./CatalogSearchListContainer";

interface CatalogSearchListContainerBaseConfig extends Config<SwitchingContainer> {
}

class CatalogSearchListContainerBase extends SwitchingContainer {
  declare Config: CatalogSearchListContainerBaseConfig;

  #activeViewExpression: ValueExpression = null;

  constructor(config: Config<CatalogSearchListContainer> = null) {
    super(config);
  }

  protected getActiveItemExpression(): ValueExpression {
    if (!this.#activeViewExpression) {
      const collectionViewModel = cast(EditorContextImpl, editorContext._).getCollectionViewModel();
      this.#activeViewExpression = ValueExpressionFactory.create(CollectionViewModel.VIEW_PROPERTY, collectionViewModel.getMainStateBean());
    }
    return this.#activeViewExpression;
  }
}

export default CatalogSearchListContainerBase;
