package com.coremedia.blueprint.studio.forms {
import com.coremedia.blueprint.base.components.timeline.TimelineEditorBase;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

use namespace editorContext;

use namespace SESSION;

public class CMVideoFormBase extends DocumentTabPanel {

  public function CMVideoFormBase(config:CMVideoForm = null) {
    super(config);
  }

  /**
   * Checks if the view type value matches the given string value.
   * The string value may be in in CSV format.
   * @param bindTo the ValueExpression that contains the content to check the view type value for
   * @param viewTypes the view type names to compare in CSV format
   */
  public static function getVisibleForViewTypeValueExpression(bindTo:ValueExpression, viewTypes:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var values:Array = viewTypes.split(",");
      var viewTypeArray:Array = bindTo.extendBy(ContentPropertyNames.PROPERTIES + ".viewtype").getValue();
      if (viewTypeArray === undefined) {
        return undefined;
      }

      if(!viewTypeArray || viewTypeArray.length === 0) {
        return false;
      }

      var viewType:Content = viewTypeArray[0];
      for each(var vt:String in values) {
        if (vt === viewType.getName()) {
          return true;
        }
      }

      return false;
    });
  }

  public function openCollectionViewHandler(linkListTargetType:ContentType):void {
    var searchType = "CMProductTeaser";
    TimelineEditorBase.openSearch(searchType);
  }

}
}
