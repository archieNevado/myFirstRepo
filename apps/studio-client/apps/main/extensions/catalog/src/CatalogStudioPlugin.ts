import BlueprintFormsStudioPluginBase from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintFormsStudioPluginBase";
import CMPictureForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMPictureForm";
import CMVideoForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMVideoForm";
import CatalogPreferencesBase from "@coremedia-blueprint/studio-client.main.ec-studio/components/preferences/CatalogPreferencesBase";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import FreshnessColumn from "@coremedia/studio-client.ext.cap-base-components/columns/FreshnessColumn";
import Validators_properties from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ConfigureDocumentTypes from "@coremedia/studio-client.main.editor-components/configuration/ConfigureDocumentTypes";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import RegisterLibraryTreeFilter from "@coremedia/studio-client.main.editor-components/configuration/RegisterLibraryTreeFilter";
import Actions_properties from "@coremedia/studio-client.main.editor-components/sdk/actions/Actions_properties";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import ListViewCreationDateColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewCreationDateColumn";
import ListViewNameColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewNameColumn";
import ListViewSiteColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteColumn";
import ListViewSiteLocaleColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteLocaleColumn";
import ListViewStatusColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewStatusColumn";
import ListViewTypeIconColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewTypeIconColumn";
import SearchFilters from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFilters";
import EditorStartup from "@coremedia/studio-client.main.editor-components/sdk/desktop/EditorStartup";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import AddTabbedDocumentFormsPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import ConfigureListViewPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/ConfigureListViewPlugin";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import Component from "@jangaroo/ext-ts/Component";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import CatalogStudioPluginBase from "./CatalogStudioPluginBase";
import CatalogStudioPlugin_properties from "./CatalogStudioPlugin_properties";
import CatalogValidator_properties from "./CatalogValidator_properties";
import ProductCodeColumn from "./ProductCodeColumn";
import ReferrerImageListWrapper from "./ReferrerImageListWrapper";
import CatalogActions_properties from "./actions/CatalogActions_properties";
import LostandfoundFilterPanel from "./collectionview/search/LostandfoundFilterPanel";
import CMCategoryForm from "./forms/CMCategoryForm";
import CMProductForm from "./forms/CMProductForm";
import CatalogLibraryPlugin from "./library/CatalogLibraryPlugin";

interface CatalogStudioPluginConfig extends Config<CatalogStudioPluginBase> {
}

class CatalogStudioPlugin extends CatalogStudioPluginBase {
  declare Config: CatalogStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.catalogStudioPlugin";

  static readonly LOST_AND_FOUND_TYPE: string = "lostAndFound";

  constructor(config: Config<CatalogStudioPlugin> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogStudioPlugin, {

      rules: [
        Config(EditorStartup, {
          plugins: [
            Config(ConfigureListViewPlugin, {
              instanceName: "catalog",
              listViewDataFields: [
                Config(DataField, {
                  name: "productCode",
                  mapping: "properties.productCode",
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
                Config(ProductCodeColumn, {
                  width: 126,
                  sortable: true,
                  ...{ sortField: "productCode" },
                }),
                Config(ListViewCreationDateColumn, {
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
                  width: 75,
                  showTypeName: true,
                  ...{ sortField: "type" },
                  sortable: true,
                  ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
                }),
                Config(ListViewNameColumn, { sortable: true }),
                Config(ProductCodeColumn, {
                  width: 126,
                  sortable: true,
                  ...{ sortField: "productCode" },
                }),
                Config(ListViewSiteColumn, { sortable: false }),
                Config(ListViewSiteLocaleColumn, { sortable: false }),
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

        Config(CMPictureForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(ReferrerImageListWrapper),
              ],
              after: [
                Config(Component, { itemId: CMPictureForm.COPYRIGHT_FORM_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CMVideoForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(ReferrerImageListWrapper),
              ],
              after: [
                Config(Component, { itemId: CMVideoForm.COPYRIGHT_FORM_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(CMProductForm, { itemId: "CMProduct" }),
                Config(CMCategoryForm, { itemId: "CMCategory" }),
              ],
            }),
          ],
        }),

        Config(CollectionView, {
          plugins: [
            Config(CatalogLibraryPlugin),
          ],
        }),

        Config(SearchFilters, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(LostandfoundFilterPanel),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new ConfigureDocumentTypes({
          names: "CMCategory,CMProduct",
          mayCreate: bind(this, this.mayCreate),
        }),

        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, CatalogStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, CatalogValidator_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Actions_properties),
          source: resourceManager.getResourceBundle(null, CatalogActions_properties),
        }),

        new RegisterLibraryTreeFilter({
          path: "Products",
          disabledExpression: ValueExpressionFactory.create(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext._.getPreferences()),
        }),

      ],

    }), config))());
  }
}

export default CatalogStudioPlugin;
