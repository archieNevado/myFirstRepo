import CMChannelExtension from "@coremedia-blueprint/studio-client.main.blueprint-forms/CMChannelExtension";
import ContentInitializer from "@coremedia-blueprint/studio-client.main.blueprint-forms/util/ContentInitializer";
import Catalog from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Catalog";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import StoreUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/StoreUtil";
import ContentTreeRelation from "@coremedia/studio-client.cap-base-models/content/ContentTreeRelation";
import contentTreeRelationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTreeRelationRegistry";
import itemConverterRegistry from "@coremedia/studio-client.cap-base-models/converter/itemConverterRegistry";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import UrlUtil from "@coremedia/studio-client.client-core/util/UrlUtil";
import ThumbnailResolverFactory from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverFactory";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import TabTooltipEntry from "@coremedia/studio-client.main.editor-components/sdk/desktop/TabTooltipEntry";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import QuickCreate from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreate";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import Component from "@jangaroo/ext-ts/Component";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogTeaserThumbnailResolver from "./CatalogTeaserThumbnailResolver";
import CatalogThumbnailResolver from "./CatalogThumbnailResolver";
import LivecontextStudioPlugin_properties from "./LivecontextStudioPlugin_properties";
import CatalogItemsToToContentConverter from "./converter/CatalogItemsToToContentConverter";
import CategoryFacetsPropertyFieldBase from "./forms/facets/CategoryFacetsPropertyFieldBase";
import LivecontextCollectionViewExtension from "./library/LivecontextCollectionViewExtension";
import LivecontextContentTreeRelation from "./library/LivecontextContentTreeRelation";
import ShowInCatalogTreeHelper from "./library/ShowInCatalogTreeHelper";
import StoreNodeRenderer from "./pbe/StoreNodeRenderer";

interface LivecontextStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class LivecontextStudioPluginBase extends StudioPlugin {
  declare Config: LivecontextStudioPluginBaseConfig;

  static readonly CONTENT_LED_PROPERTY: string = "livecontext.contentLed";

  static readonly MANAGE_NAVIGATION_PROPERTY: string = "livecontext.manageNavigation";

  static readonly EXTERNAL_ID_PROPERTY: string = "externalId";

  static readonly LOCAL_SETTINGS_STRUCT_NAME: string = "localSettings";

  static readonly OFFSET_NAME: string = "offset";

  static readonly MAX_LENGTH_NAME: string = "maxLength";

  static readonly VENDOR_IBM: string = "IBM";

  static readonly VENDOR_CM: string = "coremedia";

  static readonly VENDOR_HYBRIS: string = "SAP Hybris";

  static readonly VENDOR_SFCC: string = "Salesforce";

  static readonly CONTENT_TYPE_MARKETING_SPOT: string = "CMMarketingSpot";

  static readonly CONTENT_TYPE_EXTERNAL_CHANNEL: string = "CMExternalChannel";

  static readonly CONTENT_TYPE_EXTERNAL_PAGE: string = "CMExternalPage";

  static readonly CONTENT_TYPE_IMAGE_MAP: string = "CMImageMap";

  static readonly CONTENT_TYPE_PRODUCT_TEASER: string = "CMProductTeaser";

  static readonly CONTENT_TYPE_EXTERNAL_PRODUCT: string = "CMExternalProduct";

  static readonly CONTENT_TYPE_PRODUCT_LIST: string = "CMProductList";

  constructor(config: Config<LivecontextStudioPluginBase> = null) {
    if (UrlUtil.getHashParam("livecontext") === "false") {
      delete config.rules;
      delete config.configuration;
    }
    super(config);

    itemConverterRegistry._.registerConverter(new CatalogItemsToToContentConverter());
  }

