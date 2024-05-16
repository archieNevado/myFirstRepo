package com.coremedia.blueprint.analytics.elastic.google;

import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates queries to Google's Data Export API to retrieve events with configured category and action
 */
@DefaultAnnotation(NonNull.class)
public final class EventQuery extends GoogleAnalyticsListResultQuery {

  static final String DIMENSION_CATEGORY = "customEvent:eventCategory";
  static final String DIMENSION_ACTION = "customEvent:eventAction";
  static final String DIMENSION_LABEL = "customEvent:eventLabel";
  static final String EVENT_NAME = "eventName";
  static final String EVENT_NAME_DEFAULT_VALUE = "cm_event";

  static final String METRIC_TOTAL_EVENTS = "eventCount";

  // the event category
  private final String category;

  // the event action
  private final String action;

  /**
   * Constructor
   *
   * @param propertyId see superclass' constructor's JavaDoc
   * @param timeRange  see superclass' constructor's JavaDoc
   * @param maxResults see superclass' constructor's JavaDoc
   * @param category   the event category
   * @param action     the actions to fetch
   */
  EventQuery(final int propertyId,
             final int timeRange,
             final int maxResults,
             final String category,
             final String action) {
    super(propertyId, timeRange, maxResults);

    if (category == null) {
      throw new IllegalArgumentException("Parameter 'category' must be set.");
    }
    if (action == null) {
      throw new IllegalArgumentException("Parameter 'action' must be set.");
    }

    this.category = category;
    this.action = action;
  }

  public EventQuery(GoogleAnalyticsSettings googleAnalyticsSettings) {
    this(
            googleAnalyticsSettings.getPropertyId(),
            googleAnalyticsSettings.getTimeRange(),
            googleAnalyticsSettings.getLimit(),
            googleAnalyticsSettings.getCategory(),
            googleAnalyticsSettings.getAction());
  }

  @Override
  protected void customizeQuery(final RunReportRequest.Builder query) {
    //  configure dimensions
    query.addDimensions(Dimension.newBuilder().setName(DIMENSION_CATEGORY));
    query.addDimensions(Dimension.newBuilder().setName(DIMENSION_ACTION));
    query.addDimensions(Dimension.newBuilder().setName(DIMENSION_LABEL));

    // configure event filter
    query.mergeDimensionFilter(FilterExpression.newBuilder()
            .setAndGroup(FilterExpressionList.newBuilder()
                    .addExpressions(FilterExpression.newBuilder()
                            .setFilter(Filter.newBuilder()
                                    .setFieldName(DIMENSION_CATEGORY)
                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                            .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                            .setValue(category)
                                    )))
                    .addExpressions(FilterExpression.newBuilder().setFilter(
                            Filter.newBuilder()
                                    .setFieldName(DIMENSION_ACTION)
                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                            .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                            .setValue(action)
                                    )))
                    .addExpressions(FilterExpression.newBuilder().setFilter(Filter.newBuilder()
                            .setFieldName(EVENT_NAME)
                            .setStringFilter(Filter.StringFilter.newBuilder()
                                    .setMatchType(Filter.StringFilter.MatchType.EXACT)
                                    .setValue(EVENT_NAME_DEFAULT_VALUE)
                            )))
            ).build());

    // configure metrics
    query.addMetrics(Metric.newBuilder().setName(METRIC_TOTAL_EVENTS));

    // sort descending
    query.addOrderBys(OrderBy.newBuilder()
            .setDesc(true)
            .setDimension(OrderBy.DimensionOrderBy.newBuilder()
                    .setDimensionName(METRIC_TOTAL_EVENTS)
            )
    );
  }

  @Override
  public String toString() {
    return String.format("[query: propertyId=%s, timeRange=%s, maxResults=%s, category=%s, action=%s]",
            getPropertyId(),
            getTimeRange(),
            getMaxResults(),
            category,
            action);
  }

  @Override
  public List<String> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders) {
    int index = getDimensionColumnIndex(dimensionHeaders, EventQuery.DIMENSION_LABEL);
    List<String> list = new ArrayList<>();
    if (index >= 0) {
      for (Row row : dataEntries) {
        String cell = row.getDimensionValues(index).getValue();
        list.add(cell);
      }
    }
    return list;
  }
}
