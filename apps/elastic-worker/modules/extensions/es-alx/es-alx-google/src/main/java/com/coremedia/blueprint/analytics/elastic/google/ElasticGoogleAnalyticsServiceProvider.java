package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.blueprint.analytics.elastic.retrieval.AnalyticsServiceProvider;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.blueprint.base.analytics.elastic.util.SettingsUtil;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.KEY_PROPERTY_ID;

/**
 * Implements data retrieval for Google Analytics backed by CoreMedia Elastic storage.
 */
@SuppressWarnings("UnusedDeclaration")
@Named
@DefaultAnnotation(NonNull.class)
public class ElasticGoogleAnalyticsServiceProvider implements AnalyticsServiceProvider {

  public static final String GOOGLE_ANALYTICS_SERVICE_KEY = "googleAnalytics";
  static final String AUTH_FILE = "authFile";

  static final Map<String, Object> DEFAULT_RETRIEVAL_SETTINGS = createDefaultRetrievalSettings();

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticGoogleAnalyticsServiceProvider.class);
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  @Inject
  private SettingsService settingsService;

  @Inject
  private SitesService sitesService;

  public ElasticGoogleAnalyticsServiceProvider(SettingsService settingsService, SitesService sitesService) {
    this.settingsService = settingsService;
    this.sitesService = sitesService;
  }

  @Override
  public String getServiceKey() {
    return GOOGLE_ANALYTICS_SERVICE_KEY;
  }

  @Override
  public List<String> fetchDataFor(Content cmalxBaseList, Map<String, Object> effectiveSettings) {
    LOGGER.debug("fetching data for {}", cmalxBaseList);
    final GoogleAnalyticsSettings googleAnalyticsSettings = SettingsUtil.createProxy(GoogleAnalyticsSettings.class, effectiveSettings);

    Blob authFile = getPrivateKeyFromContent(googleAnalyticsSettings);
    if (authFile == null) {
      LOGGER.info("Cannot fetch data for {}, authFile not configured.", cmalxBaseList);
      return Collections.emptyList();
    }

    final GoogleAnalyticsListResultQuery query = createTopNListQuery(cmalxBaseList, googleAnalyticsSettings);
    if (query == null) {
      LOGGER.info("Cannot fetch data for {}, cannot create query.", cmalxBaseList);
      return Collections.emptyList();
    }

    return Optional.of(authFile)
            .map(ElasticGoogleAnalyticsServiceProvider::getCredentialSettings)
            .map(credentialSettings -> callWithGoogleDataClient(query, credentialSettings))
            .map(gaData -> query.process(gaData.getRowsList(), gaData.getDimensionHeadersList(), gaData.getMetricHeadersList()))
            .orElse(Collections.emptyList());

  }

  private static Blob getPrivateKeyFromContent(GoogleAnalyticsSettings googleAnalyticsSettings) {
    return Optional.of(googleAnalyticsSettings)
            .map(GoogleAnalyticsSettings::getAuthFile)
            .map(content -> content.getBlob("data"))
            .filter(blob -> blob.getSize() > 0)
            .orElse(null);
  }

  @Nullable
  private static BetaAnalyticsDataSettings getCredentialSettings(Blob authFile) {
    try {
      GoogleCredentials googleCredentials = GoogleCredentials.fromStream(authFile.getInputStream());
      return BetaAnalyticsDataSettings.newBuilder()
              .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
              .build();
    } catch (IOException e) {
      LOGGER.debug("Cannot create credential settings for Google authFile {}.", authFile, e);
      return null;
    }
  }

  @Nullable
  private static GoogleAnalyticsListResultQuery createTopNListQuery(Content cmalxBaseList, GoogleAnalyticsSettings googleAnalyticsSettings) {
    if (GoogleAnalyticsQuery.canCreateQuery(googleAnalyticsSettings)) {
      if (cmalxBaseList.getType().isSubtypeOf(RetrievalUtil.DOCTYPE_PAGELIST)) {
        return new PageViewQuery(googleAnalyticsSettings);
      } else if (cmalxBaseList.getType().isSubtypeOf(RetrievalUtil.DOCTYPE_EVENTLIST)) {
        return new EventQuery(googleAnalyticsSettings);
      }
    }
    return null;
  }

  @Override
  public Map<String, Map<String, Long>> fetchPageViews(Content content, Map<String, Object> rootNavigationSettings) {

    final GoogleAnalyticsSettings googleAnalyticsSettings = SettingsUtil.createProxy(GoogleAnalyticsSettings.class, rootNavigationSettings);
    if (!GoogleAnalyticsQuery.canCreateQuery(googleAnalyticsSettings)) {
      return Collections.emptyMap();
    }

    final int timeRange = googleAnalyticsSettings.getTimeRange();
    LOGGER.info("fetching data for the last {} days", timeRange > 0 ? timeRange : RetrievalUtil.DEFAULT_TIMERANGE);

    Blob authFile = getPrivateKeyFromContent(googleAnalyticsSettings);
    if (authFile == null) {
      LOGGER.info("Cannot fetch data for {}, Google authFile missing.", content);
      return Collections.emptyMap();
    }

    // execute pageView history query
    PageViewHistoryQuery pageViewHistoryQuery = new PageViewHistoryQuery(googleAnalyticsSettings);
    Map<String, Map<String, Long>> pageViewHistoryResult = Optional.of(authFile)
            .map(ElasticGoogleAnalyticsServiceProvider::getCredentialSettings)
            .map(credentialSettings -> callWithGoogleDataClient(pageViewHistoryQuery, credentialSettings))
            .map(gaData -> pageViewHistoryQuery.process(gaData.getRowsList(), gaData.getDimensionHeadersList(), gaData.getMetricHeadersList()))
            .orElse(Collections.emptyMap());

    // execute overall performance query
    OverallPerformanceQuery overAllPerformanceQuery = new OverallPerformanceQuery(content, googleAnalyticsSettings);
    Map<String, Map<String, Long>> overAllPerformanceResult = Optional.of(authFile)
            .map(ElasticGoogleAnalyticsServiceProvider::getCredentialSettings)
            .map(credentialSettings -> callWithGoogleDataClient(overAllPerformanceQuery, credentialSettings))
            .map(gaData -> overAllPerformanceQuery.process(gaData.getRowsList(), gaData.getDimensionHeadersList(), gaData.getMetricHeadersList()))
            .orElse(Collections.emptyMap());

    Map<String, Map<String, Long>> result = new HashMap<>();
    result.putAll(pageViewHistoryResult);
    result.putAll(overAllPerformanceResult);
    return result;
  }

  @Nullable
  private static RunReportResponse callWithGoogleDataClient(GoogleAnalyticsQuery query, BetaAnalyticsDataSettings betaAnalyticsDataSettings) {
    try (BetaAnalyticsDataClient analyticsDataClient =
                 BetaAnalyticsDataClient.create(betaAnalyticsDataSettings)) {
      RunReportResponse gaData = call(analyticsDataClient, query);
      if (gaData != null && !gaData.getRowsList().isEmpty()) {
        LOGGER.debug("Got {} tracked page views from Google Analytics", gaData.getRowsList().size());
        return gaData;
      } else {
        LOGGER.debug("Got no tracked page views from Google Analytics");
      }
    } catch (Exception e) {
      LOGGER.info("Cannot fetch data.", e);
    }
    return null;
  }

  /**
   * @return RunReportResponse returned by Google Analytics.
   */
  @Nullable
  private static RunReportResponse call(@NonNull BetaAnalyticsDataClient analytics, @NonNull GoogleAnalyticsQuery googleAnalyticsQuery) {
    try {
      RunReportRequest request = googleAnalyticsQuery.getDataQuery().build();
      LOGGER.debug("Firing Google Data Export API query: '{}'", request);

      // Make the request.
      RunReportResponse response = analytics.runReport(request);
      LOGGER.debug("Num results: " + response.getRowsList().size());
      return response;
    } catch (Exception e) {
      logWarningForServiceAccess(googleAnalyticsQuery, e);
    }
    return null;
  }

  private static void logWarningForServiceAccess(GoogleAnalyticsQuery googleAnalyticsQuery, Exception e) {
    LOGGER.warn("Caught exception while retrieving data for '{}' from google analytics: {}",
            googleAnalyticsQuery,
            e.getMessage());
  }

  @Override
  public Map<String, Object> computeEffectiveRetrievalSettings(Content cmalxBaseList, Content rootNavigation) {
    return RetrievalUtil.computeEffectiveRetrievalSettings(GOOGLE_ANALYTICS_SERVICE_KEY, DEFAULT_RETRIEVAL_SETTINGS, cmalxBaseList, rootNavigation, settingsService, sitesService);
  }

  private static Map<String, Object> createDefaultRetrievalSettings() {
    Map<String, Object> map = new HashMap<>(RetrievalUtil.DEFAULT_RETRIEVAL_SETTINGS);
    map.put(KEY_PROPERTY_ID, 0);
    map.put(AUTH_FILE, null);
    return Collections.unmodifiableMap(map);
  }
}
