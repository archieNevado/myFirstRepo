import Catalog from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Catalog";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Validators_properties
  from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CopyResourceBundleProperties
  from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import CollectionViewModel
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import HandleEmptySearchResultPlugin
  from "@coremedia/studio-client.main.editor-components/sdk/collectionview/HandleEmptySearchResultPlugin";
import StudioPreferenceWindow from "@coremedia/studio-client.main.editor-components/sdk/desktop/StudioPreferenceWindow";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ECommerceStudioPluginBase from "./ECommerceStudioPluginBase";
import ECommerceStudioPlugin_properties from "./ECommerceStudioPlugin_properties";
import CatalogPreferenceWindowPlugin from "./components/preferences/CatalogPreferenceWindowPlugin";
import CatalogSearchList from "./components/search/CatalogSearchList";
import CatalogSearchThumbnails from "./components/search/CatalogSearchThumbnails";
import ECommerceLibraryPlugin from "./library/ECommerceLibraryPlugin";

interface ECommerceStudioPluginConfig extends Config<ECommerceStudioPluginBase> {
}

/* Extend the standard Studio and Blueprint components for Live Context */
class ECommerceStudioPlugin extends ECommerceStudioPluginBase {
  declare Config: ECommerceStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin";

  /**
   * The itemId of the open in tab button.
   */
  static readonly OPEN_IN_TAB_BUTTON_ITEM_ID: string = "openInTab";

  /**
   * The itemId of the create category button.
   */
  static readonly CREATE_CATEGORY_BUTTON_ITEM_ID: string = "createCategory";

  /**
   * The itemId of the create product button.
   */
  static readonly CREATE_PRODUCT_BUTTON_ITEM_ID: string = "createProduct";

  /**
   * The itemId of the remove button.
   */
  static readonly REMOVE_LINK_BUTTON_ITEM_ID: string = "removeLinkButton";

  /**
   * The itemId of the remove link menu item.
   */
  static readonly REMOVE_LINK_MENU_ITEM_ID: string = "removeLink";

  /**
   * The itemId of the open menu item.
   */
  static readonly OPEN_MENU_ITEM_ID: string = "open";

  /**
   * The itemId of the open in tab menu item.
   */
  static readonly OPEN_IN_TAB_MENU_ITEM_ID: string = "openInTab";

  /**
   * The itemId of the copy menu item.
   */
  static readonly COPY_TO_CLIPBOARD_ITEM_ID: string = "copyToClipboard";

  constructor(config: Config<ECommerceStudioPlugin> = null) {
    super((()=> ConfigUtils.apply(Config(ECommerceStudioPlugin, {

      rules: [

        Config(CollectionView, {
          plugins: [
            Config(ECommerceLibraryPlugin),
          ],
        }),
        Config(CatalogSearchList, {
          plugins: [
            Config(HandleEmptySearchResultPlugin, { additionalHandler: ECommerceStudioPlugin.#handleStoreOrCategory }),
          ],
        }),
        Config(CatalogSearchThumbnails, {
          plugins: [
            Config(HandleEmptySearchResultPlugin, { additionalHandler: ECommerceStudioPlugin.#handleStoreOrCategory }),
          ],
        }),

        Config(StudioPreferenceWindow, {
          plugins: [
            Config(CatalogPreferenceWindowPlugin),
          ],
        }),

      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Editor_properties),
          source: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
        }),
      ],

    }), config))());
  }

  static #handleStoreOrCategory(mainStateBean: Bean): string {
    const searchType: string = mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY);
    if (searchType !== CatalogModel.TYPE_MARKETING_SPOT) {
      const searchFolder: any = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);
      let store: Store;
      let catalog: Catalog;
      if (is(searchFolder, Store)) {
        store = as(searchFolder, Store);
        catalog = store.getDefaultCatalog();
      } else if (is(searchFolder, Category)) {
        const category = as(searchFolder, Category);
        store = category.getStore();
        catalog = category.getCatalog();
      }

      if (store && store.isMultiCatalog()) {
        return StringUtil.format(ECommerceStudioPlugin_properties.CatalogView_multiCatalog_emptySearch_text, catalog.getName());
      }
    }
    return null;
  }
}

export default ECommerceStudioPlugin;
