package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
public final class OverallPerformanceQuery extends GoogleAnalyticsMapResultQuery {

  static final String DIMENSION_PAGE_PATH_LEVEL1 = "customEvent:pagePathLevel1";

  static final String PATH_FILTER_TEMPLATE = "/%s/";
  static final String DIMENSION_CONTENT_TYPE_FILTER_EXCLUDE = "^(CMAction)";

  private final Content content;

  /**
   * @param content  the navigation content to limit the query to
   * @param settings the GoogleAnalyticsSettings to use (although
   * @see GoogleAnalyticsQuery#GoogleAnalyticsQuery(int, int, int)
   */
  OverallPerformanceQuery(Content content,
                          GoogleAnalyticsSettings settings) {
    super(settings.getPropertyId(),
            settings.getTimeRange(),
            settings.getLimit());
    this.content = content;
  }

  @Override
  protected void customizeQuery(final RunReportRequest.Builder queryBuilder) {
    queryBuilder.addDimensions(Dimension.newBuilder().setName(DIMENSION_TRACKING_DATE));
    queryBuilder.addDimensions(Dimension.newBuilder().setName(DIMENSION_PAGE_PATH_LEVEL1));

    queryBuilder.mergeDimensionFilter(FilterExpression.newBuilder()
            .setAndGroup(FilterExpressionList.newBuilder()
                    .addExpressions(FilterExpression.newBuilder()
                            .setFilter(Filter.newBuilder()
                                    .setFieldName(DIMENSION_PAGE_PATH_LEVEL1)
                                    .setStringFilter(Filter.StringFilter.newBuilder()
                                            .setMatchType(Filter.StringFilter.MatchType.FULL_REGEXP)
                                            .setValue(String.format(PATH_FILTER_TEMPLATE, getContentPath()))
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

  @Override
  public String toString() {
    return String.format("[query: path=%s, propertyId=%s, timeRange=%s]",
            getContentPath(),
            getPropertyId(),
            getTimeRange());
  }

  public String getContentPath() {
    return content == null ? null : content.getString("segment");
  }

  public Map<String, Map<String, Long>> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders) {
    final Map<String, Map<String, Long>> allContentsWithVisits = new HashMap<>();
    int indexTrackingDate = getDimensionColumnIndex(dimensionHeaders, OverallPerformanceQuery.DIMENSION_TRACKING_DATE);
    int indexPagePath = getDimensionColumnIndex(dimensionHeaders, OverallPerformanceQuery.DIMENSION_PAGE_PATH_LEVEL1);
    int indexPageViews = getMetricColumnIndex(metricHeaders, OverallPerformanceQuery.METRIC_PAGEVIEWS);

    String contentId = getContentId(content);

    for (Row row : dataEntries) {
      final String strVisitDate = row.getDimensionValues(indexTrackingDate).getValue();
      final String pagePath = row.getDimensionValues(indexPagePath).getValue();
      final Long pageViews = Long.valueOf(row.getMetricValues(indexPageViews).getValue());

      // the endswith / (e.g. /media/) ensures, that the complete drilldown of all subsequent content will be gathered,
      // otherwise just the single channel (/media) visit  is intended
      // this should already be ensured by the dimensionFilter, just to be sure
      if (pagePath.endsWith("/") && pageViews != null && pageViews > 0) {
        if (allContentsWithVisits.get(contentId) != null) {
          allContentsWithVisits.get(contentId).put(strVisitDate, pageViews);
        } else {
          Map<String, Long> visitsData = new HashMap<>();
          visitsData.put(strVisitDate, pageViews);
          allContentsWithVisits.put(contentId, visitsData);
        }
      }
    }
    return allContentsWithVisits;
  }

  private static String getContentId(Content content) {
    return content == null ? null : Integer.toString(IdHelper.parseContentId(content.getId()));
  }
}
