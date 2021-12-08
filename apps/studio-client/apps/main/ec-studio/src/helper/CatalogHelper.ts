import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Marketing from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Marketing";
import MarketingSpot from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/MarketingSpot";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductAttribute from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductAttribute";
import ProductVariant from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductVariant";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import RemoteErrorHandlerRegistryImpl from "@coremedia/studio-client.client-core-impl/data/impl/RemoteErrorHandlerRegistryImpl";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import NotExistsError from "@coremedia/studio-client.client-core/data/error/NotExistsError";
import RemoteError from "@coremedia/studio-client.client-core/data/error/RemoteError";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import CollectionViewManagerInternal from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewManagerInternal";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, cast, is } from "@jangaroo/runtime";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import StoreUtil from "./StoreUtil";

class CatalogHelper {

  static #instance: CatalogHelper = null;

  static readonly #PROFILE_EXTENSIONS: string = "profileExtensions";

  static readonly #PROPERTIES: string = "properties";

  static readonly #LOCAL_SETTINGS_STRUCT_NAME: string = "localSettings";

  static readonly #COMMERCE_STRUCT_NAME: string = "commerce";

  static readonly REFERENCES_LIST_NAME: string = "references";

  static readonly ORIGIN_REFERENCES_LIST_NAME: string = "originReferences";

  static readonly #CATEGORY_TOKEN: string = "/category/";

  static readonly #PRODUCT_TOKEN: string = "/product/";

  static readonly #SKU_TOKEN: string = "/sku/";

  static readonly #CATALOG_REGEX: RegExp = /(?:catalog:(\w+);(.*))/;

  static readonly #TYPE_CATEGORY: string = CatalogModel.TYPE_CATEGORY;

  static readonly #TYPE_MARKETING: string = CatalogModel.TYPE_MARKETING;

  static readonly TYPE_PRODUCT: string = CatalogModel.TYPE_PRODUCT;

  static readonly TYPE_PRODUCT_VARIANT: string = CatalogModel.TYPE_PRODUCT_VARIANT;

  static readonly TYPE_MARKETING_SPOT: string = CatalogModel.TYPE_MARKETING_SPOT;

  static readonly CONTENT_TYPE_CM_CATEGORY: string = "CMCategory";

  static readonly CONTENT_TYPE_CM_PRODUCT: string = "CMProduct";

  static readonly CONTENT_TYPE_CM_ABSTRACT_CATEGORY: string = "CMAbstractCategory";

  static readonly CONTENT_TYPE_CM_EXTERNAL_CHANNEL: string = "CMExternalChannel";

  static readonly CONTENT_TYPE_CM_EXTERNAL_PRODUCT: string = "CMExternalProduct";

  static readonly #PREFERENCES_COMMERCE_STRUCT: string = "commerce";

  // lc rest error, see CatalogRestErrorCodes.java
  static readonly LC_ERROR_CODE_CATALOG_ITEM_UNAVAILABLE = "LC-01000";

  static readonly LC_ERROR_CODE_CONNECTION_UNAVAILABLE = "LC-01001";

  static readonly LC_ERROR_CODE_CATALOG_INTERNAL_ERROR = "LC-01002";

  static readonly LC_ERROR_CODE_UNAUTHORIZED = "LC-01003";

  static readonly LC_ERROR_CODE_CATALOG_UNAVAILABLE = "LC-01004";

  static readonly COULD_NOT_FIND_STORE_BEAN = "LC-01006";

  #storeExpression: ValueExpression = null;

