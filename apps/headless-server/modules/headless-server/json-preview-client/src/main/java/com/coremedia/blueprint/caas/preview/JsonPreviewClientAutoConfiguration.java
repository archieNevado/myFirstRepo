package com.coremedia.blueprint.caas.preview;

import com.coremedia.blueprint.caas.preview.client.JsonPreviewApplication;
import com.coremedia.blueprint.caas.preview.client.JsonPreviewConfigurationProperties;
import com.coremedia.blueprint.caas.preview.client.JsonPreviewController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "caas.preview", havingValue = "true")
@Import({
        Application.class,
        JsonPreviewApplication.class,
        JsonPreviewController.class,
})
@EnableConfigurationProperties({
        JsonPreviewConfigurationProperties.class,
})
public class JsonPreviewClientAutoConfiguration {
}
