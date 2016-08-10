package com.coremedia.blueprint.theme;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ThemeService {
  private static final String CM_NAVIGATION = "CMNavigation";
  private static final String THEME = "theme";

  private final TreeRelation<Content> treeRelation;

  public ThemeService(@Nonnull TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }

  /**
   * Returns the navigation's theme.
   * <p>
   * If the navigation has no theme, its parent's theme is returned.
   * Returns null if there is no theme up the navigation hierarchy.
   *
   * @param content must be of type CMNavigation
   */
  @Nullable
  public Content theme(@Nonnull Content content) {
    // Technically this would work even if content was no CMNavigation,
    // but it might be confusing if the result depends on content being in
    // the tree relation.  Better make the contract easy, and accept only
    // CMNavigation.
    checkIsNavigation(content);

    List<Content> path = treeRelation.pathToRoot(content);
    for (int i=path.size()-1; i>=0; --i) {
      if (path.get(i).getType().isSubtypeOf(CM_NAVIGATION)) {
        Content myTheme = path.get(i).getLink(THEME);
        if (myTheme!=null) {
          return myTheme;
        }
      }
    }
    return null;
  }

  private static void checkIsNavigation(Content content) {
    if (!content.getType().isSubtypeOf(CM_NAVIGATION)) {
      throw new IllegalArgumentException(content + " is no CMNavigation");
    }
  }
}
