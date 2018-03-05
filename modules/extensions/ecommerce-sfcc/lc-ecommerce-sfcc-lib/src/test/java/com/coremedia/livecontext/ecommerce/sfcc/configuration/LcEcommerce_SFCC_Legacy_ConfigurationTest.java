package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccOcapiConfigurationProperties;
import com.coremedia.springframework.context.support.RequiredPropertySourcesPlaceholderConfigurerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        RequiredPropertySourcesPlaceholderConfigurerConfiguration.class
})
@TestPropertySource(properties= "livecontext.sfcc.ocapi.data.basePath=test")
@EnableConfigurationProperties(SfccOcapiConfigurationProperties.class)
public class LcEcommerce_SFCC_Legacy_ConfigurationTest {

  @Inject
  private SfccOcapiConfigurationProperties properties;

  @Test
  public void testLegacyProperties() {
    assertThat(properties).hasFieldOrPropertyWithValue("dataBasePath", "test");
  }

}