package com.coremedia.blueprint.caas.p13n;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(P13nConfig.class)
public class P13nAutoConfiguration {
}
