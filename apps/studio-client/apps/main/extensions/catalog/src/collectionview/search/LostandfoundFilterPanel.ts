import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogStudioPlugin_properties from "../../CatalogStudioPlugin_properties";
import LostandfoundFilterPanelBase from "./LostandfoundFilterPanelBase";

interface LostandfoundFilterPanelConfig extends Config<LostandfoundFilterPanelBase> {
}

class LostandfoundFilterPanel extends LostandfoundFilterPanelBase {
  declare Config: LostandfoundFilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.catalog.lostandfoundFilterPanel";

  /**
   * The filter ID for this filter. It is used as itemId and identifier in saved searches.
   */
  static readonly FILTER_ID: string = "lostandfoundfilterid";

  constructor(config: Config<LostandfoundFilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(LostandfoundFilterPanel, {
      itemId: LostandfoundFilterPanel.FILTER_ID,
      title: CatalogStudioPlugin_properties.Filter_LostAndFound_text,

      items: [
        Config(Container, {
          items: [
            Config(Checkbox, {
              itemId: "filterLostandfoundCheckbox",
              boxLabel: CatalogStudioPlugin_properties.Filter_LostAndFound_checkbox,
              hideLabel: true,
              plugins: [
                Config(BindPropertyPlugin, {
                  bidirectional: true,
                  bindTo: new ConfigBasedValueExpression({
                    expression: LostandfoundFilterPanelBase.LOSTANDFOUND_CHECKBOX_SELECTED,
                    context: this.getStateBean(),
                  }),
                }),
              ],
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default LostandfoundFilterPanel;
