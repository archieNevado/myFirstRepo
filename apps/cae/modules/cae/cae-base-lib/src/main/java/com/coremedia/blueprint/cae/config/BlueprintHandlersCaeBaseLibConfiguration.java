package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.navigation.context.finder.TopicpageContextFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.search.PageSearchActionHandler;
import com.coremedia.blueprint.cae.action.search.SearchService;
import com.coremedia.blueprint.cae.action.webflow.BlueprintFlowUrlHandler;
import com.coremedia.blueprint.cae.handlers.BlobHandler;
import com.coremedia.blueprint.cae.handlers.CapBlobHandler;
import com.coremedia.blueprint.cae.handlers.CodeResourceHandler;
import com.coremedia.blueprint.cae.handlers.DefaultPageActionHandler;
import com.coremedia.blueprint.cae.handlers.DefaultPageHandler;
import com.coremedia.blueprint.cae.handlers.ExternalLinkHandler;
import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.cae.handlers.NavigationResolver;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.handlers.PageActionHandler;
import com.coremedia.blueprint.cae.handlers.PageHandler;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.handlers.PageRssHandler;
import com.coremedia.blueprint.cae.handlers.PaginationHandler;
import com.coremedia.blueprint.cae.handlers.RobotsHandler;
import com.coremedia.blueprint.cae.handlers.StaticUrlHandler;
import com.coremedia.blueprint.cae.handlers.ThemeHandler;
import com.coremedia.blueprint.cae.handlers.TransformedBlobHandler;
import com.coremedia.blueprint.cae.util.DefaultSecureHashCodeGeneratorStrategy;
import com.coremedia.blueprint.cae.util.DefaultToMd5MigrationSecureHashCodeGeneratorStrategy;
import com.coremedia.blueprint.cae.web.ContentValidityInterceptor;
import com.coremedia.blueprint.cae.web.ExposeCurrentNavigationInterceptor;
import com.coremedia.blueprint.cae.web.i18n.ResourceBundleInterceptor;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.common.datevalidation.ValidUntilConsumer;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cache.Cache;
import com.coremedia.cae.webflow.FlowRunner;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.cap.transform.TransformImageServiceConfiguration;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.id.IdServicesConfiguration;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.mimetype.MimeTypeServiceConfiguration;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.view.dynamic.MD5SecureHashCodeGeneratorStrategy;
import com.coremedia.objectserver.web.SecureHashCodeGeneratorStrategy;
import com.coremedia.objectserver.web.cachecontrol.CacheControlStrategy;
import com.coremedia.objectserver.web.config.CaeHandlerServicesConfiguration;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.customizer.CustomizerConfiguration;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.common.datevalidation.ValidUntilConsumer.DISABLE_VALIDITY_RECORDING_ATTRIBUTE;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * LinkSchemes and Controllers.
 */
@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cae/webflow/webflow-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-links-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-links-postprocessors.xml",
        "classpath:/framework/spring/blueprint-services.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        BlueprintSearchCaeBaseLibConfiguration.class,
        CaeHandlerServicesConfiguration.class,
        CustomizerConfiguration.class,
        IdServicesConfiguration.class,
        MimeTypeServiceConfiguration.class,
        TransformImageServiceConfiguration.class,
})
@ComponentScan(
        basePackages = {
                "com.coremedia.blueprint.coderesources"
        },
        lazyInit = true
)
public class BlueprintHandlersCaeBaseLibConfiguration {

  /**
   * "Simple Controllers" (@RequestMapping handlers) are identified by their "name".
   * <p>
   * e.g.
   * &lt;bean name="/content" id="contentViewController" class="com.coremedia.objectserver.web.ContentViewController"/&gt;
   * or
   * {@literal @}Named("/content")
   */
  @Bean
  public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping(ApplicationContext context) {
    BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();

    mapping.setInterceptors(context.getBean("handlerInterceptors", List.class).toArray());
    // make sure that these controller's precedence is higher than the (new) RequestMappingHandlerMapping since
    // the Page Handler is responsible for all URLs
    mapping.setOrder(42);

    return mapping;
  }

  //--- resources (handler/link scheme)

