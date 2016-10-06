package com.coremedia.livecontext.studio.pbe {

import com.coremedia.blueprint.studio.BlueprintDocumentTypes_properties;
import com.coremedia.cms.editor.sdk.config.premular;
import com.coremedia.cms.editor.sdk.config.previewIFrame;
import com.coremedia.cms.editor.sdk.config.previewPanel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.messageService;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.preview.metadata.IMetadataService;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTree;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTreeNode;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.commerceWorkAreaTab;
import com.coremedia.livecontext.studio.config.fragmentHighlightButton;
import com.coremedia.ui.components.IconButton;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.LocalStorageUtil;

import ext.Ext;
import ext.Panel;

import js.Window;

public class FragmentHighlightButtonBase extends IconButton {

  private var metadataService:IMetadataService;
  private var previewPanelField:PreviewPanel;
  private var fragmentHighlightMetadata:FragmentHighlightMetadata;
  private var showFragments:Boolean;
  private var previousMetadatatreeRoot:MetadataTreeNode;

  internal static const PLACEMENT_LOCAL_IDENTIFIER:String = "CMChannel_placement-";
  internal static const PLACEMENT_LOCAL_TEXT:String = "_text";
  internal static const FRAGMENTHIGHLIGHTING_LOCAL_IDENTIFIER = "FragmentHighlighting_";
  internal static const LOCAL_STARAGE_FRAGMENTHIGHLIGHTED:String = 'preview.fragmentHighlighted';


  public function FragmentHighlightButtonBase(config:fragmentHighlightButton = null) {
    super(config);
    showFragments = readButtonSelection();
    toggle(showFragments);
    hide();
    on('afterrender', initButton);
    addListener('click', handleButtonClick);
  }

  private function handleButtonClick():void {
    showFragments = !showFragments;
    sendHighlightEvent();
  }

  private function sendHighlightEvent():void {
    var targetWindow:Window = getPreviewPanel().findByType(previewIFrame)[0].getContentWindow();
    if (showFragments) {
      var returnMap =  buildPlacementsMap(BlueprintDocumentTypes_properties.INSTANCE);
      Ext.apply(returnMap, buildPlacementDescriptionMap(ECommerceStudioPlugin_properties.INSTANCE));
      messageService.sendMessage(targetWindow, LcMessageTypes.TO_HIGHLIGHT_MODE, returnMap);
    }
    else {
      messageService.sendMessage(targetWindow, LcMessageTypes.TO_DEFAULT_MODE , {});
    }
    storeButtonSelection(showFragments);
  }

  private function buildPlacementsMap(localizations:Object):Object {
    var placementsMap:Object = new Object();
    for (var key:String in localizations) {
      if(key.indexOf(PLACEMENT_LOCAL_IDENTIFIER) >= 0 && key.indexOf(PLACEMENT_LOCAL_TEXT) >= 0){
        var technicalKey = key.substr(PLACEMENT_LOCAL_IDENTIFIER.length);
        technicalKey = technicalKey.substr(0, technicalKey.indexOf(PLACEMENT_LOCAL_TEXT));
        placementsMap[technicalKey] = localizations[key];
      }
    }
    return placementsMap;
  }

  private function buildPlacementDescriptionMap(localizations:Object):Object {
    var descriptionsMap:Object = new Object();
    for (var key:String in localizations) {
      if(key.indexOf(FRAGMENTHIGHLIGHTING_LOCAL_IDENTIFIER) >= 0){
        descriptionsMap[key] = localizations[key];
      }
    }
    return descriptionsMap;
  }

  private function initButton():void {
    var metadataLoadedVE:ValueExpression;
    metadataLoadedVE = ValueExpressionFactory.createFromFunction(function ():Boolean {
      var metadataTree:MetadataTree = getMetadataService().getMetadataTree();
      return metadataTree.getRoot() ? true : false;
    });

    var metadataPreviousLoadedRootVE:ValueExpression;
    metadataPreviousLoadedRootVE = ValueExpressionFactory.createFromFunction(function ():MetadataTreeNode {
      var metadataTree:MetadataTree = getMetadataService().getMetadataTree();
      return metadataTree.getRoot();
    });

    if (!metadataLoadedVE.getValue()) {
      // metadata not yet loaded
      metadataLoadedVE.addChangeListener(reevaluateButtonState);
    }
    else {
      reevaluateButtonState();
    }

    metadataPreviousLoadedRootVE.addChangeListener(function (currentNode:ValueExpression):void {
      if (currentNode.getValue() !== previousMetadatatreeRoot) {
        reevaluateButtonState();
      }
      previousMetadatatreeRoot = currentNode.getValue();
    });
  }


  private function reevaluateButtonState():void {
    var containsFragments:Boolean = getFragmentHighlightMetadata().previewContainsFragmentMetadata();

    if (containsFragments) {
      show();
    }
    else {
      hide();
    }
  }

  private function getFragmentHighlightMetadata():FragmentHighlightMetadata {
    if (!fragmentHighlightMetadata) {
      fragmentHighlightMetadata = new FragmentHighlightMetadata(getMetadataService());
    }
    return fragmentHighlightMetadata;
  }

  private function getPreviewPanel():PreviewPanel {
    if (!previewPanelField) {
      var activeTab:Panel = editorContext.getWorkArea().getActiveTab();
      if (activeTab.isXType(premular.xtype) || activeTab.isXType(commerceWorkAreaTab.xtype)) {
        previewPanelField = activeTab.findByType(previewPanel.xtype)[0] as PreviewPanel;
        if (previewPanelField) {
          previewPanelField.addListener('previewUrl', sendHighlightEvent);
        }
      }
    }
    return previewPanelField;
  }

  public function getMetadataService():IMetadataService {
    if (!metadataService) {
      var previewPanel:PreviewPanel = getPreviewPanel();
      metadataService = previewPanel.getMetadataService();
    }
    return metadataService;
  }

  private static function storeButtonSelection(state:Boolean):void {
    LocalStorageUtil.setItem(LOCAL_STARAGE_FRAGMENTHIGHLIGHTED, state.toString());
  }

  private static function readButtonSelection():Boolean {
    return(LocalStorageUtil.getItem(LOCAL_STARAGE_FRAGMENTHIGHLIGHTED) === 'true');
  }
}
}
