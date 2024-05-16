package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.search.HeadlessSearchAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = HeadlessSearchAutoConfiguration.class)
@Import(HeadlessAugmentationConfiguration.class)
@ConditionalOnBean(name = "searchSchemaResources")
public class HeadlessAugmentationAutoConfiguration {
}
