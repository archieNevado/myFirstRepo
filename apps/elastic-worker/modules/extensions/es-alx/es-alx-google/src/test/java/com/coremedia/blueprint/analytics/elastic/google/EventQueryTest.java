package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.blueprint.base.analytics.elastic.util.DaysBack;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.blueprint.base.analytics.elastic.util.SettingsUtil;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.Metric;
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

import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.DIMENSION_ACTION;
import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.DIMENSION_CATEGORY;
import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.DIMENSION_LABEL;
import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.EVENT_NAME;
import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.EVENT_NAME_DEFAULT_VALUE;
import static com.coremedia.blueprint.analytics.elastic.google.EventQuery.METRIC_TOTAL_EVENTS;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.DATE_PATTERN;
import static com.coremedia.blueprint.analytics.elastic.google.GoogleAnalyticsQuery.METRIC_PAGEVIEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventQueryTest {
  private static final int PROPERTY_ID = 46635897;

  @Mock
  private Row row1;

  @Mock
  private Row row2;

  @Mock
  private DimensionValue dimensionValueLabel1;

  @Mock
  private DimensionValue dimensionValueLabel2;

  @Test(expected = IllegalArgumentException.class)
  public void testConstructionIllegalCategory() {
    new EventQuery(123456, 10, 10, null, "foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructionIllegalAction() {
    new EventQuery( 123456, 10, 10, "foo", null);
  }

  @Test
  public void testVerifyQuerySyntaxTotalEvents() {
    int timeRange = 10;
    int maxResults = 100;
    String testCategory = "TestCategory";
    String testAction = "TestAction";
    EventQuery defaultQuery = new EventQuery(PROPERTY_ID, timeRange, maxResults, testCategory, testAction);
    final DaysBack daysBack = new DaysBack(timeRange);
    final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    final RunReportRequest.Builder query = defaultQuery.getDataQuery();

    assertThat(query.getProperty()).isEqualTo("properties/" + PROPERTY_ID);
    assertThat(query.getLimit()).isEqualTo(maxResults);
    assertThat(query.getMetricsList()).contains(Metric.newBuilder().setName(METRIC_PAGEVIEWS).build());
    assertThat(query.getMetricsList()).contains(Metric.newBuilder().setName(METRIC_TOTAL_EVENTS).build());
    assertThat(query.getDateRangesList()).contains(DateRange.newBuilder().setStartDate(DATE_FORMAT.format(daysBack.getStartDate())).setEndDate(DATE_FORMAT.format(daysBack.getEndDate())).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_CATEGORY).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_ACTION).build());
    assertThat(query.getDimensionsList()).contains(Dimension.newBuilder().setName(DIMENSION_LABEL).build());
    assertThat(query.getOrderBysList()).contains(OrderBy.newBuilder().setDesc(true).setDimension(OrderBy.DimensionOrderBy.newBuilder().setDimensionName(METRIC_TOTAL_EVENTS)).build());
    assertThat(query.getDimensionFilter()).isEqualTo(FilterExpression.newBuilder()
            .setAndGroup(FilterExpressionList.newBuilder()
                    .addExpressions(FilterExpression.newBuilder()
                            .setFilter(Filter.newBuilder()
                                    .setFieldName(DIMENSION_CATEGORY)
                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                            .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                            .setValue(testCategory)
                                    )))
                    .addExpressions(FilterExpression.newBuilder().setFilter(
                            Filter.newBuilder()
                                    .setFieldName(DIMENSION_ACTION)
                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                            .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                            .setValue(testAction)
                                    )))
                    .addExpressions(FilterExpression.newBuilder().setFilter(Filter.newBuilder()
                            .setFieldName(EVENT_NAME)
                            .setStringFilter(Filter.StringFilter.newBuilder()
                                    .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                    .setValue(EVENT_NAME_DEFAULT_VALUE)
                            )))
            )
            .build());
  }


  @Test
  public void testCreateQuery() {
    EventQuery query = createDefaultQuery();

    assertThat(30).isEqualTo(query.getTimeRange());
    assertThat(20).isEqualTo(query.getMaxResults());
    assertThat(1234).isEqualTo(query.getPropertyId());

    assertThat(query.toString()).contains("1234");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidQuery() {
    Map<String, Object> settings = Map.of(RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE, 30);
    EventQuery query = new EventQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings));
  }

  @Test
  public void process() {
    EventQuery query = createDefaultQuery();
    String label1 = "label1";
    String label2 = "label2";

    when(dimensionValueLabel1.getValue()).thenReturn(label1);
    when(dimensionValueLabel2.getValue()).thenReturn(label2);

    // ROW 1: (not set), "label1"
    when(row1.getDimensionValues(1)).thenReturn(dimensionValueLabel1);
    // ROW 2: (not set), "label2"
    when(row2.getDimensionValues(1)).thenReturn(dimensionValueLabel2);
    List<Row> rows = List.of(row1, row2);

    List<DimensionHeader> dimensionHeaders = Arrays.asList(
            DimensionHeader.newBuilder().setName("something").build(),
            DimensionHeader.newBuilder().setName(EventQuery.DIMENSION_LABEL).build());

    List<String> processedList = query.process(rows, dimensionHeaders, List.of());
    assertThat(label1).isEqualTo(processedList.get(0));
    assertThat(label2).isEqualTo(processedList.get(1));
  }

  private static EventQuery createDefaultQuery() {
    Map<String, Object> settings = Map.of(
            RetrievalUtil.KEY_LIMIT, 20,
            RetrievalUtil.DOCUMENT_PROPERTY_TIME_RANGE, 30,
            RetrievalUtil.DOCUMENT_PROPERTY_ACTION, "myAction",
            RetrievalUtil.DOCUMENT_PROPERTY_CATEGORY, "myCategory",
            GoogleAnalyticsQuery.KEY_PROPERTY_ID, 1234
    );
    return new EventQuery(SettingsUtil.createProxy(GoogleAnalyticsSettings.class, settings));
  }
}
