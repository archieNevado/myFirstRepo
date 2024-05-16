package com.coremedia.livecontext.asset.config;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.config.BlueprintHandlersCaeBaseLibConfiguration;
import com.coremedia.blueprint.ecommerce.cae.config.ECommerceCaeConfiguration;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.cap.transform.Transformation;
import com.coremedia.livecontext.asset.AssetCommerceContextInterceptor;
import com.coremedia.livecontext.asset.CatalogPictureHandlerBase;
import com.coremedia.livecontext.asset.CategoryCatalogPictureHandler;
import com.coremedia.livecontext.asset.ProductCatalogPictureHandler;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.MappedInterceptor;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/framework/spring/blueprint-handlers.xml",
        "classpath:/META-INF/coremedia/livecontext-site-services.xml",
        "classpath:/META-INF/coremedia/livecontext-handler-interceptors.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@ComponentScan(
        basePackages = {
                // needed for com.coremedia.support.licensemanager.core.packager.LicensePackager.java
                "com.coremedia.livecontext.asset.licenses"
        },
        lazyInit = true
)
public class LcCaeAssetConfiguration {

  //--- These transformations are fallbacks/defaults and are only applied if they are not defined in
  //--- the site's responsiveImageSettings

  @Bean
  public Transformation productPictureFallback() {
    Transformation transformation = new Transformation();

    transformation.setName("portrait_ratio20x31");
    transformation.setWidthRatio(20);
    transformation.setHeightRatio(31);
    transformation.setMinWidth(646);
    transformation.setMinHeight(100);
    transformation.setPreviewWidth(400);

    return transformation;
  }

  @Bean
  public Transformation categoryPictureFallback() {
    Transformation transformation = new Transformation();

    transformation.setName("landscape_ratio16x9");
    transformation.setWidthRatio(16);
    transformation.setHeightRatio(9);
    transformation.setMinWidth(512);
    transformation.setMinHeight(288);
    transformation.setPreviewWidth(400);

    return transformation;
  }

  /**
   * Handles catalog images for products.
   */
  @Bean
  public ProductCatalogPictureHandler productCatalogPictureHandler(MimeTypeService mimeTypeService,
                                                                   UrlPathFormattingHelper urlPathFormattingHelper,
                                                                   DataViewFactory dataViewFactory,
                                                                   ContentLinkBuilder contentLinkBuilder,
                                                                   LiveContextSiteResolver siteResolver,
                                                                   TransformImageService transformImageService) {
    ProductCatalogPictureHandler productHandler = new ProductCatalogPictureHandler();

    configureCatalogPictureHandlerBase(productHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder,
            siteResolver,
            transformImageService);

    Map<String, String> pictureFormats = new HashMap<>(2);
    pictureFormats.put(CatalogPictureHandlerBase.FORMAT_KEY_THUMBNAIL, "portrait_ratio20x31/200/310");
    pictureFormats.put(CatalogPictureHandlerBase.FORMAT_KEY_FULL, "portrait_ratio20x31/646/1000");

    productHandler.setPictureFormats(pictureFormats);

    return productHandler;
  }

  /**
   * Handles catalog images for categories.
   */
  @Bean
  public CategoryCatalogPictureHandler categoryCatalogPictureHandler(MimeTypeService mimeTypeService,
                                                                     UrlPathFormattingHelper urlPathFormattingHelper,
                                                                     DataViewFactory dataViewFactory,
                                                                     ContentLinkBuilder contentLinkBuilder,
                                                                     LiveContextSiteResolver siteResolver,
                                                                     TransformImageService transformImageService) {
    CategoryCatalogPictureHandler categoryHandler = new CategoryCatalogPictureHandler();

    configureCatalogPictureHandlerBase(categoryHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder,
            siteResolver,
            transformImageService);

    Map<String, String> pictureFormats = new HashMap<>(2);
    pictureFormats.put(CatalogPictureHandlerBase.FORMAT_KEY_THUMBNAIL, "landscape_ratio16x9/256/144");
    pictureFormats.put(CatalogPictureHandlerBase.FORMAT_KEY_FULL, "landscape_ratio16x9/512/288");

    categoryHandler.setPictureFormats(pictureFormats);

    return categoryHandler;
  }

  /**
   * Duplicates abstract bean catalogPictureHandlerBase in xml config file.
   */
  public static void configureCatalogPictureHandlerBase(CatalogPictureHandlerBase catalogPictureHandler,
                                                        MimeTypeService mimeTypeService,
                                                        UrlPathFormattingHelper urlPathFormattingHelper,
                                                        DataViewFactory dataViewFactory,
                                                        ContentLinkBuilder contentLinkBuilder,
                                                        LiveContextSiteResolver liveContextSiteResolver,
                                                        TransformImageService transformImageService) {
    BlueprintHandlersCaeBaseLibConfiguration.configureHandlerBase(catalogPictureHandler,
            mimeTypeService,
            urlPathFormattingHelper,
            dataViewFactory,
            contentLinkBuilder);

    catalogPictureHandler.setSiteResolver(liveContextSiteResolver);
    catalogPictureHandler.setTransformImageService(transformImageService);
  }

  @Bean
  public AssetCommerceContextInterceptor assetCommerceContextInterceptor(SiteResolver siteResolver,
                                                                         CommerceConnectionSupplier commerceConnectionSupplier,
                                                                         LiveContextSiteResolver liveContextSiteResolver) {
    AssetCommerceContextInterceptor contextInterceptor = new AssetCommerceContextInterceptor();

    ECommerceCaeConfiguration.configureStoreContextInterceptor(contextInterceptor,
            siteResolver,
            commerceConnectionSupplier);

    contextInterceptor.setInitUserContext(false);
    contextInterceptor.setLiveContextSiteResolver(liveContextSiteResolver);

    return contextInterceptor;
  }

  @Bean
  public MappedInterceptor mappedAssetCommerceContextInterceptor(HandlerInterceptor assetCommerceContextInterceptor) {
    MappedInterceptor mappedInterceptor = new MappedInterceptor();

    mappedInterceptor.setInterceptor(assetCommerceContextInterceptor);
    mappedInterceptor.setIncludePatterns(List.of(CategoryCatalogPictureHandler.IMAGE_URI_PATTERN,
            CategoryCatalogPictureHandler.IMAGE_URI_PATTERN_FOR_CATALOG,
            ProductCatalogPictureHandler.IMAGE_URI_PATTERN,
            ProductCatalogPictureHandler.IMAGE_URI_PATTERN_FOR_CATALOG));

    return mappedInterceptor;
  }

  /**
   * Add commerce context interceptors to the beginning
   */
  @Bean(autowireCandidate = false)
  @Customize(value = "handlerInterceptors", mode = Customize.Mode.PREPEND)
  @Order(9999999)
  public MappedInterceptor appendLcAssetManagementInterceptors(MappedInterceptor mappedAssetCommerceContextInterceptor) {
    return mappedAssetCommerceContextInterceptor;
  }

}
