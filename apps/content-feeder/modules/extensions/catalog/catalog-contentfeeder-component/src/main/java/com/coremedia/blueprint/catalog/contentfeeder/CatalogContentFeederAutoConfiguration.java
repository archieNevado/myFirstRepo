package com.coremedia.blueprint.catalog.contentfeeder;

import com.coremedia.catalog.studio.lib.validators.CatalogValidatorsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(CatalogValidatorsConfiguration.class)
public class CatalogContentFeederAutoConfiguration {
}
