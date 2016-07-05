package com.coremedia.ecommerce.studio.model {
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;

[RestResource(uriTemplate="livecontext/store/{siteId:[^/]+}/{workspaceId:[^/]+}")]
public class StoreImpl extends CatalogObjectImpl implements Store {
  private var siteId:String;
  private var workspaceId:String;
  private var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod("livecontext/urlService", "POST", true, true);

  private const resolvedUrls:Object = {};

  public function StoreImpl(uri:String, vars:Object) {
    siteId = vars['siteId'];
    workspaceId = vars['workspaceId'];
    super(uri);
  }

  public function getChildrenByName():Object {
    return get(CatalogObjectPropertyNames.CHILDREN_BY_NAME);
  }

  public function getStoreId():String {
    return get(CatalogObjectPropertyNames.STORE_ID);
  }

  override public function getSiteId():String {
    return siteId;
  }

  public function getTopLevel():Array {
    return get("topLevel");
  }


  public function getMarketing():Marketing {
    return get("marketing");

  }

  public function getSegments():Segments {
    return get(CatalogObjectPropertyNames.SEGMENTS);
  }

  public function getContracts():Contracts {
    return get(CatalogObjectPropertyNames.CONTRACTS);
  }

  public function getWorkspaces():Workspaces {
    return get(CatalogObjectPropertyNames.WORKSPACES);
  }


  public function getCurrentWorkspace():Workspace {
    if (!workspaceId || workspaceId === CatalogModel.NO_WS) {
      return undefined;
    }
    if (!getWorkspaces()) {
      return undefined;
    }
    if (!getWorkspaces().getWorkspaces()) {
      return undefined;
    }

    var workspaces:Array = getWorkspaces().getWorkspaces();

    var filtered:Array = workspaces.filter(function (workspace:Workspace):Boolean {
      return workspaceId === workspace.getExternalTechId();
    });

    return filtered.length > 0 ? filtered[0] : null;
  }

  public function getRootCategory():Category {
    return get(CatalogObjectPropertyNames.ROOT_CATEGORY);
  }

  public function getVendorUrl():String {
    return get(CatalogObjectPropertyNames.VENDOR_URL);
  }

  public function getVendorVersion():String {
    return get(CatalogObjectPropertyNames.VENDOR_VERSION);
  }

  public function getWcsTimeZone():Object {
    return get("wcsTimeZone");
  }

  public function getVendorName():String {
    return get(CatalogObjectPropertyNames.VENDOR_NAME);
  }

  override public function getStore():Store {
    return this;
  }

  public function resolveShopUrlForPbe(shopUrl:String):RemoteBean {
    var resolvedUrl:Object = resolvedUrls[shopUrl];
    var ve:ValueExpression;
    if(undefined === resolvedUrl) {
      ve = ValueExpressionFactory.createFromValue();
      resolvedUrl = {at : new Date(), ve : ve};
    } else {
      ve = resolvedUrl.ve;
    }
    if(undefined === resolvedUrls[shopUrl] || resolvedUrl.at < new Date() - 5000) {
      resolvedUrls[shopUrl] = resolvedUrl;
      remoteServiceMethod.request({shopUrl: shopUrl, siteId: getSiteId()},
              function (response:RemoteServiceMethodResponse):void {
                var bean:* = response.getResponseJSON().bean;
                ve.setValue(bean);
              });
    }
    return ve.getValue();
  }
}
}