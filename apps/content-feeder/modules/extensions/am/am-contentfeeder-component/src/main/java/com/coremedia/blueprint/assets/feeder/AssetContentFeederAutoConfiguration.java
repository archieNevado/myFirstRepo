package com.coremedia.blueprint.assets.feeder;

import com.coremedia.blueprint.assets.studio.validation.AssetValidatorsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(AssetValidatorsConfiguration.class)
public class AssetContentFeederAutoConfiguration {
}
