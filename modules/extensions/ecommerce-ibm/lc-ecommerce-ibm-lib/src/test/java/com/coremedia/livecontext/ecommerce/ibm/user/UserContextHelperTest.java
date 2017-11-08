package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class UserContextHelperTest extends IbmServiceTestBase {

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
