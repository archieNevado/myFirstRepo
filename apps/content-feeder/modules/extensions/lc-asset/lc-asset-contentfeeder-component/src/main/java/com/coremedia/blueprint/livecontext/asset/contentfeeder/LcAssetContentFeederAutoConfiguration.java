package com.coremedia.blueprint.livecontext.asset.contentfeeder;

import com.coremedia.livecontext.studio.asset.validators.LcAssetValidatorsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(LcAssetValidatorsConfiguration.class)
public class LcAssetContentFeederAutoConfiguration {
}
