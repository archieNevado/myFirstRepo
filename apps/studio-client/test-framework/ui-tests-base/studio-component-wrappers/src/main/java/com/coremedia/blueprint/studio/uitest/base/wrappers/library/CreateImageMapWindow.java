package com.coremedia.blueprint.studio.uitest.base.wrappers.library;

import com.coremedia.blueprint.studio.uitest.base.wrappers.components.FolderChooserListView;
import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

@ExtJSObject(id = "imageMapWindow")
@Scope("prototype")
public class CreateImageMapWindow extends Window {

    public static final String XTYPE = "com.coremedia.blueprint.base.components.config.createImageMapWindow";

    @FindByExtJS(itemId = "okBtn")
    private Button okButton;

    @FindByExtJS(itemId = "cancelBtn")
    private Button cancelButton;


    @FindByExtJS(itemId = "documentName")
    private TextField imageMapNameField;


    @FindByExtJS(xtype = "com.coremedia.cms.editor.sdk.folderchooser.FolderChooserListView")
    FolderChooserListView folderChooserListView;

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public TextField getImageMapNameField() {
        return imageMapNameField;
    }

    public FolderChooserListView getFolderChooserListView() {
        return folderChooserListView;
    }
}
