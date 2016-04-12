package com.coremedia.blueprint.assets.studio {

import com.coremedia.blueprint.assets.studio.config.assetRepositoryListContainer;
import com.coremedia.blueprint.assets.studio.config.assetSearchListContainer;
import com.coremedia.blueprint.assets.studio.search.ExpirationDateFilterFieldset;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.cap.content.search.SearchParameters;
import com.coremedia.cms.editor.sdk.ContentTreeRelation;
import com.coremedia.cms.editor.sdk.collectionview.RepositoryCollectionViewExtension;
import com.coremedia.cms.editor.sdk.collectionview.search.ContentTypeSelectorBase;
import com.coremedia.cms.editor.sdk.collectionview.search.LastEditedFilterFieldset;
import com.coremedia.cms.editor.sdk.collectionview.search.SearchQueryUtil;
import com.coremedia.cms.editor.sdk.collectionview.search.StatusFilterFieldset;
import com.coremedia.cms.editor.sdk.editorContext;

public class AssetCollectionViewExtension extends RepositoryCollectionViewExtension {
  public static const INSTANCE_NAME:String = "assets";

  [ArrayElementType("Object")]
  private var availableSearchTypes:Array;
  private var assetTreeRelation:ContentTreeRelation = new AssetTreeRelation();

  public function AssetCollectionViewExtension() {
    this.availableSearchTypes = computeAvailableSearchTypes();
    assetTreeRelation = new AssetTreeRelation();
  }


  override public function getContentTreeRelation():ContentTreeRelation {
    return assetTreeRelation;
  }

  override public function isApplicable(model:Object):Boolean {
    var content:Content = model as Content;
    if (!content) {
      return false;
    }

    var path:String = content.getPath();
    if (path === undefined) {
      return undefined;
    }

    if (path) {
      return path.indexOf(AssetConstants.ASSET_LIBRARY_PATH) === 0;
    }

    return false;
  }

  override public function getEnabledSearchFilterIds():Array {
    return [
      StatusFilterFieldset.FILTER_ID,
      LastEditedFilterFieldset.FILTER_ID,
      'datefilter-fieldset-modificationdate',
      'datefilter-fieldset-publicationdate',
      'Location',
      'Subject',
      'Asset Download Portal',
      'rightsChannels',
      'rightsRegions',
      ExpirationDateFilterFieldset.FILTER_ID
    ];
  }

  override public function getAvailableSearchTypes(folder:Object):Array {
    return availableSearchTypes;
  }

  override public function applySearchParameters(folder:Content, filterQueryFragments:Array, searchParameters:SearchParameters):SearchParameters {
    var assetDocTypeNames:Array = AssetDoctypeUtil.getAllAssetContentTypeNames();

    var docTypeExclusions:Array = editorContext.getDocumentTypesExcludedFromSearchResult().filter(
            function (docType:String):Boolean {
              return assetDocTypeNames.indexOf(docType) === -1;
            });

    var excludeDoctypesQuery:String = SearchQueryUtil.buildExcludeDocumentTypesQuery(docTypeExclusions);
    searchParameters.filterQuery = [filterQueryFragments.join(" AND ")];
    searchParameters.filterQuery.push(excludeDoctypesQuery);

    return searchParameters;
  }

  override public function getFolderContainerItemId():String {
    return assetRepositoryListContainer.ITEM_ID;
  }

  override public function getSearchViewItemId():String {
    return assetSearchListContainer.ITEM_ID;
  }

  private static function computeAvailableSearchTypes():Array {
    var assetDocTypes:Array = session.getConnection().getContentRepository().getDocumentTypes().filter(
            function (contentType:ContentType):Boolean {
              return contentType.getName() === ContentTypeNames.DOCUMENT ||
                      contentType.isSubtypeOf(AssetConstants.DOCTYPE_ASSET);
            });

    return ContentTypeSelectorBase.getAvailableContentTypeEntries(assetDocTypes);
  }
}
}
