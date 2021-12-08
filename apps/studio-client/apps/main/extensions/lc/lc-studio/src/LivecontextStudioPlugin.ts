import CMChannelForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMChannelForm";
import CMImageMapForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMImageMapForm";
import CMTeaserForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMTeaserForm";
import CMVideoForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMVideoForm";
import DataDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/DataDocumentForm";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogPreferencesBase from "@coremedia-blueprint/studio-client.main.ec-studio/components/preferences/CatalogPreferencesBase";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ContentActions_properties from "@coremedia/studio-client.ext.cap-base-components/actions/ContentActions_properties";
import Validators_properties from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import PreviewDateSelector from "@coremedia/studio-client.main.bpbase-studio-components/previewdate/PreviewDateSelector";
import ConfigureDocumentTypes from "@coremedia/studio-client.main.editor-components/configuration/ConfigureDocumentTypes";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import RegisterLibraryTreeFilter from "@coremedia/studio-client.main.editor-components/configuration/RegisterLibraryTreeFilter";
import Actions_properties from "@coremedia/studio-client.main.editor-components/sdk/actions/Actions_properties";
import BreadcrumbElement from "@coremedia/studio-client.main.editor-components/sdk/components/breadcrumb/BreadcrumbElement";
import ActionsToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ActionsToolbar";
import ComponentBasedEntityWorkAreaTabType from "@coremedia/studio-client.main.editor-components/sdk/desktop/ComponentBasedEntityWorkAreaTabType";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import WorkAreaTabTypesPlugin from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkAreaTabTypesPlugin";
import WorkAreaTabProxiesContextMenu from "@coremedia/studio-client.main.editor-components/sdk/desktop/reusability/WorkAreaTabProxiesContextMenu";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import AddTabbedDocumentFormsPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import PreviewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewContextMenu";
import QuickCreateSettings_properties from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateSettings_properties";
import Component from "@jangaroo/ext-ts/Component";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ExternalPageQuickCreate_properties from "./ExternalPageQuickCreate_properties";
import LiveContextStudioPluginValidator_properties from "./LiveContextStudioPluginValidator_properties";
import LivecontextStudioPluginBase from "./LivecontextStudioPluginBase";
import LivecontextStudioPlugin_properties from "./LivecontextStudioPlugin_properties";
import AugmentCategoryAction from "./action/AugmentCategoryAction";
import AugmentProductAction from "./action/AugmentProductAction";
import ViewSettingsRadioGroup from "./components/product/ViewSettingsRadioGroup";
import CommerceCategoryWorkAreaTab from "./desktop/CommerceCategoryWorkAreaTab";
import CommerceProductWorkAreaTab from "./desktop/CommerceProductWorkAreaTab";
import CMExternalChannelForm from "./forms/CMExternalChannelForm";
import CMExternalPageForm from "./forms/CMExternalPageForm";
import CMExternalProductForm from "./forms/CMExternalProductForm";
import CMMarketingSpotForm from "./forms/CMMarketingSpotForm";
import CMProductListForm from "./forms/CMProductListForm";
import CMProductTeaserForm from "./forms/CMProductTeaserForm";
import ProductAssignmentField from "./forms/ProductAssignmentField";
import LivecontextCollectionViewActionsPlugin from "./library/LivecontextCollectionViewActionsPlugin";
import DisableStoreNodePlugin from "./pbe/DisableStoreNodePlugin";
import AddCatalogObjectActionsToPreviewContextMenu from "./plugins/AddCatalogObjectActionsToPreviewContextMenu";
import AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu from "./plugins/AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu";
import AddTimeZoneValidationPlugin from "./plugins/AddTimeZoneValidationPlugin";

interface LivecontextStudioPluginConfig extends Config<LivecontextStudioPluginBase> {
}

/* Extend the standard Studio and Blueprint components for Live Context */
class LivecontextStudioPlugin extends LivecontextStudioPluginBase {
  declare Config: LivecontextStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.studio.config.livecontextStudioPlugin";

  /**
   * The itemId of the open in library menu item.
   */
  static readonly SHOW_CONTENT_OF_ACTIVE_TAB_IN_LIBRARY_MENU_ITEM_ID: string = "showInLibraryMenuItem";

