package com.coremedia.blueprint.analytics.elastic.google;


import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.Row;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
public abstract class GoogleAnalyticsMapResultQuery extends GoogleAnalyticsQuery {

  protected GoogleAnalyticsMapResultQuery(int propertyId, int timeRange, int maxResults) {
    super(propertyId, timeRange, maxResults);
  }

  public abstract Map<String, Map<String, Long>> process(List<Row> dataEntries, List<DimensionHeader> dimensionHeaders, List<MetricHeader> metricHeaders);
}
