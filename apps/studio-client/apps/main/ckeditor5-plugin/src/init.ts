import { createDefaultCKEditor, createSlimCKEditor, createHtmlViewerCKEditor } from "@coremedia-blueprint/studio-client.ckeditor5/";
import { serviceAgent } from "@coremedia/service-agent";
import CKEditorTypes from "@coremedia/studio-client.ckeditor-common/CKEditorTypes";
import RichTextAreaConstants from "@coremedia/studio-client.ckeditor-common/RichTextAreaConstants";
import CKEditor5RichTextArea from "@coremedia/studio-client.ext.ckeditor5-components/CKEditor5RichTextArea";
import StudioBlobDisplayService
  from "@coremedia/studio-client.ext.ckeditor5-services-toolkit/StudioBlobDisplayService";
import StudioContentDisplayService
  from "@coremedia/studio-client.ext.ckeditor5-services-toolkit/StudioContentDisplayService";
import StudioRichtextConfigurationService
  from "@coremedia/studio-client.ext.ckeditor5-services-toolkit/StudioRichtextConfigurationService";
import richTextAreaRegistry
  from "@coremedia/studio-client.ext.richtext-components-toolkit/richtextArea/richTextAreaRegistry";
import { PRIORITY_HIGHEST } from "@coremedia/studio-client.ext.richtext-components-toolkit/richtextPropertyField/IRichTextPropertyFieldRegistry";
import richTextPropertyFieldRegistry
  from "@coremedia/studio-client.ext.richtext-components-toolkit/richtextPropertyField/richTextPropertyFieldRegistry";
import RichTextPropertyField
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/richtext/RichTextPropertyField";
import Config from "@jangaroo/runtime/Config";

/**
 * The default width of embedded images, displayed in richText fields.
 */
const DEFAULT_EMBEDDED_IMAGE_MAX_WIDTH: number = 240;

/**
 * Services used for CKEditor 5
 */
const service = new StudioContentDisplayService();
serviceAgent.registerService(service);

const richTextConfigurationService = new StudioRichtextConfigurationService();
serviceAgent.registerService(richTextConfigurationService);

const studioBlobDisplayService = new StudioBlobDisplayService(DEFAULT_EMBEDDED_IMAGE_MAX_WIDTH);
serviceAgent.registerService(studioBlobDisplayService);

/**
 * Register RichTextAreas, representing different editor versions, in the {@link richTextAreaRegistry}.
 * If only CKEditor 5 is ued, there should be no need to register any further richTextAreas.
 *
 * There may be different editor configurations, that need to be available per editor version.
 * This can be done by adding the constructor functions of those configurations to the map
 * in the richTextArea config.
 */
richTextAreaRegistry.registerRichTextArea(RichTextAreaConstants.CKE5_EDITOR, Config(CKEditor5RichTextArea, {
  editorTypeMap: new Map([
    [CKEditorTypes.DEFAULT_EDITOR_TYPE, createDefaultCKEditor],
    [CKEditorTypes.NO_TOOLBAR_EDITOR_TYPE, createSlimCKEditor],
    [CKEditorTypes.HTML_VIEWER_EDITOR_TYPE, createHtmlViewerCKEditor],
  ]),
}));

/**
 * Registers the RichTextPropertyField for ckeditor 5 with the highest priority and therefore
 * overrides CKEditor 4 for core components.
 *
 * This mechanism will be removed with ckeditor 4 in the future.
 */
richTextPropertyFieldRegistry.registerRichTextPropertyFieldConfig(Config(RichTextPropertyField), PRIORITY_HIGHEST);
