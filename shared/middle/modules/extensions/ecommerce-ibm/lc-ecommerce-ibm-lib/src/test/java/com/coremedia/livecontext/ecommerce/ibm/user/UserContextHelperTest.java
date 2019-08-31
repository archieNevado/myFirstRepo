package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserContextHelperTest {

  @BeforeEach
  void setup() {
    CurrentCommerceConnection.set(new BaseCommerceConnection());
  }

  @AfterEach
  void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  void testNullContext() {
    assertNull(UserContextHelper.getForUserName(null));
  }

  @Test
  void testInvalidValues() {
    UserContext userContext = mock(UserContext.class);
    when(userContext.getUserName()).thenReturn(null);

    assertNull(UserContextHelper.getForUserName(userContext));
  }

  @Test
  void testCreateContext() {
    UserContext userContext = UserContext.builder().withUserName("forUser").build();
    assertEquals("forUser", UserContextHelper.getForUserName(userContext));
  }

  @Test
  void testCurrentContext() {
    UserContext userContext = UserContext.builder().withUserName("forUser").build();
    UserContextHelper.setCurrentContext(userContext);
    assertEquals(userContext, UserContextHelper.getCurrentContext());

    UserContext userContext2 = UserContext.builder().withUserName("forUser2").build();
    UserContextHelper.setCurrentContext(userContext2);
    assertEquals(userContext2, UserContextHelper.getCurrentContext());
  }
}
