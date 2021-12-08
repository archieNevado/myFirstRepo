import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import EncodingUtil from "@coremedia/studio-client.client-core/util/EncodingUtil";
import Ext from "@jangaroo/ext-ts";
import { as } from "@jangaroo/runtime";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyStudioPluginSettings_properties from "../TaxonomyStudioPluginSettings_properties";
import TaxonomyNodeFactory from "./TaxonomyNodeFactory";
import TaxonomyNodeList from "./TaxonomyNodeList";
import TaxonomyUtil from "./TaxonomyUtil";

class TaxonomyNode {
  static readonly AUTO_COMMIT: boolean = true;

  static readonly PROPERTY_PATH: string = "path";

  static readonly PROPERTY_HTML: string = "html";//property used to store HTML of search combo

  static readonly PROPERTY_NAME: string = "name";

  static readonly PROPERTY_REF: string = "ref";

  static readonly PROPERTY_TAXONOMY_ID: string = "taxonomyId";

  #json: Record<string, any> = {};

  constructor(object: any) {
    this.#json = object;
  }

  toJson(): any {
    return this.#json;
  }

  static forValues(name: string, type: string, ref: string, siteId: string, level: int, root: boolean, leaf: boolean, taxonomyId: string, selectable: boolean, extendable: boolean): TaxonomyNode {
    const json: Record<string, any> = {
      name: name,
      siteId: siteId,
      type: type,
      ref: ref,
      level: level,
      root: root,
      leaf: leaf,
      taxonomyId: taxonomyId,
      selectable: selectable,
      extendable: extendable,
    };
    return new TaxonomyNode(json);
  }

  getSite(): string {
    return this.#json.siteId;
  }

  /**
   * Every taxonomy node has a name, which is shown in the taxonomy chooser and editor.
   * The names may be localized by client side resource bundles (for the root nodes).
   * @return
   */
  getName(): string {
    return this.#json.name;
  }

  /**
   * Returns the unescaped name.
   * @return
   */
  getRawName(): string {
    return this.#json.name;
  }

  setName(name: string): void {
    this.#json.name = name;
  }

  /**
   * A taxonomy node may reference entities like documents (content id) in the content repository or entries
   * in another database.
   * For the Studio UI this reference is not of interest, but the TaxonomyStrategies rely on it.
   * @return
   */
  getRef(): string {
    return this.#json.ref;
  }

  setRef(ref: string): void {
    this.#json.ref = ref;
  }

  /**
   * A taxonomy node might represent an object of a specific 'type' like: 'Country', 'State', 'City', 'Street'. These
   * types might be rendered differently in the frontend.
   * @return
   */
  getType(): string {
    return this.#json.type;
  }

  setType(type: string): void {
    this.#json.type = type;
  }

  /**
   * A taxonomy node might be selectable (or choosable) in in the taxonomy chooser.
   * @return
   */
  isSelectable(): boolean {
    return this.#json.selectable;
  }

  setSelectable(selectable: boolean): void {
    this.#json.selectable = selectable;
  }

  /**
   * this flag indicates, that a node has child nodes.
   * @return
   */
  isLeaf(): boolean {
    return this.#json.leaf;
  }

  setLeaf(leaf: boolean): void {
    this.#json.leaf = leaf;
  }

  /**
   * this flag indicates, that the taxonomy editor may add children to this node. If false this node is leaf-only.
   * @return
   */
  isExtendable(): boolean {
    return this.#json.extendable;
  }

  setExtendable(extendable: boolean): void {
    this.#json.extendable = extendable;
  }

  /**
   * Indicates that this node represents the root of a taxonomy tree, not a taxonomy node in this taxonomy.
   * In most cases - but not necessarily - root nodes do not represent entities and are not selectable.
   * @return
   */
  isRoot(): boolean {
    return this.#json.root;
  }

  setRoot(root: boolean): void {
    this.#json.root = root;
  }

