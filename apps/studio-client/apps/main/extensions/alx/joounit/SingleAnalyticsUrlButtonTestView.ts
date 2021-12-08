import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsDeepLinkButtonContainer from "../src/AnalyticsDeepLinkButtonContainer";
import OpenAnalyticsDeepLinkUrlButton from "../src/OpenAnalyticsDeepLinkUrlButton";
import ContentProvidingTestContainer from "./ContentProvidingTestContainer";

interface SingleAnalyticsUrlButtonTestViewConfig extends Config<Viewport>, Partial<Pick<SingleAnalyticsUrlButtonTestView,
  "contentValueExpression"
>> {
}

class SingleAnalyticsUrlButtonTestView extends Viewport {
  declare Config: SingleAnalyticsUrlButtonTestViewConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.singleAnalyticsUrlButtonTestView";

  contentValueExpression: ValueExpression = null;

  constructor(config: Config<SingleAnalyticsUrlButtonTestView> = null) {
    super(ConfigUtils.apply(Config(SingleAnalyticsUrlButtonTestView, {

      items: [
        Config(ContentProvidingTestContainer, {
          itemId: "contentContainer",
          contentValueExpression: config.contentValueExpression,
          items: [
            Config(AnalyticsDeepLinkButtonContainer, {
              itemId: "alxDeepLinkButtonContainer",
              bindTo: config.contentValueExpression,
              items: [
                Config(OpenAnalyticsDeepLinkUrlButton, { serviceName: "testService2" }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default SingleAnalyticsUrlButtonTestView;
