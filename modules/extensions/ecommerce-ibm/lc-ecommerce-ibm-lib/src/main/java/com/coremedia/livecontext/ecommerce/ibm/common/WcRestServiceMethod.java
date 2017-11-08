package com.coremedia.livecontext.ecommerce.ibm.common;

import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WcRestServiceMethod<T, P> {

  /**
   * it repairs a shortcomming of betamax that cannot record calls that use https
   */
  private static final boolean insecure;

  static {
    String betamaxMode = System.getProperty("betamax.defaultMode");
    String betamaxIgnoreHosts = System.getProperty("betamax.ignoreHosts");
    insecure = !"*".equals(betamaxIgnoreHosts) && ("READ_WRITE".equals(betamaxMode) || "WRITE_ONLY".equals(betamaxMode) || "READ_ONLY".equals(betamaxMode));
  }

  private final HttpMethod method;
  private final String uriTemplate;
  private final boolean secure;
  private final boolean requiresAuthentication;
  private final boolean search;
  private final boolean previewSupport;
  private final boolean userCookiesSupport;
  private final boolean contractsSupport;
  private final Class<P> parameterType;
  private final Class<T> returnType;

  private WcRestServiceMethod(@Nonnull Builder<T, P> builder) {
    method = builder.method;
    uriTemplate = builder.uriTemplate;
    secure = !insecure && builder.secure;
    requiresAuthentication = builder.requiresAuthentication;
    search = builder.search;
    previewSupport = builder.previewSupport;
    userCookiesSupport = builder.userCookiesSupport;
    contractsSupport = builder.contractsSupport;
    parameterType = builder.parameterType;
    returnType = builder.returnType;
  }

  @Nonnull
  public static <T, P> Builder<T, P> builder(@Nonnull HttpMethod method, @Nonnull String uriTemplate,
                                             @Nullable Class<P> parameterType, @Nonnull Class<T> returnType) {
    return new Builder<>(method, uriTemplate, false, parameterType, returnType);
  }

  @Nonnull
  public static <T> Builder<T, Void> builderForSearch(@Nonnull HttpMethod method, @Nonnull String uriTemplate,
                                                      @Nonnull Class<T> returnType) {
    return new Builder<>(method, uriTemplate, true, Void.class, returnType);
  }

  public static class Builder<T, P> {

    private final HttpMethod method;
    private final String uriTemplate;
    private boolean secure = false;
    private boolean requiresAuthentication = false;
    private final boolean search;
    private boolean previewSupport = false;
    private boolean userCookiesSupport = false;
    private boolean contractsSupport = false;
    private final Class<P> parameterType;
    private final Class<T> returnType;

    public Builder(@Nonnull HttpMethod method, @Nonnull String uriTemplate, boolean search,
                   @Nullable Class<P> parameterType, @Nonnull Class<T> returnType) {
      this.method = method;
      this.uriTemplate = uriTemplate;
      this.search = search;
      this.parameterType = parameterType;
      this.returnType = returnType;
    }

    @Nonnull
    public Builder<T, P> secure(boolean secure) {
      this.secure = secure;
      return this;
    }

    @Nonnull
    public Builder<T, P> requiresAuthentication(boolean requiresAuthentication) {
      this.requiresAuthentication = requiresAuthentication;
      return this;
    }

    @Nonnull
    public Builder<T, P> previewSupport(boolean previewSupport) {
      this.previewSupport = previewSupport;
      return this;
    }

    @Nonnull
    public Builder<T, P> userCookiesSupport(boolean userCookiesSupport) {
      this.userCookiesSupport = userCookiesSupport;
      return this;
    }

    @Nonnull
    public Builder<T, P> contractsSupport(boolean contractsSupport) {
      this.contractsSupport = contractsSupport;
      return this;
    }

    @Nonnull
    public WcRestServiceMethod<T, P> build() {
      return new WcRestServiceMethod<>(this);
    }
  }

  @Nonnull
  public HttpMethod getMethod() {
    return method;
  }

  public boolean isRequiresAuthentication() {
    return requiresAuthentication;
  }

  public boolean isPreviewSupport() {
    return previewSupport;
  }

  @Nonnull
  public String getUriTemplate() {
    return uriTemplate;
  }

  public boolean isSecure() {
    return secure;
  }

  public boolean isSearch() {
    return search;
  }

  public boolean isUserCookiesSupport() {
    return userCookiesSupport;
  }

  public boolean isContractsSupport() {
    return contractsSupport;
  }

  @Nullable
  public Class<P> getParameterType() {
    return parameterType;
  }

  @Nonnull
  public Class<T> getReturnType() {
    return returnType;
  }
}
