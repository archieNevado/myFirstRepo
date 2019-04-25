package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper.decodeEntryTransparently;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper.replaceTokens;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper.replaceTokensAndDecrypt;

/**
 * Salesforce Commerce Cloud Open Commerce API OAuth connector.
 */
@DefaultAnnotation(NonNull.class)
@Service("sfccOAuthConnector")
public class OAuthConnector {

  private static final Logger LOG = LoggerFactory.getLogger(OAuthConnector.class);

  // --- HTTP Header constants ---
  private static final String AUTHORIZATION_HEADER = "Authorization";

  private final String protocol;
  private final String host;
  private final String path;
  private final String clientId;
  private final String password;

  private final RestTemplate restTemplate = new RestTemplate();

  @Nullable
  private volatile AccessToken accessToken;

  OAuthConnector(SfccOAuthConfigurationProperties properties) {
    protocol = properties.getProtocol();
    host = properties.getHost();
    path = properties.getPath();
    clientId = properties.getClientId();
    password = decodeEntryTransparently(properties.getClientPassword());
  }

  public Optional<AccessToken> getOrRequestAccessToken(StoreContext storeContext) {
    if (accessToken == null || accessToken.isExpired()) {
      accessToken = requestAccessToken(storeContext);
    }

    return Optional.ofNullable(accessToken);
  }

  /**
   * Requests an access token from the configured Account Manager host using the configured clientId and password.
   */
  @Nullable
  private AccessToken requestAccessToken(StoreContext storeContext) {
    String url = buildRequestUrl();

    HttpEntity<String> requestEntity = buildRequestEntity(
            getClientId(storeContext),
            getPassword(storeContext));

    LOG.info("Requesting access token. {}", url);
    try {
      ResponseEntity<AccessToken> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
              AccessToken.class);

      if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        LOG.warn("Unable to request authentication token. Check credentials!");
        return null;
      }

      AccessToken result = responseEntity.getBody();
      LOG.info("Fetched access token. {}", result);
      return result;
    } catch (HttpClientErrorException e) {
      LOG.error("Token request '{}' returned with HTTP status code {} ({})", url, e.getStatusCode(),
              e.getLocalizedMessage());
      return null;
    }
  }

  private String buildRequestUrl() {
    return UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(path)
            .build().toString();
  }

  private static HttpEntity<String> buildRequestEntity(String clientId, String password) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    headers.add(AUTHORIZATION_HEADER, createAuthorizationHeaderValue(clientId, password));

    return new HttpEntity<>("grant_type=client_credentials", headers);
  }

  private static String createAuthorizationHeaderValue(String clientId, String password) {
    // Create basic auth header
    String auth = clientId + ":" + password;

    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));

    return "Basic " + new String(encodedAuth);
  }

  private String getClientId(StoreContext storeContext) {
    return replaceTokens(clientId, storeContext);
  }

  private String getPassword(StoreContext storeContext) {
    return replaceTokensAndDecrypt(password, storeContext);
  }
}
