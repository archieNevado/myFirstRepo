package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
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
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        XmlRepoConfiguration.class,
        AugmentationAutoConfiguration.class,
        LcStudioValidatorsXmlRepoTest.LocalConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LcStudioValidatorsXmlRepoTest {

  private static final String PROPERTY_EXTERNAL_ID = "externalId";
  private static final String PROPERTY_SEGMENT = "segment";

  @Autowired
  private ContentRepository contentRepository;

  @Autowired
  private SitesService sitesService;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  private ApplicationContextCapTypeValidators testling;

  @Autowired
  private AutowireCapableBeanFactory beanFactory;

  @Autowired
  private CatalogLinkValidator marketingSpotExternalIdValidator;

  @Autowired
  private CatalogLinkValidator productListExternalIdValidator;

  @Autowired
  private CatalogLinkValidator externalChannelExternalIdValidator;

  @Autowired
  private CatalogLinkValidator externalPageExternalIdValidator;

  @Autowired
  private SegmentFormatValidator segmentFormatValidator;

  @Autowired
  private MasterLinkInAugmentationValidator masterLinkInCategoryAugmentationValidator;

  @Autowired
  private ExternalPagePartOfNavigationValidator externalPagePartOfNavigationValidator;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  private Site site;

  @Before
  public void setup() {
    openMocks(this);

    String siteId = "theSiteId";
    site = sitesService.getSite(siteId);

    StoreContextImpl storeContext = StoreContextBuilderImpl.from(commerceConnection, siteId).build();

    when(storeContextProvider.buildContext(any())).thenReturn(StoreContextBuilderImpl.from(storeContext));

    when(commerceConnection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(commerceConnection.getInitialStoreContext()).thenReturn(storeContext);
    when(commerceConnection.getStoreContextProvider()).thenReturn(storeContextProvider);

    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.of(commerceConnection));
  }

  @After
  public void teardown() {
    CurrentStoreContext.remove();
  }

  @Test
  public void testEmptyProperty() {
    Iterable<Issue> issues = validate(12, PROPERTY_EXTERNAL_ID);

    assertIssueCode(issues, "CMExternalChannel_EmptyCategory");
  }

  @Test
  public void testInvalidExternalId() {
    ReflectionTestUtils.setField(externalChannelExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    Iterable<Issue> issues = validate(14, PROPERTY_EXTERNAL_ID);

    assertIssueCode(issues, "CMExternalChannel_InvalidId");
  }

  @Test
  public void validateMarketingSpotWithNoIssues() {
    ReflectionTestUtils.setField(marketingSpotExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(20, PROPERTY_EXTERNAL_ID);

    assertNull(issues);
  }

  @Test
  public void validateProductListWithCategory() {
    ReflectionTestUtils.setField(productListExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(30, PROPERTY_EXTERNAL_ID);

    assertNull(issues);
  }

  @Test
  public void validateProductListWithoutCategory() {
    ReflectionTestUtils.setField(productListExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(any(), any(StoreContext.class)))
            .then(invocationOnMock -> mock(CommerceBean.class));

    Iterable<Issue> issues = validate(32, PROPERTY_EXTERNAL_ID);

    assertNull(issues);
  }

  @Test
  public void storeContextNotFound() {
    // create new validator
    //noinspection TypeMayBeWeakened
    CatalogLinkValidator validator =
            new CatalogLinkValidator(Objects.requireNonNull(contentRepository.getContentType("CMMarketingSpot")),
                                     false, commerceConnectionSupplier, sitesService, PROPERTY_EXTERNAL_ID);

    when(commerceConnectionSupplier.findConnection(site)).thenThrow(CommerceException.class);
    ReflectionTestUtils.setField(validator, "commerceConnectionSupplier", commerceConnectionSupplier);

    Iterable<Issue> issues = validate(validator, 20, PROPERTY_EXTERNAL_ID);

    assertIssueCode(issues, "CMMarketingSpot_StoreContextNotFound");
  }

  @Test
  public void testExternalPageEmptyExternalId() {
    Iterable<Issue> issues = validate(22, PROPERTY_EXTERNAL_ID);

    assertIssueCode(issues, "CMExternalPage_EmptyExternalPageId");
  }

  @Test
  public void testExternalPageNonEmptyExternalId() {
    ReflectionTestUtils.setField(externalPageExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    Iterable<Issue> issues = validate(24, PROPERTY_EXTERNAL_ID);

    assertNull(issues);
  }

  @Test
  public void marketingSpotEmptyExternalId() {
    ReflectionTestUtils.setField(marketingSpotExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);

    Iterable<Issue> issues = validate(26, PROPERTY_EXTERNAL_ID);
    assertIssueCode(issues, "CMMarketingSpot_EmptyExternalId");
  }

  @Test
  public void productListInvalidExternalId() {
    ReflectionTestUtils.setField(productListExternalIdValidator, "commerceConnectionSupplier", commerceConnectionSupplier);
    Iterable<Issue> issues = validate(34, PROPERTY_EXTERNAL_ID);
    assertIssueCode(issues, "CMProductList_InvalidId");
  }

  @Test
  public void externalPageNotPartOfNavigation() {
    Iterable<Issue> issues = validate(externalPagePartOfNavigationValidator, 110, null);
    assertIssueCode(issues, "not_in_navigation");
  }

  @Test
  public void segmentWithThreeSpecialChars() {
    ReflectionTestUtils.setField(segmentFormatValidator, "commerceConnectionSupplier", commerceConnectionSupplier);
    Iterable<Issue> issues = validate(200, PROPERTY_SEGMENT);
    assertNull(issues);
  }

  @Test
  public void segmentWithLeadingSpecialChars() {
    ReflectionTestUtils.setField(segmentFormatValidator, "commerceConnectionSupplier", commerceConnectionSupplier);
    Iterable<Issue> issues = validate(202, PROPERTY_SEGMENT);
    assertIssueCode(issues, "CMChannel_SegmentReservedPrefix");
  }

  @Test
  public void segmentWithTrailingSpecialChars() {
    ReflectionTestUtils.setField(segmentFormatValidator, "commerceConnectionSupplier", commerceConnectionSupplier);
    Iterable<Issue> issues = validate(204, PROPERTY_SEGMENT);
    assertIssueCode(issues, "CMChannel_SegmentReservedSuffix");
  }

  @Test
  public void augmentedCategoryIsMissingMasterLinkButCandidateExists() {
    Iterable<Issue> issues = validate(masterLinkInCategoryAugmentationValidator, 20004, "master");
    assertIssueCode(issues, "possibly_missing_master_reference_from_derived_augmentation");
  }

  @Test
  public void augmentedCategoryIsMissingMasterLinkInDerivedCandidate() {
    Iterable<Issue> issues = validate(masterLinkInCategoryAugmentationValidator, 10004, null);
    assertIssueCode(issues, "possibly_missing_master_reference_from_master_augmentation");
  }

  @Test
  public void augmentedCategoryIsMissingMasterLinkAndNoCandidateExists() {
    Iterable<Issue> issues = validate(masterLinkInCategoryAugmentationValidator, 20006, "master");
    assertNull(issues);
  }

  private Iterable<Issue> validate(int contentId, String propertyName) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, emptySet());

    testling.validate(content, issues);

    //noinspection unchecked
    return (Iterable<Issue>) issues.getByProperty().get(propertyName);
  }

  private Iterable<Issue> validate(CapTypeValidator validator, int contentId, @Nullable String propertyName) {
    Content content = contentRepository.getContent(String.valueOf(contentId));
    IssuesImpl issues = new IssuesImpl<>(content, emptySet());

    validator.validate(content, issues);

    //noinspection unchecked
    return propertyName == null ? issues.getGlobal() : (Iterable<Issue>) issues.getByProperty().get(propertyName);
  }

  @SuppressWarnings("NewClassNamingConvention")
  @Configuration(proxyBeanMethods = false)
  @Import({LcStudioValidatorsConfiguration.class})
  public static class LocalConfig {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/lc/studio/lib/validators/lc-studio-lib-test-content.xml");
    }

    @SuppressWarnings("MethodMayBeStatic")
    @Bean
    ApplicationContextCapTypeValidators validators(List<CapTypeValidator> capTypeValidators) {
      return new ApplicationContextCapTypeValidators(capTypeValidators);
    }
  }

  private static void assertIssueCode(Iterable<Issue> issues, String expectedCode) {
    MatcherAssert.assertThat(issues, hasItem(code(expectedCode)));
  }

  static IssueCodeMatcher code(String expectedCode) {
    return new IssueCodeMatcher(expectedCode);
  }

  @SuppressWarnings("NewClassNamingConvention")
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
