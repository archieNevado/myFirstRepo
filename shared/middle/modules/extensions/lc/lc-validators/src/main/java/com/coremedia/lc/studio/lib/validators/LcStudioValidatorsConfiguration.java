package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.rest.validators.ChannelNavigationValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelReferrerValidator;
import com.coremedia.blueprint.base.rest.validators.ChannelSegmentValidator;
import com.coremedia.cache.config.CacheConfiguration;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.validators.StructLinkListIndexValidator;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@Import({
        BaseCommerceServicesConfiguration.class,
        CacheConfiguration.class,
})
@ImportResource(value = {
        "classpath:/META-INF/coremedia/lc-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-urlpathformatting.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcStudioValidatorsConfiguration {

  private static final String CMPRODUCTLIST_NAME = "CMProductList";
  private static final String CMEXTERNALCHANNEL_NAME = "CMExternalChannel";
  private static final String EXTERNALID_PROPERTY = "externalId";
  private static final String CMPRODUCTLIST_STRUCT_PROPERTY = "localSettings";

  private static CatalogLinkValidator createCatalogLinkValidator(@NonNull ContentType type,
                                                                 CommerceConnectionSupplier commerceConnectionSupplier,
                                                                 SitesService sitesService) {
    return new CatalogLinkValidator(type, true, commerceConnectionSupplier, sitesService, EXTERNALID_PROPERTY);
  }

  @NonNull
  private static ContentType type(@NonNull CapConnection connection, @NonNull String typeStr) {
    return Objects.requireNonNull(connection.getContentRepository().getContentType(typeStr));
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  ProductListValidator productListFormatValidator(CapConnection connection,
                                                  CommerceConnectionSupplier commerceConnectionSupplier,
                                                  SitesService sitesService) {
    return new ProductListValidator(type(connection, CMPRODUCTLIST_NAME), commerceConnectionSupplier, sitesService, CMPRODUCTLIST_STRUCT_PROPERTY, EXTERNALID_PROPERTY);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator productTeaserExternalIdValidator(CapConnection connection,
                                                        CommerceConnectionSupplier commerceConnectionSupplier,
                                                        SitesService sitesService) {
    return createCatalogLinkValidator(type(connection, "CMProductTeaser"), commerceConnectionSupplier, sitesService);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator marketingSpotExternalIdValidator(CapConnection connection,
                                                        CommerceConnectionSupplier commerceConnectionSupplier,
                                                        SitesService sitesService) {
    return createCatalogLinkValidator(type(connection, "CMMarketingSpot"), commerceConnectionSupplier, sitesService);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator productListExternalIdValidator(CapConnection connection,
                                                      CommerceConnectionSupplier commerceConnectionSupplier,
                                                      SitesService sitesService) {
    CatalogLinkValidator validator = createCatalogLinkValidator(type(connection, CMPRODUCTLIST_NAME), commerceConnectionSupplier, sitesService);
    validator.setOptional(true);
    return validator;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator externalChannelExternalIdValidator(CapConnection connection,
                                                          CommerceConnectionSupplier commerceConnectionSupplier,
                                                          SitesService sitesService) {
    return new ExternalChannelValidator(type(connection, CMEXTERNALCHANNEL_NAME),
            true,
            commerceConnectionSupplier,
            sitesService,
            EXTERNALID_PROPERTY);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator externalProductExternalIdValidator(CapConnection connection,
                                                          CommerceConnectionSupplier commerceConnectionSupplier,
                                                          SitesService sitesService) {
    return new ExternalProductValidator(type(connection, "CMExternalProduct"),
            true,
            commerceConnectionSupplier,
            sitesService,
            EXTERNALID_PROPERTY);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  CatalogLinkValidator externalPageExternalIdValidator(CapConnection connection,
                                                       CommerceConnectionSupplier commerceConnectionSupplier,
                                                       SitesService sitesService) {
    return new ExternalPageValidator(type(connection, "CMExternalPage"),
            true,
            commerceConnectionSupplier,
            sitesService,
            EXTERNALID_PROPERTY);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  IsLiveContextTypeValidator productListSupportedValidator(CapConnection connection,
                                                           CommerceConnectionSupplier commerceConnectionSupplier,
                                                           SitesService sitesService) {
    return new IsLiveContextTypeValidator(type(connection, CMPRODUCTLIST_NAME),
            true,
            commerceConnectionSupplier,
            sitesService);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  StructLinkListIndexValidator productListIndexValidator(CapConnection connection) {
    StructLinkListIndexValidator validator = new StructLinkListIndexValidator(type(connection, CMPRODUCTLIST_NAME), false);
    validator.setListPropertyName("links");
    validator.setMaxLengthPropertyName("maxLength");
    validator.setPropertyName("extendedItems");
    return validator;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  ExternalPagePartOfNavigationValidator externalPagePartOfNavigationValidator(CapConnection connection,
                                                                              SitesService sitesService) {
    return new ExternalPagePartOfNavigationValidator(type(connection, "CMExternalPage"), false, sitesService);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  ChannelSegmentValidator cmExternalChannelSegmentValidator(CapConnection connection,
                                                            UrlPathFormattingHelper urlPathFormattingHelper) {
    ChannelSegmentValidator validator = new ChannelSegmentValidator(type(connection, CMEXTERNALCHANNEL_NAME), false);
    validator.setUrlPathFormattingHelper(urlPathFormattingHelper);
    return validator;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  SegmentFormatValidator segmentFormatValidator(CapConnection connection,
                                                CommerceConnectionSupplier commerceConnectionSupplier,
                                                SitesService sitesService) {
    SegmentFormatValidator validator = new SegmentFormatValidator(type(connection, "CMChannel"),
            true,
            commerceConnectionSupplier,
            sitesService,
            "segment");
    validator.setFallbackPropertyName("title");
    return validator;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  ChannelNavigationValidator cmExternalChannelNavigationValidator(CapConnection connection) {
    return new ChannelNavigationValidator(type(connection, CMEXTERNALCHANNEL_NAME), false);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  ChannelReferrerValidator cmExternalChannelReferrerValidator(CapConnection connection) {
    return new ChannelReferrerValidator(type(connection, CMEXTERNALCHANNEL_NAME), false);
  }
}
