import initEditor, { CKEditorInitProps, CKEditorPluginConfig } from "@coremedia-blueprint/studio-client.main.ckeditor5";
import { serviceAgent } from "@coremedia/service-agent";
import CKEditor5Wrapper from "@coremedia/studio-client.ckeditor-base/CKEditor5Wrapper";
import CKEditorTypes from "@coremedia/studio-client.ckeditor-constants/CKEditorTypes";
import ckEditorFactory from "@coremedia/studio-client.ckeditor-factory/util/ckEditorFactory";
import StudioContentDisplayService
  from "@coremedia/studio-client.ext.ckeditor5-services-toolkit/StudioContentDisplayService";
import StudioRichtextConfigurationService
  from "@coremedia/studio-client.ext.ckeditor5-services-toolkit/StudioRichtextConfigurationService";
import richTextAreaFactory
  from "@coremedia/studio-client.ext.richtext-components-toolkit/richtextArea/richTextAreaFactory";
import CKEditor5RichTextArea from "@coremedia/studio-client.ext.ckeditor5-components/CKEditor5RichTextArea";
import Config from "@jangaroo/runtime/Config";
import RichTextAreaConstants from "@coremedia/studio-client.ckeditor-constants/RichTextAreaConstants";

/*
 * WARNING: This package is highly experimental and will probably change or be removed in future releases.
 * Please use for CKEditor 5 preview purposes only and do not rely on any experimental CKEditor Studio API.
 * @experimental
 */
const service = new StudioContentDisplayService();
serviceAgent.registerService(service);

const richtextConfigurationService = new StudioRichtextConfigurationService();
serviceAgent.registerService(richtextConfigurationService);

/**
 * Register a wrapper for the default editor type
 */
ckEditorFactory.registerConstructor(CKEditorTypes.DEFAULT_EDITOR_TYPE, (editorType, pluginConfig, placeholderText, language) => {
  const pluginConfigConverted: CKEditorPluginConfig = pluginConfig as CKEditorPluginConfig;
  return new CKEditor5Wrapper(initEditor({type: editorType, pluginConfig: pluginConfigConverted, language, placeholderText}));
}, 5);
richTextAreaFactory.registerRichTextArea(RichTextAreaConstants.CKE5_EDITOR, Config(CKEditor5RichTextArea, {}))
