import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import ExpirationDateFilterPanelBase from "./ExpirationDateFilterPanelBase";
import ExpirationDateSelector from "./ExpirationDateSelector";

interface ExpirationDateFilterPanelConfig extends Config<ExpirationDateFilterPanelBase> {
}

class ExpirationDateFilterPanel extends ExpirationDateFilterPanelBase {
  declare Config: ExpirationDateFilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.expirationDateFilterPanel";

  /**
   * The filter ID for this filter. It is used as itemId and identifier in saved searches.
   */
  static readonly FILTER_ID: string = "expirationDate";

  constructor(config: Config<ExpirationDateFilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(ExpirationDateFilterPanel, {
      itemId: ExpirationDateFilterPanel.FILTER_ID,
      title: AMStudioPlugin_properties.Filter_ExpirationDate_text,

      items: [
        Config(Container, {
          items: [
            Config(ExpirationDateSelector, {
              selectedKeyValueExpression: ValueExpressionFactory.create(ExpirationDateFilterPanelBase.KEY, this.getStateBean()),
              selectedDateValueExpression: ValueExpressionFactory.create(ExpirationDateFilterPanelBase.DATE, this.getStateBean()),
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default ExpirationDateFilterPanel;
