package com.coremedia.blueprint.caefeeder;

import com.coremedia.cap.feeder.bean.PropertyConverter;
import com.coremedia.objectserver.beans.ContentBean;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 *   This converter takes a list of {@link ContentBean ContentBeans} and returns a collection of unique paths that follow
 *   a hierarchically-structured scheme mentioned on the
 *   Solr Wiki (see <a href="https://wiki.apache.org/solr/HierarchicalFaceting">'facet.prefix' based drill down</a>).
 * </p>
 * <p>
 *   All beans in the list will be processed as follows:
 *   <ol>
 *     <li>The {@link TreePathKeyFactory} creates the path <em>'/A/B/C'</em> from the bean C in the list.</li>
 *     <li>
 *       The converter creates a depth-prefixed path for each level in the path:
 *       <ul>
 *         <li><em>0/A</em></li>
 *         <li><em>1/A/B</em></li>
 *         <li><em>2/A/B/C</em></li>
 *       </ul>
 *     </li>
 *     <li>These paths will be added to the set of resulting paths.</li>
 *   </ol>
 * </p>
 * <p>
 *   Note:<br>
 *   The converter assumes that the levels of the hierarchy are separated by slashes.
 * </p>
 *
 *
 * @see <a href="https://wiki.apache.org/solr/HierarchicalFaceting">'facet.prefix' based drill down</a>
 */
public class PrefixedPathHierarchyConverter implements PropertyConverter {

  private static final char PATH_SEPARATOR_CHAR = '/';

  private TreePathKeyFactory pathKeyFactory;

  /**
   * <p>
   * Sets the path key factory that creates a path from a {@link ContentBean}. The resulting path should start with a
   * slash and the segments have to be separated by slashes too.
   * </p>
   *
   * @param pathKeyFactory the path key factory
   */
  @Required
  public void setPathKeyFactory(TreePathKeyFactory pathKeyFactory) {
    this.pathKeyFactory = pathKeyFactory;
  }

  @Override
  public Object convertValue(Object value) {
    if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<ContentBean> contentBeans = (List<ContentBean>) value;
      Set<String> tags = new TreeSet<>();
      for (ContentBean contentBean : contentBeans) {
        String path = pathKeyFactory.getPath(contentBean.getContent());
        tags.addAll(createDepthPrefixedPathSegments(path));
      }
      return tags;

    }
    return Collections.emptyList();
  }

  @Nonnull
  private static List<String> createDepthPrefixedPathSegments(@Nullable String path) {
    if (path == null) {
      return Collections.emptyList();
    }

    // ensure the path always starts with a leading slash
    String processPath = path.charAt(0) == PATH_SEPARATOR_CHAR ? path : PATH_SEPARATOR_CHAR + path;

    List<String> resultingPrefixedPaths = new ArrayList<>();

    int pathSegmentIx = -1;
    char[] pathAsArray = processPath.toCharArray();
    StringBuilder pathBuilder = new StringBuilder();
    for (char pathLetter : pathAsArray) {
      if (pathLetter == PATH_SEPARATOR_CHAR) {
        if (pathSegmentIx > -1) {
          resultingPrefixedPaths.add( createdPrefixedPath(pathBuilder.toString(), pathSegmentIx) );
        }
        pathSegmentIx++;
      }
      pathBuilder.append(pathLetter);
    }
    resultingPrefixedPaths.add( createdPrefixedPath(pathBuilder.toString(), Math.max(pathSegmentIx, 0)) );

    return resultingPrefixedPaths;
  }

  @Nonnull
  private static String createdPrefixedPath(@Nonnull String path, int depth) {
    return String.valueOf(depth) + path;
  }

  @Override
  public Class<?> convertType(Class<?> type) {
    return List.class;
  }

}
