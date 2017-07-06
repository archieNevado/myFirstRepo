package com.coremedia.blueprint.studio {

import com.coremedia.blueprint.base.components.sites.SiteAwareVisibilityPluginBase;
import com.coremedia.blueprint.base.components.util.UserUtil;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.plugins.TabExpandPlugin;
import com.coremedia.cms.editor.sdk.util.ThumbnailResolverFactory;

public class BlueprintFormsStudioPluginBase extends StudioPlugin {
  public function BlueprintFormsStudioPluginBase(config:BlueprintFormsStudioPlugin = null) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    //caches the available users and groups
    UserUtil.init();

    // Register Navigation Parent for CMChannel
    CMChannelExtension.register(CMChannelExtension.CONTENT_TYPE_PAGE);

    //Enable advanced tabs
    TabExpandPlugin.ADVANCED_TABS_ENABLED = true;

    SiteAwareVisibilityPluginBase.preLoadConfiguration();

    ContentInitializer.applyInitializers();

    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSelectionRules", "defaultContent"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMCollection", "pictures", "items"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMTeasable", "pictures"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSpinner", "pictures", "sequence"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMDownload", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMImage", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMVideo", "pictures"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMPicture", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSymbol", "icon"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMTheme", "icon"));
  }

  /**
   * Used for columns setup:
   * Extends the sort by sorting by name.
   *
   * @param field the sortfield which is selected and should be extended
   * @param direction the sortdirection which is selected
   * @return array filled with additional order by statements
   */
  public static function extendOrderByName(field:String, direction:String):Array {
    var orderBys:Array = [];
    orderBys.push('name ' + direction);
    return orderBys;
  }

  /**
   * Prevents global downloads to be previewed.
   */
  protected function mayPreviewDownload(content:Content):Boolean {
    return editorContext.getSitesService().getSiteFor(content);
  }

}
}
