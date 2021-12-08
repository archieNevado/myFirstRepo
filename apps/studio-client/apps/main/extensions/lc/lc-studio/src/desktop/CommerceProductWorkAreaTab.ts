import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceAttributesForm from "./CommerceAttributesForm";
import CommerceProductContentForm from "./CommerceProductContentForm";
import CommerceProductStructureForm from "./CommerceProductStructureForm";
import CommerceSystemForm from "./CommerceSystemForm";
import CommerceWorkAreaTab from "./CommerceWorkAreaTab";

interface CommerceProductWorkAreaTabConfig extends Config<CommerceWorkAreaTab> {
}

class CommerceProductWorkAreaTab extends CommerceWorkAreaTab {
  declare Config: CommerceProductWorkAreaTabConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceProductWorkAreaTab";

  static readonly CONTENT_TAB_ITEM_ID: string = "contentTab";

  static readonly STRUCTURE_TAB_ITEM_ID: string = "structureTab";

  static readonly SYSTEM_TAB_ITEM_ID: string = "systemTab";

  constructor(config: Config<CommerceProductWorkAreaTab> = null) {
    super(ConfigUtils.apply(Config(CommerceProductWorkAreaTab, {

      subTabs: [
        Config(CommerceProductContentForm, { itemId: CommerceProductWorkAreaTab.CONTENT_TAB_ITEM_ID }),
        Config(CommerceAttributesForm),
        Config(CommerceProductStructureForm, { itemId: CommerceProductWorkAreaTab.STRUCTURE_TAB_ITEM_ID }),
        Config(CommerceSystemForm, {
          autoHide: true,
          itemId: CommerceProductWorkAreaTab.SYSTEM_TAB_ITEM_ID,
        }),
      ],

    }), config));
  }
}

export default CommerceProductWorkAreaTab;
