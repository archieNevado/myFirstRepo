package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.cms.editor.components.premular.fields.LinkListPropertyFieldToolbar;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.ui.IconButton;
import org.springframework.context.annotation.Scope;

/**
 * A Linklist-Property-Field toolbar enriched by placement-specific buttons and the viewtype combo box.
 */
@ExtJSObject
@Scope("prototype")
public class PlacementFieldToolbar extends LinkListPropertyFieldToolbar {
  @FindByExtJS(itemId = "inheritButton")
  private IconButton inheritButton;

  @FindByExtJS(itemId = "lockButton")
  private IconButton lockButton;

  @FindByExtJS(itemId = "viewtypeSelector")
  private ComboBoxField viewtypeSelector;

  public IconButton getInheritButton() {
    return inheritButton;
  }

  public IconButton getLockButton() {
    return lockButton;
  }

  public ComboBoxField getViewtypeSelector() {
    return viewtypeSelector;
  }
}