  /** VisibleForTesting */
  static getIsExtensionApplicable(treeRelation: ContentTreeRelation): AnyFunction {
    return (model: any): boolean => {
      const isCmStore = CatalogHelper.getInstance().isActiveCoreMediaStore();
      if (isCmStore === undefined) {
        return undefined;
      }
      if (isCmStore) {
        return false;
      }
      if (is(model, CatalogObject)) {
        return true;
      }
      const content = as(model, Content);
      if (!content) {
        return false;
      }

      const contentType = content.getType();
      if (!contentType) {
        return undefined;
      }
      const contentTypeName = contentType.getName();
      if (!contentTypeName) {
        return undefined;
      }

      return contentTypeName === treeRelation.folderNodeType()
              || contentTypeName === treeRelation.leafNodeType();
    };
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    const lcContentTreeRelation = new LivecontextContentTreeRelation();
    contentTreeRelationRegistry._.addExtension(lcContentTreeRelation, LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));
    const lcCollectionViewExtension = new LivecontextCollectionViewExtension();
    editorContext.getCollectionViewExtender().addExtension(lcCollectionViewExtension, LivecontextStudioPluginBase.getIsExtensionApplicable(lcContentTreeRelation));

    /**
     * Apply image link list preview
     */
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_CHANNEL));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PRODUCT));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PAGE, "pictures"));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(LivecontextStudioPluginBase.CONTENT_TYPE_MARKETING_SPOT));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver("CatalogObject"));
    editorContext.registerThumbnailResolver(new CatalogTeaserThumbnailResolver(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER, "pictures"));

    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_CATEGORY));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING_SPOT));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT_VARIANT));

    /**
     * Register Content initializer
     */
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_MARKETING_SPOT, LivecontextStudioPluginBase.#initMarketingSpot);
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_LIST, LivecontextStudioPluginBase.#initProductList);
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER, LivecontextStudioPluginBase.#initProductTeaser);
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_CHANNEL, LivecontextStudioPluginBase.#initExternalChannel);
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PRODUCT, LivecontextStudioPluginBase.#initExternalProduct);
    editorContext.registerContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PAGE, LivecontextStudioPluginBase.#initExternalPage);

    editorContext["getMetadataNodeRendererRegistry"]().register(new StoreNodeRenderer());

    /**
     * Extend Content initializer
     */
    editorContext.extendContentInitializer(LivecontextStudioPluginBase.CONTENT_TYPE_IMAGE_MAP, LivecontextStudioPluginBase.#extendImageMap);

    /**
     * apply the marketing spot link field to CMMarketingSpot quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(LivecontextStudioPluginBase.CONTENT_TYPE_MARKETING_SPOT, LivecontextStudioPluginBase.EXTERNAL_ID_PROPERTY,
      (data: ProcessingData, properties: any): Component => {
        const config = Config(CatalogLinkPropertyField, properties);
        config.dropAreaHandler = CatalogHelper.getInstance().openMarketingSpots;
        config.maxCardinality = 1;
        config.replaceOnDrop = true;
        config.linkTypeNames = [CatalogModel.TYPE_MARKETING_SPOT];
        config.dropAreaText = LivecontextStudioPlugin_properties.MarketingSpot_Link_empty_text;
        config.hideRemove = true;
        const myCatalogLink = new CatalogLinkPropertyField(config);
        const containerCfg = Config(FieldContainer);
        containerCfg.fieldLabel = properties.label;
        containerCfg.items = [myCatalogLink];
        return ComponentManager.create(containerCfg);
      });

    /**
     * apply the product link field to CMProductTeaser quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER, LivecontextStudioPluginBase.EXTERNAL_ID_PROPERTY,
      (data: ProcessingData, properties: any): Component => {
        const config = Config(CatalogLinkPropertyField, properties);
        config.maxCardinality = 1;
        config.replaceOnDrop = true;
        config.linkTypeNames = [CatalogModel.TYPE_PRODUCT];
        config.dropAreaText = LivecontextStudioPlugin_properties.Product_Link_empty_text;
        config.hideRemove = true;
        const myCatalogLink = new CatalogLinkPropertyField(config);
        const containerCfg = Config(FieldContainer);
        containerCfg.fieldLabel = properties.label;
        containerCfg.items = [myCatalogLink];
        return ComponentManager.create(containerCfg);
      });

    CMChannelExtension.register(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PAGE);

    /**
     * Register tab tooltip handler
     */
    cast(EditorContextImpl, editorContext).registerTabTooltipHandler(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_CHANNEL, LivecontextStudioPluginBase.#computeCatalogToolTip);
    cast(EditorContextImpl, editorContext).registerTabTooltipHandler(LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PRODUCT, LivecontextStudioPluginBase.#computeCatalogToolTip);
    cast(EditorContextImpl, editorContext).registerTabTooltipHandler(LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER, LivecontextStudioPluginBase.#computeCatalogToolTip);

    cast(EditorContextImpl, editorContext).registerTooltipSkipFlagHandler(bind(this, this.#tooltipSkipFlagHandler));

    // Colorful Studio styles
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_YELLOW, [LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_LIST]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_PURPLE, [
      LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PAGE, LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_CHANNEL,
    ]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_TURQUOISE, [
      LivecontextStudioPluginBase.CONTENT_TYPE_PRODUCT_TEASER, LivecontextStudioPluginBase.CONTENT_TYPE_MARKETING_SPOT,
      LivecontextStudioPluginBase.CONTENT_TYPE_EXTERNAL_PRODUCT,
    ]);
  }

  static #computeCatalogToolTip(content: Content): Array<any> {
    //this expression points to the catalog object
    const catalogObjectExpression = CatalogHelper.getInstance().getCatalogExpression(ValueExpressionFactory.createFromValue(content));
    const catalog: Catalog = catalogObjectExpression.extendBy(CatalogObjectPropertyNames.CATALOG).getValue();
    //null means there is no such a thing like catalog
    if (catalog === null) {
      return [];
    }
    if (catalog && catalog.getName()) {
      return [new TabTooltipEntry(CatalogObjectPropertyNames.CATALOG,
        LivecontextStudioPlugin_properties.Commerce_catalog_label, catalog.getName())];
    }
    return undefined;
  }

  /**
   * compute flags for the catalog tooltip entities
   * and collect sites from non-content entities so that
   * they are included in the site-dependent flag computation in EditorContextImpl#computeTooltipSkipFlags
   * @param tooltipSkipFlags
   * @param sites
   * @param entities
   */
  #tooltipSkipFlagHandler(tooltipSkipFlags: Bean, sites: Array<any>, entities: Array<any>): void {
    let skipCatalog = true;
    entities.forEach((entity: any): void => {
      let catalog: Catalog;
      if (is(entity, Content)) {
        const content = cast(Content, entity);
        //is the content type registered for the tooltip?
        const tooltipEntries = cast(EditorContextImpl, editorContext._).computeAdditionalTabTooltipEntries(content);
        if (tooltipEntries && tooltipEntries.length > 0) {
          const catalogObjectExpression = CatalogHelper.getInstance().getCatalogExpression(ValueExpressionFactory.createFromValue(content));
          catalog = catalogObjectExpression.extendBy(CatalogObjectPropertyNames.CATALOG).getValue();
        }
      } else if (is(entity, Product) || is(entity, Category)) {
        const site = editorContext._.getSitesService().getSite(entity.getSiteId());
        site && sites.push(site);
        catalog = entity.get(CatalogObjectPropertyNames.CATALOG);
      }
      if (catalog && !catalog.isDefault()) {
        skipCatalog = false;
      }
    });

    tooltipSkipFlags.set(CatalogObjectPropertyNames.CATALOG, skipCatalog);

  }

  static #initProductTeaser(content: Content): void {
    //don't initialize the teaser title for product teasers
    //they will inherit the teaser title form the linked product
    //setProperty(content, 'teaserTitle', content.getName());
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #initMarketingSpot(content: Content): void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #initProductList(content: Content): void {
    const localSettings: Struct = content.getProperties().get(LivecontextStudioPluginBase.LOCAL_SETTINGS_STRUCT_NAME);
    if (!localSettings.get(CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME)) {
      localSettings.getType().addStructProperty(CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME);
    }

    const productListStruct: Struct = localSettings.get(CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME);
    if (!productListStruct.get(LivecontextStudioPluginBase.OFFSET_NAME)) {
      productListStruct.getType().addIntegerProperty(LivecontextStudioPluginBase.OFFSET_NAME, 1);
    }
    if (!productListStruct.get(LivecontextStudioPluginBase.MAX_LENGTH_NAME)) {
      productListStruct.getType().addIntegerProperty(LivecontextStudioPluginBase.MAX_LENGTH_NAME, 10);
    }

    if (!productListStruct.get(CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME)) {
      productListStruct.getType().addStructProperty(CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
    }
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #initExternalPage(content: Content): void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #initExternalChannel(content: Content): void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #initExternalProduct(content: Content): void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  static #extendImageMap(content: Content): void {
    const localSettings: Struct = content.getProperties().get("localSettings");
    const overlay = localSettings.get("overlay");
    if (overlay) {
      overlay.set("displayDefaultPrice", true);
    }
  }

  //noinspection JSUnusedGlobalSymbols
  getShopExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      const store = cast(Store, CatalogHelper.getInstance().getActiveStoreExpression().getValue());
      return store && store.getName();
    });
  }

  static reloadPreview(previewPanel: PreviewPanel): void {
    if (previewPanel.rendered) {
      previewPanel.reloadFrame();
    }
  }

  static showInCatalogTree(entity: CatalogObject): void {
    new ShowInCatalogTreeHelper([entity]).showInCatalogTree();
  }

  /////////
  // Utility functions extracted from CatalogHelper
  /////////
  /**
   * Checks if the given category (either content or catalog object) is a part of a site
   * configured for the content led scenario (property 'livecontext.contentLed' in the
   * LiveContext settings document for the Site).
   * @param bindTo
   * @return
   */
  static isContentLedValueExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const catalogObjectValueExpression = AugmentationUtil.toCatalogObjectExpression(bindTo);
      const catalogObject = as(catalogObjectValueExpression.getValue(), CatalogObject);

      if (!catalogObject || !catalogObject.getStore()) {
        return undefined;
      }
      const siteId = catalogObject.getStore().getSiteId();
      if (!siteId) {
        return undefined;
      }
      const site = editorContext._.getSitesService().getSite(siteId);

      if (!site || site === undefined) {
        return undefined;
      }
      if (site.getName() === undefined || site.getLocale() === undefined || site.getLocale().getDisplayName() === undefined) {
        return undefined;
      }
      const rootFolder = site.getSiteRootFolder();
      const liveContextSettings = rootFolder.getChild("Options/Settings/LiveContext");
      if (liveContextSettings === undefined) {
        return undefined;
      }
      if (liveContextSettings !== null) {
        const liveContextSettingsProperties = liveContextSettings.getProperties();
        if (liveContextSettingsProperties === undefined) {
          return undefined;
        }
        const liveContextStruct = as(liveContextSettingsProperties.get("settings"), Struct);
        if (liveContextStruct) {
          const structType = liveContextStruct.getType();
          if (structType === undefined) {
            return undefined;
          }
          if (structType.hasProperty(LivecontextStudioPluginBase.CONTENT_LED_PROPERTY)) {
            return liveContextStruct.get(LivecontextStudioPluginBase.CONTENT_LED_PROPERTY);
          }
          if (structType.hasProperty(LivecontextStudioPluginBase.MANAGE_NAVIGATION_PROPERTY)) {
            return liveContextStruct.get(LivecontextStudioPluginBase.MANAGE_NAVIGATION_PROPERTY);
          }
        }
      }
      return false;
    });
  }

  static #mayCreate(selection: Content, vendorName: string, isBelongsTo: boolean): boolean {
    const site = editorContext._.getSitesService().getSiteFor(selection);
    if (!site) {
      return false;
    }
    const store = StoreUtil.getStoreForSite(site);
    return isBelongsTo ? CatalogHelper.getInstance().isVendor(store, vendorName) :
      CatalogHelper.getInstance().isNotVendor(store, vendorName);
  }

  static mayCreateProductList(selection: Content): boolean {
    return LivecontextStudioPluginBase.#mayCreate(selection, LivecontextStudioPluginBase.VENDOR_CM, false);
  }

  static mayCreateProductTeaser(selection: Content): boolean {
    return LivecontextStudioPluginBase.#mayCreate(selection, LivecontextStudioPluginBase.VENDOR_IBM, true) ||
            LivecontextStudioPluginBase.#mayCreate(selection, LivecontextStudioPluginBase.VENDOR_HYBRIS, true) ||
            LivecontextStudioPluginBase.#mayCreate(selection, LivecontextStudioPluginBase.VENDOR_SFCC, true);
  }

  static mayCreateESpot(selection: Content): boolean {
    return LivecontextStudioPluginBase.#mayCreate(selection, LivecontextStudioPluginBase.VENDOR_IBM, true);
  }
}

export default LivecontextStudioPluginBase;
