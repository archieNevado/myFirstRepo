package com.coremedia.blueprint.contenthub.adapters.youtube;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubObject;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract public class YouTubeHubObject implements ContentHubObject {

  private ContentHubObjectId hubId;
  private ContentHubAdapterBinding binding;

  YouTubeHubObject(ContentHubAdapterBinding binding, ContentHubObjectId hubId) {
    this.binding = binding;
    this.hubId = hubId;
  }

  @NonNull
  @Override
  public ContentHubAdapterBinding getBinding() {
    return binding;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  @Override
  public ContentHubObjectId getId() {
    return hubId;
  }
}
