import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import MessageBoxUtilInternal from "@coremedia/studio-client.ext.ui-components/messagebox/MessageBoxUtilInternal";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import StringUtil from "@jangaroo/ext-ts/String";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";
import CatalogTreeRelation from "./CatalogTreeRelation";
import CatalogTreeRelationHelper from "./CatalogTreeRelationHelper";

class CatalogCopy {
  #treeRelation: CatalogTreeRelation = null;

  #sources: Array<any> = null;

  #newParent: Content = null;

  #callback: AnyFunction = null;

  constructor(catalogTreeRelation: CatalogTreeRelation, sources: Array<any>, newParent: Content, callback?: AnyFunction) {
    this.#treeRelation = catalogTreeRelation;
    this.#sources = sources;
    this.#newParent = newParent;
    this.#callback = callback;
  }

  execute(): void {
    //TODO we only copy products here! see mayCopy check
    const parents = this.#treeRelation.getParents(this.#sources[0]);

    if (!this.#callback) {
      this.#callback = (() => {
      });
    }

    // Create a "real" product copy when we stay in the same folder
    if (parents.indexOf(this.#newParent) !== -1) {
      CatalogTreeRelationHelper.copyAndLinkProducts(this.#sources, this.#newParent, this.#callback);
    }
    //the products are copied into in another category, so we ask the user what to do: create a copy or the linking?
    else {
      const msg = StringUtil.format(CatalogStudioPlugin_properties.catalog_copy_or_link_message, this.#newParent.getName());
      MessageBoxUtilInternal.show(CatalogStudioPlugin_properties.catalog_copy_or_link_title, msg, null, {
        yes: CatalogStudioPlugin_properties.catalog_copy_btn_text,
        no: CatalogStudioPlugin_properties.catalog_link_btn_text,
        cancel: Editor_properties.dialog_defaultCancelButton_text,
      },
      (btn: string): void => {
        if (btn === "cancel") {
          return;
        }

        const copy: boolean = (btn === "yes");
        if (copy) {
          CatalogTreeRelationHelper.copyAndLinkProducts(this.#sources, this.#newParent, this.#callback);
        } else {
          this.#treeRelation.addChildNodes(this.#newParent, this.#sources, this.#callback);
        }
      },
      );
    }
  }

}

export default CatalogCopy;
