import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsDeepLinkButtonContainer from "../src/AnalyticsDeepLinkButtonContainer";
import OpenAnalyticsDeepLinkUrlButton from "../src/OpenAnalyticsDeepLinkUrlButton";
import ContentProvidingTestContainer from "./ContentProvidingTestContainer";

interface MultipleAnalyticsUrlButtonsTestViewConfig extends Config<Viewport>, Partial<Pick<MultipleAnalyticsUrlButtonsTestView,
  "contentValueExpression"
>> {
}

class MultipleAnalyticsUrlButtonsTestView extends Viewport {
  declare Config: MultipleAnalyticsUrlButtonsTestViewConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.multipleAnalyticsUrlButtonsTestView";

  contentValueExpression: ValueExpression = null;

  constructor(config: Config<MultipleAnalyticsUrlButtonsTestView> = null) {
    super(ConfigUtils.apply(Config(MultipleAnalyticsUrlButtonsTestView, {

      items: [
        Config(ContentProvidingTestContainer, {
          itemId: "contentContainer",
          contentValueExpression: config.contentValueExpression,
          items: [
            Config(AnalyticsDeepLinkButtonContainer, {
              itemId: "alxDeepLinkButtonContainer",
              bindTo: config.contentValueExpression,
              items: [
                Config(OpenAnalyticsDeepLinkUrlButton, { serviceName: "testService1" }),
                Config(OpenAnalyticsDeepLinkUrlButton, { serviceName: "testService2" }),
                Config(OpenAnalyticsDeepLinkUrlButton, { serviceName: "testService3" }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default MultipleAnalyticsUrlButtonsTestView;
