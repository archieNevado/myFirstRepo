package com.coremedia.blueprint.analytics.settings.google;

import com.coremedia.blueprint.analytics.settings.AbstractAnalyticsSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Map;

/**
 * Google specific implementation of an {@link com.coremedia.blueprint.analytics.settings.AnalyticsSettingsProvider}
 * that creates deep-link report URLs.
 */
@Named
public class GoogleAnalyticsSettingsProvider extends AbstractAnalyticsSettingsProvider {

  private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsSettingsProvider.class);

  private static final String GOOGLE_ANALYTICS = "googleAnalytics";
  private static final String KEY_PROPERTY_ID = "propertyId";

  static final String DEFAULT_REPORT_URL = "https://analytics.google.com/analytics/web/#/p";
  static final String URL_POSTFIX = "/reports/intelligenthome";

  @Override
  protected String buildReportUrl(Map<String, Object> settings, String linkToSelf){
    if (settings != null) {
      final Object propertyId = settings.get(KEY_PROPERTY_ID);
      if (propertyId == null) {
        LOG.debug("Google Analytics propertyId not set, cannot build url.");
        return null;
      }
      return DEFAULT_REPORT_URL + propertyId + URL_POSTFIX;
    }
    return null;
  }

  @Override
  public String getServiceKey() {
    return GOOGLE_ANALYTICS;
  }

  @Override
  protected boolean absolute() {
    return false;
  }
}
