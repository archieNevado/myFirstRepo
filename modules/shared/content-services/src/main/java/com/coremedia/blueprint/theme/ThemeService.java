package com.coremedia.blueprint.theme;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Lookup the theme for contents.
 */
public class ThemeService {
  private static final String CM_NAVIGATION = "CMNavigation";
  private static final String THEME = "theme";

  private final TreeRelation<Content> treeRelation;


  // --- Construct and configure ------------------------------------

  public ThemeService(@Nonnull TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }


  // --- Features ---------------------------------------------------

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
    // but for other types the result would depend on the content being in
    // the tree relation, which would not make sense.
    // Better make the contract obvious, and accept only CMNavigation.
    checkIsNavigation(content);
    return directTheme(Lists.reverse(treeRelation.pathToRoot(content)));
  }

  /**
   * Returns the first theme found in the given fallback contents list.
   * <p>
   * In the plain CMNavigation content world the fallback logic is backed by
   * the TreeRelation.  You do not need to care about it, but simply use
   * {@link #theme(Content)}.
   * <p>
   * However, if you use Navigation implementations that are not backed by
   * content, you cannot simply delegate down to this content service.  This
   * method enables you to control the fallback logic by providing a
   * precomputed fallback list according to your model.
   */
  @Nullable
  public Content directTheme(List<Content> contents) {
    for (Content content : contents) {
      if (content!=null && content.getType().isSubtypeOf(CM_NAVIGATION)) {
        Content myTheme = content.getLink(THEME);
        if (myTheme!=null) {
          return myTheme;
        }
      }
    }
    return null;
  }


  // --- internal ---------------------------------------------------

  private static void checkIsNavigation(Content content) {
    if (!content.getType().isSubtypeOf(CM_NAVIGATION)) {
      throw new IllegalArgumentException(content + " is no CMNavigation");
    }
  }
}
