package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.search.HeadlessSearchAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(HeadlessAugmentationConfiguration.class)
@AutoConfigureAfter(HeadlessSearchAutoConfiguration.class)
@ConditionalOnBean(name = "searchSchemaResources")
public class HeadlessAugmentationAutoConfiguration {
}
