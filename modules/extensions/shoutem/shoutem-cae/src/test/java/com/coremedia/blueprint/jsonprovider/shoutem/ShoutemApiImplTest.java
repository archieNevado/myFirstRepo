package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.cae.contentbeans.CMArticleImpl;
import com.coremedia.blueprint.cae.contentbeans.CMChannelImpl;
import com.coremedia.blueprint.cae.contentbeans.CMPictureImpl;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PageableRepresentation;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Post;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PostComment;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.ServiceInfo;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.elastic.core.api.SortOrder;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ShoutemApiImplTest.LocalConfig.class)
@ActiveProfiles(value = {ShoutemApiImplTest.LocalConfig.PROFILE, ShoutemCaeTestConfiguration.PROFILE})
public class ShoutemApiImplTest {

  @Mock
  private CommunityUser anonymousCommunityUser;

  @Mock
  private CommunityUser user;

  @Mock
  private Authentication auth;

  @Mock
  private SearchResultBean srb;

  @Mock
  private Comment comment;

  private ShoutemApiCredentials credentials;

  @Inject
  private CommunityUserService userService;
  @Inject
  private CommentService commentService;
  @Inject
  private LikeService likeService;
  @Inject
  private AuthenticationManager authenticationManager;
  @Inject
  private ContentBeanFactory contentBeanFactory;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ShoutemApiImpl shoutemApi;
  @Inject
  private SearchResultFactory resultFactory;

  @Before
  public void setUp() {
    // Cannot use MockitoJUnitRunner because only one runner can be used at a time.
    MockitoAnnotations.initMocks(this);

    when(userService.getUserByName(anyString())).thenReturn(anonymousCommunityUser);
    when(userService.createAnonymousUser()).thenReturn(user);

    credentials = new ShoutemApiCredentials();
    credentials.setAuthenticationManager(authenticationManager);
    credentials.setSite("channela");
    credentials.setUserService(userService);

    SearchResultBean result = new SearchResultBean();
    result.setHits(Collections.singletonList(contentBeanFactory.createBeanFor(contentRepository.getContent(Integer.toString(4)))));
    when(resultFactory.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(result);
    when(likeService.getLikesForTarget(any(), anyInt())).thenReturn(Collections.EMPTY_LIST);

    when(commentService.getComments(any(), any(CommunityUser.class), any(SortOrder.class), anyInt())).thenReturn(Collections.EMPTY_LIST);

    when(auth.getPrincipal()).thenReturn("user-1");
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
    credentials.authenticate("login", "pass");

    when(resultFactory.createSearchResultUncached(Mockito.any(SearchQueryBean.class))).thenReturn(srb);
    List results = Collections.emptyList();
    when(srb.getHits()).thenReturn(results);
  }

  @Test
  public void testGetServiceInfo() {
    assertEquals(1, shoutemApi.getServiceInfo().getApi_version());
    assertEquals("coremedia", shoutemApi.getServiceInfo().getServer_type());
  }

  @Test
  public void testPostsGet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    Post post = shoutemApi.getPostsGet(credentials, request, response, "4");
    assertNotNull(post);
  }

  @Test
  public void testPostComments() {
    PageableRepresentation result = shoutemApi.getPostsComments(credentials, "4", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testPostLikes() {
    PageableRepresentation result = shoutemApi.getPostsLikes(credentials, "4", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testPostFind() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    PageableRepresentation result = shoutemApi.getPostsFind(credentials, request, response, null, "title", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testServiceInfo() {
    ServiceInfo result = shoutemApi.getServiceInfo();
    assertNotNull(result);
  }

  @Test
  public void testComments() {
    String commentId = "4";
    when(commentService.createComment(any(CommunityUser.class), anyString(), anyObject(), anyCollectionOf(String.class), any(Comment.class))).thenReturn(comment);
    when(commentService.getComment(commentId)).thenReturn(comment);
    when(comment.getId()).thenReturn(commentId);
    PostComment comment = shoutemApi.postPostsCommentsNew(credentials, commentId, null, "4711", "nick", "mail", "url", "hello world", "subject");
    Assert.assertNotNull(comment);
    comment = shoutemApi.postPostsCommentsUpdate(credentials, comment.getComment_id(), "hello updated world");
    Assert.assertNotNull(comment);
    Assert.assertTrue(shoutemApi.postPostsCommentsDelete(credentials, comment.getComment_id()));
  }

  @Configuration
  @Import(value = {
          XmlRepoConfiguration.class,
          ShoutemCaeTestConfiguration.class
  })
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "ShoutemApiImplTest";

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }

    @Bean(name = "contentBeanFactory:CMArticle")
    @Scope("prototype")
    CMArticle article() {
      CMArticleImpl cmArticle = new CMArticleImpl();
      cmArticle.setContextStrategy(mock(ContextStrategy.class));
      cmArticle.setValidationService(mock(ValidationService.class));
      return cmArticle;
    }

    @Bean(name = "contentBeanFactory:CMPicture")
    @Scope("prototype")
    CMPicture picture() {
      return new CMPictureImpl();
    }

    @Bean(name = "contentBeanFactory:CMChannel")
    @Scope("prototype")
    CMChannel channel() {
      return new CMChannelImpl();
    }
  }

}
