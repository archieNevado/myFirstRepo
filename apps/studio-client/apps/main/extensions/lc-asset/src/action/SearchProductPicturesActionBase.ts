import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import LiveContextCatalogObjectAction from "@coremedia-blueprint/studio-client.main.lc-studio/action/LiveContextCatalogObjectAction";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import SearchState from "@coremedia/studio-client.library-services-api/SearchState";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import SearchProductPicturesAction from "./SearchProductPicturesAction";

interface SearchProductPicturesActionBaseConfig extends Config<LiveContextCatalogObjectAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 */
class SearchProductPicturesActionBase extends LiveContextCatalogObjectAction {
  declare Config: SearchProductPicturesActionBaseConfig;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<SearchProductPicturesAction> = null) {
    super((()=>ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties).content, config, "searchProductPictures", { handler: bind(this, this.#myHandler) }))());
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    // the action should be enabled only if
    // there is only one catalog object and
    // it is a product and it has an external id
    if (catalogObjects.length !== 1) {
      return true;
    }
    const catalogObject: CatalogObject = catalogObjects[0];
    if (!is(catalogObject, Product)) {
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

  #myHandler(): void {
    const catalogObject: CatalogObject = this.getCatalogObjects()[0];
    if (is(catalogObject, Product)) {
      const product = cast(Product, catalogObject);

      //search site-based
      const preferredSite = editorContext._.getSitesService().getPreferredSite();

      const searchState = new SearchState();
      searchState.searchText = product.getExternalId();
      searchState.contentType = "CMPicture";
      if (preferredSite) {
        searchState.folder = preferredSite.getSiteRootFolder();
      }
      editorContext._.getCollectionViewManager().openSearch(searchState, true, CollectionViewConstants.LIST_VIEW);
    }
  }
}

export default SearchProductPicturesActionBase;
