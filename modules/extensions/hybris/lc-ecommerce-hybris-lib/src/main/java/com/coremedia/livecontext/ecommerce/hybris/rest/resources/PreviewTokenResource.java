package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import com.coremedia.livecontext.ecommerce.hybris.rest.AccessToken;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PreviewTokenDocument;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PreviewTokenResource extends AbstractHybrisResource {
  private final static String PREVIEW_TOKEN_PATH = "/preview/";

  public PreviewTokenDocument getPreviewToken(Map bodyData, AccessToken accessToken) {
    List<String> uriTemplateParameters = new ArrayList<>(Arrays.asList(accessToken.getToken()));
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("access_token", accessToken.getToken());

    return getConnector().performPost(PREVIEW_TOKEN_PATH, PreviewTokenDocument.class, uriTemplateParameters, queryParams, bodyData, true);
  }

}