  static #static = (() =>{
    RemoteErrorHandlerRegistryImpl
      .initRemoteErrorHandlerRegistry()
      .registerErrorHandler(CatalogHelper.#remoteErrorHandler);
  })();

  static getInstance(): CatalogHelper {
    if (!CatalogHelper.#instance) {
      CatalogHelper.#instance = new CatalogHelper();
    }
    return CatalogHelper.#instance;
  }

  openCatalog(): void {
    const store = cast(Store, CatalogHelper.getInstance().getActiveStoreExpression().getValue());
    if (store) {
      store.load((): void =>
        store.getRootCategory().load((): void => {
          let selectedNode = CatalogHelper.#getCollectionViewModel().getMainStateBean().get(CollectionViewModel.FOLDER_PROPERTY);
          //if already a category is selected we don't have to change anything.
          if (!is(selectedNode, Category)) {
            selectedNode = store.getRootCategory();
          }
          const model = cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).getCollectionView().getCollectionViewModel();
          model.setMode(CollectionViewModel.REPOSITORY_MODE);

          cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).openWithAllState();
          cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).getCollectionView().showInRepositoryMode(selectedNode);
        }),
      );
    }
  }

  openMarketingSpots(): void {
    const store = cast(Store, CatalogHelper.getInstance().getActiveStoreExpression().getValue());
    if (store) {
      store.load((): void =>
        store.getMarketing().load((): void => {
          const model = cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).getCollectionView().getCollectionViewModel();
          model.setMode(CollectionViewModel.REPOSITORY_MODE);

          const selectedNode: any = store.getMarketing();
          cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).openWithAllState();
          cast(CollectionViewManagerInternal, editorContext._.getCollectionViewManager()).getCollectionView().showInRepositoryMode(selectedNode);
        }),
      );
    }
  }

  getImageUrl(catalogObject: CatalogObject): string {
    if (is(catalogObject, Product)) {
      return cast(Product, catalogObject).getThumbnailUrl();
    } else if (is(catalogObject, Category)) {
      return cast(Category, catalogObject).getThumbnailUrl();
    }

    return null;
  }

  getType(catalogObject: CatalogObject): string {
    let beanType: string;
    if (is(catalogObject, Category)) {
      beanType = CatalogHelper.#TYPE_CATEGORY;
    } else if (is(catalogObject, ProductVariant)) {
      beanType = CatalogHelper.TYPE_PRODUCT_VARIANT;
    } else if (is(catalogObject, Product)) {
      beanType = CatalogHelper.TYPE_PRODUCT;
    } else if (is(catalogObject, MarketingSpot)) {
      beanType = CatalogHelper.TYPE_MARKETING_SPOT;
    } else if (is(catalogObject, Marketing)) {
      beanType = CatalogHelper.#TYPE_MARKETING;
    } else {
      beanType = "UnknownType";
    }

    return beanType;
  }

  getExternalIdFromId(id: string): string {
    //External ids of category can contain '/'. See CMS-5075
    const token = this.getToken(id);
    let candidate: string;
    if (token) {
      candidate = id.substr(id.lastIndexOf(token) + token.length);
    } else {
      //we assume that the substring after the last '/' is the external id
      candidate = id.substr(id.lastIndexOf("/") + 1);
    }
    return CatalogHelper.stripCatalogFromExternalId(candidate);
  }

  static stripCatalogFromExternalId(candidate: string): string {
    // the candidate may include the catalog alias
    const matches = CatalogHelper.#CATALOG_REGEX.exec(candidate);
    return matches && matches.length === 3 ? matches[2] : candidate;
  }

  #encodeForUri(externalId: string): string {
    // First all chars in externalId are encoded.
    // After that, translate back encoded slashes ("%2F") to "/" because by default the tomcat container
    // do not allow encoded slashes for security reasons (see org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH).
    return encodeURIComponent(externalId).replace(/%2F/g, "/");
  }

  getCatalogAliasFromId(id: string): string {
    const groups = CatalogHelper.#CATALOG_REGEX.exec(id);
    if (groups && groups.length > 1) {
      return groups[1];
    }
    // the default catalog alias is 'catalog'
    return "catalog";
  }

  getToken(id: string): string {
    let token: string;
    if (this.#isCategoryId(id)) {
      return CatalogHelper.#CATEGORY_TOKEN;
    } else if (this.#isProductId(id)) {
      return CatalogHelper.#PRODUCT_TOKEN;
    } else if (this.#isSkuId(id)) {
      return CatalogHelper.#SKU_TOKEN;
    }
  }

  isSubType(catalogObject: CatalogObject, catalogObjectType: string): boolean {
    if (is(catalogObject, Category)) {
      return catalogObjectType === CatalogHelper.#TYPE_CATEGORY;
    } else if (is(catalogObject, ProductVariant)) {
      return catalogObjectType === CatalogHelper.TYPE_PRODUCT || catalogObjectType === CatalogHelper.TYPE_PRODUCT_VARIANT;
    } else if (is(catalogObject, Product)) {
      return catalogObjectType === CatalogHelper.TYPE_PRODUCT;
    } else if (is(catalogObject, MarketingSpot)) {
      return catalogObjectType === CatalogHelper.TYPE_MARKETING_SPOT;
    } else {
      return false;
    }
  }

  /**
   * Get the catalog object for the given catalog object id.
   * If the content is specified the store of the content will be used
   * Otherwise the active store will be used.
   * @param catalogObjectId
   * @param contentExpression
   */
  getCatalogObject(catalogObjectId: string, contentExpression?: ValueExpression): CatalogObject {

    const storeValue: any = contentExpression ?
      this.getStoreForContentExpression(contentExpression).getValue() :
      this.getActiveStoreExpression().getValue();
    if (storeValue === undefined) {
      return undefined;
    }
    if (!storeValue) {
      return null;
    }
    const store = as(storeValue, Store);

    const siteId = store.getSiteId();
    if (siteId === undefined) {
      return undefined;
    }
    if (!siteId) {
      return null;
    }

    //siteId and externalId are free text properties and therefor
    //must be uri encoded. see #encodeForUri for more details.
    const encodedSiteId = this.#encodeForUri(siteId);
    const endocedExternalId = this.#encodeForUri(this.getExternalIdFromId(catalogObjectId));
    const catalogAlias = this.getCatalogAliasFromId(catalogObjectId);
    let uriPath: string;
    if (this.#isCategoryId(catalogObjectId)) {
      uriPath = "livecontext/category/" + encodedSiteId + "/" + catalogAlias + "/" + endocedExternalId;
    } else if (this.#isSkuId(catalogObjectId)) {
      uriPath = "livecontext/sku/" + encodedSiteId + "/" + catalogAlias + "/" + endocedExternalId;
    } else if (this.#isProductId(catalogObjectId)) {
      uriPath = "livecontext/product/" + encodedSiteId + "/" + catalogAlias + "/" + endocedExternalId;
    } else if (this.#isSegmentId(catalogObjectId)) {
      uriPath = "livecontext/segment/" + encodedSiteId + "/" + endocedExternalId;
    } else if (CatalogHelper.isMarketingSpot(catalogObjectId)) {
      uriPath = "livecontext/marketingspot/" + encodedSiteId + "/" + endocedExternalId;
    } else if (CatalogHelper.isMarketing(catalogObjectId)) {
      uriPath = "livecontext/marketing/" + encodedSiteId + "/";
    } else if (CatalogHelper.isFacets(catalogObjectId)) {
      uriPath = "livecontext/facets/" + encodedSiteId + "/" + catalogAlias + "/" + endocedExternalId;
    }

    if (uriPath) {
      return cast(CatalogObject, beanFactory._.getRemoteBean(uriPath));
    }
  }

  /**
   *
   * @param bindTo value expression pointing to a document of which 'externalId' property as the id of a catalog object
   */
  getCatalogExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): any => {
      const catalogObjectId: string = bindTo.extendBy("properties").extendBy("externalId").getValue();
      if (catalogObjectId === undefined) {
        return undefined;
      }
      if (!catalogObjectId || catalogObjectId.length === 0) {
        return null;
      } else {
        return this.getCatalogObject(catalogObjectId, bindTo);
      }
    });
  }

  /**
   *
   * @param bindTo value expression pointing to a document of which 'externalId' property as the product id
   * @param productPropertyName
   */
  getProductPropertyExpression(bindTo: ValueExpression, productPropertyName: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): any => {
      let product: Product;
      const catalogObjectId: string = bindTo.extendBy("properties").extendBy("externalId").getValue();
      if (!catalogObjectId || catalogObjectId.length === 0) {
        return null;
      } else {
        product = as(this.getCatalogObject(catalogObjectId, bindTo), Product);
        if (product) {
          return product.get(productPropertyName);
        } else {
          return null;
        }
      }
    });
  }

  isStoreId(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf("livecontext/store") >= 0;
  }

  #isSkuId(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf(CatalogHelper.#SKU_TOKEN) !== -1;
  }

  #isProductId(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf(CatalogHelper.#PRODUCT_TOKEN) !== -1;
  }

  #isCategoryId(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf(CatalogHelper.#CATEGORY_TOKEN) !== -1;
  }

  #isSegmentId(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf("//catalog/segment/") !== -1;
  }

  static isMarketingSpot(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf("/marketingspot/") !== -1;
  }

  static isMarketing(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf("/marketing/") !== -1;
  }

  static isFacets(catalogObjectId: string): boolean {
    return catalogObjectId.indexOf("/facets/") !== -1;
  }

  getActiveStoreExpression(): ValueExpression {
    if (this.#storeExpression) {
      return this.#storeExpression;
    }
    this.#storeExpression = ValueExpressionFactory.createFromFunction(StoreUtil.getActiveStore);
    return this.#storeExpression;
  }

  isActiveCoreMediaStore(): boolean {
    const store: Store = this.getActiveStoreExpression().getValue();
    return this.isCoreMediaStore(store);
  }

  belongsToCoreMediaStore(items: Array<any>): boolean {
    if (items.length === 0) {
      return undefined;
    }

    const store: Store = items[0].getStore();
    return this.isCoreMediaStore(store);
  }

  isCoreMediaStore(store: Store): boolean {
    return this.isVendor(store, "coremedia");
  }

  isVendor(store: Store, vendorName: string): boolean {
    return this.#vendor(store, vendorName, true);
  }

  isNotVendor(store: Store, vendorName: string): boolean {
    return this.#vendor(store, vendorName, false);
  }

  #vendor(store: Store, vendorName: string, isBelongsTo: boolean): boolean {
    if (store === undefined) {
      return undefined;
    }
    if (!store) {
      return false;
    }
    return store.getVendorName() && isBelongsTo === (store.getVendorName().toLowerCase() === vendorName.toLowerCase());
  }

  getStoreForContentExpression(contentExpression: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Store => {
      const content: Content = contentExpression.getValue();
      if (content === undefined) {
        return undefined;
      }
      const siteId = editorContext._.getSitesService().getSiteIdFor(content);
      return StoreUtil.getValidatedStore(siteId);
    });
  }

  getStoreForContent(content: Content, callback: AnyFunction): void {
    if (!content) {
      callback.call(null, undefined);
    }
    this.getStoreForContentExpression(ValueExpressionFactory.createFromValue(content)).loadValue(
      (store: Store): void => {
        callback.call(null, store);
      },
    );
  }

  static #remoteErrorHandler(error: RemoteError, source: any): void {
    const catalogObject = as(source, CatalogObject);
    if (catalogObject) {
      const errorCode = error.errorCode;
      const errorMsg = error.message;
      // only process livecontext errors
      if (errorCode === CatalogHelper.LC_ERROR_CODE_CATALOG_ITEM_UNAVAILABLE) {
        CatalogHelper.#doHandleError(error, source);
      } else if (errorCode === CatalogHelper.LC_ERROR_CODE_CONNECTION_UNAVAILABLE) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.commerceConnectionError_title,
          StringUtil.format(ECommerceStudioPlugin_properties.commerceConnectionError_message, errorMsg));
        CatalogHelper.#doHandleError(error, source);
      } else if (errorCode === CatalogHelper.LC_ERROR_CODE_CATALOG_INTERNAL_ERROR) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.commerceCatalogError_title,
          StringUtil.format(ECommerceStudioPlugin_properties.commerceCatalogError_message, errorMsg));
        CatalogHelper.#doHandleError(error, source);
      } else if (errorCode === CatalogHelper.LC_ERROR_CODE_UNAUTHORIZED) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.commerceUnauthorizedError_title,
          StringUtil.format(ECommerceStudioPlugin_properties.commerceUnauthorizedError_message, errorMsg));
        CatalogHelper.#doHandleError(error, source);
      } else if (errorCode === CatalogHelper.LC_ERROR_CODE_CATALOG_UNAVAILABLE) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.commerceCatalogNotFoundError_title,
          StringUtil.format(ECommerceStudioPlugin_properties.commerceCatalogNotFoundError_message, errorMsg));
        CatalogHelper.#doHandleError(error, source);
      } else if (errorCode === CatalogHelper.COULD_NOT_FIND_STORE_BEAN) {
        trace("[WARN]", StringUtil.format(ECommerceStudioPlugin_properties.commerceStoreItemNotFoundError_message, errorMsg));
        CatalogHelper.#doHandleError(error, source);
      }
    }
  }

  static #doHandleError(error: RemoteError, source: any): void {
    // do not call error.setHandled(true) to allow the RemoteBeanImpl to clean up
    // if we would do the library freezes
    trace("[DEBUG]", "Handled commerce error " + error + " raised by " + source);
  }

  getChildren(catalogObject: CatalogObject): Array<any> {
    if (!catalogObject) {
      return [];
    } else if (is(catalogObject, Marketing)) {
      return cast(Marketing, catalogObject).getMarketingSpots();
    } else if (is(catalogObject, Store)) {
      return [];
    } else if (is(catalogObject, Category)) {
      return cast(Category, catalogObject).getChildren();
    } else {
      return [];
    }
  }

  /**
   *
   * @param productVariant
   * @return the defining attributes as comma-separated list. E.g. (Red, XL)
   */
  #getDefiningAttributesString(productVariant: ProductVariant): string {
    let attributesStr: string = undefined;
    const definingAttributes: Array<any> = productVariant.getDefiningAttributes();
    if (definingAttributes) {
      for (const attribute of definingAttributes as ProductAttribute[]) {
        if (!attributesStr) {
          attributesStr = "(";
        } else {
          attributesStr += ", ";
        }
        attributesStr += attribute.value;
      }
      if (attributesStr) {
        attributesStr += ")";
      }
    }
    return attributesStr;
  }

  /**
   *
   * @param catalogObject
   * @return the name and for variants the defining attributes as comma-separated list. E.g. (Red, XL)
   */
  getDecoratedName(catalogObject: CatalogObject): string {
    let name = catalogObject.getName();
    if (is(catalogObject, ProductVariant)) {
      const attributes = this.#getDefiningAttributesString(as(catalogObject, ProductVariant));
      if (attributes) {
        name += " " + attributes;
      }
    }
    return name;
  }

  /**
   *
   * @param catalogObject
   * @return the name and for variants the defining attributes as comma-separated list. E.g. (Red, XL)
   */
  getDisplayName(catalogObject: CatalogObject): string {
    const name: string = is(catalogObject, Category) ? as(catalogObject, Category).getDisplayName() : this.getDecoratedName(catalogObject);
    return name;
  }

  getPriceWithCurrencyExpression(bindTo: ValueExpression, priceProperty: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      let priceWithCurrency: string = undefined;
      const product = as(bindTo.getValue(), Product);
      if (product && product.get(priceProperty)) {
        priceWithCurrency = product.get(priceProperty) + " " + product.getCurrency();
      }
      return priceWithCurrency;
    });

  }

  getIsVariantExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean =>
      is(bindTo.getValue(), ProductVariant),
    );
  }

  getIsNotVariantExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean =>
      !is(bindTo.getValue(), ProductVariant),
    );
  }

  createOrUpdateProductListStructs(bindTo: ValueExpression, product?: Product): Promise<any> {
    return new Promise((resolve: AnyFunction): void => {
      const localSettingsStructExpression = bindTo.extendBy(CatalogHelper.#PROPERTIES, CatalogHelper.#LOCAL_SETTINGS_STRUCT_NAME);
      localSettingsStructExpression.loadValue((): void => {
        const localSettingsStruct: Struct = localSettingsStructExpression.getValue();
        cast(RemoteBean, localSettingsStruct).load((): void => {
          if (!localSettingsStruct.get(CatalogHelper.#COMMERCE_STRUCT_NAME)) {
            localSettingsStruct.getType().addStructProperty(CatalogHelper.#COMMERCE_STRUCT_NAME);
          }
          const commerceStruct: Struct = localSettingsStruct.get(CatalogHelper.#COMMERCE_STRUCT_NAME);
          if (!commerceStruct.get(CatalogHelper.REFERENCES_LIST_NAME)) {
            commerceStruct.getType().addStringListProperty(CatalogHelper.REFERENCES_LIST_NAME, 1000000);
          }
          //avoid duplicates
          if (product && as(commerceStruct.get(CatalogHelper.REFERENCES_LIST_NAME), Array).indexOf(product.getId()) < 0) {
            commerceStruct.addAt(CatalogHelper.REFERENCES_LIST_NAME, -1, product.getId());
          }
          resolve(product);
        });
      });
    });
  }

  static getCatalogObjectsExpression(contentExpression: ValueExpression,
    catalogObjectIdListName: string,
    invalidMessage: string,
    catalogObjectIdsExpression?: ValueExpression): ValueExpression<(Bean | CatalogObject)[]> {

    const idsExpression: ValueExpression = catalogObjectIdsExpression || CatalogHelper.#getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    return ValueExpressionFactory.createFromFunction(() => {
      const catalogObjectIds: string[] = idsExpression.getValue();
      if (!catalogObjectIds) {
        return undefined;
      }
      return catalogObjectIds.map((id): Bean => {
        const externalId = CatalogHelper.getInstance().getExternalIdFromId(id);
        try {
          const catalogObject = as(CatalogHelper.getInstance().getCatalogObject(id, contentExpression), CatalogObject);
          if (catalogObject && catalogObject.getName()) {
            return catalogObject;
          } else {
            // no catalog object or name found : probably wrong catalog object id
            //use local bean to display the id instead
            return beanFactory._.createLocalBean({
              id: id,
              externalId: externalId,
              name: StringUtil.format(invalidMessage, externalId),
            });
          }
        } catch (e) {
          if (is(e, NotExistsError)) {
          // if remote bean could not be loaded (404) local bean shall be displayed
            return beanFactory._.createLocalBean({
              id: id,
              externalId: externalId,
              name: StringUtil.format(invalidMessage, externalId),
            });
          } else throw e;
        }
      })
      //todo: the catalog objects may be null (why?)
        .filter((obj: Bean): boolean => !!obj);
    });
  }

  static addCatalogObject(contentExpression: ValueExpression, catalogObjectIdListName: string,
    catlogObjectId: string, catalogObjectIdsExpression?: ValueExpression): void {
    const idsExpression: ValueExpression = catalogObjectIdsExpression || CatalogHelper.#getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    if (idsExpression.isLoaded()) {
      CatalogHelper.#doAddCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression);
    } else {
      idsExpression.loadValue((): void =>
        CatalogHelper.#doAddCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression),
      );
    }
  }

  static #doAddCatalogObject(contentExpression: ValueExpression, catalogObjectIdListName: string,
    catlogObjectId: string, catalogObjectIdsExpression?: ValueExpression): void {
    const idsExpression: ValueExpression = catalogObjectIdsExpression || CatalogHelper.#getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    const catalogObjectIds: Array<any> = idsExpression.getValue();
    if (!catalogObjectIds) {
      CatalogHelper.#createStructsIfNecessary(contentExpression, catalogObjectIdListName, catalogObjectIdsExpression);
      CatalogHelper.addCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression);
    } else {
      if (catalogObjectIds.indexOf(catlogObjectId) >= 0) return;
      idsExpression.setValue(catalogObjectIds.concat([catlogObjectId]));
    }
  }

  static #createStructsIfNecessary(contentExpression: ValueExpression,
    catalogObjectIdListName: string,
    catalogObjectIdsExpression?: ValueExpression): void {
    if (catalogObjectIdsExpression) return;
    const structProperty: Struct = contentExpression.extendBy(CatalogHelper.#PROPERTIES, CatalogHelper.#PROFILE_EXTENSIONS).getValue();

    let propertiesStruct: Struct = structProperty.get(CatalogHelper.#PROPERTIES);
    if (!propertiesStruct) {
      structProperty.getType().addStructProperty(CatalogHelper.#PROPERTIES);
      propertiesStruct = structProperty.get(CatalogHelper.#PROPERTIES);
    }

    let commerceStruct: Struct = propertiesStruct.get(CatalogHelper.#COMMERCE_STRUCT_NAME);
    if (!commerceStruct) {
      propertiesStruct.getType().addStructProperty(CatalogHelper.#COMMERCE_STRUCT_NAME);
      commerceStruct = propertiesStruct.get(CatalogHelper.#COMMERCE_STRUCT_NAME);
    }

    if (!commerceStruct.get(catalogObjectIdListName)) {
      commerceStruct.getType().addStringListProperty(catalogObjectIdListName, 1000000, []);
    }
  }

  static removeCatalogObject(contentExpression: ValueExpression,
    catalogObjectIdListName: string, catlogObjectId: string,
    catalogObjectIdsExpression?: ValueExpression): void {
    const idsExpression: ValueExpression = catalogObjectIdsExpression || CatalogHelper.#getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    idsExpression.loadValue((catalogObjectIds: Array<any>): void => {
      if (catalogObjectIds) {
        if (catalogObjectIds.indexOf(catlogObjectId) < 0) return;

        const newCatalogObjects = catalogObjectIds.filter((oldCatalogObjectId: string): boolean =>
          oldCatalogObjectId !== catlogObjectId,
        );

        idsExpression.setValue(newCatalogObjects);
      }
    });
  }

  static #getCatalogObjectIdsExpression(contentExpression: ValueExpression, catalogObjectIdListName: string): ValueExpression {
    return contentExpression.extendBy(CatalogHelper.#PROPERTIES, CatalogHelper.#PROFILE_EXTENSIONS, CatalogHelper.#PROPERTIES, CatalogHelper.#COMMERCE_STRUCT_NAME, catalogObjectIdListName);
  }

  static #getCollectionViewModel(): CollectionViewModel {
    return cast(EditorContextImpl, editorContext._).getCollectionViewModel();
  }

}

export default CatalogHelper;
