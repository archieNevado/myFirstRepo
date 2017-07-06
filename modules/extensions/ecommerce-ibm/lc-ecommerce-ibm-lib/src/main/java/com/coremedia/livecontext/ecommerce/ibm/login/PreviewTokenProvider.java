package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provides preview token for commerce preview.
 */
public class PreviewTokenProvider implements CommercePropertyProvider {

  private LoginService loginService;

  @Nullable
  @Override
  public Object provideValue(@Nonnull Map<String, Object> parameters) {
    String result = null;
    WcPreviewToken previewToken = loginService.getPreviewToken();
    if (previewToken != null) {
      result = previewToken.getPreviewToken();
    }
    return result;
  }

  @Required
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }
}
