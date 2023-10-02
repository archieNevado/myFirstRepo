import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import BEMModifier from "@coremedia/studio-client.ext.ui-components/models/bem/BEMModifier";
import Base from "@jangaroo/ext-ts/Base";

class TaxonomyBEMEntities extends Base {

  static readonly NODE_WRAP: BEMBlock = new BEMBlock("cm-taxonomy-node-wrap");

  static readonly NODE_BLOCK: BEMBlock = new BEMBlock("cm-taxonomy-node");

  static readonly NODE_ELEMENT_BOX: BEMElement = TaxonomyBEMEntities.NODE_BLOCK.createElement("box");

  static readonly NODE_ELEMENT_NAME: BEMElement = TaxonomyBEMEntities.NODE_BLOCK.createElement("name");

  static readonly NODE_ELEMENT_CONTROL: BEMElement = TaxonomyBEMEntities.NODE_BLOCK.createElement("control");

  static readonly NODE_ELEMENT_LINK: BEMElement = TaxonomyBEMEntities.NODE_BLOCK.createElement("link");

  static readonly NODE_MODIFIER_ARROW: BEMModifier = TaxonomyBEMEntities.NODE_BLOCK.createModifier("has-children");

  static readonly NODE_MODIFIER_ELLIPSIS: BEMModifier = TaxonomyBEMEntities.NODE_BLOCK.createModifier("ellipsis");

  static readonly NODE_MODIFIER_LEAF: BEMModifier = TaxonomyBEMEntities.NODE_BLOCK.createModifier("leaf");
}

export default TaxonomyBEMEntities;
