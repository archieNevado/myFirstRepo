package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.i18n.PageResourceBundleFactory;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMPlaceholder;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.controller.ContributionMessageKeys;
import com.coremedia.blueprint.elastic.social.cae.controller.HandlerInfo;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteHelper;
import com.coremedia.cap.user.User;
import com.coremedia.ecommerce.test.TestVendors;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReviewsResultHandlerTest {

  private final String contextId = "5678";
  private final String targetId = "vendor:///catalog/product/1234";
  private final String text = "test test test test test test test";
  private final String title = "title";
  private final int rating = 5;

  @InjectMocks
  private ProductReviewsResultHandler handler;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private PageResourceBundleFactory resourceBundleFactory;

  @Mock
  private ResourceBundle resourceBundle;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private Content navigationContent;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private CommunityUser user;

  @Mock
  private Review review;

  @Mock
  private CMContext context;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private Site site;

  @Mock
  private CMPlaceholder page;

  @Mock
  private UriTemplate uriTemplate;

  private HttpServletRequest request;

  @Mock
  private Product product;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private CMNavigation cmNavigation;

  @BeforeEach
  void setup() {
    request = new MockHttpServletRequest();
    RequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    handler.setElasticSocialUserHelper(new ElasticSocialUserHelper(communityUserService));
    handler.setContextHelper(contextHelper);
    handler.setElasticSocialPlugin(elasticSocialPlugin);
    handler.setContributionTargetHelper(contributionTargetHelper);
    handler.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);

    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;productId=1234";
    FragmentContext fragmentContext = new FragmentContext();
    FragmentParameters fragmentParameters = FragmentParametersFactory.create(url);
    fragmentContext.setParameters(fragmentParameters);
    fragmentContext.setFragmentRequest(true);
    setAttribute("CM_FRAGMENT_CONTEXT", fragmentContext);

    setAttribute("guid", "1234+5");
    lenient().when(communityUserService.getUserById("1234")).thenReturn(user);

    lenient().when(contentRepository.getContent(IdHelper.formatContentId(contextId))).thenReturn(navigationContent);
    lenient().when(contentBeanFactory.createBeanFor(navigationContent, ContentBean.class)).thenReturn(cmNavigation);
    lenient().when(cmNavigation.getContext()).thenReturn(context);

    lenient().when(elasticSocialPlugin.getElasticSocialConfiguration(any(Object[].class))).thenReturn(elasticSocialConfiguration);
    lenient().when(elasticSocialConfiguration.isFeedbackEnabled()).thenReturn(true);
    lenient().when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    lenient().when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(true);
    lenient().when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(true);

    lenient().when(resourceBundleFactory.resourceBundle(any(Navigation.class), nullable(User.class))).thenReturn(resourceBundle);

    lenient().when(catalogService.findProductById(any(), any(StoreContext.class))).thenReturn(product);

    setAttribute(SiteHelper.SITE_KEY, site);

    BaseCommerceIdProvider idProvider = TestVendors.getIdProvider("vendor");

    CommerceConnection commerceConnection = mock(CommerceConnection.class);
    lenient().when(commerceConnection.getIdProvider()).thenReturn(idProvider);
    lenient().when(commerceConnection.getCatalogService()).thenReturn(catalogService);

    lenient().when(storeContext.getConnection()).thenReturn(commerceConnection);

    CurrentStoreContext.set(storeContext, request);
  }

  public void setAttribute(String key, Object value) {
    request.setAttribute(key, value);
  }

  @AfterEach
  public void cleanUp() {
    UserContext.clear();
    CurrentStoreContext.remove();
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void getReviews() {
    String view = "default";
    List<Review> reviews = new ArrayList<>();
    reviews.add(review);
    when(elasticSocialService.getReviews(product, user)).thenReturn(reviews);
    ModelAndView result = handler.getReviews(contextId, targetId, view, request);

    assertThat(result.getViewName()).isEqualTo(view);
    assertThat(result.getModelMap().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION)).isEqualTo(cmNavigation);

    ProductReviewsResult productReviewsResult = getModel(result, ProductReviewsResult.class);
    Product target = (Product) productReviewsResult.getTarget();
    assertThat(target).isEqualTo(product);

    List<Review> reviewsResult = productReviewsResult.getReviews();

    assertThat(reviewsResult).isEqualTo(reviews);
    // check if configuration is what configured in setup
    assertThat(productReviewsResult.isEnabled()).isTrue();
    assertThat(productReviewsResult.isWritingContributionsEnabled()).isTrue();
    assertThat(productReviewsResult.isAnonymousContributingEnabled()).isTrue();
    verify(elasticSocialService).getReviews(any(Product.class), any(CommunityUser.class));

    // make sure reviews are loaded lazily when getReviews is called
    productReviewsResult.getReviews();
    verify(elasticSocialService).getReviews(target, user);
  }

  @Test
  void createReview() {
    when(elasticSocialService.createReview(eq(user), any(Product.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), nullable(List.class), any(Navigation.class))).thenReturn(review);
    when(elasticSocialConfiguration.getReviewModerationType()).thenReturn(ModerationType.POST_MODERATION);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertThat(resultModel.getErrors()).isEmpty();
    assertThat(resultModel.getMessages()).hasSize(1);
    assertThat(resultModel.isSuccess()).isTrue();
    verify(elasticSocialService).createReview(eq(user), any(Product.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), nullable(List.class), any(Navigation.class));
    verify(resourceBundle).getString(ContributionMessageKeys.REVIEW_FORM_SUCCESS);
  }

  @Test
  void createReviewRatingNull() {
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, null, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertThat(resultModel.getErrors()).isEmpty();
    assertThat(resultModel.getMessages()).hasSize(1);
    assertThat(resultModel.isSuccess()).isFalse();
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyList(), any(Navigation.class));
    verify(resourceBundle, never()).getString(ContributionMessageKeys.REVIEW_FORM_SUCCESS);
  }

  @Test
  void createReviewDisabled() {
    when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(false);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertThat(resultModel.getErrors()).isEmpty();
    assertThat(resultModel.getMessages()).hasSize(1);
    assertThat(resultModel.isSuccess()).isFalse();
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyList(), any(Navigation.class));
    verify(resourceBundle, never()).getString(ContributionMessageKeys.REVIEW_FORM_SUCCESS);
  }

  @Test
  void createReviewAnonymousDisabled() {
    when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(false);
    when(user.isAnonymous()).thenReturn(true);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertThat(resultModel.getErrors()).isEmpty();
    assertThat(resultModel.getMessages()).hasSize(1);
    assertThat(resultModel.isSuccess()).isFalse();
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyList(), any(Navigation.class));
    verify(resourceBundle, never()).getString(ContributionMessageKeys.REVIEW_FORM_SUCCESS);
  }

  @Test
  void getReviewsPlaceholder() {
    ProductReviewsResult reviewsResult = handler.getReviews(page, request);

    assertThat(reviewsResult).isNotNull();
    assertThat(reviewsResult.getTarget()).isEqualTo(product);
  }

  @Test
  void buildLink() throws URISyntaxException {
    Map<String, Object> linkParameters = new HashMap<>();
    List<String> pathList = new ArrayList<>();
    String path = "path/" + contextId;
    pathList.add(path);
    when(contextHelper.currentSiteContext()).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContentId()).thenReturn(Integer.parseInt(contextId));
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
    URI uri = new URI(path);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);

    when(product.getReference()).thenReturn(parseCommerceIdOrThrow(targetId));
    ProductReviewsResult productReviewsResult = new ProductReviewsResult(product);
    UriComponents uriComponents = handler.buildLink(productReviewsResult, uriTemplate, linkParameters, request);

    assertThat(uriComponents).isNotNull();
    assertThat(uriComponents.getPath()).isEqualTo(path);
  }

  private <T> T getModel(ModelAndView modelAndView, Class<T> type) {
    return getModel(modelAndView, "self", type);
  }

  private <T> T getModel(ModelAndView modelAndView, String key, Class<T> type) {
    Map<String, Object> modelMap = modelAndView.getModel();
    Object model = modelMap.get(key);
    // assertTrue(model instanceof type);
    return (T) model; // NO_SONAR
  }
}
