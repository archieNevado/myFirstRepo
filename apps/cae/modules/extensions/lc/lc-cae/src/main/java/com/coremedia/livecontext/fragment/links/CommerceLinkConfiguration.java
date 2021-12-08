package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.livecontext.fragment.links.transformers.StoreContextTokenReplacingLinkTransformer;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLinkResolver;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@ImportResource(
        value = {"classpath:/META-INF/coremedia/livecontext-fragment.xml",
                "classpath:/META-INF/coremedia/livecontext-links.xml",
                "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml"},
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@DefaultAnnotation(NonNull.class)
public class CommerceLinkConfiguration {

  @Bean
  CommerceLinkResolver commerceLinkResolver(ExternalSeoSegmentBuilder seoSegmentBuilder) {
    return new CommerceLinkResolver(seoSegmentBuilder);
  }

  @Bean
  CommerceLinkHelper commerceLinkHelper(CommerceLedLinkBuilderHelper commerceLedPageExtension,
                                        SettingsService settingsService,
                                        CommerceConnectionSupplier commerceConnectionSupplier) {
    return new CommerceLinkHelper(commerceLedPageExtension, settingsService, commerceConnectionSupplier);
  }

  @Bean
  CommerceLinks commerceLinks(CommerceLinkHelper commerceLinkHelper,
                              ExternalSeoSegmentBuilder seoSegmentBuilder) {
    var commerceStudioLinks = new CommerceStudioLinks(seoSegmentBuilder, commerceLinkHelper);
    var commerceContentLedLinks = new CommerceContentLedLinks(commerceLinkHelper, seoSegmentBuilder);
    return new CommerceLinks(commerceLinkHelper, commerceStudioLinks, commerceContentLedLinks);
  }

  @Bean
  @Customize(value = "linkTransformers", mode = Customize.Mode.PREPEND)
  StoreContextTokenReplacingLinkTransformer storeContextTokenReplacingLinkTransformer() {
    return new StoreContextTokenReplacingLinkTransformer();
  }
}
