package com.coremedia.blueprint.contenthub.adapters.rss;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.MimeTypeFactory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.activation.MimeType;
import java.util.Date;

public class RSSItem extends RSSHubObject implements Item {

  private final SyndEntry rssEntry;

  RSSItem(ContentHubAdapterBinding binding, ContentHubObjectId id, SyndFeed feed, SyndEntry rssEntry) {
    super(binding, id, feed);
    this.rssEntry = rssEntry;
  }

  SyndEntry getRssEntry() {
    return rssEntry;
  }

  @Override
  public MimeType getItemType() {
    return MimeTypeFactory.create("rss");
  }

  @Override
  public long getSize() {
    return -1;
  }

  @NonNull
  @Override
  public String getName() {
    return rssEntry.getTitle();
  }

  @Nullable
  @Override
  public String getDescription() {
    if(rssEntry.getDescription() != null) {
      return rssEntry.getDescription().getValue();
    }

    return null;
  }

  @Override
  public Date getLastModified() {
    Date date = rssEntry.getUpdatedDate();
    if (date == null) {
      date = rssEntry.getPublishedDate();
    }
    if (date == null) {
      date = getFeed().getPublishedDate();
    }
    return date;
  }

  @Nullable
  @Override
  public String getTargetContentType() {
    return "CMArticle";
  }
}
