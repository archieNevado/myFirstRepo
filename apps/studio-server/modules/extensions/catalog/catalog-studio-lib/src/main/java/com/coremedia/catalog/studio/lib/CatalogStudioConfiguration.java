package com.coremedia.catalog.studio.lib;

import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogConfiguration;
import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogTypes;
import com.coremedia.blueprint.base.rest.validators.UniqueInSiteStringValidator;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.util.ContentStringPropertyIndex;
import com.coremedia.catalog.studio.lib.validators.RootCategoryInvalidationSource;
import com.coremedia.rest.cap.config.StudioConfigurationProperties;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@SuppressWarnings("MethodMayBeStatic")
@Configuration
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

  @Bean
  RootCategoryInvalidationSource rootCategoryInvalidationSource(StudioConfigurationProperties studioConfigurationProperties, ApplicationContext applicationContext) {
    RootCategoryInvalidationSource rootCategoryInvalidationSource = new RootCategoryInvalidationSource(applicationContext);
    rootCategoryInvalidationSource.setCapacity(studioConfigurationProperties.getRest().getCatalogStudioCache().getCapacity());
    return rootCategoryInvalidationSource;
  }
}
