package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.links.RuleProvider;
import com.coremedia.blueprint.base.links.impl.AbsoluteUrlPrefixRuleProvider;
import com.coremedia.blueprint.base.links.impl.ApplicationPropertyReplacerFormatter;
import com.coremedia.blueprint.base.links.impl.RuleUrlPrefixResolver;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.internal.PageGridConfiguration;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.adapter.CommerceBeanPageGridAdapterFactory;
import com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade;
import com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapterFactory;
import com.coremedia.blueprint.caas.augmentation.model.AssetFacade;
import com.coremedia.blueprint.caas.augmentation.model.Augmentation;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationFacade;
import com.coremedia.blueprint.caas.augmentation.model.CategoryAugmentation;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.ProductAugmentation;
import com.coremedia.blueprint.caas.search.HeadlessSearchConfiguration;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.caas.search.solr.SolrQueryBuilder;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.caas.web.CaasServiceConfigurationProperties;
import com.coremedia.caas.wiring.ProvidesTypeNameResolver;
import com.coremedia.caas.wiring.TypeNameResolver;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdScheme;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.pagegrid.ContentAugmentedPageGridServiceImpl;
import com.coremedia.livecontext.pagegrid.ContentAugmentedProductPageGridServiceImpl;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceBeanPageGridAdapterFactory.PDP_PAGEGRID_PROPERTY_NAME;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "caas.commerce", name = "enabled", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties({
        CaasAssetSearchServiceConfigProperties.class
})
@ComponentScan({
        "com.coremedia.blueprint.base.livecontext.augmentation",
        "com.coremedia.livecontext.asset.impl",
})
@Import(value = HeadlessSearchConfiguration.class)
@ImportResource(value = "classpath:/META-INF/coremedia/lc-services.xml", reader = ResourceAwareXmlBeanDefinitionReader.class)
@PropertySource("classpath:/META-INF/coremedia/headless-server-ec-augmentation-defaults.properties")
public class HeadlessAugmentationConfiguration {

  public static final String CATEGORY_REF = "CategoryRef";
  public static final String PRODUCT_REF = "ProductRef";

  private static final Set<String> AUGMENTATION_GQL_INTERFACES = Set.of(
          Augmentation.class.getSimpleName(),
          CategoryAugmentation.class.getSimpleName(),
          ProductAugmentation.class.getSimpleName(),
          CommerceRef.class.getSimpleName()
  );

  private static final Map<CommerceBeanType, String> TYPE_RESOLVE_MAP = Map.of(
          BaseCommerceBeanType.CATEGORY, "CategoryImpl",
          BaseCommerceBeanType.PRODUCT, "ProductImpl",
          BaseCommerceBeanType.SKU, "ProductVariantImpl",
          BaseCommerceBeanType.CATALOG, "CatalogImpl"
  );

  @Value("${link.urlPrefixType:''}")
  private String urlPrefixType;

  // Indicate for which interface we provide a type resolver
  @Bean
  public ProvidesTypeNameResolver providesAugmentationTypeNameResolver() {
    return typeName -> AUGMENTATION_GQL_INTERFACES.contains(typeName)
            ? Optional.of(true)
            : Optional.empty();
  }

  @Bean("query-root:commerce")
  @Qualifier("queryRoot")
  public Object commerceAugmentation() {
    return new Object();
  }

  @Bean
  public AugmentationFacade augmentationFacade(AugmentationService categoryAugmentationService,
                                               AugmentationService productAugmentationService,
                                               SitesService sitesService,
                                               CommerceEntityHelper commerceEntityHelper,
                                               CatalogAliasTranslationService catalogAliasTranslationService,
                                               CommerceSiteFinder commerceSiteFinder) {
    return new AugmentationFacade(categoryAugmentationService, productAugmentationService, sitesService,
            commerceEntityHelper, catalogAliasTranslationService, commerceSiteFinder);
  }

  @Bean
  public AssetFacade assetFacade(AssetService assetService,
                                 CommerceEntityHelper commerceEntityHelper) {
    return new AssetFacade(assetService, commerceEntityHelper);
  }

  @Bean
  public TypeNameResolver<Augmentation> augmentationTypeNameResolver() {
    return augmentation -> {
      String simpleClassName = augmentation.getClass().getSimpleName();
      return Optional.of(simpleClassName + "Impl");
    };
  }

  @Bean
  public TypeNameResolver<CommerceRef> commerceRefTypeNameResolver() {
    return commerceRef -> {
      CommerceBeanType type = commerceRef.getType();
      if (type.equals(BaseCommerceBeanType.CATEGORY)){
        return Optional.of(CATEGORY_REF);
      } else if (type.equals(BaseCommerceBeanType.PRODUCT) || type.equals(BaseCommerceBeanType.SKU)){
        return Optional.of(PRODUCT_REF);
      }
      return Optional.of(CommerceRef.class.getSimpleName());
    };
  }

