package com.coremedia.livecontext.hybris.links;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * expose the library's default configuration
 */
@Configuration
@PropertySource("classpath:/META-INF/coremedia/hybris-cae-defaults.properties")
class LcEcommerceHybrisCaeConfiguration {

}


