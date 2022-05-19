import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import ObservableUtil from "@coremedia/studio-client.ext.ui-components/util/ObservableUtil";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import CollectionViewConstants
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CollectionViewModel
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogRepositoryList from "./CatalogRepositoryList";
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

  protected getCatalogThumbnailItemsValueExpression(config): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Array<any> => {
      const [component] = this.query(createComponentSelector().itemId(CollectionViewConstants.LIST_VIEW).build());
      const listView = component ? as(component, CatalogRepositoryList) : null;
      if (!listView) {
        ObservableUtil.dependOn(this, "add");
        return CatalogHelper.getInstance().getChildren(config.selectedFolderValueExpression.getValue());
      }

      ObservableUtil.dependOn(listView.getStore(), "load");
      ObservableUtil.dependOn(listView.getStore(), "datachanged");

      return listView.getStore().getData().items.map((record: BeanRecord) => record.getBean());
    });
  }
}

export default CatalogRepositoryListContainerBase;
