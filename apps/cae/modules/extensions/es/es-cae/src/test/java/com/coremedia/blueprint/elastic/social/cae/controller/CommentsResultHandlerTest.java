package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.i18n.PageResourceBundleFactory;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteHelper;
import com.coremedia.cap.user.User;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HttpError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.coremedia.blueprint.elastic.social.cae.controller.CommentsResultHandler.ERROR_MESSAGE;
import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentsResultHandlerTest {
  @InjectMocks
  private CommentsResultHandler handler;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private NavigationSegmentsUriHelper uriHelper;


  @Mock
  private PageResourceBundleFactory resourceBundleFactory;

  @Mock
  private ResourceBundle resourceBundle;

  @Mock
  private CommunityUser user;

  @Mock
  private Content content;

  @Mock
  private Content navigationContent;

  @Mock
  private ContentWithSite contentWithSite;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;

  @Mock
  private CMLinkable contentBean;

  @Mock
  private CMNavigation navigation;

  @Mock
  private CMContext navigationContext;

  @Mock
  private ElasticSocialUserHelper elasticSocialUserHelper;

  @Mock
  private HttpServletRequest request;

  @Mock
  private UriTemplate uriTemplate;

  @Mock
  private Site site;

  private final String contextId = "1234";
  private String targetId = "12";
  private final String view = "view";
  private final String text = "default comment"; // we could randomize this one :-)
  private String authorName;
  private String replyTo;
  private final String permittedParamName = "test";
  private final String uriPath = "helios";

  @BeforeEach
  public void setup() throws URISyntaxException {
    handler.setPermittedLinkParameterNames(Collections.singletonList(permittedParamName));

    lenient().when(contentRepository.getContent(IdHelper.formatContentId(targetId))).thenReturn(content);
    lenient().when(contentBeanFactory.createBeanFor(content, ContentBean.class)).thenReturn(contentBean);
    lenient().when(contentRepository.getContent(IdHelper.formatContentId(contextId))).thenReturn(navigationContent);
    lenient().when(contentBeanFactory.createBeanFor(navigationContent, ContentBean.class)).thenReturn(navigation);

    lenient().when(contentBean.getContent()).thenReturn(content);
    lenient().when(content.getId()).thenReturn(targetId);

    lenient().when(contentWithSite.getContent()).thenReturn(content);
    lenient().when(contributionTargetHelper.getContentFromTarget(any())).thenReturn(content);

    URI uri = new URI(uriPath);
    lenient().when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);
    lenient().when(uriHelper.getPathList(navigation)).thenReturn(Collections.singletonList(uriPath));
    lenient().when(contextHelper.contextFor(any(CMLinkable.class))).thenReturn(navigation);
    lenient().when(navigation.getContext()).thenReturn(navigationContext);
    lenient().when(navigationContext.getContentId()).thenReturn(Integer.parseInt(contextId));

    lenient().when(resourceBundleFactory.resourceBundle(any(Navigation.class), nullable(User.class))).thenReturn(resourceBundle);

    lenient().when(elasticSocialPlugin.getElasticSocialConfiguration(any(Object[].class))).thenReturn(elasticSocialConfiguration);

    lenient().when(elasticSocialConfiguration.isFeedbackEnabled()).thenReturn(true);
    lenient().when(elasticSocialConfiguration.getCommentType()).thenReturn(ANONYMOUS);
    lenient().when(elasticSocialConfiguration.isWritingCommentsEnabled()).thenReturn(true);
    lenient().when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(true);

    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);
  }

  @Test
  void getCommentsWithNoTarget() {
    targetId = " ";
    ModelAndView modelAndView = handler.getComments(contextId, targetId, view, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertThat(httpError.getErrorCode()).isEqualTo(404);
  }

  @Test
  void getCommentsWithUnknownTarget() {
    targetId = "123";
    ModelAndView modelAndView = handler.getComments(contextId, targetId, view, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertThat(httpError.getErrorCode()).isEqualTo(404);
  }

  @Test
  void getComments() {
    ModelAndView result = handler.getComments(contextId, targetId, view, request);
    CommentsResult commentsResultResult = getModel(result, CommentsResult.class);

    assertThat(commentsResultResult).isNotNull();
    ContentWithSite target = (ContentWithSite) commentsResultResult.getTarget();
    assertThat(target.getContent()).isEqualTo(content);

    assertThat(result.getViewName()).isEqualTo(view);
    assertThat(result.getModelMap().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION)).isEqualTo(navigation);
    assertThat(commentsResultResult.getContributionType()).isEqualTo(ANONYMOUS);
  }

  @Test
  void createComment() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.POST_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertThat(resultModel.getErrors()).isEmpty();
    assertThat(resultModel.getMessages()).hasSize(1);

    verify(elasticSocialService).createComment(eq(user), isNull(), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(), anyList());  // NO_SONAR suppress warning
    verify(resourceBundle).getString(ContributionMessageKeys.COMMENT_FORM_SUCCESS);
  }

  @Test
  void createCommentForAnonymousNotAllowed() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(null);
    when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(false);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertThat(resultModel.isSuccess()).isFalse();
    assertThat(resultModel.getMessages()).hasSize(1);

    verify(elasticSocialService, never()).createComment(any(CommunityUser.class), isNull(), eq(content), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(), isNull());  // NO_SONAR suppress warning
    verify(resourceBundle).getString(ContributionMessageKeys.COMMENT_FORM_NOT_LOGGED_IN);
  }

  @Test
  void createCommentForUnknownContent() {
    String unknownContentId = "12345";
    ModelAndView modelAndView = handler.createComment(contextId, unknownContentId, text, authorName, replyTo, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertThat(httpError.getErrorCode()).isEqualTo(404);
  }

  @Test
  void createCommentWithPreModeration() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.PRE_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertThat(resultModel.getMessages()).hasSize(1);
    assertThat(resultModel.isSuccess()).isTrue();
    verify(elasticSocialService).createComment(eq(user), isNull(), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.PRE_MODERATION), isNull(), anyList());  // NO_SONAR suppress warning
    verify(resourceBundle).getString(ContributionMessageKeys.COMMENT_FORM_SUCCESS_PREMODERATION);
  }

  @Test
  void createCommentWithException() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialService.createComment(eq(user), isNull(), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(), anyList())).thenThrow(new RuntimeException("intended"));
    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.POST_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    List<HandlerInfo.Message> messages = resultModel.getMessages();
    assertThat(messages).hasSize(1);
    assertThat(resultModel.isSuccess()).isFalse();
    assertThat(messages.get(0).getType()).isEqualTo(ERROR_MESSAGE);

    verify(elasticSocialService).createComment(eq(user), isNull(), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(), anyList());  // NO_SONAR suppress warning
    verify(resourceBundle).getString(ContributionMessageKeys.COMMENT_FORM_ERROR);
  }

  @Test
  void buildCommentInfoLink() {
    String notPermittedParamName = "not permitted";
    String paramValue = "value";
    Map<String, Object> linkParameters = Map.of(permittedParamName, paramValue, notPermittedParamName, paramValue);
    CommentsResult commentsResult = new CommentsResult(contentWithSite);
    UriComponents result = handler.buildCommentInfoLink(commentsResult, uriTemplate, linkParameters, request);

    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(uriPath);
    MultiValueMap<String, String> queryParams = result.getQueryParams();
    assertThat(queryParams).isEmpty();

    verify(uriTemplate).expand(uriPath, Integer.parseInt(contextId), Integer.parseInt(targetId));
  }

  @Test
  void buildFragmentLink() {
    String notPermittedParamName = "not permitted";
    String paramValue = "value";
    Map<String, Object> linkParameters = Map.of(permittedParamName, paramValue, notPermittedParamName, paramValue);
    CommentsResult commentsResult = new CommentsResult(contentWithSite);
    UriComponents result = handler.buildFragmentLink(commentsResult, uriTemplate, linkParameters, request);

    assertThat(result).isNotNull();
    assertThat(result.getPath()).isEqualTo(uriPath);
    MultiValueMap<String, String> queryParams = result.getQueryParams();
    assertThat(queryParams).hasSize(1);
    List<String> queryParamValues = queryParams.get(permittedParamName);
    assertThat(queryParamValues).hasSize(1);
    assertThat(queryParamValues.get(0)).isEqualTo(paramValue);
    verify(uriTemplate).expand(uriPath, Integer.parseInt(contextId), Integer.parseInt(targetId));
  }

  @Test
  void anonymousCommentingNotEnabled() {
    when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(false);

    ModelAndView mv = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo result = getModel(mv, HandlerInfo.class);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getMessages()).isNotNull();
    assertThat(result.getMessages()).hasSize(1);
  }

  @Test
  void commentTextBlank() {
    ModelAndView mv = handler.createComment(contextId, targetId, "", authorName, replyTo, request);
    HandlerInfo result = getModel(mv, HandlerInfo.class);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getMessages()).isNotNull();
    assertThat(result.getMessages()).hasSize(1);
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
