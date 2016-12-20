package com.coremedia.livecontext.studio.pbe {

import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.messageService;
import com.coremedia.cms.editor.sdk.premular.Premular;
import com.coremedia.cms.editor.sdk.preview.PreviewIFrame;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.preview.metadata.IMetadataService;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTree;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTreeNode;
import com.coremedia.livecontext.studio.desktop.CommerceWorkAreaTab;
import com.coremedia.ui.components.IconButton;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.LocalStorageUtil;

import ext.Ext;
import ext.panel.Panel;

import js.Window;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
[ResourceBundle('com.coremedia.blueprint.studio.BlueprintDocumentTypes')]
public class FragmentHighlightButtonBase extends IconButton {

  private var metadataService:IMetadataService;
  private var previewPanelField:PreviewPanel;
  private var fragmentHighlightMetadata:FragmentHighlightMetadata;
  private var showFragments:Boolean;
  private var previousMetadatatreeRoot:MetadataTreeNode;

  internal static const PLACEMENT_LOCAL_IDENTIFIER:String = "CMChannel_placement-";
  internal static const PLACEMENT_LOCAL_TEXT:String = "_text";
  internal static const FRAGMENTHIGHLIGHTING_LOCAL_IDENTIFIER:String = "FragmentHighlighting_";
  internal static const LOCAL_STARAGE_FRAGMENTHIGHLIGHTED:String = 'preview.fragmentHighlighted';


  public function FragmentHighlightButtonBase(config:FragmentHighlightButton = null) {
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
    var frame:PreviewIFrame = getPreviewPanel().findByType(PreviewIFrame)[0] as PreviewIFrame;
    var targetWindow:Window = frame.getContentWindow();
    if (showFragments) {
      var returnMap =  buildPlacementsMap(resourceManager.getResourceBundle(null, 'com.coremedia.blueprint.studio.BlueprintDocumentTypes').content);
      Ext.apply(returnMap, buildPlacementDescriptionMap(resourceManager.getResourceBundle(null, 'com.coremedia.ecommerce.studio.ECommerceStudioPlugin').content));
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
      var ms:IMetadataService = getMetadataService();
      if(!ms) {
        return undefined;
      }
      var metadataTree:MetadataTree = ms.getMetadataTree();
      return metadataTree.getRoot() ? true : false;
    });

    var metadataPreviousLoadedRootVE:ValueExpression;
    metadataPreviousLoadedRootVE = ValueExpressionFactory.createFromFunction(function ():MetadataTreeNode {
      var ms:IMetadataService = getMetadataService();
      if(!ms) {
        return undefined;
      }
      var metadataTree:MetadataTree = ms.getMetadataTree();
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
      var activeTab:Panel = editorContext.getWorkArea().getActiveTab() as Panel;
      if (activeTab && (activeTab.isXType(Premular.xtype) || activeTab.isXType(CommerceWorkAreaTab.xtype))) {
        previewPanelField = activeTab.findByType(PreviewPanel.xtype)[0] as PreviewPanel;
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
      if(previewPanel) {
        metadataService = previewPanel.getMetadataService();
      }
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
