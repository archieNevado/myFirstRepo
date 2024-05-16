package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.SitesService;
import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.analytics.elastic.google.ElasticGoogleAnalyticsServiceProvider.GOOGLE_ANALYTICS_SERVICE_KEY;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.KEY_PROPERTY_ID;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.DOCUMENT_PROPERTY_ACTION;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.DOCUMENT_PROPERTY_ANALYTICS_PROVIDER;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.DOCUMENT_PROPERTY_CATEGORY;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.DOCUMENT_PROPERTY_MAX_LENGTH;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.KEY_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElasticGoogleAnalyticsServiceProviderTest {

  private static final int PROPERTY_ID = 1234;
  private static final int TIME_RANGE = 30;

  private static final Map<String, Object> SETTINGS_WITH_TIME_RANGE = Map.of(DOCUMENT_PROPERTY_TIME_RANGE, TIME_RANGE);
  private static final Map<String, Object> SETTINGS_WITH_UNUSED = Map.of("unused", "does not matter");
  private final Map<String, Object> googleAnalyticsSettings = new HashMap<>();

  private ElasticGoogleAnalyticsServiceProvider provider;

  @Mock
  private Content cmAlxBaseList;

  @Mock
  private Content cmAlxPageList;

  @Mock
  private Content cmAlxEventList;

  @Mock
  private ContentType baseType;

  @Mock
  private ContentType pageType;

  @Mock
  private ContentType eventType;

  @Mock
  private SettingsService settingsService;

  @Mock
  private SitesService sitesService;

  @Mock
  private Content content;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private Content contentBlob;

  @Mock
  private Blob blob;

  @Mock
  private BetaAnalyticsDataClient dataClient;

  @Mock
  private BetaAnalyticsDataSettings dataSettings;

  @Mock
  private BetaAnalyticsDataSettings.Builder builder;

  @Mock
  private RunReportResponse runReportResponse;

  @Mock
  private RunReportRequest.Builder requestBuilder;

  @Mock
  private RunReportRequest request;

  @Mock
  private GoogleCredentials googleCredentials;

  @Mock
  private Row row;

  private final MockedStatic<BetaAnalyticsDataClient> staticDataClient = mockStatic(BetaAnalyticsDataClient.class);
  private final MockedStatic<BetaAnalyticsDataSettings> staticDataSettings = mockStatic(BetaAnalyticsDataSettings.class);
  private final MockedStatic<GoogleCredentials> staticGoogleCredentials = mockStatic(GoogleCredentials.class);

  @Before
  public void setup() throws IOException {
    provider = new ElasticGoogleAnalyticsServiceProvider(settingsService, sitesService);
    when(cmAlxBaseList.getType()).thenReturn(baseType);
    when(cmAlxPageList.getType()).thenReturn(pageType);
    when(cmAlxEventList.getType()).thenReturn(eventType);

    lenient().when(baseType.isSubtypeOf("CMALXBaseList")).thenReturn(true);

    lenient().when(pageType.isSubtypeOf("CMALXBaseList")).thenReturn(true);
    lenient().when(pageType.isSubtypeOf("CMALXPageList")).thenReturn(true);

    lenient().when(eventType.isSubtypeOf("CMALXBaseList")).thenReturn(true);
    lenient().when(eventType.isSubtypeOf("CMALXEventList")).thenReturn(true);

    lenient().when(cmAlxBaseList.getInteger(DOCUMENT_PROPERTY_MAX_LENGTH)).thenReturn(null);
    lenient().when(cmAlxPageList.getInteger(DOCUMENT_PROPERTY_MAX_LENGTH)).thenReturn(null);
    lenient().when(cmAlxEventList.getInteger(DOCUMENT_PROPERTY_MAX_LENGTH)).thenReturn(null);
    lenient().when(cmAlxBaseList.getInteger(DOCUMENT_PROPERTY_TIME_RANGE)).thenReturn(null);
    lenient().when(cmAlxPageList.getInteger(DOCUMENT_PROPERTY_TIME_RANGE)).thenReturn(null);
    lenient().when(cmAlxEventList.getInteger(DOCUMENT_PROPERTY_TIME_RANGE)).thenReturn(null);

    when(contentBlob.getBlob("data")).thenReturn(blob);
    when(blob.getSize()).thenReturn(42);

    when(settingsService.setting(eq(DOCUMENT_PROPERTY_ANALYTICS_PROVIDER), eq(String.class), any(Content.class), any(Content.class))).thenReturn(provider.getServiceKey());
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    // no site specific internal settings
    when(contentSiteAspect.getSite()).thenReturn(null);

    when(dataClient.runReport(request)).thenReturn(runReportResponse);
    when(runReportResponse.getRowsList()).thenReturn(List.of(row));
    when(requestBuilder.build()).thenReturn(request);

    staticDataClient.when(() -> BetaAnalyticsDataClient.create(any(BetaAnalyticsDataSettings.class))).thenReturn(dataClient);
    staticDataSettings.when(BetaAnalyticsDataSettings::newBuilder).thenReturn(builder);
    when(builder.setCredentialsProvider(any(CredentialsProvider.class))).thenReturn(builder);
    when(builder.build()).thenReturn(dataSettings);
    staticGoogleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(googleCredentials);
  }

  @After
  public void tearDown() {
    staticDataClient.close();
    staticDataSettings.close();
    staticGoogleCredentials.close();
  }

  @Test
  public void fetchNoDataPageList() {
    List<String> reportDataItems = provider.fetchDataFor(cmAlxPageList, SETTINGS_WITH_TIME_RANGE);
    assertThat(reportDataItems.size()).isEqualTo(0);
  }

  @Test
  public void fetchDataPageList() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxPageList), any(Content.class))).thenReturn(SETTINGS_WITH_TIME_RANGE);
    Map<String, Object> effectiveSettings = provider.computeEffectiveRetrievalSettings(cmAlxPageList, mock(Content.class));

    Map<String, Object> expectedEffectiveSettings = getEmptyEffectiveSettings();
    expectedEffectiveSettings.putAll(googleAnalyticsSettings);
    assertThat(effectiveSettings).isEqualTo(expectedEffectiveSettings);

    getChangedDefaultSettings();
    List<String> pageViews = List.of("1234");

    List<String> reportDataItems;
    try (MockedConstruction<PageViewQuery> ignored =
                 mockConstruction(PageViewQuery.class, (pageViewQuery, context) -> {
                   when(pageViewQuery.getDataQuery()).thenReturn(requestBuilder);
                   when(pageViewQuery.process(anyList(), anyList(), anyList())).thenReturn(pageViews);
                 })) {
      reportDataItems = provider.fetchDataFor(cmAlxPageList, googleAnalyticsSettings);
    }
    assertThat(reportDataItems.size()).isEqualTo(1);
  }

  @Test
  public void fetchDataEventList() {
    List<String> pageViews = List.of("1234");
    getChangedDefaultSettings();

    List<String> reportDataItems;
    try (MockedConstruction<EventQuery> ignored =
                 mockConstruction(EventQuery.class, (eventQuery, context) -> {
                   when(eventQuery.getDataQuery()).thenReturn(requestBuilder);
                   when(eventQuery.process(anyList(), anyList(), anyList())).thenReturn(pageViews);
                 })) {
      reportDataItems = provider.fetchDataFor(cmAlxEventList, googleAnalyticsSettings);
    }

    assertThat( reportDataItems.size()).isEqualTo(1);
  }

  @Test
  public void emptyListForInvalidContentbean() {
    getChangedDefaultSettings();
    List<String> reportDataItems = provider.fetchDataFor(cmAlxBaseList, googleAnalyticsSettings);

    assertThat(reportDataItems.size()).withFailMessage("No report data items for invalid contentbean.").isEqualTo(0);
  }

  @Test
  public void fetchPageViews() {
    String contentId = "12";
    String dateString = "20130713";
    long uniqueViews = 42L;

    getChangedDefaultSettings();

    Map<String, Long> map = Map.of(dateString, uniqueViews);
    Map<String, Map<String, Long>> processedResult = Map.of(contentId, map);

    Map<String, Map<String, Long>> result;
    try (MockedConstruction<PageViewHistoryQuery> ignored1 =
                 mockConstruction(PageViewHistoryQuery.class, (pageViewHistoryQuery, context) -> {
                   when(pageViewHistoryQuery.getDataQuery()).thenReturn(requestBuilder);
                   when(pageViewHistoryQuery.process(anyList(), anyList(), anyList())).thenReturn(processedResult);
                 });
         MockedConstruction<OverallPerformanceQuery> ignored2 =
                 mockConstruction(OverallPerformanceQuery.class, (overallPerformanceQuery, context) -> {
                   when(overallPerformanceQuery.getDataQuery()).thenReturn(requestBuilder);
                   when(overallPerformanceQuery.process(anyList(), anyList(), anyList())).thenReturn(processedResult);
                 })) {
      result = provider.fetchPageViews(content, googleAnalyticsSettings);
    }

    assertThat(result.size()).isEqualTo(1);
    assertThat((Object) result.get(contentId).get(dateString)).isEqualTo(42L);
  }

  @Test
  public void fetchPageViewsWithInvalidSettings() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxBaseList), any(Content.class))).thenReturn(SETTINGS_WITH_TIME_RANGE);
    Map<String, Object> effectiveSettings = getEmptyEffectiveSettings();
    effectiveSettings.putAll(googleAnalyticsSettings);
    assertThat(provider.computeEffectiveRetrievalSettings(cmAlxBaseList, mock(Content.class))).isEqualTo(effectiveSettings);
    provider.computeEffectiveRetrievalSettings(cmAlxBaseList, mock(Content.class));

    Map<String, Map<String, Long>> result = provider.fetchPageViews(content, googleAnalyticsSettings);

    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  public void testServiceKey() {
    assertThat(provider.getServiceKey()).isEqualTo(GOOGLE_ANALYTICS_SERVICE_KEY);
  }

  @Test
  public void computeEffectiveSettingsWithEmptySettings() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxPageList), any(Content.class))).thenReturn(new HashMap<>());

    Map<String, Object> effectiveSettings = provider.computeEffectiveRetrievalSettings(cmAlxPageList, mock(Content.class));

    // expect empty settings when called with empty map
    assertThat(effectiveSettings).isEqualTo(Collections.EMPTY_MAP);
  }

  @Test
  public void computeEffectiveSettingsWithUnimportantSettings() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxPageList), any(Content.class))).thenReturn(SETTINGS_WITH_UNUSED);

    Map<String, Object> effectiveSettings = provider.computeEffectiveRetrievalSettings(cmAlxPageList, mock(Content.class));

    // expect all the retrieval defaults as settings when map contained any data
    Map<String, Object> expectedEffectiveSettings = new HashMap<>(getEmptyEffectiveSettings());

    assertThat(effectiveSettings).isEqualTo(expectedEffectiveSettings);
  }

  @Test
  public void computeEffectiveSettingsWithTimeRangeChanged() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxPageList), any(Content.class))).thenReturn(SETTINGS_WITH_TIME_RANGE);

    Map<String, Object> effectiveSettings = provider.computeEffectiveRetrievalSettings(cmAlxPageList, mock(Content.class));

    // expect all the retrieval defaults as settings
    Map<String, Object> expectedEffectiveSettings = new HashMap<>(getEmptyEffectiveSettings());
    expectedEffectiveSettings.put(DOCUMENT_PROPERTY_TIME_RANGE, TIME_RANGE);

    assertThat(effectiveSettings).isEqualTo(expectedEffectiveSettings);
  }

  @Test
  public void computeEffectiveSettingsWithDifferentValues() {
    when(settingsService.mergedSettingAsMap(eq(GOOGLE_ANALYTICS_SERVICE_KEY), eq(String.class), eq(Object.class), eq(cmAlxPageList), any(Content.class))).thenReturn(getChangedDefaultSettings());

    Map<String, Object> effectiveSettings = provider.computeEffectiveRetrievalSettings(cmAlxPageList, mock(Content.class));

    // expect all configured settings plus the retrieval defaults
    Map<String, Object> expectedEffectiveSettings = new HashMap<>();
    expectedEffectiveSettings.putAll(getEmptyEffectiveSettings());
    expectedEffectiveSettings.putAll(googleAnalyticsSettings);

    assertThat(effectiveSettings).isEqualTo(expectedEffectiveSettings);
  }

  private Map<String, Object> getChangedDefaultSettings() {
    googleAnalyticsSettings.put(DOCUMENT_PROPERTY_TIME_RANGE, TIME_RANGE);
    googleAnalyticsSettings.put(DOCUMENT_PROPERTY_ACTION, "myAction");
    googleAnalyticsSettings.put(DOCUMENT_PROPERTY_CATEGORY, "myCategory");
    googleAnalyticsSettings.put(KEY_PROPERTY_ID, PROPERTY_ID);
    googleAnalyticsSettings.put(KEY_LIMIT, 20);
    googleAnalyticsSettings.put(ElasticGoogleAnalyticsServiceProvider.AUTH_FILE, contentBlob);

    return googleAnalyticsSettings;
  }

  private static Map<String, Object> getEmptyEffectiveSettings() {
    return new HashMap<>(ElasticGoogleAnalyticsServiceProvider.DEFAULT_RETRIEVAL_SETTINGS);
  }
}
