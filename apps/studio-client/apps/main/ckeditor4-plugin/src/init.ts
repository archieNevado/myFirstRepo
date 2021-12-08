import CKEditorTypes from "@coremedia/studio-client.ckeditor-constants/CKEditorTypes";
import ckEditorFactory from "@coremedia/studio-client.ckeditor-factory/util/ckEditorFactory";
import CKEditor4Wrapper from "@coremedia/studio-client.main.ckeditor4-components/util/CKEditor4Wrapper";

ckEditorFactory.registerConstructor(CKEditorTypes.DEFAULT_EDITOR_TYPE, editorType => {
  return new CKEditor4Wrapper();
}, 4);

ckEditorFactory.registerConstructor(CKEditorTypes.NO_TOOLBAR_EDITOR_TYPE, editorType => {
  return new CKEditor4Wrapper();
}, 4);
