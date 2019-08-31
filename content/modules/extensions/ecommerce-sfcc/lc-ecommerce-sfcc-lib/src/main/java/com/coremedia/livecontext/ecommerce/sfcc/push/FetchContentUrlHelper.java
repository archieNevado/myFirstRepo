package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory.createHttpClient;
import static com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl.PREVIEW_PARAMETER;
import static com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl.PUSH_MODE_PARAMETER;
import static com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl.PUSH_MODE_PARAMETER_VALUE_RECORD;

public class FetchContentUrlHelper {

  private static final Logger LOG = LoggerFactory.getLogger(FetchContentUrlHelper.class);

  public static final String PUSH_MODE_VIEW = "pushMode";

  private final String previewUrlPrefix;
  private final IdProvider idProvider;
  private RestTemplate restTemplate;

  public FetchContentUrlHelper(String previewUrlPrefix, IdProvider idProvider) {
    this.previewUrlPrefix = previewUrlPrefix;
    this.idProvider = idProvider;
  }

  @PostConstruct
  void initialize() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(createHttpClient(true));
    restTemplate = new RestTemplate(requestFactory);
  }

  String computePreviewUrl(String commerceIdOrConentId, StoreContext storeContext) {
    UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(previewUrlPrefix)
            .path("/preview")
            .queryParam("id", commerceIdOrConentId)
            .queryParam("view", PUSH_MODE_VIEW)
            .queryParam(PREVIEW_PARAMETER, false)
            .queryParam(PUSH_MODE_PARAMETER, PUSH_MODE_PARAMETER_VALUE_RECORD);

    //add site parameter for commerce ids
    Object o = idProvider.parseId(commerceIdOrConentId);
    if (o instanceof CommerceBean){
      String siteId = storeContext.getSiteId();
      ucb.queryParam("site", siteId);
    }

    return ensureSchemeForUrl(ucb.build().toUriString());
  }

  @VisibleForTesting
  @NonNull
  Optional<String> getSeoSegment(Content content) {
    String seoBuilderUrl = previewUrlPrefix + "/" + "seoSegment/" + IdHelper.parseContentId(content.getId());
    seoBuilderUrl = ensureSchemeForUrl(seoBuilderUrl);

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(seoBuilderUrl, String.class);
      if (response.getStatusCode().isError()) {
        LOG.error("Requesting seo segments from {} returns {}", seoBuilderUrl, response.getStatusCode());
        return Optional.empty();
      }
      return Optional.ofNullable(response.getBody());

    } catch (RestClientException e) {
      LOG.error("Cannot request seo segment from {} ({})", seoBuilderUrl, e.getMessage(), e);
    }

    return Optional.empty();
  }

  /**
   * If url is scheme relative (starts with "//") use the current studio request and prefix the url with its scheme.
   *
   * @param url url to preview cae
   * @return url starting with http or https
   */
  private static String ensureSchemeForUrl(@NonNull String url) {
    if (url.startsWith("http")) {
      return url;
    } else if (url.startsWith("//")) {
      //try to get scheme from current request
      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (requestAttributes == null) {
        // use http as fallback
        return "http" + ":" + url;
      }
      HttpServletRequest request = requestAttributes.getRequest();
      String scheme = request.getScheme();
      return scheme + ":" + url;
    } else {
      throw new IllegalArgumentException(url + "not a valid url");
    }
  }
}
