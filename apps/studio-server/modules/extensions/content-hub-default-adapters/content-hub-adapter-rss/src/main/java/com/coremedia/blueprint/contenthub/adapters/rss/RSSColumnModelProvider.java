package com.coremedia.blueprint.contenthub.adapters.rss;

import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.ui.Column;
import com.coremedia.contenthub.api.ui.ColumnModelProvider;
import com.coremedia.contenthub.api.ui.ColumnValue;
import com.coremedia.contenthub.ui.DefaultAdapterColumn;
import com.coremedia.contenthub.ui.DefaultAdapterColumnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * Adds a custom column for the lifecycle status of a content.
 */
public class RSSColumnModelProvider implements ColumnModelProvider {

  @NonNull
  @Override
  public String getFactoryId() {
    return "rss";
  }

  @NonNull
  @Override
  public List<Column> getColumns(Folder folder) {
    return Arrays.asList(new DefaultAdapterColumn("author", "author", 150, -1));
  }

  @NonNull
  @Override
  public List<ColumnValue> getColumnValues(ContentHubObject hubObject) {
    String author = null;
    if(hubObject instanceof RSSItem) {
      RSSItem item = (RSSItem) hubObject;
      author = item.getRssEntry().getAuthor();
    }

    return Arrays.asList(new DefaultAdapterColumnValue("author", author, null, author));
  }

  @NonNull
  @Override
  public List<String> getDefaultColumns() {
    return Arrays.asList(ColumnModelProvider.COLUMN_TYPE,
            ColumnModelProvider.COLUMN_NAME,
            ColumnModelProvider.COLUMN_LAST_MODIFIED);
  }
}
