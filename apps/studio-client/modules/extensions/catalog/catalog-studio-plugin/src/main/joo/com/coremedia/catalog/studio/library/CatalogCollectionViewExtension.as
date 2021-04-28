package com.coremedia.catalog.studio.library {

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.cap.content.search.SearchParameters;
import com.coremedia.catalog.studio.repository.RepositoryCatalogSearchListContainer;
import com.coremedia.cms.editor.sdk.collectionview.RepositoryCollectionViewExtension;
import com.coremedia.cms.editor.sdk.collectionview.search.SearchQueryUtil;
import com.coremedia.cms.editor.sdk.collectionview.sort.RepositoryListSorter;
import com.coremedia.cms.editor.sdk.editorContext;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.catalog.studio.CatalogStudioPlugin')]
public class CatalogCollectionViewExtension extends RepositoryCollectionViewExtension {
  public static const CATALOG_FOLDER_CONTAINER_ITEM_ID:String = "catalogFolderContent";

  private var repositoryListSorter:CatalogRepositoryListSorter;

  protected static const ALL_TYPE_RECORD:Object = {
    name: ContentTypeNames.CONTENT,
    label: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'Catalog_show_all'),
    icon: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'All_icon')
  };

  protected static const PRODUCT_TYPE_RECORD:Object = {
    name: CatalogTreeRelation.CONTENT_TYPE_PRODUCT,
    label: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'CMProduct_text'),
    icon: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'CMProduct_icon')
  };

  protected static const CATEGORY_TYPE_RECORD:Object = {
    name: CatalogTreeRelation.CONTENT_TYPE_CATEGORY,
    label: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'CMCategory_text'),
    icon: ResourceManager.getInstance().getString('com.coremedia.catalog.studio.CatalogStudioPlugin', 'CMCategory_icon')
  };

  public function CatalogCollectionViewExtension() {
    super();
    repositoryListSorter = new CatalogRepositoryListSorter(this);
  }

  override public function getAvailableSearchTypes(folder:Object):Array {
    return [ALL_TYPE_RECORD, PRODUCT_TYPE_RECORD, CATEGORY_TYPE_RECORD];
  }

  /**
   * Adds an additional query fragment to filter for categories if a category is selected
   */
  override public function applySearchParameters(folder:Content, filterQueryFragments:Array, searchParameters:SearchParameters):SearchParameters {
    filterQueryFragments.push((searchParameters.includeSubfolders ? "allProductCategories" : "directProductCategories") + ":" + IdHelper.parseContentId(folder));
    searchParameters.folder = null;

    //re-apply doctype filtering without catalog doctypes
    var docTypeExclusions:Array = editorContext.getContentTypesExcludedFromSearchResult().filter(
            function (excludedDocType:String):Boolean {
              return excludedDocType !== CatalogTreeRelation.CONTENT_TYPE_CATEGORY &&
                      excludedDocType !== CatalogTreeRelation.CONTENT_TYPE_PRODUCT
            });

    var excludeDoctypesQuery:String = SearchQueryUtil.buildExcludeContentTypesQuery(docTypeExclusions);
    searchParameters.filterQuery = Array.from(filterQueryFragments);
    searchParameters.filterQuery.push(excludeDoctypesQuery);

    return searchParameters;
  }

  override public function getFolderContainerItemId():String {
    return CATALOG_FOLDER_CONTAINER_ITEM_ID;
  }

  override public function getRepositoryListSorter():RepositoryListSorter {
    return repositoryListSorter;
  }

  override public function getSearchViewItemId():String {
    return RepositoryCatalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;
  }

  override public function isUploadDisabledFor(folder:Object):Boolean {
    return true;
  }

  override public function showInTree(contents:Array, view:String = null, treeModelId:String = null):void {
    new ShowInCatalogTreeHelper(contents).showItems(treeModelId);
  }
}
}
