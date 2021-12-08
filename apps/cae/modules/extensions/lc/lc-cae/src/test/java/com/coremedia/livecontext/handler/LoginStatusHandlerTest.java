package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = LoginStatusHandlerTest.LocalConfig.class)
public class LoginStatusHandlerTest {

  private static final String STORE_ID = "4711";
  private static final Locale LOCALE = Locale.US;

  @Inject
  private WebApplicationContext wac;

  @Inject
  private LiveContextSiteResolver liveContextSiteResolver;

  @Inject
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Inject
  private LinkFormatter linkFormatter;

  private MockMvc mockMvc;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private StoreContext storeContext;

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    when(commerceConnection.getInitialStoreContext()).thenReturn(storeContext);
    when(storeContext.getConnection()).thenReturn(commerceConnection);
  }

  @Test
  public void testStatusSiteNotFound() throws Exception {
    loginStatus().andExpect(status().isNotFound());
  }

  @Test
  public void testStatusConnectionNotFound() throws Exception {
    mockSite();
    loginStatus().andExpect(status().isNotFound());
  }

  @Test
  public void testStatusNotLoggedIn() throws Exception {
    Site site = mockSite();
    mockConnection(site);
    mockUserContext(false);

    loginStatus()
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("{\"loggedIn\":false}"));
  }

  @Test
  public void testStatusLoggedIn() throws Exception {
    Site site = mockSite();
    mockConnection(site);
    mockUserContext(true);
    loginStatus()
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("{\"loggedIn\":true}"));
  }

  @Test
  public void testLinkStatus() {
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();

    // The StoreContext is extracted internally from the request
    request.setAttribute(StoreContext.class.getName(), storeContext);

    when(storeContext.getStoreId()).thenReturn(STORE_ID);
    when(storeContext.getLocale()).thenReturn(LOCALE);


    String expected = "/dynamic/loginstatus?storeId=" + STORE_ID + "&locale=" + LOCALE.toLanguageTag();
    String actual = linkFormatter.formatLink(LoginStatusHandler.LinkType.STATUS, null, request, response, false);
    assertEquals(expected, actual);

  }

  private Site mockSite() {
    Site site = mock(Site.class);
    when(liveContextSiteResolver.findSiteFor(STORE_ID, Locale.forLanguageTag(LOCALE.toLanguageTag()))).thenReturn(Optional.of(site));
    return site;
  }

  private void mockConnection(Site site) {
    UserContextProvider userContextProvider = mock(UserContextProvider.class);
    when(userContextProvider.createContext(any(HttpServletRequest.class))).thenReturn(UserContext.builder().build());
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.of(commerceConnection));
  }

  private void mockUserContext(boolean isLoggedIn) {
    UserContextProvider userContextProvider = mock(UserContextProvider.class);
    UserContext userContext = isLoggedIn ? UserContext.builder().withLoggedIn(true).build() : UserContext.builder().build();
    when(userContextProvider.createContext(any(HttpServletRequest.class))).thenReturn(userContext);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
  }

  private ResultActions loginStatus() throws Exception {
    return mockMvc.perform(get("/dynamic/loginstatus")
            .accept("application/json")
            .param("storeId", STORE_ID)
            .param("locale", LOCALE.toLanguageTag())
    );
  }

  @Configuration(proxyBeanMethods = false)
  @EnableWebMvc
  @ImportResource(
          locations = {"classpath:/com/coremedia/cae/link-services.xml"},
          reader = ResourceAwareXmlBeanDefinitionReader.class)
  static class LocalConfig {

    @Bean
    LoginStatusHandler loginStatusHandler(LiveContextSiteResolver liveContextSiteResolver,
                                          CommerceConnectionSupplier commerceConnectionSupplier) {
      return new LoginStatusHandler(liveContextSiteResolver, commerceConnectionSupplier);
    }

    @Bean
    LiveContextSiteResolver liveContextSiteResolver() {
      return mock(LiveContextSiteResolver.class);
    }

    @Bean
    CommerceConnectionSupplier commerceConnectionSupplier() {
      return mock(CommerceConnectionSupplier.class);
    }
  }
}
