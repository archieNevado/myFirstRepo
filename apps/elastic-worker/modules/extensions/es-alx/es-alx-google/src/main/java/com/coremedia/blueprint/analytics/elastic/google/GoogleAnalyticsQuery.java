package com.coremedia.blueprint.analytics.elastic.google;

import com.coremedia.blueprint.base.analytics.elastic.util.DaysBack;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.cap.content.Content;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.RunReportRequest;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Base class for queries to Google's Data Export API.
 * <p/>
 * Gathers common parameters, that shape a query to Google's Data Export API
 */
@DefaultAnnotation(NonNull.class)
public abstract class GoogleAnalyticsQuery {

  // Slots for Google Analytics' "custom vars" (dimension1 ... n)
  static final String DIMENSION_CONTENT_ID = "customEvent:dimension1";
  static final String DIMENSION_CONTENT_TYPE = "customEvent:dimension2";
  static final String DIMENSION_TRACKING_DATE = "date";
  static final String METRIC_PAGEVIEWS = "screenPageViews"; // unique pageviews are not available in GA4
  static final String DATE_PATTERN = "yyyy-MM-dd";

  // Maximum number of rows to be included in a response allowed by Google. In combination with start-index this can be
  // used to retrieve a subset of elements, or alone to restrict the number of returned elements, starting with the first.
  // The default is set to 10000 if max-results is not supplied.
  static final int DEFAULT_MAX_RESULTS = 10000;

  private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAnalyticsQuery.class);

  static final String KEY_PROPERTY_ID = "propertyId";
  private final int propertyId;
  private final int timeRange;
  private final int maxResults;

  /**
   * @param propertyId ID of the Google Analytics property that shall track the visit
   * @param timeRange  Days from now the query will extend to (e.g. a value of 14 means
   *                   "look back two weeks") Maps to the corresponding document type
   *                   property of 'CMALXBaseList'
   * @param maxResults The number of results this query will be limited to. Maps to the 'CMALXBaseList' document's settings
   *                   property 'limit'. If the property is not set, it defaults to {@link com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil#DEFAULT_LIMIT}
   */
  protected GoogleAnalyticsQuery(int propertyId, int timeRange, int maxResults) {
    assertGreaterThanZero(propertyId, KEY_PROPERTY_ID);

    this.propertyId = propertyId;
    this.timeRange = timeRange > 0 ? timeRange : RetrievalUtil.DEFAULT_TIMERANGE;
    this.maxResults = maxResults > 0 ? maxResults : DEFAULT_MAX_RESULTS;
  }

  private static void assertGreaterThanZero(int i, String paramName) {
    if (i <= 0) {
      throw new IllegalArgumentException("Parameter '" + paramName + "' must not be negative or zero.");
    }
  }

  /**
   * Builds the URL that represents the call to Google's Data Export API.
   * <p/>
   * This method creates a query and invokes the following method before returning its URL:
   * <p/>
   *
   * @return the URL that represents the call to Google's Data Export API
   */
  public final RunReportRequest.Builder getDataQuery() {
    final DaysBack daysBack = new DaysBack(timeRange);
    final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    RunReportRequest.Builder runReportRequest = RunReportRequest.newBuilder()
            .setProperty("properties/" + propertyId)
            .addMetrics(Metric.newBuilder().setName(METRIC_PAGEVIEWS).build())
            .addDateRanges(DateRange.newBuilder().setStartDate(DATE_FORMAT.format(daysBack.getStartDate())).setEndDate(DATE_FORMAT.format(daysBack.getEndDate())));

    // limit results
    // (no matter when set, the corresponding URL parameter
    // will be always put in front of the parameter list)
    if (maxResults > 0) {
      runReportRequest.setLimit(maxResults);
    }

    customizeQuery(runReportRequest);

    return runReportRequest;
  }

  /**
   * Customize the given query
   *
   * @param queryBuilder the queryBuilder to customize
   */
  protected void customizeQuery(RunReportRequest.Builder queryBuilder) {
    // nothing to do
  }

  protected static int getDimensionColumnIndex(List<DimensionHeader> columnHeaders, String columnName) {
    int columnIndex = -1;
    for (int index = 0; index < columnHeaders.size(); index++) {
      if (columnName.equals(columnHeaders.get(index).getName())) {
        columnIndex = index;
      }
    }
    return columnIndex;
  }

  protected static int getMetricColumnIndex(List<MetricHeader> columnHeaders, String columnName) {
    int columnIndex = -1;
    for (int index = 0; index < columnHeaders.size(); index++) {
      if (columnName.equals(columnHeaders.get(index).getName())) {
        columnIndex = index;
      }
    }
    return columnIndex;
  }

  /**
   * @return see superclass' constructor's JavaDoc
   * @see GoogleAnalyticsQuery#GoogleAnalyticsQuery(int, int, int)
   */
  public final int getMaxResults() {
    return maxResults;
  }

  /**
   * @return see superclass' constructor's JavaDoc
   * @see GoogleAnalyticsQuery#GoogleAnalyticsQuery(int, int, int)
   */
  public int getPropertyId() {
    return propertyId;
  }

  /**
   * @return see superclass' constructor's JavaDoc
   * @see GoogleAnalyticsQuery#GoogleAnalyticsQuery(int, int, int)
   */
  public int getTimeRange() {
    return timeRange;
  }

  /**
   * Check whether the given settings can be used to retrieve data. GoogleAnalyticsQuery
   * instances may only be created if this method returns <code>true</code>.
   *
   * @param settings the effective settings used for retrieval
   * @return true if and only if all required properties are available
   */
  static boolean canCreateQuery(GoogleAnalyticsSettings settings) {
    final int propertyId = settings.getPropertyId();
    boolean ok = true;
    if (propertyId < 1) {
      LOGGER.info("Google analytics propertyId id must be greater than zero but is : {}, disabling retrieval", propertyId);
      ok = false;
    }

    Content authFile = settings.getAuthFile();
    if (authFile == null) {
      LOGGER.info("google analytics auth file is not configured, disabling retrieval");
      ok = false;
    }

    return ok;
  }
}
