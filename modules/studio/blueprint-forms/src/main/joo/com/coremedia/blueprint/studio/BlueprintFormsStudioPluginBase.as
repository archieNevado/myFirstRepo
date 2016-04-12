package com.coremedia.blueprint.studio {

import com.coremedia.blueprint.base.components.util.UserUtil;
import com.coremedia.blueprint.studio.config.blueprintFormsStudioPlugin;
import com.coremedia.blueprint.studio.plugins.SiteAwareVisibilityPluginBase;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.plugins.TabExpandPlugin;
import com.coremedia.cms.editor.sdk.util.ImageLinkListRenderer;

public class BlueprintFormsStudioPluginBase extends StudioPlugin {
  public function BlueprintFormsStudioPluginBase(config:blueprintFormsStudioPlugin = null) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    //caches the available users and groups
    UserUtil.init();
    
    // Register Navigation Parent for CMChannel
    CMChannelExtension.register();

    //Enable advanced tabs
    TabExpandPlugin.ADVANCED_TABS_ENABLED = true;

    SiteAwareVisibilityPluginBase.preLoadConfiguration();

    ContentInitializer.applyInitializers();

    editorContext.registerThumbnailUriRenderer("CMSelectionRules", renderCMSelectionRules);
    editorContext.registerThumbnailUriRenderer("CMCollection", renderCMCollections);
    editorContext.registerThumbnailUriRenderer("CMTeasable", renderCMTeasable);
    editorContext.registerThumbnailUriRenderer("CMPicture", renderPicture);
    editorContext.registerThumbnailUriRenderer("CMImage", renderPicture);
    editorContext.registerThumbnailUriRenderer("CMSymbol", renderSymbol);
    editorContext.registerThumbnailUriRenderer("CMSpinner", renderSpinner);
  }


  private static function renderSpinner(content:Content):String {
    return ImageLinkListRenderer.propertyPathLoader(content, 'properties.pictures', 'properties.sequence');
  }

  private static function renderCMCollections(content:Content):String {
    return ImageLinkListRenderer.propertyPathLoader(content, 'properties.pictures', 'properties.items');
  }

  private static function renderCMSelectionRules(content:Content):String {
    return ImageLinkListRenderer.propertyPathLoader(content, 'properties.defaultContent');
  }

  private static function renderCMTeasable(content:Content):String {
    return ImageLinkListRenderer.propertyPathLoader(content, 'properties.pictures');
  }

  private static function renderPicture(content:Content):String {
    return ImageLinkListRenderer.blobPropertyUriResolver(content, 'data');
  }

  private static function renderSymbol(content:Content):String {
    return ImageLinkListRenderer.blobPropertyUriResolver(content, 'icon');
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

}
}
