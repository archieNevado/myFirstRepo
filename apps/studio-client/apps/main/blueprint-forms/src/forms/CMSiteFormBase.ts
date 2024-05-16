import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CMSiteForm from "./CMSiteForm";

interface CMSiteFormBaseConfig extends Config<DocumentTabPanel> {
}

class CMSiteFormBase extends DocumentTabPanel {
  declare Config: CMSiteFormBaseConfig;

  #siteNameReadOnlyValueExpression: ValueExpression = null;

  constructor(config: Config<CMSiteForm> = null) {
    super(config);
  }

  getSiteNameReadOnlyValueExpression(): ValueExpression {
    return this.#siteNameReadOnlyValueExpression ||
            (this.#siteNameReadOnlyValueExpression = ValueExpressionFactory.createFromFunction(bind(this, this.#calculateReadOnly)));
  }

  #calculateReadOnly(): boolean {
    // the admin user always may change the name of the site
    if (CMSiteFormBase.isAdministrator()) {
      return false;
    }

    const sitesService = editorContext._.getSitesService();
    let content: Content;

    if (!this.bindTo) {
      return true;
    }

    content = as(this.bindTo.getValue(), Content);

    if (!content) {
      return true;
    }

    return ! !sitesService.getMaster(content);
  }

  protected static isAdministrator(): boolean {
    return session._.getUser().isAdministrative();
  }
}

export default CMSiteFormBase;
