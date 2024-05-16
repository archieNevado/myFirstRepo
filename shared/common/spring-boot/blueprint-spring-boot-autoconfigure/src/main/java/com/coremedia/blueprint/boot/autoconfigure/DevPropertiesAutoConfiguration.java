package com.coremedia.blueprint.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@Profile("dev")
@PropertySource("classpath:/com/coremedia/blueprint/boot/autoconfigure/default-dev.properties")
public class DevPropertiesAutoConfiguration {
}
