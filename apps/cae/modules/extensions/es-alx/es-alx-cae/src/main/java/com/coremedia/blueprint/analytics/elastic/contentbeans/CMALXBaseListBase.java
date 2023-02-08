package com.coremedia.blueprint.analytics.elastic.contentbeans;

import com.coremedia.blueprint.base.analytics.elastic.ReportModel;
import com.coremedia.blueprint.base.analytics.elastic.TopNReportModelService;
import com.coremedia.blueprint.cae.contentbeans.CMDynamicListImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Base class for beans of document type "CMALXBaseList".
 */
public abstract class CMALXBaseListBase<V> extends CMDynamicListImpl<V> implements CMALXBaseList<V> {

  private static final Logger LOG = LoggerFactory.getLogger(CMALXBaseListBase.class);

  private TopNReportModelService cmalxBaseListModelServiceFactory;

  /**
   * Reads the tracked objects for this page lists from mongo db.
   * @return list of tracked objects
   * @see ReportModel#getReportData()
   */
  protected final List<String> getTrackedObjects() {
    try {
      final String analyticsProvider = getAnalyticsProvider();
      if(analyticsProvider != null) {
        final List<String> reportData = cmalxBaseListModelServiceFactory.getReportModel(this.getContent(), analyticsProvider).getReportData();
        if(reportData != null) {
          LOG.trace("report data for ({},{}) is {}", new Object[]{this, analyticsProvider, reportData});
          return reportData;
        }
      } else {
        LOG.trace("analytics service provider not set for {} ", this);
      }
    } catch(Exception e) {
      LOG.warn("Ignoring Exception while retrieving tracked objects", e);
    }
    return Collections.emptyList();
  }

  public void setCmalxBaseListModelServiceFactory(TopNReportModelService cmalxBaseListModelServiceFactory) {
    this.cmalxBaseListModelServiceFactory = cmalxBaseListModelServiceFactory;
  }

  @Override
  protected void initialize() {
    super.initialize();
    if (cmalxBaseListModelServiceFactory == null) {
      throw new IllegalStateException("Required property not set: cmalxBaseListModelServiceFactory");
    }
  }
}
