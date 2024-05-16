import session from "@coremedia/studio-client.cap-rest-client/common/session";
import ContentRepository from "@coremedia/studio-client.cap-rest-client/content/ContentRepository";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import AssetConstants from "./AssetConstants";

/**
 * Utility class for easy access to all asset doctypes.
 */
class AssetDoctypeUtil {
  static #amAssetContentType: ContentType = null;

  static #allAssetContentTypes: Array<ContentType> = null;

  static #allAssetContentTypeNames: Array<string> = null;

  static getAssetContentType(): ContentType {
    if (!AssetDoctypeUtil.#amAssetContentType) {
      AssetDoctypeUtil.#amAssetContentType = AssetDoctypeUtil.#getRepository().getContentType(AssetConstants.DOCTYPE_ASSET);
    }
    return AssetDoctypeUtil.#amAssetContentType;
  }

  static getAllAssetContentTypes(): Array<any> {
    if (!AssetDoctypeUtil.#allAssetContentTypes) {
      AssetDoctypeUtil.#allAssetContentTypes = AssetDoctypeUtil.#getRepository().getContentTypes().filter(
        (contentType: ContentType): boolean =>
          contentType.isSubtypeOf(AssetDoctypeUtil.getAssetContentType()),
      );
    }
    return AssetDoctypeUtil.#allAssetContentTypes;
  }

  static getAllAssetContentTypeNames(): Array<any> {
    if (!AssetDoctypeUtil.#allAssetContentTypeNames) {
      AssetDoctypeUtil.#allAssetContentTypeNames = AssetDoctypeUtil.getAllAssetContentTypes().map(
        (contentType: ContentType): string =>
          contentType.getName(),
      );
    }
    return AssetDoctypeUtil.#allAssetContentTypeNames;
  }

  static #getRepository(): ContentRepository {
    return session._.getConnection().getContentRepository();
  }
}

export default AssetDoctypeUtil;
