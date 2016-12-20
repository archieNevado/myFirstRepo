package com.coremedia.blueprint.studio.upload.dialog {
import com.coremedia.blueprint.studio.upload.FileWrapper;

import ext.container.Container;

/**
 * The file container wraps the preview information for each
 * upload, including the name and mime type of the uploading document.
 */
public class FileContainerBase extends Container {

  protected static const PREVIEW_WIDTH:int = 90;
  protected static const PREVIEW_HEIGHT:int = 70;

  private var fileWrapper:FileWrapper;

  public function FileContainerBase(config:FileContainer = null) {
    super(config);
    this.fileWrapper = config.file;
    addListener('afterlayout', layoutPanel);
  }

  public native function get removeFileHandler():Function;

  /**
   * Returns the file wrapper used for this panel.
   * Getter is used for the global validation.
   * @return
   */
  public function getFile():FileWrapper {
    return fileWrapper;
  }

  /**
   * Layout preview after render
   */
  private function layoutPanel():void {
    removeListener('afterlayout', layoutPanel);
    var previewContainer:Container = queryById('preview') as Container;
    previewContainer.setDisabled(true);
    fileWrapper.appendPreviewElement(previewContainer, PREVIEW_WIDTH, PREVIEW_HEIGHT, function ():void {
      previewContainer.setDisabled(!isVisible(true));
    });
  }

  protected function callRemoveHandler():void {
    removeFileHandler(this);
  }
}
}
