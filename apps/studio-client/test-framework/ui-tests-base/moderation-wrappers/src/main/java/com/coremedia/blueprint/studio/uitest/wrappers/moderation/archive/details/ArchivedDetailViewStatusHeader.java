package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.details;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems.ModeratedItemsRowSelectionModel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ArchivedDetailViewStatusHeader extends GridPanel<ModeratedItemsRowSelectionModel> {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "status-field-item-id", global = false)
  private StringDisplayField statusDisplayField;

  public Condition<String> statusText() {
    statusDisplayField.visible().waitUntilTrue();
    return statusDisplayField.value();
  }
}
