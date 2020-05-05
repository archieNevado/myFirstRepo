package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid.PageGridPropertyField;
import com.coremedia.uitesting.cms.editor.components.premular.CollapsiblePanel;
import com.coremedia.uitesting.cms.editor.components.premular.PropertyFieldGroup;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.ui.ckeditor.RichTextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceCategoryContentForm.XTYPE)
@Scope("prototype")
public class CommerceCategoryContentForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceCategoryContentForm";
  private static final String SHORT_DESCRIPTION_COLLAPSIBLE_ITEM_ID = "shortDescriptionCollapsibleItemId";

  @FindByExtJS(itemId = "name")
  private Container name;

  @FindByExtJS(itemId = SHORT_DESCRIPTION_COLLAPSIBLE_ITEM_ID)
  private CollapsiblePanel shortDescriptionCollapsiblePanel;

  @FindByExtJS(itemId = "shortDescription")
  private RichTextArea shortDescription;

  @FindByExtJS(itemId = "longDescription")
  private RichTextArea longDescription;

  @FindByExtJS(itemId = PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY)
  private PropertyFieldGroup categoryPageGridPropertyFieldGroup;

  public TextField getName() {
    return name.find(TextField.class, ExtJSBy.xtype("textfield"));
  }

  public RichTextArea getLongDescription() {
    return longDescription;
  }

  public CollapsiblePanel getShortDescriptionCollapsiblePanel() {
    return shortDescriptionCollapsiblePanel;
  }

  public RichTextArea getShortDescription() {
    return shortDescription;
  }

  public PageGridPropertyField getCategoryPageGridPropertyField() {
    return categoryPageGridPropertyFieldGroup.find(PageGridPropertyField.class, ExtJSBy.xtype(PageGridPropertyField.XTYPE));

  }
}
