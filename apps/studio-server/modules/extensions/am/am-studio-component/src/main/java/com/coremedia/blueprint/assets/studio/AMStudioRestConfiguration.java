package com.coremedia.blueprint.assets.studio;
import com.coremedia.blueprint.assets.AssetManagementConfigurationProperties;
import com.coremedia.blueprint.assets.studio.intercept.LinkedAssetMetadataExtractorInterceptor;
import com.coremedia.blueprint.assets.studio.intercept.UpdateAssetMetadataWriteInterceptor;
import com.coremedia.blueprint.assets.studio.upload.AMDoctypeRewriteUploadInterceptor;
import com.coremedia.blueprint.assets.studio.validation.AssetValidatorsConfiguration;
import com.coremedia.blueprint.base.rest.BlueprintBaseStudioRestConfiguration;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.rest.cap.configuration.ConfigurationPublisher;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {"classpath:/com/coremedia/cap/common/uapi-services.xml"},
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        AssetValidatorsConfiguration.class,
        BlueprintBaseStudioRestConfiguration.class
})
@EnableConfigurationProperties({
        AssetManagementConfigurationProperties.class
})
public class AMStudioRestConfiguration {

  private static final String CM_PICTURE_DOCTYPE = "CMPicture";
  private static final String CM_VIDEO_DOCTYPE = "CMVideo";
  private static final String AM_PICTURE_ASSET_DOCTYPE = "AMPictureAsset";
  private static final String AM_VIDEO_ASSET_DOCTYPE = "AMVideoAsset";
  private static final String AM_DOCUMENT_ASSET_DOCTYPE = "AMDocumentAsset";

  /**
   * A write interceptor that reacts to writes on asset blob properties (renditions) by storing parsed rendition metadata.
   */
  @Bean
  UpdateAssetMetadataWriteInterceptor updateAssetMetadataWriteInterceptor(ContentRepository contentRepository) {
    UpdateAssetMetadataWriteInterceptor updateAssetMetadataWriteInterceptor = new UpdateAssetMetadataWriteInterceptor();
    updateAssetMetadataWriteInterceptor.setType(contentRepository.getContentType("AMAsset"));
    updateAssetMetadataWriteInterceptor.setInterceptingSubtypes(true);
    updateAssetMetadataWriteInterceptor.setMetadataProperty("metadata");
    updateAssetMetadataWriteInterceptor.setMetadataSourceProperty("original");
    return updateAssetMetadataWriteInterceptor;
  }

  /**
   * A write interceptor that reacts to writes on pictures that link to a picture asset.
   * The picture asset metadata are extracted and provided for subsequent interceptors
   */
  @Bean
  LinkedAssetMetadataExtractorInterceptor updatePictureMetadataWriteInterceptor(ContentRepository contentRepository) {
    LinkedAssetMetadataExtractorInterceptor linkedAssetMetadataExtractorInterceptor = new LinkedAssetMetadataExtractorInterceptor();

    linkedAssetMetadataExtractorInterceptor.setPriority(-1);
    linkedAssetMetadataExtractorInterceptor.setType(contentRepository.getContentType("CMPicture"));
    linkedAssetMetadataExtractorInterceptor.setInterceptingSubtypes(true);
    linkedAssetMetadataExtractorInterceptor.setAssetMetadataProperty("metadata");
    linkedAssetMetadataExtractorInterceptor.setAssetLinkProperty("asset");
    linkedAssetMetadataExtractorInterceptor.setLinkedAssetType("AMPictureAsset");

    return linkedAssetMetadataExtractorInterceptor;
  }


