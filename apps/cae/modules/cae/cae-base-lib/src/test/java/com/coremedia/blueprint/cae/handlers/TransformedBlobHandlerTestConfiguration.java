package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.cae.configuration.BlueprintPageCaeContentBeansConfiguration;
import com.coremedia.blueprint.cae.util.DefaultSecureHashCodeGeneratorStrategy;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        BlueprintPageCaeContentBeansConfiguration.class,
        ContentTestConfiguration.class,
        XmlRepoConfiguration.class,
})
class TransformedBlobHandlerTestConfiguration extends AbstractHandlerTestConfiguration {

  @Bean
  public XmlUapiConfig xmlUapiConfig() {
    return new XmlUapiConfig(
            "classpath:/com/coremedia/blueprint/cae/handlers/blob-test-content.xml",
            "classpath:/com/coremedia/blueprint/cae/handlers/blob-test-users.xml"
    );
  }

  @Bean
  TransformedBlobHandler transformedBlobHandler(MimeTypeService mimeTypeService,
                                                UrlPathFormattingHelper urlPathFormattingHelper,
                                                ContentLinkBuilder contentLinkBuilder,
                                                TransformImageService transformImageService,
                                                DataViewFactory dataViewFactory,
                                                ValidationService<ContentBean> validationService) {
    TransformedBlobHandler testling = new TransformedBlobHandler();
    testling.setMimeTypeService(mimeTypeService);
    testling.setUrlPathFormattingHelper(urlPathFormattingHelper);
    testling.setContentLinkBuilder(contentLinkBuilder);
    DefaultSecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy = new DefaultSecureHashCodeGeneratorStrategy();
    secureHashCodeGeneratorStrategy.setEncoding("UTF-8");
    testling.setSecureHashCodeGeneratorStrategy(secureHashCodeGeneratorStrategy);
    testling.setTransformImageService(transformImageService);
    testling.setDataViewFactory(dataViewFactory);
    testling.setValidationService(validationService);
    return testling;
  }

}
