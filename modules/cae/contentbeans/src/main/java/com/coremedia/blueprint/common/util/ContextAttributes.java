package com.coremedia.blueprint.common.util;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Attributes abstraction from the servlet api.
 * <p>
 * If the methods are invoked from outside a request scope, they return null.
 * <p>
 * <b>WARNING</b>
 * <p>
 * Calculations which (transitively) invoke this class MUST NOT be cached!
 * Usage of the current request makes your feature unpredictable, unstable
 * and hard to test
 */
public class ContextAttributes {
  // static utility class
  private ContextAttributes() {}

  /**
   * Get the request bound to the current thread.
   */
  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
    return null;
  }

  /**
   * Get a request attribute of the request bound to the current thread.
   */
  public static <T> T getRequestAttribute(String name, Class<T> expectedType) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(expectedType);
    HttpServletRequest request = getRequest();
    return typed(request==null ? null : request.getAttribute(name), expectedType);
  }

  /**
   * Get a session attribute of the request bound to the current thread.
   */
  public static <T> T getSessionAttribute(String name, Class<T> expectedType) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(expectedType);
    HttpServletRequest request = getRequest();
    HttpSession session = request==null ? null : request.getSession(false);
    return typed(session==null ? null : session.getAttribute(name), expectedType);
  }

  /**
   * Get a request parameter of the request bound to the current thread.
   */
  public static String getRequestParameter(String name) {
    Objects.requireNonNull(name);
    HttpServletRequest request = getRequest();
    return request==null ? null : request.getParameter(name);
  }


  // --- internal ---------------------------------------------------

  @VisibleForTesting
  static <T> T typed(Object value, Class<T> expectedType) {
    if (value==null) {
      return null;
    }
    Class<?> actualType = value.getClass();
    if (expectedType.isAssignableFrom(actualType)) {
      return expectedType.cast(value);
    }
    return null;
  }
}
