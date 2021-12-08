import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Marketing from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Marketing";
import MarketingSpot from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpot";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import SearchResult from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/SearchResult";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import CollectionViewExtension from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewExtension";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import RepositoryListSorter from "@coremedia/studio-client.main.editor-components/sdk/collectionview/sort/RepositoryListSorter";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import UploadSettings from "@coremedia/studio-client.main.editor-components/sdk/upload/UploadSettings";
import { as, is, mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import catalogHelper from "../catalogHelper";
import CatalogRepositoryListContainer from "../components/repository/CatalogRepositoryListContainer";
import CatalogRepositoryToolbarContainer from "../components/repository/CatalogRepositoryToolbarContainer";
import CatalogSearchFilters from "../components/search/CatalogSearchFilters";
import CatalogSearchListContainer from "../components/search/CatalogSearchListContainer";
import CatalogSearchToolbarContainer from "../components/search/CatalogSearchToolbarContainer";
import FacetsFilterPanel from "../components/search/filters/FacetsFilterPanel";
import CatalogHelper from "../helper/CatalogHelper";
import categoryTreeRelation from "../tree/categoryTreeRelation";

class ECommerceCollectionViewExtension implements CollectionViewExtension {

  protected static readonly DEFAULT_TYPE_PRODUCT_RECORD: Record<string, any> = {
    name: ContentTypeNames.CONTENT,
    label: ECommerceStudioPlugin_properties.Product_label,
    icon: ECommerceStudioPlugin_properties.Product_icon,
  };

  constructor() {
  }

  search(searchParameters: SearchParameters, callback: AnyFunction): void {
    const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();

    if (store) {
      const catalogSearch = new RemoteServiceMethod("livecontext/search/" + store.getSiteId(), "GET");

      //to object conversion
      let searchParams = ObjectUtils.getPublicProperties(searchParameters);
      searchParams = ObjectUtils.removeUndefinedOrNullProperties(searchParams);

      catalogSearch.request(searchParams,
        (response: RemoteServiceMethodResponse): void => {
          const searchResult = new SearchResult();
          const responseObject = response.getResponseJSON();
          searchResult.setHits(responseObject["hits"]);
          searchResult.setTotal(responseObject["total"]);
          callback.call(null, searchResult);
        });
    }
  }

  getSearchOrSearchSuggestionsParameters(filters: Array<any>, mainStateBean: Bean): SearchParameters {
    const searchText: string = mainStateBean.get(CollectionViewModel.SEARCH_TEXT_PROPERTY);
    let catalogType: string = mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY);

    const searchParameters = Object.setPrototypeOf({}, SearchParameters.prototype);

    const catalogObject: CatalogObject = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);

    if (is(catalogObject, Category)) {
      searchParameters["category"] = catalogObject.getExternalTechId() || catalogObject.getExternalId();
      searchParameters["catalogAlias"] = CatalogHelper.getInstance().getCatalogAliasFromId(catalogObject.getId());
    }

    searchParameters.query = searchText || "*";

    if (!catalogType || catalogType === ContentTypeNames.CONTENT) {
      // Cannot search in 'All' catalog objects, so fall back to guessed type depending on catalogObject type:
      catalogType = (is(catalogObject, Marketing)) ? CatalogModel.TYPE_MARKETING_SPOT : CatalogModel.TYPE_PRODUCT;
    }

    searchParameters["searchType"] = catalogType;
    searchParameters["siteId"] = editorContext._.getSitesService().getPreferredSiteId();

    if (filters && filters.length > 0) {
      const facetFilterPanel: FacetsFilterPanel = filters[0];
      const filterValues = facetFilterPanel.getStateBean().toObject();
      let filterQueryFragments = [];

      for (const facetId in filterValues) {
        if (filterValues[facetId] !== undefined) {
          const queries: Array<any> = filterValues[facetId];
          if (queries && queries.length > 0) {
            filterQueryFragments = filterQueryFragments.concat(queries);
          }
        }
      }

      searchParameters.filterQuery = Array.from(filterQueryFragments);
    }

    return searchParameters;
  }

  getSearchSuggestionsUrl(): string {
    return "api/livecontext/suggestions";
  }

  getSearchViewItemId(): string {
    return CatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;
  }

  getSearchFiltersItemId(): string {
    return CatalogSearchFilters.ITEM_ID;
  }

  getAvailableSearchTypes(folder: any): Array<any> {
    return [ECommerceCollectionViewExtension.DEFAULT_TYPE_PRODUCT_RECORD];
  }

  getRepositoryToolbarItemId(): string {
    return CatalogRepositoryToolbarContainer.CATALOG_REPOSITORY_TOOLBAR_ITEM_ID;
  }

  getRepositoryListSorter(): RepositoryListSorter {
    return null;
  }

  isSearchable(): boolean {
    return true;
  }

  isUploadDisabledFor(folder: any): boolean {
    return true;
  }

  upload(files: Array<any>, folder: any, settings: UploadSettings): void {
  }

  getSearchToolbarItemId(): string {
    return CatalogSearchToolbarContainer.CATALOG_SEARCH_TOOLBAR_ITEM_ID;
  }

  getFolderContainerItemId(): string {
    return CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID;
  }

  getPathInfo(model: any): string {
    let catalogObject = as(model, CatalogObject);
    if (!catalogObject) {
      return "";
    }
    const namePath = [];
    const store = catalogObject.getStore();
    const isSingleRootCategory: boolean = !store || !store.getCatalogs() ||
            store.getCatalogs().length <= 1;
    while (catalogObject) {
      //when multi-catalog is not configured there will be only one root category. then the root category should called
      // 'Product Catalog' for the sake of backward compatibility.
      namePath.push(isSingleRootCategory && is(catalogObject, Category) && categoryTreeRelation.isRoot(catalogObject) ?
        ECommerceStudioPlugin_properties.StoreTree_root_category : catalogHelper.getDecoratedName(catalogObject));
      if (is(catalogObject, Product)) {
        catalogObject = as(catalogObject, Product).getCategory();
      } else if (is(catalogObject, Category)) {
        catalogObject = as(catalogObject, Category).getParent();
      } else if (is(catalogObject, MarketingSpot)) {
        catalogObject = as(catalogObject, MarketingSpot).getMarketing();
      } else {
        break;
      }
    }
    namePath.push(store.getName());
    return "/" + namePath.reverse().join("/");
  }

  showInTree(contents: Array<any>, view: string = null, treeModelId: string = null): void {
    // noop
  }
}
mixin(ECommerceCollectionViewExtension, CollectionViewExtension);

export default ECommerceCollectionViewExtension;
