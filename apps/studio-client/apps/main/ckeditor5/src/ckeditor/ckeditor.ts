import { createDefaultCKEditor } from "./ckeditorDefault";
import ClassicEditor from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
import CKEditorTypes from "@coremedia/studio-client.ckeditor-constants/CKEditorTypes";


export default function init(type:string = CKEditorTypes.DEFAULT_EDITOR_TYPE):(domElement:HTMLElement) => Promise<ClassicEditor> {
  switch (type) {
    case CKEditorTypes.DEFAULT_EDITOR_TYPE:
      return (domElement) => createDefaultCKEditor(domElement);
    default:
      throw new Error(`There is no CKEditor 5 build of type '${type}'. Please add a corresponding ckeditor configuration in your blueprint.`);
  }
};
