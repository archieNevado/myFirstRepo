package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.struct.Struct;
import com.coremedia.common.personaldata.PersonalData;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents the document type {@link #NAME CMPerson}.
 *
 * @cm.template.api
 */
public interface CMPerson extends CMTeasable {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMPerson'.
   */
  String NAME = "CMPerson";

  // Constants for property names
  String FIRST_NAME = "firstName";
  String LAST_NAME = "lastName";
  String DISPLAY_NAME = "displayName";
  String EMAIL = "eMail";
  String ORGANIZATION = "organization";
  String JOB_TITLE = "jobTitle";
  String MISC = "misc";


  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMPerson} object
   */
  @Override
  CMPerson getMaster();

  @Override
  Map<Locale, ? extends CMPerson> getVariantsByLocale();

  @Override
  Collection<? extends CMPerson> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMPerson>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMPerson>> getAspects();

  /**
   * <p>
   * Returns the value of the document property {@link #FIRST_NAME}.
   * </p>
   *
   * @return the value of the document property {@link #FIRST_NAME}
   * @cm.template.api
   */
  @PersonalData String getFirstName();

  /**
   * <p>
   * Returns the value of the document property {@link #LAST_NAME}.
   * </p>
   *
   * @return the value of the document property {@link #LAST_NAME}
   * @cm.template.api
   */
  @PersonalData String getLastName();

  /**
   * <p>
   * Returns the value of the document property {@link #DISPLAY_NAME}.
   * </p>
   *
   * @return the value of the document property {@link #DISPLAY_NAME}
   * @cm.template.api
   */
  @PersonalData String getDisplayName();

  /**
   * <p>
   * Returns the value of the document property {@link #EMAIL}.
   * </p>
   *
   * @return the value of the document property {@link #EMAIL}
   * @cm.template.api
   */
   @PersonalData String getEMail();

  /**
   * <p>
   * Returns the value of the document property {@link #ORGANIZATION}.
   * </p>
   *
   * @return the value of the document property {@link #ORGANIZATION}
   * @cm.template.api
   */
  @PersonalData String getOrganization();

  /**
   * <p>
   * Returns the value of the document property {@link #JOB_TITLE}.
   * </p>
   *
   * @return the value of the document property {@link #JOB_TITLE}
   * @cm.template.api
   */
  @PersonalData String getJobTitle();

  /**
   * <p>
   * Returns the value of the document property {@link #MISC}.
   * </p>
   *
   * @return the value of the document property {@link #MISC}
   * @cm.template.api
   */
  @PersonalData Struct getMisc();
}
