package com.coremedia.blueprint.contenthub.adapters.rss;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Folder;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Date;

public class RSSFolder extends RSSHubObject implements Folder {

  RSSFolder(ContentHubAdapterBinding binding, ContentHubObjectId id, SyndFeed feed) {
    super(binding, id, feed);
  }

  @Override
  public String getType() {
    return "feed";
  }

  @Override
  public Date getLastModified() {
    return getFeed().getPublishedDate();
  }
}
