import RemoteService from "@coremedia/studio-client.client-core-impl/data/impl/RemoteService";
import EncodingUtil from "@coremedia/studio-client.client-core/util/EncodingUtil";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import DateUtil from "@jangaroo/ext-ts/Date";
import Model from "@jangaroo/ext-ts/data/Model";
import NodeInterface from "@jangaroo/ext-ts/data/NodeInterface";
import Format from "@jangaroo/ext-ts/util/Format";
import TableView from "@jangaroo/ext-ts/view/Table";
import Config from "@jangaroo/runtime/Config";
import uint from "@jangaroo/runtime/uint";

interface ZipFileEntry {
  name: string;
  path: string;
  time: Date;
  size: uint;
  directory: boolean;
  children: ZipFileEntry[];
  url: string;
}

interface BlobTreeModelConfig extends Config<NodeInterface> {
  visible?: boolean;
  time?: Date;
  size?: number;
  directory?: boolean;
}

/**
 * Common utility method for the studio.
 */
class BlobMetadataUtil {

  static rowDblClick(tree: TableView, record: Model): void {
    if (record && record.data.leaf && record.data.url) {
      const url = RemoteService.calculateRequestURI(record.data.url);
      window.open(url);
    }
  }

  static convertDirectoryTree(files: ZipFileEntry[]): BlobTreeModelConfig {
    return {
      expanded: true,
      visible: true,
      leaf: false,
      size: 0,
      directory: true,
      text: "root",
      children: BlobMetadataUtil.#convertChildren(files),
    };
  }

  static #convertChildren(files: ZipFileEntry[]): BlobTreeModelConfig[] {
    return files.map(f => ({
      text: f.name,
      time: f.time,
      size: f.size,
      leaf: !f.directory,
      url: f.url,
      children: BlobMetadataUtil.#convertChildren(f.children),
    }));
  }

  static emptyRootNode(): BlobTreeModelConfig {
    return {
      expanded: true,
      visible: true,
      leaf: false,
      text: "root",
      children: [],
    };
  }

  static fileNameRenderer(value: any, metaData: any, record: any): string {
    return EncodingUtil.encodeForHTML(record.data.text);
  }

  static fileSizeRenderer(value: any, metaData: any, record: any): string {
    const directory: boolean = !record.data.leaf;
    const size: number = record.data.size;
    return directory ? "" : Format.fileSize(size);
  }

  static fileDateRenderer(value: any, metaData: any, record: any): string {
    if (record.data.time) {
      return DateUtil.format(record.data.time, Editor_properties.dateFormat);
    }

    return "";
  }
}

export default BlobMetadataUtil;
