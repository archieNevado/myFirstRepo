package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.cae.web.links.ThemeResourceLinkBuilder;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Link transformation
 */
@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-links-services.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@ComponentScan(
        basePackages = {
                "com.coremedia.blueprint.cae.web.links"
        },
        lazyInit = true
)
public class BlueprintLinksCaeBaseLibConfiguration {

  @Bean
  public ThemeResourceLinkBuilder themeResourceLinkBuilder(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                                           LinkFormatter linkFormatter,
                                                           CurrentContextService currentContextService,
                                                           ContentRepository contentRepository,
                                                           DataViewFactory dataViewFactory,
                                                           ContentBeanFactory contentBeanFactory) {
    ThemeResourceLinkBuilder linkBuilder = new ThemeResourceLinkBuilder();

    linkBuilder.setUseLocalResources(deliveryConfigurationProperties.isLocalResources());
    linkBuilder.setLinkFormatter(linkFormatter);
    linkBuilder.setContextService(currentContextService);
    linkBuilder.setRepository(contentRepository);
    linkBuilder.setDataViewFactory(dataViewFactory);
    linkBuilder.setContentBeanFactory(contentBeanFactory);

    return linkBuilder;
  }

  /**
   * Creates a CAE LinkTransformer that rewrites all generated links
   * and adds the request parameter for the preview date.
   */
  @Bean
  public ParameterAppendingLinkTransformer previewDateAppendingLinkTransformer() {
    ParameterAppendingLinkTransformer linkTransformer = new ParameterAppendingLinkTransformer();

    linkTransformer.setParameterName(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE);

    return linkTransformer;
  }

}
