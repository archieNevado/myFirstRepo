import BlueprintFormsStudioPluginBase from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintFormsStudioPluginBase";
import CMPictureForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMPictureForm";
import CMVideoForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMVideoForm";
import SEOForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/SEOForm";
import TaxonomyStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/TaxonomyStudioPlugin_properties";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import FreshnessColumn from "@coremedia/studio-client.ext.cap-base-components/columns/FreshnessColumn";
import Validators_properties from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import EditedContentToolsMenu from "@coremedia/studio-client.main.control-room-editor-components/EditedContentToolsMenu";
import AddDependingParameterPreviewUrlTransformer from "@coremedia/studio-client.main.editor-components/configuration/AddDependingParameterPreviewUrlTransformer";
import AddParameterPreviewUrlTransformer from "@coremedia/studio-client.main.editor-components/configuration/AddParameterPreviewUrlTransformer";
import ConfigureDocumentTypes from "@coremedia/studio-client.main.editor-components/configuration/ConfigureDocumentTypes";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import ListViewCreationDateColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewCreationDateColumn";
import ListViewNameColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewNameColumn";
import ListViewStatusColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewStatusColumn";
import ListViewTypeIconColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewTypeIconColumn";
import EditorStartup from "@coremedia/studio-client.main.editor-components/sdk/desktop/EditorStartup";
import LicenseNames_properties from "@coremedia/studio-client.main.editor-components/sdk/desktop/LicenseNames_properties";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import AddTabbedDocumentFormsPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import ConfigureListViewPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/ConfigureListViewPlugin";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import AMCollectionViewPlugin from "./AMCollectionViewPlugin";
import AMDocumentTypes_properties from "./AMDocumentTypes_properties";
import AMStudioPluginBase from "./AMStudioPluginBase";
import AMStudioPlugin_properties from "./AMStudioPlugin_properties";
import AMTaxonomyStudioPlugin_properties from "./AMTaxonomyStudioPlugin_properties";
import AMValidators_properties from "./AMValidators_properties";
import AddAssetLinkPlugin from "./AddAssetLinkPlugin";
import AssetCollectionViewExtension from "./AssetCollectionViewExtension";
import AssetConstants from "./AssetConstants";
import ExpirationDateColumn from "./columns/ExpirationDateColumn";
import AMAssetForm from "./forms/AMAssetForm";
import AMDocumentAssetForm from "./forms/AMDocumentAssetForm";
import AMPictureAssetForm from "./forms/AMPictureAssetForm";
import AMTaxonomyForm from "./forms/AMTaxonomyForm";
import AMVideoAssetForm from "./forms/AMVideoAssetForm";
import ShowAssetsInEditedContentsCheckbox from "./forms/ShowAssetsInEditedContentsCheckbox";

interface AMStudioPluginConfig extends Config<AMStudioPluginBase> {
}

class AMStudioPlugin extends AMStudioPluginBase {
  declare Config: AMStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amStudioPlugin";

