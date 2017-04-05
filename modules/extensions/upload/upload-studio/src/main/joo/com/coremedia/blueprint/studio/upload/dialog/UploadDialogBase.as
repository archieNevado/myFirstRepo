package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.base.components.util.ContentCreationUtil;
import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.components.folderprompt.FolderCreationResultImpl;
import com.coremedia.cms.editor.sdk.components.html5.BrowsePlugin;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.cms.editor.sdk.util.PathFormatter;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.MessageBox;
import ext.StringUtil;
import ext.container.Container;
import ext.form.field.Checkbox;
import ext.form.field.ComboBox;
import ext.window.Window;

/**
 * Base class of the upload dialog, contains the current
 * items marked for uploading
 */
[ResourceBundle('com.coremedia.blueprint.studio.UploadStudioPlugin')]
public class UploadDialogBase extends Window {
  protected static const UPLOAD_AREA_HEIGHT:int = 30;
  protected static const UPLOAD_AREA_COLLAPSED_HEIGHT:int = 25;
  protected static const UPLOAD_WINDOW_HEIGHT:int = 453;
  protected static const UPLOAD_WINDOW_WIDTH:int = 430;

  private static const DROP_ZONE_COLLAPSED_CSS:String = 'dialog-upload-helptext-collapsed';

  /**
   * The selected content if dialog opened from library or null
   */
  [Bindable]
  public var content:Content;

  /**
   * The selected content if dialog opened from library or null
   */
  [Bindable]
  public var file:FileWrapper;

  private var fileContainers:FileContainersObservable;
  private var dropAreaCollapsed:Boolean = false;
  private var pathCombo:ComboBox;

  /**
   * The settings used for this dialog.
   */
  [Bindable]
  public var settings:UploadSettings;

  public function UploadDialogBase(config:UploadDialogBase = null) {
    super(config);
  }

  /**
   * Some dialog initializations after setup...
   */
  override protected function afterRender():void {
    super.afterRender();

    var openInTabCheckbox:Checkbox = Ext.getCmp(UploadDialog.OPEN_IN_TAB_CHECKBOX) as Checkbox;
    openInTabCheckbox.setValue(settings.getOpenInTab());

    pathCombo = Ext.getCmp(UploadDialog.FOLDER_COMBOBOX) as ComboBox;
  }

  protected function getFolders():Array {
    var baseFolder:String = baseFolderCalculation();
    if (baseFolder) {
      return [baseFolder];
    }
    return [];
  }

  private function baseFolderCalculation():String {
    var path:String;
    if (content) {
      path = content.getPath();
    }
    else {
      path = PathFormatter.formatSitePath(settings.getDefaultUploadPath());
    }
    return path;
  }

  /**
   * Removes the given file container from the list of uploading files.
   * @param fileContainer
   */
  public function removeFileContainer(fileContainer:FileContainer):void {
    fileContainers.remove(fileContainer);
    //expand drop zone again?
    if (fileContainers.isEmpty()) {
      toggleDropZoneStatus();
    }
  }

  /**
   * The upload button handler, converts the selected files to FileWrapper objects.
   * @param browsePlugin the browse plugin used for the file selection and contains the file selection.
   */
  protected function uploadButtonHandler(browsePlugin:BrowsePlugin):void {
    var fileWrappers:Array = [];
    var fileList:* = browsePlugin.getFileList();
    for (var i:int = 0; i < fileList.length; i++) {
      fileWrappers.push(new FileWrapper(fileList.item(i)));
    }
    handleDrop(fileWrappers);
  }

  /**
   * Fired when a file object has been dropped on the target drop area.
   * The file drop plugin fire an event for each file that is dropped
   * and the corresponding action is handled here.
   */
  protected function handleDrop(files:Array):void {
    MessageBox.show({
      title: resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_progress_title'),
      msg: resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_progress_msg'),
      closable: false,
      width: 300
    });
    EventUtil.invokeLater(function ():void {//otherwise the progress bar does not appear :(
      for (var i:int = 0; i < files.length; i++) {
        var fc:FileContainer = FileContainer({});
        fc.file = files[i];
        fc.uploadSettings = settings;
        fc.removeFileHandler = removeFileContainer;
        var fileContainer:FileContainer = new FileContainer(fc);
        fileContainers.add(fileContainer);
      }
      MessageBox.hide();
      refreshUploadList();
    });
  }

