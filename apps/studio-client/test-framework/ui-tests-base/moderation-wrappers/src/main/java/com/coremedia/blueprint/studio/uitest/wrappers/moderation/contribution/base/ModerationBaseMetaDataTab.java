package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationBaseMetaDataTab extends Panel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-statusbar-author-information", global = false)
  private StringDisplayField userSubtext;

  public StringDisplayField getUserSubtext() {
    return userSubtext;
  }

}
