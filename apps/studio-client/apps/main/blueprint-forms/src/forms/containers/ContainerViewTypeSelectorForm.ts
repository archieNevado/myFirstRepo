import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ViewTypeSelectorForm from "./ViewTypeSelectorForm";

interface ContainerViewTypeSelectorFormConfig extends Config<ViewTypeSelectorForm> {
}

class ContainerViewTypeSelectorForm extends ViewTypeSelectorForm {
  declare Config: ContainerViewTypeSelectorFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.containerViewTypeSelectorForm";

  constructor(config: Config<ContainerViewTypeSelectorForm> = null) {
    super(ConfigUtils.apply(Config(ContainerViewTypeSelectorForm, { paths: ["/Settings/Options/Viewtypes/CMChannel/", "Options/Viewtypes/CMChannel/"] }), config));
  }
}

export default ContainerViewTypeSelectorForm;
