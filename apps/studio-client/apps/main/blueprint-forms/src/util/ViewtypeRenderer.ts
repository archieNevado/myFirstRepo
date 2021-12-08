import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import QtipUtil from "@coremedia/studio-client.ext.ui-components/util/QtipUtil";
import ViewtypeLocalizationUtil from "@coremedia/studio-client.main.bpbase-studio-components/viewtypes/ViewtypeLocalizationUtil";
import Base from "@jangaroo/ext-ts/Base";

class ViewtypeRenderer extends Base {

  static readonly #BLOCK: BEMBlock = new BEMBlock("cm-viewtype-thumb");

  static convert(value: string, content: Content): any {
    if (content) {
      const hasProperty = ViewtypeRenderer.#hasContentProperty(content, "viewtype");
      if (hasProperty === undefined) {
        return undefined;
      }
      if (hasProperty) {
        const viewtypes: Array<any> = ObjectUtils.getPropertyAt(content, "properties.viewtype", null);
        if (viewtypes === undefined) {
          return undefined;
        }
        if (viewtypes && viewtypes.length > 0) {
          const viewType: Content = viewtypes[0];
          const thumbUri: string = ObjectUtils.getPropertyAt(viewType, "properties.icon.uri", null);
          const viewTypeName = ViewtypeLocalizationUtil.localizeText(viewType);
          if (thumbUri === undefined) {
            return undefined;
          }
          if (viewTypeName === undefined) {
            return undefined;
          }
          return {
            url: thumbUri,
            viewTypeName: viewTypeName,
          };
        }
      }
    }
    return {
      url: "",
      viewTypeName: "",
    };
  }

  static renderer(value: any, metaData: any, record: BeanRecord): string {
    if (value) {
      const viewTypeName: string = value.viewTypeName;
      const thumbUri: string = value.url;
      if (viewTypeName && thumbUri) {
        return "<img " + QtipUtil.formatQtip(viewTypeName) + " src=\"" + thumbUri + "/s;w=32;h=22/rm\" loading=\"lazy\" width=\"32\" height=\"22\" class=\"" + ViewtypeRenderer.#BLOCK + "\"/>";
      }
    }
    return "<div style=\"width:32px;height:22px\"></div>"; // spacer
  }

  /**
   * Returns true if the given content has the property field with the give name.
   * Returns undefined if the content has not yet been loaded.
   *
   * @param content the content
   * @param name the name of the property
   * @return true if the given content has the property
   */
  static #hasContentProperty(content: Content, name: string): boolean {
    const contentType = content.getType();
    if (contentType === undefined) {
      return undefined;
    }
    const desc = contentType.getDescriptors();
    for (let i = 0; i < desc.length; i++) {
      if (desc[i].name === name) {
        return true;
      }
    }
    return false;
  }
}

export default ViewtypeRenderer;
