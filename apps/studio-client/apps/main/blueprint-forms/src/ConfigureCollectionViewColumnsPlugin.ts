import FreshnessColumn from "@coremedia/studio-client.ext.cap-base-components/columns/FreshnessColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ListViewCreationDateColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewCreationDateColumn";
import ListViewNameColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewNameColumn";
import ListViewSiteColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteColumn";
import ListViewSiteLocaleColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteLocaleColumn";
import ListViewStatusColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewStatusColumn";
import ListViewTypeIconColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewTypeIconColumn";
import ConfigureListViewPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/ConfigureListViewPlugin";
import SiteUtil from "@coremedia/studio-client.multi-site-models/SiteUtil";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintFormsStudioPluginBase from "./BlueprintFormsStudioPluginBase";

interface ConfigureCollectionViewColumnsPluginConfig extends Config<ConfigureListViewPlugin> {
}

class ConfigureCollectionViewColumnsPlugin extends ConfigureListViewPlugin {
  declare Config: ConfigureCollectionViewColumnsPluginConfig;

  constructor(config: Config<ConfigureCollectionViewColumnsPlugin> = null) {
    super(ConfigUtils.apply(Config(ConfigureCollectionViewColumnsPlugin, {

      listViewDataFields: [
        Config(DataField, {
          name: "siteName",
          mapping: "",
          convert: SiteUtil.getSiteNameFor,
        }),
        Config(DataField, {
          name: "siteLocale",
          mapping: "",
          convert: SiteUtil.getSiteLocaleNameFor,
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
          flex: 1,
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
        Config(ListViewNameColumn, {
          sortable: true,
          flex: 1,
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
          ...{
            defaultSortColumn: true,
            defaultSortDirection: "desc",
          },
          hidden: true,
        }),
        Config(ListViewStatusColumn, {
          width: 46,
          sortable: true,
          ...{ extendOrderBy: BlueprintFormsStudioPluginBase.extendOrderByName },
        }),
      ],

    }), config));
  }
}

export default ConfigureCollectionViewColumnsPlugin;
