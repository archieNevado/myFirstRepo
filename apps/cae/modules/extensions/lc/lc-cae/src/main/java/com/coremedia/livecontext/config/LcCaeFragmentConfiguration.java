package com.coremedia.livecontext.config;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.cae.action.search.SearchService;
import com.coremedia.blueprint.cae.config.BlueprintHandlersCaeBaseLibConfiguration;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.asset.ProductAssetsHandler;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.fragment.CMSearchFragmentHandler;
import com.coremedia.livecontext.fragment.CategoryFragmentHandler;
import com.coremedia.livecontext.fragment.ExternalPageContextStrategy;
import com.coremedia.livecontext.fragment.ExternalPageFragmentHandler;
import com.coremedia.livecontext.fragment.ExternalRefFragmentHandler;
import com.coremedia.livecontext.fragment.FragmentHandler;
import com.coremedia.livecontext.fragment.FragmentPageHandler;
import com.coremedia.livecontext.fragment.ProductFragmentHandler;
import com.coremedia.livecontext.fragment.pagegrid.CompositePageGridPlacementResolver;
import com.coremedia.livecontext.fragment.pagegrid.DefaultPageGridPlacementResolver;
import com.coremedia.livecontext.fragment.pagegrid.PageGridPlacementResolver;
import com.coremedia.livecontext.fragment.pagegrid.PdpPageGridPlacementResolver;
import com.coremedia.livecontext.fragment.resolver.BreadcrumbExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ContentCapIdExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ContentNumericIdExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ContentNumericIdWithChannelIdExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ContentPathExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ContentSeoSegmentExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.ExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.SearchTermExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.SegmentPathResolver;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cae/uapi-services.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/cae/contentbean-services.xml",
        "classpath:/com/coremedia/cae/dataview-services.xml",
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml",
        "classpath:/com/coremedia/cache/cache-services.xml",
        "classpath:META-INF/coremedia/livecontext-resolver.xml",
        "classpath:META-INF/coremedia/livecontext-handlers.xml",
        "classpath:META-INF/coremedia/context-services.xml",
        "classpath:META-INF/coremedia/livecontext-contentbeans.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)

public class LcCaeFragmentConfiguration {

