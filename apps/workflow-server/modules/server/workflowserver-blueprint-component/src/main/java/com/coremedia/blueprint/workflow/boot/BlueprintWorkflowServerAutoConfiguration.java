package com.coremedia.blueprint.workflow.boot;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.translate.workflow.AllMergeablePropertiesPredicateFactory;
import com.coremedia.translate.workflow.CleanInTranslation;
import com.coremedia.translate.workflow.DefaultTranslationWorkflowDerivedContentsStrategy;
import com.coremedia.translate.workflow.TranslationWorkflowDerivedContentsStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration class to be loaded when no customer spring context manager is configured.
 */
@Configuration
@Import({
        WorkflowServerElasticProcessArchiveConfiguration.class,
        WorkflowServerMemoryProcessArchiveConfiguration.class,
})
@ImportResource({
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-sitemodel.xml",
        "classpath:com/coremedia/cap/multisite/multisite-services.xml"
})
@PropertySource("classpath:/com/coremedia/blueprint/base/multisite/bpbase-sitemodel-defaults.properties")
class BlueprintWorkflowServerAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(BlueprintWorkflowServerAutoConfiguration.class);

  @PostConstruct
  void initialize() {
    LOG.info("Configuring blueprint workflow server component.");
  }

  /**
   * A strategy for extracting derived contents from the default translation.xml workflow definition.
   * You can alter this definition and/or add additional strategy beans when using a modified translation workflow.
   */
  @Bean
  DefaultTranslationWorkflowDerivedContentsStrategy defaultTranslationWorkflowDerivedContentsStrategy() {
    DefaultTranslationWorkflowDerivedContentsStrategy strategy = new DefaultTranslationWorkflowDerivedContentsStrategy();
    strategy.setProcessDefinitionName("Translation");
    strategy.setDerivedContentsVariable("derivedContents");
    strategy.setMasterContentObjectsVariable("masterContentObjects");
    return strategy;
  }

  @Bean
  AllMergeablePropertiesPredicateFactory allMergeablePropertiesPredicateFactory() {
    return new AllMergeablePropertiesPredicateFactory();
  }

  @Bean
  CleanInTranslation cleanInTranslation(List<TranslationWorkflowDerivedContentsStrategy> strategies,
                                        ContentRepository contentRepository,
                                        SitesService sitesService) {
    return new CleanInTranslation(strategies, contentRepository, sitesService);
  }

  @Configuration
  @EnableScheduling
  static class BlueprintWorkflowServerSchedulingConfiguration {
    private final CleanInTranslation cleanInTranslation;

    BlueprintWorkflowServerSchedulingConfiguration(CleanInTranslation cleanInTranslation) {
      this.cleanInTranslation = cleanInTranslation;
    }

    /**
     * Regularly clean up "in translation" states left over by aborted workflows.
     */
    @Scheduled(initialDelay = 10_000, fixedDelay = 5_000)
    void doCleanInTranslation() {
      cleanInTranslation.run();
    }

  }

}
