package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import com.coremedia.rest.cap.validation.CapTypeValidator;
import com.coremedia.rest.cap.validation.impl.ApplicationContextCapTypeValidators;
import com.coremedia.rest.validation.impl.Issue;
import com.coremedia.rest.validation.impl.IssuesImpl;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        XmlRepoConfiguration.class,
        LcStudioValidatorsXmlRepoTest.LocalConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LcStudioValidatorsXmlRepoTest {

  private static final String PROPERTY_NAME = "externalId";

  private static final WorkspaceId WORKSPACE_1 = WorkspaceId.of("workspace1");
  private static final WorkspaceId WORKSPACE_2 = WorkspaceId.of("workspace2");

  @Inject
  private ContentRepository contentRepository;

  @Inject
  private SitesService sitesService;

  private CommerceConnectionInitializer commerceConnectionInitializer;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ApplicationContextCapTypeValidators testling;

  @Inject
  private AutowireCapableBeanFactory beanFactory;

  private BaseCommerceConnection commerceConnection;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private WorkspaceService workspaceService;

  @Inject
  private CatalogLinkValidator marketingSpotExternalIdValidator;

  @Inject
  private CatalogLinkValidator productListExternalIdValidator;

  @Inject
  private CatalogLinkValidator externalChannelExternalIdValidator;

  @Inject
  private CatalogLinkValidator externalPageExternalIdValidator;

  private Site site;

  @Before
  public void setup() {
    initMocks(this);

    String siteId = "theSiteId";
    site = sitesService.getSite(siteId);

    commerceConnection = new BaseCommerceConnection();

    StoreContextImpl storeContext = StoreContextBuilderImpl.from()
            .withSiteId(siteId)
            .build();
    commerceConnection.setStoreContext(storeContext);

    when(storeContextProvider.buildContext(any())).thenReturn(StoreContextBuilderImpl.from(storeContext));

    commerceConnection.setCommerceBeanFactory(commerceBeanFactory);
    commerceConnection.setStoreContextProvider(storeContextProvider);
    commerceConnection.setWorkspaceService(workspaceService);

    commerceConnectionInitializer = mock(CommerceConnectionInitializer.class);
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(commerceConnection));
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testEmptyProperty() {
    Iterable<Issue> issues = validate(12);

    assertIssueCode(issues, "CMExternalChannel_EmptyCategory");
  }

  @Test
  public void testInvalidExternalId() {
    externalChannelExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    Iterable<Issue> issues = validate(14);

    assertIssueCode(issues, "CMExternalChannel_InvalidId");
  }

  @Test
  public void validateMarketingSpotWithNoIssues() {
    marketingSpotExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(20);

    assertNull(issues);
  }

  @Test
  public void validateProductListWithCategory() {
    productListExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(30);

    assertNull(issues);
  }

  @Test
  public void validateProductListWithoutCategory() {
    productListExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(32);

    assertNull(issues);
  }

  @Test
  public void storeContextNotFound() {
    // create new validator
    CatalogLinkValidator validator = new CatalogLinkValidator();
    beanFactory.configureBean(validator, "marketingSpotExternalIdValidator");

    when(commerceConnectionInitializer.findConnectionForSite(site)).thenThrow(CommerceException.class);
    validator.setCommerceConnectionInitializer(commerceConnectionInitializer);

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
    externalPageExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    Iterable<Issue> issues = validate(24);

    assertNull(issues);
  }

  @Test
  public void marketingSpotEmptyExternalId() {
    marketingSpotExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);
    Iterable<Issue> issues = validate(26);
    assertIssueCode(issues, "CMMarketingSpot_EmptyExternalId");
  }

  @Test
  public void productListInvalidExternalId() {
    productListExternalIdValidator.setCommerceConnectionInitializer(commerceConnectionInitializer);
    Iterable<Issue> issues = validate(34);
    assertIssueCode(issues, "CMProductList_InvalidId");
  }

  @Test
  public void validOnlyInWorkspace() {
    CatalogLinkValidator validator = new ExternalChannelValidator();
    beanFactory.configureBean(validator, "externalChannelExternalIdValidator");
    validator.setCommerceConnectionInitializer(commerceConnectionInitializer);

    Workspace workspace1 = mock(Workspace.class);
    Workspace workspace2 = mock(Workspace.class);
    when(workspace1.getName()).thenReturn(WORKSPACE_1.value());
    when(workspace1.getExternalTechId()).thenReturn(WORKSPACE_1.value());
    when(workspace2.getName()).thenReturn(WORKSPACE_2.value());
    when(workspace2.getExternalTechId()).thenReturn(WORKSPACE_2.value());
    when(commerceConnection.getWorkspaceService().findAllWorkspaces(any(StoreContext.class))).thenReturn(asList(workspace1, workspace2));

    StoreContext storeContext = commerceConnection.getStoreContext();
    storeContext = StoreContextBuilderImpl
            .from((StoreContextImpl) storeContext)
            .withWorkspaceId(WORKSPACE_1)
            .build();
    commerceConnection.setStoreContext(storeContext);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then((Answer<CommerceBean>) invocationOnMock -> {
              StoreContext context = (StoreContext) invocationOnMock.getArguments()[1];
              if (WORKSPACE_1.equals(context.getWorkspaceId().orElse(null))) {
                return mock(CommerceBean.class);
              }

              return null;
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

  private Iterable<Issue> validate(int contentId) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, emptySet());

    testling.validate(content, issues);

    //noinspection unchecked
    return (Iterable<Issue>) issues.getByProperty().get(PROPERTY_NAME);
  }

  private Iterable<Issue> validate(CapTypeValidator validator, int contentId) {
    return validate(validator, contentId, PROPERTY_NAME);
  }

  private Iterable<Issue> validate(CapTypeValidator validator, int contentId, @Nullable String propertyName) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, emptySet());

    validator.validate(content, issues);

    //noinspection unchecked
    return propertyName == null ? issues.getGlobal() : (Iterable<Issue>) issues.getByProperty().get(propertyName);
  }

  @Configuration
  @ImportResource(value = {
          "classpath:/com/coremedia/lc/studio/lib/validators.xml",
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
    public Properties jerseyParameters(ApplicationContext applicationContext) throws IOException {
      Resource resource = applicationContext.getResource("classpath:/com/coremedia/rest/jerseyParameters.properties");
      EncodedResource encodedResource = new EncodedResource(resource);
      return PropertiesLoaderUtils.loadProperties(encodedResource);
    }
  }

  private void assertIssueCode(Iterable<Issue> issues, String expectedCode) {
    MatcherAssert.assertThat(issues, hasItem(code(expectedCode)));
  }

  static IssueCodeMatcher code(String expectedCode) {
    return new IssueCodeMatcher(expectedCode);
  }

  private static class IssueCodeMatcher extends CustomTypeSafeMatcher<Issue> {

    private final String expectedCode;

    IssueCodeMatcher(String expectedCode) {
      super("code: " + expectedCode);
      this.expectedCode = expectedCode;
    }

    @Override
    protected boolean matchesSafely(Issue item) {
      return item.getCode().equals(expectedCode);
    }
  }
}
