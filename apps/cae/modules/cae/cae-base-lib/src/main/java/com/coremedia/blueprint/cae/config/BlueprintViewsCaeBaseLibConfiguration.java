package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.feeds.FeedItemDataProvider;
import com.coremedia.blueprint.cae.feeds.impl.PictureFeedItemDataProvider;
import com.coremedia.blueprint.cae.feeds.impl.TeasableFeedItemDataProvider;
import com.coremedia.blueprint.cae.sitemap.SitemapHelper;
import com.coremedia.blueprint.cae.view.BlueprintHttpErrorView;
import com.coremedia.blueprint.cae.view.FeedView;
import com.coremedia.blueprint.cae.view.HttpHeadView;
import com.coremedia.blueprint.cae.view.MergeableResourcesView;
import com.coremedia.blueprint.cae.view.PlainView;
import com.coremedia.blueprint.cae.view.RobotsView;
import com.coremedia.blueprint.cae.view.ScriptView;
import com.coremedia.blueprint.cae.view.resolver.BlueprintViewLookupTraversal;
import com.coremedia.blueprint.cae.view.resolver.BlueprintViewRepositoryNameProvider;
import com.coremedia.blueprint.cae.view.resolver.ThemeTemplateViewRepositoryProvider;
import com.coremedia.blueprint.cae.view.viewtype.ViewTypeRenderNodeDecorator;
import com.coremedia.blueprint.cae.view.viewtype.ViewTypeRenderNodeDecoratorProvider;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cache.config.CacheConfiguration;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.multisite.impl.MultiSiteConfiguration;
import com.coremedia.cap.util.JarBlobResourceLoader;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.view.ErrorView;
import com.coremedia.objectserver.view.MultiRangeBlobView;
import com.coremedia.objectserver.view.RenderNodeDecoratorProvider;
import com.coremedia.objectserver.view.RichtextToHtmlFilterFactory;
import com.coremedia.objectserver.view.View;
import com.coremedia.objectserver.view.ViewDecorator;
import com.coremedia.objectserver.view.ViewEngine;
import com.coremedia.objectserver.view.XmlFilterFactory;
import com.coremedia.objectserver.view.XmlMarkupView;
import com.coremedia.objectserver.view.config.CaeViewErrorServicesConfiguration;
import com.coremedia.objectserver.view.config.CaeViewServicesConfiguration;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import com.coremedia.objectserver.view.dynamic.DynamicIncludeRenderNodeDecorator;
import com.coremedia.objectserver.view.dynamic.DynamicIncludeRenderNodeDecoratorProvider;
import com.coremedia.objectserver.view.events.ViewHookEventView;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.springframework.core.io.CompoundResourceLoader;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blueprint specific definitions of the CAE View layer.
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan(
        basePackages = {
                "com.coremedia.cap.util.configuration",
                "com.coremedia.blueprint.coderesources"
        },
        lazyInit = true
)
@ImportResource(value = {
        "classpath:/com/coremedia/cae/dataview-services.xml",
        "classpath:/com/coremedia/cae/view-services-lifecycle.xml",
        "classpath:/com/coremedia/cap/common/uapi-services.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/framework/spring/blueprint-services.xml",
        "classpath:/framework/spring/blueprint-handlers.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-cae-services.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        BlueprintRichtextFiltersConfiguration.class,
        CacheConfiguration.class,
        CaeViewServicesConfiguration.class,
        CaeViewErrorServicesConfiguration.class,
        MultiSiteConfiguration.class,
})
public class BlueprintViewsCaeBaseLibConfiguration {

  /**
   * The CAE default RichtextToHtmlFilterFactory providing the default set of richtext filters (not the Blueprint
   * specific one) for programmed views that need it.
   */
  @Bean
  public RichtextToHtmlFilterFactory cmRichtextToHtmlFilterFactory(IdProvider idProvider,
                                                                   LinkFormatter linkFormatter) {
    RichtextToHtmlFilterFactory filterFactory = new RichtextToHtmlFilterFactory();

    filterFactory.setIdProvider(idProvider);
    filterFactory.setLinkFormatter(linkFormatter);

    return filterFactory;
  }

  /**
   * A programmed view to generate CSS/JS stored in CoreMedia richtext.
   */
  @Bean
  public ScriptView scriptView(XmlFilterFactory cmRichtextToHtmlFilterFactory) {
    ScriptView scriptView = new ScriptView();

    scriptView.setXmlFilterFactory(cmRichtextToHtmlFilterFactory);

    return scriptView;
  }

