package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cache.Cache;
import com.coremedia.cache.config.CacheConfigurationProperties;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.asset.util.AssetReadSettingsHelper;
import com.coremedia.livecontext.asset.util.AssetWriteSettingsHelper;
import com.coremedia.livecontext.studio.asset.validators.LcAssetValidatorsConfiguration;
import com.coremedia.rest.cap.CapRestServiceSearchConfiguration;
import com.coremedia.rest.cap.content.search.SearchService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@Import({
        CapRestServiceSearchConfiguration.class,
        BaseCommerceServicesConfiguration.class,
        LcAssetValidatorsConfiguration.class
})
@ImportResource(value = {
        "classpath:/framework/spring/lc-asset-helpers.xml",
        "classpath:/framework/spring/lc-asset-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcAssetConfiguration {

  private static final String CMDOWNLOAD_NAME = "CMDownload";
  private static final String CMPICTURE_NAME = "CMPicture";
  private static final String CMSPINNER_NAME = "CMSpinner";
  private static final String CMVIDEO_NAME = "CMVideo";

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWriteInterceptor assetDownloadInvalidationWriteInterceptor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                              CommerceConnectionSupplier commerceConnectionSupplier,
                                                                              ContentRepository contentRepository) {
    return createInvalidationWriteInterceptor(commerceCacheInvalidationSource, commerceConnectionSupplier,
            contentRepository, CMDOWNLOAD_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWriteInterceptor assetPictureInvalidationWriteInterceptor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                             CommerceConnectionSupplier commerceConnectionSupplier,
                                                                             ContentRepository contentRepository) {
    return createInvalidationWriteInterceptor(commerceCacheInvalidationSource, commerceConnectionSupplier,
            contentRepository, CMPICTURE_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  SpinnerAssetInvalidationWriteInterceptor assetSpinnerInvalidationWriteInterceptor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                    CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                    AssetReadSettingsHelper assetReadSettingsHelper,
                                                                                    AssetWriteSettingsHelper assetWriteSettingsHelper,
                                                                                    ContentRepository contentRepository) {
    SpinnerAssetInvalidationWriteInterceptor interceptor = new SpinnerAssetInvalidationWriteInterceptor(commerceCacheInvalidationSource,
            commerceConnectionSupplier, assetReadSettingsHelper, assetWriteSettingsHelper, contentRepository);
    interceptor.setInterceptingSubtypes(true);

    ContentType contentType = contentRepository.getContentType(CMSPINNER_NAME);
    interceptor.setType(contentType);
    return interceptor;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWriteInterceptor assetVideoInvalidationWriteInterceptor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                           CommerceConnectionSupplier commerceConnectionSupplier,
                                                                           ContentRepository contentRepository) {
    return createInvalidationWriteInterceptor(commerceCacheInvalidationSource, commerceConnectionSupplier,
            contentRepository, CMVIDEO_NAME);
  }

  private static AssetInvalidationWriteInterceptor createInvalidationWriteInterceptor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                      CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                      ContentRepository contentRepository,
                                                                                      String contentTypeName) {
    AssetInvalidationWriteInterceptor interceptor = new AssetInvalidationWriteInterceptor(commerceConnectionSupplier,
            commerceCacheInvalidationSource);
    interceptor.setInterceptingSubtypes(true);

    ContentType contentType = contentRepository.getContentType(contentTypeName);
    interceptor.setType(contentType);
    return interceptor;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWritePostProcessor assetDownloadInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                  CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                  ContentRepository contentRepository) {
    return createInvalidationWritePostProcessor(commerceCacheInvalidationSource, commerceConnectionSupplier, contentRepository, CMDOWNLOAD_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWritePostProcessor assetPictureInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                 CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                 ContentRepository contentRepository) {
    return createInvalidationWritePostProcessor(commerceCacheInvalidationSource, commerceConnectionSupplier, contentRepository, CMPICTURE_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWritePostProcessor assetSpinnerInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                 CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                 ContentRepository contentRepository) {
    return createInvalidationWritePostProcessor(commerceCacheInvalidationSource, commerceConnectionSupplier, contentRepository, CMSPINNER_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationWritePostProcessor assetVideoInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                               CommerceConnectionSupplier commerceConnectionSupplier,
                                                                               ContentRepository contentRepository) {
    return createInvalidationWritePostProcessor(commerceCacheInvalidationSource, commerceConnectionSupplier, contentRepository, CMVIDEO_NAME);
  }

  private static AssetInvalidationWritePostProcessor createInvalidationWritePostProcessor(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                                          CommerceConnectionSupplier commerceConnectionSupplier,
                                                                                          ContentRepository contentRepository,
                                                                                          String contentTypeName) {
    AssetInvalidationWritePostProcessor processor = new AssetInvalidationWritePostProcessor(commerceCacheInvalidationSource, commerceConnectionSupplier);
    ContentType contentType = contentRepository.getContentType(contentTypeName);
    processor.setType(contentType);
    processor.setPostProcessingSubtypes(true);
    return processor;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  AssetInvalidationRepositoryListener assetInvalidationRepositoryListener(CommerceCacheInvalidationSource commerceCacheInvalidationSource,
                                                                          ContentRepository contentRepository) {
    return new AssetInvalidationRepositoryListener(commerceCacheInvalidationSource, contentRepository);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  BlobUploadXmpDataInterceptor pictureUploadXmpDataInterceptor(CommerceConnectionSupplier commerceConnectionSupplier,
                                                               AssetHelper assetHelper,
                                                               ContentRepository contentRepository) {
    return createUploadXmpDataInterceptor(commerceConnectionSupplier, assetHelper, contentRepository, CMPICTURE_NAME);
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  BlobUploadXmpDataInterceptor videoUploadXmpDataInterceptor(CommerceConnectionSupplier commerceConnectionSupplier,
                                                             AssetHelper assetHelper,
                                                             ContentRepository contentRepository) {
    return createUploadXmpDataInterceptor(commerceConnectionSupplier, assetHelper, contentRepository, CMVIDEO_NAME);
  }

  private static BlobUploadXmpDataInterceptor createUploadXmpDataInterceptor(CommerceConnectionSupplier commerceConnectionSupplier,
                                                              AssetHelper assetHelper,
                                                              ContentRepository contentRepository,
                                                              String contentTypeName) {
    BlobUploadXmpDataInterceptor interceptor = new BlobUploadXmpDataInterceptor(commerceConnectionSupplier, "data", assetHelper);
    interceptor.setPriority(1);

    ContentType contentType = contentRepository.getContentType(contentTypeName);
    interceptor.setType(contentType);
    return interceptor;
  }

  @SuppressWarnings("MethodMayBeStatic")
  @Bean
  StudioAssetSearchService studioAssetSearchService(SearchService searchService,
                                                    ContentRepository contentRepository,
                                                    Cache cache,
                                                    CacheConfigurationProperties properties) {
   return new StudioAssetSearchService(searchService, contentRepository, cache, properties);
  }

}
