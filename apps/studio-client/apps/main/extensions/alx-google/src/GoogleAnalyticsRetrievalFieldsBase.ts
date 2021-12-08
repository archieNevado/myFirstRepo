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

  static readonly #P12_FILE: string = "p12File";

  static readonly #LOCAL_SETTINGS: string = "localSettings";

  static readonly #CM_DOWNLOAD: string = "CMDownload";

  #p12FileVE: ValueExpression = null;

  #localSettings: RemoteBean = null;

  constructor(config: Config<GoogleAnalyticsRetrievalFields> = null) {
    super(config);
    this.#updateP12FileFromStruct();
    this.getP12FileVE().addChangeListener(bind(this, this.#updateStruct));
    this.bindTo.addChangeListener(bind(this, this.#updateP12FileFromStruct));
  }

  #updateStruct(): void {
    const value: Array<any> = this.getP12FileVE().getValue();
    if (value && value.length > 0) {
      this.#applyToStruct(this.bindTo.getValue(), GoogleAnalyticsRetrievalFieldsBase.#CM_DOWNLOAD, GoogleAnalyticsRetrievalFieldsBase.#P12_FILE, value[0]);
    } else {
      GoogleAnalyticsRetrievalFieldsBase.#removeLinkFromStruct(this.bindTo.getValue(), GoogleAnalyticsRetrievalFieldsBase.#P12_FILE);
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

  protected getP12FileVE(): ValueExpression {
    if (!this.#p12FileVE) {
      this.#p12FileVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#p12FileVE;
  }

  #updateP12FileFromStruct(): void {
    const c: Content = this.bindTo.getValue();
    c.load((): void => {
      const props = c.getProperties();
      let init = false;
      if (!this.#localSettings) {
        init = true;
      }
      this.#localSettings = as(props.get(GoogleAnalyticsRetrievalFieldsBase.#LOCAL_SETTINGS), RemoteBean);
      if (init) {
        this.#localSettings.addPropertyChangeListener(GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS, bind(this, this.#updateP12FileFromLocalSettings));
      }
      this.#localSettings.load((): void =>
        this.#updateP12FileFromLocalSettings(),
      );
    });
  }

  #updateP12FileFromLocalSettings(): void {
    const googleAnalytics = GoogleAnalyticsRetrievalFieldsBase.#getStruct(as(this.#localSettings, Struct), GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS);
    if (googleAnalytics) {
      const p12File: Struct = googleAnalytics.get(GoogleAnalyticsRetrievalFieldsBase.#P12_FILE);
      if (!p12File) {
        this.getP12FileVE().setValue([]);
      } else {
        this.getP12FileVE().setValue([p12File]);
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
    this.getP12FileVE().setValue([link]);
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#localSettings.removePropertyChangeListener(GoogleAnalyticsRetrievalFieldsBase.#GOOGLE_ANALYTICS, bind(this, this.#updateP12FileFromLocalSettings));
    this.getP12FileVE().removeChangeListener(bind(this, this.#updateStruct));
    this.bindTo.removeChangeListener(bind(this, this.#updateP12FileFromStruct));
  }
}

export default GoogleAnalyticsRetrievalFieldsBase;