  /**
   * A view for merging the JavaScript of a page into one file.
   */
  @Bean
  public MergeableResourcesView mergedJavaScriptResourcesView(XmlFilterFactory cmRichtextToHtmlFilterFactory,
                                                              Cache cache) {
    MergeableResourcesView mergeableResourcesView = new MergeableResourcesView();

    configureMergedCodeResourcesViewBase(mergeableResourcesView,
            cmRichtextToHtmlFilterFactory,
            cache);

    mergeableResourcesView.setContentType("text/javascript");

    return mergeableResourcesView;
  }

  /**
   * A view for merging the CSS of a page into one file.
   */
  @Bean
  public MergeableResourcesView mergedCssResourcesView(XmlFilterFactory cmRichtextToHtmlFilterFactory,
                                                       Cache cache) {
    MergeableResourcesView mergeableResourcesView = new MergeableResourcesView();

    configureMergedCodeResourcesViewBase(mergeableResourcesView,
            cmRichtextToHtmlFilterFactory,
            cache);

    mergeableResourcesView.setContentType("text/css");

    return mergeableResourcesView;
  }

  private void configureMergedCodeResourcesViewBase(MergeableResourcesView mergeableResourcesView,
                                                    XmlFilterFactory cmRichtextToHtmlFilterFactory,
                                                    Cache cache) {
    mergeableResourcesView.setCache(cache);
    mergeableResourcesView.setXmlFilterFactory(cmRichtextToHtmlFilterFactory);
  }

  /**
   * Handles instances of com.coremedia.objectserver.web.HttpError.
   */
  @Bean
  public BlueprintHttpErrorView blueprintHttpErrorView() {
    BlueprintHttpErrorView errorView = new BlueprintHttpErrorView();

    errorView.setErrorsRendered(List.of(400, 404));

    return errorView;
  }

  @Bean
  public HttpHeadView httpHeadView() {
    return new HttpHeadView();
  }

  /**
   * A programmed view to generate plain text from markup.
   */
  @Bean
  public PlainView plainView() {
    return new PlainView();
  }

  /**
   * A programmed view to generate a Robots.txt from CMChannel settings.
   */
  @Bean
  public RobotsView robotsView(LinkFormatter linkFormatter,
                               ObjectProvider<SitemapHelper> sitemapHelperProvider) {
    RobotsView robotsView = new RobotsView();

    robotsView.setLinkFormatter(linkFormatter);
    sitemapHelperProvider.ifAvailable(robotsView::setSitemapHelper);

    return robotsView;
  }

  /**
   * Programmed view that generates RSS/ATOM feeds for several document types.
   */
  @Bean
  public FeedView feedView(SitesService sitesService,
                           LinkFormatter linkFormatter,
                           @Qualifier("feedItemDataProviders") List<FeedItemDataProvider> feedItemDataProviders,
                           SettingsService settingsService) {
    FeedView feedView = new FeedView();

    feedView.setFeedItemLimit(20);
    feedView.setSitesService(sitesService);
    feedView.setLinkFormatter(linkFormatter);
    feedView.setFeedItemDataProviders(feedItemDataProviders);
    feedView.setSettingsService(settingsService);

    return feedView;
  }

  /**
   * Programmed view for rendering markup without further filters, e.g. for plain HTML.
   */
  @Bean
  public XmlMarkupView htmlMarkupView() {
    return new XmlMarkupView();
  }

  /**
   * Contains programmed views injected into all view repositories.
   */
  @SuppressWarnings("MethodWithTooManyParameters")
  @Bean
  @Customize(value = "programmedViews", mode = Customize.Mode.REPLACE)
  @Order(10000)
  public Map<String, View> blueprintProgrammedViews(XmlMarkupView richtextMarkupView,
                                                    XmlMarkupView htmlMarkupView,
                                                    MultiRangeBlobView blobView,
                                                    ViewHookEventView viewHookEventView,
                                                    ErrorView errorView,
                                                    ScriptView scriptView,
                                                    MergeableResourcesView mergedJavaScriptResourcesView,
                                                    MergeableResourcesView mergedCssResourcesView,
                                                    PlainView plainView,
                                                    BlueprintHttpErrorView blueprintHttpErrorView,
                                                    HttpHeadView httpHeadView,
                                                    RobotsView robotsView,
                                                    FeedView feedView) {
    Map<String, View> viewMap = new HashMap();

    viewMap.put("com.coremedia.xml.Markup", richtextMarkupView);
    viewMap.put("com.coremedia.xml.Markup#html", htmlMarkupView);
    viewMap.put("com.coremedia.cap.common.Blob", blobView);
    viewMap.put("com.coremedia.objectserver.view.events.ViewHookEvent", viewHookEventView);
    viewMap.put("java.lang.Throwable", errorView);
    viewMap.put("com.coremedia.xml.Markup#script", scriptView);
    viewMap.put("com.coremedia.blueprint.common.contentbeans.MergeableResources#js", mergedJavaScriptResourcesView);
    viewMap.put("com.coremedia.blueprint.common.contentbeans.MergeableResources#css", mergedCssResourcesView);
    viewMap.put("com.coremedia.xml.Markup#plain", plainView);
    viewMap.put("com.coremedia.objectserver.web.HttpError", blueprintHttpErrorView);
    viewMap.put("com.coremedia.objectserver.web.HttpError#asError", blueprintHttpErrorView);
    viewMap.put("com.coremedia.blueprint.cae.web.HttpHead", httpHeadView);
    viewMap.put("com.coremedia.blueprint.common.robots.RobotsBean", robotsView);
    viewMap.put("com.coremedia.blueprint.common.feeds.FeedSource#asFeed", feedView);

    return viewMap;
  }

