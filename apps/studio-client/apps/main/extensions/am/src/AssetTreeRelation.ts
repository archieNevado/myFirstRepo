import RepositoryContentTreeRelation from "@coremedia/studio-client.cap-base-models/content/RepositoryContentTreeRelation";
import ContentRepositoryImpl from "@coremedia/studio-client.cap-rest-client-impl/content/impl/ContentRepositoryImpl";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import AssetConstants from "./AssetConstants";

class AssetTreeRelation extends RepositoryContentTreeRelation {

  override mayCreate(folder: Content, contentType: ContentType): boolean {
    const mayCreate = super.mayCreate(folder, contentType);
    if (mayCreate === undefined) {
      return undefined;
    }
    if (!mayCreate) {
      return false;
    }

    return contentType.isSubtypeOf(ContentRepositoryImpl.FOLDER_CONTENT_TYPE) ||
            contentType.isSubtypeOf(AssetConstants.DOCTYPE_ASSET);
  }

  override mayMove(sources: Array<any>, newParent: Content): boolean {
    return super.mayMove(sources, newParent) && AssetTreeRelation.#mayMoveOrCopyToAssetLibrary(sources);
  }

  override mayCopy(sources: Array<any>, newParent: Content): boolean {
    return super.mayCopy(sources, newParent) && AssetTreeRelation.#mayMoveOrCopyToAssetLibrary(sources);
  }

  static #mayMoveOrCopyToAssetLibrary(sources: Array<any>): boolean {
    for (const content of sources as Content[]) {
      if (content.getPath() === undefined) {
        return undefined;
      }

      const path = content.getPath();
      if (path.indexOf(AssetConstants.ASSET_LIBRARY_PATH) !== 0) {
        return false;
      }
    }
    return true;
  }
}

export default AssetTreeRelation;
