package com.coremedia.blueprint.contenthub.adapters.rss;

import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.column.Column;
import com.coremedia.contenthub.api.column.ColumnValue;
import com.coremedia.contenthub.api.column.DefaultColumnProvider;
import com.rometools.rome.feed.synd.SyndEntry;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Adds a custom column for the lifecycle status of a content.
 */
class RSSColumnProvider extends DefaultColumnProvider {

  @NonNull
  @Override
  public List<Column> getColumns(Folder folder) {
    List<Column> columns = new ArrayList<>(super.getColumns(folder));
    columns.add(new Column("author", "author", 100, -1));
    columns.add(new Column("lastModified", "lastModified", 100, -1));
    return columns;
  }

  @NonNull
  @Override
  public List<ColumnValue> getColumnValues(ContentHubObject hubObject) {
    if (hubObject instanceof RSSItem) {
      return getColumnValues((RSSItem) hubObject);
    }

    if (hubObject instanceof RSSFolder) {
      return getColumnValues((RSSFolder) hubObject);
    }

    List<ColumnValue> columnValues = new ArrayList<>(super.getColumnValues(hubObject));
    columnValues.add(new ColumnValue("author", null, null, null));
    columnValues.add(new ColumnValue("lastModified", null, null, null));
    return columnValues;
  }

  @NonNull
  private List<ColumnValue> getColumnValues(@NonNull RSSFolder rssFolder) {
    Calendar lastModified = evaluateLastModified(rssFolder);
    List<ColumnValue> columnValues = new ArrayList<>(super.getColumnValues(rssFolder));
    columnValues.add(new ColumnValue("author", null, null, null));
    columnValues.add(new ColumnValue("lastModified", lastModified, null, null));
    return columnValues;
  }

  @Nullable
  private Calendar evaluateLastModified(@NonNull RSSFolder rssFolder) {
    Date publishedDate = rssFolder.getFeed().getPublishedDate();
    if (publishedDate == null) {
      return null;
    }
    return getCalendarFromDate(publishedDate);
  }

  @NonNull
  private List<ColumnValue> getColumnValues(@NonNull RSSItem rssItem) {
    String author = rssItem.getRssEntry().getAuthor();
    SyndEntry rssEntry = rssItem.getRssEntry();
    Date lastModifiedDate = rssEntry.getUpdatedDate() != null ? rssEntry.getUpdatedDate() : rssEntry.getPublishedDate();
    Calendar lastModified = getCalendarFromDate(lastModifiedDate);
    List<ColumnValue> columnValues = new ArrayList<>(super.getColumnValues(rssItem));
    columnValues.add(new ColumnValue("author", author, null, author));
    columnValues.add(new ColumnValue("lastModified", lastModified, null, author));
    return columnValues;
  }

  @NonNull
  private Calendar getCalendarFromDate(@NonNull Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }
}
