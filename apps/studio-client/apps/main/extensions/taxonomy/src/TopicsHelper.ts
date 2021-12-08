import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import { AnyFunction } from "@jangaroo/runtime/types";

/**
 * Provides helper methods processing topic pages, like reloading the topics.
 */
class TopicsHelper {

  /**
   * Loads all topics for the given taxonomy.
   * @param taxonomy The id of the taxonomy folder
   * @param siteId The id of the preferred site
   * @param term The search term to filter for or null/undefined.
   * @param callback The callback that contains the topics.
   */
  static loadTopics(taxonomy: number, siteId: string, term: string, callback: AnyFunction): void {
    const params = Ext.urlEncode({
      taxonomy: taxonomy,
      site: siteId,
      term: term,
    });
    const entriesBean = beanFactory._.getRemoteBean("topicpages/topics?" + params);
    entriesBean.invalidate((): void => {
      callback.call(null, entriesBean.get("items"), entriesBean.get("filtered"));
    });
  }

  /**
   * Loads the topic page settings.
   * @param callback
   */
  static loadSettings(callback: AnyFunction): void {
    let url = "topicpages/settings";
    const preferredSiteId = editorContext._.getSitesService().getPreferredSiteId();
    if (preferredSiteId) {
      url += ("?" + Ext.urlEncode({ site: preferredSiteId }));
    }
    const settingsRemoteBean = beanFactory._.getRemoteBean(url);
    settingsRemoteBean.invalidate((): void => {
      callback.call(null, settingsRemoteBean);
    });
  }

  /**
   * Updates the default page or the custom page for the given topic.
   * @param id The id of the topic to update the context link for.
   * @param site The site to update the page for
   * @param create determines, if this site should be created or not
   * @param callback The callback that contains the updated topic representation.
   */
  static updatePage(id: number, site: string, create: boolean, callback: AnyFunction): void {
    const url = "topicpages/page";
    new RemoteServiceMethod(url, "POST").request({
      id: id,
      site: site,
      create: create,
    }, (response: RemoteServiceMethodResponse): void => {
      const json: any = response.getResponseJSON();
      callback.call(null, json);
    }, null);
  }

  /**
   * Tries to resolve the taxonomy if the active content is a Channel and a custom topic page
   * @param c
   * @return the taxonomy content (or undefined if not loaded)
   */
  static resolveTaxonomyForTopicPage(c: Content): Content {
    if (c) {
      const contentType = c.getType();
      if (undefined === contentType) {
        return undefined;
      }
      if (contentType.getName() === "CMChannel") {
        const items = c.getReferrers();
        if (undefined === items) {
          return undefined;
        }
        for (let i = 0; i < items.length; i++) {
          const ref: Content = items[i];
          if (ref && !ref.getState().readable) {
            continue;
          }
          if (undefined === ref.getType()) {
            return undefined;
          }
          if (ref.getType().isSubtypeOf("CMTaxonomy")) {
            return ref;
          }
        }
      }
    }
    return null;
  }

}

export default TopicsHelper;
