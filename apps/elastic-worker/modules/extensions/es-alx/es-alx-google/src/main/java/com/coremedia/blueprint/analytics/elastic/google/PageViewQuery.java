package com.coremedia.blueprint.analytics.elastic.google;

import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates queries to Google's Universal Data Export API</a>
 * that ask for pageviews.
 */
@DefaultAnnotation(NonNull.class)
public final class PageViewQuery extends GoogleAnalyticsListResultQuery {

  static final String DIMENSION_TITLE = "pageTitle";
  static final String DIMENSION_PATH = "pagePath";

  /**
   * Constructor
   *
   * @param propertyId  see superclass' constructor's JavaDoc
   * @param timeRange  see superclass' constructor's JavaDoc
   * @param maxResults see superclass' constructor's JavaDoc
   * @see GoogleAnalyticsQuery#GoogleAnalyticsQuery(int, int, int)
   */
  public PageViewQuery(int propertyId,
                       int timeRange,
                       int maxResults) {
    super(propertyId, timeRange, maxResults);
  }

  public PageViewQuery(GoogleAnalyticsSettings googleAnalyticsSettings) {
    super(googleAnalyticsSettings.getPropertyId(),
            googleAnalyticsSettings.getTimeRange(),
            googleAnalyticsSettings.getLimit());
  }

  @Override
  protected void customizeQuery(final RunReportRequest.Builder queryBuilder) {
    // configure dimensions
    queryBuilder.addDimensions(Dimension.newBuilder().setName(DIMENSION_CONTENT_ID));

    // sort descending
    queryBuilder.addOrderBys(OrderBy.newBuilder()
            .setDesc(true)
            .setMetric(OrderBy.MetricOrderBy.newBuilder()
                    .setMetricName(METRIC_PAGEVIEWS)
            )
    );
  }

  @Override
  public String toString() {
    return String.format("[query: propertyId=%s, timeRange=%s, maxResults=%s]",
            getPropertyId(),
            getTimeRange(),
            getMaxResults());
  }

  @Override
  public List<String> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders) {
    int indexContentId = getDimensionColumnIndex(dimensionHeaders, GoogleAnalyticsQuery.DIMENSION_CONTENT_ID);

    List<String> list = new ArrayList<>();
    if (indexContentId >= 0) {
      for (Row row : dataEntries) {
        String cell = row.getDimensionValues(indexContentId).getValue();
        list.add(cell);
      }
    }
    return list;
  }
}
