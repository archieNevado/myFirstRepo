import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "BlueprintDeviceTypes".
 * @see BlueprintDeviceTypes_properties#INSTANCE
 */
interface BlueprintDeviceTypes_properties {

/**
 * Icon Style classes for the responsive slider
 */
  Device_mobile_portrait_icon: string;
  Device_mobile_landscape_icon: string;
  Device_tablet_portrait_icon: string;
  Device_tablet_landscape_icon: string;
  Device_notebook_icon: string;
  Device_desktop_icon: string;
  Device_hybrid_app_portrait_icon: string;
  Device_hybrid_app_landscape_icon: string;
/**
 * Names for the devices in the responsive slider
 */
  Device_mobile_portrait_text: string;
  Device_mobile_landscape_text: string;
  Device_tablet_portrait_text: string;
  Device_tablet_landscape_text: string;
  Device_notebook_text: string;
  Device_desktop_text: string;
  Device_hybrid_app_portrait_text: string;
  Device_hybrid_app_landscape_text: string;
  Device_desktopMode_text: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "BlueprintDeviceTypes".
 * @see BlueprintDeviceTypes_properties
 */
const BlueprintDeviceTypes_properties: BlueprintDeviceTypes_properties = {
  Device_mobile_portrait_icon: CoreIcons_properties.channel_mobile_portrait,
  Device_mobile_landscape_icon: CoreIcons_properties.channel_mobile_landscape,
  Device_tablet_portrait_icon: CoreIcons_properties.channel_tablet_portrait,
  Device_tablet_landscape_icon: CoreIcons_properties.channel_tablet_landscape,
  Device_notebook_icon: CoreIcons_properties.channel_notebook,
  Device_desktop_icon: CoreIcons_properties.channel_desktop,
  Device_hybrid_app_portrait_icon: CoreIcons_properties.channel_tablet_portrait,
  Device_hybrid_app_landscape_icon: CoreIcons_properties.channel_tablet_landscape,
  Device_mobile_portrait_text: "Mobile",
  Device_mobile_landscape_text: "Mobile",
  Device_tablet_portrait_text: "Tablet",
  Device_tablet_landscape_text: "Tablet",
  Device_notebook_text: "Notebook",
  Device_desktop_text: "Desktop",
  Device_hybrid_app_portrait_text: "Hybrid App",
  Device_hybrid_app_landscape_text: "Hybrid App",
  Device_desktopMode_text: "Desktop",
};

export default BlueprintDeviceTypes_properties;
