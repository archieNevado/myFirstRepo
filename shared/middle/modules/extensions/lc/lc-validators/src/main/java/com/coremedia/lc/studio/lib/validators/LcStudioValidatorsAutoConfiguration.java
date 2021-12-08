package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        LcStudioValidatorsConfiguration.class,
        LcUniqueInSiteValidatorsConfiguration.class,
})
@AutoConfigureAfter({
        BaseCommerceServicesAutoConfiguration.class,
})
public class LcStudioValidatorsAutoConfiguration {

}