  @Bean
  public CommerceEntityHelper commerceEntityHelper(SitesService siteService,
                                                   CommerceConnectionInitializer commerceConnectionInitializer) {
    return new CommerceEntityHelper(siteService, commerceConnectionInitializer);
  }

  @Bean
  // uses autoconfigured beans exported by lib bpbase-lc-common
  public CommerceBeanPageGridAdapterFactory categoryPageGridAdapter(
          AugmentationService categoryAugmentationService,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation,
          ContentBackedPageGridService categoryContentBackedPageGridService,
          SitesService sitesService, CommerceEntityHelper commerceEntityHelper) {
    return new CommerceBeanPageGridAdapterFactory(
            PAGE_GRID_STRUCT_PROPERTY,
            categoryAugmentationService,
            externalChannelContentTreeRelation,
            categoryContentBackedPageGridService,
            sitesService, commerceEntityHelper);
  }

  @Bean
  // uses autoconfigured beans exported by lib bpbase-lc-common
  public CommerceBeanPageGridAdapterFactory productPageGridAdapter(
          AugmentationService productAugmentationService,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation,
          ContentBackedPageGridService productContentBackedPageGridService,
          SitesService sitesService, CommerceEntityHelper commerceEntityHelper) {
    return new CommerceBeanPageGridAdapterFactory(
            PDP_PAGEGRID_PROPERTY_NAME,
            productAugmentationService,
            externalChannelContentTreeRelation,
            productContentBackedPageGridService,
            sitesService, commerceEntityHelper);
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl categoryContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    return pageGridService;
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl pdpContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    return pageGridService;
  }

  @Bean
  public ContentAugmentedProductPageGridServiceImpl productContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedProductPageGridServiceImpl pageGridService = new ContentAugmentedProductPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    return pageGridService;
  }

  @Bean
  public SolrSearchResultFactory caasAssetSearchServiceSearchResultFactory(@Qualifier("solrClient") SolrClient solrClient,
                                                                           ContentRepository contentRepository,
                                                                           CaasServiceConfigurationProperties caasServiceConfigurationProperties,
                                                                           CaasAssetSearchServiceConfigProperties caasAssetSearchServiceConfigProperties) {
    SolrSearchResultFactory solrSearchResultFactory = new SolrSearchResultFactory(contentRepository, solrClient, caasServiceConfigurationProperties.getSolr().getCollection());
    solrSearchResultFactory.setCacheForSeconds(caasAssetSearchServiceConfigProperties.getCacheSeconds());
    return solrSearchResultFactory;
  }

  @Bean
  public CaasAssetSearchService caasAssetSearchService(CaasAssetSearchServiceConfigProperties caasAssetSearchServiceConfigProperties,
                                                       @Qualifier("caasAssetSearchServiceSearchResultFactory") SolrSearchResultFactory searchResultFactory,
                                                       ContentRepository contentRepository,
                                                       List<IdScheme> idSchemes,
                                                       @Qualifier("dynamicContentSolrQueryBuilder") SolrQueryBuilder solrQueryBuilder) {
    return new CaasAssetSearchService(caasAssetSearchServiceConfigProperties, searchResultFactory, contentRepository, idSchemes, solrQueryBuilder);
  }

  @Bean
  public CommerceSearchFacade commerceSearchHelper(CommerceEntityHelper commerceEntityHelper) {
    return new CommerceSearchFacade(commerceEntityHelper);
  }

  @Bean
  public ProductListAdapterFactory productListAdapter(@Qualifier("settingsService") SettingsService settingsService,
                                                      @Qualifier("sitesService") SitesService sitesService,
                                                      @Qualifier("collectionExtendedItemsAdapter") ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory,
                                                      @Qualifier("augmentationFacade") AugmentationFacade augmentationFacade,
                                                      CommerceSearchFacade commerceSearchFacade) {
    return new ProductListAdapterFactory(settingsService, sitesService, extendedLinkListAdapterFactory, augmentationFacade, commerceSearchFacade);
  }

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
                                                                     SettingsService settingsService,
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
  public TypeNameResolver<CommerceBean> commerceBeanTypeNameResolver() {
    return commerceBean -> {
      CommerceBeanType commerceBeanType = commerceBean.getId().getCommerceBeanType();
      return Optional.ofNullable(TYPE_RESOLVE_MAP.get(commerceBeanType));
    };
  }
}
