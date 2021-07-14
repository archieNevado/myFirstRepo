package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.premular.fields.LinkListDropArea;
import com.coremedia.cms.editor.sdk.util.ILinkListWrapper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.mixins.IHidableMixin;
import com.coremedia.ui.mixins.ISideButtonMixin;
import com.coremedia.ui.mixins.IValidationStateMixin;
import com.coremedia.ui.mixins.ValidationState;
import com.coremedia.ui.mixins.ValidationStateMixin;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;
import com.coremedia.ui.util.createComponentSelector;

import ext.Component;
import ext.Ext;
import ext.StringUtil;
import ext.button.Button;
import ext.dd.DropTarget;
import ext.dd.ScrollManager;
import ext.grid.GridPanel;

/**
 * @private
 *
 * The application logic for a property field editor that edits
 * link lists. Links can be limited to documents of a given type.
 *
 * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
 * @see com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField
 */
[ResourceBundle('com.coremedia.cms.editor.Editor')]
[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class TaxonomyLinkListGridPanelBase extends GridPanel implements IValidationStateMixin, ISideButtonMixin, IHidableMixin {

  [ExtConfig]
  public var linkListWrapper:ILinkListWrapper;

  [ExtConfig]
  public var readOnlyValueExpression:ValueExpression;

  /**
   * A ValueExpression whose value is set to the array of indexes of selected items.
   * The array is empty if nothing is selected. The change of the value doesn't update the selection of the grid.
   */
  [ExtConfig]
  public var selectedPositionsExpression:ValueExpression;

  /**
   * A ValueExpression whose value is set to the array of selected items.
   * The array is empty if nothing is selected.
   * The selection is updated by changing the value of this expression.
   */
  [ExtConfig]
  public var selectedValuesExpression:ValueExpression;

  /**
   * The premular content ValueExpression
   */
  [ExtConfig]
  public var bindTo:ValueExpression;

  /**
   * The taxonomy identifier configured on the server side.
   */
  [ExtConfig]
  public var taxonomyIdExpression:ValueExpression;

  [ExtConfig]
  public var selectionMode:String;

  [ExtConfig]
  public var removeCallback:Function;

  private var dropTarget:DropTarget;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  public function TaxonomyLinkListGridPanelBase(config:TaxonomyLinkListGridPanelBase = null) {
    super(config);
    initValidationStateMixin();
    initSideButtonMixin();
    this.addListener("afterlayout", refreshLinkList);
    on("validationStateChanged", onValidationChanged);
    on("validationMessageChanged", onValidationChanged);
    onValidationChanged();
  }

  override protected function afterRender():void {
    super.afterRender();

    if (this.scrollable) {
      ScrollManager.register(getScrollerDom());

      this.addListener("beforedestroy", onBeforeDestroy, this, {single: true});
    }

    if(readOnlyValueExpression) {
      readOnlyValueExpression.addChangeListener(readOnlyChanged);
    }
  }

  private function readOnlyChanged():void {
    refreshLinkList(true);
  }

  private function onValidationChanged():void {
    var viewValidation:ValidationStateMixin = view as ValidationStateMixin;
    if (viewValidation) {
      viewValidation.validationState = validationState;
      viewValidation.validationMessage = validationMessage;
    }
    var dropArea:Component = query(createComponentSelector()._xtype(LinkListDropArea.xtype).build())[0];
    if (dropArea) {
      var dropAreaValidation:ValidationStateMixin = dropArea as ValidationStateMixin;
      if (dropAreaValidation) {
        dropAreaValidation.validationState = validationState;
        dropAreaValidation.validationMessage = validationMessage;
      }
    }
  }

  private function isWritable():Boolean {
    return !readOnlyValueExpression || !readOnlyValueExpression.getValue();
  }

  private function onBeforeDestroy():void {
    if(readOnlyValueExpression) {
      readOnlyValueExpression.removeChangeListener(readOnlyChanged);
    }

    // if we previously registered with the scroll manager, unregister
    // it (if we don't, it will lead to problems in IE)
    ScrollManager.unregister(getScrollerDom());
  }

  /**
   * Return the DOM element associated with the scroller of the grid.
   * This method uses undocumented API.
   *
   * @return the DOM element
   */
  private function getScrollerDom():* {
    return getEl().dom;
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   *
   * @see http://docs.sencha.com/extjs/6.0.1-classic/Ext.grid.column.Column.html#cfg-renderer
   *
   * @return String
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    var taxonomyId:String = taxonomyIdExpression.getValue();
    TaxonomyUtil.isEditable(taxonomyId, function (editable:Boolean):void {
      if (editable) {
        var content:Content = null;
        if (bindTo && bindTo.getValue()) {
          content = bindTo.getValue();
        }

        TaxonomyUtil.loadTaxonomyPath(record, content, taxonomyId, function (updatedRecord:BeanRecord):void {
          //noinspection JSMismatchedCollectionQueryUpdate
          var links:Array = linkListWrapper.getLinks() || [];
          var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSelectedListRenderer(record.data.nodes, getId(), links.length > 3);
          renderer.setRenderControl(!readOnlyValueExpression || !readOnlyValueExpression.getValue());

          renderer.doRender(function (html:String):void {
            if (record.data.html !== html) {
              record.data.html = html;
              record.commit(false);
            }
          });
        });
      } else {
        var msg:String = StringUtil.format(resourceManager.getString('com.coremedia.cms.editor.Editor', 'Content_notReadable_text'), IdHelper.parseContentId(record.getBean()));
        var html:String = '<img width="16" height="16" src="' + Ext.BLANK_IMAGE_URL + '" data-qtip="" />'
                + '<span>' + msg + '</span>';
        if (record.data.html !== html) {
          record.data.html = html;
          EventUtil.invokeLater(function ():void {
            record.commit(false);
          });
        }
      }
    }, record.getBean() as Content);

    if (!record.data.html) {
      return "<div class='loading'>" + resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyLinkList_status_loading_text') + "</div>";
    }
    return record.data.html;
  }

  /**
   * Executes after layout, we have to refresh the HTML too.
   */
  private function refreshLinkList(forceCommit:Boolean = false):void {
    this.removeListener("afterlayout", refreshLinkList);
    for (var i:int = 0; i < getStore().getCount(); i++) {
      getStore().getAt(i).data.html = null;
      if(forceCommit) {
        getStore().getAt(i).commit(false);
      }
    }
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Removes the given taxonomy<br>
   * Used in TaxonomyRenderer#plusMinusClicked
   */
  public function plusMinusClicked(nodeRef:String):void {
    if (isWritable()) {
      TaxonomyUtil.removeNodeFromSelection(linkListWrapper.getVE(), nodeRef, removeCallback);
    }
  }

  override protected function onRemoved(destroying:Boolean):void {
    dropTarget && dropTarget.unreg();
    super.onRemoved(destroying);
  }

  /** @inheritDoc */
  public native function initValidationStateMixin():void;

  /** @private */
  [Bindable]
  public native function set validationState(validationState:ValidationState):void;

  /** @private */
  [Bindable]
  public native function set validationMessage(validationMessage:String):void;

  /** @inheritDoc */
  [Bindable]
  public native function get validationState():ValidationState;

  /** @inheritDoc */
  [Bindable]
  public native function get validationMessage():String;

  /** @inheritDoc */
  public native function initSideButtonMixin():void;

  /** @private */
  [ExtConfig(create=false)]
  [Bindable]
  public native function set sideButtonCfg(newSideButton:Button):void;

  /** @private */
  [Bindable]
  public native function get sideButton():Button;

  /** @private */
  [Bindable]
  public native function set sideButtonDisabled(disabled:Boolean):void;

  /** @private */
  [Bindable]
  public native function set sideButtonSticky(sticky:Boolean):void;

  /** @private */
  [Bindable]
  public native function set sideButtonDisableAdjustOnHiddenLabel(disableAdjust:Boolean):void;

  /** @private */
  [Bindable]
  public native function set sideButtonVerticalAdjustment(position:Number):void;

  /** @private */
  [Bindable]
  public native function set sideButtonHorizontalAdjustment(position:Number):void;

  /** @private */
  [Bindable]
  public native function set sideButtonRenderToFunction(renderTo:Function):void;

  /** @private */
  [Bindable]
  public function set hideText(newHideText:String):void {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  [Bindable]
  public function get hideText():String {
    return getTitle();
  }

  /** @private */
  [Bindable]
  public native function set hideId(newHideId:String):void;

  /** @inheritDoc */
  [Bindable]
  public native function get hideId():String;

}
}
