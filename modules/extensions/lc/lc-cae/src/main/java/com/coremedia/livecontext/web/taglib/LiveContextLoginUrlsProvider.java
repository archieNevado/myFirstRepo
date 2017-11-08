package com.coremedia.livecontext.web.taglib;

/**
 * Provides URLs to Commerce Login Form and logout handlers.
 */
public interface LiveContextLoginUrlsProvider {

  /**
   * Returns the URL for the login form of the Commerce system.
   *
   * @return login form URL
   */
  String buildLoginFormUrl();

  /**
   * Returns the URL to log out of the Commerce system.
   *
   * @return logout URL
   */
  String buildLogoutUrl();

  /**
   *  Transforms the given URL of the {@link com.coremedia.livecontext.handler.LoginStatusHandler}
   *  for the use with the Commerce system.
   *
   *  <p>For example, the implementation could add required query parameters.
   *
   * @param url login status handler URL
   * @return transformed URL
   */
  String transformLoginStatusUrl(String url);

}
