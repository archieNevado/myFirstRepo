import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyStudioPlugin from "../TaxonomyStudioPlugin";
import TaxonomyNode from "./TaxonomyNode";
import TaxonomyNodeList from "./TaxonomyNodeList";
import TaxonomyPreferencesBase from "./preferences/TaxonomyPreferencesBase";

class TaxonomyNodeFactory {

  static loadTaxonomies(site: string, callback: AnyFunction, reload: boolean = true): void {
    let url = "taxonomies/roots";
    url = TaxonomyNodeFactory.#appendParam(url, "site", site);
    url = TaxonomyNodeFactory.#appendParam(url, "reload", reload);
    TaxonomyNodeFactory.loadRemoteTaxonomyNodeList(url, reload, callback);
  }

  static loadTaxonomyRoot(site: string, taxonomyId: string, callback: AnyFunction) {
    let url = "taxonomies/root?" + Ext.urlEncode({ taxonomyId: taxonomyId });
    if (site) {
      url += "&site=" + site;
    }
    const remote = beanFactory._.getRemoteBean(url);
    remote.load((): void => {
      const obj = remote.toObject();
      if (!obj.type || !obj.name) {
        trace("[INFO]", "No taxonomy found for site \"" + site + "\" and taxonomy id \"" + taxonomyId + "\"");
        callback.call(null, null);
      } else {
        const reloadedNode = new TaxonomyNode(obj);
        callback(reloadedNode);
      }
    });
  }

  static loadPath(taxonomyId: string, ref: string, siteId: string, callback: AnyFunction): void {
    const url = "taxonomies/path?" + Ext.urlEncode({
      taxonomyId: taxonomyId,
      nodeRef: ref,
      site: siteId,
    });
    const entriesBean = beanFactory._.getRemoteBean(url);
    entriesBean.invalidate((): void => {
      if (entriesBean.get("path")) { //null if another root node was returned as default.
        const nodelist = new TaxonomyNodeList(entriesBean.get("path")["nodes"]);
        callback(nodelist);
      }
    });
  }

  static loadRemoteTaxonomyNodeList(url: string, refresh: boolean, callback: AnyFunction) {
    // create a remote bean which
    const entriesBean = beanFactory._.getRemoteBean(url);
    if (refresh) {
      entriesBean.invalidate((): void => {
        const nodelist = new TaxonomyNodeList(entriesBean.get("nodes"));
        callback(nodelist);
      });
    } else {
      entriesBean.load((): void => {
        const nodelist = new TaxonomyNodeList(entriesBean.get("nodes"));
        callback(nodelist);
      });
    }
  }

  static loadSuggestions(taxonomyId: string, document: Content, callback: AnyFunction): void {
    const id = document.getId();
    let valueString: string = editorContext._.getPreferences().get(TaxonomyPreferencesBase.PREFERENCE_SEMANTIC_SETTINGS_KEY);
    if (!valueString) {
      valueString = TaxonomyStudioPlugin.TAXONOMY_NAME_MATCHING_KEY;
    }
    const semanticService = valueString;
    let remoteBeanUrl = "taxonomies/suggestions?" + Ext.urlEncode({
      taxonomyId: taxonomyId,
      semanticStrategyId: semanticService,
      id: id,
      max: 20,
    });
    ValueExpressionFactory.create(ContentPropertyNames.PATH, document).loadValue((): void => {
      const siteId = editorContext._.getSitesService().getSiteIdFor(document);
      if (siteId) {
        remoteBeanUrl += "&site=" + siteId;
      }
      const remoteBean = beanFactory._.getRemoteBean(remoteBeanUrl);
      remoteBean.invalidate((): void => {
        const nodelist = new TaxonomyNodeList(remoteBean.get("nodes"));
        callback(nodelist);
      });
    });
  }

  static #appendParam(url: string, name: string, value: any): string {
    if (!value) {
      return url;
    }

    if (url.indexOf("?") === -1) {
      url = url + "?" + name + "=" + value;
    } else {
      url = url + "&" + name + "=" + value;
    }

    return url;
  }
}

export default TaxonomyNodeFactory;
