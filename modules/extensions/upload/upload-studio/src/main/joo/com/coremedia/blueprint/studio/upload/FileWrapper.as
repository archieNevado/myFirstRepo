package com.coremedia.blueprint.studio.upload {

import com.coremedia.blueprint.base.components.util.StringHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.results.BulkOperationResult;
import com.coremedia.cms.editor.sdk.components.html5.Uploader;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.components.IconDisplayField;
import com.coremedia.ui.components.Image;
import com.coremedia.ui.data.impl.BeanImpl;
import com.coremedia.ui.data.impl.RemoteService;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponseImpl;
import com.coremedia.ui.logging.Logger;
import com.coremedia.ui.mixins.OverflowBehaviour;
import com.coremedia.ui.mixins.TextAlign;
import com.coremedia.ui.skins.IconDisplayFieldSkin;

import ext.container.Container;
import ext.dom.Element;
import ext.layout.container.BoxLayout;

import js.XMLHttpRequest;

import mx.resources.ResourceManager;

/**
 * Access wrapper for a HTML 5 file object.
 */
[ResourceBundle('com.coremedia.icons.CoreIcons')]
[ResourceBundle('com.coremedia.blueprint.studio.UploadStudioPlugin')]
public class FileWrapper extends BeanImpl {
  private static const DEFAULT_UPLOAD_SIZE:int = 67108864;

  public static const FILE_PROPERTY:String = 'file';
  public static const NAME_PROPERTY:String = 'name';
  public static const MIME_TYPE_PROPERTY:String = 'mimeType';
  public static const FILE_TYPE_PROPERTY:String = "extensionType";
  public static const SIZE_PROPERTY:String = 'size';
  public static const UPLOAD_FOLDER_PROPERTY:String = 'uploadFolder';

  private static const XLIFF_MIME_TYPE:String = "application/x-xliff";
  private static const XLIFF_MIME_TYPE_EXT:String = XLIFF_MIME_TYPE + '+xml';

  public static const STATUS_ERROR:int = -1;
  public static const STATUS_WAITING:int = 0;
  public static const STATUS_UPLOADING:int = 1;
  public static const STATUS_UPLOADED:int = 2;

  //The HTML file object
  private var file:*;

  private var status:int = STATUS_WAITING;

  public function FileWrapper(file:*) {
    this.file = file;

    var fileName:String = file.name;
    var name:String = fileName;

    if (name.indexOf(".") !== -1) {
      name = StringHelper.trim(name.substring(0, name.lastIndexOf(".")), ' ');
    }

    set(NAME_PROPERTY, name);
    set(FILE_PROPERTY, file);

    var mimeType:String = file.type;
    if (!mimeType) {
      mimeType = 'text/plain';
    }
    if (fileName.search(/\.(xliff|xlf)$/) !== -1) {
      mimeType = XLIFF_MIME_TYPE;
    }

    var extensionType:String = "";
    if (fileName.indexOf(".") !== -1) {
      extensionType = fileName.substr(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    set(FILE_TYPE_PROPERTY, extensionType);
    set(MIME_TYPE_PROPERTY, mimeType);
    set(SIZE_PROPERTY, file.size);
  }

  public function getName():String {
    return get(NAME_PROPERTY);
  }

  public function getSize():int {
    return get(SIZE_PROPERTY);
  }

  public function getMimeType():String {
    return get(MIME_TYPE_PROPERTY);
  }

  public function getFile():* {
    return get(FILE_PROPERTY);
  }

  public function isXliff():Boolean {
    return getMimeType() === XLIFF_MIME_TYPE || getMimeType() === XLIFF_MIME_TYPE_EXT;
  }

  public function getStatus():int {
    return status;
  }

  public function setStatus(value:int):void {
    status = value;
  }

  public function uploadXliff(settings:UploadSettings, success:Function, error:Function, progress:Function):void {
    var fileSelector:Object = {};
    fileSelector.getInputFile = function ():* {
    };
    fileSelector.detachInputFile = function ():* {
    };
    fileSelector.getFileCls = function ():String {
      return settings.getDefaultContentType();
    };
    fileSelector.getFileName = function ():String {
      return "";
    };

    var url:String = RemoteService.calculateRequestURI('translate/importXliff');
    var upldr:Uploader = new Uploader(Uploader({
      fileSelector: fileSelector,
      timeout: settings.getTimeout(),
      url: url,
      method: 'POST'
    }));

    upldr.addListener('uploadcomplete', function (_uploader:Uploader, response:XMLHttpRequest):void {
      var remoteServiceMethodResponse:RemoteServiceMethodResponse = new RemoteServiceMethodResponseImpl(url, true, response, {});
      var bulkOperationResult:BulkOperationResult = new XliffBulkOperationResultBuilder().convert(remoteServiceMethodResponse);
      success(bulkOperationResult);
    });

    upldr.addListener('uploadfailure', function (_uploader:Uploader, response:XMLHttpRequest):void {
      error(response);
    });

    upldr.addListener('uploadprogress', function (_uploader:Uploader, e:*):void {
      var percent:Number = Math.round(e.loaded / e.total * 100);
      progress(percent);
      Logger.debug('Upload progress: ' + percent);
    });

    upldr.upload(file);
  }

  /**
   * Starts the actual data transfer. For the file of this file wrapper a Studio Uploader instance is
   * created with the corresponding event listeners. The given result callbacks are call with necessary parameters
   * to show the result of the upload.
   * @param settings The settings that define the max upload size ...
   * @param folder The folder under which to upload the file.
   * @param success The success handler called after a successful upload.
   * @param error The error handler called if the upload failed.
   * @param progress Callback for upload progress listener
   */
  public function upload(settings:UploadSettings, folder:Content, success:Function, error:Function, progress:Function):void {
    var headerParams:Object;

    if (editorContext.getSitesService().getPreferredSite()) {
      headerParams = {
        site: editorContext.getSitesService().getPreferredSiteId(),
        folderUri: folder.getUriPath()
      };
    } else {
      headerParams = {
        folderUri: folder.getUriPath()
      };
    }

    var uploaderConfig:Uploader = Uploader({});
    uploaderConfig.maxFileSize = DEFAULT_UPLOAD_SIZE;
    uploaderConfig.timeout = settings.getTimeout();
    uploaderConfig.url = RemoteService.calculateRequestURI('upload/create');
    uploaderConfig.method = 'POST';
    uploaderConfig.headerParams = headerParams;
    uploaderConfig.params = {contentName: getName()};

    var upldr:Uploader = new Uploader(uploaderConfig);

    upldr.addListener('uploadcomplete', function (_uploader:Uploader, response:XMLHttpRequest):void {
      //Hack for html4 upload.
      if (response.status === 201) {
        success.call(null, response);
      }
      else {
        error.call(null, response.statusText + ' (code ' + response.status + ')');
      }
    });

    upldr.addListener('uploadfailure', function (_uploader:Uploader, response:XMLHttpRequest):void {
      error.call(null, response.statusText);
    });

    upldr.addListener('uploadprogress', function (_uploader:Uploader, e:*):void {
      if (e.lengthComputable) {
        var percent:Number = Math.round(e.loaded / e.total * 100);
        progress(percent);
      } else {
        Logger.debug('Unable to compute progress information of Upload since the total size is unknown.');
      }
    });

    upldr.upload(file);
  }

  /**
   * Appends an image tag that contains the preview image.
   * @param preview The element to add the image element for.
   * @param width The max-width of the previewed image.
   * @param height The max-height of the previewed image.
   * @param callback The callback to call after the image preview has been set up.
   */
  public function appendPreviewElement(preview:Container, width:int, height:int, callback:Function):void {
    var reader:* = new window.FileReader();
    reader.onload = (function ():Function {
      return function (e:*):void {
        var previewable:Boolean = appendPreviewImage(preview, e, width, height, callback);
        if (!previewable) {
          var text:IconDisplayField = IconDisplayField({});
          text.value = ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.UploadStudioPlugin', 'Upload_mimetype_text');
          text.ui = IconDisplayFieldSkin.ITALIC.getSkin();
          text.maxWidth = width - 10;
          text.textAlign = TextAlign.CENTER;
          text.overflowBehaviour = OverflowBehaviour.BREAK_WORD;
          text.scale = "medium";
          preview.add(text);
          callback.call(null);
        }
      };
    })();

    reader.readAsDataURL(getFile());
  }

  /**
   * Uses the browsers file API to create an image preview
   * if the given file type is supported
   * @param preview the container to add the preview to
   * @param e the drop event
   * @param width the max-width of the image preview
   * @param height the max-height of the image preview
   * @param callback the callback to call when finished
   * @return true if the preview image was generated
   */
  private function appendPreviewImage(preview:Container, e:*, width:Number, height:Number, callback:Function):Boolean {
    if (!e.target || !e.target.result || e.target.result.indexOf('data:image') === -1) {
      return false;
    }

    var imgPath:String = getFile()['name'];
    if(imgPath.lastIndexOf('.') === -1) {
      return false;
    }

    var extn:String = imgPath.substring(imgPath.lastIndexOf('.') + 1).toLowerCase();
    if (extn !== "gif" && extn !== "png" && extn !== "jpg" && extn !== "jpeg") {
      return false;
    }

    try {
      var image:Image = new Image({});
      image.setSrc(e.target.result);
      image.setStyle("max-height:" + height + "px; max-width:" + width + "px;");
      preview.add(image);
      var imgEl:Element = image.getEl();
      var layout:BoxLayout = BoxLayout(preview.getLayout());
      if (imgEl.getWidth() < imgEl.getHeight()) {
        layout.setVertical(false);
      }
      else {
        layout.setVertical(true);
      }
      // setVertical does not trigger layout update. so let's do it here
      preview.updateLayout();
      callback.call(null);
      return true;
    }
    catch (error:*) {
      Logger.error('Failed to create preview: ' + error);
    }
  }

}
}