  /**
   * The itemId of the search product variants menu item.
   */
  static readonly SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID: string = "searchProductVariants";

  /**
   * The itemId of the create product teaser  menu item.
   */
  static readonly CREATE_PRODUCT_TEASER_MENU_ITEM_ID: string = "createProductTeaser";

  /**
   * The itemId of the create e-marketing spot  menu item.
   */
  static readonly CREATE_MARKETING_SPOT_MENU_ITEM_ID: string = "createMarketingSpot";

  /**
   * The itemId of the create augmented category menu item.
   */
  static readonly AUGMENT_CATEGORY_MENU_ITEM_ID: string = "augmentCategory";

  /**
   * The itemId of the create augmented product menu item.
   */
  static readonly AUGMENT_PRODUCT_MENU_ITEM_ID: string = "augmentProduct";

  /**
   * The itemId of the search product variants button item.
   */
  static readonly SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID: string = "searchProductVariants";

  /**
   * The itemId of the create augmented category button
   */
  static readonly AUGMENT_CATEGORY_BUTTON_ITEM_ID: string = "augmentCategory";

  /**
   * The itemId of the create augmented product button
   */
  static readonly AUGMENT_PRODUCT_BUTTON_ITEM_ID: string = "augmentProduct";

  /**
   * The itemId of the create product teaser button
   */
  static readonly CREATE_PRODUCT_TEASER_BUTTON_ITEM_ID: string = "createProductTeaser";

  /**
   * The itemId of the create marketing spot button
   */
  static readonly CREATE_MARKETING_SPOT_BUTTON_ITEM_ID: string = "createMarketingSpot";

