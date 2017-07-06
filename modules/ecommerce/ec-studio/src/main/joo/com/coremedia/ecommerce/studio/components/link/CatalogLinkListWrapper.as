package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.*;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.BeanState;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.logging.Logger;

public class CatalogLinkListWrapper implements ILinkListWrapper {

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var propertyName:String;

  [Bindable]
  public var linkTypeNames:Array;

  [Bindable]
  public var maxCardinality:int;

  [Bindable]
  public var model:Bean;

  [Bindable]
  public var createStructFunction:Function;

  private var ve:ValueExpression;
  private var propertyExpression:ValueExpression;
  private var catalogObjRemoteBean:RemoteBean;

  public function CatalogLinkListWrapper(config:CatalogLinkListWrapper = null):void {
    super(config);
    bindTo = config.bindTo;
    propertyName = config.propertyName;
    linkTypeNames = config.linkTypeNames;
    maxCardinality = config.maxCardinality || 0;
    model = config.model;
    createStructFunction = config.createStructFunction;
  }

  public function getVE():ValueExpression {
    if (!ve) {
      ve = ValueExpressionFactory.createTransformingValueExpression(getPropertyExpression(),
              transformer, reverseTransformer, []);
    }
    return ve;
  }

  private function invalidateIssues(event:PropertyChangeEvent):void {
    if (event.newState === BeanState.NON_EXISTENT || event.oldState === BeanState.NON_EXISTENT) {
      var content:Content = bindTo.getValue() as Content;
      if (content) {
        if (content.getIssues()) {
          content.getIssues().invalidate();
        }
      }
    }
  }

  protected function getPropertyExpression():ValueExpression {
    if (!propertyExpression) {
      if (bindTo) {
        if (bindTo.getValue() is Content) {
          propertyExpression = bindTo.extendBy('properties').extendBy(propertyName);
        } else {
          propertyExpression = bindTo.extendBy(propertyName);
        }
      } else {
        propertyExpression = ValueExpressionFactory.create(propertyName, model);
      }
    }
    return propertyExpression;
  }

  public function getTotalCapacity():int {
    return maxCardinality > 0 ? maxCardinality : int.MAX_VALUE;
  }

  public function getFreeCapacity():int {
    if (!maxCardinality) {
      return int.MAX_VALUE;
    }
    var catalogItems:Array = getVE().getValue() as Array;
    return maxCardinality - catalogItems.length;
  }

  public function acceptsLinks(links:Array):Boolean {
    return links.every(function(link:Object):Boolean {
      var catalogObject:CatalogObject = getCatalogObject(link);
      if (!catalogObject) {
        return false;
      }
      if (catalogObject.getSiteId() !== getSiteId()) {
        return false;
      }

      return linkTypeNames.some(function(linkTypeName:String):Boolean {
        return CatalogHelper.getInstance().isSubType(catalogObject, linkTypeName);
      });
    });
  }

  private function getSiteId():String {
    var siteId:String;
    if (bindTo) {
      var content:Content = bindTo.getValue() as Content;
      if (content) {
        siteId = editorContext.getSitesService().getSiteFor(content).getId();
      }
    } else {
      //no content there. so let's take the preferred site
      var preferredSite:Site = editorContext.getSitesService().getPreferredSite();
      if (preferredSite) {
        siteId = preferredSite.getId();
      }
    }
    return siteId;
  }

  private function getCatalogObject(link:Object):CatalogObject {
    var catalogObject:CatalogObject = link as CatalogObject;
    if (!catalogObject) {
      var content:Content = link as Content;
      if (content) {
        catalogObject = augmentationService.getCatalogObject(content);
      }
    }
    return catalogObject;
  }

  public function getLinks():Array {
    return getVE().getValue();
  }

  public function setLinks(links:Array):void {
    if (createStructFunction) {
      createStructFunction.apply();
    }
    var myLinks:Array = links.map(getCatalogObject);
    //are some links yet not loaded?
    var notLoadedLinks:Array = myLinks.filter(function(myLink:CatalogObject):Boolean {
      return !myLink.isLoaded();
    });
    if (!notLoadedLinks || notLoadedLinks.length === 0) {
      getVE().setValue(myLinks);
    } else {
      notLoadedLinks.every(function(notLoadedLink:CatalogObject):void {
        notLoadedLink.load(function():void {
          setLinks(myLinks);
        })
      });
    }
  }

  private function transformer(value:*):Array {
    var valuesArray:Array = [];
    if (value) {
      //the value can be a string or a catalog object bean
      if (value is String || value is CatalogObject) {
        //this is a single catalog object stored
        valuesArray = [value];
      } else if (value is Array) {
        //this are multiple catalog objects stored in an array
        valuesArray = value;
      }
    }

    return valuesArray.map(function (value:*):CatalogObject {
      //the value can be a string or a catalog object bean
      var catalogObject:CatalogObject;

      if (value is CatalogObject) {
        catalogObject = value;
      } else if (value is String) {
        catalogObject = CatalogHelper.getInstance().getCatalogObject(value, bindTo) as CatalogObject;
      } else {
        Logger.error("CatalogLink does not accept the value: " + value);
      }

      if (catalogObject === undefined) {
        return undefined;
      }

      if (catalogObject !== catalogObjRemoteBean) {
        if (catalogObjRemoteBean) {
          catalogObjRemoteBean.removePropertyChangeListener(BeanState.PROPERTY_NAME, invalidateIssues);
        }
        catalogObjRemoteBean = catalogObject;
        if (catalogObject) {
          catalogObjRemoteBean.addPropertyChangeListener(BeanState.PROPERTY_NAME, invalidateIssues);
        }
      }
      return catalogObject;
    });
  }

  private function reverseTransformer(value:Array):* {
    if (value) {
      if (maxCardinality === 1) {
        return CatalogObject(value[0]).getId();
      } else {
        return value.map(function (bean:CatalogObject):String {
          return bean.getId();
        })
      }
    }
    return value;
  }

}
}
