package com.coremedia.blueprint.contenthub.adapters.rss;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubObject;
import com.rometools.rome.feed.synd.SyndFeed;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract public class RSSHubObject implements ContentHubObject {

  private ContentHubObjectId hubId;
  private ContentHubAdapterBinding binding;
  private SyndFeed feed;
  private String name;

  RSSHubObject(ContentHubAdapterBinding binding, ContentHubObjectId hubId, SyndFeed feed) {
    this.binding = binding;
    this.hubId = hubId;
    this.name = feed.getTitle();
    this.feed = feed;
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @NonNull
  @Override
  public ContentHubAdapterBinding getBinding() {
    return binding;
  }

  public void setBinding(ContentHubAdapterBinding binding) {
    this.binding = binding;
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

  SyndFeed getFeed() {
    return feed;
  }
}
