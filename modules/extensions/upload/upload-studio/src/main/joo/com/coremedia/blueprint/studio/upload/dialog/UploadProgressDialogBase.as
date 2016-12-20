package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.util.EventUtil;

import ext.container.Container;
import ext.window.Window;

/**
 * Base class of the upload dialog, contains the
 * items marked for uploading.
 */
[ResourceBundle('com.coremedia.blueprint.studio.UploadStudioPlugin')]
public class UploadProgressDialogBase extends Window {

  /**
   * The file array of file wrapper instances.
   */
  [Bindable]
  public var files:Array;

  /**
   * The directory the files will be uploaded too.
   */
  [Bindable]
  public var folder:Content;

  private var uploadContainers:Array = [];
  private var activeUploadIndex:int = 0;

  /**
   * The settings used for this dialog.
   */
  [Bindable]
  public var settings:UploadSettings;

  public function UploadProgressDialogBase(config:UploadProgressDialogBase = null) {
    super(config);
    addListener('render', addUploads);
  }

  /**
   * Add the file panels after render
   */
  private function addUploads():void {
    removeListener('render', addUploads);
    addUploadItems();
    EventUtil.invokeLater(function ():void {
      uploadActiveItem();
    });
  }

  /**
   * Creates the upload status panels and adds
   * them to the dialog.
   */
  private function addUploadItems():void {
    var uploadContainer:Container = queryById(UploadProgressDialog.PROGRESS_LIST) as Container;
    for (var i:int = 0; i < files.length; i++) {
      var progressContainer:UploadProgressContainer = new UploadProgressContainer(UploadProgressContainer({
        file: files[i],
        folder: folder,
        settings: settings,
        callback: uploadActiveItem
      }));
      uploadContainer.add(progressContainer);
      uploadContainers.push(progressContainer);
    }
  }

  /**
   * Starts the upload for the current active item.
   * Once the callback is called, the next upload is triggered
   * until all uploads have finished and the dialog is closed.
   */
  public function uploadActiveItem():void {
    if (uploadContainers.length > activeUploadIndex) {
      var uploadContainer:UploadProgressContainer = uploadContainers[activeUploadIndex];
      uploadContainer.startUpload();
      activeUploadIndex++;
    }
    else {
      //all files processed, check error state afterwards
      var close:Boolean = true;
      for (var i:int = 0; i < files.length; i++) {
        if (files[i].getStatus() === FileWrapper.STATUS_ERROR) {
          close = false;
          setTitle(resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_failed'));
          break;
        }
      }
      if (close) {
        this.close();
      }
    }
  }

  override public function close():void {
    var closeable:Boolean = true;
    for (var i:int = 0; i < files.length; i++) {
      var file:FileWrapper = files[i];
      if (file.getStatus() === FileWrapper.STATUS_WAITING || file.getStatus() === FileWrapper.STATUS_UPLOADING) {
        closeable = false;
        break;
      }
    }
    if (closeable) {
      super.close();
    }
  }

}
}
