package com.coremedia.blueprint.cae.configuration;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.base.tree.TreeRelationServicesConfiguration;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.cache.Cache;
import com.coremedia.cache.config.CacheConfiguration;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

@Configuration(proxyBeanMethods = false)
@Import({
        CacheConfiguration.class,
        TreeRelationServicesConfiguration.class,
})
@ImportResource(value = {
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml",
        "classpath:/com/coremedia/cae/contentbean-services.xml",
        "classpath:/com/coremedia/cae/dataview-services.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@EnableConfigurationProperties({
        BlueprintPageCaeContentBeansConfigurationProperties.class
})
public class BlueprintPageCaeContentBeansConfiguration {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public PageImpl cmPage(DeliveryConfigurationProperties deliveryConfigurationProperties,
                         BlueprintPageCaeContentBeansConfigurationProperties contentbeansConfigurationProperties,
                         SitesService sitesService,
                         Cache cache,
                         TreeRelation<Content> navigationTreeRelation,
                         ContentBeanFactory contentBeanFactory,
                         DataViewFactory dataViewFactory) {
    PageImpl page = new PageImpl(
            deliveryConfigurationProperties.isDeveloperMode(),
            sitesService,
            cache,
            navigationTreeRelation,
            contentBeanFactory,
            dataViewFactory);
    page.setMergeCodeResources(contentbeansConfigurationProperties.isMergeCodeResources());
    return page;
  }
}
