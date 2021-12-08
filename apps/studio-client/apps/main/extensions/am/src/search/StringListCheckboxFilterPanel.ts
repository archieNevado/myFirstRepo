import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import CheckboxGroup from "@jangaroo/ext-ts/form/CheckboxGroup";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import StringListCheckboxFilterPanelBase from "./StringListCheckboxFilterPanelBase";

interface StringListCheckboxFilterPanelConfig extends Config<StringListCheckboxFilterPanelBase>, Partial<Pick<StringListCheckboxFilterPanel,
  "filterId"
>> {
}

class StringListCheckboxFilterPanel extends StringListCheckboxFilterPanelBase {
  declare Config: StringListCheckboxFilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.stringListCheckboxFilterPanel";

  constructor(config: Config<StringListCheckboxFilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(StringListCheckboxFilterPanel, {
      itemId: config.filterId,

      items: [
        Config(Container, {
          itemId: "checkboxContainer",
          defaultType: CheckboxGroup["xtype"],
          defaults: Config<CheckboxGroup>({
            columns: 1,
            plugins: [
              Config(BindPropertyPlugin, {
                bindTo: ValueExpressionFactory.create(config.filterId, this.getStateBean()),
                bidirectional: true,
                transformer: bind(this, this.transformer),
                reverseTransformer: bind(this, this.reverseTransformer),
              }),
            ],
          }),
        }),
      ],

    }), config))());
  }

  /**
   * The filter ID for this filter. It is used as itemId and identifier in saved searches.
   */
  filterId: string = null;
}

export default StringListCheckboxFilterPanel;
