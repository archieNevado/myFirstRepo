package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class UserContextHelperTest {

  @Before
  public void setup() {
   CurrentCommerceConnection.set(new BaseCommerceConnection());
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testNullContext() {
    assertNull(UserContextHelper.getForUserName(null));
  }

  @Test
  public void testInvalidValues() {
    UserContext userContext = Mockito.mock(UserContext.class);
    when(userContext.getUserName()).thenReturn(null);

    assertNull(UserContextHelper.getForUserName(userContext));
  }

  @Test
  public void testCreateContext() {
    UserContext userContext = UserContext.builder().withUserName("forUser").build();
    assertEquals("forUser", UserContextHelper.getForUserName(userContext));
  }

  @Test
  public void testCurrentContext() {
    UserContext userContext = UserContext.builder().withUserName("forUser").build();
    UserContextHelper.setCurrentContext(userContext);
    assertEquals(userContext, UserContextHelper.getCurrentContext());

    UserContext userContext2 = UserContext.builder().withUserName("forUser2").build();
    UserContextHelper.setCurrentContext(userContext2);
    assertEquals(userContext2, UserContextHelper.getCurrentContext());
  }
}
