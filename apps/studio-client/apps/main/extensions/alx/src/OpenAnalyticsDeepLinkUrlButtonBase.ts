import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import OpenAnalyticsDeepLinkUrlButton from "./OpenAnalyticsDeepLinkUrlButton";
import OpenAnalyticsUrlButtonBase from "./OpenAnalyticsUrlButtonBase";

interface OpenAnalyticsDeepLinkUrlButtonBaseConfig extends Config<OpenAnalyticsUrlButtonBase>, Partial<Pick<OpenAnalyticsDeepLinkUrlButtonBase,
  "contentExpression"
>> {
}

class OpenAnalyticsDeepLinkUrlButtonBase extends OpenAnalyticsUrlButtonBase {
  declare Config: OpenAnalyticsDeepLinkUrlButtonBaseConfig;

  static readonly NO_PREVIEW_TYPES: Array<any> = editorContext._.getDocumentTypesWithoutPreview();

  contentExpression: ValueExpression = null;

  #uriExpression: ValueExpression = null;

  constructor(config: Config<OpenAnalyticsDeepLinkUrlButton> = null) {
    super(config);
    const localBean = beanFactory._.createLocalBean();

    this.#uriExpression = ValueExpressionFactory.create("serviceUrl", localBean);

    this.#getAlxServiceBean();
    this.contentExpression.addChangeListener(bind(this, this.#getAlxServiceBean));
  }

  setContent(content: Content): void {
    this.contentExpression.setValue(content);
  }

  override initUrlValueExpression(): void {
    (this as unknown)["urlValueExpression"] = this.getAlxReportUrl();
  }

  #getAlxServiceBean(): void {
    if (this.contentExpression && this.contentExpression.getValue()) {
      const content: Content = this.contentExpression.getValue();
      // if content has no preview, it cannot be rendered by CAE and thus has no report URL link either
      const nameValueExpression = ValueExpressionFactory.create("type.name", content);
      nameValueExpression.loadValue((typeName: string): void => {
        if (OpenAnalyticsDeepLinkUrlButtonBase.NO_PREVIEW_TYPES.indexOf(typeName) < 0) {
          const id: int = IdHelper.parseContentId(content);
          this.#uriExpression.setValue("alxservice/" + id);
        }
      });
    }
  }

  protected getAlxReportUrl(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((name: string): string => {
      const uri: string = this.#uriExpression && this.#uriExpression.getValue(); // uriExpression maybe null
      if (typeof(uri) === "string") {
        const remoteBean = beanFactory._.getRemoteBean(uri);
        if (remoteBean) {
          return ValueExpressionFactory.create<string>(name, remoteBean).getValue();
        }
      }
      return null;
    }, this.serviceName);
  }

}

export default OpenAnalyticsDeepLinkUrlButtonBase;
