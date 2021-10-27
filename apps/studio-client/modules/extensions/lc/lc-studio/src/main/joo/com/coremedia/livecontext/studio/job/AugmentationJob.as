package com.coremedia.livecontext.studio.job {
import com.coremedia.cap.common.Job;
import com.coremedia.cap.common.JobContext;
import com.coremedia.cap.common.JobExecutionError;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.editor.sdk.actions.ShowInRepositoryAction;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.jobs.BackgroundJob;
import com.coremedia.cms.editor.sdk.util.ContentCreationUtil;
import com.coremedia.cms.studio.multisite.models.sites.Site;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.ProductVariant;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;

import ext.Ext;

import joo.localeSupport;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.livecontext.studio.LivecontextStudioPlugin')]
public class AugmentationJob implements Job, BackgroundJob {

  private var catalogObject:CatalogObject;
  private var targetFolder:Content;
  private var augmentedObject:Content;

  public function AugmentationJob(catalogObject:CatalogObject, targetFolder:Content = null) {
    this.catalogObject = catalogObject;
    this.targetFolder = targetFolder;
  }

  public function execute(jobContext:JobContext):void {
    if (catalogObject is ProductVariant) {
      var externalId:String = catalogObject.getId();
      var preferredName:String = catalogObject.getName();
      var properties:Object = {
        externalId: externalId,
        locale: getLocale(catalogObject)
      };
      ContentCreationUtil.createContent(targetFolder, false, false, preferredName, getContentType("CMProductTeaser"), function (content:Content):void {
        augmentedObject = content;
        jobContext.notifySuccess(content);
      }, Ext.emptyFn, properties);
    } else {
      var augmentCommerceBeanUri:String = catalogObject.getStore().getUriPath() + "/augment";
      var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod(augmentCommerceBeanUri, 'POST', true);
      remoteServiceMethod.request({$Ref: catalogObject.getUriPath()}, function (response:RemoteServiceMethodResponse):void {
        if (response.success) {
          var content:Content = Content(response.getResponseJSON());
          augmentedObject = content;
          content.load(function ():void {
            ContentCreationUtil.initialize(content);
            jobContext.notifySuccess(content);
          });
        } else {
          jobContext.notifyError(new JobExecutionError("Augmentation failed"));
        }
      });
    }
  }

  public function requestAbort(jobContext:JobContext):void {
    // No
  }

  public function getNameExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      return catalogObject.getName();
    });
  }

  public function getIconClsExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      if(catalogObject is ProductVariant) {
        return ResourceManager.getInstance().getString("com.coremedia.livecontext.studio.LivecontextStudioPlugin", "CMProductTeaser_icon");
      } else if (catalogObject is Product) {
        return ResourceManager.getInstance().getString("com.coremedia.livecontext.studio.LivecontextStudioPlugin", "CMExternalProduct_icon");
      } else {
        return ResourceManager.getInstance().getString("com.coremedia.livecontext.studio.LivecontextStudioPlugin", "CMExternalChannel_icon");
      }
    });
  }

  public function getErrorHandler():Function {
    return null;
  }

  public function getSuccessHandler():Function {
    return function ():void {
      var showInRepositoryAction:ShowInRepositoryAction = new ShowInRepositoryAction(ShowInRepositoryAction({
        contentValueExpression: ValueExpressionFactory.createFromValue(augmentedObject)
      }));
      showInRepositoryAction.execute();
    }
  }

  private static function getLocale(catalogObject:CatalogObject):String {
    var site:Site = editorContext.getSitesService().getSite(catalogObject.getSiteId());
    var locale:String;
    if (site) {
      locale = site.getLocale().getLanguageTag();
    } else {
      locale = localeSupport.getLocale();
    }
    return locale;
  }

  internal static function getContentType(contentType:String):ContentType {
    return SESSION.getConnection().getContentRepository().getContentType(contentType) as ContentType;
  }
}
}
