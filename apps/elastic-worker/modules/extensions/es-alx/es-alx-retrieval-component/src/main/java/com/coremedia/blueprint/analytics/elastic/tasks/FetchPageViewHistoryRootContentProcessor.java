package com.coremedia.blueprint.analytics.elastic.tasks;

import com.coremedia.blueprint.analytics.elastic.retrieval.AnalyticsServiceProvider;
import com.coremedia.blueprint.base.analytics.elastic.PageViewReportModelService;
import com.coremedia.blueprint.base.analytics.elastic.PageViewTaskReportModelService;
import com.coremedia.blueprint.base.analytics.elastic.ReportModel;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.blueprint.base.analytics.elastic.validation.ResultItemValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.coremedia.blueprint.analytics.elastic.tasks.FetchPublicationsHistoryTask.PUBLICATION_HISTORY_DOCUMENT_TYPE;
import static com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil.needsUpdate;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

@Named
class FetchPageViewHistoryRootContentProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(FetchPageViewHistoryRootContentProcessor.class);
  private static final String INTERVAL = "pageViewHistoryInterval";

  private final PageViewReportModelService modelService;
  private final PageViewTaskReportModelService taskReportModelService;
  private final ContentRepository contentRepository;
  private final ResultItemValidationService resultItemValidatorService;

  @Inject
  FetchPageViewHistoryRootContentProcessor(PageViewReportModelService modelService,
                                           PageViewTaskReportModelService taskReportModelService,
                                           ContentRepository contentRepository,
                                           ResultItemValidationService resultItemValidatorService) {
    this.modelService = modelService;
    this.contentRepository = contentRepository;
    this.taskReportModelService = taskReportModelService;
    this.resultItemValidatorService = resultItemValidatorService;
  }

  void processRootContent(@NonNull Content root,
                          @NonNull Map<String, Object> serviceProviderSettings,
                          @NonNull AnalyticsServiceProvider analyticsServiceProvider) {
    final String serviceKey = analyticsServiceProvider.getServiceKey();
    LOG.trace("Processing analytics provider {} for root navigation content {}", serviceKey, root);
    int interval = RetrievalUtil.getInterval(serviceProviderSettings, INTERVAL);
    if (interval <= 0) {
      LOG.debug("Retrieval for content ({}, {}) is disabled. Set setting '{}' greater than 0 to enable retrieval",
              root, serviceKey, interval);
      return;
    }
    ReportModel taskModelForRoot = taskReportModelService.getReportModel(root, serviceKey);
    Date now = new Date();

    if (!serviceProviderSettings.equals(taskModelForRoot.getSettings()) || needsUpdate(taskModelForRoot.getLastSaved(), now.getTime(), interval)) {
      Map<String, Map<String, Long>> data = getValidatedPageViews(analyticsServiceProvider, root, serviceProviderSettings);
      LOG.info("Updating {} page views for ('{}' / '{}')", data.size(), root, analyticsServiceProvider.getServiceKey());
      final Collection<ReportModel> reportModels = data.entrySet().stream()
              .map(entry -> createReportModel(serviceKey, now, entry.getKey(), entry.getValue()))
              .collect(Collectors.toList());
      modelService.saveAll(reportModels);
      taskModelForRoot.setSettings(serviceProviderSettings);
      savedAt(taskModelForRoot, now);
      taskModelForRoot.setReportData(new LinkedList<>(data.keySet()));
      taskModelForRoot.save();
    } else {
      LOG.debug("Report data {} for content ({}, {}) is still fresh", taskModelForRoot, root, serviceKey);
    }
  }

  @NonNull
  private ReportModel createReportModel(String serviceKey, Date now, String key, Map<String, Long> value) {
    Content content = contentRepository.getContent(key);
    ReportModel reportModel = modelService.getReportModel(content, serviceKey);
    reportModel.setReportMap(value);
    savedAt(reportModel, now);
    return reportModel;
  }

  private Map<String, Map<String, Long>> getValidatedPageViews(AnalyticsServiceProvider analyticsServiceProvider, Content root, Map<String, Object> serviceProviderSettings) {
    try {
      Map<String, Map<String, Long>> data = analyticsServiceProvider.fetchPageViews(root, serviceProviderSettings);
      final Map<String, Map<String, Long>> validatedPageViews = validatePageViews(data);
      LOG.info("Got {} valid page views for ('{}' / '{}') from {} raw entries", validatedPageViews.size(), root, analyticsServiceProvider.getServiceKey(), data.size());
      return validatedPageViews;
    } catch (Exception e) {
      LOG.warn(format("Could not fetch analytics data of provider %s: %s", analyticsServiceProvider.getServiceKey(), e.getMessage()), e);
      return emptyMap();
    }
  }

  private Map<String, Map<String, Long>> validatePageViews(Map<String, Map<String, Long>> data) {
    Iterable<String> validContentIds = resultItemValidatorService.filterValidResultItems(data.keySet(), PUBLICATION_HISTORY_DOCUMENT_TYPE);
    List<String> validContentIdsList = StreamSupport.stream(validContentIds.spliterator(), false).collect(Collectors.toList());
    return data.entrySet().stream()
            .filter(entry -> validContentIdsList.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  void savedAt(ReportModel reportModel, Date now) {
    // this property is used for ttl feature
    reportModel.setLastSavedDate(now);
    reportModel.setLastSaved(now.getTime());
  }
}
