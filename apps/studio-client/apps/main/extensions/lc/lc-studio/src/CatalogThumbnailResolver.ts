import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import catalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/catalogHelper";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ThumbnailResolver from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolver";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as, mixin } from "@jangaroo/runtime";

class CatalogThumbnailResolver implements ThumbnailResolver {

  static readonly EXTERNAL_ID_PROPERTY: string = "externalId";

  #docType: string = null;

  constructor(docType: string) {
    this.#docType = docType;
  }

  getContentType(): string {
    return this.#docType;
  }

  getThumbnail(model: any, operations: string = null): any {
    if (as(model, Content)) {
      return this.renderLiveContextPreview(as(model, Content));
    }

    const url = catalogHelper.getImageUrl(as(model, CatalogObject));
    if (url) {
      return url;
    }
    return null;
  }

  /**
   * Since all live context bean use the "externalId" property we can register the same
   * rendering function for all content types.
   * @param content The livecontext content to render.
   * @return The preview url of the catalog object.
   */
  protected renderLiveContextPreview(content: Content): any {
    let blob: string = undefined;
    const contentExpression = ValueExpressionFactory.createFromValue(content);
    const externalIdExpression = contentExpression.extendBy("properties." + CatalogThumbnailResolver.EXTERNAL_ID_PROPERTY);
    catalogHelper.getStoreForContentExpression(contentExpression).loadValue((): void =>{
      const catalogObject = as(catalogHelper.getCatalogObject(externalIdExpression.getValue(), contentExpression), CatalogObject);
      const urlString = catalogHelper.getImageUrl(catalogObject);
      if (urlString) {
        blob = urlString;
      }
    });
    return blob;
  }

  /**
   * Helper for thumbnail property fields.
   * @param bindTo
   * @return
   */
  static imageValueExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => editorContext._.getThumbnailUri(bindTo.getValue(), null, CatalogHelper.getInstance().getType(bindTo.getValue())));
  }
}
mixin(CatalogThumbnailResolver, ThumbnailResolver);

export default CatalogThumbnailResolver;