  /**
   * A write interceptor that reacts to writes on videos that link to a video asset.
   * The video asset metadata are extracted and provided for subsequent interceptors
   */
  @Bean
  LinkedAssetMetadataExtractorInterceptor updateVideoMetadataWriteInterceptor(ContentRepository contentRepository) {
    LinkedAssetMetadataExtractorInterceptor linkedAssetMetadataExtractorInterceptor = new LinkedAssetMetadataExtractorInterceptor();

    linkedAssetMetadataExtractorInterceptor.setPriority(-1);
    linkedAssetMetadataExtractorInterceptor.setType(contentRepository.getContentType("CMVideo"));
    linkedAssetMetadataExtractorInterceptor.setInterceptingSubtypes(true);
    linkedAssetMetadataExtractorInterceptor.setAssetMetadataProperty("metadata");
    linkedAssetMetadataExtractorInterceptor.setAssetLinkProperty("asset");
    linkedAssetMetadataExtractorInterceptor.setLinkedAssetType("AMVideoAsset");

    return linkedAssetMetadataExtractorInterceptor;
  }

  /**
   * Makes the asset management configuration available on client at
   * editorContext.getConfiguration().assetManagement.
   */
  @Bean
  ConfigurationPublisher assetManagementConfigurationPublisher(AssetManagementConfigurationProperties assetManagementConfigurationProperties) {
    ConfigurationPublisher configurationPublisher = new ConfigurationPublisher();
    configurationPublisher.setName("assetManagement");
    AssetManagementConfiguration assetManagementConfiguration = new AssetManagementConfiguration();
    assetManagementConfiguration.setSettingsDocument(assetManagementConfigurationProperties.getSettingsDocument());
    configurationPublisher.setConfiguration(assetManagementConfiguration);
    return configurationPublisher;
  }

  @Bean
  public AMDoctypeRewriteUploadInterceptor amPictureUploadInterceptor(@NonNull ContentRepository contentRepository) {
    ContentType pictureDoctype = contentRepository.getContentType(CM_PICTURE_DOCTYPE);
    ContentType amPictureDoctype = contentRepository.getContentType(AM_PICTURE_ASSET_DOCTYPE);

    if (pictureDoctype == null || amPictureDoctype == null) {
      throw new IllegalStateException("Can not initialize UploadInterceptor. Missing Doctype " + CM_PICTURE_DOCTYPE + " or " + AM_PICTURE_ASSET_DOCTYPE);
    }

    return new AMDoctypeRewriteUploadInterceptor(pictureDoctype, amPictureDoctype);
  }

  @Bean
  public AMDoctypeRewriteUploadInterceptor amVideoUploadInterceptor(@NonNull ContentRepository contentRepository) {
    ContentType videoDoctype = contentRepository.getContentType(CM_VIDEO_DOCTYPE);
    ContentType amVideoDoctype = contentRepository.getContentType(AM_VIDEO_ASSET_DOCTYPE);
    if (videoDoctype == null || amVideoDoctype == null) {
      throw new IllegalStateException("Can not initialize UploadInterceptor. Missing Doctype " + CM_VIDEO_DOCTYPE + " or " + AM_VIDEO_ASSET_DOCTYPE);
    }

    return new AMDoctypeRewriteUploadInterceptor(videoDoctype, amVideoDoctype);
  }

  @Bean
  public AMDoctypeRewriteUploadInterceptor amDocumentUploadInterceptor(@NonNull ContentRepository contentRepository) {
    ContentType amDocumentDoctype = contentRepository.getContentType(AM_DOCUMENT_ASSET_DOCTYPE);
    if (amDocumentDoctype == null) {
      throw new IllegalStateException("Can not initialize UploadInterceptor. Missing Doctype " + AM_DOCUMENT_ASSET_DOCTYPE);
    }

    AMDoctypeRewriteUploadInterceptor amDoctypeRewriteUploadInterceptor = new AMDoctypeRewriteUploadInterceptor(contentRepository.getContentContentType(), amDocumentDoctype);
    amDoctypeRewriteUploadInterceptor.setInterceptingSubtypes(true);
    // avoid that this interceptor is run before the other AMDoctypeRewriteUploadInterceptors
    amDoctypeRewriteUploadInterceptor.setPriority(amDoctypeRewriteUploadInterceptor.getPriority() + 1);
    return amDoctypeRewriteUploadInterceptor;
  }
}
