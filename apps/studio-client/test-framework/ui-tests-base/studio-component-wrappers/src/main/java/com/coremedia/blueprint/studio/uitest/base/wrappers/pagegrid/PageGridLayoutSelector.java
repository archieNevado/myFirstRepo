package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.webdriver.conditions.WebElementConditions;
import net.joala.condition.Condition;
import org.openqa.selenium.By;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

/**
 * The layout chooser and the display area for the effective layout.
 */
@ExtJSObject
@Scope("prototype")
public class PageGridLayoutSelector extends Container {
  public static final String XTYPE = "com.coremedia.blueprint.base.pagegrid.config.pageGridLayoutSelector";

  @FindByExtJS(itemId = "combobox")
  private ComboBoxField chooser;

  @Inject
  private WebElementConditions webElementConditions;

  public ComboBoxField getChooser() {
    return chooser;
  }

  public Condition<String> effectiveLayoutDescription() {
    visible().assertTrue();
    return webElementConditions.text(getEl(), By.className("cm-combo-box-item__description"));
  }

  public Condition<String> effectiveLayoutName() {
    visible().assertTrue();
    return webElementConditions.text(getEl(), By.className("cm-combo-box-item__name"));
  }
}