  @Bean
  public TeasableFeedItemDataProvider teasableFeedItemDataProvider(LinkFormatter linkFormatter) {
    TeasableFeedItemDataProvider dataProvider = new TeasableFeedItemDataProvider();

    dataProvider.setLinkFormatter(linkFormatter);

    return dataProvider;
  }

  @Bean
  public PictureFeedItemDataProvider pictureFeedItemDataProvider(LinkFormatter linkFormatter) {
    PictureFeedItemDataProvider dataProvider = new PictureFeedItemDataProvider();

    dataProvider.setLinkFormatter(linkFormatter);

    return dataProvider;
  }

  @Bean
  public List<FeedItemDataProvider> feedItemDataProviders(PictureFeedItemDataProvider pictureFeedItemDataProvider,
                                                          TeasableFeedItemDataProvider teasableFeedItemDataProvider) {
    // order: from special to generic
    return List.of(pictureFeedItemDataProvider, teasableFeedItemDataProvider);
  }

  //--- View Dispatcher

  /**
   * This bean provides symbolic names for each view lookup the view repositories to be queried for views.
   * The Blueprint implementation takes repository names from
   * 1. the String list setting "viewRepositoryNames" of the current navigation
   * 2. the chosen viewRepository string property of a theme if attached to navigation
   * 3. a list of common view repository names added in the property "commonViewRepositoryNames" below
   * The resulting repository names are later matched to view repositories by a CAE ViewRespositoryProvider.
   */
  @Bean
  @Customize(value = "viewRepositoryNameProviders", mode = Customize.Mode.PREPEND)
  @Order(10_000)
  public BlueprintViewRepositoryNameProvider blueprintViewRepositoryNameProvider(@Qualifier("viewRepositories") List<String> viewRepositories,
                                                                                 SettingsService settingsService,
                                                                                 ThemeTemplateViewRepositoryProvider themeTemplateViewRepositoryProvider) {
    BlueprintViewRepositoryNameProvider nameProvider = new BlueprintViewRepositoryNameProvider();

    nameProvider.setCommonViewRepositoryNames(viewRepositories);
    nameProvider.setSettingsService(settingsService);
    nameProvider.setThemeTemplateViewRepositoryProvider(themeTemplateViewRepositoryProvider);

    return nameProvider;
  }

  /**
   * Empty list of common (i.e. not lookup-specific) view repositories.
   * Exposed as a bean in order to allow plugins and extensions to add to the list via customizers.
   */
  @Bean
  public List<String> viewRepositories() {
    return new ArrayList<>();
  }

  @Bean
  @Customize(value = "viewRepositories", mode = Customize.Mode.PREPEND)
  @Order(1000)
  public String addCommonRepositoryName() {
    return "common";
  }

  @Bean
  @Customize(value = "viewRepositories", mode = Customize.Mode.PREPEND)
  @Order(900)
  public String addErrorRepositoryName() {
    return "error";
  }

  //--- View Type specific customizations

  /**
   * Add the RenderNodeDecoratorProvider which decorates views with content managed "viewtypes".
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "renderNodeDecoratorProviders", mode = Customize.Mode.APPEND)
  @Order(10000)
  public List<RenderNodeDecoratorProvider> addBlueprintRenderNodeDecoratorProvider(@Qualifier("dynamicIncludePredicates") List<DynamicIncludePredicate> dynamicIncludePredicates) {
    ViewTypeRenderNodeDecoratorProvider viewTypeProvider = new ViewTypeRenderNodeDecoratorProvider();
    viewTypeProvider.setDecorator(new ViewTypeRenderNodeDecorator());

    DynamicIncludeRenderNodeDecorator dynamicIncludeDecorator = new DynamicIncludeRenderNodeDecorator(
            dynamicIncludePredicates,
            com.coremedia.blueprint.cae.view.DynamicInclude::new);

    DynamicIncludeRenderNodeDecoratorProvider dynamicIncludeProvider
            = new DynamicIncludeRenderNodeDecoratorProvider(dynamicIncludeDecorator, dynamicIncludePredicates);

    return List.of(viewTypeProvider, dynamicIncludeProvider);
  }

  /**
   * This list of predicates will be traversed by the dynamicIncludeRenderDecorator/Provider in order to decide
   * whether a DynamicInclude should be used for a given Bean/View combination.
   */
  @Bean
  public List<DynamicIncludePredicate> dynamicIncludePredicates() {
    return new ArrayList<>();
  }

