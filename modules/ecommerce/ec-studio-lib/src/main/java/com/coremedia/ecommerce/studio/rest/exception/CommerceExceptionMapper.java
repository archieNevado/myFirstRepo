package com.coremedia.ecommerce.studio.rest.exception;

import com.coremedia.ecommerce.studio.rest.CatalogRestErrorCodes;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteError;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.rest.cap.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Maps {@link com.coremedia.livecontext.ecommerce.common.CommerceException} to REST responses.
 */
@Provider
public class CommerceExceptionMapper implements ExceptionMapper<CommerceException> {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceExceptionMapper.class);

  @Context
  private HttpServletRequest request;

  @Override
  public Response toResponse(CommerceException ex) {

    Response.Status status;
    String errorCode;
    String name;
    String msg;

    if (ex instanceof CommerceRemoteException) {
      status = INTERNAL_SERVER_ERROR;
      CommerceRemoteError remoteError = ((CommerceRemoteException) ex).getRemoteError();
      errorCode = CatalogRestErrorCodes.CATALOG_INTERNAL_ERROR;
      name = remoteError.getErrorKey();
      msg = remoteError.getErrorMessage();
    } else {
      status = NOT_FOUND;
      errorCode = CatalogRestErrorCodes.CATALOG_UNAVAILABLE;
      name = ex.getClass().getSimpleName();
      msg = ex.getMessage();
    }

    if (status.getFamily() == Response.Status.Family.SERVER_ERROR) {
      LOG.warn("A server error occurred: {}", msg, ex);
    } else {
      LOG.debug("exception while processing request: {}", msg, ex);
    }

    return ResponseUtil.buildResponse(request, status, errorCode, name, msg);
  }

}
