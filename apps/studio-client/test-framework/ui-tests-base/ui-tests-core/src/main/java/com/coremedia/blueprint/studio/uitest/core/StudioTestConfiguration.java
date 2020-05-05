package com.coremedia.blueprint.studio.uitest.core;

import com.coremedia.cms.editor.test.context.EditorTestContextImpl;
import com.coremedia.qa.async.ConditionTimeout;
import com.coremedia.qa.async.context.AsyncSupportConfiguration;
import com.coremedia.qa.async.factory.ConditionFactory;
import com.coremedia.qa.async.factory.DefaultConditionFactory;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.xml.sax.XMLFilter;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Configuration
@ComponentScan(
        basePackages = {
                "com.coremedia.uitesting",
                "com.coremedia.blueprint.studio.uitest"
        },
        lazyInit = true
)
@ImportResource(value = {
        "classpath:com/coremedia/cap/common/uapi-services.xml",
        "classpath:META-INF/joala/condition/conditions-context.xml",
        "classpath:META-INF/joala/bdd/bdd-context.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import(AsyncSupportConfiguration.class)
public class StudioTestConfiguration {

  @Value("${context.content.editor.username}")
  private String editorTestUserName;
  @Value("${context.content.editor.usergroup}")
  private String repositoryTestUserGroup;
  @Value("${context.studio.main.url}")
  private String testApplicationUrl;
  @Value("${context.management.ior.url}")
  private String repositoryUrl;
  @Value("${context.studio.rest.entry.point}")
  private String studioRestEntryPoint;

  /**
   * Condition factory bean.
   *
   * @param conditionTimeout base timeout for all provided conditions
   * @return condition factory
   */
  @Bean
  @Scope(SCOPE_SINGLETON)
  // DevNote: Required to do it here explicitly, as the default bean name will
  // collide with ConditionFactory provided by Joala. This bean may/must
  // be removed, as soon as Joala Conditions is not used anymore.
  public ConditionFactory asyncConditionFactory(ConditionTimeout conditionTimeout) {
    return new DefaultConditionFactory(conditionTimeout);
  }

  @Bean
  public EditorTestContextImpl repositoryTestContext() {
    EditorTestContextImpl testContext = new EditorTestContextImpl();
    testContext.setEditorTestUserName(editorTestUserName);
    testContext.setRepositoryTestUserGroup(repositoryTestUserGroup);
    testContext.setTestApplicationUrl(testApplicationUrl);
    testContext.setRepositoryUrl(repositoryUrl);
    testContext.setStudioRestEntryPoint(studioRestEntryPoint);
    return testContext;
  }

  @Bean
  public Supplier<List<XMLFilter>> xmlFiltersSupplier() {
    return Collections::emptyList;
  }

}
