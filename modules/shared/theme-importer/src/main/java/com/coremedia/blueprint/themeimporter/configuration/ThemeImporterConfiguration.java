package com.coremedia.blueprint.themeimporter.configuration;

import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.inject.Inject;

@Configuration
@ImportResource(
        value = {
                "classpath:/com/coremedia/mimetype/mimetype-service.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
public class ThemeImporterConfiguration {
  private MimeTypeService mimeTypeService;
  private CapConnection capConnection;

  @Resource(name="mimeTypeService")
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Inject
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Bean(name="themeImporter")
  public ThemeImporter themeImporter() {
    return new ThemeImporter(capConnection, mimeTypeService);
  }
}
