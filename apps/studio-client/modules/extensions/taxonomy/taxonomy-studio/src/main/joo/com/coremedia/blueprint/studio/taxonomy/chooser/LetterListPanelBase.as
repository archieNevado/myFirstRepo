package com.coremedia.blueprint.studio.taxonomy.chooser {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.models.bem.BEMBlock;
import com.coremedia.ui.models.bem.BEMElement;
import com.coremedia.ui.skins.LoadMaskSkin;
import com.coremedia.ui.store.BeanRecord;

import ext.LoadMask;
import ext.Template;
import ext.XTemplate;
import ext.container.Container;
import ext.event.Event;
import ext.view.DataView;

import js.HTMLElement;

/**
 * Displays the active taxonomy node sorted alphabetically.
 */
public class LetterListPanelBase extends Container {
  public const ITEMS_CONTAINER_ITEM_ID:String = "itemsContainer";

  /**
   * Contains the row that children should be displayed next.
   */
  [ExtConfig]
  public var nodePathExpression:ValueExpression;

  protected static const LIST_BLOCK:BEMBlock = new BEMBlock("widget-content-list");

  protected static const LIST_ELEMENT_ENTRY:BEMElement = LIST_BLOCK.createElement("entry");

  [ExtConfig]
  public var loadingExpression:ValueExpression;

  private var listValuesExpression:ValueExpression;
  private var activeLetters:ValueExpression;
  private var selectedLetter:ValueExpression;
  private var selectionExpression:ValueExpression;
  private var selectedNodeList:ValueExpression;

  private var taxonomyId:String;
  private var activeNodeList:TaxonomyNodeList;


  //used for skipping letter column rendering
  private var letter2NodeMap:Bean;

  private var singleSelection:Boolean;

  protected static const TEMPLATE:Template = new XTemplate(
          '<table class="' + LIST_BLOCK + '">',
          '  <tpl for=".">',
          '    <tr class="' + LIST_ELEMENT_ENTRY + ' cm-taxonomy-row">',
          '      <td style="font-weight:bold; width: 30px; text-align: center;"><b>{letter}</b>',
          '      </td>',
          '      <td>',
          '        <span class="cm-taxonomy-node {customCss}" style="cursor:pointer;">',
          '           <span class="cm-taxonomy-node__box">',
          '             <span class="cm-taxonomy-node__name">',
          '               <tpl if="!leaf"><b>{name}</b></tpl>',
          '               <tpl if="leaf">{name}</tpl>',
          '             </span>',
          '             <tpl if="renderControl">',
          '               <tpl if="!added"><span class="cm-taxonomy-node__control cm-core-icons cm-core-icons--add-special-size" data-ref="{ref}"></span></tpl>',
          '               <tpl if="added"><span class="cm-taxonomy-node__control cm-core-icons cm-core-icons--remove-small" data-ref="{ref}"></span></tpl>',
          '             </tpl>',
          '           </span>',
          '         </span>',
          '      </td>',
          '      <td style="width:16px;">',
          '        <tpl if="!leaf"><span style="cursor:pointer;" width="16" height="16" class="cm-core-icons cm-core-icons--arrow-right"></span></tpl>',
          '      </td>',
          '      <td style="width:16px;">',
          '      </td>',
          '    </tr>',
          '  </tpl>',
          '</table>'
  ).compile();

  private var loadMask:LoadMask;

  public function LetterListPanelBase(config:LetterListPanel = null) {
    super(config);
    singleSelection = config.singleSelection;
    activeLetters = config.activeLetters;
    taxonomyId = config.taxonomyId;
    activeLetters = config.activeLetters;

    selectionExpression = config.selectionExpression;

    selectedNodeList = config.selectedNodeList;
    selectedNodeList.addChangeListener(updateUI);

    selectedLetter = config.selectedLetter;
    selectedLetter.addChangeListener(updateSelectedLetter);

    config.loadingExpression.addChangeListener(loadingChanged);
  }

  private function loadingChanged(ve:ValueExpression):void {
    var loading:Boolean = ve.getValue();
    if (loading) {
      loadMask.show();
    }
    else {
      loadMask.hide();
    }
  }

  override protected function afterRender():void {
    super.afterRender();
    nodePathExpression.setValue(taxonomyId); //lets start with the root level to show

    // listen to click events
    getDataView().on("itemclick", listEntryClicked);

    loadMask = createLoadMask();
  }

  private function createLoadMask():LoadMask {
    var loadMaskConfig:LoadMask = LoadMask({});
    loadMaskConfig.ui = LoadMaskSkin.LIGHT.getSkin();
    loadMaskConfig.msg = resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyExplorerColumn_emptyText_loading');
    loadMaskConfig.target = this.up();
    loadMaskConfig.baseCls = "cm-thread-load-mask";
    loadMaskConfig.style = "background:rgba(0,0,0,0.65);opacity:1;z-index:1001";
    var loadMask:LoadMask = new LoadMask(loadMaskConfig);
    return loadMask;
  }

  private function listEntryClicked(dataView:DataView, record:BeanRecord, node:HTMLElement, index:Number, e:Event):void {
    // make sure only text element can be clicked
    if (e.getTarget(".cm-taxonomy-node__name", null, true)) {
      var bean:Bean = record.getBean();
      var ref:String = bean.get('ref');
      nodeClicked(ref);
    }
    else if (e.getTarget(".cm-taxonomy-node__control", null, true)) {
      var childBean:Bean = record.getBean();
      var childRef:String = childBean.get('ref');
      plusMinusClicked(childRef);
    }
    else if (e.getTarget(".cm-core-icons--arrow-right", null, true)) {
      var pathBean:Bean = record.getBean();
      var pathRef:String = pathBean.get('ref');
      updateSelection(pathRef);
    }
  }

  protected function getListValuesExpression():ValueExpression {
    if (!listValuesExpression) {
      listValuesExpression = ValueExpressionFactory.create("nodes", beanFactory.createLocalBean());
    }
    return listValuesExpression;
  }

  /**
   * Selects the entry in the list with the active letter
   */
  private function updateSelectedLetter():void {
    var letter:String = selectedLetter.getValue().toLowerCase();
    if (letter) {
      for (var i:int = 0; i < activeNodeList.getNodes().length; i++) {
        var node:TaxonomyNode = activeNodeList.getNodes()[i];
        var itemLetter:String = letterRenderer(node).toLowerCase();
        if (itemLetter === letter) {
          var table:* = this.el.query(LIST_BLOCK.getCSSSelector())[0];
          table.firstElementChild.children[i].scrollIntoView();
          break;
        }
      }
    }
  }

  /**
   * Refresh the path and list and button column.
   */
  public function updateUI():void {
    var list:TaxonomyNodeList = selectedNodeList.getValue();
    if (list) {
      doUpdate();
    }
  }

  private function doUpdate():void {
    loadingExpression.setValue(true);
    //give the load mask some time to appear, otherwise the dialog may look stuck
    window.setTimeout(function ():void {
      var list:TaxonomyNodeList = selectedNodeList.getValue();
      if (list) {
        activeNodeList = list;
        letter2NodeMap = beanFactory.createLocalBean();
        updateLetterList(list);

        var nodes:Array = activeNodeList.getNodes();
        var result:Array = [];
        for each(var node:TaxonomyNode in nodes) {
          var bean:Bean = beanFactory.createLocalBean({});
          var letter:String = letterRenderer(node);
          bean.set('letter', letter);
          bean.set('name', node.getName());
          bean.set('leaf', node.isLeaf());
          bean.set('ref', node.getRef());

          var selection:Array = selectionExpression.getValue();
          var added:Boolean = TaxonomyUtil.isInSelection(selection, node.getRef());
          bean.set('added', added);
          if (added) {
            bean.set('customCss', "cm-taxonomy-node--leaf");
          }

          bean.set('renderControl', added || (singleSelection && selection.length === 0) || !singleSelection);
          result.push(bean);
        }
        getListValuesExpression().setValue(result);
        loadingExpression.setValue(false);
      }
    }, 50);
  }

  private function getDataView():DataView {
    return queryById(ITEMS_CONTAINER_ITEM_ID) as DataView;
  }

  /**
   * Fills the letter value expression with an array of the active letters.
   * @param list
   */
  private function updateLetterList(list:TaxonomyNodeList):void {
    var letters:Array = [];
    var nodes:Array = list.getNodes();
    for (var i:int = 0; i < nodes.length; i++) {
      var name:String = nodes[i].getName();
      letters.push(name.substr(0, 1).toLowerCase());
    }
    activeLetters.setValue(letters);
  }


  /**
   * Fired when the user double clicks a row.
   * The next taxonomy child level of the selected node is entered then.
   */
  private function updateSelection(ref:String):void {
    if (ref) {
      var id:String = TaxonomyUtil.getRestIdFromCapId(ref);
      if (activeNodeList && !activeNodeList.getNode(id).isLeaf()) {
        //fire event for path update
        nodePathExpression.setValue(id);
      }
    }
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each letter of a taxonomy
   */
  public function letterRenderer(node:TaxonomyNode):String {
    var letter:String = node.getName().substr(0, 1).toUpperCase();
    var html:String = '';
    if (!letter2NodeMap.get(letter) || letter2NodeMap.get(letter).getRef() === node.getRef()) {
      html = letter;
      letter2NodeMap.set(letter, node);
    }
    return html;
  }

  /**
   * Handler executed when the node text is clicked on.
   */
  public function nodeClicked(ref:String):void {
    updateSelection(ref); //has the same behaviour like when double clicking a row.
  }

  /**
   * Handler executed when the plus button is clicked.
   * Used in TaxonomyRenderer#plusMinusClicked$static
   */
  public function plusMinusClicked(nodeRef:String):void {
    var parent:TaxonomySelectionWindow = findParentByType(TaxonomySelectionWindow.xtype) as TaxonomySelectionWindow;
    parent.updateSelection(nodeRef, true, true);
  }
}
}
