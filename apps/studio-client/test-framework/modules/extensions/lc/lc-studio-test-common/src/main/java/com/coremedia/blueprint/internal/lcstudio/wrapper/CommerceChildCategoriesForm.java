package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ext3.wrappers.Component;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.Radio;
import com.coremedia.uitesting.ext3.wrappers.form.RadioGroup;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceChildCategoriesForm.XTYPE)
@Scope("prototype")
public class CommerceChildCategoriesForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceChildCategoriesForm";

  private static final String INHERIT_OR_SELECT_RADIO_GROUP_ITEM_ID = "inheritOrSelectRadioGroup";
  private static final String INHERIT_RADIO_BUTTON_ITEM_ID = "inheritFromCatalogRadioButton";
  private static final String SELECT_RADIO_BUTTON_ITEM_ID = "selectChildrenRadioButton";

  private static final String INHERITED_CATEGORIES_CONTAINER_ITEM_ID = "inheritedCategories";
  private static final String INHERITED_CATEGORIES_LIST_ITEM_ID = "readOnlyCatalogLink";

  private static final String SELECTED_CATEGORIES_CONTAINER_ITEM_ID = "selectedCategories";
  private static final String SELECTED_CATEGORIES_DROP_AREA_ITEM_ID = "dropArea";

  @FindByExtJS(itemId = INHERIT_OR_SELECT_RADIO_GROUP_ITEM_ID)
  private RadioGroup inheritOrSelectRadioGroup;

  @FindByExtJS(itemId = INHERITED_CATEGORIES_CONTAINER_ITEM_ID)
  private Container inheritedCategoriesContainer;

  public Radio getInheritRadio() {
    return (Radio) inheritOrSelectRadioGroup.find(ExtJSBy.itemId(INHERIT_RADIO_BUTTON_ITEM_ID));

  }

  public Radio getSelectRadio() {
    return (Radio) inheritOrSelectRadioGroup.find(ExtJSBy.itemId(SELECT_RADIO_BUTTON_ITEM_ID));
  }

  public GridPanel getInheritedCategoriesList() {
    return inheritedCategoriesContainer.find(GridPanel.class, ExtJSBy.itemId(INHERITED_CATEGORIES_LIST_ITEM_ID));
  }

  public GridPanel getSelectedCategoriesList() {
    return find(GridPanel.class, ExtJSBy.itemId(SELECTED_CATEGORIES_CONTAINER_ITEM_ID));
  }

  public Component getSelectedCategoriesDropArea() {
    return getSelectedCategoriesList().find(Component.class, ExtJSBy.itemId(SELECTED_CATEGORIES_DROP_AREA_ITEM_ID));
  }
}
