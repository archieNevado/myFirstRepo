package com.coremedia.livecontext.fragment.links.transformers;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * This class can replace tokens in a uri. Tokens must be enclosed with simple curly braces.
 * You can choose whether the result should be encoded or not. The potential replacements come from a map.
 */
public class TokenResolverUtils {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private TokenResolverUtils() {
  }

  /**
   * Replaces tokens in simple curly braces within the given URI as specified by the parameters map, mustBeComplete,
   * and encode.
   *
   * @param uri            an URI that contains the tokens
   * @param parameters     the map containing the replacements for the tokens
   * @param mustBeComplete <code>false</code> to leave tokens as-is if they are not part of the parameters map.
   *                       Otherwise non-existing tokens will be removed from the URI.
   * @param encode         <code>true</code> to URI-encode the resulting URI (only if there are tokens)
   * @return the URI containing the replacements from the parameter map.
   */
  public static String replaceTokens(@NonNull String uri, @NonNull Map<String, ?> parameters, boolean mustBeComplete,
                                     boolean encode) {
    boolean isEncoded = false;
    if (uri.contains("%7B")) { // URI-encoded opening curly brace
      isEncoded = true;
    } else if (uri.indexOf('{') == -1) {
      return uri;
    }

    try {
      // The URI needs to be decomposed prior to decoding it. Decoding could otherwise result in a different URL.
      UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(uri);
      UriComponents uriComponents = ucb.build(isEncoded);
      if (isEncoded) {
        uriComponents = decodeUriComponents(uriComponents);
      }

      uriComponents = uriComponents.expand(mustBeComplete ? parameters : new TokenSkippingFakedMap(parameters));
      if (encode || isEncoded) { // if the source was encoded then the result should be too
        uriComponents = uriComponents.encode();
      }

      return uriComponents.toUriString();
    } catch (IllegalArgumentException ex) {
      String message = String.format("Cannot transform url: \"%s\" (%s)", uri, ex.getMessage());
      LOG.warn(message);
      LOG.debug(message, ex);
    }

    return uri;
  }

  /**
   * It is not possible to simply decode the entire String as it could lead to a different URI if the query for
   * example contains an encoded & or #. Consequently each component needs be decoded separately.
   */
  @NonNull
  private static UriComponents decodeUriComponents(@NonNull UriComponents uriComponents) {
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

    String scheme = uriComponents.getScheme();
    if (scheme != null) {
      builder.scheme(decode(scheme));
    }

    String userInfo = uriComponents.getUserInfo();
    if (userInfo != null) {
      builder.userInfo(decode(userInfo));
    }

    String host = uriComponents.getHost();
    if (host != null) {
      builder.host(decode(host));
    }

    // As an int value, the port cannot contain a pattern, so there is no need to decode it.
    builder.port(uriComponents.getPort());

    String path = uriComponents.getPath();
    if (path != null) {
      builder.path(decode(path));
    }

    String schemeSpecificPart = uriComponents.getSchemeSpecificPart();
    if (schemeSpecificPart != null) {
      builder.schemeSpecificPart(decode(schemeSpecificPart));
    }

    addDecodedQueryParameters(builder, uriComponents.getQueryParams());

    String fragment = uriComponents.getFragment();
    if (fragment != null) {
      builder.fragment(decode(fragment));
    }

    return builder.build();
  }

  private static void addDecodedQueryParameters(@NonNull UriComponentsBuilder builder,
                                                @NonNull MultiValueMap<String, String> encodedQueryParams) {
    for (Map.Entry<String, List<String>> queryParam : encodedQueryParams.entrySet()) {
      String key = decode(queryParam.getKey());
      List<String> values = queryParam.getValue();
      if (key != null && values != null) {
        for (String value : values) {
          builder.queryParam(key, decode(value));
        }
      }
    }
  }

  private static String decode(@Nullable String uriComponent) {
    return uriComponent == null ? null : UriUtils.decode(uriComponent, StandardCharsets.UTF_8);
  }

  private static class TokenSkippingFakedMap extends AbstractMap<String, Object> {

    private Map<String, ?> delegate;

    public TokenSkippingFakedMap(Map<String, ?> delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean containsKey(Object key) {
      return true;
    }

    @Override
    public boolean containsValue(Object value) {
      return true;
    }

    @Override
    public Object get(Object key) {
      Object value = delegate.get(key);
      return value != null ? value : UriComponents.UriTemplateVariables.SKIP_VALUE;
    }

    @Override
    @NonNull
    public Set<Entry<String, Object>> entrySet() {
      return Collections.emptySet();
    }
  }
}