  constructor(config: Config<LivecontextStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(LivecontextStudioPlugin, {

      rules: [
        Config(CMTeaserForm, {
          plugins: [
            Config(AddItemsPlugin, {
              applyTo: (cont: Container): Container => {
                const cmp = cont.queryById("validTo");
                return cmp ? cmp.ownerCt : null;
              },
              items: [
                Config(BooleanPropertyField, {
                  propertyName: "localSettings.useTeaserTargetValidity",
                  dontTransformToInteger: true,
                }),
              ],
              after: [
                Config(Component, { itemId: "validTo" }),
              ],
            }),
          ],
        }),
        Config(CMChannelForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 2,
              applyTo: (cont: Container): Container =>
                as(cast(Container, cont.queryById("system")).queryById("linkedSettings"), Container)
              ,
              items: [
                Config(ViewSettingsRadioGroup, {
                  propertyName: "localSettings.shopNow",
                  inheritLabel: LivecontextStudioPlugin_properties.CMChannel_settings_inherit,
                }),
              ],
            }),
          ],
        }),
        Config(CMImageMapForm, {
          plugins: [
            Config(AddItemsPlugin, {
              applyTo: (cont: Container): Container =>
                as(cont.queryById(CMImageMapForm.OVERLAY_CONFIG_ITEMID), Container)
              ,
              items: [
                Config(BooleanPropertyField, {
                  itemId: "displayDefaultPrice",
                  propertyName: "localSettings.overlay.displayDefaultPrice",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
                Config(BooleanPropertyField, {
                  itemId: "displayDiscountedPrice",
                  propertyName: "localSettings.overlay.displayDiscountedPrice",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
                Config(BooleanPropertyField, {
                  itemId: "displayOutOfStockLink",
                  propertyName: "localSettings.overlay.displayOutOfStockLink",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
                Config(BooleanPropertyField, {
                  itemId: "hideOutOfStockProducts",
                  propertyName: "localSettings.overlay.hideOutOfStockProducts",
                  hideLabel: true,
                  dontTransformToInteger: true,
                }),
              ],
            }),
          ],
        }),
        Config(CMVideoForm, {
          plugins: [
            Config(AddItemsPlugin, {
              applyTo: (cont: Container): Container =>
                cast(Container, cont.getComponent(CMVideoForm.CONTENT_TAB_ITEM_ID))
              ,
              after: [
                Config(DataDocumentForm),
              ],
              items: [
                Config(ProductAssignmentField, { itemId: "videoTimeLineForm" }),
              ],
            }),
          ],
        }),
        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(CMMarketingSpotForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT }),
                Config(CMExternalChannelForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL }),
                Config(CMExternalPageForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE }),
                Config(CMProductTeaserForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER }),
                Config(CMExternalProductForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PRODUCT }),
                Config(CMProductListForm, { itemId: LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_LIST }),
              ],
            }),
          ],
        }),

        /* Register product view for the workArea*/
        Config(WorkArea, {
          plugins: [
            Config(WorkAreaTabTypesPlugin, {
              tabTypes: [
                new ComponentBasedEntityWorkAreaTabType({
                  ddGroup: "ContentDD",
                  entityProperty: "object",
                  entityType: Product,
                  tabComponent: Config(CommerceProductWorkAreaTab, { closable: true }),
                }),
                new ComponentBasedEntityWorkAreaTabType({
                  ddGroup: "ContentDD",
                  entityProperty: "object",
                  entityType: Category,
                  tabComponent: Config(CommerceCategoryWorkAreaTab, { closable: true }),
                }),
              ],
            }),
          ],
        }),

        Config(WorkAreaTabProxiesContextMenu, {
          plugins: [
            Config(AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu),
          ],
        }),
        Config(PreviewDateSelector, {
          plugins: [
            Config(AddTimeZoneValidationPlugin),
            Config(AddItemsPlugin, {
              after: [
                Config(Button, { itemId: "reset" }),
              ],
            }),
          ],
        }),
        Config(ActionsToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(IconButton, {
                  itemId: LivecontextStudioPlugin.AUGMENT_CATEGORY_BUTTON_ITEM_ID,
                  disabled: true,
                  baseAction: new AugmentCategoryAction({ catalogObjectExpression: WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION }),
                }),
                Config(IconButton, {
                  itemId: LivecontextStudioPlugin.AUGMENT_PRODUCT_BUTTON_ITEM_ID,
                  disabled: true,
                  baseAction: new AugmentProductAction({ catalogObjectExpression: WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION }),
                }),
              ],
              before: [
                Config(Component, { itemId: ActionsToolbar.WITHDRAW_BUTTON_ITEM_ID }),
              ],
            }),
          ],
        }),
        Config(PreviewContextMenu, {
          plugins: [
            Config(AddCatalogObjectActionsToPreviewContextMenu),
          ],
        }),
        Config(BreadcrumbElement, {
          plugins: [
            Config(DisableStoreNodePlugin),
          ],
        }),
      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Actions_properties),
          source: resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentActions_properties),
          source: resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, QuickCreateSettings_properties),
          source: resourceManager.getResourceBundle(null, ExternalPageQuickCreate_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
          source: resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, LiveContextStudioPluginValidator_properties),
        }),
        new LivecontextCollectionViewActionsPlugin({}),
        /* exclude Augmented Category and Product from the create new documents menu
    it will created from a selected augmented category directly */
        new ConfigureDocumentTypes({
          names: LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL,
          exclude: true,
        }),
        new ConfigureDocumentTypes({
          names: LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PRODUCT,
          exclude: true,
        }),
        new ConfigureDocumentTypes({
          names: LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_LIST,
          includeSubtypes: true,
          mayCreate: LivecontextStudioPluginBase.mayCreateProductList,
        }),
        new ConfigureDocumentTypes({
          names: LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER,
          includeSubtypes: true,
          mayCreate: LivecontextStudioPluginBase.mayCreateProductTeaser,
        }),
        new ConfigureDocumentTypes({
          names: LivecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT,
          includeSubtypes: true,
          mayCreate: LivecontextStudioPluginBase.mayCreateESpot,
        }),
        new RegisterLibraryTreeFilter({
          path: "Augmentation",
          disabledExpression: ValueExpressionFactory.create(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext._.getPreferences()),
        }),
        new ConfigureDocumentTypes({
          names: "CMMarketingSpot",
          preview: false,
        }),
      ],

    }), config));
  }
}

export default LivecontextStudioPlugin;
