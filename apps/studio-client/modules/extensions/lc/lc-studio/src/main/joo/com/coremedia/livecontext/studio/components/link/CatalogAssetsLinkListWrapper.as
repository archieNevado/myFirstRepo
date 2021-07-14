package com.coremedia.livecontext.studio.components.link {
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.sdk.util.LinkListWrapperBase;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import js.Promise;

public class CatalogAssetsLinkListWrapper extends LinkListWrapperBase {

  [ExtConfig]
  public var bindTo:ValueExpression;

  [ExtConfig]
  public var linksVE:ValueExpression;

  [ExtConfig]
  public var assetContentTypes:Array;

  [ExtConfig]
  public var maxCardinality:int;

  [ExtConfig]
  public var readOnlyVE:ValueExpression;

  public function CatalogAssetsLinkListWrapper(config:CatalogAssetsLinkListWrapper = null) {
    super(config);
    bindTo = config.bindTo;
    linksVE = config.linksVE;
    assetContentTypes = config.assetContentTypes || [];
    maxCardinality = config.maxCardinality || 0;
    readOnlyVE = config.readOnlyVE;
  }

  override public function getVE():ValueExpression {
    return linksVE;
  }

  override public function acceptsLinks(links:Array, replaceLinks:Boolean = false):Boolean {
    if (links.length > (replaceLinks ? getTotalCapacity() : getFreeCapacity())) {
      return false;
    }
    for each (var asset:Content in links) {
      if (!(asset is Content)) {
        return false;
      }

      //check the content type
      var typeAccepted:Boolean = assetContentTypes.some(function (assetContentType:String):Boolean {
        return ContentUtil.filterMatchingTypes(getContentType(assetContentType), [asset], true).length === 0;
      });
      if (!typeAccepted) {
        return false;
      }

      if (PropertyEditorUtil.isReadOnly(asset)) {
        return false;
      }
    }
    return true;
  }

  override public function getLinks():Array {
    var value:* = getVE().getValue();
    return value === undefined ? undefined : value as Array;
  }

  override public function setLinks(links:Array):Promise {
    if (links) {
      return new Promise(function (resolve:Function):void {
        var promises:Array = [];
        for each (var content:Content in links) {
          if (!PropertyEditorUtil.isReadOnly(content)) {
            promises.push(CatalogHelper.getInstance().createOrUpdateProductListStructs(ValueExpressionFactory.createFromValue(content),
                    bindTo.getValue()));
          }
        }

        Promise.all(promises).then(function (results:Array):void {
          resolve(results.filter(function (product:Product):Boolean {
            return product !== null;
          }));
        })
      });
    } else {
      return Promise.resolve([]);
    }
  }

  override public function getTotalCapacity():int {
    return maxCardinality > 0 ? maxCardinality : int.MAX_VALUE;
  }

  override public function getFreeCapacity():int {
    return getTotalCapacity() - (getLinks() ? getLinks().length : 0);
  }

  override public function isReadOnly():Boolean {
    return readOnlyVE ? readOnlyVE.getValue() : false;
  }

  private static function getContentType(linkTypeName:String):ContentType {
    return SESSION.getConnection().getContentRepository().getContentType(linkTypeName) as ContentType;
  }
}
}
