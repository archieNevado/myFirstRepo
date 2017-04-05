package com.coremedia.ecommerce.studio.rest.exception;

import com.coremedia.ecommerce.studio.rest.CatalogRestErrorCodes;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.rest.cap.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.GONE;

/**
 * Maps {@link com.coremedia.livecontext.ecommerce.common.CommerceException} to REST responses.
 */
@Provider
public class CommerceExceptionMapper implements ExceptionMapper<CommerceException> {

  // Attention: use only status codes that causes no other error handling on the client side
  // the 4xx status codes have proven to be working, so we use consistent 410 (GONE).
  // only the error codes (LC-xxxx) will be evaluated in the corresponding catalog error handler
  private final Map<Class, ResultCodes> EXCEPTION_MAP =
          Collections.unmodifiableMap(new HashMap<Class, ResultCodes>() {{
            // alphabetic order of exception classes
            put(CommerceRemoteException.class, new ResultCodes(CatalogRestErrorCodes.CATALOG_INTERNAL_ERROR, GONE));
            put(UnauthorizedException.class, new ResultCodes(CatalogRestErrorCodes.UNAUTHORIZED, GONE));
          }});

  @Context
  private HttpServletRequest request;

  @Override
  public Response toResponse(CommerceException ex) {

    String name = ex.getClass().getSimpleName() + "(" + ex.getResultCode() + ")";
    String msg = ex.getMessage();
    ResultCodes resultCodes = EXCEPTION_MAP.get(ex.getClass());

    if (resultCodes == null) {
      // for all other commerce exceptions we set...
      resultCodes = new ResultCodes(CatalogRestErrorCodes.CATALOG_UNAVAILABLE, GONE);
    }

    return ResponseUtil.buildResponse(request, resultCodes.statusCode, resultCodes.errorCode, name, msg);
  }

  private class ResultCodes {
    private String errorCode;
    private Response.Status statusCode;

    public ResultCodes(String errorCode, Response.Status statusCode) {
      this.errorCode = errorCode;
      this.statusCode = statusCode;
    }
  }

}
