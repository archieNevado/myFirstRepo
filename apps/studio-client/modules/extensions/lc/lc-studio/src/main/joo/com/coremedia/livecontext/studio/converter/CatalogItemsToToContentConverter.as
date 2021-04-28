package com.coremedia.livecontext.studio.converter {
import com.coremedia.cap.common.jobService;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.studio.base.cap.models.converter.ConverterTargetKeys;
import com.coremedia.cms.studio.base.cap.models.converter.ItemConverter;
import com.coremedia.cms.studio.base.cap.services.api.converter.content.ToContentConverterHint;
import com.coremedia.cms.studio.multisite.models.sites.Site;
import com.coremedia.cms.studio.multisite.models.sites.global.sitesService;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.ProductVariant;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin;
import com.coremedia.livecontext.studio.job.AugmentationJob;

import ext.Ext;

import joo.JavaScriptObject;

import js.Promise;

public class CatalogItemsToToContentConverter extends JavaScriptObject implements ItemConverter {

  public static const ID:String = "catalogItemsToContentConverter";

  public function getId():String {
    return ID;
  }

  public function getTargetKey():String {
    return ConverterTargetKeys.CONTENT;
  }

  public function handles(item:*):Promise {
    return new Promise(function (resolve:Function):void {
      resolve(item is Product || item is Category);
    });
  }

  public function computeHints(items:Array, options:Object = null):Promise {
    return new Promise(function (resolve:Function):void {
      resolve(items.map(function (item:CatalogObject):ToContentConverterHint {
        var contentConverterHint:ToContentConverterHint = new ToContentConverterHint();
        contentConverterHint.sourceId = item.getUriPath();
        contentConverterHint.targetContentType = getTargetContentType(item);
        contentConverterHint.targetFolderUriPath = getTargetFolderPath(item, options);
        contentConverterHint.cacheable = true;
        return contentConverterHint;
      }));
    })
  }

  private static function getTargetFolderPath(catalogObject:CatalogObject, options:Object):String {
    if (catalogObject is ProductVariant) {
      return !!options && !!options.targetFolder ? (options.targetFolder as Content).getUriPath() : null
    }

    return null;
  }

  private static function getTargetContentType(item:CatalogObject):String {
    if (item is ProductVariant) {
      return LivecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER;
    } else if (item is Product) {
      return LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PRODUCT;
    } else if (item is Category) {
      return LivecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL;
    }

    return null;
  }

  public function convert(commerceObjects:Array, options:Object = null):Promise {
    return new Promise(function (resolve:Function):void {
      var targetFolder:Content = evaluateTargetFolder(options);

      commerceObjects.forEach(function (catalogObject:CatalogObject):void {

        var augmentationJob:AugmentationJob = new AugmentationJob(catalogObject, targetFolder);
        jobService.executeJob(augmentationJob, function (content:Content):void {
          content.checkIn();
          resolve([content]);
        }, Ext.emptyFn);
      })
    });
  }

  private static function evaluateTargetFolder(options:Object):Content {
    if (options && !!options.targetFolder) {
      return options.targetFolder;
    }

    var preferredSite:Site = sitesService.getPreferredSite();
    if (preferredSite) {
      return preferredSite.getSiteRootFolder();
    }

    return null;
  }
}
}
