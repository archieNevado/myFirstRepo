package com.coremedia.catalog.studio.lib;

import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogConfiguration;
import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogTypes;
import com.coremedia.blueprint.base.rest.validators.UniqueInSiteStringValidator;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.util.ContentStringPropertyIndex;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@SuppressWarnings("MethodMayBeStatic")
@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml", // for "sitesService"
        "classpath:/framework/spring/bpbase-ec-cms-connection.xml",
        "classpath:/com/coremedia/blueprint/ecommerce/segments/ecommerce-segments.xml",
        "classpath:/framework/spring/bpbase-ec-cms-commercebeans.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        CmsCatalogConfiguration.class,
        CatalogStudioValidationConfiguration.class,
})
public class CatalogStudioConfiguration {

  @Bean
  UniqueInSiteStringValidator cmsCatalogUniqueProductCodeValidator(ContentRepository contentRepository,
                                                                   CmsCatalogTypes cmsCatalogTypes,
                                                                   ContentStringPropertyIndex cmsProductCodeIndex,
                                                                   SitesService sitesService) {
    UniqueInSiteStringValidator uniqueInSiteStringValidator =
            new UniqueInSiteStringValidator(contentRepository,
                    cmsCatalogTypes.getProductContentType(),
                    cmsCatalogTypes.getProductCodeProperty(),
                    cmsProductCodeIndex.createContentsByValueFunction(),
                    sitesService);
    uniqueInSiteStringValidator.setValidatingSubtypes(true);
    return uniqueInSiteStringValidator;
  }
}
