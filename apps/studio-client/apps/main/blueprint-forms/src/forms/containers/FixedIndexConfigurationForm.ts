import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AdvancedFieldContainer from "@coremedia/studio-client.ext.ui-components/components/AdvancedFieldContainer";
import StatefulNumberField from "@coremedia/studio-client.ext.ui-components/components/StatefulNumberField";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Binding from "@coremedia/studio-client.ext.ui-components/plugins/Binding";
import BlockEnterPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BlockEnterPlugin";
import LabelableSkin from "@coremedia/studio-client.ext.ui-components/skins/LabelableSkin";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import FixedIndexConfigurationFormBase from "./FixedIndexConfigurationFormBase";
import FixedIndexViewModel from "./FixedIndexViewModel";

interface FixedIndexConfigurationFormConfig extends Config<FixedIndexConfigurationFormBase>, Partial<Pick<FixedIndexConfigurationForm,
  "propertyName"
>> {
}

/**
 * This is a form panel which combines several form elements to an editor for local settings to configure
 * the fixed index behaviour. A combination of integer field and reset button.
 */
class FixedIndexConfigurationForm extends FixedIndexConfigurationFormBase {
  declare Config: FixedIndexConfigurationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.fixedIndexConfigurationForm";

  constructor(config: Config<FixedIndexConfigurationForm> = null) {
    super((()=> ConfigUtils.apply(Config(FixedIndexConfigurationForm, {
      itemId: "fixedIndexConfigurationForm",
      header: false,
      hideSingleComponentLabel: false,
      propertyNames: [],
      collapsed: config.collapsed || true,
      settingsVE: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.propertyName),

      ...ConfigUtils.append({
        plugins: [
          Config(Binding, {
            source: "indexSettings.index",
            destination: "indexViewModel.index",
            twoWay: true,
          }),

        ],
      }),
      items: [
        Config(FieldContainer, {
          fieldLabel: BlueprintDocumentTypes_properties.CMQueryList_fixedIndex_title,
          items: [
            Config(AdvancedFieldContainer, {
              ui: LabelableSkin.PLAIN_LABEL.getSkin(),
              labelAlign: "top",
              labelSeparator: "",
              fieldLabel: BlueprintDocumentTypes_properties.CMQueryList_fixedIndex_label,
              hideLabel: false,
              items: [
                Config(StatefulNumberField, {
                  allowBlank: true,
                  allowDecimals: false,
                  emptyText: BlueprintDocumentTypes_properties.CMQueryList_fixedIndex_emptyText,
                  width: 200,
                  itemId: "fixedIndex",
                  plugins: [
                    Config(BlockEnterPlugin),
                    Config(BindPropertyPlugin, {
                      bindTo: ValueExpressionFactory.create(FixedIndexViewModel.INDEX_PROPERTY_NAME, this.indexViewModel),
                      bidirectional: true,
                    }),
                  ],
                }),
              ],
            }),
          ],
        }),
      ],
    }), config))());
  }

  /** the property of the Bean to bind in this field */
  propertyName: string = null;
}

export default FixedIndexConfigurationForm;
