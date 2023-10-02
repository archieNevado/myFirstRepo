import RemoteService from "@coremedia/studio-client.client-core-impl/data/impl/RemoteService";
import EncodingUtil from "@coremedia/studio-client.client-core/util/EncodingUtil";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import DateUtil from "@jangaroo/ext-ts/Date";
import Model from "@jangaroo/ext-ts/data/Model";
import NodeInterface from "@jangaroo/ext-ts/data/NodeInterface";
import Event from "@jangaroo/ext-ts/event/Event";
import TreePanel from "@jangaroo/ext-ts/tree/Panel";
import Format from "@jangaroo/ext-ts/util/Format";

/**
 * Common utility method for the studio.
 */
class BlobMetadataUtil {

  static rowDblClick(tree: TreePanel, record: Model, tr: HTMLElement, rowIndex: number, e: Event): void {
    if (record && record.data.leaf && record.data.url) {
      const url = RemoteService.calculateRequestURI(record.data.url);
      window.open(url);
    }
  }

  static convertDirectoryTree(files: Array<any>): NodeInterface {
    const root: Record<string, any> = {
      expanded: true,
      visible: true,
      leaf: false,
      size: 0,
      directory: true,
      text: "root",
      children: BlobMetadataUtil.#convertChildren(files),
    };
    return root;
  }

  static #convertChildren(files: Array<any>): Array<any> {
    const result = [];
    files.forEach((f: any): void => {
      const node: Record<string, any> = {
        text: f.name,
        time: f.time,
        size: f.size,
        leaf: !f.directory,
        url: f.url,
        children: BlobMetadataUtil.#convertChildren(f.children),
      };
      result.push(node);
    });
    return result;
  }

  static emptyRootNode(): any {
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