  @Bean
  public FragmentPageHandler fragmentPageHandler(MimeTypeService mimeTypeService,
                                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                                 DataViewFactory dataViewFactory,
                                                 ContextHelper contextHelper,
                                                 NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                 ContentLinkBuilder contentLinkBuilder,
                                                 ContentBeanFactory contentBeanFactory,
                                                 SitesService sitesService,
                                                 Cache cache,
                                                 @Qualifier("fragmentHandlers") List<FragmentHandler> fragmentHandlers,
                                                 CatalogAliasTranslationService catalogAliasTranslationService) {
    FragmentPageHandler pageHandler = new FragmentPageHandler();

    BlueprintHandlersCaeBaseLibConfiguration.configurePageHandlerBase(pageHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    pageHandler.setFragmentHandlers(fragmentHandlers);
    pageHandler.setDataViewFactory(dataViewFactory);
    pageHandler.setCatalogAliasTranslationService(catalogAliasTranslationService);

    return pageHandler;
  }

  @SuppressWarnings("squid:S00107")
  @Bean
  public ExternalPageFragmentHandler externalPageFragmentHandler(MimeTypeService mimeTypeService,
                                                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                                                 DataViewFactory dataViewFactory,
                                                                 ContextHelper contextHelper,
                                                                 NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                                 ContentLinkBuilder contentLinkBuilder,
                                                                 ContentBeanFactory contentBeanFactory,
                                                                 SitesService sitesService,
                                                                 Cache cache,
                                                                 PageGridPlacementResolver pageGridPlacementResolver,
                                                                 ValidationService<Linkable> validationService,
                                                                 ContextStrategy<String, Navigation> contextStrategy,
                                                                 SettingsService settingsService) {
    ExternalPageFragmentHandler fragmentHandler = new ExternalPageFragmentHandler();

    configureFragmentHandler(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            pageGridPlacementResolver,
            validationService);

    fragmentHandler.setContextStrategy(contextStrategy);
    fragmentHandler.setSettingsService(settingsService);
    fragmentHandler.setNavigationViewName("asNavigation");

    return fragmentHandler;
  }

  @SuppressWarnings("squid:S00107")
  @Bean
  public CMSearchFragmentHandler cmSearchFragmentHandler(MimeTypeService mimeTypeService,
                                                         UrlPathFormattingHelper urlPathFormattingHelper,
                                                         DataViewFactory dataViewFactory,
                                                         ContextHelper contextHelper,
                                                         NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                         ContentLinkBuilder contentLinkBuilder,
                                                         ContentBeanFactory contentBeanFactory,
                                                         SitesService sitesService,
                                                         Cache cache,
                                                         PageGridPlacementResolver pageGridPlacementResolver,
                                                         ValidationService<Linkable> validationService,
                                                         SettingsService settingsService,
                                                         SearchService searchActionService) {
    CMSearchFragmentHandler fragmentHandler = new CMSearchFragmentHandler();

    configureFragmentHandler(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            pageGridPlacementResolver,
            validationService);

    fragmentHandler.setSettingsService(settingsService);
    fragmentHandler.setSearchService(searchActionService);
    fragmentHandler.setMinimalSearchQueryLength(3);

    return fragmentHandler;
  }

  @SuppressWarnings("squid:S00107")
  @Bean
  public ProductFragmentHandler productFragmentHandler(MimeTypeService mimeTypeService,
                                                       UrlPathFormattingHelper urlPathFormattingHelper,
                                                       DataViewFactory dataViewFactory,
                                                       ContextHelper contextHelper,
                                                       NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                       ContentLinkBuilder contentLinkBuilder,
                                                       ContentBeanFactory contentBeanFactory,
                                                       SitesService sitesService,
                                                       Cache cache,
                                                       PageGridPlacementResolver pageGridPlacementResolver,
                                                       ValidationService<Linkable> validationService,
                                                       ResolveContextStrategy resolveLivecontextContextStrategy,
                                                       PageGridPlacementResolver pdpPageGridPlacementResolver) {
    ProductFragmentHandler fragmentHandler = new ProductFragmentHandler();

    configureFragmentHandler(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            pageGridPlacementResolver,
            validationService);

    fragmentHandler.setContextStrategy(resolveLivecontextContextStrategy);
    fragmentHandler.setPageGridPlacementResolver(pdpPageGridPlacementResolver);

    return fragmentHandler;
  }

  @Bean
  public ProductAssetsHandler productAssetsHandler(MimeTypeService mimeTypeService,
                                                   UrlPathFormattingHelper urlPathFormattingHelper,
                                                   DataViewFactory dataViewFactory,
                                                   ContextHelper contextHelper,
                                                   NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                   ContentLinkBuilder contentLinkBuilder,
                                                   ContentBeanFactory contentBeanFactory,
                                                   SitesService sitesService,
                                                   Cache cache) {
    ProductAssetsHandler assetsHandler = new ProductAssetsHandler();

    BlueprintHandlersCaeBaseLibConfiguration.configurePageHandlerBase(assetsHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    return assetsHandler;
  }

  @SuppressWarnings("squid:S00107")
  @Bean
  public CategoryFragmentHandler categoryFragmentHandler(MimeTypeService mimeTypeService,
                                                         UrlPathFormattingHelper urlPathFormattingHelper,
                                                         DataViewFactory dataViewFactory,
                                                         ContextHelper contextHelper,
                                                         NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                         ContentLinkBuilder contentLinkBuilder,
                                                         ContentBeanFactory contentBeanFactory,
                                                         SitesService sitesService,
                                                         Cache cache,
                                                         PageGridPlacementResolver pageGridPlacementResolver,
                                                         ValidationService<Linkable> validationService,
                                                         ResolveContextStrategy resolveLivecontextContextStrategy) {
    CategoryFragmentHandler fragmentHandler = new CategoryFragmentHandler();

    configureFragmentHandler(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            pageGridPlacementResolver,
            validationService);

    fragmentHandler.setContextStrategy(resolveLivecontextContextStrategy);

    return fragmentHandler;
  }

  @SuppressWarnings("squid:S00107")
  @Bean
  public ExternalRefFragmentHandler externalRefFragmentHandler(MimeTypeService mimeTypeService,
                                                               UrlPathFormattingHelper urlPathFormattingHelper,
                                                               DataViewFactory dataViewFactory,
                                                               ContextHelper contextHelper,
                                                               NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                               ContentLinkBuilder contentLinkBuilder,
                                                               ContentBeanFactory contentBeanFactory,
                                                               SitesService sitesService,
                                                               Cache cache,
                                                               PageGridPlacementResolver pageGridPlacementResolver,
                                                               ValidationService<Linkable> validationService,
                                                               List<ExternalReferenceResolver> fragmentExternalRefResolvers) {
    ExternalRefFragmentHandler fragmentHandler = new ExternalRefFragmentHandler();

    configureFragmentHandler(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            pageGridPlacementResolver,
            validationService);

    fragmentHandler.setExternalReferenceResolvers(fragmentExternalRefResolvers);

    return fragmentHandler;
  }

  /**
   * Duplicates abstract bean fragmentHandler in xml config file.
   */
  @SuppressWarnings("squid:S00107")
  public static void configureFragmentHandler(FragmentHandler fragmentHandler,
                                              MimeTypeService mimeTypeService,
                                              UrlPathFormattingHelper urlPathFormattingHelper,
                                              DataViewFactory dataViewFactory,
                                              ContextHelper contextHelper,
                                              NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                              ContentLinkBuilder contentLinkBuilder,
                                              ContentBeanFactory contentBeanFactory,
                                              SitesService sitesService,
                                              Cache cache,
                                              PageGridPlacementResolver pageGridPlacementResolver,
                                              ValidationService<Linkable> validationService) {
    BlueprintHandlersCaeBaseLibConfiguration.configurePageHandlerBase(fragmentHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    fragmentHandler.setPageGridPlacementResolver(pageGridPlacementResolver);
    fragmentHandler.setValidationService(validationService);
  }

  /**
   * This list contains all handlers that are used for fragment calls.
   */
  @Bean
  public List<FragmentHandler> fragmentHandlers(CMSearchFragmentHandler cmSearchFragmentHandler,
                                                ExternalRefFragmentHandler externalRefFragmentHandler,
                                                ExternalPageFragmentHandler externalPageFragmentHandler,
                                                ProductFragmentHandler productFragmentHandler,
                                                CategoryFragmentHandler categoryFragmentHandler) {
    return new ArrayList<>(List.of(cmSearchFragmentHandler,
            externalRefFragmentHandler,
            externalPageFragmentHandler,
            productFragmentHandler,
            categoryFragmentHandler));
  }

  /**
   * This list of predicates will be traversed by the dynamicIncludeRenderDecorator/Provider in order to decide
   * whether a DynamicInclude should be used for a given Bean/View combination.
   */
  @Bean
  public List<ExternalReferenceResolver> fragmentExternalRefResolvers(ContentCapIdExternalReferenceResolver contentCapIdExternalReferenceResolver,
                                                                      ContentPathExternalReferenceResolver contentPathExternalReferenceResolver,
                                                                      ContentNumericIdExternalReferenceResolver contentNumericIdExternalReferenceResolver,
                                                                      ContentNumericIdWithChannelIdExternalReferenceResolver contentNumericIdWithChannelIdExternalReferenceResolver,
                                                                      ContentSeoSegmentExternalReferenceResolver contentSeoSegmentExternalReferenceResolver,
                                                                      SearchTermExternalReferenceResolver searchTermExternalReferenceResolver,
                                                                      SegmentPathResolver segmentPathResolver,
                                                                      BreadcrumbExternalReferenceResolver breadcrumbExternalReferenceResolver) {
    return new ArrayList<>(List.of(contentCapIdExternalReferenceResolver,
            contentPathExternalReferenceResolver,
            contentNumericIdExternalReferenceResolver,
            contentNumericIdWithChannelIdExternalReferenceResolver,
            contentSeoSegmentExternalReferenceResolver,
            searchTermExternalReferenceResolver,
            segmentPathResolver,
            breadcrumbExternalReferenceResolver));
  }

  @Bean
  public DefaultPageGridPlacementResolver defaultPageGridPlacementResolver() {
    return new DefaultPageGridPlacementResolver();
  }

  @Bean
  public PdpPageGridPlacementResolver pdpPageGridPlacementResolver() {
    return new PdpPageGridPlacementResolver();
  }

  @Bean
  public CompositePageGridPlacementResolver pageGridPlacementResolver(DefaultPageGridPlacementResolver defaultPageGridPlacementResolver,
                                                                      DataViewFactory dataViewFactory) {
    CompositePageGridPlacementResolver placementResolver = new CompositePageGridPlacementResolver();

    placementResolver.setResolvers(List.of(defaultPageGridPlacementResolver));
    placementResolver.setDataViewFactory(dataViewFactory);

    return placementResolver;
  }

  @Bean
  public ExternalPageContextStrategy externalPageContextStrategy(Cache cache,
                                                                 SitesService sitesService,
                                                                 ContentBeanFactory contentBeanFactory,
                                                                 TreeRelation<Content> childrenTreeRelation) {
    ExternalPageContextStrategy contextStrategy = new ExternalPageContextStrategy();

    contextStrategy.setCache(cache);
    contextStrategy.setSitesService(sitesService);
    contextStrategy.setContentBeanFactory(contentBeanFactory);
    contextStrategy.setTreeRelation(childrenTreeRelation);

    return contextStrategy;
  }

}
