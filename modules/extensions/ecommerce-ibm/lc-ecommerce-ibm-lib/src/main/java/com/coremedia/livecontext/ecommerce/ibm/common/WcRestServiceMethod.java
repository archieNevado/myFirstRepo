package com.coremedia.livecontext.ecommerce.ibm.common;

import org.springframework.http.HttpMethod;

public class WcRestServiceMethod<T, P> {

  /**
   * it repairs a shortcomming of betamax that cannot record calls that use https
   */
  private static final boolean inSecure;

  static {
    String betamaxMode = System.getProperty("betamax.defaultMode");
    String betamaxIgnoreHosts = System.getProperty("betamax.ignoreHosts");
    inSecure = !"*".equals(betamaxIgnoreHosts) && ("READ_WRITE".equals(betamaxMode) || "WRITE_ONLY".equals(betamaxMode));
  }

  private final HttpMethod method;
  private final String uriTemplate;
  private final boolean secure;
  private final boolean requiresAuthentication;
  private final boolean search;
  private final boolean previewSupport;
  private boolean userCookiesSupport;
  private boolean contractsSupport;
  private Class<P> parameterType;
  private Class<T> returnType;

  public WcRestServiceMethod(HttpMethod method,
                             String uriTemplate,
                             boolean secure,
                             boolean requiresAuthentication,
                             boolean search,
                             boolean previewSupport,
                             boolean userCookiesSupport,
                             boolean contractsSupport,
                             Class<P> parameterType, Class<T> returnType) {
    this.method = method;
    this.uriTemplate = uriTemplate;
    this.secure = !inSecure && secure;
    this.requiresAuthentication = requiresAuthentication;
    this.search = search;
    this.previewSupport = previewSupport;
    this.userCookiesSupport = userCookiesSupport;
    this.contractsSupport = contractsSupport;
    this.parameterType = parameterType;
    this.returnType = returnType;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public boolean isRequiresAuthentication() {
    return requiresAuthentication;
  }

  public boolean isPreviewSupport() {
    return previewSupport;
  }

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

  public Class<P> getParameterType() {
    return parameterType;
  }

  public Class<T> getReturnType() {
    return returnType;
  }
}
