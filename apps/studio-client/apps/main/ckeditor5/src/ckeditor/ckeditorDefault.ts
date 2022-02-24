import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import List from '@ckeditor/ckeditor5-list/src/list';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Link from '@ckeditor/ckeditor5-link/src/link';
import AutoLink from "@ckeditor/ckeditor5-link/src/autolink";
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import CoreMediaStudioEssentials from "@coremedia/ckeditor5-studio-essentials/CoreMediaStudioEssentials";
import CoreMediaSymbolOnPasteMapper from '@coremedia/ckeditor5-symbol-on-paste-mapper/SymbolOnPasteMapper';
import GeneralRichTextSupport from "@coremedia/ckeditor5-coremedia-richtext-support/GeneralRichTextSupport";

import Autosave from '@ckeditor/ckeditor5-autosave/src/autosave';
import { CKEditorPluginConfig } from "./ckeditor";

export function createDefaultCKEditor (domElement:(string | HTMLElement), language: string, placeholderText:string | undefined, pluginConfig: CKEditorPluginConfig): Promise<ClassicEditor> {

  const defaultToolbarItems = [
    'heading',
    '|',
    'bold',
    'italic',
    'link',
    'bulletedList',
    'numberedList',
    'blockQuote',
    'undo',
    'redo'
  ];

  return ClassicEditor.create(domElement, {
    // Add License Key retrieved via CKEditor for Premium Features Support.
    licenseKey: '',
    placeholder: placeholderText ? placeholderText : 'Type your text here...',
    plugins: [
      Essentials,
      Bold,
      Italic,
      BlockQuote,
      Heading,
      AutoLink,
      Link,
      Autosave,
      CoreMediaStudioEssentials,
      //@ts-expect-error
      CoreMediaSymbolOnPasteMapper,
      //@ts-expect-error
      GeneralRichTextSupport,
      List,
      Paragraph,
    ],
    toolbar: {
      items: defaultToolbarItems
    },
    link: {
      defaultProtocol: 'https://'
    },
    language: language,
    autosave: {
      save: pluginConfig.autosave.save,
      waitingTime: 50,
    }
  });
}
