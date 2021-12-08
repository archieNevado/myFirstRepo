package com.coremedia.blueprint.component.cae.management;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.common.predicates.ValidContentPredicate;
import com.coremedia.blueprint.cae.handlers.PreviewUrlHandler;
import com.coremedia.blueprint.cae.handlers.UrlHandler;
import com.coremedia.blueprint.cae.sitemap.CaeSitemapConfigurationProperties;
import com.coremedia.blueprint.cae.sitemap.ContentBasedSitemapSetupFactory;
import com.coremedia.blueprint.cae.sitemap.ContentUrlGenerator;
import com.coremedia.blueprint.cae.sitemap.PlainSitemapRendererFactory;
import com.coremedia.blueprint.cae.sitemap.SitemapGenerationHandler;
import com.coremedia.blueprint.cae.sitemap.SitemapSetup;
import com.coremedia.blueprint.cae.sitemap.SitemapSetupSelector;
import com.coremedia.blueprint.cae.sitemap.SitemapUrlGenerator;
import com.coremedia.blueprint.cae.sitemap.TestUrlsDoctypePredicate;
import com.coremedia.blueprint.cae.sitemap.TestUrlsGenerationHandler;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.request.RequestUtils;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Predicate;

@ManagementContextConfiguration(proxyBeanMethods = false)
@Order(0)
public class CaeManagementConfiguration {

  private final String caeServletPath;

  public CaeManagementConfiguration(ServerProperties serverProperties, WebMvcProperties webMvcProperties) {
    var path = serverProperties.getServlet().getContextPath() + webMvcProperties.getServlet().getPath();
    caeServletPath = UriComponentsBuilder.fromPath(path).build().getPath();
  }

  @Bean
  @Primary
  public LinkFormatter managementLinkFormatter(LinkFormatter linkFormatter) {
    return new LinkFormatter() {
      @Override
      public String formatLink(Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
        // set the public CAE base URI so that links do not point to the internal controller
        request.setAttribute(RequestUtils.PARAMETERS.replace("parameters", "baseuri"), caeServletPath);
        return linkFormatter.formatLink(bean, view, request, response, forRedirect);
      }

    };
  }

  @Bean
  @ConditionalOnProperty("delivery.preview-mode")
  public PreviewUrlHandler previewUrlHandler(LinkFormatter linkFormatter,
                                             IdProvider idProvider,
                                             ContentBeanFactory contentBeanFactory,
                                             SitesService sitesService) {
    var previewUrlHandler = new PreviewUrlHandler(linkFormatter);
    previewUrlHandler.setIdProvider(idProvider);
    previewUrlHandler.setContentBeanFactory(contentBeanFactory);
    previewUrlHandler.setSitesService(sitesService);
    return previewUrlHandler;
  }

  @Bean
  public UrlHandler urlHandler(LinkFormatter linkFormatter,
                               ContentBeanFactory contentBeanFactory,
                               IdProvider idProvider,
                               SitesService sitesService) {
    var urlHandler = new UrlHandler(linkFormatter);
    urlHandler.setContentBeanFactory(contentBeanFactory);
    urlHandler.setIdProvider(idProvider);
    urlHandler.setSitesService(sitesService);
    return urlHandler;
  }

  /**
   * The handler that generates the sitemaps.
   */
  @Bean
  public SitemapGenerationHandler sitemapGenerationHandler(SiteResolver siteResolver,
                                                           ContentBasedSitemapSetupFactory contentBasedSitemapSetupFactory) {
    return new SitemapGenerationHandler(siteResolver, contentBasedSitemapSetupFactory);
  }

  /**
   * SitemapSetupFactory which uses the settings "sitemapOrgConfiguration" from the site.
   */
  @Bean
  public ContentBasedSitemapSetupFactory contentBasedSitemapSetupFactory(SitemapSetupSelector sitemapSetupSelector) {
    return new ContentBasedSitemapSetupFactory(sitemapSetupSelector);
  }

  @Bean
  @ConditionalOnBean(name = "testUrlsConfig")
  public TestUrlsGenerationHandler testUrlsGenerationHandler(SiteResolver siteResolver,
                                                             SitemapSetup testUrlsConfig) {
    return new TestUrlsGenerationHandler(siteResolver, site -> testUrlsConfig);
  }

  @Profile("dev")
  @Configuration(proxyBeanMethods = false)
  static class TestUrlsConfiguration {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public ContentUrlGenerator testContentUrlGenerator(ContentBeanFactory contentBeanFactory,
                                                       LinkFormatter linkFormatter,
                                                       ValidationService validationService) {
      List<String> exclusionPaths = List.of(
              "/Settings",
              "/Themes",
              "/System",
              "/Home",
              "/Sites/Aurora Augmentation/United States/English/Navigation/Fragments"
      );
      List<Predicate<Content>> predicates = List.of(
              new ValidContentPredicate(contentBeanFactory),
              new TestUrlsDoctypePredicate(List.of(
                      "CMArticle",
                      "CMChannel"
              )));
      return new ContentUrlGenerator(linkFormatter, contentBeanFactory, validationService, exclusionPaths, predicates);
    }

    @Bean
    public SitemapSetup testUrlsConfig(CaeSitemapConfigurationProperties properties,
                                       SitemapUrlGenerator testContentUrlGenerator) {
      var sitemapSetup = new SitemapSetup(properties);
      sitemapSetup.setSitemapRendererFactory(new PlainSitemapRendererFactory());
      sitemapSetup.setUrlGenerators(List.of(testContentUrlGenerator));
      return sitemapSetup;
    }

  }
}
