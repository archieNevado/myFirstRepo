import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import Ext from "@jangaroo/ext-ts";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import OSMPanel from "./OSMPanel";
import OSMStudioPlugin_properties from "./OSMStudioPlugin_properties";

interface OSMPanelBaseConfig extends Config<Panel>, Config<HidableMixin>, Partial<Pick<OSMPanelBase,
  "hideText"
>> {
}

/**
 * The open streetmap panel implementation, including marker moving
 * and position updating.
 *
 * Note that the implementation is based on the OpenLayers API.
 */
class OSMPanelBase extends Panel {
  declare Config: OSMPanelBaseConfig;

  static readonly #MARKER_Z_INDEX: int = 1000;

  #zoom: int = 5;

  #latLngExpression: ValueExpression = null;

  #map;

  #marker: any;

  #vectorLayer: any;

  #skipCalcZoomLevel: boolean = false;

  constructor(config: Config<OSMPanel> = null) {
    super(config);
    this.#latLngExpression = config.latLngExpression;
    this.addListener("afterlayout", bind(this, this.#initMap));
    this.addListener("afterlayout", bind(this, this.#resized));
    this.#calcZoomLevel();
  }

  /**
   * Fired after the panel has been resized.
   */
  #resized(): void {
    this.#map.updateSize();
  }

  #calcZoomLevel(): void {
    //set by the taxonomy manager
    const level: int = editorContext._.getApplicationContext().get("taxonomy_node_level");
    if (level && level <= 1) {
      this.#zoom = (level + 2);
    } else if (level && level <= 2) {
      this.#zoom = (level + 3);
    } else if (level && level <= 3) {
      this.#zoom = (level + 4);
    } else if (level && level <= 5) {
      this.#zoom = (level + 5);
    } else {
      this.#zoom = 11;
    }
  }

  /**
   * Initialize the map and add it to the dom.
   */
  #initMap(): void {
    this.removeListener("afterlayout", bind(this, this.#initMap));

    const dispatcher = as(this.findParentByType(TabbedDocumentFormDispatcher), TabbedDocumentFormDispatcher);
    if (dispatcher) {
      dispatcher.addListener("resize", bind(this, this.#resized));
    }

    this.#latLngExpression.loadValue((): void => {
      this.#createMap();
      this.#createMarkerLayer();
      this.#createDragFeature();
      this.#setMarker();

      // Listen to changes on latitude/longitude expression
      this.#latLngExpression.addChangeListener(bind(this, this.#setMarker));
    });
  }

  /**
   * Creates the map and its navigation controls.
   */
  #createMap(): void {
    //determine the panel element where the map should be added, (could be improved via findBy?)
    const mapId = this.body.getAttribute("id"); //nothing special, just the generated ExtJs id.
    this.#map = new OpenLayers.Map(mapId, {
      autoUpdateSize: true,
      projection: new OpenLayers.Projection("EPSG:900913"),
      displayProjection: new OpenLayers.Projection("EPSG:4326"),
      controls: [
        new OpenLayers["Control"].Navigation({ documentDrag: true }),
        new OpenLayers["Control"].PanZoomBar(),
      ],
      maxExtent: new OpenLayers.Bounds(-20037508.34, -20037508.34,
        20037508.34, 20037508.34),
      numZoomLevels: 10,
      maxResolution: 156543,
      units: "meters",
    });

    //adds the actual map layer to the map. enable the layer switcher control to see which layers are added to the map.
    const osmLayer = new OpenLayers["Layer"].OSM();
    this.#map.addLayer(osmLayer);
  }

  /**
   * OpenLayers (as the name implies) uses different layers to control the map.
   * We add a vector layer here so that geometric figures like 'points' can be drawn on it.
   * Additionally, the layer is styled afterwards, in this case we use the base64 encoded
   * marker as graphic for the layer.
   */
  #createMarkerLayer(): void {
    let renderer = OpenLayers["Util"].getParameters(window.location.href).renderer;
    renderer = (renderer) ? [renderer] : OpenLayers["Layer"].Vector.prototype["renderers"];

