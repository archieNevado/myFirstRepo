import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import AbstractRemoteTest from "@coremedia/studio-client.client-core-test-helper/AbstractRemoteTest";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as, cast, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import GoogleAnalyticsReportPreviewButton from "../src/GoogleAnalyticsReportPreviewButton";
import GoogleAnalyticsStudioButtonTestView from "./GoogleAnalyticsStudioButtonTestView";

class GoogleAnalyticsStudioButtonTest extends AbstractRemoteTest {

  #viewPort: Viewport = null;

  constructor() {
    super();
  }

  override setUp(): void {
    super.setUp();

    EditorContextImpl.initEditorContext();

    this.#viewPort = new GoogleAnalyticsStudioButtonTestView(
      Config(GoogleAnalyticsStudioButtonTestView, { contentValueExpression: ValueExpressionFactory.createFromValue() }));
  }

  override tearDown(): void {
    super.tearDown();
  }

  testButtonDisabled(): void {
    const button = as(
      this.#viewPort.down(createComponentSelector()._xtype(GoogleAnalyticsReportPreviewButton.xtype).build()), GoogleAnalyticsReportPreviewButton);
    Assert.assertTrue(button.disabled);
  }

  testDeepLinkReportUrl(): void {
    let args: any = undefined;
    window.open = ((... myArgs): Window => {
      args = myArgs;
      return window;
    });

    const button = as(
      this.#viewPort.down(createComponentSelector()._xtype(GoogleAnalyticsReportPreviewButton.xtype).build()), GoogleAnalyticsReportPreviewButton);
    button.setContent(Object.setPrototypeOf({
      getNumericId: (): int => 42,
      "get": (prop: string): any => {
        if (prop === "type") {
          return { name: "typeWithPreview" };
        }
      },
    }, mixin(class {}, Content).prototype));
    this.waitUntil("button still disabled",
      (): boolean =>
        !button.disabled
      ,
      cast(Function, button.handler), // simulate click
    );
    this.waitUntil("no window opened",
      (): boolean => args !== undefined,
    );
  }

  static readonly #DRILLDOWN_URL: string = "http://host.domain.net/gai/drilldown/42";

  protected override getMockCalls(): Array<any> {
    return [
      {
        "request": { "uri": "alxservice/42" },
        "response": { "body": { "googleAnalytics": GoogleAnalyticsStudioButtonTest.#DRILLDOWN_URL } },
      },
    ];
  }

}

export default GoogleAnalyticsStudioButtonTest;
