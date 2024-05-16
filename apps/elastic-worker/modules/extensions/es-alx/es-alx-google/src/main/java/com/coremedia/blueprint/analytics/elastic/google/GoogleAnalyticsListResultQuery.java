package com.coremedia.blueprint.analytics.elastic.google;


import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.Row;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

@DefaultAnnotation(NonNull.class)
public abstract class GoogleAnalyticsListResultQuery extends GoogleAnalyticsQuery {

  protected GoogleAnalyticsListResultQuery(int propertyId, int timeRange, int maxResults) {
    super(propertyId, timeRange, maxResults);
  }

  public abstract List<String> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders);
}
