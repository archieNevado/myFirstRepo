package com.coremedia.blueprint.cae.exception.handler;

import com.coremedia.objectserver.web.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Simple exception handler implementation for plain spring configuration.
 */
public class SimpleExceptionHandler<T extends Exception> extends AbstractErrorAndExceptionHandler<T, HttpError> {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private int statusCode;
  private Class<T> exceptionType;

  @Override
  public HttpError resolveSelf(T exception) {
    return new HttpError(getStatusCode(), exception.getMessage());
  }

  @Override
  public T resolveException(Exception exception) {
    if (exceptionType.isInstance(exception)) {
      return exceptionType.cast(exception);
    } else {
      return null;
    }
  }

  @Override
  public void handleExceptionInternal(T exception, ModelAndView modelAndView, String viewName, HttpServletRequest request) {
    LOG.warn("Caught Exception '{}' for {} with view {} (stacktrace on DEBUG level).", exception.getMessage(), modelAndView, viewName);
    LOG.debug("Caught Exception for {} with view {}.", modelAndView, viewName, exception);
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * The status code to send if this resolver handles the exception
   */
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * The exception type to handle
   */
  public void setExceptionType(Class<T> exceptionType) {
    this.exceptionType = exceptionType;
  }

  @PostConstruct
  protected void initialize() {
    if (exceptionType == null) {
      throw new IllegalStateException("Required property not set: exceptionType");
    }
    if (statusCode == 0) {
      throw new IllegalStateException("Required property not set: statusCode");
    }
  }

}
