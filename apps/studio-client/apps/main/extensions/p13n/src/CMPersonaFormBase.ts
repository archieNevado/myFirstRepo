import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import StructRemoteBean from "@coremedia/studio-client.cap-rest-client/struct/StructRemoteBean";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import CMPersonaForm from "./CMPersonaForm";

interface CMPersonaFormBaseConfig extends Config<DocumentTabPanel> {
}

class CMPersonaFormBase extends DocumentTabPanel {
  declare Config: CMPersonaFormBaseConfig;

  protected static readonly TAXONOMY_PROPERTY_NAME_EXPLICIT: string = "explicit";

  protected static readonly TAXONOMY_PROPERTY_NAME_IMPLICIT: string = "subjectTaxonomies";

  protected static readonly PROFILE_IMAGE_NAME: string = "profileImage";

  static readonly #PROPERTY_PREFIX_PATH: string = "properties";

  static readonly #CONTENT_TYPE: string = "CMTaxonomy";

  static readonly #PICTURE_TYPE: string = "CMPicture";

  constructor(config: Config<CMPersonaForm> = null) {
    super(config);

    const remoteValue = config.bindTo.extendBy("properties.profileExtensions");

    // lets load the struct from the remoteValue
    remoteValue.loadValue((structRemoteBean: StructRemoteBean): void =>
      // and load the properties from that bean
      structRemoteBean.load((): void =>
        CMPersonaFormBase.#createPropertiesIfNecessary(structRemoteBean),
      ),
    );
  }

  /**
   * Create taxonomy properties if they doesn't exit already. Also, the type of the taxonomy property (Array) is set to
   * its linkListProperty.
   * @param structRemoteBean the bean that holds the struct properties
   * @param contentType the contentType that needs to be added to the LinkListProperty
   */
  static #createPropertiesIfNecessary(structRemoteBean: StructRemoteBean): void {
    let properties: Struct = structRemoteBean.get(CMPersonaFormBase.#PROPERTY_PREFIX_PATH);
    const taxonomyContentType = session._.getConnection().getContentRepository().getContentType(CMPersonaFormBase.#CONTENT_TYPE);
    const pictureContentType = session._.getConnection().getContentRepository().getContentType(CMPersonaFormBase.#PICTURE_TYPE);

    if (!properties) {
      structRemoteBean.getType().addStructProperty(CMPersonaFormBase.#PROPERTY_PREFIX_PATH);
      properties = structRemoteBean.get(CMPersonaFormBase.#PROPERTY_PREFIX_PATH);
    }

    if (!properties.get(CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_EXPLICIT)) {
      properties.getType().addLinkListProperty(CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_EXPLICIT, taxonomyContentType, []);
    }

    if (!properties.get(CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_IMPLICIT)) {
      properties.getType().addLinkListProperty(CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_IMPLICIT, taxonomyContentType, []);
    }

    if (!properties.get(CMPersonaFormBase.PROFILE_IMAGE_NAME)) {
      properties.getType().addLinkListProperty(CMPersonaFormBase.PROFILE_IMAGE_NAME, pictureContentType, []);
    }
  }

}

export default CMPersonaFormBase;
