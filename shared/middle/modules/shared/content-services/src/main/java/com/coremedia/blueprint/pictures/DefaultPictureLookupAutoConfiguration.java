package com.coremedia.blueprint.pictures;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(DefaultPictureLookupConfiguration.class)
public class DefaultPictureLookupAutoConfiguration {

}
