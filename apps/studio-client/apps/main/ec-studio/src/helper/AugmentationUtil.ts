import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteErrorHandlerRegistryImpl
  from "@coremedia/studio-client.client-core-impl/data/impl/RemoteErrorHandlerRegistryImpl";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import RemoteError from "@coremedia/studio-client.client-core/data/error/RemoteError";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, is } from "@jangaroo/runtime";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import augmentationService from "../augmentation/augmentationService";
import catalogHelper from "../catalogHelper";

class AugmentationUtil {

  // lc rest error, see CatalogRestErrorCodes.java
  static readonly LC_ERROR_CODE_ROOT_CATEGORY_NOT_AUGMENTED = "LC-01005";

  static #static = (() =>{
    RemoteErrorHandlerRegistryImpl
      .initRemoteErrorHandlerRegistry()
      .registerErrorHandler(AugmentationUtil.#remoteErrorHandler);
  })();

  static #remoteErrorHandler(error: RemoteError, source: any): void {
    const errorCode = error.errorCode;
    const errorMsg = error.message;
    if (errorCode === AugmentationUtil.LC_ERROR_CODE_ROOT_CATEGORY_NOT_AUGMENTED) {
      MessageBoxUtil.showError(ECommerceStudioPlugin_properties.commerceAugmentationError_title,
        StringUtil.format(ECommerceStudioPlugin_properties.commerceAugmentationError_message, errorMsg));
      AugmentationUtil.#doHandleError(error, source);
    }
  }

  static #doHandleError(error: RemoteError, source: any): void {
    // do not call error.setHandled(true) to allow the RemoteBeanImpl to clean up
    // if we would do the library freezes
    trace("[DEBUG]", "Handled augmentation error " + error + " raised by " + source);
  }

  /**
   * Checks if a given category has child categories.
   * @param bindTo The category as content
   * @return
   */
  static hasChildCategoriesExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      if (is(bindTo.getValue(), Content)) {
        const childrenExpression = bindTo.extendBy("properties").extendBy("children");
        const children: Array<any> = childrenExpression.getValue();
        return children && children.length > 0;
      }
      return false;
    });
  }

  /**
   * Converts the given content to a catalog object.
   * @param bindTo
   * @return
   */
  static toCatalogObjectExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): any => {
      const content = as(bindTo.getValue(), Content);
      if (content) {
        return augmentationService.getCategory(content);
      }
      return bindTo.getValue();
    });
  }

  /**
   *
   * @param contentExpression expression pointing to the content augmenting a catalog object
   * @return expression pointing to the catalog object
   */
  static getCatalogObjectExpression(contentExpression: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): RemoteBean => {
      const content: Content = contentExpression.getValue();
      if (content === undefined) {
        return undefined;
      }
      return augmentationService.getCatalogObject(content);
    });
  }

  static getTypeLabel(catalogObject: CatalogObject): string {
    // if the catalog object is an augmented category
    // take the type label of the augmenting content
    const categoryContent = augmentationService.getContent(catalogObject);
    if (categoryContent && categoryContent.getType() && categoryContent.getType().getName()) {
      return ContentLocalizationUtil.localizeDocumentTypeName(categoryContent.getType().getName());
    }
    const catalogType = catalogHelper.getType(catalogObject);
    return ECommerceStudioPlugin_properties[catalogType + "_label"];
  }

  static getIconFunctionWithLink(selectedParentNodeValueExpression: ValueExpression): AnyFunction {
    return (catalogObject: CatalogObject): string => {
      const isVirtual = AugmentationUtil.calculateIfVirtual(catalogObject, selectedParentNodeValueExpression);
      const catalogType = catalogHelper.getType(catalogObject);
      //if a catalog object is augmented show a different icon
      if (augmentationService.getContent(catalogObject)) {
        if (catalogType === CatalogModel.TYPE_CATEGORY) {
          if (isVirtual) {
            const augmentedLinkIcon = CoreIcons_properties.augmented_link;
            return augmentedLinkIcon;
          } else {
            const augmentedCategoryIcon = ECommerceStudioPlugin_properties.AugmentedCategory_icon;
            return augmentedCategoryIcon;
          }
        } else if (catalogType === CatalogModel.TYPE_PRODUCT) {
          const augmentedProductIcon = ECommerceStudioPlugin_properties.AugmentedProduct_icon;
          return augmentedProductIcon;
        }
      }
      if (isVirtual) {
        return CoreIcons_properties.link;
      } else {
        return ECommerceStudioPlugin_properties[catalogType + "_icon"];
      }
    };
  }

  static getTypeCls(catalogObject: CatalogObject): string {
    const catalogType = catalogHelper.getType(catalogObject);
    //if a catalog object is augmented show a different icon
    if (augmentationService.getContent(catalogObject)) {
      if (catalogType === CatalogModel.TYPE_CATEGORY) {
        return ECommerceStudioPlugin_properties.AugmentedCategory_icon;
      } else if (catalogType === CatalogModel.TYPE_PRODUCT) {
        return ECommerceStudioPlugin_properties.AugmentedProduct_icon;
      }
    }
    return ECommerceStudioPlugin_properties[catalogType + "_icon"];
  }

  static calculateIfVirtual(catalogObject: CatalogObject, selectedNodeValueExpression: ValueExpression): boolean {
    const category = as(catalogObject, Category);
    if (!category) {
      return false;
    }

    const categoryParent = as(category.getParent(), Category);
    if (!categoryParent) {
      return false;
    }

    const selectedParent = as(selectedNodeValueExpression.getValue(), Category);
    if (!selectedParent) {
      return false;
    }

    return categoryParent.getId() !== selectedParent.getId();
  }
}

export default AugmentationUtil;
