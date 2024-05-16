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
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
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
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.METRIC_PAGEVIEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageViewQueryTest {
  private static final int PROPERTY_ID = 46635897;

  @Mock
  private Row row1;

  @Mock
  private Row row2;

  @Mock
  private DimensionValue dimensionValue1;

  @Mock
  private DimensionValue dimensionValue2;

  @Test(expected = IllegalArgumentException.class)
  public void testConstructionIllegalProfileId() {
    new PageViewQuery(0, 10, 10);
  }

  @Test
  public void testDefaultTimeRange() {
    assertThat(new PageViewQuery(PROPERTY_ID, 0, 10).getTimeRange()).isEqualTo(RetrievalUtil.DEFAULT_TIMERANGE);
  }

  @Test
  public void testDefaultMaxResults() {
    assertThat(new PageViewQuery(PROPERTY_ID, 10, 0).getMaxResults()).isEqualTo(GoogleAnalyticsQuery.DEFAULT_MAX_RESULTS);
  }

  @Test
  public void testGetDataQuery() {
    int timeRange = 10;
    int maxResults = 100;
    final DaysBack daysBack = new DaysBack(timeRange);
    final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    final PageViewQuery defaultQuery = new PageViewQuery(PROPERTY_ID, timeRange, maxResults);
    RunReportRequest.Builder query = defaultQuery.getDataQuery();

    assertThat(query.getProperty()).isEqualTo("properties/" + PROPERTY_ID);
    assertThat(query.getLimit()).isEqualTo(maxResults);
    assertThat(query.getMetricsList()).contains(Metric.newBuilder().setName(METRIC_PAGEVIEWS).build());
    assertThat(query.getDateRangesList()).contains(DateRange.newBuilder().setStartDate(DATE_FORMAT.format(daysBack.getStartDate())).setEndDate(DATE_FORMAT.format(daysBack.getEndDate())).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_CONTENT_ID).build());
    assertThat(query.getOrderBysList()).contains(OrderBy.newBuilder().setDesc(true).setMetric(OrderBy.MetricOrderBy.newBuilder().setMetricName(METRIC_PAGEVIEWS)).build());
  }

  @Test
  public void testCreateQuery() {
    PageViewQuery query = createDefaultQuery();

    assertThat(query.getTimeRange()).isEqualTo(30);
    assertThat(query.getMaxResults()).isEqualTo(20);
    assertThat(query.getPropertyId()).isEqualTo(1234);

    assertThat(query.toString()).contains("1234");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidQuery() {
    Map<String, Object> settings = Map.of(
            RetrievalUtil.KEY_LIMIT, 20,
            RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE, 30
    );

    new PageViewQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings));
  }

  @Test
  public void process() {
    PageViewQuery query = createDefaultQuery();
    String contentId1 = "1234";
    String contentId2 = "5678";

    when(dimensionValue1.getValue()).thenReturn(contentId1);
    when(dimensionValue2.getValue()).thenReturn(contentId2);

    // ROW 1: (not set), "contentId1"
    when(row1.getDimensionValues(1)).thenReturn(dimensionValue1);
    // ROW 2: (not set), "contentId2"
    when(row2.getDimensionValues(1)).thenReturn(dimensionValue2);
    List<Row> rows = List.of(row1, row2);

    List<DimensionHeader> dimensionHeaders = Arrays.asList(
            DimensionHeader.newBuilder().setName("something").build(),
            DimensionHeader.newBuilder().setName(DIMENSION_CONTENT_ID).build());
    List<MetricHeader> metricHeaders = List.of(MetricHeader.newBuilder().setName(METRIC_PAGEVIEWS).build());

    List<String> processedList = query.process(rows, dimensionHeaders, metricHeaders);

    assertThat(processedList.get(0)).isEqualTo(contentId1);
    assertThat(processedList.get(1)).isEqualTo(contentId2);
  }

  private static PageViewQuery createDefaultQuery() {
    Map<String, Object> settings = Map.of(
            RetrievalUtil.KEY_LIMIT, 20,
            RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE, 30,
            GoogleAnalyticsQuery.KEY_PROPERTY_ID, 1234
    );
    return new PageViewQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings));
  }
}
