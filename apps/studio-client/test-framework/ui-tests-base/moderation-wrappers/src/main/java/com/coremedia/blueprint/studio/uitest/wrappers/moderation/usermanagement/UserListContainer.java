package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserListContainer extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-user-search-field", global = false)
  private SearchField searchTermTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "magnifier", global = false)
  private Button submitSearchButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "back", global = false)
  private Button backButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "forward", global = false)
  private Button forwardButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "searchResult", global = false)
  private UserSearchResultView userSearchResultView;

  public SearchField getSearchField() {
    return searchTermTextField;
  }

  public Button getSubmitSearchButton() {
    return submitSearchButton;
  }

  public UserSearchResultView getUserSearchResultView() {
    return userSearchResultView;
  }

  public Button getBackButton() {
    return backButton;
  }

  public Button getForwardButton() {
    return forwardButton;
  }
}
