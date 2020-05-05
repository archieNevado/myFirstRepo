package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.selection.RowModel;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope("prototype")
public class UserSearchResultRowSelectionModel extends RowModel {

  private static final String USER_NAME_FIELD_NAME = "name";

  /**
   * Check, if the row is available at the specified field with the given value.
   *
   * @param userNameValue      the name of the user or the author name of the comment
   * @return BooleanCondition - indicates whether the row is available
   */
  public BooleanCondition rowAvailable(final String userNameValue) {
    return booleanCondition(
            "self.store.findBy(function(record){"
                    + "function startsWith(f, s){return f && f.indexOf(s) === 0};"
                    + "return (startsWith(record.get(userNameFieldName),userNameValue));"
                    + "}) !== -1",
            "userNameFieldName", USER_NAME_FIELD_NAME,
            "userNameValue", userNameValue)
            .withMessage(String.format("Check if row is available with '%s'=='%s", USER_NAME_FIELD_NAME, userNameValue));
  }

  /**
   * Check, if the row is available at the specified field with the given value.
   *
   * @param userNameValue      the name of the user or the author name of the comment
   * @return BooleanCondition - indicates whether the row is available
   */
  public BooleanCondition rowAvailable(final String userNameValue, String emailAddressValue, String stateValue) {
    return booleanCondition("self.store.findBy(function(record){" +
            "function startsWith(f, s){return f && f.indexOf(s) === 0};" +
            "return (startsWith(record.get(userNameFieldName),userNameValue) && record.get(emailAddressFieldName)=== emailAddressValue && record.get(stateFieldName)=== stateValue);}) != -1",
            "userNameFieldName", USER_NAME_FIELD_NAME,
            "emailAddressFieldName", "email",
            "stateFieldName", "state",
            "userNameValue", userNameValue,
            "emailAddressValue", emailAddressValue,
            "stateValue", stateValue)
            .withMessage(String.format("Check if row is available with '%s'=='%s', '%s'=='%s', '%s'=='%s'",
                    USER_NAME_FIELD_NAME, userNameValue, "email", emailAddressValue, "state", stateValue));
  }

  public Condition<Long> rowIndex(final String userNameValue) {
    return longCondition("" +
            "self.store.findBy(" +
            "  function(record,id){" +
            "    function startsWith(f, s){return f && f.indexOf(s) === 0};" +
            "    return startsWith(record.get(userNameFieldName), userNameValue);" +
            "  })",
            "userNameFieldName", USER_NAME_FIELD_NAME,
            "userNameValue", userNameValue);
  }
}
