package com.coremedia.blueprint.analytics.google;


import com.coremedia.blueprint.analytics.AnalyticsProvider;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.Page;

public class GoogleAnalytics extends AnalyticsProvider {
  /**
   * The Google Analytics service key.
   */
  public static final String GOOGLE_ANALYTICS_SERVICE_KEY = "googleAnalytics";

  private static final String MEASUREMENT_ID = "measurementId";

  // visible for testing
  static final String DISABLE_AD_FEATURES_PLUGIN = "disableAdvertisingFeaturesPlugin";

  public GoogleAnalytics(Page page, SettingsService settingsService) {
    super(GOOGLE_ANALYTICS_SERVICE_KEY, page, settingsService);
  }

  @Override
  protected boolean isConfigValid() {
    return isNonEmptyString(getMeasurementId(), MEASUREMENT_ID);
  }

  public Object getMeasurementId() {
    return getSettings().get(MEASUREMENT_ID);
  }

  public boolean isAdvertisingFeaturesPluginDisabled() {
    final Object isDisabledAdFeaturesPlugin = getSettings().get(DISABLE_AD_FEATURES_PLUGIN);
    return isDisabledAdFeaturesPlugin != null && Boolean.parseBoolean(isDisabledAdFeaturesPlugin.toString());
  }
}