  /**
   * Handles HTML page resources.
   */
  @SuppressWarnings("squid:S00107")
  @Bean
  public PageHandler pageHandler(MimeTypeService mimeTypeService,
                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                 DataViewFactory dataViewFactory,
                                 ContextHelper contextHelper,
                                 NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                 ContentLinkBuilder contentLinkBuilder,
                                 ContentBeanFactory contentBeanFactory,
                                 SitesService sitesService,
                                 Cache cache,
                                 NavigationResolver navigationResolver,
                                 TopicpageContextFinder uapiTopicpageContextFinder,
                                 SettingsService settingsService,
                                 @Qualifier("pageHandlerViewToBean") Map<String, Class> pageHandlerViewToBean) {
    PageHandler pageHandler = new PageHandler();

    configureDefaultPageHandler(pageHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            navigationResolver,
            uapiTopicpageContextFinder,
            settingsService,
            pageHandlerViewToBean);

    return pageHandler;
  }

  /**
   * Handles page actions.
   */
  @SuppressWarnings("deprecation")
  @Bean
  public PageActionHandler actionHandler(MimeTypeService mimeTypeService,
                                         UrlPathFormattingHelper urlPathFormattingHelper,
                                         DataViewFactory dataViewFactory,
                                         ContextHelper contextHelper,
                                         NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                         ContentLinkBuilder contentLinkBuilder,
                                         ContentBeanFactory contentBeanFactory,
                                         SitesService sitesService,
                                         Cache cache,
                                         FlowRunner flowRunner,
                                         ResourceBundleInterceptor pageResourceBundlesInterceptor) {
    PageActionHandler actionHandler = new PageActionHandler();

    configureDefaultPageActionHandler(actionHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache,
            flowRunner,
            pageResourceBundlesInterceptor);

    return actionHandler;
  }

  /**
   * Maps view names to interfaces, available as settings backed beans named "viewBean"
   * in the templates. To be populated by customizers.
   */
  @Bean
  public Map<String, Class> pageHandlerViewToBean() {
    return new HashMap<>();
  }

  @Bean
  public NavigationResolver navigationResolver(TopicpageContextFinder uapiTopicpageContextFinder,
                                               NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                               ContextHelper contextHelper,
                                               UrlPathFormattingHelper urlPathFormattingHelper) {
    NavigationResolver navigationResolver = new NavigationResolver();

    navigationResolver.setTopicPageContextFinder(uapiTopicpageContextFinder);
    navigationResolver.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    navigationResolver.setContextHelper(contextHelper);
    navigationResolver.setUrlPathFormattingHelper(urlPathFormattingHelper);

    return navigationResolver;
  }

