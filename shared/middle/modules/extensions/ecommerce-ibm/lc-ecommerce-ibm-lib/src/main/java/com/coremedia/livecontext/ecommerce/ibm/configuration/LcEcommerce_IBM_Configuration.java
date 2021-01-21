package com.coremedia.livecontext.ecommerce.ibm.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * expose the library's default configuration
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Configuration(proxyBeanMethods = false)
@Deprecated
@PropertySource("classpath:framework/spring/lc-ecommerce-ibm.properties")
class LcEcommerce_IBM_Configuration {

}
