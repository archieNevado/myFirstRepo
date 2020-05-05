package com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

/**
 * The page grid wrapper, access all placement and the layout combo.
 */
@ExtJSObject
@Scope("prototype")
public class PageGridPropertyField extends Container {
  public static final String XTYPE = "com.coremedia.blueprint.base.pagegrid.config.pageGridPropertyField";

  @FindByExtJS(itemId = "layoutSelector")
  private PageGridLayoutSelector layoutSelector;

  @FindByExtJS(itemId = "placementsContainer")
  private Container placementsContainer;

  public Condition<String> propertyName() {
    return stringCondition("self.propertyName");
  }

  public String getPropertyName() {
    return propertyName().await();
  }

  public PageGridLayoutSelector getLayoutSelector() {
    return layoutSelector;
  }

  public Container getPlacementsContainer() {
    return placementsContainer;
  }

  /**
   * Return a placement field wrapper.
   *
   * @param sectionName the name of the placement
   * @return the wrapper
   */
  public PlacementField getPlacementField(final String sectionName) {
    return placementsContainer.find(PlacementField.class, ExtJSBy.propertyValue("propertyFieldName", getPropertyName() + "-" + sectionName));
  }
}
