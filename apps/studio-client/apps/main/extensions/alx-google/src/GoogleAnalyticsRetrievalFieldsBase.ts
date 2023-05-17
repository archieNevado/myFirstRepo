import CapType from "@coremedia/studio-client.cap-rest-client/common/CapType";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import GoogleAnalyticsRetrievalFields from "./GoogleAnalyticsRetrievalFields";

interface GoogleAnalyticsRetrievalFieldsBaseConfig extends Config<PropertyFieldGroup> {
}

class GoogleAnalyticsRetrievalFieldsBase extends PropertyFieldGroup {
  declare Config: GoogleAnalyticsRetrievalFieldsBaseConfig;

  static readonly #GOOGLE_ANALYTICS: string = "googleAnalytics";

  static readonly #AUTH_FILE: string = "authFile";

  static readonly #LOCAL_SETTINGS: string = "localSettings";

  static readonly #CM_DOWNLOAD: string = "CMDownload";

  #authFileVE: ValueExpression = null;

  #localSettings: RemoteBean = null;

  constructor(config: Config<GoogleAnalyticsRetrievalFields> = null) {
    super(config);
    this.#updateAuthFileFromStruct();
    this.getAuthFileVE().addChangeListener(bind(this, this.#updateStruct));
    this.bindTo.addChangeListener(bind(this, this.#updateAuthFileFromStruct));
  }

  #updateStruct(): void {
    const value: Array<any> = this.getAuthFileVE().getValue();
    if (value && value.length > 0) {
      this.#applyToStruct(this.bindTo.getValue(), GoogleAnalyticsRetrievalFieldsBase.#CM_DOWNLOAD, GoogleAnalyticsRetrievalFieldsBase.#AUTH_FILE, value[0]);
    } else {
      GoogleAnalyticsRetrievalFieldsBase.#removeLinkFromStruct(this.bindTo.getValue(), GoogleAnalyticsRetrievalFieldsBase.#AUTH_FILE);
    }
  }

  static #removeLinkFromStruct(content: Content, structPropertyName: string): void {
    const struct: Struct = content.getProperties().get(GoogleAnalyticsRetrievalFieldsBase.#LOCAL_SETTINGS);
    if (struct) {
      const googleAnalytics = GoogleAnalyticsRetrievalFieldsBase.#getStruct(struct, GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS);
      if (googleAnalytics) {
        googleAnalytics.getType().removeProperty(structPropertyName);
      }
    }
  }

  static #getStruct(struct: Struct, key: string): Struct {
    return struct.get(key);
  }

  protected getAuthFileVE(): ValueExpression {
    if (!this.#authFileVE) {
      this.#authFileVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#authFileVE;
  }

  #updateAuthFileFromStruct(): void {
    const c: Content = this.bindTo.getValue();
    c.load((): void => {
      const props = c.getProperties();
      let init = false;
      if (!this.#localSettings) {
        init = true;
      }
      this.#localSettings = as(props.get(GoogleAnalyticsRetrievalFieldsBase.#LOCAL_SETTINGS), RemoteBean);
      if (init) {
        this.#localSettings.addPropertyChangeListener(GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS, bind(this, this.#updateAuthFileFromLocalSettings));
      }
      this.#localSettings.load((): void =>
        this.#updateAuthFileFromLocalSettings(),
      );
    });
  }

  #updateAuthFileFromLocalSettings(): void {
    const googleAnalytics = GoogleAnalyticsRetrievalFieldsBase.#getStruct(as(this.#localSettings, Struct), GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS);
    if (googleAnalytics) {
      const authFile: Struct = googleAnalytics.get(GoogleAnalyticsRetrievalFieldsBase.#AUTH_FILE);
      if (!authFile) {
        this.getAuthFileVE().setValue([]);
      } else {
        this.getAuthFileVE().setValue([authFile]);
      }
    }
  }

  #applyToStruct(content: Content, contentType: string, structPropertyName: string, link: Content): void {
    const struct: Struct = content.getProperties().get(GoogleAnalyticsRetrievalFieldsBase.#LOCAL_SETTINGS);

    //the substruct can be created on the fly but isn't loaded, so we trigger an invalidate in this case
    const googleAnalytics = GoogleAnalyticsRetrievalFieldsBase.#getStruct(struct, GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS);
    if (!googleAnalytics) {
      struct.getType().addStructProperty(GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS);
      content.invalidate((): void =>
        this.#applyToStruct(content, contentType, structPropertyName, link),
      );
      return;
    }

    const capType: CapType = session._.getConnection().getContentRepository().getContentType(contentType);
    googleAnalytics.getType().addLinkProperty(structPropertyName, capType, link);

    // apply the link again: in case the substruct had to be created previously,
    // we need to notify the component about the missed initialization
    this.getAuthFileVE().setValue([link]);
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#localSettings.removePropertyChangeListener(GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS, bind(this, this.#updateAuthFileFromLocalSettings));
    this.getAuthFileVE().removeChangeListener(bind(this, this.#updateStruct));
    this.bindTo.removeChangeListener(bind(this, this.#updateAuthFileFromStruct));
  }
}

export default GoogleAnalyticsRetrievalFieldsBase;
