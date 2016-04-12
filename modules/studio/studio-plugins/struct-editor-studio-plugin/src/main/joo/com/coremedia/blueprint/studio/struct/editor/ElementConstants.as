package com.coremedia.blueprint.studio.struct.editor {
public class ElementConstants {
  public static const ELEMENT_ROOT:int = 0;

  public static const ELEMENT_INT_LIST_PROPERTY:int = 1;
  public static const ELEMENT_INT_PROPERTY:int = 2;
  public static const ELEMENT_INT:int = 3;

  public static const ELEMENT_STRING_LIST_PROPERTY:int = 4;
  public static const ELEMENT_STRING_PROPERTY:int = 5;
  public static const ELEMENT_STRING:int = 6;

  public static const ELEMENT_BOOLEAN_LIST_PROPERTY:int = 7;
  public static const ELEMENT_BOOLEAN_PROPERTY:int = 8;
  public static const ELEMENT_BOOLEAN:int = 9;

  public static const ELEMENT_LINK_LIST_PROPERTY:int = 10;
  public static const ELEMENT_LINK_PROPERTY:int = 11;
  public static const ELEMENT_LINK:int = 12;

  public static const ELEMENT_STRUCT_LIST_PROPERTY:int = 13;
  public static const ELEMENT_STRUCT_PROPERTY:int = 14;
  public static const ELEMENT_STRUCT:int = 15;

  public static var NAMES:Array = [];
  {
    NAMES[ELEMENT_ROOT] = 'Struct xmlns="http://www.coremedia.com/2008/struct" xmlns:xlink="http://www.w3.org/1999/xlink"';

    NAMES[ELEMENT_INT_LIST_PROPERTY] = 'IntListProperty';
    NAMES[ELEMENT_INT_PROPERTY] = 'IntProperty';
    NAMES[ELEMENT_INT] = 'Int';

    NAMES[ELEMENT_STRING_LIST_PROPERTY] = 'StringListProperty';
    NAMES[ELEMENT_STRING_PROPERTY] = 'StringProperty';
    NAMES[ELEMENT_STRING] = 'String';

    NAMES[ELEMENT_BOOLEAN_LIST_PROPERTY] = 'BooleanListProperty';
    NAMES[ELEMENT_BOOLEAN_PROPERTY] = 'BooleanProperty';
    NAMES[ELEMENT_BOOLEAN] = 'Boolean';

    NAMES[ELEMENT_LINK_LIST_PROPERTY] = 'LinkListProperty';
    NAMES[ELEMENT_LINK_PROPERTY] = 'LinkProperty';
    NAMES[ELEMENT_LINK] = 'Link';

    NAMES[ELEMENT_STRUCT_LIST_PROPERTY] = 'StructListProperty';
    NAMES[ELEMENT_STRUCT_PROPERTY] = 'StructProperty';
    NAMES[ELEMENT_STRUCT] = 'Struct';
  }

  public static const TYPES:Array = [
    ELEMENT_STRING, ELEMENT_STRING_LIST_PROPERTY, ELEMENT_STRING_PROPERTY,
    ELEMENT_BOOLEAN, ELEMENT_BOOLEAN_LIST_PROPERTY, ELEMENT_BOOLEAN_PROPERTY,
    ELEMENT_INT, ELEMENT_INT_LIST_PROPERTY, ELEMENT_INT_PROPERTY,
    ELEMENT_STRUCT, ELEMENT_STRUCT_LIST_PROPERTY, ELEMENT_STRUCT_PROPERTY,
    ELEMENT_LINK, ELEMENT_LINK_LIST_PROPERTY, ELEMENT_LINK_PROPERTY
  ];

  public static const NAME_PROPERTY:String = 'Name';
  public static const VALUE_PROPERTY:String = 'Value';
  public static const HREF_PROPERTY:String = 'xlink:href';
  public static const MIN_PROPERTY:String = 'Min';
  public static const MAX_PROPERTY:String = 'Max';
  public static const LENGTH_PROPERTY:String = 'Length';
  public static const LINK_TYPE_PROPERTY:String = 'LinkType';

}
}