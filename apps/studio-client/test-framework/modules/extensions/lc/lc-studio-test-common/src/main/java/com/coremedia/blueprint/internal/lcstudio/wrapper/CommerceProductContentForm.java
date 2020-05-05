package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.blueprint.studio.uitest.base.wrappers.pagegrid.PageGridPropertyField;
import com.coremedia.uitesting.cms.editor.components.premular.PropertyFieldGroup;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import com.coremedia.uitesting.ui.ckeditor.RichTextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceProductContentForm.XTYPE)
@Scope("prototype")
public class CommerceProductContentForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceProductContentForm";

  @FindByExtJS(itemId = "name")
  private Container name;

  @FindByExtJS(itemId = "longDescription")
  private RichTextArea longDescription;

  @FindByExtJS(itemId = "offerPrice")
  private StringDisplayField offerPrice;

  @FindByExtJS(itemId = "listPrice")
  private StringDisplayField listPrice;

  @FindByExtJS(itemId = "richMedia")
  private PropertyFieldGroup richMediaPanel;

  @FindByExtJS(itemId = "pdpPagegrid")
  private PropertyFieldGroup productPageGridPropertyFieldGroup;

  public TextField getName() {
    return name.find(TextField.class, ExtJSBy.xtype("textfield"));
  }

  public RichTextArea getLongDescription() {
    return longDescription;
  }

  public StringDisplayField getOfferPrice() {
    return offerPrice;
  }

  public StringDisplayField getListPrice() {
    return listPrice;
  }

  public GridPanel getRichMediaGrid() {
    return richMediaPanel.find(GridPanel.class, ExtJSBy.itemId("catalogAssets"));
  }

  public PageGridPropertyField getProductPageGridPropertyField() {
    return productPageGridPropertyFieldGroup.find(PageGridPropertyField.class, ExtJSBy.xtype(PageGridPropertyField.XTYPE));
  }
}
