package com.coremedia.livecontext.ecommerce.sfcc.cae.preview;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@DefaultAnnotation(NonNull.class)
public class SiteDateFormatter {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

  private SiteDateFormatter() {
  }

  public static String format(ZonedDateTime dateTime) {
    return dateTime.format(FORMATTER);
  }

}
