package com.coremedia.blueprint.coderesources;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        ThemeServiceConfiguration.class,
})
public class ThemeServiceAutoConfiguration {

}
