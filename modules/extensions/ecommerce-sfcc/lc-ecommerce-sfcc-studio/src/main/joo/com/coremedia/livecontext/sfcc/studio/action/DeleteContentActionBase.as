package com.coremedia.livecontext.sfcc.studio.action {
import com.coremedia.cap.common.TrackedJob;
import com.coremedia.cap.common.impl.GenericRemoteJob;
import com.coremedia.cap.common.jobService;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.desktop.ActionsToolbar;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.jobs.TrackedJobLoadMask;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin_properties;
import com.coremedia.livecontext.sfcc.studio.components.DeleteContentButton;
import com.coremedia.livecontext.sfcc.studio.components.DeleteContentNotification;
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
public class DeleteContentActionBase extends AbstractPushContentActionBase {

  /**
   * @param config the configuration object
   */
  public function DeleteContentActionBase(config:DeleteContentAction = null) {
    super(DeleteContentActionBase(ActionConfigUtil.extendConfiguration(ResourceManager.getInstance()
                    .getResourceBundle(null, 'com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin').content,
            config, 'deleteContent', {handler: deleteContentJob})));

    actionId = 'deleteContent';
    setHandler(deleteContentJob);
  }

  private function deleteContentJob():void {
    getStoreVE().loadValue(function (store:Store):void {
      if (store) {
        WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.loadValue(function (entity:RemoteBean):void {
          if (entity) {
            button.setDisabled(true);
            var trackedJob:TrackedJob = jobService.executeJob(new GenericRemoteJob("removePushedContent", {entityUri: entity.getUri(),siteId: store.getSiteId()}),
                    //on success
                    function ():void {
                      // Show the animated notification at the button
                      var notificationConfig:DeleteContentNotification = DeleteContentNotification({});
                      var toolbar:ActionsToolbar = Ext.getCmp(ActionsToolbar.ID) as ActionsToolbar;
                      var deleteContentButton:Component = toolbar.getComponent(DeleteContentButton.ITEM_ID);

                      notificationConfig.target = Element(deleteContentButton.getEl()).dom.id;
                      var notification:DeleteContentNotification = new DeleteContentNotification(notificationConfig);
                      notification.show();
                      //calculate the active state after deleting the content successfully.
                      calculateActive();
                      loadMask.destroy();
                    },
                    //on error
                    function ():void {
                      MessageBoxUtil.showWarn(EcommerceSfccStudioPlugin_properties.INSTANCE.Action_pushContent_messageBox_title,
                              EcommerceSfccStudioPlugin_properties.INSTANCE.Action_pushContent_messageBox_warn_text);
                    });
            var loadMaskConfig:TrackedJobLoadMask = TrackedJobLoadMask({});
            loadMaskConfig.target = button;
            loadMaskConfig.trackedJob = trackedJob;
            var loadMask:TrackedJobLoadMask = new TrackedJobLoadMask(loadMaskConfig);
          }
        })
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

    if (entity is Content) {
      var content:Content = entity as Content;
      if(!content.getType().isSubtypeOf("CMLinkable")) {
        return false;
      }
    }

    //check if pushed
    var pushStateBean:RemoteBean = createPushStateRemoteBean(entity);
    if (!RemoteBeanUtil.isAccessible(pushStateBean)){
      return undefined;
    }
    var pushState:String = pushStateBean.get('state');
    return pushState !== "NOT_PUSHED";
  }

  override protected function calculateIconCls():String {
    var visible:Boolean = getVisibleVE().getValue();
    if (!visible) {
      return "";
    }
    var defaultIconCls:String = resourceManager.getString('com.coremedia.icons.CoreIcons', 'salesforce_withdraw');

    var entity:RemoteBean = WorkArea.ACTIVE_ENTITY_VALUE_EXPRESSION.getValue();
    if (!entity) {
      return defaultIconCls;
    }

    if (entity is Content){
      var content:Content = entity as Content;
      if(!content.getType().isSubtypeOf("CMLinkable")) {
        return defaultIconCls;
      }

      var pushStateBean:RemoteBean = createPushStateRemoteBean(entity);
      if (!RemoteBeanUtil.isAccessible(pushStateBean)){
        return undefined;
      }
      var pushState:String = pushStateBean.get('state');
      if (pushState !== "NOT_PUSHED" && !content.isPublished()){
        return resourceManager.getString('com.coremedia.icons.CoreIcons', 'salesforce_withdraw_error');
      }
    }

    return defaultIconCls;
  }

  override protected function calculateTooltip():String {
    var visible:Boolean = getVisibleVE().getValue();
    if (!visible) {
      return "";
    }
    var prefix:String = ResourceManager.getInstance().getString('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin', "Action_deleteContentt_tooltip");
    return "<span style='white-space: nowrap'>" + prefix + "<br/>" + super.calculateTooltip() + "</span>";
  }
}
}
