package com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.ColumnModel;
import org.springframework.context.annotation.Scope;

/**
 * <p>
 * Wrapper for the column model of the moderated items view. Especially provides
 * IDs for the columns which then can be located with {@link #indexById(String)}.
 * </p>
 *
 * @since 2013-02-25
 */
@ExtJSObject
@Scope("prototype")
public class ModeratedItemsViewColumnModel extends ColumnModel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String COMPLAINT_ID = "complaint";
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String TYPE_ID = "type";
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String USER_ID = "user";
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String TEXT_ID = "text";
  public static final String PRIORITIZED_ID = "prioritized";
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String CREATION_DATE_ID = "creationDate";
  @SuppressWarnings("UnusedDeclaration") // NOSONAR; provided as API
  public static final String LAST_COMPLAINT_DATE_ID = "lastComplaintDate";
}
