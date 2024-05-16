import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import RepositoryCollectionViewExtension from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryCollectionViewExtension";
import SearchQueryUtil from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchQueryUtil";
import RepositoryListSorter from "@coremedia/studio-client.main.editor-components/sdk/collectionview/sort/RepositoryListSorter";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";
import CatalogSearchFilters from "../collectionview/search/CatalogSearchFilters";
import RepositoryCatalogSearchListContainer from "../repository/RepositoryCatalogSearchListContainer";
import CatalogRepositoryListSorter from "./CatalogRepositoryListSorter";
import CatalogTreeRelation from "./CatalogTreeRelation";
import ShowInCatalogTreeHelper from "./ShowInCatalogTreeHelper";

class CatalogCollectionViewExtension extends RepositoryCollectionViewExtension {
  static readonly CATALOG_FOLDER_CONTAINER_ITEM_ID: string = "catalogFolderContent";

  #repositoryListSorter: CatalogRepositoryListSorter = null;

  protected static readonly ALL_TYPE_RECORD: Record<string, any> = {
    name: ContentTypeNames.CONTENT,
    label: CatalogStudioPlugin_properties.Catalog_show_all,
    icon: CatalogStudioPlugin_properties.All_icon,
  };

  protected static readonly PRODUCT_TYPE_RECORD: Record<string, any> = {
    name: CatalogTreeRelation.CONTENT_TYPE_PRODUCT,
    label: CatalogStudioPlugin_properties.CMProduct_text,
    icon: CatalogStudioPlugin_properties.CMProduct_icon,
  };

  protected static readonly CATEGORY_TYPE_RECORD: Record<string, any> = {
    name: CatalogTreeRelation.CONTENT_TYPE_CATEGORY,
    label: CatalogStudioPlugin_properties.CMCategory_text,
    icon: CatalogStudioPlugin_properties.CMCategory_icon,
  };

  constructor() {
    super();
    this.#repositoryListSorter = new CatalogRepositoryListSorter(this);
  }

  override getAvailableSearchTypes(folder: any): Array<any> {
    return [CatalogCollectionViewExtension.ALL_TYPE_RECORD, CatalogCollectionViewExtension.PRODUCT_TYPE_RECORD, CatalogCollectionViewExtension.CATEGORY_TYPE_RECORD];
  }

  /**
   * Adds an additional query fragment to filter for categories if a category is selected
   */
  override applySearchParameters(folder: Content, filterQueryFragments: Array<any>, searchParameters: SearchParameters): SearchParameters {
    filterQueryFragments.push((searchParameters.includeSubfolders ? "allProductCategories" : "directProductCategories") + ":" + IdHelper.parseContentId(folder));
    searchParameters.folder = null;

    //re-apply doctype filtering without catalog doctypes
    const docTypeExclusions = editorContext._.getContentTypesExcludedFromSearchResult().filter(
      (excludedDocType: string): boolean =>
        excludedDocType !== CatalogTreeRelation.CONTENT_TYPE_CATEGORY &&
                      excludedDocType !== CatalogTreeRelation.CONTENT_TYPE_PRODUCT,
    );

    const excludeDoctypesQuery = SearchQueryUtil.buildExcludeContentTypesQuery(docTypeExclusions);
    searchParameters.filterQuery = Array.from(filterQueryFragments);
    searchParameters.filterQuery.push(excludeDoctypesQuery);

    return searchParameters;
  }

  override getFolderContainerItemId(): string {
    return CatalogCollectionViewExtension.CATALOG_FOLDER_CONTAINER_ITEM_ID;
  }

  override getRepositoryListSorter(): RepositoryListSorter {
    return this.#repositoryListSorter;
  }

  override getSearchViewItemId(): string {
    return RepositoryCatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;
  }

  override getSearchFiltersItemId(): string {
    return CatalogSearchFilters.ITEM_ID;
  }

  override isUploadDisabledFor(folder: any): boolean {
    return true;
  }

  override showInTree(contents: Array<any>, view: string = null, treeModelId: string = null): void {
    new ShowInCatalogTreeHelper(contents).showItems(treeModelId);
  }
}

export default CatalogCollectionViewExtension;
