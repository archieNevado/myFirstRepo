package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.base.components.util.StringHelper;
import com.coremedia.blueprint.studio.struct.StructEditor_properties;
import com.coremedia.blueprint.studio.struct.XMLUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.ContentTypes_properties;
import com.coremedia.ui.data.impl.BeanImpl;
import com.coremedia.ui.util.EncodingUtil;

import ext.tree.TreeNode;
import ext.util.StringUtil;

import js.Element;
import js.Node;

/**
 * The model that represents each node of the tree.
 */
public class ElementModel extends BeanImpl {
  private var type:int;
  private var formattedLinkTypeName:String;
  private var formattedContentName:String;

  public function ElementModel(type:int, node:Node = undefined) {
    this.type = type;
    if (node) {
      //the first child is the text node, so we set the value property using the first child's node value.
      if (node.firstChild) {
        set(ElementConstants.VALUE_PROPERTY, node.firstChild.nodeValue);
      }
      var attributes:* = node['attributes'];
      if (attributes) {
        set(ElementConstants.NAME_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.NAME_PROPERTY));
        set(ElementConstants.MIN_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.MIN_PROPERTY));
        set(ElementConstants.MAX_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.MAX_PROPERTY));
        set(ElementConstants.LENGTH_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.LENGTH_PROPERTY));
        set(ElementConstants.LINK_TYPE_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.LINK_TYPE_PROPERTY));
        set(ElementConstants.HREF_PROPERTY, XMLUtil.getAttributeValue(attributes, ElementConstants.HREF_PROPERTY));
      }
    }
  }

  /**
   * Copies all values of this node into the new element model.
   * @param newModel The model to copy the values into.
   */
  public function copyTo(newModel:ElementModel):void {
    newModel.formattedContentName = this.formattedContentName;
    newModel.formattedLinkTypeName = this.formattedLinkTypeName;
    newModel.set(ElementConstants.NAME_PROPERTY, getName());
    newModel.set(ElementConstants.MIN_PROPERTY, getMin());
    newModel.set(ElementConstants.MAX_PROPERTY, getMax());
    newModel.set(ElementConstants.LENGTH_PROPERTY, getLength());
    newModel.set(ElementConstants.LINK_TYPE_PROPERTY, getLinkType());
    newModel.set(ElementConstants.HREF_PROPERTY, getHRef());
    newModel.set(ElementConstants.VALUE_PROPERTY, getValue());
  }
  
  public function getFormattedLinkTypeName():String{
    return EncodingUtil.encodeForHTML(formattedLinkTypeName);
  }
  
  public function getFormattedContentName():String {
    return EncodingUtil.encodeForHTML(formattedContentName);
  }

  public function getRawLinkTypeName():String {
    var type:String = getLinkType();
    if(type) {
      return type.substr(type.lastIndexOf('/')+1, type.length);
    }
    return undefined;
  }

  public function getType():int {
    return type;
  }

  public function getName():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.NAME_PROPERTY));
  }

  public function getValue(escaped:Boolean = false):String {
    var value:String = get(ElementConstants.VALUE_PROPERTY);
    if (value !== undefined && value !== null) {
      if(escaped) {
        value = ""+value; //int to string
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\t'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\r'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\n'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter(' '));
        return XMLUtil.escapeXML(value);
      }
      return value;
    }

    if(getType() === ElementConstants.ELEMENT_INT) {
      return ''+0;
    }
    if(getType() === ElementConstants.ELEMENT_BOOLEAN) {
      return 'false';
    }
    return value;
  }

  public function getHRef():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.HREF_PROPERTY));
  }

  public function getMax():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.MAX_PROPERTY));
  }

  public function getMin():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.MIN_PROPERTY));
  }

  public function getLength():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.LENGTH_PROPERTY));
  }

  public function getLinkType():String {
    return EncodingUtil.encodeForHTML(get(ElementConstants.LINK_TYPE_PROPERTY));
  }

  public function toNodeString(modus:int):String {
    return ElementStringFactory.toNodeString(this, modus);
  }

  /**
   * Creates the JS dom element for the given model.
   * @param document The document to create the element for.
   * @return The corresponding element, including attributes of this bean.
   */
  public function toElement(document:*):Element {
    var xml:String = ElementConstants.NAMES[type];
    var elem:Element = document.createElement(xml);

    if (getName()) {
      elem.setAttribute(ElementConstants.NAME_PROPERTY, getName());
    }
    if (getMin()) {
      elem.setAttribute(ElementConstants.MIN_PROPERTY, getMin());
    }
    if (getMax()) {
      elem.setAttribute(ElementConstants.MAX_PROPERTY, getMax());
    }
    if (getHRef()) {
      elem.setAttribute(ElementConstants.HREF_PROPERTY, getHRef());
    }
    if (getLength()) {
      elem.setAttribute(ElementConstants.LENGTH_PROPERTY, getLength());
    }
    if (getLinkType()) {
      elem.setAttribute(ElementConstants.LINK_TYPE_PROPERTY, getLinkType());
    }

    if (getValue() !== null && getValue() !== undefined) {
      var textNode:Node = document.createTextNode(getValue());
      elem.appendChild(textNode);
    }
    
    if(getType() !== ElementConstants.ELEMENT_ROOT) {
      elem.removeAttribute('xmlns');
    }
    return elem;
  }

  /**
   * Returns true if the given element is valid
   * and contains all mandatory attributes.
   * @return The validation error if there is one or undefined.
   */
  public function validate():String {
    //common property check
    var elementName:String = ElementConstants.NAMES[getType()];
    if(elementName.indexOf('Property') != -1) {
      if(!getName()) {
        return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, ElementConstants.NAME_PROPERTY);
      }
    }
    //link checks
    if(elementName.indexOf('LinkProperty') != -1 || elementName.indexOf('LinkListProperty') != -1) {
      if(!getLinkType()) {
        return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, ElementConstants.LINK_TYPE_PROPERTY);
      }
    }

    //native values check
    if((getType() === ElementConstants.ELEMENT_INT || getType() === ElementConstants.ELEMENT_BOOLEAN) && (getValue() === null || getValue() === undefined)) {
      return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, ElementConstants.VALUE_PROPERTY);
    }
    return undefined;
  }

  /**
   * Refreshs the node with the data of this model.
   * @param activeNode
   */
  public function refresh(activeNode:TreeNode, modus:int):void {
    if (StructHandler.MODUS_FORMATTED == modus) {
      formattedLinkTypeName = getLinkType();
      if(formattedLinkTypeName) {
        formattedLinkTypeName = formattedLinkTypeName.substr(formattedLinkTypeName.lastIndexOf('/')+1, formattedLinkTypeName.length);
        var bundleValue:String = ContentTypes_properties.INSTANCE[formattedLinkTypeName + '_text'];
        if(bundleValue) {
          formattedLinkTypeName = bundleValue;
        }
      }
      else {
        formattedLinkTypeName = undefined;
      }

      if(getHRef()) {
        var linkContent:Content = ContentUtil.getContent(getHRef());
        linkContent.load(function ():void {
          if(linkContent.getState().exists) {
            formattedContentName = linkContent.getName();
          }
          else {
            formattedContentName = getHRef();
          }
          refreshHTML(activeNode, modus);
        });
      }
      else {
        formattedContentName = undefined;
      }
    } //plain mode
    else {
      formattedContentName = getHRef();
      formattedLinkTypeName = getLinkType(); //plain link type formatting
    }
    refreshHTML(activeNode, modus);
  }

  /**
   * Re-creates the HTML for the selected Node.
   */
  private function refreshHTML(activeNode:TreeNode, modus:int):void {
    activeNode.getUI().getEl().childNodes[0].childNodes[3].childNodes[0].innerHTML = toNodeString(modus);
    activeNode.getUI().show();
  }  
}
}