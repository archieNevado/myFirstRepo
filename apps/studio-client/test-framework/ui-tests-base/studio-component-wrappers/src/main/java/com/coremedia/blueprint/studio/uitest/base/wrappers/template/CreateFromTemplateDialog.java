package com.coremedia.blueprint.studio.uitest.base.wrappers.template;

import com.coremedia.blueprint.studio.uitest.base.wrappers.components.FolderChooserListView;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

/**
 * @since 6/8/12
 */
@ExtJSObject
@Scope("prototype")
public class CreateFromTemplateDialog extends Window {
  public static final String XTYPE = "com.coremedia.blueprint.studio.template.config.createFromTemplateDialog";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "nameField", global = false)
  private TextField createContentNameField;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "baseFolderChooser", global = false)
  private FolderChooserListView baseFolderChooser;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "contentBaseFolderChooser", global = false)
  private FolderChooserListView contentbaseFolderChooser;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createBtn", global = false)
  private Button createButton;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "templateChooserField", global = false)
  private TemplateChooserWrapper templateChooserWrapper;

  public Button getCreateButton() {
    return createButton;
  }

  public TextField getCreateContentNameField() {
    return createContentNameField;
  }

  public FolderChooserListView getFolderChooserListView() {
    return baseFolderChooser;
  }

  public FolderChooserListView getEditorialFolderChooserListView() {
    return contentbaseFolderChooser;
  }

  public TemplateChooserWrapper getTemplateChooser() {
    return templateChooserWrapper;
  }
}