    this.#vectorLayer = new OpenLayers["Layer"].Vector("Marker Drop Shadows", {
      styleMap: new OpenLayers["StyleMap"]({
        // Set the external graphic and background graphic images.
        externalGraphic: this.#getMarker(),
        graphicYOffset: -25,

        // Set the z-indexes of both graphics to make sure the background
        // graphics stay in the background (shadows on top of markers looks
        // odd; let's not do that).
        graphicZIndex: OSMPanelBase.#MARKER_Z_INDEX,
        pointRadius: 12, //marker height
      }),
      isBaseLayer: false,
      rendererOptions: { yOrdering: true },
      renderers: renderer,
    });

    //add the layer to the map. enable the layer switcher control to see which layers are added to the map.
    this.#map.addLayer(this.#vectorLayer);
  }

  /**
   * The drag and drop feature is used to support
   * the point dragging on the vector layer.
   * There is no "real" marker here, but a geometric point instead with the marker layout.
   */
  #createDragFeature(): void {
    const modifyFeaturesControl = new OpenLayers["Control"].ModifyFeature(this.#vectorLayer);
    modifyFeaturesControl.mode = OpenLayers["Control"].ModifyFeature.RESHAPE;
    this.#map.addControl(modifyFeaturesControl);
    modifyFeaturesControl.activate();

    // Add a drag feature control to move features around.
    const dragFeature = new OpenLayers["Control"].DragFeature(this.#vectorLayer, {
      autoActivate: true,
      onComplete: (layer, xy) => {
        const px = new OpenLayers["Pixel"](xy.x, xy.y + 16); //TODO mmmh, somehow more precisely?

        //we have to transform the coordinates back to the system that is used in the blueprint.
        const lonLat = this.#map.getLonLatFromPixel(px).transform(
          this.#map.getProjectionObject(), // transform from WGS 1984
          new OpenLayers.Projection("EPSG:4326"));

        const newLatLng = lonLat.lat + "," + lonLat.lon;

        //remember current zoom level
        this.#zoom = this.#map.getZoom();
        editorContext._.getApplicationContext().set("taxonomy_node_level", this.#zoom);
        this.#skipCalcZoomLevel = true;
        this.#latLngExpression.setValue(newLatLng);
      },
    });
    this.#map.addControl(dragFeature);
    dragFeature.activate();
  }

  /**
   * Initial setup of the marker.
   * Will create and place the marker, set the zoom level and center the map.
   */
  #setMarker(): void {
    //the fallback coord.
    let lat = 53.5492;
    let lon = 9.9803;

    if (this.#latLngExpression.getValue()) {
      lon = this.#getLongitude();
      lat = this.#getLatitude();
    }

    const lonLat = this.#getLatLon(lat, lon);

    //skip zoom change if the marker was moved!
    if (!this.#skipCalcZoomLevel) {
      this.#calcZoomLevel();
    }
    this.#skipCalcZoomLevel = false;

    if (this.#marker) {
      this.#vectorLayer.removeAllFeatures();
    }
    this.#marker = new OpenLayers["Feature"].Vector(new OpenLayers["Geometry"].Point(lonLat.lon, lonLat.lat));
    this.#map.zoomIn(this.#zoom);
    this.#vectorLayer.addFeatures([this.#marker]);
    this.#map.setCenter(lonLat, this.#zoom);
  }

  /**
   * Returns the latitude value.
   * @return
   */
  #getLatitude(): number {
    const latLngArray: Array<any> = this.#latLngExpression.getValue().split(",");
    const latitude = Number(latLngArray[0]);
    return latitude;
  }

  /**
   * Returns the longitude value.
   * @return
   */
  #getLongitude(): number {
    const latLngArray: Array<any> = this.#latLngExpression.getValue().split(",");
    const longitude = Number(latLngArray[1]);
    return longitude;
  }

  /**
   * Resolves the marker that is used to show the lat/long value.
   */
  #getMarker(): string {
    const groupId = OSMStudioPlugin_properties["osm.groupId"];
    if (groupId && groupId.length > 0) {
      const markerUrl = Ext.getResourcePath("osm/img/marker.png", null, groupId);
      if (markerUrl) {
        return markerUrl;
      }
    }

    return OSMStudioPlugin_properties["osm.marker"];
  }

  /**
   * Returns the current latitude and longitude.
   * @return
   */
  #getLatLon(lat: any, lon: any): any {
    const latLng: any = new OpenLayers["LonLat"](lon, lat)
      .transform(
        new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
        this.#map.getProjectionObject(), // to Spherical Mercator Projection
      );
    return latLng;
  }

  override onRemoved(destroying: boolean): void {
    this.#latLngExpression && this.#latLngExpression.removeChangeListener(bind(this, this.#setMarker));

    super.onRemoved(destroying);
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return as(this.getTitle(), String);
  }

}

interface OSMPanelBase extends HidableMixin{}

mixin(OSMPanelBase, HidableMixin);

export default OSMPanelBase;
