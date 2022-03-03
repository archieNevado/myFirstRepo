package com.coremedia.blueprint.cae.exception.handler;

import com.coremedia.blueprint.cae.exception.ContentError;
import com.coremedia.cache.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.invoke.MethodHandles.lookup;

public class EvaluationExceptionHandler extends AbstractErrorAndExceptionHandler<EvaluationException, ContentError> {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Override
  public void handleExceptionInternal(EvaluationException exception, ModelAndView modelAndView, String viewName, HttpServletRequest request) {
    LOG.warn("Handled exception: {} (stacktrace on DEBUG level).", exception.getMessage());
    LOG.debug("Handled exception.", exception);
  }

  @Override
  public int getStatusCode() {
    return HttpServletResponse.SC_FORBIDDEN;
  }

  @Override
  public ContentError resolveSelf(EvaluationException exception) {
    return new ContentError(exception.getMessage(), exception);
  }

  @Override
  public EvaluationException resolveException(Exception exception) {
    return exception instanceof EvaluationException ? (EvaluationException) exception : null;
  }
}