  /**
   * Handles RSS feed resources.
   */
  @SuppressWarnings("squid:S00107")
  @Bean
  public PageRssHandler pageRssHandler(MimeTypeService mimeTypeService,
                                       UrlPathFormattingHelper urlPathFormattingHelper,
                                       DataViewFactory dataViewFactory,
                                       ContextHelper contextHelper,
                                       NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                       ContentLinkBuilder contentLinkBuilder,
                                       ContentBeanFactory contentBeanFactory,
                                       SitesService sitesService,
                                       Cache cache) {
    PageRssHandler pageRssHandler = new PageRssHandler();

    configurePageHandlerBase(pageRssHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    return pageRssHandler;
  }

  /**
   * Handles External Link resources.
   */
  @Bean
  public ExternalLinkHandler externalLinkHandler(MimeTypeService mimeTypeService,
                                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                                 DataViewFactory dataViewFactory,
                                                 ContentLinkBuilder contentLinkBuilder) {
    ExternalLinkHandler externalLinkHandler = new ExternalLinkHandler();

    configureHandlerBase(externalLinkHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    return externalLinkHandler;
  }

  /**
   * Handles the Search action.
   */
  @SuppressWarnings("squid:S00107")
  @Bean
  public PageSearchActionHandler pageSearchActionHandler(ContextHelper contextHelper,
                                                         MimeTypeService mimeTypeService,
                                                         UrlPathFormattingHelper urlPathFormattingHelper,
                                                         DataViewFactory dataViewFactory,
                                                         NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                         ContentLinkBuilder contentLinkBuilder,
                                                         ContentBeanFactory contentBeanFactory,
                                                         SitesService sitesService,
                                                         Cache cache,
                                                         SearchService searchService,
                                                         SettingsService settingsService) {
    PageSearchActionHandler actionHandler = new PageSearchActionHandler();
    actionHandler.setMinimalSearchQueryLength(3);
    actionHandler.setPermittedLinkParameterNames(List.of("query", "pageNum", "channelId", "docType", "key", "view", "sortByDate", "facetFilters"));

    configurePageHandlerBase(actionHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    actionHandler.setSearchService(searchService);
    actionHandler.setSettingsService(settingsService);
    return actionHandler;
  }

  /**
   * Handles standard blobs.
   */
  @Bean
  public BlobHandler blobHandler(ValidationService<ContentBean> validationService,
                                 MimeTypeService mimeTypeService,
                                 SettingsService settingsService) {
    return new BlobHandler(validationService, mimeTypeService, settingsService);
  }

  /**
   * Handles scaled/transformed images/blobs.
   */
  @Bean
  public TransformedBlobHandler transformedBlobHandler(MimeTypeService mimeTypeService,
                                                       UrlPathFormattingHelper urlPathFormattingHelper,
                                                       DataViewFactory dataViewFactory,
                                                       ContentLinkBuilder contentLinkBuilder,
                                                       ValidationService<ContentBean> validationService,
                                                       ObjectProvider<SecureHashCodeGeneratorStrategy> secureHashCodeGeneratorStrategy,
                                                       TransformImageService transformImageService) {
    TransformedBlobHandler transformedBlobHandler = new TransformedBlobHandler();

    configureHandlerBase(transformedBlobHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    transformedBlobHandler.setValidationService(validationService);
    //The secureHashCodeGeneratorStrategy is provided by auto configuration. @ConditionOnBean does not work here.
    secureHashCodeGeneratorStrategy.ifAvailable(transformedBlobHandler::setSecureHashCodeGeneratorStrategy);
    transformedBlobHandler.setTransformImageService(transformImageService);

    return transformedBlobHandler;
  }

  /**
   * Handles standard images/blobs referenced by a cap object.
   */
  @Bean
  public CapBlobHandler capBlobHandler(MimeTypeService mimeTypeService,
                                       UrlPathFormattingHelper urlPathFormattingHelper,
                                       DataViewFactory dataViewFactory,
                                       ContentLinkBuilder contentLinkBuilder,
                                       ValidationService<ContentBean> validationService,
                                       ThemeService themeService,
                                       ContentBeanFactory contentBeanFactory) {
    CapBlobHandler capBlobHandler = new CapBlobHandler();

    configureHandlerBase(capBlobHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    capBlobHandler.setValidationService(validationService);
    capBlobHandler.setThemeService(themeService);
    capBlobHandler.setContentBeanFactory(contentBeanFactory);

    return capBlobHandler;
  }

  /**
   * Handles CSS and JavaScript resources.
   */
  @SuppressWarnings("squid:S00107")
  @Bean
  public CodeResourceHandler codeResourceHandler(MimeTypeService mimeTypeService,
                                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                                 DataViewFactory dataViewFactory,
                                                 ContentLinkBuilder contentLinkBuilder,
                                                 Cache cache,
                                                 CapConnection capConnection,
                                                 ContentBeanFactory contentBeanFactory,
                                                 SitesService sitesService,
                                                 DeliveryConfigurationProperties deliveryConfigurationProperties) {
    CodeResourceHandler codeResourceHandler = new CodeResourceHandler();

    configureHandlerBase(codeResourceHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    codeResourceHandler.setDeveloperModeEnabled(deliveryConfigurationProperties.isDeveloperMode());
    codeResourceHandler.setCapConnection(capConnection);
    codeResourceHandler.setLocalResourcesEnabled(deliveryConfigurationProperties.isLocalResources());
    codeResourceHandler.setCache(cache);
    codeResourceHandler.setContentBeanFactory(contentBeanFactory);
    codeResourceHandler.setSitesService(sitesService);

    return codeResourceHandler;
  }

  @Bean
  public ThemeHandler themeHandler(MimeTypeService mimeTypeService,
                                   UrlPathFormattingHelper urlPathFormattingHelper,
                                   DataViewFactory dataViewFactory,
                                   ContentLinkBuilder contentLinkBuilder) {
    ThemeHandler themeHandler = new ThemeHandler();

    configureHandlerBase(themeHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    return themeHandler;
  }

  /**
   * Handles Strings.
   */
  @Bean
  public StaticUrlHandler staticUrlHandler(MimeTypeService mimeTypeService,
                                           UrlPathFormattingHelper urlPathFormattingHelper,
                                           DataViewFactory dataViewFactory,
                                           ContentLinkBuilder contentLinkBuilder) {
    StaticUrlHandler staticUrlHandler = new StaticUrlHandler();
    staticUrlHandler.setPermittedLinkParameterNames(List.of("width", "height", "imageId"));

    configureHandlerBase(staticUrlHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    return staticUrlHandler;
  }

  /**
   * Handler that generates a configured robots.txt.
   */
  @Bean
  public RobotsHandler robotsHandler(MimeTypeService mimeTypeService,
                                     UrlPathFormattingHelper urlPathFormattingHelper,
                                     DataViewFactory dataViewFactory,
                                     ContentLinkBuilder contentLinkBuilder,
                                     NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                     SettingsService settingsService,
                                     SitesService sitesService) {
    RobotsHandler robotsHandler = new RobotsHandler();

    configureHandlerBase(robotsHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    robotsHandler.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    robotsHandler.setSettingsService(settingsService);
    robotsHandler.setSitesService(sitesService);

    return robotsHandler;
  }

  /**
   * Handler for pagination URLs for Container.
   * (Currently supported only by CMQueryListImpl)
   */
  @SuppressWarnings("squid:S00107")
  @Bean
  public PaginationHandler paginationHandler(ContextHelper contextHelper,
                                             MimeTypeService mimeTypeService,
                                             UrlPathFormattingHelper urlPathFormattingHelper,
                                             DataViewFactory dataViewFactory,
                                             NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                             ContentLinkBuilder contentLinkBuilder,
                                             ContentBeanFactory contentBeanFactory,
                                             SitesService sitesService,
                                             Cache cache) {
    PaginationHandler paginationHandler = new PaginationHandler();
    paginationHandler.setPermittedLinkParameterNames(List.of("view", "pageNum"));

    configurePageHandlerBase(paginationHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    return paginationHandler;
  }

  /**
   * Duplicates abstract bean handlerBase in xml config file.
   */
  public static void configureHandlerBase(HandlerBase handlerBase,
                                          MimeTypeService mimeTypeService,
                                          UrlPathFormattingHelper urlPathFormattingHelper,
                                          DataViewFactory dataViewFactory,
                                          ContentLinkBuilder contentLinkBuilder) {
    handlerBase.setMimeTypeService(mimeTypeService);
    handlerBase.setUrlPathFormattingHelper(urlPathFormattingHelper);
    handlerBase.setDataViewFactory(dataViewFactory);
    handlerBase.setContentLinkBuilder(contentLinkBuilder);
  }

  /**
   * Duplicates abstract bean pageHandlerBase in xml config file.
   */
  @SuppressWarnings("squid:S00107")
  public static void configurePageHandlerBase(PageHandlerBase pageHandlerBase,
                                              MimeTypeService mimeTypeService,
                                              UrlPathFormattingHelper urlPathFormattingHelper,
                                              DataViewFactory dataViewFactory,
                                              ContextHelper contextHelper,
                                              NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                              ContentLinkBuilder contentLinkBuilder,
                                              ContentBeanFactory contentBeanFactory,
                                              SitesService sitesService,
                                              Cache cache) {
    configureHandlerBase(pageHandlerBase,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    pageHandlerBase.setContextHelper(contextHelper);
    pageHandlerBase.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    pageHandlerBase.setContentBeanFactory(contentBeanFactory);
    pageHandlerBase.setSitesService(sitesService);
    pageHandlerBase.setCache(cache);
  }

  /**
   * Handles HTML page resources.
   * Duplicates abstract bean defaultHandlerBase in xml config file.
   */
  @SuppressWarnings("squid:S00107")
  public static void configureDefaultPageHandler(DefaultPageHandler defaultPageHandler,
                                                 MimeTypeService mimeTypeService,
                                                 UrlPathFormattingHelper urlPathFormattingHelper,
                                                 DataViewFactory dataViewFactory,
                                                 ContextHelper contextHelper,
                                                 NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                 ContentLinkBuilder contentLinkBuilder,
                                                 ContentBeanFactory contentBeanFactory,
                                                 SitesService sitesService,
                                                 Cache cache,
                                                 NavigationResolver navigationResolver,
                                                 TopicpageContextFinder uapiTopicpageContextFinder,
                                                 SettingsService settingsService,
                                                 @Qualifier("pageHandlerViewToBean") Map<String, Class> pageHandlerViewToBean) {
    configurePageHandlerBase(defaultPageHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    defaultPageHandler.setPermittedLinkParameterNames(List.of("index"));
    defaultPageHandler.setNavigationResolver(navigationResolver);
    defaultPageHandler.setTopicPageContextFinder(uapiTopicpageContextFinder);
    defaultPageHandler.setSettingsService(settingsService);
    defaultPageHandler.setViewToBean(pageHandlerViewToBean);
  }

  /**
   * Handles actions.
   * Duplicates abstract bean defaultPageHandlerBase in xml config file.
   */
  public static void configureDefaultPageActionHandler(DefaultPageActionHandler actionHandler,
                                                       MimeTypeService mimeTypeService,
                                                       UrlPathFormattingHelper urlPathFormattingHelper,
                                                       DataViewFactory dataViewFactory,
                                                       ContextHelper contextHelper,
                                                       NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                                       ContentLinkBuilder contentLinkBuilder,
                                                       ContentBeanFactory contentBeanFactory,
                                                       SitesService sitesService,
                                                       Cache cache,
                                                       FlowRunner flowRunner,
                                                       ResourceBundleInterceptor resourceBundleInterceptor) {
    configurePageHandlerBase(actionHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contextHelper,
            navigationSegmentsUriHelper,
            contentLinkBuilder,
            contentBeanFactory,
            sitesService,
            cache);

    actionHandler.setPermittedLinkParameterNames(List.of("next", "userName"));  // required by elastic webflows
    actionHandler.setFlowRunner(flowRunner);
    actionHandler.setResourceBundleInterceptor(resourceBundleInterceptor);
  }

  //--- services

  /**
   * A FlowUrlHandler extending org.springframework.webflow.context.servlet.DefaultFlowUrlHandler
   * will only add context path and servlet path if CAE is configured to do so.
   * <p>
   * Overwrites the pre-configured FlowUrlHandler.
   */
  @Bean
  @Customize(value = "flowHandlerAdapter.flowUrlHandler", mode = Customize.Mode.REPLACE)
  public BlueprintFlowUrlHandler blueprintFlowUrlHandler(DeliveryConfigurationProperties deliveryConfigurationProperties) {
    BlueprintFlowUrlHandler urlHandler = new BlueprintFlowUrlHandler();
    urlHandler.setPrependBaseUri(deliveryConfigurationProperties.isStandalone());
    return urlHandler;
  }

  /**
   * Generate URI path from a navigation and vice versa.
   */
  @Bean
  public NavigationSegmentsUriHelper navigationSegmentsUriHelper(Cache cache,
                                                                 ContentBeanFactory contentBeanFactory,
                                                                 SitesService sitesService,
                                                                 UrlPathFormattingHelper urlPathFormattingHelper) {
    NavigationSegmentsUriHelper uriHelper = new NavigationSegmentsUriHelper();

    uriHelper.setCache(cache);
    uriHelper.setContentBeanFactory(contentBeanFactory);
    uriHelper.setSitesService(sitesService);
    uriHelper.setUrlPathFormattingHelper(urlPathFormattingHelper);

    return uriHelper;
  }

  @Bean
  @ConditionalOnExpression("${cae.hashing.backward-compatibility:false} || ${cae.hashing.migration-mode:false}")
  public DefaultSecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy() {
    return new DefaultSecureHashCodeGeneratorStrategy();
  }

  @Bean
  @Primary
  @ConditionalOnProperty("cae.hashing.migration-mode")
  public DefaultToMd5MigrationSecureHashCodeGeneratorStrategy defaultToMd5MigrationSecureHashCodeGeneratorStrategy(
          DefaultSecureHashCodeGeneratorStrategy defaultSecureHashCodeGeneratorStrategy,
          MD5SecureHashCodeGeneratorStrategy md5SecureHashCodeGeneratorStrategy) {
    return new DefaultToMd5MigrationSecureHashCodeGeneratorStrategy(defaultSecureHashCodeGeneratorStrategy, md5SecureHashCodeGeneratorStrategy);
  }

  @Bean
  @Customize(value = "handlerInterceptors", mode = Customize.Mode.APPEND)
  @Order(9999997)
  public ContentValidityInterceptor contentValidityInterceptor(ValidationService<Object> validationService) {
    ContentValidityInterceptor validityInterceptor = new ContentValidityInterceptor();
    validityInterceptor.setValidationService(validationService);
    return validityInterceptor;
  }

  @Bean
  @Customize(value = "handlerInterceptors", mode = Customize.Mode.APPEND)
  @Order(9999998)
  public ExposeCurrentNavigationInterceptor exposeCurrentNavigationInterceptor() {
    return new ExposeCurrentNavigationInterceptor();
  }

  @Bean
  ValidUntilConsumer validUntilConsumer(ObjectProvider<CacheControlStrategy<Instant>> cacheControlStrategyProvider) {
    return instant -> {
      cacheControlStrategyProvider.ifAvailable(strategy -> {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
          throw new IllegalStateException("Request attributes not available.");
        }
        if (requestAttributes.getAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE, SCOPE_REQUEST) == null) {
          strategy.recordValidUntil(instant);
        }
      });
    };
  }

  @Bean
  public BlueprintCacheConfigurationTypesFactory cacheConfigurationTypesFactory(){
    return new BlueprintCacheConfigurationTypesFactory();
  }
}
