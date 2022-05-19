import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Indent from '@ckeditor/ckeditor5-indent/src/indent';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import List from '@ckeditor/ckeditor5-list/src/list';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Link from '@ckeditor/ckeditor5-link/src/link';
import AutoLink from "@ckeditor/ckeditor5-link/src/autolink";
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import Table from '@ckeditor/ckeditor5-table/src/table';
import TableToolbar from '@ckeditor/ckeditor5-table/src/tabletoolbar';
import RemoveFormat from '@ckeditor/ckeditor5-remove-format/src/removeformat';
import Strikethrough from '@ckeditor/ckeditor5-basic-styles/src/strikethrough';
import Subscript from '@ckeditor/ckeditor5-basic-styles/src/subscript';
import Superscript from '@ckeditor/ckeditor5-basic-styles/src/superscript';
import Underline from '@ckeditor/ckeditor5-basic-styles/src/underline';
import ContentClipboard from "@coremedia/ckeditor5-coremedia-content-clipboard/ContentClipboard";
import ContentImagePlugin from "@coremedia/ckeditor5-coremedia-images/ContentImagePlugin";
import ContentLinks from "@coremedia/ckeditor5-coremedia-link/contentlink/ContentLinks";
import LinkTarget from "@coremedia/ckeditor5-coremedia-link/linktarget/LinkTarget";
import CoreMediaStudioEssentials from "@coremedia/ckeditor5-studio-essentials/CoreMediaStudioEssentials";
import CoreMediaSymbolOnPasteMapper from '@coremedia/ckeditor5-symbol-on-paste-mapper/SymbolOnPasteMapper';

import Autosave from '@ckeditor/ckeditor5-autosave/src/autosave';
import { CKEditorPluginConfig } from "./ckeditor";

export function createDefaultCKEditor (domElement:(string | HTMLElement), language: string, placeholderText:string | undefined, pluginConfig: CKEditorPluginConfig): Promise<ClassicEditor> {

  const defaultToolbarItems = [
    'undo',
    'redo',
    '|',
    'heading',
    '|',
    'bold',
    'italic',
    'underline',
    'strikethrough',
    'subscript',
    'superscript',
    'removeFormat',
    '|',
    'link',
    '|',
    'alignment',
    'blockQuote',
    '|',
    'insertTable',
    '|',
    'numberedList',
    'bulletedList',
    'outdent',
    'indent'
  ];

  return ClassicEditor.create(domElement, {
    // Add License Key retrieved via CKEditor for Premium Features Support.
    licenseKey: '',
    placeholder: placeholderText ? placeholderText : 'Type your text here...',
    plugins: [
      Alignment,
      AutoLink,
      Autosave,
      BlockQuote,
      Bold,
      ContentLinks,
      ContentClipboard,
      ContentImagePlugin,
      CoreMediaStudioEssentials,
      CoreMediaSymbolOnPasteMapper,
      Essentials,
      Heading,
      Indent,
      Italic,
      Link,
      LinkTarget,
      List,
      Paragraph,
      RemoveFormat,
      Strikethrough,
      Subscript,
      Superscript,
      Table,
      TableToolbar,
      Underline,
    ],
    toolbar: {
      items: defaultToolbarItems
    },
    link: {
      defaultProtocol: 'https://'
    },
    alignment: {
      options: [
        {
          name: "left",
          className: "align--left",
        },
        {
          name: "right",
          className: "align--right",
        },
        {
          name: "center",
          className: "align--center",
        },
        {
          name: "justify",
          className: "align--justify",
        },
      ],
    },
    table: {
      contentToolbar: [
        'tableRow',
        'tableColumn',
        'mergeTableCells'
      ]
    },
    language: language,
    autosave: {
      save: pluginConfig.autosave.save,
      waitingTime: 50,
    }
  });
}
