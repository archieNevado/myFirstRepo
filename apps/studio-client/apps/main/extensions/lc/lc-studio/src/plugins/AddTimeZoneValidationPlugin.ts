import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import PreviewDateSelector from "@coremedia/studio-client.main.bpbase-studio-components/previewdate/PreviewDateSelector";
import DateTimePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyField";
import DateTimePropertyFieldBase from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyFieldBase";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PreviewPanelTimeZoneValidationPlugin from "./PreviewPanelTimeZoneValidationPlugin";

interface AddTimeZoneValidationPluginConfig extends Config<NestedRulesPlugin> {
}

class AddTimeZoneValidationPlugin extends NestedRulesPlugin {
  declare Config: AddTimeZoneValidationPluginConfig;

  #myPreviewDateSelector: PreviewDateSelector = null;

  constructor(config: Config<AddTimeZoneValidationPlugin> = null) {
    super((()=>{
      this.#myPreviewDateSelector = as(config.cmp, PreviewDateSelector);
      return ConfigUtils.apply(Config(AddTimeZoneValidationPlugin, {
        rules: [
          Config(DateTimePropertyField, {
            itemId: PreviewDateSelector.DATE_TIME_FIELD_ITEM_ID,
            plugins: [
              Config(NestedRulesPlugin, {
                rules: [
                  Config(LocalComboBox, {
                    itemId: DateTimePropertyFieldBase.TIME_ZONE_ITEM_ID,
                    plugins: [
                      Config(PreviewPanelTimeZoneValidationPlugin, {
                        model: this.#myPreviewDateSelector.getModel(),
                        previewPanel: this.#myPreviewDateSelector.previewPanel,
                      }),
                    ],
                  }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default AddTimeZoneValidationPlugin;
