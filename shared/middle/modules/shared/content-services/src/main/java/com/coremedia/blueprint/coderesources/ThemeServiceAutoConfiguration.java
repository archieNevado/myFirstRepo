package com.coremedia.blueprint.coderesources;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        ThemeServiceConfiguration.class,
})
public class ThemeServiceAutoConfiguration {

}