  /**
   * Returns the value expression that enables/disables the upload button.
   * the status of the buttons depends on if all file panels on this dialog are valid.
   * @return
   */
  protected function getUploadButtonDisabledExpression():ValueExpression {
    if (!fileContainers) {
      fileContainers = new FileContainersObservable();
      fileContainers.getValidityExpression().setValue(true);
    }
    return fileContainers.getValidityExpression();
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Stores the openInTab option into the settings bean
   * @param checkbox
   * @param checked
   */
  protected function openInTabHandler(checkbox:Checkbox, checked:Boolean):void {
    settings.setOpenInTab(checked);
  }

  /**
   * Rebuilds all panels representing a future upload.
   */
  private function refreshUploadList():void {
    //collapse the drop area if there are upload containers
    if (!fileContainers.isEmpty() && !dropAreaCollapsed) {
      toggleDropZoneStatus();
    }

    //clear and add list of upload containers
    var list:Container = Ext.getCmp(UploadDialog.UPLOAD_LIST) as Container;
    var fileContainer:FileContainer = null;
    for (var i:int = 0; i < fileContainers.size(); i++) {
      fileContainer = fileContainers.getAt(i);
      list.add(fileContainer);
    }
  }

  /**
   * Expands or collapses the drop zone status.
   */
  private function toggleDropZoneStatus():void {
    var dropArea:Container = Ext.getCmp(UploadDialog.DROP_BOX) as Container;
    if (!dropAreaCollapsed) {
      dropAreaCollapsed = true;
      dropArea.setHeight(UPLOAD_AREA_COLLAPSED_HEIGHT);
      Ext.getCmp(UploadDialog.DROP_LABEL).addCls(DROP_ZONE_COLLAPSED_CSS);
    }
    else {
      dropAreaCollapsed = false;
      dropArea.setHeight(UPLOAD_AREA_HEIGHT);
      Ext.getCmp(UploadDialog.DROP_LABEL).removeCls(DROP_ZONE_COLLAPSED_CSS);
    }
  }

  /**
   * Opens the progress upload and passes all the file wrapper and the upload dir to it.
   */
  protected function okPressed():void {
    var uploadDirectory:String = pathCombo.getValue();
    if(uploadDirectory) {
      ContentCreationUtil.updateLastUsedBean(null, settings.getDefaultContentType(), uploadDirectory);
    }

    var needsUpload:Boolean = false;
    var files:Array = fileContainers.getFiles();
    for (var i:int = 0; i < files.length; i++) {
      var fileWrapper:FileWrapper = files[i];
      if (!fileWrapper.isXliff()) {
        needsUpload = true;
      }
    }

    if (!needsUpload) {
      var progressDialog:UploadProgressDialog = new UploadProgressDialog(UploadProgressDialog({
        files: fileContainers.getFiles(),
        settings: settings,
        folder: null
      }));
      progressDialog.show();
      close();
    } else {
      if (uploadDirectory) {
        SESSION.getConnection().getContentRepository().getChild(uploadDirectory, function (folder:Content):void {
          if (folder) { //ensure loading to display path information
            folder.load(function ():void {
              var progressDialog:UploadProgressDialog = new UploadProgressDialog(UploadProgressDialog({
                files: fileContainers.getFiles(),
                settings: settings,
                folder: folder
              }));
              progressDialog.show();
              close();
            });
          }
          else {
            ContentCreationUtil.createRequiredSubfolders(uploadDirectory,
                    function (result:FolderCreationResultImpl):void {
                      if (result.success) {
                        okPressed();
                      }
                      else if (result.remoteError) {
                        var msg:String = StringUtil.format(resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_folder_error'),
                                uploadDirectory,
                                result.remoteError.errorName);
                        MessageBoxUtil.showError(resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_error'), msg);
                      }
                    }, true);
          }
        });
      }
    }
  }

}
}
