package com.coremedia.blueprint.workflow.boot;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Duration;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Configuration Properties for Blueprint Workflow Server.
 */
@ConfigurationProperties(prefix = "workflow.blueprint")
@Validated
public class BlueprintWorkflowServerConfigurationProperties {
  /**
   * Configures scheduling for `CleanInTranslation` task.
   */
  @NestedConfigurationProperty
  @Valid
  private final CleanInTranslationProperties cleanInTranslation = new CleanInTranslationProperties();

  @NonNull
  public CleanInTranslationProperties getCleanInTranslation() {
    return cleanInTranslation;
  }

  /**
   * Configuration of {@code CleanInTranslation} task.
   */
  @Validated
  public static class CleanInTranslationProperties {
    /**
     * The initial delay for the `CleanInTranslation` scheduled task.
     */
    @DurationMin
    @DurationUnit(MILLIS)
    @NonNull
    private Duration initialDelay = ofSeconds(10L);
    /**
     * The fixed delay for the `CleanInTranslation` scheduled task. If you
     * decrease the value, consider increasing `confidenceThreshold` when you
     * choose a low delay and thus frequent scheduling.
     */
    @DurationMin(inclusive = false, message = "Duration must be greater than 0.")
    @DurationUnit(MILLIS)
    @NonNull
    private Duration fixedDelay = ofMinutes(15L);
    /**
     * Threshold for confidence we need to reach, before we are going to remove
     * a merge-version of a derived content. 0 (zero) or below signals,
     * that merge-versions shall be removed immediately when considered unused.
     * <p>
     * The confidence is increased on each scheduled run of `CleanInTranslation`.
     * Thus, a threshold of 1 will clear a merge-version when it got detected
     * twice as being unused. A threshold is recommended not to be chosen
     * higher than 10.
     * <p>
     * A threshold greater than 0 (zero) is strongly recommended, as
     * asynchronous updates may cause false-positive rating as unused.
     * <p>
     * The higher the threshold is, the longer it will take until a possibly
     * broken state caused by some aborted or escalated
     * Translation/Synchronization workflow is cleaned up. Thus, the longer
     * it will take that a false-positive 'in-translation' state is cleaned
     * up.
     * <p>
     * In contrast to this, if the threshold is too low, you may not only get
     * a false-negative 'in-translation' state, you may also experience that
     * tools such as 'cm cleanversions' erroneously clean up master content
     * versions, which are about to be translated. Translation and
     * Synchronization workflows may escalate, if these are removed.
     * <p>
     * This setting is especially related to `fixedDelay`: As the
     * confidence is increased on every scheduled run, you will typically want
     * to increase the confidence threshold when decreasing the delay.
     */
    private int confidenceThreshold = 1;

    @NonNull
    public Duration getInitialDelay() {
      return initialDelay;
    }

    public void setInitialDelay(@NonNull Duration initialDelay) {
      this.initialDelay = initialDelay;
    }

    @NonNull
    public Duration getFixedDelay() {
      return fixedDelay;
    }

    public void setFixedDelay(@NonNull Duration fixedDelay) {
      this.fixedDelay = fixedDelay;
    }

    public int getConfidenceThreshold() {
      return confidenceThreshold;
    }

    public void setConfidenceThreshold(int confidenceThreshold) {
      this.confidenceThreshold = confidenceThreshold;
    }
  }
}
