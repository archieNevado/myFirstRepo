import Action from "@jangaroo/ext-ts/Action";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MetadataToEntitiesActionAdapterBase from "./MetadataToEntitiesActionAdapterBase";

interface MetadataToEntitiesActionAdapterConfig extends Config<MetadataToEntitiesActionAdapterBase>, Partial<Pick<MetadataToEntitiesActionAdapter,
  "backingAction" |
  "setEntities"
>> {
}

/**
 *
 * Adapter that implements a MetadataAction based on a backing action.
 *
 * All critical methods are delegated to the backing action after extracting a bean
 * from the underlying metadata. If no bean can be obtained from the MetadataTreeNode
 * (or one of it's parents if useParentNode is enabled), the backing action is configured
 * with metadata properties (if available).
 *
 */
class MetadataToEntitiesActionAdapter extends MetadataToEntitiesActionAdapterBase {
  declare Config: MetadataToEntitiesActionAdapterConfig;

  constructor(config: Config<MetadataToEntitiesActionAdapter> = null) {
    config = ConfigUtils.apply({ setEntities: "setContents" }, config);
    super(ConfigUtils.apply(Config(MetadataToEntitiesActionAdapter), config));
  }

  backingAction: Action = null;

  /**
   * The name of the backing action's function to pass the resolved metadata beans to.
   * Defaults to ContentAction#setContents.
   */
  setEntities: string = null;
}

export default MetadataToEntitiesActionAdapter;
