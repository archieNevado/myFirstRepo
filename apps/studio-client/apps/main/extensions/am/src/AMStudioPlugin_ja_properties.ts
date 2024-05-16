import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AMStudioPlugin_properties from "./AMStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "AMStudioPlugin" for Locale "ja".
 * @see AMStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(AMStudioPlugin_properties, { ExpirationDate_dateFormat: "Y年m月d日" });
