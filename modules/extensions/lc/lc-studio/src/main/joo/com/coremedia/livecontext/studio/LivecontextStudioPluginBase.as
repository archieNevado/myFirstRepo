package com.coremedia.livecontext.studio {

import com.coremedia.blueprint.base.components.quickcreate.QuickCreate;
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.blueprint.studio.CMChannelExtension;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructType;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.preview.PreviewURI;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.ThumbnailResolverFactory;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.components.link.QuickCreateCatalogLink;
import com.coremedia.ecommerce.studio.config.quickCreateCatalogLink;
import com.coremedia.ecommerce.studio.helper.AugmentationUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.livecontext.studio.library.LivecontextCollectionViewExtension;
import com.coremedia.livecontext.studio.library.ShowInCatalogTreeHelper;
import com.coremedia.livecontext.studio.pbe.StoreNodeRenderer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.UrlUtil;

import ext.Component;
import ext.ComponentMgr;
import ext.config.container;

public class LivecontextStudioPluginBase extends StudioPlugin {

  internal static const CONTENT_LED_PROPERTY:String = 'livecontext.contentLed';
  internal static const EXTERNAL_ID_PROPERTY:String = 'externalId';

  public function LivecontextStudioPluginBase(config:livecontextStudioPlugin = null) {
    if (UrlUtil.getHashParam('livecontext') === 'false') {
      delete config['rules'];
      delete config['configuration'];
    }
    super(config)
  }


  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);
    editorContext.getCollectionViewExtender().addExtension(new LivecontextCollectionViewExtension());

    //forward the workspaceId (configured by the hash param) to the preview url.
    editorContext.registerPreviewUrlTransformer(function (uri:PreviewURI, callback:Function):void {
      var workspaceId:String = CatalogHelper.getInstance().getExtractedWorkspaceId();
      if (workspaceId && workspaceId !== CatalogHelper.NO_WS) {
        uri.appendParameter("workspaceId", workspaceId);
      }
      callback.call(null);
    });

    /**
     * Apply image link list preview
     */
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE, "pictures"));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT));
    editorContext.registerThumbnailResolver(new CatalogTeaserThumbnailResolver(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, "pictures"));


    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_CATEGORY));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_MARKETING_SPOT));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT));
    editorContext.registerThumbnailResolver(new CatalogThumbnailResolver(CatalogModel.TYPE_PRODUCT_VARIANT));

    /**
     * Register Content initializer
     */
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT, initMarketingSpot);
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, initProductTeaser);
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_CHANNEL, initExternalChannel);
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE, initExternalPage);

    editorContext['getMetadataNodeRendererRegistry']().register(new StoreNodeRenderer());

    /**
     * Extend Content initializer
     */
    editorContext.extendContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_IMAGE_MAP, extendImageMap);

    /**
     * apply the marketing spot link field to CMMarketingSpot quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT, EXTERNAL_ID_PROPERTY,
            function (data:ProcessingData, properties:Object):Component {
              properties.openLinkSources = CatalogHelper.getInstance().openMarketingSpots;
              properties.catalogObjectType = CatalogModel.TYPE_MARKETING_SPOT;
              properties.emptyText = LivecontextStudioPlugin_properties.INSTANCE.MarketingSpot_Link_empty_text;
              var myCatalogLink:QuickCreateCatalogLink = new QuickCreateCatalogLink(quickCreateCatalogLink(properties));
              var containerCfg:container = new container();
              containerCfg.cls = 'link-list-wrapper-hbox-layout';
              containerCfg.fieldLabel = properties.label;
              containerCfg.autoWidth = true;
              containerCfg.items = [myCatalogLink];
              return ComponentMgr.create(containerCfg);
            });


    /**
     * apply the product link field to CMProductTeaser quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, EXTERNAL_ID_PROPERTY,
            function (data:ProcessingData, properties:Object):Component {
              properties.openLinkSources = CatalogHelper.getInstance().openCatalog;
              properties.catalogObjectType = CatalogModel.TYPE_PRODUCT;
              properties.emptyText = LivecontextStudioPlugin_properties.INSTANCE.Product_Link_empty_text;
              var myCatalogLink:QuickCreateCatalogLink = new QuickCreateCatalogLink(quickCreateCatalogLink(properties));
              var containerCfg:container = new container();
              containerCfg.cls = 'link-list-wrapper-hbox-layout';
              containerCfg.fieldLabel = properties.label;
              containerCfg.autoWidth = true;
              containerCfg.items = [myCatalogLink];
              return ComponentMgr.create(containerCfg);
            });

    CMChannelExtension.register(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE);
  }

  private static function initProductTeaser(content:Content):void {
    //don't initialize the teaser title for product teasers
    //they will inherit the teaser title form the linked product
    //setProperty(content, 'teaserTitle', content.getName());
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function initMarketingSpot(content:Content):void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function initExternalPage(content:Content):void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function initExternalChannel(content:Content):void {
    ContentInitializer.initializePropertyWithName(content, 'title');
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function extendImageMap(content:Content):void {
    var localSettings:Struct = content.getProperties().get('localSettings');
    var overlay:* = localSettings.get("overlay");
    if (overlay) {
      overlay.set("displayDefaultPrice", true);
    }
  }

  //noinspection JSUnusedGlobalSymbols
  internal function getShopExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var store:Store = Store(CatalogHelper.getInstance().getActiveStoreExpression().getValue());
      return store && store.getName();
    });
  }

  internal function isWorkspaceEnabledExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var workspaces:Array;
      var activeStore:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
      if (activeStore) {
        if (activeStore.getWorkspaces()) {
          workspaces = activeStore.getWorkspaces().getWorkspaces();
        }
      }
      return workspaces && workspaces.length > 0;
    });
  }

  static internal function reloadPreview(previewPanel:PreviewPanel):void {
    if (previewPanel.rendered) {
      previewPanel.reloadFrame();
    }
  }

  internal static function showInCatalogTree(entity:CatalogObject):void {
    new ShowInCatalogTreeHelper([entity]).showInCatalogTree();
  }

  /////////
  // Utility functions extracted from CatalogHelper
  /////////
  /**
   * Checks if the given category (either content or catalog object) is a part of a site
   * configured for the content led scenario (property 'livecontext.contentLed' in the
   * LiveContext settings document for the Site).
   * @param bindTo
   * @return
   */
  public static function isContentLedValueExpression(bindTo:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():Boolean {
      var catalogObjectValueExpression:ValueExpression = AugmentationUtil.toCatalogObjectExpression(bindTo);
      var catalogObject:CatalogObject = catalogObjectValueExpression.getValue() as CatalogObject;

      if(!catalogObject || !catalogObject.getStore()) {
        return undefined;
      }
      var siteId:String = catalogObject.getStore().getSiteId();
      if (!siteId) {
        return undefined;
      }
      var site:Site = editorContext.getSitesService().getSite(siteId);

      if (site === undefined) {
        return undefined;
      }
      if (site.getName() === undefined || site.getLocale() === undefined || site.getLocale().getDisplayName() === undefined) {
        return undefined;
      }
      var rootFolder:Content = site.getSiteRootFolder();
      var liveContextSettings:Content = rootFolder.getChild("Options/Settings/LiveContext");
      if (liveContextSettings === undefined) {
        return undefined;
      }
      if (liveContextSettings !== null) {
        var liveContextSettingsProperties:ContentProperties = liveContextSettings.getProperties();
        if (liveContextSettingsProperties === undefined) {
          return undefined;
        }
        var liveContextStruct:Struct = liveContextSettingsProperties.get("settings") as Struct;
        if (liveContextStruct) {
          var structType:StructType = liveContextStruct.getType();
          if (structType === undefined) {
            return undefined;
          }
          if (structType.hasProperty(CONTENT_LED_PROPERTY)) {
            return liveContextStruct.get(CONTENT_LED_PROPERTY);
          }
        }
      }
      return false;
    });
  }
}
}