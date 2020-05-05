package com.coremedia.blueprint.studio.uitest.base.wrappers.preview;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;

@ExtJSObject(xtype="com.coremedia.blueprint.studio.config.components.previewDateSelectorDialog", global=true)
public class PreviewDateTimeDialog extends Window {
  public static final String XTYPE = "com.coremedia.blueprint.studio.config.components.previewDateSelectorDialog";


  public Container getPreviewDateSelector() {
    return evalJsProxy(Container.class, "self.find('itemId','previewDateSelector')[0]");
  }

  public ComboBoxField getTimeComboBox() {
    final Container dateTimePropertyField = getPreviewDateSelector().evalJsProxy(Container.class, "self.find('dateTimePropertyField')[0]");
    return dateTimePropertyField.evalJsProxy(ComboBoxField.class, "self.items.items[1]");
  }
}
