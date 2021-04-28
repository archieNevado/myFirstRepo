package com.coremedia.blueprint.caas.augmentation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(HeadlessAugmentationConfiguration.class)
public class HeadlessAugmentationAutoConfiguration {
}
