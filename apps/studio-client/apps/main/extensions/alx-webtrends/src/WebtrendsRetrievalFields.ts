import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import WebtrendsStudioPlugin_properties from "./WebtrendsStudioPlugin_properties";

interface WebtrendsRetrievalFieldsConfig extends Config<PropertyFieldGroup> {
}

class WebtrendsRetrievalFields extends PropertyFieldGroup {
  declare Config: WebtrendsRetrievalFieldsConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.webtrends.webtrendsRetrievalFields";

  constructor(config: Config<WebtrendsRetrievalFields> = null) {
    super(ConfigUtils.apply(Config(WebtrendsRetrievalFields, {
      itemId: "webtrendsRetrievalForm",
      title: WebtrendsStudioPlugin_properties.SpacerTitle_webtrends_retrieval,

      items: [
        Config(StringPropertyField, {
          propertyName: "localSettings.webtrends.accountName",
          itemId: "accountName",
        }),
        Config(StringPropertyField, {
          propertyName: "localSettings.webtrends.userName",
          itemId: "userName",
        }),
        Config(StringPropertyField, {
          propertyName: "localSettings.webtrends.password",
          itemId: "password",
        }),
        Config(IntegerPropertyField, {
          propertyName: "localSettings.webtrends.profileId",
          itemId: "profileId",
        }),
        Config(StringPropertyField, {
          propertyName: "localSettings.webtrends.reportId",
          itemId: "reportId",
        }),
        Config(StringPropertyField, {
          propertyName: "localSettings.webtrends.sortByMeasure",
          itemId: "sortByMeasure",
        }),
        Config(IntegerPropertyField, {
          propertyName: "localSettings.webtrends.limit",
          itemId: "limit",
        }),
        Config(IntegerPropertyField, {
          propertyName: "localSettings.webtrends.interval",
          itemId: "interval",
        }),
      ],

    }), config));
  }
}

export default WebtrendsRetrievalFields;