  /**
   * Creates and registers a ViewLookupTraversal considering viewtypes.
   */
  @Bean
  @Customize(value = "modelAwareViewResolver.viewLookupTraversal", mode = Customize.Mode.REPLACE)
  @Order(10000)
  public BlueprintViewLookupTraversal blueprintViewLookupTraversal() {
    return new BlueprintViewLookupTraversal();
  }

  //--- View Repository and resolving

  /**
   * Add types that trigger the clearing of the viewcache if they are modified / created / deleted.
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "viewLookupTypeTriggers", mode = Customize.Mode.APPEND)
  @Order(10000)
  public List<String> addViewLookupTypeTriggers() {
    return List.of("CMTemplateSet", "CMTheme");
  }

  /**
   * Register the template location patterns of this module.
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "templateLocationPatterns", mode = Customize.Mode.REPLACE)
  @Order(0)
  public List<String> customizeTemplateLocationPatterns() {
    return List.of("/WEB-INF/templates/%s");
  }

  /**
   * Register template location pattern that loads templates from the Content Repository.
   * (!) Make sure that this customizer runs after "customizeTemplateLocationPatterns" since this customizer prepends, while
   * the other one replaces the list (!)
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "templateLocationPatterns", mode = Customize.Mode.PREPEND)
  @Order(1)
  @ConditionalOnProperty(name = "delivery.local-resources", havingValue = "false", matchIfMissing = true)
  public String addRepositoryTemplateLocationPattern() {
    return "jar:id:contentproperty:/Themes/%1$s/templates/%1$s-templates.jar/archive!/META-INF/resources/WEB-INF/templates/%1$s";
  }

  @Bean
  @Customize(value = "viewRepositoryProviders", mode = Customize.Mode.PREPEND)
  @Order(10000)
  public ThemeTemplateViewRepositoryProvider themeTemplateViewRepositoryProvider(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                                                                 @Qualifier("viewDecorators") List<ViewDecorator> viewDecorators,
                                                                                 @Qualifier("viewEngines") Map<String, ViewEngine> viewEngines,
                                                                                 @Qualifier("programmedViews") Map<String, View> programmedViews,
                                                                                 @Qualifier("templatesResourceLoader") CompoundResourceLoader resourceLoader,
                                                                                 Cache cache,
                                                                                 ThemeService themeService,
                                                                                 CapConnection capConnection,
                                                                                 JarBlobResourceLoader jarBlobResourceLoader) {
    ThemeTemplateViewRepositoryProvider repositoryProvider = new ThemeTemplateViewRepositoryProvider();

    repositoryProvider.setViewDecorators(viewDecorators);
    repositoryProvider.setViewEngines(viewEngines);
    repositoryProvider.setProgrammedViews(programmedViews);
    repositoryProvider.setResourceLoader(resourceLoader);
    repositoryProvider.setCache(cache);
    repositoryProvider.setThemeService(themeService);
    repositoryProvider.setCapConnection(capConnection);
    repositoryProvider.setJarBlobResourceLoader(jarBlobResourceLoader);
    repositoryProvider.setUseLocalResources(deliveryConfigurationProperties.isLocalResources());

    return repositoryProvider;
  }

  /**
   * These values will be added to every request that is processed by the DispatcherServlet.
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "viewResolverAttributes", mode = Customize.Mode.APPEND)
  @Order(10000)
  public Map<String, Object> blueprintViewResolverCustomizer(ContextHelper contextHelper,
                                                             ContextStrategy contextStrategy) {
    Map<String, Object> map = new HashMap<>();

    map.put(ContextHelper.NAME_CONTEXTHELPER, contextHelper);
    map.put(ContextStrategy.NAME_CONTEXTSTRATEGY, contextStrategy);

    return map;
  }

  //--- Preview webresources

  @Bean(autowireCandidate = false)
  @Customize(value = "previewResourcesCssList", mode = Customize.Mode.APPEND)
  @Order(10000)
  public String addPreviewCss() {
    return "/static/preview/coremedia.preview.blueprint.css";
  }

  @Bean(autowireCandidate = false)
  @Customize(value = "previewResourcesJsList", mode = Customize.Mode.APPEND)
  @Order(10000)
  public String addPreviewJs() {
    return "/static/preview/coremedia.preview.blueprint.js";
  }
}
