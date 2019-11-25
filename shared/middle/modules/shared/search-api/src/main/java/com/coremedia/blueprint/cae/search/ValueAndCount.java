package com.coremedia.blueprint.cae.search;

/**
 * A string value with a count.
 *
 * @cm.template.api
 */
public class ValueAndCount {

  private String value;
  private long count;

  public ValueAndCount(String name, long count) {
    this.value = name;
    this.count = count;
  }

  /**
   * Returns the value.
   *
   * @return value
   * @cm.template.api
   */
  public String getValue() {
    return value;
  }

  /**
   * @deprecated since 1810, use {@link #getValue} instead
   */
  @Deprecated
  public String getName() {
    return getValue();
  }

  /**
   * @deprecated since 1810, do not modify existing objects but create a new instances instead
   */
  @Deprecated
  public void setName(String name) {
    this.value = name;
  }

  /**
   * Returns the count.
   *
   * @return count
   * @cm.template.api
   */
  public long getCount() {
    return count;
  }

  /**
   * @deprecated since 1810, do not modify existing objects but create a new instances instead
   */
  @Deprecated
  public void setCount(long count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "ValueAndCount[value='" + value + "', count=" + count + ']';
  }
}
