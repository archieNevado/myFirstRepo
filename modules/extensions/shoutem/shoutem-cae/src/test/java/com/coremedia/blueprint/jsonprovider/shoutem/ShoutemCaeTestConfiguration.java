package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.cae.search.solr.SolrSearchResultFactory;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;

import static org.mockito.Mockito.mock;

@Configuration
@ImportResource(
        value = {
                "classpath:/META-INF/coremedia/component-shoutem-jsonprovider.xml",
                "classpath:com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
                "classpath:com/coremedia/blueprint/segments/blueprint-segments.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import(XmlRepoConfiguration.class)
@Profile(ShoutemCaeTestConfiguration.PROFILE)
class ShoutemCaeTestConfiguration {

  static final String PROFILE = "ShoutemCaeTestConfiguration";

  @Bean
  @Scope(BeanDefinition.SCOPE_SINGLETON)
  public XmlUapiConfig xmlUapiConfig() {
    return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
  }

  @Bean
  public LikeService likeService() {
    return mock(LikeService.class);
  }

  @Bean
  public CommentService commentService() {
    return mock(CommentService.class);
  }

  @Bean
  public CommunityUserService communityUserService() {
    return mock(CommunityUserService.class);
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return mock(AuthenticationManager.class);
  }

  @Bean
  public ElasticSocialPlugin elasticSocialPlugin() {
    return new ElasticSocialPlugin();
  }

  @Bean
  public static BeanPostProcessor searchEngineReplacer() {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SolrSearchResultFactory) {
          // don't use SOLR but a mocked search result factory for testing
          return mock(SearchResultFactory.class);
        }
        return bean;
      }

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
      }
    };
  }
}
