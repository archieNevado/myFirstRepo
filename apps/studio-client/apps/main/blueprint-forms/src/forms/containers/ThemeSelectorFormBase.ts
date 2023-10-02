import ImageUtil from "@coremedia/studio-client.cap-base-models/util/ImageUtil";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import ContentLocalizationUtil from "@coremedia/studio-client.main.bpbase-studio-components/localization/ContentLocalizationUtil";
import ContentLookupUtil from "@coremedia/studio-client.main.bpbase-studio-components/util/ContentLookupUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ComboBoxLinkPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/ComboBoxLinkPropertyField";
import ComboBoxLinkPropertyFieldBase from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/ComboBoxLinkPropertyFieldBase";
import Template from "@jangaroo/ext-ts/Template";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ThemeSelector_properties from "../../ThemeSelector_properties";
import ThemeSelectorForm from "./ThemeSelectorForm";

interface ThemeSelectorFormBaseConfig extends Config<ComboBoxLinkPropertyField> {
}

class ThemeSelectorFormBase extends ComboBoxLinkPropertyField {
  declare Config: ThemeSelectorFormBaseConfig;

  static readonly #NO_IMAGE_TOOLTIP: string = ThemeSelector_properties.ThemeSelector_no_image_tooltip;

  static readonly DEFAULT_PATHS: Array<any> = ["/Themes/"];

  static DEFAULT_CROPPING: string = ImageUtil.getCroppingOperation(82, 50);

  protected static readonly DISPLAY_FIELD_NAME: string = "titleUnencoded";

  protected static readonly TITLE_FIELD_NAME: string = "title";

  protected static readonly DESCRIPTION_FIELD_NAME: string = "description";

  protected static readonly THUMBNAIL_URI_FIELD_NAME: string = "thumbnailUri";

  protected static readonly THUMBNAIL_TOOLTIP_FIELD_NAME: string = "thumbnailTooltip";

  protected static readonly COMBO_BOX_TEMPLATE: Template = ComboBoxLinkPropertyFieldBase.getExtendedComboBoxTpl(ThemeSelectorFormBase.TITLE_FIELD_NAME, ThemeSelectorFormBase.DESCRIPTION_FIELD_NAME, ThemeSelectorFormBase.THUMBNAIL_URI_FIELD_NAME, ThemeSelectorFormBase.THUMBNAIL_TOOLTIP_FIELD_NAME, null);

  protected static readonly DISPLAY_TEMPLATE: Template = ComboBoxLinkPropertyFieldBase.getExtendedDisplayTpl(ThemeSelectorFormBase.TITLE_FIELD_NAME, ThemeSelectorFormBase.DESCRIPTION_FIELD_NAME, ThemeSelectorFormBase.THUMBNAIL_URI_FIELD_NAME, ThemeSelectorFormBase.THUMBNAIL_TOOLTIP_FIELD_NAME, null);

  constructor(config: Config<ThemeSelectorForm> = null) {
    super(config);
  }

  static createAvailableThemesValueExpression(config: Config<ThemeSelectorForm>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(ThemeSelectorFormBase.#computeAvailableLayouts, config);
  }

  static #computeAvailableLayouts(config: Config<ThemeSelectorForm>): Array<any> {
    const paths = config.themesFolderPaths;
    let themeFolders = [];
    for (const path of paths as string[]) {
      const baseFolder = session._.getConnection().getContentRepository().getChild(path);
      if (baseFolder === undefined) {
        themeFolders = undefined;
      } else if (baseFolder.isFolder()) {
        const subFolders = baseFolder.getSubFolders();
        if (subFolders === undefined) {
          themeFolders = undefined;
        }
        for (const folder of subFolders as Content[]) {
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
  static localizeText(content: Content): string {
    return ContentLocalizationUtil.localize(content, "ThemeSelector_default_text", resourceManager.getResourceBundle(null, ThemeSelector_properties).content, ThemeSelectorFormBase.#getName);
  }

  static #getName(content: Content): string {
    return content === null ? "" : content.getName();
  }

  /**
   * Localization of the theme description. The description is looked
   * up in the ThemeSelector_properties resource bundle.
   * The method returns undefined if the return value cannot be determined yet.
   *
   * @param content the content identifying the theme
   * @return the formatted description
   */
  static localizeDescription(content: Content): string {
    return ContentLocalizationUtil.localize(content, "ThemeSelector_default_description", resourceManager.getResourceBundle(null, ThemeSelector_properties).content, null, ThemeSelectorFormBase.#getContentDescription);
  }

  static #getContentDescription(content: Content): any {
    return content
      ? ObjectUtils.getPropertyAt(content, [ContentPropertyNames.PROPERTIES, "description"])
      : null;
  }

  static getThumbnailUri(content: Content): string {
    return content === null ? "" : editorContext._.getThumbnailUri(content, ThemeSelectorFormBase.DEFAULT_CROPPING);
  }

  static getThumbnailTooltip(content: Content): string {
    return ThemeSelectorFormBase.getThumbnailUri(content) ? ThemeSelectorFormBase.#getName(content) : ThemeSelectorFormBase.#NO_IMAGE_TOOLTIP;
  }
}

export default ThemeSelectorFormBase;
