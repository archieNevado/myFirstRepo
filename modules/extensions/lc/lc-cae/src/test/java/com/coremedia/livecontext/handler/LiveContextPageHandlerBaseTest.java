package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LiveContextPageHandlerBaseTest {

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private SecurityContext securityContext;

  @Spy
  private LiveContextPageHandlerBase testling;

  @Mock
  private Product product;

  @Mock
  private Catalog catalog;

  @Before
  public void beforeEachTest() {
    doReturn(securityContext).when(testling).getSecurityContext();
  }

  @Test
  public void testInitUserContextNoKnownUser() throws Exception {
    configureSpringSecurity("anonymous");

    verify(userContextProvider, times(0)).setCurrentContext(any(UserContext.class));
  }

  @Test
  public void testInitUserContextNoSession() throws Exception {
    verify(userContextProvider, times(0)).setCurrentContext(any(UserContext.class));
  }

  @Test
  public void updateQueryParams() {
    when(product.getExternalTechId()).thenReturn("42");
    when(product.getCatalog()).thenReturn(Optional.of(catalog));
    when(catalog.isDefaultCatalog()).thenReturn(false);
    when(catalog.getExternalId()).thenReturn("4711");
    Map<String, Object> queryParams = testling.updateQueryParams(product, Collections.emptyMap());
    assertThat(queryParams).containsKey("catalogId");
  }

  private void configureSpringSecurity(Object user) {
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
  }
}
