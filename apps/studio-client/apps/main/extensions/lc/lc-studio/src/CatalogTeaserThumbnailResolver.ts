import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ThumbnailResolverImpl from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverImpl";
import ImageLinkListRenderer from "@coremedia/studio-client.ext.content-link-list-components/util/ImageLinkListRenderer";
import { as } from "@jangaroo/runtime";
import CatalogThumbnailResolver from "./CatalogThumbnailResolver";

/**
 * Catalog content thumbnails not necessarily based on blobs but could be
 * external URLs too.
 */
class CatalogTeaserThumbnailResolver extends CatalogThumbnailResolver {

  constructor(docType: string) {
    super(docType);
  }

  override getThumbnail(model: any, operations: string = null): any {
    return this.#renderLiveContextProductTeaserPreview(as(model, Content));
  }

  #renderLiveContextProductTeaserPreview(content: Content): any {
    //manually build the lookup path since we can not access the editorContext which would result in a stackoverflow
    const resolver = new ThumbnailResolverImpl();
    resolver.addMapping("CMProductTeaser", "pictures");
    resolver.addMapping("CMPicture", "data");
    const result = resolver.getThumbnail(content, ImageLinkListRenderer.DEFAULT_CROPPING);
    if (result === undefined) {
      return undefined;
    }

    if (!result) {
      return this.renderLiveContextPreview(content);
    }

    return result;
  }
}

export default CatalogTeaserThumbnailResolver;
