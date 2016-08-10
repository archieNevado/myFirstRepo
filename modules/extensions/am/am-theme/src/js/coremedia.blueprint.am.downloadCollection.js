/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

/**
 *  CoreMedia Blueprint Asset Management Namespace
 */
coremedia.blueprint.am = (function (module) {
  return module;
}(coremedia.blueprint.am || {}));

/**
 *  CoreMedia Blueprint Asset Management Download Collection Namespace
 *  including following functions for
 *
 *  - Download Collection
 */
coremedia.blueprint.am.downloadCollection = (function (module) {

  "use strict";

  var $ = coremedia.blueprint.$ || window.$;
  var $window = $(window);

  var DOWNLOAD_COLLECTION_MAX_QUANTITY = 999;

  var BLOCK_NAME_DLC = "am-download-collection";

  var ELEMENT_NAME_DLC_BUTTON = "button";
  var ELEMENT_NAME_DLC_COUNTER = "counter";

  var BLOCK_NAME_DLC_RENDITION_CONTROL = "am-download-collection-rendition-control";

  var MODIFIER_NAME_DLC_RENDITION_CONTROL_ADDABLE = "addable";
  var MODIFIER_NAME_DLC_RENDITION_CONTROL_REMOVABLE = "removable";

  module.DOWNLOAD_COLLECTION_PROPERTY = 'downloadCollection';

  module.RENDITION_SELECTION_PROPERTY = 'renditionSelection';

  var EVENT_PREFIX = "coremedia.blueprint.am.downloadCollection.";
  module.EVENT_UPDATED = EVENT_PREFIX + "updated";

  // PRIVATE

  /**
   * Get all renditions for given download collection with given asset id.
   * @param {Array} downloadCollection
   * @param {Number} assetId
   * @returns {Array}
   */
  var getRenditionsToDownloadForAsset = function (downloadCollection, assetId) {
    if (!downloadCollection[assetId]) {
      downloadCollection[assetId] = [];
    }
    return downloadCollection[assetId];
  };

  // PUBLIC

  /**
   * Checks whether the given <b>rendition</b> is set under key <b>assetId</b>.
   * @param {Number} assetId Asset Id
   * @param {String} rendition Name of rendition
   * @returns {boolean}
   */
  module.isInDownloadCollection = function (assetId, rendition) {
    var renditionsToDownloadForAsset = getRenditionsToDownloadForAsset(module.getDownloadCollection(), assetId);
    return renditionsToDownloadForAsset.indexOf(rendition) >= 0;
  };

  /**
   * Checks whether an asset with the given <b>assetId</b> has renditions in download collection.
   * @param {Number} assetId Asset Id
   * @returns {boolean}
   */
  module.hasRenditionInDownloadCollection = function (assetId) {
    var renditionsToDownloadForAsset = getRenditionsToDownloadForAsset(module.getDownloadCollection(), assetId);
    return renditionsToDownloadForAsset && renditionsToDownloadForAsset.length > 0;
  };


  /**
   * Counts all renditions for all asset id in download collection.
   * @returns {number}
   */
  module.getDownloadCollectionCount = function () {
    var downloadCollection = module.getDownloadCollection();

    var count = 0;

    for (var assetId in downloadCollection) {
      if (downloadCollection.hasOwnProperty(assetId)) {
        count = count + downloadCollection[assetId].length;
      }
    }

    return count;
  };

  /**
   * Initializes the Asset Download Collection in {@link localStorage} under key {@link module.DOWNLOAD_COLLECTION_PROPERTY}
   */
  module.initDownloadCollection = function () {
    if (!localStorage.getItem(module.DOWNLOAD_COLLECTION_PROPERTY)) {
      localStorage.setItem(module.DOWNLOAD_COLLECTION_PROPERTY, "{}");
    }
    $window.trigger(module.EVENT_UPDATED);
  };

  /**
   * Re-initializes download collection
   */
  module.clearDownloadCollection = function () {
    localStorage.removeItem(module.DOWNLOAD_COLLECTION_PROPERTY);
    module.initDownloadCollection();
    // init already triggers UPDATED event
  };

  /**
   * Retrieves a copy of the collection of renditions selected for download.
   * The collection has asset IDs as keys and an array of the selected
   * renditions as value.
   *
   * @returns {Array} the rendition download collection
   */
  module.getDownloadCollection = function () {
    return JSON.parse(localStorage.getItem(module.DOWNLOAD_COLLECTION_PROPERTY));
  };

  module.saveDownloadCollection = function (downloadCollection) {
    var downloadCollectionString = JSON.stringify(downloadCollection);
    localStorage.setItem(module.DOWNLOAD_COLLECTION_PROPERTY, downloadCollectionString);
    $window.trigger(module.EVENT_UPDATED);
  };

  module.getRenditionSelection = function () {
    return JSON.parse(localStorage.getItem(module.RENDITION_SELECTION_PROPERTY)) || [];
  };

  module.updateDownloadCollectionCounterState = function ($counter) {
    var count = module.getDownloadCollectionCount();
    $counter.text(count);
  };

  module.saveRenditionSelection = function (renditionSelection) {
    var renditionSelectionString = JSON.stringify(renditionSelection);
    localStorage.setItem(module.RENDITION_SELECTION_PROPERTY, renditionSelectionString);
  };
  
  module.updateRenditionLinkTextState = function ($control, config) {
    if (module.isInDownloadCollection(config.assetId, config.rendition)) {
      $control.removeBEMModifier(BLOCK_NAME_DLC_RENDITION_CONTROL, MODIFIER_NAME_DLC_RENDITION_CONTROL_ADDABLE)
              .addBEMModifier(BLOCK_NAME_DLC_RENDITION_CONTROL, MODIFIER_NAME_DLC_RENDITION_CONTROL_REMOVABLE);
    } else {
      $control.removeBEMModifier(BLOCK_NAME_DLC_RENDITION_CONTROL, MODIFIER_NAME_DLC_RENDITION_CONTROL_REMOVABLE)
              .addBEMModifier(BLOCK_NAME_DLC_RENDITION_CONTROL, MODIFIER_NAME_DLC_RENDITION_CONTROL_ADDABLE);
    }
  };

  /**
   * Checks whether the Download Collection Button is disabled (no contents in local storage) or not (there is at least one rendition in local storage)
   */
  module.updateDownloadCollectionButtonState = function ($button, $counter) {
    // TODO: remove fallback
    var $collection = $("." + BLOCK_NAME_DLC);
    $button = $button || $collection.findBEMElement(BLOCK_NAME_DLC, ELEMENT_NAME_DLC_BUTTON);
    $counter = $counter || $collection.findBEMElement(BLOCK_NAME_DLC, ELEMENT_NAME_DLC_COUNTER);

    var $buttons = $button.add($counter);

    var downloadCollection = module.getDownloadCollection();
    var disabled = (!downloadCollection || downloadCollection.length == 0 || $.isEmptyObject(downloadCollection));
    $buttons.prop("disabled", disabled);
  };

  /**
   * Adds a rendition name to the download collection's associated asset id array or removes it, if found.
   *
   * @param {Number} assetId
   * @param {String} renditionName
   */
  module.addOrRemoveRenditionFromDownloadCollection = function (assetId, renditionName) {
    var downloadCollection = module.getDownloadCollection();

    var renditionsToDownloadForAsset = getRenditionsToDownloadForAsset(downloadCollection, assetId);

    var indexOfRendition = renditionsToDownloadForAsset.indexOf(renditionName);
    if (indexOfRendition === -1) {
      if (module.getDownloadCollectionCount() < DOWNLOAD_COLLECTION_MAX_QUANTITY) {
        renditionsToDownloadForAsset.push(renditionName);
      } else {
        console.error("Maximum number of items in Asset Download Collection reached", DOWNLOAD_COLLECTION_MAX_QUANTITY);
      }
    } else {
      renditionsToDownloadForAsset.splice(indexOfRendition, 1);
      if (renditionsToDownloadForAsset.length === 0) {
        delete downloadCollection[assetId];
      }
    }

    module.saveDownloadCollection(downloadCollection);
  };

  module.addRenditionToDownloadCollection = function (assetId, renditionName) {
    var downloadCollection = module.getDownloadCollection();

    var renditionsToDownloadForAsset = getRenditionsToDownloadForAsset(downloadCollection, assetId);

    var indexOfRendition = renditionsToDownloadForAsset.indexOf(renditionName);
    if (indexOfRendition === -1) {
      if (module.getDownloadCollectionCount() < DOWNLOAD_COLLECTION_MAX_QUANTITY) {
        renditionsToDownloadForAsset.push(renditionName);
      } else {
        console.error("Maximum number of items in Asset Download Collection reached", DOWNLOAD_COLLECTION_MAX_QUANTITY);
      }
    }

    module.saveDownloadCollection(downloadCollection);

  };

  module.removeRenditionFromDownloadCollection = function (assetId, renditionName) {
    var downloadCollection = module.getDownloadCollection();

    var renditionsToDownloadForAsset = getRenditionsToDownloadForAsset(downloadCollection, assetId);

    var indexOfRendition = renditionsToDownloadForAsset.indexOf(renditionName);
    if (indexOfRendition > -1) {
      renditionsToDownloadForAsset.splice(indexOfRendition, 1);
      if (renditionsToDownloadForAsset.length === 0) {
        delete downloadCollection[assetId];
      }
    }

    module.saveDownloadCollection(downloadCollection);

  };

  module.clearDefaultRenditionSelection = function() {
    module.saveRenditionSelection([]);
  };

  module.addDefaultRenditionSelection = function(rendition) {
    var renditionSelection = module.getRenditionSelection();
    if (renditionSelection.indexOf(rendition) < 0) {
      renditionSelection.push(rendition);
    }
    module.saveRenditionSelection(renditionSelection);
  };

  module.removeDefaultRenditionSelection = function(rendition) {
    var renditionSelection = module.getRenditionSelection();
    var pos = renditionSelection.indexOf(rendition);
    if (pos >= 0) {
      renditionSelection.splice(pos, 1);
    }
    module.saveRenditionSelection(renditionSelection);
  };

  module.getDefaultRenditionSelection = function(rendition) {
    var renditionSelection = module.getRenditionSelection();
    return renditionSelection.indexOf(rendition) >= 0;
  };
  return module;
}(coremedia.blueprint.am.downloadCollection || {}));
