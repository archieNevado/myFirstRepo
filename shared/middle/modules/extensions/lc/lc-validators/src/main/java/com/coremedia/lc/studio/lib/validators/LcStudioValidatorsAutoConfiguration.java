package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = BaseCommerceServicesAutoConfiguration.class)
@Import({
        LcStudioValidatorsConfiguration.class,
        LcUniqueInSiteValidatorsConfiguration.class,
})
public class LcStudioValidatorsAutoConfiguration {

}
