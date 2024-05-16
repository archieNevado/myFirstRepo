import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import AbstractRemoteTest from "@coremedia/studio-client.client-core-test-helper/AbstractRemoteTest";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import Ext from "@jangaroo/ext-ts";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { bind, cast, mixin } from "@jangaroo/runtime";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import OpenAnalyticsDeepLinkUrlButton from "../src/OpenAnalyticsDeepLinkUrlButton";

class OpenAnalyticsDeepLinkUrlButtonTest extends AbstractRemoteTest {

  static readonly #DRILLDOWN_URL: string = "http://host.domain.net/my/drilldown";

  static readonly #MY_ID: number = 4711;

  #button: OpenAnalyticsDeepLinkUrlButton = null;

  #window_open: AnyFunction = null;

  #args: any = null;

  override setUp(): void {
    super.setUp();
    this.#window_open = bind(window, window.open);
    window.open = ((... myArgs): Window => {
      this.#args = myArgs;
      return window;
    });

    EditorContextImpl.initEditorContext();

    this.#button = Ext.create(OpenAnalyticsDeepLinkUrlButton, {
      serviceName: "googleAnalytics",
      contentExpression: ValueExpressionFactory.createFromValue(),
    });
    Ext.create(Viewport, { items: [this.#button] });
  }

  override tearDown(): void {
    super.tearDown();
    this.#button = null;
    window.open = this.#window_open;
    this.#args = null;
  }

  testButtonDisabled(): void {
    Assert.assertTrue(this.#button.disabled);
  }

  testDeepLinkReportUrl(): void {
    this.#button.setContent(Object.setPrototypeOf({
      getNumericId: (): int =>
        OpenAnalyticsDeepLinkUrlButtonTest.#MY_ID
      ,
      "get": (prop: string): any => {
        if (prop === "type") {
          return { name: "typeWithPreview" };
        }
      },
    }, mixin(class {}, Content).prototype));
    this.waitUntil("button still disabled",
      (): boolean =>
        !this.#button.disabled
      ,
      cast(Function, this.#button.handler), // simulate click
    );
    this.waitUntil("no window opened",
      (): boolean =>
        this.#args !== null && this.#args[0] == OpenAnalyticsDeepLinkUrlButtonTest.#DRILLDOWN_URL,

    );
  }

  protected override getMockCalls(): Array<any> {
    return [
      {
        "request": { "uri": "alxservice/" + OpenAnalyticsDeepLinkUrlButtonTest.#MY_ID },
        "response": { "body": { "googleAnalytics": OpenAnalyticsDeepLinkUrlButtonTest.#DRILLDOWN_URL } },
      },
    ];
  }

}

export default OpenAnalyticsDeepLinkUrlButtonTest;
