package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.services.SessionSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

public class SessionSynchronizationInterceptor extends HandlerInterceptorAdapter {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse
          response, Object handler) throws GeneralSecurityException {
    if (RequestMethod.OPTIONS.toString().equals(request.getMethod())) {
      return true;
    }

    CommerceConnection currentConnection = DefaultConnection.get();
    if (currentConnection == null || currentConnection.getUserSessionService() == null) {
      return true;
    }

    try {
      sessionSynchronizer.synchronizeUserSession(request, response);
    } catch (GeneralSecurityException e) {
      LOG.error("Could not synchronize the user sessions between CAE and commerce.", e);
    }

    return true;
  }

  @Required
  public void setSessionSynchronizer(SessionSynchronizer sessionSynchronizer) {
    this.sessionSynchronizer = sessionSynchronizer;
  }

  private SessionSynchronizer sessionSynchronizer;

  private static final Logger LOG = LoggerFactory.getLogger(SessionSynchronizationInterceptor.class);
}