  /** this property is used to find the TaxonomyStrategy for a given node. **/
  getTaxonomyId(): string {
    return EncodingUtil.decodeFromHTML(this.#json.taxonomyId).split(",")[0].trim();
  }

  setTaxonomyId(taxonomy: string): void {
    this.#json.taxonomyId = taxonomy;
  }

  getLevel(): int {
    return this.#json.level;
  }

  setLevel(level: int): void {
    this.#json.level = level;
  }

  /**
   * If the taxonomy node is content, the remote bean of
   * it is invalidated here, e.g. when a node is deleted.
   * @param callback
   */
  invalidate(callback: AnyFunction): void {
    const thisNode = this;
    const taxContent = as(beanFactory._.getRemoteBean(this.getRef()), Content);
    if (taxContent) {
      taxContent.invalidate(() =>
        this.reloadNode((reloaded: TaxonomyNode): void => {
          thisNode.#json = reloaded.#json;
          callback.call(null);
        }),
      );
    } else {
      callback.call(null);
    }
  }

  getPath(): TaxonomyNodeList {
    if (this.#json.path) {
      return new TaxonomyNodeList(this.#json.path.nodes);
    }
    return null;
  }

  getPathString(): string {
    return this.#json.pathString;
  }

  getDisplayName(): string {
    let name = this.getRawName();
    if (this.getWeight()) {
      name = name + " (" + this.getWeight() + ")";
    }
    return TaxonomyUtil.escapeHTML(name);
  }

  /**
   * Triggers a reload of the given node, invokes the callback
   * function with the reloaded node.
   * @param callback
   */
  reloadNode(callback: AnyFunction): void {
    const url = "taxonomies/node?" + this.#toNodeQuery();
    this.#executeNodeOperation(url, callback);
  }

  /**
   * Commits the changes executed on the active node.
   * The commit is triggered once another node is selected.
   * @param callback
   */
  commitNode(callback: AnyFunction = null): void {
    if (TaxonomyNode.AUTO_COMMIT) {
      const url = "taxonomies/commit?" + this.#toNodeQuery();
      this.#executeNodeOperation(url, callback);
    }
  }

  /**
   * Loads the parent of this node.
   * @param callback
   */
  loadParent(callback: AnyFunction): void {
    const url = "taxonomies/parent?" + this.#toNodeQuery();
    this.#executeNodeOperation(url, callback);
  }

  /**
   * Creates a new taxonomy node, using the given parent for type and parent.
   * @param callback
   */
  createChild(callback: AnyFunction): void {
    const url = "taxonomies/createChild?" + this.#toNodeQuery() + "&" + Ext.urlEncode({ defaultName: TaxonomyStudioPluginSettings_properties.taxonomy_default_name });
    this.#executeNodeOperation(url, callback);
  }

  /**
   * Calls the given REST method which always return a taxonomy node as result.
   * @param url
   * @param callback
   */
  #executeNodeOperation(url: string, callback: AnyFunction): void {
    const remote = beanFactory._.getRemoteBean(url);
    remote.invalidate((): void => {
      if (remote.getState().readable) {
        const obj = remote.toObject();
        const node = new TaxonomyNode(obj);
        callback(node);
      } else {
        callback(null);
      }
    });
  }

  /**
   * Callback returns a node list with all child nodes of this node.
   * @param refresh true to force reload
   * @param callback
   */
  loadChildren(refresh: boolean, callback: AnyFunction): void {
    const url = "taxonomies/children?" + this.#toNodeQuery();
    TaxonomyNodeFactory.loadRemoteTaxonomyNodeList(url, refresh, callback);
  }

  /**
   * Creates the REST uri for the node actions.
   * @return
   */
  #toNodeQuery(): string {
    let query = Ext.urlEncode({
      taxonomyId: this.getTaxonomyId(),
      nodeRef: this.getRef(),
    });
    if (this.getSite()) {
      query = Ext.urlEncode({
        taxonomyId: this.getTaxonomyId(),
        nodeRef: this.getRef(),
        site: this.getSite(),
      });
    }
    return query;
  }

  getWeight(): string {
    if (this.#json.weight !== -1) {
      return this.#json.weight;
    }
    return undefined;
  }
}

export default TaxonomyNode;
