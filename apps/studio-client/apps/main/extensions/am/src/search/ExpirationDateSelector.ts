import StatefulDateField from "@coremedia/studio-client.ext.ui-components/components/StatefulDateField";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import ComboBoxAutoWidth from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/ComboBoxAutoWidth";
import Component from "@jangaroo/ext-ts/Component";
import JsonStore from "@jangaroo/ext-ts/data/JsonStore";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import ExpirationDateSelectorBase from "./ExpirationDateSelectorBase";

interface ExpirationDateSelectorConfig extends Config<ExpirationDateSelectorBase>, Partial<Pick<ExpirationDateSelector,
  "availableKeys"
>> {
}

class ExpirationDateSelector extends ExpirationDateSelectorBase {
  declare Config: ExpirationDateSelectorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.expirationDateSelector";

  constructor(config: Config<ExpirationDateSelector> = null) {
    config = ConfigUtils.apply({ availableKeys: ["any", "inOneDay", "inOneWeek", "inTwoWeeks", "inOneMonth", "byDate"] }, config);
    super((()=> ConfigUtils.apply(Config(ExpirationDateSelector, {

      items: [
        Config(ComboBoxAutoWidth, {
          valueField: "id",
          displayField: "name",
          encodeItems: true,
          hideLabel: true,
          store: new JsonStore({
            data: this.comboboxEntryTransformer(config.availableKeys),
            fields: [
              Config(DataField, {
                name: "id",
                encode: false,
              }),
              Config(DataField, {
                name: "name",
                encode: false,
              }),
            ],
          }),
          ...ConfigUtils.append({
            plugins: [
              Config(BindPropertyPlugin, {
                bindTo: config.selectedKeyValueExpression,
                bidirectional: true,
              }),
            ],
          }),
        }),
        Config(Component, {
          height: "6px",
          plugins: [
            Config(BindVisibilityPlugin, {
              bindTo: config.selectedKeyValueExpression,
              transformer: bind(this, this.datefieldVisibilityTransformer),
            }),
          ],
        }),
        Config(StatefulDateField, {
          ariaLabel: Editor_properties.Date_property_field,
          format: AMStudioPlugin_properties.ExpirationDate_dateFormat,
          formatText: "",
          hideLabel: true,
          width: "100%",
          plugins: [
            Config(BindVisibilityPlugin, {
              bindTo: config.selectedKeyValueExpression,
              transformer: bind(this, this.datefieldVisibilityTransformer),
            }),
            Config(BindPropertyPlugin, {
              bindTo: config.selectedDateValueExpression,
              bidirectional: true,
            }),
          ],
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config))());
  }

  /**
   * The list of keys selectable in the combobox.
   * The display values can be localized in the file AMStudioPlugin.properties using the following pattern:
   * 'Filter_ExpirationDate_[key]_text'
   */
  availableKeys: Array<any> = null;
}

export default ExpirationDateSelector;
