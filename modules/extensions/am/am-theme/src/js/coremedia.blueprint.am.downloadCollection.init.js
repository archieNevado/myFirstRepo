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

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;
  var $window = $(window);

  var updateLeftBadgeIcon = function($badgeIconLeft, hasRenditions) {
    if (hasRenditions) {
      $badgeIconLeft.addClass("am-icon--rendition-added");
      $badgeIconLeft.removeClass("am-icon--picture-overlay");
    } else {
      $badgeIconLeft.addClass("am-icon--picture-overlay");
      $badgeIconLeft.removeClass("am-icon--rendition-added");
    }
  };

  coremedia.blueprint.am.downloadCollection.initDownloadCollection();

  // show overlay button
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            assetId: undefined
          },
          "am-picture-box__badge-icon-left",
          function ($badgeIconLeft, config) {
            var hasRenditions = coremedia.blueprint.am.downloadCollection.hasRenditionInDownloadCollection(config.assetId);
            updateLeftBadgeIcon($badgeIconLeft, hasRenditions);

            $badgeIconLeft.on("click", function (event) {
              event.preventDefault();
              $(this).closest('.am-asset-teaser').find('.am-overlay').show();

              var hasRenditions = coremedia.blueprint.am.downloadCollection.hasRenditionInDownloadCollection(config.assetId);
              if (!hasRenditions) {
                $(this).closest('.am-asset-teaser').find('.am-overlay__add-to-collection').show();
                $(this).closest('.am-asset-teaser').find('.am-overlay__update-collection').hide();
              } else {
                $(this).closest('.am-asset-teaser').find('.am-overlay__add-to-collection').hide();
                $(this).closest('.am-asset-teaser').find('.am-overlay__update-collection').show();
              }

              $(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-right').hide();
              $(this).hide();

              var checkboxes = $(this).closest('.am-asset-teaser').find(".am-overlay--content").find(":checkbox");
              var checkboxSelected = false;
              checkboxes.each(function() {
                var json = this.attributes.getNamedItem('data-am-overlay__checkbox').nodeValue;
                var data = JSON.parse(json);
                if (hasRenditions) {
                  this.checked = coremedia.blueprint.am.downloadCollection.isInDownloadCollection(config.assetId, data.rendition);
                } else {
                  this.checked = coremedia.blueprint.am.downloadCollection.getDefaultRenditionSelection(data.rendition);
                  if (this.checked) {
                    checkboxSelected = this.checked;
                  }
                }
              });
              if (!hasRenditions) {
                var $updateButton = $(this).closest('.am-asset-teaser').find('.am-overlay__submit-button');
                if (checkboxSelected) {
                  $updateButton.removeAttr("disabled");
                } else {
                  $updateButton.attr("disabled", "disabled");
                }
              }
            });
          });

  // overlay close button
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".am-overlay__close-button",
          function ($closeButton, config) {

            $closeButton.on("click", function (event) {
              event.preventDefault();
              $(this).closest('.am-asset-teaser').find('.am-overlay').hide();
              $(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-left').css("display", "");
              $(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-right').show();
            });
          });

  // overlay update button
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            assetId: undefined
          },
          "am-overlay__update-button",
          function ($addToDownloadCollectionBtn, config) {
            $addToDownloadCollectionBtn.on("click", function (event) {
              event.preventDefault();

              if (!$(this).attr('disabled')) {
                var assetId = config.assetId;
                var hasRenditions = coremedia.blueprint.am.downloadCollection.hasRenditionInDownloadCollection(assetId);
                if (!hasRenditions) {
                  coremedia.blueprint.am.downloadCollection.clearDefaultRenditionSelection();
                }

                var checkboxes = $(this).closest(".am-overlay").find(":checkbox");
                checkboxes.each(function () {
                  var json = this.attributes.getNamedItem('data-am-overlay__checkbox').nodeValue;
                  var data = JSON.parse(json);
                  if (this.checked) {
                    coremedia.blueprint.am.downloadCollection.addRenditionToDownloadCollection(assetId, data.rendition);
                    if (!hasRenditions) {
                      coremedia.blueprint.am.downloadCollection.addDefaultRenditionSelection(data.rendition);
                    }
                  } else {
                    coremedia.blueprint.am.downloadCollection.removeRenditionFromDownloadCollection(assetId, data.rendition);
                    if (!hasRenditions) {
                      coremedia.blueprint.am.downloadCollection.removeDefaultRenditionSelection(data.rendition);
                    }
                  }
                });
                $(this).closest('.am-asset-teaser').find('.am-overlay').hide();
                $(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-left').css("display", "");
                $(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-right').show();

                hasRenditions = coremedia.blueprint.am.downloadCollection.hasRenditionInDownloadCollection(assetId);
                updateLeftBadgeIcon($(this).closest('.am-asset-teaser').find('.am-picture-box__badge-icon-left'), hasRenditions);
              }
            });
          });

  // overlay checkboxes
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
      {
        assetId: undefined
      },
      "am-overlay__checkbox",
      function ($checkbox, config) {

        $checkbox.on("click", function (event) {
          var hasRenditions = coremedia.blueprint.am.downloadCollection.hasRenditionInDownloadCollection(config.assetId);
          if (!hasRenditions) {
            var checkboxes = $(this).closest(".am-overlay__checkboxes").find(":checkbox");
            var buttonEnabled = false;
            checkboxes.each(function() {
              if (this.checked) {
                buttonEnabled = true;
              }
            });

            var submitButton = $(this).closest('.am-asset-teaser').find('.am-overlay__submit-button');
            if (buttonEnabled) {
              submitButton.removeAttr('disabled');
            } else {
              submitButton.attr("disabled", "disabled");
            }
          }
        });
      });

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            assetId: undefined,
            rendition: undefined
          },
          "am-download-collection-rendition-control",
          function ($renditionControl, config, state) {

            $.extend(state, {
              windowListener: function () {
                coremedia.blueprint.am.downloadCollection.updateRenditionLinkTextState($renditionControl, config);
              }
            });

            $window.on(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);

            // init
            coremedia.blueprint.am.downloadCollection.updateRenditionLinkTextState($renditionControl, config);

            $renditionControl.on("click", function () {
              coremedia.blueprint.am.downloadCollection.addOrRemoveRenditionFromDownloadCollection(config.assetId, config.rendition);
              coremedia.blueprint.am.downloadCollection.updateDownloadCollectionButtonState(undefined, undefined); // TODO: get button and counter
            });

          },
          function ($renditionControl, config, state) {
            state.windowListener && $window.off(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
          }
  );

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {},
          "am-download-collection-counter",
          function ($counter, config, state) {
            $.extend(state, {
              windowListener: function () {
               coremedia.blueprint.am.downloadCollection.updateDownloadCollectionCounterState($counter);
              }
            });
            $window.on(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
            // init
            coremedia.blueprint.am.downloadCollection.updateDownloadCollectionCounterState($counter);
          },
          function ($counter, config, state) {
            state.windowListener && $window.off(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
          }
  );

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            assetId: undefined,
            rendition: undefined
          },
          "am-rendition-collection-item",
          function ($collectionItem, config, state) {
            $.extend(state, {
              windowListener: function () {
                if (!coremedia.blueprint.am.downloadCollection.isInDownloadCollection(config.assetId, config.rendition)) {
                  $collectionItem.fadeOut(800, function() {
                    if($collectionItem)
                      $collectionItem.remove();
                  });
                }
              }
            });
            $window.on(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
          },
          function ($collectionItem, config, state) {
            state.windowListener && $window.off(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
          }
  );

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            prepareUrl : undefined,
            downloadUrl : undefined
          },
          "am-download-collection-trigger",
          function ($downloadCollection, config) {

            var $button = $downloadCollection.find(".am-download-collection__button");
            var $counter = $downloadCollection.find(".am-download-collection__counter");
            var $buttons = $button.add($counter);

            coremedia.blueprint.am.downloadCollection.updateDownloadCollectionButtonState($button, $counter);

            $buttons.on("click", function(event){

              event.preventDefault();

              var downloadCollection = coremedia.blueprint.am.downloadCollection.getDownloadCollection();
              var downloadCollectionString = JSON.stringify(downloadCollection);

              $downloadCollection.addClass("am-download-collection--loading");

              $buttons.prop("disabled", true);

              $.ajax({
                method: "POST",
                url: config.prepareUrl,
                data: { 'download-collection-data': downloadCollectionString}
              }).done(
                      function () {

                        $downloadCollection.removeClass("am-download-collection--loading");

                        var downloadUrl = config.downloadUrl;

                        var $form = $('<form></form>');
                        var $input = $('<input />');
                        var downloadCollectionDataString = JSON.stringify(coremedia.blueprint.am.downloadCollection.getDownloadCollection());
                        $input.attr("type", "hidden");
                        $input.attr("name", "download-collection-data");
                        $input.val(downloadCollectionDataString);
                        $form.append($input);
                        $downloadCollection.append($form);
                        $form.attr("action", downloadUrl);
                        $form.attr("method", "POST");
                        $form.submit();
                        $form.remove();

                        coremedia.blueprint.am.downloadCollection.clearDownloadCollection();
                        coremedia.blueprint.am.downloadCollection.updateDownloadCollectionButtonState($button, $counter);

                      }
              ).fail(function(response){

                        console.error("Failed to prepare download: ", response.responseText);

                      });
              return false;
            });
          });

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData(
          {
            url: undefined
          },
          "am-download-collection-overview",
          function ($collectionOverview, config, state) {
            var refresh = function () {
              var requestParams = {
                "download-collection-data": JSON.stringify(coremedia.blueprint.am.downloadCollection.getDownloadCollection())
              };
              var requestConfig = {
                url: config.url,
                params: requestParams,
                method: "POST"
              };
              coremedia.blueprint.basic.updateTargetWithAjaxResponse($collectionOverview, requestConfig, false, undefined);
            };
            $.extend(state, {
              windowListener: function () {
                if (coremedia.blueprint.am.downloadCollection.getDownloadCollectionCount() === 0) {
                  refresh();
                }
              }
            });
            $window.on(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
            refresh();
          },
          function ($collectionOverview, config, state) {
            state.windowListener && $window.off(coremedia.blueprint.am.downloadCollection.EVENT_UPDATED, state.windowListener);
          }
  );
});
