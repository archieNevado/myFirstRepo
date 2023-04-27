package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.cap.content.Content;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.MetricValue;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DIMENSION_CONTENT_TYPE;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DIMENSION_TRACKING_DATE;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.METRIC_PAGEVIEWS;
import static com.coremedia.blueprint.analytics.elastic.google.OverallPerformanceQuery.DIMENSION_CONTENT_TYPE_FILTER_EXCLUDE;
import static com.coremedia.blueprint.analytics.elastic.google.OverallPerformanceQuery.DIMENSION_PAGE_PATH_LEVEL1;
import static com.coremedia.blueprint.analytics.elastic.google.OverallPerformanceQuery.PATH_FILTER_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OverallPerformanceQueryTest {
  private static final int PROPERTY_ID = 1234;
  private static final int TIME_RANGE = 30;
  private static final String CONTENT_ID = "1234";
  private static final String SEGMENT = "segment";
  private OverallPerformanceQuery overallPerformanceQuery;

  @Mock
  private Content content;

  @Mock
  private GoogleAnalyticsSettings googleAnalyticsSettings;

  @Before
  public void setup() {
    when(googleAnalyticsSettings.getTimeRange()).thenReturn(TIME_RANGE);
    when(googleAnalyticsSettings.getPropertyId()).thenReturn(PROPERTY_ID);
    when(content.getId()).thenReturn(CONTENT_ID);
    when(content.getString(SEGMENT)).thenReturn(SEGMENT);
    overallPerformanceQuery = new OverallPerformanceQuery(content, googleAnalyticsSettings);
  }

  @Test
  public void process() {
    String pagePath1 = "/path1/";
    String pagePath2 = "/path2/";
    String day1 = "20130709";
    String day2 = "20130710";
    String day3 = "20130711";
    long metric1 = 13;
    long metric2 = 42;
    long metric3 = 1;

    // ROW 1: pagePath1, day1, "13"
    Row.Builder row1 = Row.newBuilder();
    row1.addDimensionValues(DimensionValue.newBuilder().setValue(pagePath1).build());
    row1.addDimensionValues(DimensionValue.newBuilder().setValue(day1).build());
    row1.addMetricValues(MetricValue.newBuilder().setValue(String.valueOf(metric1)));

    // ROW 2: pagePath1, day2, "42"
    Row.Builder row2 = Row.newBuilder();
    row2.addDimensionValues(DimensionValue.newBuilder().setValue(pagePath1).build());
    row2.addDimensionValues(DimensionValue.newBuilder().setValue(day2).build());
    row2.addMetricValues(MetricValue.newBuilder().setValue(String.valueOf(metric2)));

    // ROW 3: pagePath2, day3, "1"
    Row.Builder row3 = Row.newBuilder();
    row3.addDimensionValues(DimensionValue.newBuilder().setValue(pagePath2).build());
    row3.addDimensionValues(DimensionValue.newBuilder().setValue(day3).build());
    row3.addMetricValues(MetricValue.newBuilder().setValue(String.valueOf(metric3)));

    List<Row> dataList = List.of(row1.build(), row2.build(), row3.build());

    List<DimensionHeader> dimensionHeaders = Arrays.asList(
            DimensionHeader.newBuilder().setName(DIMENSION_PAGE_PATH_LEVEL1).build(),
            DimensionHeader.newBuilder().setName(DIMENSION_TRACKING_DATE).build());
    List<MetricHeader> metricHeaders = List.of(MetricHeader.newBuilder().setName(METRIC_PAGEVIEWS).build());

    Map<String, Map<String, Long>> processedEntries = overallPerformanceQuery.process(dataList, dimensionHeaders, metricHeaders);

    assertThat(1).isEqualTo(processedEntries.size());
    assertThat(3).isEqualTo(processedEntries.get(CONTENT_ID).entrySet().size());
    assertThat(metric1).isEqualTo(processedEntries.get(CONTENT_ID).get(day1));
    assertThat(metric2).isEqualTo(processedEntries.get(CONTENT_ID).get(day2));
    assertThat(metric3).isEqualTo(processedEntries.get(CONTENT_ID).get(day3));
  }

  @Test
  public void testToString() {
    assertThat(overallPerformanceQuery.toString()).isNotEmpty();
  }

  @Test
  public void getContentPath() {
    String contentPath = overallPerformanceQuery.getContentPath();
    assertThat(contentPath).isEqualTo(SEGMENT);
  }

  @Test
  public void customizeQuery() {
    String segment = "corporate";
    when(content.getString("segment")).thenReturn(segment);
    RunReportRequest.Builder queryBuilder = RunReportRequest.newBuilder();
    overallPerformanceQuery.customizeQuery(queryBuilder);

    assertThat(queryBuilder.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_TRACKING_DATE).build());
    assertThat(queryBuilder.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_PAGE_PATH_LEVEL1).build());
    assertThat(queryBuilder.getDimensionFilter()).isEqualTo(
            FilterExpression.newBuilder()
                    .setAndGroup(FilterExpressionList.newBuilder()
                            .addExpressions(FilterExpression.newBuilder()
                                    .setFilter(Filter.newBuilder()
                                            .setFieldName(DIMENSION_PAGE_PATH_LEVEL1)
                                            .setStringFilter(Filter.StringFilter.newBuilder()
                                                    .setMatchType(Filter.StringFilter.MatchType.FULL_REGEXP)
                                                    .setValue(String.format(PATH_FILTER_TEMPLATE, segment))
                                            )))
                            .addExpressions(FilterExpression.newBuilder()
                                    .setNotExpression(FilterExpression.newBuilder()
                                            .setFilter(Filter.newBuilder()
                                                    .setFieldName(DIMENSION_CONTENT_TYPE)
                                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                                            .setMatchType(Filter.StringFilter.MatchType.FULL_REGEXP)
                                                            .setValue(DIMENSION_CONTENT_TYPE_FILTER_EXCLUDE)
                                                    ))))

                    ).build());
  }
}
