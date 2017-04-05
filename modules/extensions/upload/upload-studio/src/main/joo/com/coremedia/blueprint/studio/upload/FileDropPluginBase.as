package com.coremedia.blueprint.studio.upload {

import ext.Component;
import ext.Plugin;
import ext.container.Container;
import ext.dom.Element;
import ext.util.Observable;

/**
 * Fire notification to the passed drop event handler
 * if a file has been dropped to the container this plugin has been bind to.
 */
public class FileDropPluginBase extends Observable implements Plugin {
  private var blobBoxCmp:Container;
  private var dropHandler:Function;

  /**
   * @param config
   */
  public function FileDropPluginBase(config:FileDropPlugin = null) {
    this.dropHandler = config.dropHandler;
  }


  /**
   * Adds the drop listener for the component the plugin has been added to.
   * @param cmp The component to register the listener for.
   */
  public function init(cmp:Component):void {
    this.blobBoxCmp = cmp as Container;

    cmp.mon(cmp, "afterrender", initFileDrop, this);
  }

  /**
   * Add the concrete drag and drop listener for the element of the given component.
   * The listeners have to registered after render so that the element exists.
   * @param cmp The component to register the listeners for.
   */
  private function initFileDrop(cmp:Component):void {
    var el:Element = cmp.getEl();
    el.on("dragover", onDragOver, this);
    el.on("drop", onDrop, this);
  }

  /**
   * Event handler for the drag over events.
   * @param e The drag event object.
   */
  private function onDragOver(e:*):void {
    if (e.browserEvent && e.browserEvent.dataTransfer) {
      e.browserEvent.dataTransfer.dropEffect = 'copy';
    }
    e.stopEvent();
    blobBoxCmp.fireEvent("dragover", blobBoxCmp, e);
  }

  /**
   * The event handler for the drop event.
   * @param e The drop event.
   */
  private function onDrop(e:*):void {
    e.stopEvent(); //prevent additional events.

    var cmp:Component = this.blobBoxCmp,
      browserEvent:* = e.browserEvent,
      dataTransfer:* = browserEvent.dataTransfer,
      files:Array = dataTransfer.files,
      numFiles:Number = files.length,
      i:Number = 0,
      file:*;

    cmp.fireEvent("drop", cmp, e);

    //create a file wrapper for every dropped file object.
    var wrappers:Array = [];
    for (; i < numFiles; i++) {
      file = files[i];
      var wrapper:FileWrapper = new FileWrapper(file);
      wrappers.push(wrapper);
    }
    dropHandler.call(null, wrappers);
  }
}
}