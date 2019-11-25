package com.coremedia.blueprint.assets.studio;


import com.coremedia.blueprint.assets.studio.upload.AMDoctypeRewriteUploadInterceptor;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.lang.NonNull;

@Configuration
@ImportResource(value = "classpath:/com/coremedia/cap/common/uapi-services.xml",
                reader = ResourceAwareXmlBeanDefinitionReader.class)
public class AMStudioRestConfiguration {

  private static final String CM_PICTURE_DOCTYPE = "CMPicture";
  private static final String CM_VIDEO_DOCTYPE = "CMVideo";
  private static final String CM_ARTICLE_DOCTYPE = "CMArticle";
  private static final String AM_PICTURE_ASSET_DOCTYPE = "AMPictureAsset";
  private static final String AM_VIDEO_ASSET_DOCTYPE = "AMVideoAsset";
  private static final String AM_DOCUMENT_ASSET_DOCTYPE = "AMDocumentAsset";

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
    ContentType articleDoctype = contentRepository.getContentType(CM_ARTICLE_DOCTYPE);
    ContentType amDocumentDoctype = contentRepository.getContentType(AM_DOCUMENT_ASSET_DOCTYPE);
    if (articleDoctype == null || amDocumentDoctype == null) {
      throw new IllegalStateException("Can not initialize UploadInterceptor. Missing Doctype " + CM_ARTICLE_DOCTYPE + " or " + AM_DOCUMENT_ASSET_DOCTYPE);
    }

    return new AMDoctypeRewriteUploadInterceptor(articleDoctype, amDocumentDoctype);
  }
}
