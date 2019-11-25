package com.coremedia.lc.studio.lib;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan
@ImportResource(value = {
        "classpath:/com/coremedia/lc/studio/lib/validators.xml",
        "classpath:/com/coremedia/lc/studio/lib/placements.xml",
        "classpath:/META-INF/coremedia/lc-services.xml",
        "classpath:/com/coremedia/ecommerce/studio/rest/cache.xml"
},
reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
public class LcStudioLibComponentAutoConfiguration {
}
