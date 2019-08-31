package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.ui.Column;
import com.coremedia.contenthub.api.ui.ColumnModelProvider;
import com.coremedia.contenthub.api.ui.ColumnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Adds a custom column for the lifecycle status of a content.
 */
public class YouTubeColumnModelProvider implements ColumnModelProvider {

  @NonNull
  @Override
  public String getFactoryId() {
    return "youtube";
  }

  @NonNull
  @Override
  public List<Column> getColumns(Folder folder) {
    return Collections.emptyList();
  }

  @NonNull
  @Override
  public List<ColumnValue> getColumnValues(ContentHubObject hubObject) {
    return Collections.emptyList();
  }

  @NonNull
  @Override
  public List<String> getDefaultColumns() {
    return Arrays.asList(ColumnModelProvider.COLUMN_TYPE,
            ColumnModelProvider.COLUMN_NAME,
            ColumnModelProvider.COLUMN_LAST_MODIFIED);
  }
}
