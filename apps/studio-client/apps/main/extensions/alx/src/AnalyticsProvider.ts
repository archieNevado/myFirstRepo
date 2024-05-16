import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsProviderBase from "./AnalyticsProviderBase";

interface AnalyticsProviderConfig extends Config<AnalyticsProviderBase>, Partial<Pick<AnalyticsProvider,
  "providerName" |
  "localizedProviderName"
>> {
}

class AnalyticsProvider extends AnalyticsProviderBase {
  declare Config: AnalyticsProviderConfig;

  constructor(config: Config<AnalyticsProvider> = null) {
    super(ConfigUtils.apply(Config<AnalyticsProvider>({}), config));
  }

  /**
   * The provider name of the analytics service provider
   */
  providerName: string = null;

  /**
   * The localized provider name of the analytics service provider
   */
  localizedProviderName: string = null;
}

export default AnalyticsProvider;
