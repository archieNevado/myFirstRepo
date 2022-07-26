import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Indent from '@ckeditor/ckeditor5-indent/src/indent';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
//@ts-expect-error
import DocumentList from '@ckeditor/ckeditor5-list/src/documentlist';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Link from '@ckeditor/ckeditor5-link/src/link';
import ImageInline from "@ckeditor/ckeditor5-image/src/imageinline";
import ImageStyle from "@ckeditor/ckeditor5-image/src/imagestyle";
import ImageToolbar from "@ckeditor/ckeditor5-image/src/imagetoolbar";
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
import CoreMediaFontMapper from '@coremedia/ckeditor5-font-mapper/FontMapper';

import Autosave from '@ckeditor/ckeditor5-autosave/src/autosave';
import { CKEditorPluginConfig } from "./ckeditor";
import { icons } from '@ckeditor/ckeditor5-core';
import { localization, localize } from "../lang/LocalizationUtils";

const {
  //@ts-expect-error
  objectInline: withinTextIcon,
  objectLeft: alignLeftIcon,
  objectRight: alignRightIcon,
  objectSizeFull: pageDefaultIcon
} = icons;

/**
 * Localization for the image alignment entries in
 * the editor configuration.
 */
localization.add({
  "de": {
    "Left-aligned": "Linksbündig",
    "Right-aligned": "Rechtsbündig",
    "Within Text": "Im Text",
    "Page default": "Standardeinstellung"
  }
})

/**
 * This is the default configuration for the CKEditor 5 in CoreMedia Studio.
 * Please be advised, that
 *
 * *** THIS CONFIGURATION MUST NOT BE CHANGED ***
 *
 * Any misconfiguration of this editor might cause issues in CoreMedia Studio.
 * If you want to add a custom configuration, you should create a separate module
 * and add the exported editor in {@link ckeditor.ts} accordingly.
 */
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
      CoreMediaFontMapper,
      Essentials,
      Heading,
      ImageInline,
      ImageStyle,
      ImageToolbar,
      Indent,
      Italic,
      Link,
      LinkTarget,
      DocumentList,
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
    image: {
      styles: {
        // Defining custom styling options for the images.
        options: [
          {
            name: 'float-left',
            icon: alignLeftIcon,
            title: localize('Left-aligned', language),
            className: 'float--left',
            modelElements: [ 'imageInline' ]
          },
          {
            name: 'float-right',
            icon: alignRightIcon,
            title: localize('Right-aligned', language),
            className: 'float--right',
            modelElements: [ 'imageInline' ]
          },
          {
            name: 'float-none',
            icon: withinTextIcon,
            title: localize('Within Text', language),
            className: 'float--none',
            modelElements: [ 'imageInline' ]
          },
          {
            name: 'inline',
            title: localize('Page default', language),
            icon: pageDefaultIcon,
          }
        ]
      },
      toolbar: [
        'imageStyle:float-left',
        'imageStyle:float-right',
        'imageStyle:float-none',
        'imageStyle:inline',
      ]
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
