package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.rest.cap.validation.CapTypeValidator;
import com.coremedia.rest.cap.validation.impl.ApplicationContextCapTypeValidators;
import com.coremedia.rest.validation.impl.Issue;
import com.coremedia.rest.validation.impl.IssuesImpl;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, LcStudioValidatorsXmlRepoTest.LocalConfig.class})
public class LcStudioValidatorsXmlRepoTest {

  private static final String PROPERTY_NAME = "externalId";
  private static final String WORKSPACE_1 = "workspace1";
  private static final String WORKSPACE_2 = "workspace2";

  @Inject
  private ContentRepository contentRepository;
  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ApplicationContextCapTypeValidators testling;
  @Inject
  private AutowireCapableBeanFactory beanFactory;

  Iterable<Issue> validate(int contentId) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, Collections.<String>emptySet());
    testling.validate(content, issues);
    //noinspection unchecked
    return (Iterable<Issue>) issues.getByProperty().get(PROPERTY_NAME);
  }

  Iterable<Issue> validate(CapTypeValidator validator, int contentId) {
    return validate(validator, contentId, PROPERTY_NAME);
  }

  Iterable<Issue> validate(CapTypeValidator validator, int contentId, @Nullable String propertyName) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, Collections.<String>emptySet());
    validator.validate(content, issues);
    //noinspection unchecked
    return propertyName==null ? issues.getGlobal() : (Iterable<Issue>) issues.getByProperty().get(propertyName);
  }

  @Before
  public void setup() {
    MockCommerceEnvBuilder.create().setupEnv();
    Commerce.getCurrentConnection().getStoreContext().put(StoreContextBuilder.SITE, "theSiteId");
  }

  @Test
  public void testEmptyProperty() {
    Iterable<Issue> issues = validate(12);

    assertIssueCode(issues, "CMExternalChannel_EmptyCategory");
  }

  @Test
  public void testInvalidExternalId() {
    Iterable<Issue> issues = validate(14);

    assertIssueCode(issues, "CMExternalChannel_InvalidId");
  }

  @Test
  public void testDuplicateExternalId() {
    Iterable<Issue> issues = validate(16);

    assertIssueCode(issues, "UniqueInSiteStringValidator");

    issues = validate(18);

    assertIssueCode(issues, "UniqueInSiteStringValidator");
  }

  @Test
  public void validateMarketingSpotWithNoIssues() {
    when(Commerce.getCurrentConnection().getCommerceBeanFactory().loadBeanFor(anyString(), any(StoreContext.class))).then(new Answer<CommerceBean>() {
      @Override
      public CommerceBean answer(InvocationOnMock invocationOnMock) throws Throwable {
        String externalId = BaseCommerceIdHelper.parseExternalIdFromId(((String) invocationOnMock.getArguments()[0]));
        if (externalId.contains("null")) {
          return null;
        }
        return mock(CommerceBean.class);
      }
    });

    Iterable<Issue> issues = validate(20);
    assertNull(issues);
  }

  @Test
  public void storeContextNotFound() {
    // create new validator
    CatalogLinkValidator validator = new CatalogLinkValidator();
    beanFactory.configureBean(validator, "marketingSpotExternalIdValidator");

    // set mocked context initializer so that the commerce connection won't be set
    validator.setCommerceConnectionInitializer(mock(CommerceConnectionInitializer.class));
    Commerce.clearCurrent();

    Iterable<Issue> issues = validate(validator, 20);

    assertIssueCode(issues, "CMMarketingSpot_StoreContextNotFound");
  }

  @Test
  public void testExternalPageEmptyExternalId() {
    Iterable<Issue> issues = validate(22);
    assertIssueCode(issues, "CMExternalPage_EmptyExternalPageId");
  }

  @Test
  public void testExternalPageNonEmptyExternalId() {
    Iterable<Issue> issues = validate(24);
    assertNull(issues);
  }

  @Test
  public void marketingSpotEmptyExternalId() {
    Iterable<Issue> issues = validate(26);
    assertIssueCode(issues, "CMMarketingSpot_EmptyExternalId");
  }

  @Test
  public void validOnlyInWorkspace() {

    CatalogLinkValidator validator = new ExternalChannelValidator();
    beanFactory.configureBean(validator, "externalChannelExternalIdValidator");
    validator.setCommerceConnectionInitializer(mock(CommerceConnectionInitializer.class));

    Workspace workspace1 = mock(Workspace.class);
    Workspace workspace2 = mock(Workspace.class);
    when(workspace1.getName()).thenReturn(WORKSPACE_1);
    when(workspace1.getExternalTechId()).thenReturn(WORKSPACE_1);
    when(workspace2.getName()).thenReturn(WORKSPACE_2);
    when(workspace2.getExternalTechId()).thenReturn(WORKSPACE_2);
    when(Commerce.getCurrentConnection().getWorkspaceService().findAllWorkspaces()).thenReturn(asList(workspace1, workspace2));

    StoreContext currentContext = Commerce.getCurrentConnection().getStoreContext();
    currentContext.put("configId", "myConfigId");
    currentContext.put("catalogId", "10001");
    currentContext.setWorkspaceId(WORKSPACE_1);
    when(Commerce.getCurrentConnection().getCommerceBeanFactory().loadBeanFor(anyString(), any(StoreContext.class))).then(new Answer<CommerceBean>() {
      @Override
      public CommerceBean answer(InvocationOnMock invocationOnMock) throws Throwable {
        if (WORKSPACE_1.equals(((StoreContext)invocationOnMock.getArguments()[1]).getWorkspaceId())) {
          return mock(CommerceBean.class);
        }
        return null;
      }
    });

    // validate
    Iterable<Issue> issues = validate(validator, 28);
    assertIssueCode(issues, "CMExternalChannel_ValidInAWorkspace");
  }

  @Test
  public void externalPageNotPartOfNavigation() {
    ExternalPagePartOfNavigationValidator validator = beanFactory.getBean(ExternalPagePartOfNavigationValidator.class);
    Iterable<Issue> issues = validate(validator, 110, null);

    assertIssueCode(issues, "not_in_navigation");
  }


  @Configuration
  @ImportResource(value = {
          "classpath:/com/coremedia/lc/studio/lib/validators.xml"
  },
          reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/lc/studio/lib/validators/lc-studio-lib-test-content.xml");
    }

    @Bean
    ApplicationContextCapTypeValidators validators() {
      return new ApplicationContextCapTypeValidators();
    }

    @Bean
    @Inject
    public Properties jerseyParameters(ApplicationContext applicationContext) throws IOException {
      final Resource resource = applicationContext.getResource("classpath:/com/coremedia/rest/jerseyParameters.properties");
      return PropertiesLoaderUtils.loadProperties(new EncodedResource(resource));
    }

  }

  void assertIssueCode(Iterable<Issue> issues, String expectedCode) {
    MatcherAssert.assertThat(issues, hasItem(code(expectedCode)));
  }

  private static IssueCodeMatcher code(String expectedCode) {
    return new IssueCodeMatcher(expectedCode);
  }

  private static class IssueCodeMatcher extends CustomTypeSafeMatcher<Issue> {
    private final String expectedCode;

    public IssueCodeMatcher(String expectedCode) {
      super("code: " + expectedCode);
      this.expectedCode = expectedCode;
    }

    @Override
    protected boolean matchesSafely(Issue item) {
      return item.getCode().equals(expectedCode);
    }

  }
}
