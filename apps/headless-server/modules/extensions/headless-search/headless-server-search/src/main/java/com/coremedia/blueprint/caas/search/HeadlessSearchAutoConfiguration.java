package com.coremedia.blueprint.caas.search;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(HeadlessSearchConfiguration.class)
public class HeadlessSearchAutoConfiguration {
}