  constructor(config: Config<AMStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(AMStudioPlugin, {

      rules: [
        Config(EditorStartup, {
          plugins: [
            Config(ConfigureListViewPlugin, {
              instanceName: AssetCollectionViewExtension.INSTANCE_NAME,
              listViewDataFields: [
                Config(DataField, {
                  name: AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE,
                  mapping: "properties." + AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE + ".date",
                }),
              ],

              repositoryListViewColumns: [
                Config(ListViewTypeIconColumn, {
                  width: 75,
                  showTypeName: true,
                  sortable: true,
                  ...{
                    sortField: "type",
                    extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName,
                  },
                }),
                Config(ListViewNameColumn, {
                  sortable: true,
                  ...{
                    defaultSortColumn: true,
                    defaultSortDirection: "asc",
                  },
                }),
                Config(ExpirationDateColumn),
                Config(ListViewCreationDateColumn, {
                  width: 120,
                  sortable: true,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
                Config(FreshnessColumn, {
                  sortable: true,
                  hidden: true,
                }),
                Config(ListViewStatusColumn, {
                  width: 46,
                  sortable: true,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
              ],

              searchListViewColumns: [
                Config(ListViewTypeIconColumn, {
                  width: 120,
                  showTypeName: true,
                  ...{ sortField: "type" },
                  sortable: true,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
                Config(ListViewNameColumn, { sortable: true }),
                Config(ExpirationDateColumn),
                Config(ListViewCreationDateColumn, {
                  sortable: true,
                  width: 120,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
                Config(FreshnessColumn, {
                  sortable: true,
                  hidden: true,
                  ...{
                    defaultSortColumn: true,
                    defaultSortDirection: "desc",
                  },
                }),
                Config(ListViewStatusColumn, {
                  width: 46,
                  sortable: true,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
              ],
            }),
          ],
        }),

        Config(EditedContentToolsMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(ShowAssetsInEditedContentsCheckbox, { itemId: "includeAssetsItemId" }),
              ],
            }),
          ],
        }),

        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(AMAssetForm, { itemId: AssetConstants.DOCTYPE_ASSET }),
                Config(AMPictureAssetForm, { itemId: AssetConstants.DOCTYPE_PICTURE_ASSET }),
                Config(AMVideoAssetForm, { itemId: AssetConstants.DOCTYPE_VIDEO_ASSET }),
                Config(AMDocumentAssetForm, { itemId: AssetConstants.DOCTYPE_DOCUMENT_ASSET }),
                Config(AMTaxonomyForm, { itemId: AssetConstants.DOCTYPE_ASSET_TAXONOMY }),
              ],
            }),
          ],
        }),

        Config(CMPictureForm, {
          plugins: [
            Config(AddAssetLinkPlugin, {
              tabItemId: CMPictureForm.EXTRAS_TAB_ITEM_ID,
              afterItemId: SEOForm.SEO_FORM_ITEM_ID,
              title: AMStudioPlugin_properties.PropertyGroup_asset_label,
            }),
          ],
        }),

        Config(CMVideoForm, {
          plugins: [
            Config(AddAssetLinkPlugin, {
              tabItemId: CMVideoForm.EXTRAS_TAB_ITEM_ID,
              afterItemId: SEOForm.SEO_FORM_ITEM_ID,
              title: AMStudioPlugin_properties.PropertyGroup_asset_label,
            }),
          ],
        }),

        Config(CollectionView, {
          plugins: [
            Config(AMCollectionViewPlugin),
          ],
        }),

        Config(PreviewPanel, {
          plugins: [
            Config(BindPlugin, {
              bindTo: editorContext._.getSitesService().getPreferredSiteIdExpression(),
              boundValueChanged: AMStudioPluginBase.reloadAssetPreview,
            }),
          ],
        }),
      ],

      configuration: [
        new ConfigureDocumentTypes({
          names: AssetConstants.DOCTYPE_ASSET,
          includeSubtypes: true,
          mayCreate: AMStudioPluginBase.mayCreate,
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, LicenseNames_properties),
          source: resourceManager.getResourceBundle(null, AMStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, AMDocumentTypes_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, TaxonomyStudioPlugin_properties),
          source: resourceManager.getResourceBundle(null, AMTaxonomyStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, AMValidators_properties),
        }),

        /* there is no preview available for these DocTypes */
        new ConfigureDocumentTypes({
          names: AssetConstants.DOCTYPE_ASSET_TAXONOMY,
          preview: false,
        }),

        new ConfigureDocumentTypes({
          names: AssetConstants.DOCTYPE_ASSET,
          previewUrlTransformers: [
            new AddParameterPreviewUrlTransformer({
              name: "view",
              value: "fragmentPreview",
            }),
            new AddDependingParameterPreviewUrlTransformer({
              name: "studioPreferredSite",
              valueExpression: editorContext._.getSitesService().getPreferredSiteIdExpression(),
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMStudioPlugin;
