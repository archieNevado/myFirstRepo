package com.coremedia.blueprint.component.cae.management.corporate;

import com.coremedia.blueprint.cae.common.predicates.ValidContentPredicate;
import com.coremedia.blueprint.cae.sitemap.CaeSitemapConfigurationProperties;
import com.coremedia.blueprint.cae.sitemap.ContentUrlGenerator;
import com.coremedia.blueprint.cae.sitemap.ExcludeFromSearchSitemapPredicate;
import com.coremedia.blueprint.cae.sitemap.SitemapDoctypePredicate;
import com.coremedia.blueprint.cae.sitemap.SitemapRendererFactory;
import com.coremedia.blueprint.cae.sitemap.SitemapSetup;
import com.coremedia.blueprint.cae.sitemap.SitemapUrlGenerator;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.component.cae.management.CaeManagementConfiguration;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.customizer.CustomizerConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

@ManagementContextConfiguration(proxyBeanMethods = false)
@Import({
        CustomizerConfiguration.class,
        CaeManagementConfiguration.class,
})
public class CorporateManagementConfiguration {

  @Bean
  @Customize("sitemapConfigurations")
  Map<String, SitemapSetup> appendCorporateSitemapConfiguration(SitemapSetup corporateSitemapConfiguration) {
    return Map.of("corporate", corporateSitemapConfiguration);
  }

  @Bean
  public SitemapSetup corporateSitemapConfiguration(CaeSitemapConfigurationProperties properties,
                                                    SitemapRendererFactory sitemapIndexRendererFactory,
                                                    SitemapUrlGenerator corporateSitemapContentUrlGenerator) {
    SitemapSetup sitemapSetup = new SitemapSetup(properties);
    sitemapSetup.setSitemapRendererFactory(sitemapIndexRendererFactory);
    sitemapSetup.setUrlGenerators(List.of(corporateSitemapContentUrlGenerator));
    return sitemapSetup;
  }

  @SuppressWarnings("unchecked")
  @Bean
  public ContentUrlGenerator corporateSitemapContentUrlGenerator(ContentBeanFactory contentBeanFactory, LinkFormatter linkFormatter, ValidationService validationService) {
    List<String> exclusionPaths = List.of("Options");
    return new ContentUrlGenerator(linkFormatter, contentBeanFactory, validationService, exclusionPaths, List.of(
            new ValidContentPredicate(contentBeanFactory),
            new SitemapDoctypePredicate(List.of(
                    "CMTeasable",
                    "CMAudio",
                    "CMVideo"
            ), List.of(
                    "CMHTML",
                    "CMVisual",
                    "CMExternalLink",
                    "CMAction",
                    "CMPlaceholder",
                    "CMDynamicList",
                    "CMTeaser",
                    "CMCollection",
                    "CMTaxonomy",
                    "CMLocTaxonomy"
            )),
            new ExcludeFromSearchSitemapPredicate("CMTeasable", "notSearchable")
    ));
  }

}
