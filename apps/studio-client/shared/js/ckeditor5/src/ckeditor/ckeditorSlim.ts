import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
//@ts-expect-error
import DocumentList from '@ckeditor/ckeditor5-list/src/documentlist';
import Differencing from "@coremedia/ckeditor5-coremedia-differencing/Differencing";
import CoreMediaStudioEssentials from "@coremedia/ckeditor5-coremedia-studio-essentials/CoreMediaStudioEssentials";
import CoreMediaFontMapper from '@coremedia/ckeditor5-font-mapper/FontMapper';

import { localization, localize } from "../lang/LocalizationUtils";
import LocaleUtil from "@coremedia/studio-client.cap-base-models/locale/LocaleUtil";
import { CKEditorPluginConfig, CreateCKEditorFunction } from "@coremedia/studio-client.ckeditor-common/CKEditorCreateFunctionType";
import '../theme/custom.css';
import AutoSave from "@ckeditor/ckeditor5-autosave/src/autosave";
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';

/**
 * Localization the editor configuration
 */
localization.add({
  "de": {
    "Type your text here...": "Text hier eingeben..."
  }
})

/**
 * This is the slim configuration for the CKEditor 5 in CoreMedia Studio.
 * Please be advised, that any misconfiguration of this editor might cause issues in CoreMedia Studio.
 *
 * If you want to add a custom configuration, you should create a separate module
 * and add the exported editor in {@link ckeditor.ts} accordingly.
 */
export const createSlimCKEditor: CreateCKEditorFunction = (domElement:(string | HTMLElement), pluginConfig: CKEditorPluginConfig): Promise<ClassicEditor> => {
  const defaultToolbarItems = [
    'undo',
    'redo',
    ];
  const language = LocaleUtil.getLocale();
  return ClassicEditor.create(domElement, {
    // Add License Key retrieved via CKEditor for Premium Features Support.
    licenseKey: '',
    placeholder: localize('Type your text here...', language),
    plugins: [
      AutoSave,
      //@ts-ignore
      CoreMediaStudioEssentials,
      //@ts-ignore
      CoreMediaFontMapper,
      //@ts-ignore
      Differencing,
      Essentials,
      Paragraph,
    ],
    toolbar: {
      items: defaultToolbarItems
    },
    language: language,
    autosave: {
      save: pluginConfig.autosave.save,
      waitingTime: 1000,
    }
  });
}
