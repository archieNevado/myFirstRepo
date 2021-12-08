import contentTreeRelationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTreeRelationRegistry";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import ThumbnailResolverFactory from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverFactory";
import SearchState from "@coremedia/studio-client.library-services-api/SearchState";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CollectionViewManagerInternal from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewManagerInternal";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import AMStudioPlugin from "./AMStudioPlugin";
import AMStudioPlugin_properties from "./AMStudioPlugin_properties";
import AssetCollectionViewExtension from "./AssetCollectionViewExtension";
import AssetConstants from "./AssetConstants";
import AssetDoctypeUtil from "./AssetDoctypeUtil";
import AssetTreeRelation from "./AssetTreeRelation";

interface AMStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class AMStudioPluginBase extends StudioPlugin {
  declare Config: AMStudioPluginBaseConfig;

  static readonly #SHOW_PROPERTY: string = "show";

  // if false, no rendition is downloadable per default
  // if true, all renditions are downloadable per default
  //noinspection JSFieldCanBeLocal
  static readonly #DEFAULT_SHOW_VALUE: boolean = false;

  constructor(config: Config<AMStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create(AssetConstants.DOCTYPE_ASSET, AssetConstants.PROPERTY_ASSET_THUMBNAIL));

    editorContext.registerContentInitializer(AssetConstants.DOCTYPE_PICTURE_ASSET, AMStudioPluginBase.#initAMPictureAsset);
    editorContext.registerContentInitializer(AssetConstants.DOCTYPE_VIDEO_ASSET, AMStudioPluginBase.#initAMVideoAsset);

    const contentRepository = editorContext.getSession().getConnection().getContentRepository();
    contentRepository.getChild(AssetConstants.ASSET_LIBRARY_PATH,
      (assetsFolder: Content): void => {
        if (assetsFolder) {
          assetsFolder.load((): void => {
            if (assetsFolder.getState().readable) {
              const collectionViewManager =
                            cast(CollectionViewManagerInternal, editorContext.getCollectionViewManager());
              collectionViewManager.addRepositoryTreeRoot(assetsFolder,
                AMStudioPlugin_properties.CollectionView_assetRootFolder_icon);
            }
          });
        }
      });

    AMStudioPluginBase.#addAssetExtensions();
    AMStudioPluginBase.#removeAssetDoctypesByDefault();

    // Colorful Studio type
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_GREEN, [
      AssetConstants.DOCTYPE_PICTURE_ASSET, AssetConstants.DOCTYPE_DOCUMENT_ASSET, AssetConstants.DOCTYPE_VIDEO_ASSET,
    ]);
  }

  protected static reloadAssetPreview(previewPanel: PreviewPanel): void {
    if (previewPanel.rendered && AMStudioPluginBase.#isAssetContent(as(previewPanel.getCurrentPreviewContentValueExpression().getValue(), Content))) {
      previewPanel.reloadFrame();
    }
  }

  static #addAssetExtensions(): void {
    const isApplicable: AnyFunction = (model: any): boolean => {
      const content = as(model, Content);
      if (!content) {
        return false;
      }

      const path = content.getPath();
      if (path === undefined) {
        return undefined;
      }

      if (path) {
        return path.indexOf(AssetConstants.ASSET_LIBRARY_PATH) === 0;
      }

      return false;
    };
    contentTreeRelationRegistry._.addExtension(new AssetTreeRelation(), isApplicable, 500);
    editorContext._.getCollectionViewExtender().addExtension(new AssetCollectionViewExtension(), isApplicable, 500);
  }

  static #removeAssetDoctypesByDefault(): void {
    AssetDoctypeUtil.getAllAssetContentTypeNames().forEach(AMStudioPluginBase.#removeDoctype);
  }

  static #removeDoctype(contentTypeName: string): void {
    AMStudioPluginBase.#addToArrayIfNotAlreadyContained(editorContext._.getContentTypesExcludedFromSearch(), contentTypeName);
    AMStudioPluginBase.#addToArrayIfNotAlreadyContained(editorContext._.getContentTypesExcludedFromSearchResult(), contentTypeName);
  }

  static #addToArrayIfNotAlreadyContained(array: Array<any>, item: any): void {
    if (array.indexOf(item) === -1) {
      array.push(item);
    }
  }

  static #initAMAsset(content: Content): void {
    const original = AMStudioPluginBase.#createRendition(content, AssetConstants.PROPERTY_ASSET_ORIGINAL);
    AMStudioPluginBase.#setShowValue(original);
  }

  static #initAMPictureAsset(content: Content): void {
    AMStudioPluginBase.#initAMAsset(content);
    const web = AMStudioPluginBase.#createRendition(content, AssetConstants.PROPERTY_ASSET_WEB);
    const print = AMStudioPluginBase.#createRendition(content, AssetConstants.PROPERTY_ASSET_PRINT);
    AMStudioPluginBase.#setShowValue(web);
    AMStudioPluginBase.#setShowValue(print);
  }

  static #initAMVideoAsset(content: Content): void {
    AMStudioPluginBase.#initAMAsset(content);
    const web = AMStudioPluginBase.#createRendition(content, AssetConstants.PROPERTY_ASSET_WEB);
    AMStudioPluginBase.#setShowValue(web);
  }

  static #createRendition(content: Content, rendition: string): Struct {
    const metadata: Struct = content.getProperties().get(AssetConstants.PROPERTY_ASSET_METADATA);
    metadata.getType().addStructProperty(AssetConstants.PROPERTY_ASSET_METADATA_RENDITIONS);
    const renditions: Struct = metadata.get(AssetConstants.PROPERTY_ASSET_METADATA_RENDITIONS);
    renditions.getType().addStructProperty(rendition);
    return renditions.get(rendition);
  }

  static #setShowValue(rendition: Struct): void {
    rendition.set(AMStudioPluginBase.#SHOW_PROPERTY, AMStudioPluginBase.#DEFAULT_SHOW_VALUE);
  }

  static #isAssetContent(content: Content): boolean {
    if (!content) {
      return false;
    }
    return content.getType().isSubtypeOf(AssetDoctypeUtil.getAssetContentType());
  }

  static mayCreate(selection: Content, contentType: ContentType): boolean {
    if (!selection.getPath()) {
      return undefined;
    }

    return selection.getPath().indexOf(AssetConstants.ASSET_LIBRARY_PATH) === 0;
  }

  /**
   * Custom search handler for assets.
   * The collection manager must trigger the search under the Assets folder.
   */
  static openAssetSearch(linkListTargetType: ContentType, sourceContent: Content): void {
    let searchType = linkListTargetType.getName();

    //default supertype to all documents
    if (searchType === "CMLinkable") {
      searchType = ContentTypeNames.DOCUMENT;
    }

    const contentRepository = editorContext._.getSession().getConnection().getContentRepository();
    contentRepository.getChild(AssetConstants.ASSET_LIBRARY_PATH, (assetsRootFolder: Content): void => {
      if (assetsRootFolder) {
        const collectionViewModel = cast(EditorContextImpl, editorContext._).getCollectionViewModel();
        const state = new SearchState();
        state.contentType = searchType;
        state.folder = assetsRootFolder;
        collectionViewModel.setSearchState(true, CollectionViewConstants.LIST_VIEW, state, assetsRootFolder);
        editorContext._.getCollectionViewManager().openSearch(state, false, CollectionViewConstants.LIST_VIEW);
      }
    });
  }
}

export default AMStudioPluginBase;
