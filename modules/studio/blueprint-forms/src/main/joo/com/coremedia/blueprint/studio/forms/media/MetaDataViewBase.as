package com.coremedia.blueprint.studio.forms.media {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.models.bem.BEMBlock;
import com.coremedia.ui.models.bem.BEMElement;

import ext.XTemplate;

import ext.container.Container;

public class MetaDataViewBase extends Container {

  public static const PROPERTIES_BLOCK:BEMBlock = new BEMBlock("meta-data-view");
  public static const PROPERTIES_ELEMENT_CONTAINER:BEMElement = PROPERTIES_BLOCK.createElement("container");
  public static const PROPERTIES_ELEMENT_LABEL:BEMElement = PROPERTIES_BLOCK.createElement("label");
  public static const PROPERTIES_ELEMENT_TEXT:BEMElement = PROPERTIES_BLOCK.createElement("text");

  private var metaDataExpression:ValueExpression;

  public function MetaDataViewBase(config:MetaDataView = null) {
    super(config);
  }

  public function getMetaDataExpression(metaDataSection:MetaDataSection):ValueExpression {
    if (!metaDataExpression) {
      metaDataExpression = ValueExpressionFactory.createFromValue(metaDataSection.getData());
    }
    return metaDataExpression;
  }

  protected static function getXTemplate():XTemplate {
    var xTemplate:XTemplate = new XTemplate([
      '<table class="' + PROPERTIES_BLOCK + '">',
      '<tpl for=".">',
      '<tr class="' + PROPERTIES_ELEMENT_CONTAINER + '">',
      '<td class="' + PROPERTIES_ELEMENT_LABEL + '">',
      '{property}:',
      '</td>',
      '<td data-qtip="{value:htmlEncode}" class="' + PROPERTIES_ELEMENT_TEXT + '">',
      '{formattedValue}',
      '</td>',
      '</tr>',
      '</tpl>',
      '</table>'
    ]);
    return xTemplate;
  }
}
}