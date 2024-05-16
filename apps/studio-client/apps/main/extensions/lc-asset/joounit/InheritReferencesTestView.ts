import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import PropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyField";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceReferencesForm from "../src/components/CommerceReferencesForm";
import InheritReferencesButton from "../src/components/InheritReferencesButton";

interface InheritReferencesTestViewConfig extends Config<Viewport>, Partial<Pick<InheritReferencesTestView,
  "bindTo" |
  "forceReadOnlyValueExpression"
>> {
}

class InheritReferencesTestView extends Viewport {
  declare Config: InheritReferencesTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.asset.studio.config.inheritReferencesTestView";

  static readonly TEST_VIEW_ID: string = "viewport";

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  constructor(config: Config<InheritReferencesTestView> = null) {
    super(ConfigUtils.apply(Config(InheritReferencesTestView, {

      defaultType: PropertyField.xtype,

      defaults: Config<PropertyField>({
        bindTo: config.bindTo,
        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
      }),
      items: [
        Config(CommerceReferencesForm, {
          additionalToolbarItems: [
            Config(InheritReferencesButton, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default InheritReferencesTestView;
