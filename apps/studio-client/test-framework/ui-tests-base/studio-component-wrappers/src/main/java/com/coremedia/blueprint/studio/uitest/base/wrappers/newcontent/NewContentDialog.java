package com.coremedia.blueprint.studio.uitest.base.wrappers.newcontent;

import com.coremedia.blueprint.studio.uitest.base.wrappers.components.FolderChooserListView;
import com.coremedia.blueprint.studio.uitest.base.wrappers.components.FolderPrompt;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

/**
 * @since 6/8/12
 */
@ExtJSObject(xtype = "com.coremedia.cms.editor.sdk.config.quickCreateDialog")
@Scope("prototype")
public class NewContentDialog extends Window {
  public static final String XTYPE = "com.coremedia.cms.editor.sdk.config.quickCreateDialog";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createContentNameField", global = false)
  private TextField createContentNameField;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "folderChooserListView", global = false)
  private FolderChooserListView folderChooserListView;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "createBtn", global = false)
  private Button createButton;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cancelBtn", global = false)
  private Button cancelButton;
  @Inject
  private IdleIndicators idleIndicators;
  @Inject
  private FolderPrompt folderPrompt;

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getCreateButton() {
    return createButton;
  }

  public TextField getCreateContentNameField() {
    return createContentNameField;
  }

  public FolderChooserListView getFolderChooserListView() {
    return folderChooserListView;
  }

  public FolderPrompt getFolderPrompt() {
    return folderPrompt;
  }


  /**
   * Fills in the new content dialog and submits the creation request.
   *
   * @param name   name of the document to create
   * @param folder folder to create the content in; {@code null} to use default given in UI
   */
  public void createContent(final String name, final Content folder) {
    idleIndicators.idle().waitUntilTrue();
    createContentNameField.focused().waitUntilTrue(); //ensures that the name field receives focus before we start
                                                      //sending keys (sendKeys) either to the name text field or
                                                      // folder combo; a late focus event can interrupt sendKeys
    if (name != null) {
      createContentNameField.clear();
      createContentNameField.writeString(name);
    }
    if (folder != null) {
      folderChooserListView.selectFolder(folder);   //implementation uses sendKeys() method
    }
    createButton.enabled().waitUntilTrue();
    createButton.click();
  }

  public NewContentDialogActionPerformer action() {
    return new NewContentDialogActionPerformer(this);
  }

  public static class NewContentDialogActionPerformer {
    private final NewContentDialog dialog;
    private String name;
    private Content folder;
    private boolean expectCreateFolderMessage;
    private boolean acknowledgeCreateFolderMessage;
    private final List<Runnable> before = newArrayListWithExpectedSize(1);

    private NewContentDialogActionPerformer(final NewContentDialog dialog) {
      this.dialog = dialog;
    }

    public NewContentDialogActionPerformer withName(final String name) {
      this.name = name;
      return this;
    }

    public NewContentDialogActionPerformer inFolder(final Content folder) {
      this.folder = folder;
      return this;
    }

    public NewContentDialogActionPerformer expectingCreateFolderMessage() {
      expectCreateFolderMessage = true;
      return this;
    }

    public NewContentDialogActionPerformer acknowledgingCreateFolderMessage() {
      expectingCreateFolderMessage();
      acknowledgeCreateFolderMessage = true;
      return this;
    }

    public NewContentDialogActionPerformer before(final Runnable... runnable) {
      before.addAll(Arrays.asList(runnable));
      return this;
    }

    public void execute() {
      for (final Runnable runnable : before) {
        runnable.run();
      }
      dialog.visible().waitUntilTrue();
      dialog.createContent(name, folder);
      if (expectCreateFolderMessage) {
        final FolderPrompt prompt = dialog.getFolderPrompt();
        prompt.visible().waitUntilTrue();
        if (acknowledgeCreateFolderMessage) {
          prompt.getOkButton().click();
        }
      }
    }
  }
}
