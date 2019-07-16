package com.coremedia.livecontext.sfcc.studio.action {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.TimeUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.components.IconButton;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.RemoteBeanUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Action;
import ext.Component;
import ext.Ext;

import mx.resources.ResourceManager;

/**
 * This action is intended to be used from within EXML, only.
 */
[ResourceBundle('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin')]
public class AbstractPushContentActionBase extends Action {

  private var storeVE:ValueExpression;
  private var visibleVE:ValueExpression;
  private var activeVE:ValueExpression;
  private var tooltipVE:ValueExpression;
  private var iconClsVE:ValueExpression;

  protected var button:IconButton;

  /**
   * @param config the configuration object
   */
  public function AbstractPushContentActionBase(config:Action = null) {
    super(config);

    getStoreVE();

    getVisibleVE().addChangeListener(updateVisible);
    updateVisible();

    getActiveVE().addChangeListener(updateActive);
    updateActive();

    getTooltipValueExpression().addChangeListener(updateTooltip);
    updateTooltip();

    getIconClsVE().addChangeListener(updateIcon);
    updateIcon(getIconClsVE());
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    this.button = comp as IconButton;
    this.updateIcon(getIconClsVE());
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);

    //remove the event listeners
    getVisibleVE().removeChangeListener(updateVisible);
    getActiveVE().removeChangeListener(updateActive);
    getTooltipValueExpression().removeChangeListener(updateTooltip);
    getIconClsVE().removeChangeListener(updateIcon);
  }

  protected function getStoreVE():ValueExpression {
    if (!storeVE) {
      storeVE = ValueExpressionFactory.createFromFunction(calculateStore);
    }
    return storeVE;
  }

  protected function getVisibleVE():ValueExpression {
    if (!visibleVE) {
      visibleVE = ValueExpressionFactory.createFromFunction(calculateVisible);
    }
    return visibleVE;
  }

  protected function getActiveVE():ValueExpression {
    if (!activeVE) {
      activeVE = ValueExpressionFactory.createFromFunction(calculateActive);
    }
    return activeVE;
  }

  private function getIconClsVE():ValueExpression {
    if (!iconClsVE) {
      iconClsVE = ValueExpressionFactory.createFromFunction(function ():String {
        return calculateIconCls();
      });
    }
    return iconClsVE;
  }

  private function getTooltipValueExpression():ValueExpression {
    if (!tooltipVE) {
      tooltipVE = ValueExpressionFactory.createFromFunction(calculateTooltip);
    }
    return tooltipVE;
  }

  protected function updateVisible():void {
    setHidden(!getVisibleVE().getValue());
  }

  protected function updateActive():void {
    setDisabled(!getActiveVE().getValue());
  }

  protected function updateIcon(ve:ValueExpression):void {
    if (this.button) {
      this.button.setIconCls(ve.getValue());
    }
  }

  protected function updateTooltip():void {
    var value:String = getTooltipValueExpression().getValue() || tooltip;
    setTooltip(value);
  }

  protected static function calculateStore():Store {
    var entity:RemoteBean = WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.getValue();

    if (entity === undefined) {
      return undefined;
    }

    if (entity) {
      if (entity is Content) {
        return CatalogHelper.getInstance().getStoreForContentExpression(ValueExpressionFactory.createFromValue(entity))
                .getValue();
      } else if (entity is CatalogObject) {
        return CatalogObject(entity).getStore();
      }
    }

    return null;
  }

  /**
   * Needs to be implemented by the sublcasses
   * @return true if the button shall be active
   */
  protected function calculateActive():Boolean {
    return false;
  }

  protected function calculateVisible():Boolean {
    var store:Store = getStoreVE().getValue();
    if (!store) {
      return false;
    }
    var siteId:String = store.getSiteId();
    if (!siteId) {
      return false;
    }

    var site:Site = editorContext.getSitesService().getSite(siteId);
    if (site) {
      var rootFolder:Content = site.getSiteRootFolder();
      var liveContextSettings:Content = rootFolder.getChild("Options/Settings/LiveContext");
      if (liveContextSettings) {
        var pushContentExpression:ValueExpression = ValueExpressionFactory.createFromValue(liveContextSettings)
                .extendBy("properties.settings.livecontext-fragments.pushContent");
        pushContentExpression.loadValue(Ext.emptyFn);
        return pushContentExpression.getValue();
      }
    }
    return false;
  }

  /**
   * needs to be implemented by subclasses
   * @return the css class string for the icon
   */
  protected function calculateIconCls():String {
    //needs to be implemented by subclasses
  }

  protected function calculateTooltip():String {
    var entity:RemoteBean = WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.getValue();

    if (!entity || !(entity is Content || entity is CatalogObject)) {
      return "";
    } else if (entity is Content) {
      var content:Content = entity as Content;
      if (!content.getType().isSubtypeOf("CMLinkable")) {
        return "";
      }
    }

    var pushStateBean:RemoteBean = createPushStateRemoteBean(entity);
    if (!RemoteBeanUtil.isAccessible(pushStateBean)){
      return undefined;
    }
    var state:String = pushStateBean.get("state") as String;
    var modificationDate:Date = pushStateBean.get("modificationDate") as Date;

    return formatPushStateTooltip(state, modificationDate);
  }

  protected static function formatPushStateTooltip(state:String, date:Date):String {
    var stateLabel:String = ResourceManager.getInstance().getString('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin', "PushState_state_label");
    var dateLabel:String = ResourceManager.getInstance().getString('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin', "PushState_date_label");

    return stateLabel +": " + ResourceManager.getInstance().getString('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin', "PushState_" + state)
            + "<br/>" + dateLabel +": " + TimeUtil.formatDateTime(date);
  }

  internal static function createPushStateRemoteBean(entity:RemoteBean):RemoteBean{
    var pushStateBeanUri:String = entity.getUriPath();
    if (entity is Content){
      pushStateBeanUri = "livecontext/pushState/" + pushStateBeanUri;
    } else if (entity is CatalogObject){
      pushStateBeanUri = pushStateBeanUri.replace("livecontext/", "livecontext/pushState/");
    } else {
      return null;
    }

    return beanFactory.getRemoteBean(pushStateBeanUri);
  }
}
}
