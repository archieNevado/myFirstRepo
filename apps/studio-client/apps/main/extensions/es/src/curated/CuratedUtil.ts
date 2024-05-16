import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ModeratedItem from "@coremedia/studio-client.main.es-models/ModeratedItem";
import ModerationImpl from "@coremedia/studio-client.main.es-models/impl/ModerationImpl";
import { as } from "@jangaroo/runtime";

class CuratedUtil {
  /**
   * Create an Article and make a {@link RemoteServiceMethod} request to curate that article with the selected comments.
   * @param content the Content that is being created via the openCreateContentDialog
   */
  static postCreateArticleFromComments(content: Content): void {
    const remoteServiceMethod = new RemoteServiceMethod(as(ModerationImpl.getInstance(), ModerationImpl).getTenantAwareESUriPrefix() + "/curate/comments", "POST");
    const params: any = CuratedUtil.#makeRequestParameters(content);
    remoteServiceMethod.request(params, CuratedUtil.#openOnSuccessfullyCreatedArticle, null);
  }

  /**
   * Creates an image gallery from comments with pictures using a {@link RemoteServiceMethod} request.
   * @param content the Content that is being created via the openCreateContentDialog
   */
  static postCreateGalleryFromComments(content: Content): void {
    const remoteServiceMethod = new RemoteServiceMethod(as(ModerationImpl.getInstance(), ModerationImpl).getTenantAwareESUriPrefix() + "/curate/images", "POST");
    const params: any = CuratedUtil.#makeRequestParameters(content);
    remoteServiceMethod.request(params, CuratedUtil.#openOnSuccessfullyCreatedArticle, null);
  }

  /**
   *
   * @param content the Content that is being created via the openCreateContentDialog.
   * @return a plain JS Object that holds the capId and an array of commentIds that needs to be curated
   */
  static #makeRequestParameters(content: Content): any {
    return {
      capId: content.getId(),
      commentIds: CuratedUtil.#getCommentIds(),
    };
  }

  /**
   * Open the created Article when everything is ok.
   * @param response the RemoteServiceMethodResponse which is provided at the request
   */
  static #openOnSuccessfullyCreatedArticle(response: RemoteServiceMethodResponse): void {
    const id = response.text;
    const content = UndocContentUtil.getContent(id);
    content.invalidate((): void => {
      editorContext._.getContentTabManager().openDocument(content);
      ModerationImpl.getInstance().getArchiveContributionAdministration().invalidateDisplayed();
    });
  }

  /**
   * Get the Comment IDs of each comment in a single string, separated by ";".
   * @return String Get the Comment IDs of each comment in a single string, separated by ";"
   */
  static #getCommentIds(): string {
    const commentArray = ModerationImpl.getInstance().getArchiveContributionAdministration().getSelectedContributionItems();
    let commentIdsAsString = "";

    for (let i: number = 0; i < commentArray.length; i++) {
      const commentId = as(commentArray[i], ModeratedItem).getTargetId();
      commentIdsAsString = commentIdsAsString.concat(commentId + ";");
    }
    return commentIdsAsString;
  }
}

export default CuratedUtil;
