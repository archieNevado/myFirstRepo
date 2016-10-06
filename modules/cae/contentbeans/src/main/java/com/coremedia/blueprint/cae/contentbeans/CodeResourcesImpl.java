package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CMCSS;
import com.coremedia.blueprint.common.contentbeans.CMJavaScript;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTheme;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.coremedia.blueprint.common.contentbeans.CMNavigation.CSS;
import static com.coremedia.blueprint.common.contentbeans.CMNavigation.JAVA_SCRIPT;

/**
 * Assembles the code resources (CSS and JS) of a channel.
 * <p>
 * Merges the resources of the channel's theme with the resources of the
 * channel's css and javaScript link lists.
 * <p>
 * Does *NOT* inherit code resources of the parent channel.
 */
class CodeResourcesImpl implements CodeResources {
  private static final String DIGEST_ALGORITHM = "MD5";

  private final boolean developerMode;
  private final CMNavigation navigation;
  private final String codePropertyName;

  private final String contentHash;

  private List<CMAbstractCode> mergeableResources = new ArrayList<>();
  private List<CMAbstractCode> ieExcludes = new ArrayList<>();
  private List<CMAbstractCode> externalLinks = new ArrayList<>();

  /**
   * Visible only for {@link CodeResourcesCacheKey}
   * <p>
   * Use a {@link CodeResourcesCacheKey} instead.
   * <ul>
   *   <li>If the navigation carries code, this constructor is expensive.</li>
   *   <li>If the navigation carries no code, the cache key includes the
   *   fallback to the parent channel (strange, but for good reasons), while
   *   this instance would not represent the intended business logic.</li>
   * </ul>
   * So, in either case you are better off with the cache key.
   */
  CodeResourcesImpl(CMNavigation navigation, String codePropertyName, boolean developerMode) {
    checkCodePropertyName(codePropertyName);
    this.navigation = navigation;
    this.codePropertyName = codePropertyName;
    this.developerMode = developerMode;
    MessageDigest digest = createDigest();
    transitiveClosure(getCodeResourcsFromContext(), digest, new ArrayList<>());
    contentHash = String.format("%01x", new BigInteger(1, digest.digest()));
  }

  @Override
  public CMNavigation getContext() {
    return navigation;
  }

  @Override
  public String getETag() {
    return contentHash;
  }

  @Override
  public List<?> getLinkTargetList() {
    List<Object> result = new ArrayList<>();
    result.addAll(externalLinks);
    if (developerMode) {
      result.addAll(getMergeableResources());
    } else {
      result.add(this);
    }
    result.addAll(ieExcludes);
    return result;
  }

  @Override
  public List<CMAbstractCode> getMergeableResources() {
    return mergeableResources;
  }

  @Nonnull
  private List<CMCSS> getCssIncludingThemes(CMNavigation context) {
    List<CMCSS> result = new ArrayList<>();

    CMTheme theme = context.getTheme();
    if (theme != null) {
      result.addAll(theme.getCss());
    }

    List<? extends CMCSS> cssFromNavigation = context.getCss();
    if (cssFromNavigation != null) {
      result.addAll(cssFromNavigation);
    }

    return result;
  }

  @Nonnull
  private List<CMJavaScript> getJavaScriptIncludingThemes(CMNavigation context) {
    List<CMJavaScript> result = new ArrayList<>();

    CMTheme theme = context.getTheme();
    if (theme != null) {
      result.addAll(theme.getJavaScriptLibraries());
      result.addAll(theme.getJavaScripts());
    }

    List<? extends CMJavaScript> jsFromNavigation = context.getJavaScript();
    if (jsFromNavigation != null) {
      result.addAll(jsFromNavigation);
    }

    return result;
  }

  @Nonnull
  private List<? extends CMAbstractCode> getCodeResourcsFromContext() {
    return CSS.equals(codePropertyName)
            ? getCssIncludingThemes(getContext())
            : getJavaScriptIncludingThemes(getContext());
  }

  private static void checkCodePropertyName(String codePropertyName) {
    if (!CSS.equals(codePropertyName) && !JAVA_SCRIPT.equals(codePropertyName)) {
      throw new IllegalArgumentException("No such CMNavigation code property: " + codePropertyName);
    }
  }

  /**
   * Compute a filtered lists of {@link CMAbstractCode codes} for the given
   * list of codes and their {@link CMAbstractCode#getInclude() includes}.
   */
  private void transitiveClosure(@Nonnull List<? extends CMAbstractCode> codes, MessageDigest digest, List<CMAbstractCode> visited) {
    for (CMAbstractCode code : codes) {
      //only traverse code if not already traversed.
      if (!visited.contains(code)) {
        visited.add(code);
        // get all included contents as well.
        if (!code.getInclude().isEmpty()) {
          transitiveClosure(code.getInclude(), digest, visited);
        }
        processCode(code, digest);
      }
    }
  }

  private void processCode(CMAbstractCode code, MessageDigest digest) {
    boolean isIeExclude = !StringUtils.isEmpty(code.getIeExpression());
    boolean isExternalLink = !StringUtils.isEmpty(code.getDataUrl());

    // If an external links is also an IE exclude, treat it as an IE exclude as the conditional comments are required:
    if (isIeExclude) {
      ieExcludes.add(code);
    } else if (isExternalLink) {
      externalLinks.add(code);
    } else if (code.getCode() != null) {
      mergeableResources.add(code);
    }

    byte[] statusFlags = new byte[]{(byte) (isIeExclude ? 1 : 0), (byte) (isExternalLink ? 1 : 0)};
    digest.update(statusFlags);
    if (code.getCode() != null) {
      digest.update(Integer.toString(code.getCode().hashCode()).getBytes());
    }
  }

  /**
   * Compile a single hash code for the tree of codes linked in a navigation.
   */
  private static MessageDigest createDigest() {
    try {
      return MessageDigest.getInstance(DIGEST_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Unsupported digest algorithm", e);
    }
  }
}
