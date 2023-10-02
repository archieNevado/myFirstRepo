import RepositoryContentTreeRelation from "@coremedia/studio-client.cap-base-models/content/RepositoryContentTreeRelation";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";

/**
 * Content Tree relation that cares only for augmented categories and products.
 */
class LivecontextContentTreeRelation extends RepositoryContentTreeRelation {

  override folderNodeType(): string {
    return "CMExternalChannel";
  }

  override leafNodeType(): string {
    return "CMExternalProduct";
  }

  override mayCopy(contents: Array<any>, newParent: Content): boolean {
    return false;
  }

  override mayMove(contents: Array<any>, newParent: Content): boolean {
    return false;
  }

  override mayCreate(folder: Content, contentType: ContentType): boolean {
    return false;
  }
}

export default LivecontextContentTreeRelation;
