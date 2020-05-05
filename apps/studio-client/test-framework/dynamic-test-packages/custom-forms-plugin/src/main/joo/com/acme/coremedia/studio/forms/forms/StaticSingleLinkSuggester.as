package com.acme.coremedia.studio.forms.forms {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.ILinkListWrapper;
import com.coremedia.cms.editor.sdk.util.ILinkSuggester;

public class StaticSingleLinkSuggester implements ILinkSuggester{

  [Bindable]
  public var content:Content;

  public function StaticSingleLinkSuggester(config:StaticSingleLinkSuggester = null) {
    content = config.content;
  }

  public function suggestLinks(linkListWrapper:ILinkListWrapper, searchTerm:String, callback:Function):void {
    callback([content]);
  }
}
}
