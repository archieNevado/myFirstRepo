import CKEditor5RichTextArea from "@coremedia/studio-client.ext.ckeditor5-components/CKEditor5RichTextArea";
import Component from "@jangaroo/ext-ts/Component";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";

interface CKEditor5FeatureFlagPluginConfig extends Config<AbstractPlugin> {
  "featureFlags"
}

/**
 * A simple plugin that adds feature flags to any CKEditor5RichTextArea.
 *
 * The featureFlags property in CKEditor5RichTextAreas is used to extend
 * existing editor configurations (e.g., for certain user groups).
 *
 * While feature flags can be set directly at the editor component,
 * the corresponding property can also be extended by additional flags,
 * using this plugin.
 *
 * This plugin adds an array of feature flags to the editor's
 * already present feature flags.
 */
class CKEditor5FeatureFlagPlugin extends AbstractPlugin {
  declare Config: CKEditor5FeatureFlagPluginConfig;

  featureFlags: string[] = [];

  constructor(config: Config<CKEditor5FeatureFlagPlugin> = null) {
    super(config);
    this.featureFlags = config.featureFlags;
  }

  override init(component: Component): void {
    const richTextArea = as(component, CKEditor5RichTextArea);

    // use Set to make sure the entries in this array are unique
    richTextArea.featureFlags = [... new Set(this.featureFlags.concat(...richTextArea.featureFlags ?? []))];
  }
}

export default CKEditor5FeatureFlagPlugin;
