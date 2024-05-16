package com.coremedia.blueprint.analytics.elastic.google;

import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The @PageViewHistoryQuery encapsulates queries to
 * Google's number of different pages within a session from given custom variables.
 */
@DefaultAnnotation(NonNull.class)
public final class PageViewHistoryQuery extends GoogleAnalyticsMapResultQuery {

  PageViewHistoryQuery(int propertyId,
                       int timeRange,
                       int limit) {
    super(propertyId, timeRange, limit);
  }

  public PageViewHistoryQuery(GoogleAnalyticsSettings googleAnalyticsSettings) {
    this(googleAnalyticsSettings.getPropertyId(),
            googleAnalyticsSettings.getTimeRange(),
            googleAnalyticsSettings.getLimit());
  }

  @Override
  protected void customizeQuery(RunReportRequest.Builder query) {
    query.addDimensions(Dimension.newBuilder().setName(DIMENSION_TRACKING_DATE));
    query.addDimensions(Dimension.newBuilder().setName(DIMENSION_CONTENT_ID));

    query.addOrderBys(OrderBy.newBuilder()
            .setDesc(true)
            .setDimension(OrderBy.DimensionOrderBy.newBuilder()
                    .setDimensionName(DIMENSION_CONTENT_ID)
            )
    );
  }

  @Override
  public String toString() {
    return String.format("[query: propertyId=%s, timeRange=%s]",
            getPropertyId(),
            getTimeRange());
  }

  /**
   * Process the results of the Google's Core Reporting API data response and returns a map of content with their page views and related
   * visit date.
   */
  public Map<String, Map<String, Long>> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders) {

    final Map<String, Map<String, Long>> allContentsWithVisits = new TreeMap<>();
    int contentIdIndex = getDimensionColumnIndex(dimensionHeaders, DIMENSION_CONTENT_ID);
    int trackingDateIndex = getDimensionColumnIndex(dimensionHeaders, DIMENSION_TRACKING_DATE);
    int pageViewsIndex = getMetricColumnIndex(metricHeaders, METRIC_PAGEVIEWS);

    for (Row row : dataEntries) {
      String contentId = row.getDimensionValues(contentIdIndex).getValue();
      String strVisitDate = row.getDimensionValues(trackingDateIndex).getValue();
      Long pageViews = Long.valueOf(row.getMetricValues(pageViewsIndex).getValue());

      if (pageViews != null && pageViews > 0) {
        if (allContentsWithVisits.get(contentId) != null) {
          allContentsWithVisits.get(contentId).put(strVisitDate, pageViews);
        } else {
          Map<String, Long> visitsData = new TreeMap<>();
          visitsData.put(strVisitDate, pageViews);
          allContentsWithVisits.put(contentId, visitsData);
        }
      }
    }
    return allContentsWithVisits;
  }
}
