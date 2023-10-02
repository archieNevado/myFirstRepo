import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentProxyHelper from "@coremedia/studio-client.cap-rest-client/content/ContentProxyHelper";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import { as, mixin } from "@jangaroo/runtime";
import CatalogHelper from "../helper/CatalogHelper";
import IAugmentationService from "./IAugmentationService";

class AugmentationServiceImpl implements IAugmentationService {

  getContent(catalogObject: CatalogObject): Content {
    const content = ContentProxyHelper.getContent(catalogObject);
    //load the content already so that Studio knows more about the augmenting content.
    if (content) {
      content.load();
    }
    return content;
  }

  getCatalogObject(content: Content): CatalogObject {
    if (!content) {
      return null;
    }
    const properties = content.getProperties();
    if (properties === undefined) {
      return undefined;
    }
    const externalId: string = properties.get(CatalogObjectPropertyNames.EXTERNAL_ID);
    if (externalId === undefined) {
      return undefined;
    }
    // if external id is either null or an empty string we don't have a corresponding catalog object
    if (!externalId) {
      return null;
    }
    const catalogObject = CatalogHelper.getInstance().getCatalogObject(externalId, ValueExpressionFactory.createFromValue(content));
    if (catalogObject === undefined) {
      return undefined;
    }

    return catalogObject;
  }

  getCategory(content: Content): Category {
    const catalogObject = this.getCatalogObject(content);
    if (catalogObject === undefined) {
      return undefined;
    }
    return as(catalogObject, Category);
  }

  getProduct(content: Content): Product {
    const catalogObject = this.getCatalogObject(content);
    if (catalogObject === undefined) {
      return undefined;
    }
    return as(catalogObject, Product);
  }
}
mixin(AugmentationServiceImpl, IAugmentationService);

export default AugmentationServiceImpl;
