package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.rest.cap.validation.AbstractContentTypeValidator;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Checks if the segment uses special characters or internally reserved keywords and
 * if the segment starts or ends with '-'
 */
public class SegmentFormatValidator extends AbstractContentTypeValidator {

  private static final String CODE_ISSUE_SEGMENT_RESERVED_CHARS_FOUND = "SegmentReservedCharsFound";
  private static final String CODE_ISSUE_SEGMENT_RESERVED_PREFIX = "SegmentReservedPrefix";
  private static final String CODE_ISSUE_SEGMENT_RESERVED_SUFFIX = "SegmentReservedSuffix";

  private static final String CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_CHARS_FOUND = "FallbackSegmentReservedCharsFound";
  private static final String CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_PREFIX = "FallbackSegmentReservedPrefix";
  private static final String CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_SUFFIX = "FallbackSegmentReservedSuffix";

  private static final String SEPARATOR = "-";
  private static final String RESERVED_SEPARATOR = "--";

  private final String propertyName;
  private String fallbackPropertyName;

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final SitesService sitesService;

  //allow only numbers, letters and '-'
  private static final Pattern PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\-]");

  public SegmentFormatValidator(@NonNull ContentType type,
                                boolean isValidatingSubtypes,
                                CommerceConnectionSupplier commerceConnectionSupplier,
                                SitesService sitesService,
                                String propertyName) {
    super(type, isValidatingSubtypes);
    this.propertyName = propertyName;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.sitesService = sitesService;
  }

  @Override
  public void validate(Content content, Issues issues) {
    if (content == null || !content.isInProduction()) {
      return;
    }

    //check if the content belongs to a livecontext site
    Optional<Site> site = sitesService.getContentSiteAspect(content).findSite();
    Optional<CommerceConnection> commerceConnection = site.flatMap(commerceConnectionSupplier::findConnection);
    if (commerceConnection.isEmpty()) {
      return;
    }

    validateProperty(content, issues);
  }

  @SuppressWarnings("TypeMayBeWeakened")
  private void validateProperty(Content content, Issues issues) {
    String propertyValue = content.getString(propertyName);

    if (!isBlank(propertyValue)) {
      validateProperty(propertyValue, issues,
              CODE_ISSUE_SEGMENT_RESERVED_CHARS_FOUND, CODE_ISSUE_SEGMENT_RESERVED_PREFIX, CODE_ISSUE_SEGMENT_RESERVED_SUFFIX);
    } else if (fallbackPropertyName != null && content.getType().getDescriptor(fallbackPropertyName) != null ){
      String fallbackPropertyValue = content.getString(fallbackPropertyName);
      if (!isBlank(fallbackPropertyValue)) {
        validateProperty(fallbackPropertyValue, issues,
                CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_CHARS_FOUND, CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_PREFIX, CODE_ISSUE_FALLBACK_SEGMENT_RESERVED_SUFFIX);
      }
    }
  }

  @SuppressWarnings("TypeMayBeWeakened")
  private void validateProperty(String propertyValue, Issues issues,
                                String codeReservedChars, String codePrefix, String codeSuffix) {
    String replacedValue = PATTERN.matcher(propertyValue).replaceAll(SEPARATOR);
    if (replacedValue.contains(RESERVED_SEPARATOR)) {
      //... but two sequential special characters could then be the reserved separator  "--"
      issues.addIssue(getCategories(), Severity.ERROR, propertyName, getContentType() + '_' + codeReservedChars, RESERVED_SEPARATOR, replacedValue);
    }
    if (replacedValue.startsWith(SEPARATOR)) {
      issues.addIssue(getCategories(), Severity.ERROR, propertyName, getContentType() + '_' + codePrefix, SEPARATOR, replacedValue);
    }
    if (replacedValue.endsWith(SEPARATOR)) {
      issues.addIssue(getCategories(), Severity.ERROR, propertyName, getContentType() + '_' + codeSuffix, SEPARATOR, replacedValue);
    }

  }

  public void setFallbackPropertyName(String fallbackPropertyName) {
    this.fallbackPropertyName = fallbackPropertyName;
  }
}
