package com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems;

import com.coremedia.uitesting.ext3.wrappers.selection.RowModel;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope("prototype")
public class ModeratedItemsRowSelectionModel extends RowModel {


  /**
   * Get the row index of the row with the given properties
   *
   * @param userNameValue      the name of the user or the author name of the comment
   * @param detailsColumnValue the given and the last name of the user, or the comments article ref title
   * @param contributionType   the type of the contribution
   * @return the row index
   */
  public Condition<Long> rowIndex(final String userNameValue, final String detailsColumnValue, final String contributionType) {
    return longCondition("" +
            "self.store.findBy(" +
            "  function(record,id){" +
            "    function startsWith(f, s){return f && f.indexOf(s) === 0};" +
            "    return (startsWith(record.get(userNameFieldName), userNameValue) " +
            "         && startsWith(record.get(detailsColumnFieldName), detailsColumnValue) " +
            "         && record.get(fieldType) === contributionType);" +
            "  })",
            "userNameFieldName", ModeratedItemsView.FIELD_AUTHOR_NAME,
            "userNameValue", userNameValue,
            "detailsColumnFieldName", ModeratedItemsView.FIELD_DETAILS_TEXT,
            "detailsColumnValue", detailsColumnValue,
            "fieldType", ModeratedItemsView.FIELD_TYPE,
            "contributionType", contributionType);
  }

}
