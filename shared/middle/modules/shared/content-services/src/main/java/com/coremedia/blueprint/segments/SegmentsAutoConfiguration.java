package com.coremedia.blueprint.segments;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(SegmentsConfiguration.class)
public class SegmentsAutoConfiguration {

}
