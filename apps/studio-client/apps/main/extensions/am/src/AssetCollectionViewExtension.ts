import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import ContentTypeSelectorBase from "@coremedia/studio-client.ext.cap-base-components/contenttypes/ContentTypeSelectorBase";
import RepositoryCollectionViewExtension from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryCollectionViewExtension";
import SearchQueryUtil from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchQueryUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import AssetConstants from "./AssetConstants";
import AssetDoctypeUtil from "./AssetDoctypeUtil";
import AssetRepositoryListContainer from "./repository/AssetRepositoryListContainer";
import AssetSearchListContainer from "./repository/AssetSearchListContainer";
import AssetSearchFilters from "./search/AssetSearchFilters";

class AssetCollectionViewExtension extends RepositoryCollectionViewExtension {
  static readonly INSTANCE_NAME: string = "assets";

  #availableSearchTypes: Array<any> = null;

  constructor() {
    super();
    this.#availableSearchTypes = AssetCollectionViewExtension.#computeAvailableSearchTypes();
  }

  override isUploadDisabledFor(folder: any): boolean {
    return true;
  }

  override getAvailableSearchTypes(folder: any): Array<any> {
    return this.#availableSearchTypes;
  }

  override applySearchParameters(folder: Content, filterQueryFragments: Array<any>, searchParameters: SearchParameters): SearchParameters {
    const assetDocTypeNames = AssetDoctypeUtil.getAllAssetContentTypeNames();

    const docTypeExclusions = editorContext._.getContentTypesExcludedFromSearchResult().filter(
      (docType: string): boolean =>
        assetDocTypeNames.indexOf(docType) === -1,
    );

    const excludeDoctypesQuery = SearchQueryUtil.buildExcludeContentTypesQuery(docTypeExclusions);
    searchParameters.filterQuery = Array.from(filterQueryFragments);
    searchParameters.filterQuery.push(excludeDoctypesQuery);

    return searchParameters;
  }

  override getFolderContainerItemId(): string {
    return AssetRepositoryListContainer.ITEM_ID;
  }

  override getSearchFiltersItemId(): string {
    return AssetSearchFilters.ITEM_ID;
  }

  override getSearchViewItemId(): string {
    return AssetSearchListContainer.ITEM_ID;
  }

  static #computeAvailableSearchTypes(): Array<any> {
    const assetDocTypes = session._.getConnection().getContentRepository().getDocumentTypes().filter(
      (contentType: ContentType): boolean =>
        contentType.getName() === ContentTypeNames.DOCUMENT ||
                      contentType.isSubtypeOf(AssetConstants.DOCTYPE_ASSET),
    );

    return ContentTypeSelectorBase.getAvailableContentTypeEntries(assetDocTypes);
  }
}

export default AssetCollectionViewExtension;
