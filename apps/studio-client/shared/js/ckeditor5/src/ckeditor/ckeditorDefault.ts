import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Indent from '@ckeditor/ckeditor5-indent/src/indent';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import Code from '@ckeditor/ckeditor5-basic-styles/src/code';
//@ts-expect-error
import DocumentList from '@ckeditor/ckeditor5-list/src/documentlist';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Link from '@ckeditor/ckeditor5-link/src/link';
import ImageInline from "@ckeditor/ckeditor5-image/src/imageinline";
import ImageBlockEditing from "@ckeditor/ckeditor5-image/src/image/imageblockediting";
import ImageStyle from "@ckeditor/ckeditor5-image/src/imagestyle";
import ImageToolbar from "@ckeditor/ckeditor5-image/src/imagetoolbar";
import ImageTextAlternative from "@ckeditor/ckeditor5-image/src/imagetextalternative";
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
import PasteContentPlugin from "@coremedia/ckeditor5-coremedia-content-clipboard/paste/PasteContentPlugin";
import ContentImagePlugin from "@coremedia/ckeditor5-coremedia-images/ContentImagePlugin";
import ContentLinks from "@coremedia/ckeditor5-coremedia-link/contentlink/ContentLinks";
import Differencing from "@coremedia/ckeditor5-coremedia-differencing/Differencing";
import LinkTarget from "@coremedia/ckeditor5-coremedia-link/linktarget/LinkTarget";
import CoreMediaStudioEssentials, { Strictness } from "@coremedia/ckeditor5-coremedia-studio-essentials/CoreMediaStudioEssentials";
import CoreMediaFontMapper from '@coremedia/ckeditor5-font-mapper/FontMapper';
import Autoformat from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import FindAndReplace from '@ckeditor/ckeditor5-find-and-replace/src/findandreplace';
import CodeBlock from '@ckeditor/ckeditor5-code-block/src/codeblock';

import Autosave from '@ckeditor/ckeditor5-autosave/src/autosave';
import { icons } from '@ckeditor/ckeditor5-core';
import { localization, localize } from "../lang/LocalizationUtils";
import LocaleUtil from "@coremedia/studio-client.cap-base-models/locale/LocaleUtil";
import { CKEditorPluginConfig, CreateCKEditorFunction } from "@coremedia/studio-client.ckeditor-common/CKEditorCreateFunctionType";
import { LinkImage } from "@ckeditor/ckeditor5-link";
import '../theme/custom.css';
import { PasteFromOffice } from "@ckeditor/ckeditor5-paste-from-office";
import { SourceEditing } from "@ckeditor/ckeditor5-source-editing";
import CKEditorFeatureFlags from "@coremedia/studio-client.ckeditor-common/CKEditorFeatureFlags";

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
    "Page default": "Standardeinstellung",
    "Type your text here...": "Text hier eingeben...",
    "More formatting": "Weitere Formatierung",
    "Paragraph": "Absatz",
    "Heading 1": "Überschrift 1",
    "Heading 2": "Überschrift 2",
    "Heading 3": "Überschrift 3",
  }
})

/**
 * This is the default configuration for the CKEditor 5 in CoreMedia Studio.
 * Please be advised, that any misconfiguration of this editor might cause issues in CoreMedia Studio.
 *
 * If you want to add a custom configuration, you should create a separate module
 * and add the exported editor in {@link ckeditor.ts} accordingly.
 *
 * **Available Feature Flags:**
 *
 * * `administrative`
 *
 *   Adds administrative capabilities to the provided CKEditor instance, such as
 *   source editing.
 */
export const createDefaultCKEditor: CreateCKEditorFunction = (domElement:(string | HTMLElement), pluginConfig: CKEditorPluginConfig): Promise<ClassicEditor> => {
  const language = LocaleUtil.getLocale();
  const defaultToolbarItems = [
    'undo',
    'redo',
    '|',
    'heading',
    '|',
    'bold',
    'italic',
    'underline',
    {
      label: localize('More formatting', language),
      icon: 'threeVerticalDots',
      items: [ 'strikethrough', 'subscript', 'superscript', 'code' ]
    },
    '|',
    'removeFormat',
    '|',
    'link',
    '|',
    'alignment',
    'blockQuote',
    'codeBlock',
    '|',
    'insertTable',
    '|',
    'numberedList',
    'bulletedList',
    'outdent',
    'indent',
    '|',
    'pasteContent',
    'findAndReplace'
  ];
  if (pluginConfig.featureFlags?.includes(CKEditorFeatureFlags.ADMINISTRATIVE)) {
    defaultToolbarItems.push("|", "sourceEditing");
  }
  return ClassicEditor.create(domElement, {
    // Add License Key retrieved via CKEditor for Premium Features Support.
    licenseKey: '',
    placeholder: localize('Type your text here...', language),
    plugins: [
      Alignment,
      Autoformat,
      AutoLink,
      Autosave,
      BlockQuote,
      Bold,
      Code,
      CodeBlock,
      ContentLinks,
      ContentClipboard,
      ContentImagePlugin,
      CoreMediaStudioEssentials,
      CoreMediaFontMapper,
      Differencing,
      Essentials,
      FindAndReplace,
      Heading,
      ImageInline,
      ImageBlockEditing,
      ImageStyle,
      ImageToolbar,
      ImageTextAlternative,
      Indent,
      Italic,
      Link,
      LinkImage,
      LinkTarget,
      DocumentList,
      Paragraph,
      PasteContentPlugin,
      PasteFromOffice,
      RemoveFormat,
      SourceEditing,
      Strikethrough,
      Subscript,
      Superscript,
      Table,
      TableToolbar,
      Underline,
    ],
    //@ts-ignore (ignore instead of expect-error because of other ignores in module) types only expect strings here, but objects are also allowed
    toolbar: defaultToolbarItems,
    heading: {
      options: [
        { model: 'paragraph', title: localize('Paragraph', language), class: 'ck-heading_paragraph' },
        { model: 'heading1', view: 'h1', title: localize('Heading 1', language), class: 'ck-heading_heading1' },
        { model: 'heading2', view: 'h2', title: localize('Heading 2', language), class: 'ck-heading_heading2' },
        { model: 'heading3', view: 'h3', title: localize('Heading 3', language), class: 'ck-heading_heading3' },
      ],
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
        "|",
        "linkImage",
        "imageTextAlternative",
        "contentImageOpenInTab",
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
      waitingTime: 1000,
    },
    //@ts-ignore (ignore instead of expect-error because of other ignores in module) ClassicEditor.create(..., EditorConfig), EditorConfig does not know about custom plugin configurations.
    "coremedia:richtext": {
      strictness: Strictness.LOOSE,
    },
  });
}
