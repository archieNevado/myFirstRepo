import { createDefaultCKEditor } from "./ckeditorDefault";
import ClassicEditor from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
import CKEditorTypes from "@coremedia/studio-client.ckeditor-constants/CKEditorTypes";
import LocaleUtil from "@coremedia/studio-client.cap-base-models/locale/LocaleUtil";

export type CKEditorPluginConfig = {
  autosave: {
    save: () => Promise<unknown>;
  }
}

export type CKEditorInitProps = {
  type?: string;
  language?: string;
  placeholderText?: string;
  pluginConfig?: CKEditorPluginConfig;
}

const defaultPluginConfig: CKEditorPluginConfig = {
    autosave: {
      save: () => new Promise<unknown>(resolve => { resolve(null)})
  }
}

export default function init({pluginConfig, type = CKEditorTypes.DEFAULT_EDITOR_TYPE, placeholderText, language = LocaleUtil.getLocale()}: CKEditorInitProps):(domElement:HTMLElement) => Promise<ClassicEditor> {
  const mergedConfig = Object.assign({}, defaultPluginConfig, pluginConfig);
  switch (type) {
    case CKEditorTypes.DEFAULT_EDITOR_TYPE:
      return (domElement) => createDefaultCKEditor(domElement, language, placeholderText, mergedConfig);
    default:
      throw new Error(`There is no CKEditor 5 build of type '${type}'. Please add a corresponding ckeditor configuration in your blueprint.`);
  }
};
