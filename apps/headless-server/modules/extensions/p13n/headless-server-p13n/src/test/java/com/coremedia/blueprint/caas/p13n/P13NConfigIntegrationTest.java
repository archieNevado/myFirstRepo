package com.coremedia.blueprint.caas.p13n;

import com.coremedia.blueprint.base.caas.p13n.adapter.PersonalizationRule;
import com.coremedia.blueprint.base.caas.p13n.adapter.PersonalizationRulesAdapterFactory;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import graphql.GraphQLContext;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = P13NConfigIntegrationTest.TestConfig.class)
@Import(P13nConfig.class)
@ExtendWith({SpringExtension.class})
public class P13NConfigIntegrationTest {

  @Inject
  private PersonalizationRulesAdapterFactory personalizationRulesAdapterFactory;

  @Inject
  private ContentRepository contentRepository;

  @Mock
  private InstrumentationFieldFetchParameters parameters;

  @Mock
  private DataFetchingEnvironment environment;

  @Mock
  private GraphQLContext graphQLContext;

  @BeforeEach
  public void setup() {
    when(parameters.getEnvironment()).thenReturn(environment);
    when(environment.getGraphQlContext()).thenReturn(graphQLContext);
  }

  @Test
  void searchConfigTest() {
    Content content = contentRepository.getContent("2");

    List<PersonalizationRule> rules = personalizationRulesAdapterFactory.to(content, "rules").rules();
    assertEquals(2, rules.size());
  }

  @Configuration(proxyBeanMethods = false)
  @Import({XmlRepoConfiguration.class})
  public static class TestConfig {

    @Bean
    static XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/caas/p13n/contentrepository.xml");
    }
  }
}
