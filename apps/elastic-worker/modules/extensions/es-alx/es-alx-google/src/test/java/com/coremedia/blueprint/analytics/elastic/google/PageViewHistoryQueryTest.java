package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.blueprint.base.analytics.elastic.util.DaysBack;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.blueprint.base.analytics.elastic.util.SettingsUtil;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.MetricValue;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DATE_PATTERN;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DIMENSION_CONTENT_ID;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DIMENSION_TRACKING_DATE;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.METRIC_PAGEVIEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class PageViewHistoryQueryTest {

  public static final int TIME_RANGE = 7;
  public static final int PROPERTY_ID = 12345678;

  @Mock
  private Row row1;

  @Mock
  private Row row2;

  @Mock
  private Row row3;

  @Mock
  private DimensionValue dimensionValue1;

  @Mock
  private DimensionValue dimensionValue2;

  @Mock
  private DimensionValue dimensionValueDay1;

  @Mock
  private DimensionValue dimensionValueDay2;

  @Mock
  private MetricValue metricValue1;

  @Mock
  private MetricValue metricValue2;

  @Mock
  private MetricValue metricValue3;

  @Test(expected = IllegalArgumentException.class)
  public void testConstructionIllegalProfileId() {
    new PageViewHistoryQuery(0, 10, 0);
  }

  @Test
  public void testConstructionIllegalTimeRange() {
    assertThat(new PageViewHistoryQuery(46635897, 0, 0).getTimeRange()).isEqualTo(RetrievalUtil.DEFAULT_TIMERANGE);
  }

  @Test
  public void testGetUrlVerifySyntax() {
    int maxResults = 100;
    final DaysBack daysBack = new DaysBack(TIME_RANGE);
    final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    final PageViewHistoryQuery defaultQuery = new PageViewHistoryQuery(PROPERTY_ID, TIME_RANGE, maxResults);
    RunReportRequest.Builder query = defaultQuery.getDataQuery();

    assertThat(query.getProperty()).isEqualTo("properties/" + PROPERTY_ID);
    assertThat(query.getLimit()).isEqualTo(maxResults);
    assertThat(query.getMetricsList()).contains(Metric.newBuilder().setName(METRIC_PAGEVIEWS).build());
    assertThat(query.getDateRangesList()).contains(DateRange.newBuilder().setStartDate(DATE_FORMAT.format(daysBack.getStartDate())).setEndDate(DATE_FORMAT.format(daysBack.getEndDate())).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_TRACKING_DATE).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_CONTENT_ID).build());
    assertThat(query.getOrderBysList()).contains(OrderBy.newBuilder().setDesc(true).setDimension(OrderBy.DimensionOrderBy.newBuilder().setDimensionName(DIMENSION_CONTENT_ID)).build());
  }

  @Test
  public void testGetProfileId() {
    int profileId = createDefaultQuery().getPropertyId();
    Assert.assertEquals(PROPERTY_ID, profileId);
  }

  @Test
  public void testGetTimeRange() {
    int timeRange = createDefaultQuery().getTimeRange();
    Assert.assertEquals(TIME_RANGE, timeRange);
  }

  @Test
  public void testProcess() {
    String contentId1 = "42";
    String contentId2 = "44";
    String day1 = "20130709";
    String day2 = "20130710";
    long metric1 = 13;
    long metric2 = 42;
    long metric3 = 1;

    when(dimensionValue1.getValue()).thenReturn(contentId1);
    when(dimensionValue2.getValue()).thenReturn(contentId2);
    when(dimensionValueDay1.getValue()).thenReturn(day1);
    when(dimensionValueDay2.getValue()).thenReturn(day2);
    when(metricValue1.getValue()).thenReturn(String.valueOf(metric1));
    when(metricValue2.getValue()).thenReturn(String.valueOf(metric2));
    when(metricValue3.getValue()).thenReturn(String.valueOf(metric3));

    // ROW 1: contentId1, day1, "13"
    when(row1.getDimensionValues(0)).thenReturn(dimensionValue1);
    when(row1.getDimensionValues(1)).thenReturn(dimensionValueDay1);
    when(row1.getMetricValues(0)).thenReturn(metricValue1);
    // ROW 2: contentId1, day2, "42"
    when(row2.getDimensionValues(0)).thenReturn(dimensionValue1);
    when(row2.getDimensionValues(1)).thenReturn(dimensionValueDay2);
    when(row2.getMetricValues(0)).thenReturn(metricValue2);
    // ROW 3: contentId2, day1, "1"
    when(row3.getDimensionValues(0)).thenReturn(dimensionValue2);
    when(row3.getDimensionValues(1)).thenReturn(dimensionValueDay1);
    when(row3.getMetricValues(0)).thenReturn(metricValue3);
    List<Row> dataList = List.of(row1, row2, row3);

    List<DimensionHeader> dimensionHeaders = Arrays.asList(
            DimensionHeader.newBuilder().setName(DIMENSION_CONTENT_ID).build(),
            DimensionHeader.newBuilder().setName(DIMENSION_TRACKING_DATE).build());
    List<MetricHeader> metricHeaders = List.of(MetricHeader.newBuilder().setName(METRIC_PAGEVIEWS).build());

    Map<String, Map<String, Long>> processedEntries = createDefaultQuery().process(dataList, dimensionHeaders, metricHeaders);

    assertThat(processedEntries.size()).isEqualTo(2);
    assertThat(processedEntries.get(contentId1).size()).isEqualTo(2);
    assertThat(processedEntries.get(contentId1).get(day1)).isEqualTo((Object) 13L);
    assertThat(processedEntries.get(contentId1).get(day2)).isEqualTo((Object) 42L);
    assertThat(processedEntries.get(contentId2).size()).isEqualTo(1);
    assertThat(processedEntries.get(contentId2).get(day1)).isEqualTo((Object) 1L);
    assertThat(processedEntries.get(contentId2).get(day2)).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidQuery() {
    Map<String, Object> settings = Map.of();
    PageViewHistoryQuery query = new PageViewHistoryQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings).getPropertyId(), 10, 0);
  }

  @Test
  public void testCreateQuery() {
    int profileId = 1234;
    int timeRange = 20;

    Map<String, Object> settings = Map.of(GoogleAnalyticsQuery.KEY_PROPERTY_ID, profileId);
    PageViewHistoryQuery query = new PageViewHistoryQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings).getPropertyId(), timeRange, 0);
    assertThat(query.getPropertyId()).isEqualTo(profileId);
    assertThat(query.getMaxResults()).isEqualTo(PageViewHistoryQuery.DEFAULT_MAX_RESULTS);
    assertThat(query.getTimeRange()).isEqualTo(timeRange);
    assertThat(query.toString()).contains(String.valueOf(profileId));
  }

  private static PageViewHistoryQuery createDefaultQuery() {
    Map<String, Object> settings = Map.of(
            RetrievalUtil.KEY_LIMIT, 20,
            RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE, TIME_RANGE,
            GoogleAnalyticsQuery.KEY_PROPERTY_ID, PROPERTY_ID
    );
    return new PageViewHistoryQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings));
  }
}
