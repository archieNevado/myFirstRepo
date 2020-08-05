package com.coremedia.blueprint.assets.cae.handlers;

import com.coremedia.blueprint.assets.cae.DownloadPortal;
import com.coremedia.blueprint.cae.view.HashBasedFragmentHandler;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import com.coremedia.objectserver.view.dynamic.DynamicIncludeProvider;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

@DefaultAnnotation(NonNull.class)
public class DownloadPortalPredicate implements DynamicIncludePredicate, DynamicIncludeProvider {

  private static final List<String> VALID_PARAMS = List.of(
          DownloadPortalHandler.CATEGORY_REQUEST_PARAMETER_NAME,
          DownloadPortalHandler.ASSET_REQUEST_PARAMETER_NAME,
          DownloadPortalHandler.SUBJECT_REQUEST_PARAMETER_NAME,
          DownloadPortalHandler.SEARCH_REQUEST_PARAMETER_NAME,
          DownloadPortalHandler.DOWNLOAD_COLLECTION_REQUEST_PARAMETER_NAME
  );

  @Override
  public boolean test(RenderNode input) {
    return input.getBean() instanceof DownloadPortal
            && input.getView() == null;
  }

  @Override
  public HashBasedFragmentHandler getDynamicInclude(Object delegate, String view) {
    return new HashBasedFragmentHandler(delegate, null, VALID_PARAMS);
  }
}
