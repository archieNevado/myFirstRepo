package com.coremedia.catalog.studio.lib.validators;

import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogConfiguration;
import com.coremedia.blueprint.base.ecommerce.content.CmsCatalogTypes;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.rest.validators.ChannelNavigationValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelReferrerValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelSegmentValidator;
import com.coremedia.blueprint.base.rest.validators.UniqueInSiteStringValidator;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.util.ContentStringPropertyIndex;
import com.coremedia.rest.validators.NotEmptyValidator;
import com.coremedia.rest.validators.RegExpValidator;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.List;
import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@Import({
        CmsCatalogConfiguration.class,
})
@ImportResource(value = {
        "classpath:/com/coremedia/blueprint/base/links/bpbase-urlpathformatting.xml",
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml", // for "sitesService"
        "classpath:/framework/spring/bpbase-ec-cms-connection.xml",
        "classpath:/framework/spring/bpbase-ec-cms-commercebeans.xml",
        "classpath:/com/coremedia/blueprint/ecommerce/segments/ecommerce-segments.xml"

}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class CatalogValidatorsConfiguration {

  @Bean
  UniqueInSiteStringValidator cmsCatalogUniqueProductCodeValidator(ContentRepository contentRepository,
                                                                   CmsCatalogTypes cmsCatalogTypes,
                                                                   ContentStringPropertyIndex cmsProductCodeIndex,
                                                                   SitesService sitesService) {
    return new UniqueInSiteStringValidator(type(contentRepository, cmsCatalogTypes.getProductContentType()),
                                           true,
                                           cmsCatalogTypes.getProductCodeProperty(),
                                           cmsProductCodeIndex.createContentsByValueFunction(),
                                           sitesService);
  }

  @Bean
  NotEmptyValidator notEmptyValidatorProductName() {
    return new NotEmptyValidator("productName");
  }

  @Bean
  NotEmptyValidator notEmptyValidatorProductCode() {
    return new NotEmptyValidator("productCode");
  }

  @Bean
  RegExpValidator regExpValidatorProductCode() {
    return new RegExpValidator("productCode", "[^:/\\s]*");
  }

  @Bean
  CatalogProductValidator catalogProductValidator(ContentRepository repository,
                                                  NotEmptyValidator notEmptyValidatorProductName,
                                                  NotEmptyValidator notEmptyValidatorProductCode,
                                                  RegExpValidator regExpValidatorProductCode) {
    return new CatalogProductValidator(type(repository, "CMProduct"),
                                       false,
                                       List.of(notEmptyValidatorProductName,
                                               notEmptyValidatorProductCode,
                                               regExpValidatorProductCode));
  }

  @Bean
  CatalogCategoryValidator catalogCategoryValidator(ContentRepository repository) {
    return new CatalogCategoryValidator(type(repository, "CMCategory"), false, "LiveContext");
  }

  @Bean
  ChannelSegmentValidator cmCategorySegmentValidator(ContentRepository repository,
                                                     UrlPathFormattingHelper urlPathFormattingHelper) {
    ChannelSegmentValidator validator = new ChannelSegmentValidator(type(repository, "CMCategory"), false);
    validator.setUrlPathFormattingHelper(urlPathFormattingHelper);
    return validator;
  }

  @Bean
  ChannelNavigationValidator cmCategoryNavigationValidator(ContentRepository repository) {
    ChannelNavigationValidator channelNavigationValidator = new ChannelNavigationValidator(type(repository, "CMCategory"), false);
    channelNavigationValidator.setChannelLoopCode("category_loop");
    return channelNavigationValidator;
  }

  @Bean
  ChannelReferrerValidator cmCategoryReferrerValidator(ContentRepository repository) {
    ChannelReferrerValidator channelReferrerValidator = new ChannelReferrerValidator(type(repository, "CMCategory"), false);
    channelReferrerValidator.setDuplicateReferrerCode("duplicate_category_parent");
    return channelReferrerValidator;
  }

  @NonNull
  private static ContentType type(@NonNull ContentRepository repository, @NonNull String typeStr) {
    return Objects.requireNonNull(repository.getContentType(typeStr));
  }
}
