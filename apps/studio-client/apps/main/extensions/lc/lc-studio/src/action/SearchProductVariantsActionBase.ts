import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductVariant from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductVariant";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import SearchState from "@coremedia/studio-client.library-services-api/SearchState";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import LiveContextCatalogObjectAction from "./LiveContextCatalogObjectAction";
import SearchProductVariantsAction from "./SearchProductVariantsAction";

interface SearchProductVariantsActionBaseConfig extends Config<LiveContextCatalogObjectAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 */
class SearchProductVariantsActionBase extends LiveContextCatalogObjectAction {
  declare Config: SearchProductVariantsActionBaseConfig;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<SearchProductVariantsAction> = null) {
    super((()=>ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties).content, config, "searchProductVariants", { handler: bind(this, this.#doExecute) }))());
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    // the action should be enabled only if
    // there is only one catalog object and
    // it is a product but no product variant and
    // it has an external id
    if (catalogObjects.length !== 1) {
      return true;
    }
    const catalogObject: CatalogObject = catalogObjects[0];
    if (!is(catalogObject, Product) || is(catalogObject, ProductVariant)) {
      return true;
    }

    if (!cast(Product, catalogObject).getExternalId()) {
      return true;
    }

    return super.isDisabledFor(catalogObjects);
  }

  protected override isHiddenFor(catalogObjects: Array<any>): boolean {
    return super.isHiddenFor(catalogObjects) || this.isDisabledFor(catalogObjects);
  }

  #doExecute(): void {
    const catalogObject: CatalogObject = this.getCatalogObjects()[0];
    if (is(catalogObject, Product)) {
      const product = cast(Product, catalogObject);

      const collectionViewModel = cast(EditorContextImpl, editorContext._).getCollectionViewModel();

      const searchState = new SearchState();
      searchState.searchText = product.getExternalId();
      searchState.contentType = CatalogModel.TYPE_PRODUCT_VARIANT;

      const selection: any = collectionViewModel.getMainStateBean().get(CollectionViewModel.FOLDER_PROPERTY);
      collectionViewModel.setSearchState(true, CollectionViewConstants.LIST_VIEW, searchState, selection);

      editorContext._.getCollectionViewManager().openSearch(searchState, false, CollectionViewConstants.LIST_VIEW);
    }
  }
}

export default SearchProductVariantsActionBase;
