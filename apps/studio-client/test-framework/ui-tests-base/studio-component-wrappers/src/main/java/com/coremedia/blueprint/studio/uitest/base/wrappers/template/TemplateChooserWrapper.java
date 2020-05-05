package com.coremedia.blueprint.studio.uitest.base.wrappers.template;

import com.coremedia.uitesting.ext3.wrappers.DataView;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class TemplateChooserWrapper extends Panel {

  public static final String XTYPE = "com.coremedia.blueprint.studio.template.templateBeanListChooser";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "dataView")
  private DataView dataView;

  public void selectTemplate(final int index) {
    dataView.containsAtLeast(index)
            .withMessage("In order to select template no. " + index + " at least that many templates need to be present.")
            .waitUntilTrue();
    dataView.select(index);
  }

}
