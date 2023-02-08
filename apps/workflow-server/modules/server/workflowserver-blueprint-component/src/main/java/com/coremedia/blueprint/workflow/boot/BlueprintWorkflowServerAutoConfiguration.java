package com.coremedia.blueprint.workflow.boot;

import com.coremedia.blueprint.workflow.boot.BlueprintWorkflowServerConfigurationProperties.CleanInTranslationProperties;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.coremedia.translate.TranslatablePredicate;
import com.coremedia.translate.workflow.AllMergeablePropertiesPredicateFactory;
import com.coremedia.translate.workflow.CleanInTranslation;
import com.coremedia.translate.workflow.DefaultAutoMergePredicateFactory;
import com.coremedia.translate.workflow.DefaultAutoMergeStructListMapKey;
import com.coremedia.translate.workflow.DefaultAutoMergeStructListMapKeyFactory;
import com.coremedia.translate.workflow.DefaultTranslationWorkflowDerivedContentsStrategy;
import com.coremedia.translate.workflow.TranslationWorkflowDerivedContentsStrategy;
import com.coremedia.translate.workflow.WorkflowAutoMergeConfigurationProperties;
import com.coremedia.translate.workflow.synchronization.CopyOver;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;

/**
 * Configuration class to be loaded when no customer spring context manager is configured.
 */
@AutoConfiguration
@EnableConfigurationProperties({
        WorkflowAutoMergeConfigurationProperties.class,
        BlueprintWorkflowServerConfigurationProperties.class
})
@Import({
        WorkflowServerElasticProcessArchiveConfiguration.class,
        WorkflowServerMemoryProcessArchiveConfiguration.class,
})
@ImportResource(value = {
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-sitemodel.xml",
        "classpath:com/coremedia/cap/multisite/multisite-services.xml",
        "classpath:/com/coremedia/blueprint/common/multisite/translation-config.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
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
  DefaultAutoMergePredicateFactory defaultAutoMergePredicateFactory(TranslatablePredicate translatablePredicate,
                                                                    WorkflowAutoMergeConfigurationProperties autoMerge) {
    return autoMerge.isTranslatable()
            ? new DefaultAutoMergePredicateFactory(true)
            : new DefaultAutoMergePredicateFactory(translatablePredicate);
  }

  @Bean
  AllMergeablePropertiesPredicateFactory allMergeablePropertiesPredicateFactory() {
    return new AllMergeablePropertiesPredicateFactory();
  }

  /**
   * A factory that returns keys for struct list items, that are used to find corresponding items when merging
   * struct list changes in the {@link com.coremedia.translate.workflow.AutoMergeTranslationAction}.
   *
   * <p>This factory is used by default, if no other bean name is configured in the workflow definition with
   * {@link com.coremedia.translate.workflow.AutoMergeTranslationAction#setAutoMergeStructListMapKeyFactoryName(String)}
   *
   * @param keys auto-wired {@link DefaultAutoMergeStructListMapKey}-s which configure keys for struct lists
   */
  @Bean
  DefaultAutoMergeStructListMapKeyFactory defaultAutoMergeStructListMapKeyFactory(
          Collection<? extends Collection<DefaultAutoMergeStructListMapKey>> keys) {
    List<DefaultAutoMergeStructListMapKey> flattenedKeys = keys.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return new DefaultAutoMergeStructListMapKeyFactory(flattenedKeys);
  }

  /**
   * This bean just collects separate {@link DefaultAutoMergeStructListMapKey} beans and returns them as one list, so
   * that they're injected into {@link #defaultAutoMergeStructListMapKeyFactory}.
   */
  @Bean
  List<DefaultAutoMergeStructListMapKey> additionalDefaultAutoMergeStructListKeys(ObjectProvider<DefaultAutoMergeStructListMapKey> keys) {
    return keys.stream().collect(Collectors.toList());
  }

  @Bean
  List<DefaultAutoMergeStructListMapKey> defaultAutoMergeStructListKeys() {
    return List.of(
            new DefaultAutoMergeStructListMapKey("CMNavigation", "placement.placements", "section"),
            new DefaultAutoMergeStructListMapKey("CMNavigation", "placement.placements.extendedItems", "target"),
            new DefaultAutoMergeStructListMapKey("CMExternalProduct", "pdpPagegrid.placements", "section"),
            new DefaultAutoMergeStructListMapKey("CMExternalProduct", "pdpPagegrid.placements.extendedItems", "target"),
            new DefaultAutoMergeStructListMapKey("CMAbstractCategory", "pdpPagegrid.placements", "section"),
            new DefaultAutoMergeStructListMapKey("CMAbstractCategory", "pdpPagegrid.placements.extendedItems", "target"),
            new DefaultAutoMergeStructListMapKey("CMCollection", "extendedItems.links", "target"),
            new DefaultAutoMergeStructListMapKey("CMTeaser", "targets.links", "target")

    );
  }

  @Bean
  CleanInTranslation cleanInTranslation(List<TranslationWorkflowDerivedContentsStrategy> strategies,
                                        ContentRepository contentRepository,
                                        SitesService sitesService,
                                        @NonNull BlueprintWorkflowServerConfigurationProperties properties) {
    return new CleanInTranslation(strategies, contentRepository, sitesService, properties.getCleanInTranslation().getConfidenceThreshold());
  }

  @Bean
  CopyOver copyOver() {
    return new CopyOver();
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(BlueprintWorkflowServerConfigurationProperties.class)
  @EnableScheduling
  static class BlueprintWorkflowServerSchedulingConfiguration {
    private final CleanInTranslation cleanInTranslation;

    BlueprintWorkflowServerSchedulingConfiguration(CleanInTranslation cleanInTranslation) {
      this.cleanInTranslation = cleanInTranslation;
    }

    /**
     * Regularly clean up "in translation" states left over by aborted workflows.
     * Scheduling can be configured with the following duration properties:
     * <dl>
     *   <dt>{@code workflow.blueprint.clean-in-translation.initial-delay} (default: 10 seconds)</dt>
     *   <dd>The initial delay when to start {@code CleanInTranslation} task. Example value: {@code 20s}.</dd>
     *   <dt>{@code workflow.blueprint.clean-in-translation.fixed-delay} (default: 15 minutes)</dt>
     *   <dd>The delay when to repeat {@code CleanInTranslation} task at a fixed interval. Example value: {@code 10m}.</dd>
     * </dl>
     */
    @Bean
    public ScheduledFuture<?> scheduleCleanInTranslation(@NonNull TaskScheduler scheduler,
                                                         @NonNull BlueprintWorkflowServerConfigurationProperties properties) {
      // Kudos to https://stackoverflow.com/questions/59786883/is-there-way-to-use-scheduled-together-with-duration-string-like-15s-and-5m
      requireNonNull(scheduler, "Required TaskScheduler unavailable.");
      requireNonNull(properties, "Required BlueprintWorkflowServerConfigurationProperties unavailable.");

      CleanInTranslationProperties taskProperties = properties.getCleanInTranslation();

      Duration fixedDelay = taskProperties.getFixedDelay();
      Duration initialDelay = taskProperties.getInitialDelay();
      Instant startTime = now().plus(initialDelay);

      LOG.info("Scheduling CleanInTranslation: startTime: {}, initialDelay: {}, fixedDelay: {}", startTime, initialDelay, fixedDelay);

      return scheduler.scheduleWithFixedDelay(cleanInTranslation, startTime, fixedDelay);
    }
  }

}
