package com.coremedia.blueprint.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@PropertySource("classpath:/com/coremedia/blueprint/boot/autoconfigure/default.properties")
public class DefaultPropertiesAutoConfiguration {
}
