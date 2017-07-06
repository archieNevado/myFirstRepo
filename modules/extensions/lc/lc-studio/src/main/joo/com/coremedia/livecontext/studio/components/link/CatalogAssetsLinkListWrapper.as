package com.coremedia.livecontext.studio.components.link {
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.editor.sdk.util.*;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CatalogAssetsLinkListWrapper implements ILinkListWrapper {

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var linksVE:ValueExpression;

  [Bindable]
  public var assetContentTypes:Array;

  [Bindable]
  public var maxCardinality:int;

  public function CatalogAssetsLinkListWrapper(config:CatalogAssetsLinkListWrapper = null) {
    bindTo = config.bindTo;
    linksVE = config.linksVE;
    assetContentTypes = config.assetContentTypes || [];
    maxCardinality = config.maxCardinality || 0;
  }

  public function getVE():ValueExpression {
    return linksVE;
  }

  public function acceptsLinks(links:Array):Boolean {
    for each (var asset:Content in links) {
      if (!(asset is Content)) {
        return false;
      }

      //check the content type
      var typeAccepted:Boolean = assetContentTypes.some(function(assetContentType:String):Boolean {
        return LinkListUtil.containsContentNotMatchingType(getContentType(assetContentType), [asset]) === null;
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

  public function getLinks():Array {
    var value:* = linksVE.getValue();
    return value === undefined ? undefined : value as Array;
  }

  public function setLinks(links:Array):void {
    if (links) {
      for each (var content:Content in links) {
        if (!PropertyEditorUtil.isReadOnly(content)) {
          CatalogHelper.getInstance().createOrUpdateProductListStructs(ValueExpressionFactory.createFromValue(content),
                  bindTo.getValue());
        }
      }
    }
  }

  public function getTotalCapacity():int {
    return maxCardinality > 0 ? maxCardinality : int.MAX_VALUE;
  }

  public function getFreeCapacity():int {
    return getTotalCapacity() - getLinks().length;
  }

  private static function getContentType(linkTypeName:String):ContentType {
    return SESSION.getConnection().getContentRepository().getContentType(linkTypeName);
  }

}
}
