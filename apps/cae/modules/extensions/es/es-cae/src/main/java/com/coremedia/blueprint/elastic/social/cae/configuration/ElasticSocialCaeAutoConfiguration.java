package com.coremedia.blueprint.elastic.social.cae.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * A Spring hook to enable the {@link ElasticSocialCaeConfigurationProperties}
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ElasticSocialCaeConfigurationProperties.class})
public class ElasticSocialCaeAutoConfiguration {
}
