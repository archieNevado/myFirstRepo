package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.InvalidLoginException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.HttpMethod;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.hasText;

public class WcLoginWrapperService extends AbstractWcWrapperService {

  public static final String ERROR_KEY_AUTHENTICATION_ERROR = "_ERR_AUTHENTICATION_ERROR";

  private static final WcRestServiceMethod<WcSession, WcLoginParam> LOGIN_IDENTITY = WcRestServiceMethod
          .builder(HttpMethod.POST, "store/{storeId}/loginidentity", WcLoginParam.class, WcSession.class)
          .secure(true)
          .build();

  private static final WcRestServiceMethod<Void, Void> LOGOUT_IDENTITY = WcRestServiceMethod
          .builder(HttpMethod.DELETE, "store/{storeId}/loginidentity/@self", (Class<Void>) null, Void.class)
          .secure(true)
          .requiresAuthentication(true)
          .build();

  private static final WcRestServiceMethod<WcPreviewToken, WcPreviewTokenParam> PREVIEW_TOKEN = WcRestServiceMethod
          .builder(HttpMethod.POST, "store/{storeId}/previewToken", WcPreviewTokenParam.class, WcPreviewToken.class)
          .secure(true)
          .requiresAuthentication(true)
          .build();

  private static final WcRestServiceMethod<HashMap, Void> USER_CONTEXT_DATA = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/usercontext/@self/contextdata", (Class<Void>) null, HashMap.class)
          .userCookiesSupport(true)
          .build();

  @NonNull
  public Optional<WcSession> login(String logonId, String password, StoreContext storeContext) {
    try {
      List<String> variableValues = singletonList(getStoreId(storeContext));
      WcLoginParam wcLoginParam = new WcLoginParam(logonId, password);

      return getRestConnector().callServiceInternal(LOGIN_IDENTITY, variableValues, emptyMap(), wcLoginParam,
              storeContext, null);

      //if login not successfully a RemoteException is thrown
    } catch (CommerceRemoteException e) {
      if (ERROR_KEY_AUTHENTICATION_ERROR.equals(e.getErrorKey())) {
        throw new InvalidLoginException("The specified logon ID '" + logonId + "' or the used password is incorrect.");
      } else {
        throw e;
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean isLoggedIn(String logonId, StoreContext storeContext, UserContext userContext) {
    try {
      Map<String, String[]> parameters = buildParameterMap()
              .withCurrency(storeContext)
              .withLanguageId(storeContext)
              .build();

      List<String> variableValues = singletonList(getStoreId(storeContext));

      Map userContextData = getRestConnector().callServiceInternal(USER_CONTEXT_DATA, variableValues, parameters, null,
              storeContext, userContext).orElse(null);

      if (userContextData != null && hasText(logonId)) {
        Double value = (Double) DataMapHelper.findValue(userContextData, "basicInfo.callerId", Double.class)
                .orElse(null);
        if (value != null) {
          return equalsWithTypeConversion(logonId, value);
        }
      }
      return false;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @VisibleForTesting
  boolean equalsWithTypeConversion(String logonId, double value) {
    int logonIdInt = Integer.parseInt(logonId);
    int integer = (int) value;
    return Objects.equals(integer, logonIdInt);
  }

  public boolean logout(String storeId) {
    List<String> variableValues = singletonList(storeId);

    getRestConnector().callServiceInternal(LOGOUT_IDENTITY, variableValues, emptyMap(), null, null, null);

    // Todo: if no exception is thrown we assume that the user was logged out successfully. is that correct?
    return true;
  }

  @NonNull
  public Optional<WcPreviewToken> getPreviewToken(WcPreviewTokenParam bodyData, StoreContext storeContext) {
    try {
      List<String> variableValues = singletonList(getStoreId(storeContext));

      return getRestConnector().callService(PREVIEW_TOKEN, variableValues, emptyMap(), bodyData, storeContext, null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }
}
