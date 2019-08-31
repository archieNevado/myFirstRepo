package com.coremedia.blueprint.caas.commerce;

import com.coremedia.blueprint.base.links.RuleProvider;
import com.coremedia.blueprint.base.links.impl.AbsoluteUrlPrefixRuleProvider;
import com.coremedia.blueprint.base.links.impl.ApplicationPropertyReplacerFormatter;
import com.coremedia.blueprint.base.links.impl.RuleUrlPrefixResolver;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.commerce.model.CommerceFacade;
import com.coremedia.blueprint.caas.commerce.wiring.CommerceInstrumentation;
import com.coremedia.blueprint.caas.commerce.wiring.CommerceWiringFactory;
import com.coremedia.caas.search.solr.SolrQueryBuilder;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdScheme;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@ComponentScan({
        "com.coremedia.livecontext.asset.impl"
})
@Import({BaseCommerceServicesAutoConfiguration.class})
public class CommerceConfig {

  @Value("${link.urlPrefixType:''}")
  private String urlPrefixType;

  @Bean
  public RuleUrlPrefixResolver ruleUrlPrefixResolver(List<RuleProvider> ruleProviders) {
    RuleUrlPrefixResolver ruleUrlPrefixResolver = new RuleUrlPrefixResolver();
    ruleUrlPrefixResolver.setRuleProviders(ruleProviders);
    return ruleUrlPrefixResolver;
  }

  @Bean
  public ApplicationPropertyReplacerFormatter applicationPropertyReplacerFormatter() {
    return new ApplicationPropertyReplacerFormatter();
  }

  @Bean
  public AbsoluteUrlPrefixRuleProvider absoluteUrlPrefixRuleProvider(SitesService sitesService,
                                                                     @Qualifier("settingsService") SettingsService settingsService,
                                                                     Cache cache,
                                                                     ApplicationPropertyReplacerFormatter urlPrefixProcessor) {
    AbsoluteUrlPrefixRuleProvider ruleProvider = new AbsoluteUrlPrefixRuleProvider();
    ruleProvider.setSitesService(sitesService);
    ruleProvider.setSettingsService(settingsService);
    ruleProvider.setCache(cache);
    ruleProvider.setUrlPrefixProcessor(urlPrefixProcessor);
    ruleProvider.setUrlPrefixType(urlPrefixType);
    return ruleProvider;
  }

  @Bean
  public CommerceFacade commerceFacade(@Qualifier("commerceConnectionInitializer") CommerceConnectionInitializer commerceConnectionInitializer, SitesService sitesService) {
    return new CommerceFacade(commerceConnectionInitializer, sitesService);
  }

  @Bean
  public CommerceWiringFactory commerceWiringFactory() {
    return new CommerceWiringFactory();
  }

  @Bean
  public CommerceInstrumentation commerceInstrumentation() {
    return new CommerceInstrumentation();
  }

  @Bean("query-root:commerce")
  @Qualifier("queryRoot")
  public Object commerce(CommerceFacade commerceFacade) {
    // A simple Object suffices because all commerce root fields are implemented via @fetch directives
    return new Object();
  }

  @Bean
  public CaasAssetSearchService caasAssetSearchService(@Qualifier("searchResultFactory") SolrSearchResultFactory searchResultFactory,
                                                       ContentRepository contentRepository,
                                                       @Qualifier("settingsService") SettingsService settingsService,
                                                       List<IdScheme> idSchemes,
                                                       @Qualifier("dynamicContentSolrQueryBuilder") SolrQueryBuilder solrQueryBuilder,
                                                       @Value("${caas.search.cache.seconds}") Integer cacheForSeconds) {
    return new CaasAssetSearchService(searchResultFactory, contentRepository, idSchemes, solrQueryBuilder, cacheForSeconds);
  }

}
