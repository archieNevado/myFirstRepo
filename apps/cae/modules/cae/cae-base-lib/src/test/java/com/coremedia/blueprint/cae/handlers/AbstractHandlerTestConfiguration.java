package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.objectserver.configuration.CaeConfigurationProperties;
import com.coremedia.objectserver.web.config.CaeHandlerServicesConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@EnableConfigurationProperties({
        DeliveryConfigurationProperties.class,
        CaeConfigurationProperties.class
})
@Import({
        CaeHandlerServicesConfiguration.class,
        ContentTestConfiguration.class,
        WebMvcAutoConfiguration.class,
})
abstract class AbstractHandlerTestConfiguration {

  @Bean
  MockMvc mockMvc(WebApplicationContext wac) {
    return MockMvcBuilders.webAppContextSetup(wac).build();
  }

}
