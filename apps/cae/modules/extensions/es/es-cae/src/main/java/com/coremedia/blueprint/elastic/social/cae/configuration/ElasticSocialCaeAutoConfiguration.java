package com.coremedia.blueprint.elastic.social.cae.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * A Spring hook to enable the {@link ElasticSocialCaeConfigurationProperties}
 */
@AutoConfiguration
@EnableConfigurationProperties({ElasticSocialCaeConfigurationProperties.class})
public class ElasticSocialCaeAutoConfiguration {
}
