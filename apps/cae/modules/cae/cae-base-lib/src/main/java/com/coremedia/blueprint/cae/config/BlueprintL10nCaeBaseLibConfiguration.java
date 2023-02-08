package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.localization.configuration.TaxonomyLocalizationStrategyConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        TaxonomyLocalizationStrategyConfiguration.class,
})
/*
 * Localization (L10n) features
 */
public class BlueprintL10nCaeBaseLibConfiguration {
}
