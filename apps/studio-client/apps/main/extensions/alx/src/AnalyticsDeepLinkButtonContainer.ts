import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsDeepLinkButtonContainerBase from "./AnalyticsDeepLinkButtonContainerBase";

interface AnalyticsDeepLinkButtonContainerConfig extends Config<AnalyticsDeepLinkButtonContainerBase>, Partial<Pick<AnalyticsDeepLinkButtonContainer,
  "bindTo"
>> {
}

class AnalyticsDeepLinkButtonContainer extends AnalyticsDeepLinkButtonContainerBase {
  declare Config: AnalyticsDeepLinkButtonContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.analyticsDeepLinkButtonContainer";

  constructor(config: Config<AnalyticsDeepLinkButtonContainer> = null) {
    super(ConfigUtils.apply(Config(AnalyticsDeepLinkButtonContainer, { defaults: { contentExpression: config.bindTo } }), config));
  }

  bindTo: ValueExpression = null;
}

export default AnalyticsDeepLinkButtonContainer;
