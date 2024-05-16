import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CMChannelForm from "./CMChannelForm";

interface CMChannelFormBaseConfig extends Config<DocumentTabPanel> {
}

class CMChannelFormBase extends DocumentTabPanel {
  declare Config: CMChannelFormBaseConfig;

  /**
   * A value expression that evaluates to an object mapping
   * ids of root channels to site structure infos.
   *
   * @see com.coremedia.cms.studio.multisite.models.sites.Site
   */
  static #rootChannelsExpression: ValueExpression = null;

  constructor(config: Config<CMChannelForm> = null) {
    super(config);
  }

  /**
   * Returns a value expression that evaluates to an object mapping
   * ids of root channels to site objects.
   *
   * @return the root channels expression
   *
   * @see com.coremedia.cms.studio.multisite.models.sites.Site
   */
  static getRootChannelsExpression(): ValueExpression {
    if (!CMChannelFormBase.#rootChannelsExpression) {
      CMChannelFormBase.#rootChannelsExpression = ValueExpressionFactory.createFromFunction(CMChannelFormBase.#computeRootChannels);
    }
    return CMChannelFormBase.#rootChannelsExpression;
  }

  static #computeRootChannels(): any {
    const sites = editorContext._.getSitesService().getSites();
    if (!sites) {
      return undefined;
    }
    const result: Record<string, any> = {};
    for (let i = 0; i < sites.length; i++) {
      const site: Site = sites[i];
      const siteRootDocument = site.getSiteRootDocument();
      if (siteRootDocument) {
        result[siteRootDocument.getId()] = site;
      }
    }
    return result;
  }

  /**
   * Returns a value expression that checks if the content passed
   * in the given value expression is a root channel.
   * @param bindTo The value expression that contains the channel content.
   * @return A value expression that contains true if the given channel is an immediate child of CMSite.
   */
  static getIsRootChannelValueExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const content = as(bindTo.getValue(), Content);
      if (content === undefined) {
        return undefined;
      }
      if (!content) {
        return false;
      }
      const rootChannels = CMChannelFormBase.getRootChannelsExpression().getValue();
      if (rootChannels == undefined) {
        return undefined;
      }
      return ! !rootChannels[content.getId()];
    });
  }
}

export default CMChannelFormBase;
