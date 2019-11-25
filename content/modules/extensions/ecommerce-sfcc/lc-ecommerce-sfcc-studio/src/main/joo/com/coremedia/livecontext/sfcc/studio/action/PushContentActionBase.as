package com.coremedia.livecontext.sfcc.studio.action {
import com.coremedia.cap.common.TrackedJob;
import com.coremedia.cap.common.impl.GenericRemoteJob;
import com.coremedia.cap.common.jobService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.Version;
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.desktop.ActionsToolbar;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.jobs.TrackedJobLoadMask;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin_properties;
import com.coremedia.livecontext.sfcc.studio.components.PushContentButton;
import com.coremedia.livecontext.sfcc.studio.components.PushContentNotification;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.RemoteBeanUtil;

import ext.Component;
import ext.Ext;
import ext.dom.Element;

import mx.resources.ResourceManager;

/**
 * This action is intended to be used from within EXML, only.
 */
[ResourceBundle('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin')]
public class PushContentActionBase extends AbstractPushContentActionBase {


  /**
   * @param config the configuration object
   */
  public function PushContentActionBase(config:PushContentAction = null) {
    super(PushContentAction(ActionConfigUtil.extendConfiguration(ResourceManager.getInstance()
                    .getResourceBundle(null, 'com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin').content,
            config, 'pushContent', {handler: pushContentJob})));

    actionId = 'pushContent';
    setHandler(pushContentJob);
  }

  private function pushContentJob():void {
    getStoreVE().loadValue(function (store:Store):void {
      if (store) {
        WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.loadValue(function (entity:RemoteBean):void {
          if (entity) {
            button.setDisabled(true);
            var trackedJob:TrackedJob = jobService.executeJob(
                    new GenericRemoteJob("pushContent", {entityUri: entity.getUri(), siteId: store.getSiteId()}),
                    //on success
                    function ():void {
                      // Show the animated notification at the button
                      var notificationConfig:PushContentNotification = PushContentNotification({});
                      var toolbar:ActionsToolbar = Ext.getCmp(ActionsToolbar.ID) as ActionsToolbar;
                      var pushContentButton:Component = toolbar.getComponent(PushContentButton.ITEM_ID);

                      notificationConfig.target = Element(pushContentButton.getEl()).dom.id;
                      var notification:PushContentNotification = new PushContentNotification(notificationConfig);
                      notification.show();
                      //calculate the active state after pushing the content successfully.
                      calculateActive();
                      button.setDisabled(false);
                      loadMask.destroy();
                    },
                    //on error
                    function ():void {
                      MessageBoxUtil.showWarn(EcommerceSfccStudioPlugin_properties.INSTANCE.Action_pushContent_messageBox_title,
                              EcommerceSfccStudioPlugin_properties.INSTANCE.Action_pushContent_messageBox_warn_text);
                      button.setDisabled(false)}
            );
            var loadMaskConfig:TrackedJobLoadMask = TrackedJobLoadMask({});
            loadMaskConfig.target = button;
            loadMaskConfig.trackedJob = trackedJob;
            var loadMask:TrackedJobLoadMask = new TrackedJobLoadMask(loadMaskConfig);
          }
        });
      }
    });
  }

  override protected function calculateActive():Boolean {
    var visible:Boolean = getVisibleVE().getValue();
    if (!visible) {
      return false;
    }
    var entity:RemoteBean = WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.getValue();

    if (!entity) {
      return false;
    }

    if (entity is CatalogObject) {
      return true;
    } else if (entity is Content) {
      var content:Content = entity as Content;
      var checkedInVersion:Version = content.getCheckedInVersion();

      return content.getType().isSubtypeOf("CMLinkable")
              && content.isCheckedIn()
              && checkedInVersion !== null
              && checkedInVersion.isLatestPublishedVersion();
    }
    return false;
  }

  override protected function calculateIconCls():String {
    var visible:Boolean = getVisibleVE().getValue();
    if (!visible) {
      return "";
    }
    var defaultIconCls:String = resourceManager.getString('com.coremedia.icons.CoreIcons', 'salesforce_push');

    var entity:RemoteBean = WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.getValue();
    if (!entity) {
      return defaultIconCls;
    }

    if (entity is Content) {
      var content:Content = entity as Content;
      if (!content.getType().isSubtypeOf("CMLinkable")) {
        return defaultIconCls;
      }
    }

    var pushStateBean:RemoteBean = createPushStateRemoteBean(entity);
    if (!RemoteBeanUtil.isAccessible(pushStateBean)){
      return undefined;
    }
    var pushState:String = pushStateBean.get('state');
    if (pushState !== "NOT_PUSHED") {
      return resourceManager.getString('com.coremedia.icons.CoreIcons', 'salesforce_push_check');
    }

    return defaultIconCls;
  }

  override protected function calculateTooltip():String {
    var visible:Boolean = getVisibleVE().getValue();
    if (!visible) {
      return "";
    }
    var prefix:String = ResourceManager.getInstance().getString('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin', "Action_pushContent_tooltip");
    return "<span style='white-space: nowrap'>" + prefix + "<br/>" + super.calculateTooltip() + "</span>";
  }
}
}
