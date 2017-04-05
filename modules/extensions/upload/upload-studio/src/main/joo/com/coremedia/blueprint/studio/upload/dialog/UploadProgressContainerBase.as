package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.blueprint.studio.upload.XliffBulkOperationResult;
import com.coremedia.blueprint.studio.upload.XliffBulkOperationResultItem;
import com.coremedia.blueprint.studio.upload.XliffImportResultCodes;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.components.StatefulProgressBar;
import com.coremedia.ui.components.StatefulQuickTip;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;
import com.coremedia.ui.mixins.ValidationState;
import com.coremedia.ui.util.EventUtil;

import ext.ComponentManager;
import ext.JSON;
import ext.StringUtil;
import ext.container.Container;
import ext.form.field.DisplayField;
import ext.tip.QuickTipManager;

/**
 * Base class for the upload progress container, implements
 * the actual upload and updates the status of the panel.
 */
[ResourceBundle('com.coremedia.blueprint.studio.UploadStudioPlugin')]
public class UploadProgressContainerBase extends Container {
  private const ALWAYS_CHECKIN:Boolean = true;

  /**
   * The file wrapper model for this panel.
   */
  [Bindable]
  public var file:FileWrapper;

  /**
   * The folder to upload the file to.
   */
  [Bindable]
  public var folder:Content;

  /**
   * The function to call when the upload is finished.
   */
  [Bindable]
  public var callback:Function;

  /**
   * The settings used for this dialog.
   */
  [Bindable]
  public var settings:UploadSettings;

  /**
   * A set of XliffImportResultCodes that should be ignored when uploading an XLIFF file
   */
  [Bindable]
  public var ignoredXliffResultCodes:Array = [XliffImportResultCodes.EMPTY_TRANSUNIT_TARGET_FOR_WHITESPACE_SOURCE];

  private var progressBar:StatefulProgressBar;
  private var filenameLabel:DisplayField;

  public function UploadProgressContainerBase(config:UploadProgressContainerBase = null) {
    super(config);
  }

  override protected function initComponent():void {
    super.initComponent();
    progressBar = queryById(UploadProgressContainer.PROGRESS_BAR) as StatefulProgressBar;
    filenameLabel = queryById(UploadProgressContainer.PROGRESS_STATUS_TEXT) as DisplayField;
    progressBar.updateText(resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_waiting'));
  }


  override protected function afterRender():void {
    super.afterRender();
    QuickTipManager.register(StatefulQuickTip({
      target: filenameLabel.getId(),
      text: file.getName(),
      validationState: ValidationState.INFO,
      trackMouse: false,
      autoHide: true,
      dismissDelay: 3000
    }));
  }

  /**
   * Triggers the upload with the given data.
   */
  public function startUpload():void {
    //update the visible status
    if (file.getStatus() !== FileWrapper.STATUS_ERROR) {
      file.setStatus(FileWrapper.STATUS_UPLOADING);
      progressBar.updateProgress(0, resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_running'));
    }

    //start data transfer
    if (file.isXliff()) {
      file.uploadXliff(settings, xliffUploaded, uploadError, uploadProgress);
    } else {
      file.upload(settings, folder, fileUploadedAndContentCreated, uploadError, uploadProgress);
    }
  }

  private function uploadProgress(percentage:int):void {
    var progress:Number = percentage/100;
    if(percentage < 100) {
      progressBar.updateProgress(progress, resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_running'));
    }
    else {
      progressBar.updateProgress(progress, resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_processing'));
    }
  }

  private function xliffUploaded(xliffBulkOperationResult:XliffBulkOperationResult):void {
    var translatedContents:Array = [];
    var results:Array = xliffBulkOperationResult.results;

    for (var i:int = 0; i < results.length; i++) {
      var resultItem:XliffBulkOperationResultItem = results[i];
      if (translatedContents.indexOf(resultItem.content) === -1) {
        translatedContents.push(resultItem.content);
      }
    }

    var filteredResults:Array = results.filter(function (resultItem:XliffBulkOperationResultItem):Boolean {
      return resultItem.resultCode !== XliffImportResultCodes.SUCCESS &&
              ignoredXliffResultCodes.indexOf(resultItem.resultCode) === -1;
    });

    if (filteredResults.length > 0) {
      var xliffImportResultWindowCfg:XliffImportResultWindow = XliffImportResultWindow({});
      xliffImportResultWindowCfg.bulkResultItems = filteredResults;
      xliffImportResultWindowCfg.successful = xliffBulkOperationResult.successful;
      ComponentManager.create(xliffImportResultWindowCfg).show();
    }

    finalizeSuccessfulUpload(translatedContents);
  }

  /**
   * Success handler executed after the file has been uploaded.
   * @param response the response from the server.
   */
  private function fileUploadedAndContentCreated(response:Object):void {
    var content:Content = BeanFactoryImpl.resolveBeans(JSON.decode(response.responseText)) as Content;
    content.load(function (postProcessedContent:Content):void {
      var initializer:Function = editorContext.lookupContentInitializer(postProcessedContent.getType());
      if (initializer) {
        initializer(postProcessedContent);
      }

      finalizeSuccessfulUpload([postProcessedContent]);
    });
  }

  private function finalizeSuccessfulUpload(postProcessedContents:Array):void {
    ValueExpressionFactory.createFromFunction(function():Boolean {
      if (ALWAYS_CHECKIN) {
        for each(var content:Content in postProcessedContents) {
          if(content.isCheckedOutByCurrentSession()) {
            content.checkIn();
            return undefined;
          }
        }
      }

      if (settings.getOpenInTab()) {
        editorContext.getContentTabManager().openDocuments(postProcessedContents);
      }

      return true;
    }).loadValue(function():void {
      progressBarUpdateSuccess();
      file.setStatus(FileWrapper.STATUS_UPLOADED);
      callback.call(null);
    });
  }

  private function progressBarUpdateSuccess():void {
    progressBar.reset();
    progressBar.updateProgress(1, resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_successful'), true);
    progressBar.validationState = ValidationState.SUCCESS;
  }

  /**
   * Invoked when the import document created failed or when the upload
   * itself failed.
   * @param result The remote service method result.
   */
  private function uploadError(result:String):void {
    file.setStatus(FileWrapper.STATUS_ERROR);
    EventUtil.invokeLater(function ():void {
      QuickTipManager.register(StatefulQuickTip({
        target: progressBar.getId(),
        text: StringUtil.format(resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_error_tooltip'), result),
        validationState: ValidationState.ERROR,
        trackMouse: false,
        autoHide: true,
        dismissDelay: 3000
      }));

      progressBar.validationState = ValidationState.ERROR;
      progressBar.reset();
      progressBar.setValue(100);
      progressBar.updateProgress(1, resourceManager.getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'UploadProgressDialog_upload_failed'), false);
      callback.call(null);
    });
  }

}
}
