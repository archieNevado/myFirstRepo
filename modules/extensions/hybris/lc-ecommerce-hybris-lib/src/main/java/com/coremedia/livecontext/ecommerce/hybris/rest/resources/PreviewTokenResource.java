package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import com.coremedia.livecontext.ecommerce.hybris.rest.AccessToken;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PreviewTokenDocument;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;
import java.util.Map;

public class PreviewTokenResource extends AbstractHybrisResource {

  private static final String PREVIEW_TOKEN_PATH = "/preview/";

  @Nullable
  public PreviewTokenDocument getPreviewToken(@Nullable Map bodyData, @NonNull AccessToken accessToken) {
    List<String> uriTemplateParameters = newUriTemplateParameters("accessToken", accessToken.getToken());

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("access_token", accessToken.getToken());

    return getConnector().performPost(PREVIEW_TOKEN_PATH, PreviewTokenDocument.class, uriTemplateParameters,
            queryParams, bodyData, true);
  }
}
