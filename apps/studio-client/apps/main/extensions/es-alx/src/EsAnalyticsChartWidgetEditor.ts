import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import com_coremedia_ui_store_DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ext_data_field_DataField from "@jangaroo/ext-ts/data/field/Field";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChartWidgetEditorBase from "./EsAnalyticsChartWidgetEditorBase";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

interface EsAnalyticsChartWidgetEditorConfig extends Config<EsAnalyticsChartWidgetEditorBase> {
}

class EsAnalyticsChartWidgetEditor extends EsAnalyticsChartWidgetEditorBase {
  declare Config: EsAnalyticsChartWidgetEditorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidgetEditor";

  static readonly CONTENT_TYPE_SELECTOR_ITEM_ID: string = "contentTypeSelector";

  static readonly SEARCH_TEXT_ITEM_ID: string = "searchField";

  static readonly ROOT_CHANNEL_ITEM_ID: string = "rootChannelItemId";

  constructor(config: Config<EsAnalyticsChartWidgetEditor> = null) {
    super((()=> ConfigUtils.apply(Config(EsAnalyticsChartWidgetEditor, {
      ...{ labelAlign: "left" },
      properties: "content",

      items: [
        Config(LocalComboBox, {
          fieldLabel: EsAnalyticsStudioPlugin_properties.widget_combo_root_channel_label,
          itemId: EsAnalyticsChartWidgetEditor.ROOT_CHANNEL_ITEM_ID,
          displayField: "value",
          valueField: "id",
          encodeItems: true,
          ...ConfigUtils.append({
            plugins: [
              Config(BindListPlugin, {
                bindTo: this.getRootChannelValueExpression(),
                sortField: "value",
                fields: [
                  Config(ext_data_field_DataField, { name: "id" }),
                  Config(com_coremedia_ui_store_DataField, {
                    name: "value",
                    mapping: "name",
                    encode: false,
                  }),
                ],
              }),
              Config(BindPropertyPlugin, {
                bidirectional: true,
                componentEvent: "select",
                bindTo: this.getSelectedSiteExpression(),
                reverseTransformer: EsAnalyticsChartWidgetEditorBase.getContentFromId,
                transformer: EsAnalyticsChartWidgetEditorBase.getIdFromContent,
              }),
            ],
          }),
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
      propertyDefaults: { contentType: ContentTypeNames.DOCUMENT },

    }), config))());
  }
}

export default EsAnalyticsChartWidgetEditor;
