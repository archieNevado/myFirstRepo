package com.coremedia.livecontext.ecommerce.ibm.common;

import org.apache.http.cookie.Cookie;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreFrontServiceConvertCookiesTest {

  private StoreFrontService testling;

  @Before
  public void beforeEachTest() {
    testling = new StoreFrontService(){};
  }

  @Test
  public void convertCookies() {
    Date date = new Date();
    String name = "name";
    String value = "value";
    String comment = "comment";
    String path = "/";
    String domain = ".subdomain.tld";
    int version = 1;
    boolean secure = true;

    Cookie mock = mockApacheCookie(name, value, comment, date, path, domain, secure, version);
    CookieMatcher cookieMatcher = new CookieMatcher(name, value, comment, path, domain, secure, version);

    javax.servlet.http.Cookie cookie = testling.convertCookie(mock);

    assertThat(cookie, cookieMatcher);
  }

  private Cookie mockApacheCookie(String name, String value, String comment, Date date, String path, String domain, boolean secure, int version) {
    Cookie mock = mock(Cookie.class);
    when(mock.getName()).thenReturn(name);
    when(mock.getValue()).thenReturn(value);
    when(mock.getComment()).thenReturn(comment);
    when(mock.getExpiryDate()).thenReturn(date);
    when(mock.getPath()).thenReturn(path);
    when(mock.getDomain()).thenReturn(domain);
    when(mock.getVersion()).thenReturn(version);
    when(mock.isSecure()).thenReturn(secure);
    return mock;
  }
  private static class CookieMatcher extends BaseMatcher<javax.servlet.http.Cookie> {

    private String name;
    private String value;
    private String comment;
    private String path;
    private String domain;
    private int version;
    private boolean secure;

    public CookieMatcher(String name, String value, String comment, String path, String domain, boolean secure, int version) {
      this.name = name;
      this.value = value;
      this.comment = comment;
      this.path = path;
      this.domain = domain;
      this.secure = secure;
      this.version = version;
    }

    @Override
    public boolean matches(Object object) {
      if (!(object instanceof javax.servlet.http.Cookie)) {
        return false;
      }
      javax.servlet.http.Cookie actualCookie = (javax.servlet.http.Cookie) object;
      if(!isSameValue(actualCookie.getName(), name)) {
        return false;
      }
      if(!isSameValue(actualCookie.getValue(), value)) {
        return false;
      }
      if(!isSameValue(actualCookie.getComment(), comment)) {
        return false;
      }
      if(!isSameValue(actualCookie.getPath(), path)) {
        return false;
      }
      if(!isSameValue(actualCookie.getDomain(), domain)) {
        return false;
      }
      if(!(actualCookie.getVersion() == version)) {
        return false;
      }
      if(!(actualCookie.getSecure() == secure)) {
        return false;
      }
      return true;
    }

    private boolean isSameValue(String name, String name1) {
      if(name != null && name1 != null) {
        return name.equals(name1);
      }

      return name == null && name1 == null;
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("cookie does not match expected name (\""+name+"\") or value (\""+value+"\")");
    }
  }
}
