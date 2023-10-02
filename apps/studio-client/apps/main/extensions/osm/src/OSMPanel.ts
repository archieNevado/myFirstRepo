import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import FitLayout from "@jangaroo/ext-ts/layout/container/Fit";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OSMPanelBase from "./OSMPanelBase";

interface OSMPanelConfig extends Config<OSMPanelBase>, Partial<Pick<OSMPanel,
  "latLngExpression"
>> {
}

class OSMPanel extends OSMPanelBase {
  declare Config: OSMPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.osm.config.osmPanel";

  constructor(config: Config<OSMPanel> = null) {
    super(ConfigUtils.apply(Config(OSMPanel, {
      height: 325,
      itemId: "osmPanel",

      items: [
      ],
      layout: Config(FitLayout),

    }), config));
  }

  /**
   * Value expression that holds the latitude and longitude values.
   */
  latLngExpression: ValueExpression = null;
}

export default OSMPanel;
