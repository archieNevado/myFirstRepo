package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.blueprint.base.components.localization.ContentLocalizationUtil;
import com.coremedia.blueprint.base.components.util.ContentLookupUtil;
import com.coremedia.blueprint.base.components.viewtypes.Viewtypes_properties;
import com.coremedia.blueprint.studio.ThemeSelector_properties;
import com.coremedia.blueprint.studio.config.themeSelectorForm;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.fields.ComboBoxLinkPropertyField;
import com.coremedia.cms.editor.sdk.util.ImageUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EncodingUtil;
import com.coremedia.ui.util.ObjectUtils;

import ext.Ext;

use namespace editorContext;

use namespace Ext;

use namespace session;

public class ThemeSelectorFormBase extends ComboBoxLinkPropertyField {

  public static const DEFAULT_PATHS:Array = ['/Themes/'];

  public static var DEFAULT_CROPPING:String = ImageUtil.getCroppingOperation(82, 50);


  // Caution: The display field must not be encoded. Make sure to encode it when using it in a template.
  internal static const FULL_LAYOUTS_INFO_TEMPLATE:Array = [
    '<tpl for=".">',
    '<div class="x-menu-item x-combo-list-item multipath-theme-combo">',
    '<table border="0" class="text-normal-60">',
    '<tr>',
    '<th class="theme-header-image" rowspan="2">',
    '<tpl if="iconUri"><img src="{iconUri}"/></tpl>',
    '<tpl if="!iconUri"><img src="', Ext.BLANK_IMAGE_URL, '" class="theme-icon-warning" ext:qtip="' + EncodingUtil.encodeForHTML(Viewtypes_properties.INSTANCE.no_image) + '"/></tpl>',
    '</th>',
    '<th align="left" valign="top" class="theme-header-title">',
    '<span class="text-bold">{displayName}</span>',
    '</th>',
    '</tr>',
    '<tr>',
    '<td class="theme-text">',
    '<span>{description}</span>',
    '</td>',
    '</table>',
    '</div>',
    '</tpl>'
  ];

  public function ThemeSelectorFormBase(config:themeSelectorForm = null) {
    super(config);
  }

  internal static function createAvailableThemesValueExpression(config:themeSelectorForm):ValueExpression {
    return ValueExpressionFactory.createFromFunction(computeAvailableLayouts, config);
  }

  private static function computeAvailableLayouts(config:themeSelectorForm):Array {
    var paths:Array = config.themesFolderPaths;
    var themeFolders:Array = [];
    for each(var path:String in paths) {
      var baseFolder:Content = session.getConnection().getContentRepository().getChild(path);
      if (baseFolder === undefined) {
        themeFolders = undefined;
      }
      else if (baseFolder.isFolder()) {
        var subFolders:Array = baseFolder.getSubFolders();
        if (subFolders === undefined) {
          themeFolders = undefined;
        }
        for each(var folder:Content in subFolders) {
          if (folder === undefined) {
            themeFolders = undefined;
          } else if (folder.getPath() === undefined) {
            themeFolders = undefined;
          } else if (themeFolders) {
            themeFolders.push(folder.getPath());
          }
        }
      }
    }
    if (themeFolders === undefined) {
      return undefined;
    }
    //concat null to allow resetting
    return [null].concat(ContentLookupUtil.findContentsOfTypeInPaths(themeFolders, ["CMTheme"], config.bindTo.getValue()));
  }

  /**
   * Localization of the theme name. The display name is looked
   * up in the ThemeSelector_properties resource bundle.
   * The method returns undefined if the return value cannot be determined yet.
   * The value of the layout property (if set) takes precedence over
   * the content name for the purposes of localization.
   *
   * @param content the content identifying the theme
   * @return the formatted display name
   */
  public static function localizeText(content:Content):String {
    return ContentLocalizationUtil.localize(content, 'ThemeSelector_default_text', ThemeSelector_properties.INSTANCE, getName);
  }

  private static function getName(content:Content):String {
    return content == null ? "" : content.getName();
  }

  /**
   * Localization of the theme description. The description is looked
   * up in the ThemeSelector_properties resource bundle.
   * The method returns undefined if the return value cannot be determined yet.
   *
   * @param content the content identifying the theme
   * @return the formatted description
   */
  public static function localizeDescription(content:Content):String {
    return ContentLocalizationUtil.localize(content, 'ThemeSelector_default_description', ThemeSelector_properties.INSTANCE, null, getContentDescription);
  }

  private static function getContentDescription(content:Content):Object {
    return content
            ? ObjectUtils.getPropertyAt(content, [ContentPropertyNames.PROPERTIES, 'description'])
            : null;
  }

  public static function getIconUri(theme:Content):String {
    return theme == null ? "" : editorContext.getThumbnailUri(theme, DEFAULT_CROPPING);
  }

}
}
