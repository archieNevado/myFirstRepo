package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@DefaultAnnotation(NonNull.class)
public class ExternalBreadcrumbTreeRelation implements TreeRelation<CommerceId> {

  private final List<CommerceId> breadcrumb;

  public ExternalBreadcrumbTreeRelation(List<CommerceId> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  @Override
  public Collection<CommerceId> getChildrenOf(CommerceId parent) {
    List<CommerceId> breadcrumb = getBreadcrumb();
    int indexOfParent = breadcrumb.indexOf(parent);
    if (indexOfParent < 0) {
      var breadcrumbString = getBreadcrumbString(breadcrumb);
      throw new IllegalArgumentException(String.format("Could not find %s in %s", parent, breadcrumbString));
    }
    return indexOfParent + 1 < breadcrumb.size() ? List.of(breadcrumb.get(indexOfParent + 1)) : List.of();
  }

  @Override
  public CommerceId getParentOf(CommerceId child) {
    List<CommerceId> breadcrumb = getBreadcrumb();
    int indexOfChild = breadcrumb.indexOf(child);
    if (indexOfChild < 0) {
      var breadcrumbString = getBreadcrumbString(breadcrumb);
      throw new IllegalArgumentException(String.format("Could not find %s in %s", child, breadcrumbString));
    }
    return indexOfChild > 0 ? breadcrumb.get(indexOfChild - 1) : null;
  }

  private static String getBreadcrumbString(List<CommerceId> breadcrumb) {
    return breadcrumb.stream().map(CommerceIdFormatterHelper::format).collect(Collectors.joining(" / "));
  }

  @Override
  public CommerceId getParentUnchecked(CommerceId child) {
    return getParentOf(child);
  }

  @Override
  public List<CommerceId> pathToRoot(CommerceId child) {
    List<CommerceId> breadcrumb = getBreadcrumb();
    int indexOfChild = breadcrumb.indexOf(child);
    if (indexOfChild < 0) {
      return List.of();
    }
    return breadcrumb.subList(0, indexOfChild + 1);
  }

  @Override
  public boolean isRoot(CommerceId item) {
    return getBreadcrumb().indexOf(item) == 0;
  }

  @Override
  public boolean isApplicable(CommerceId item) {
    return getBreadcrumb().contains(item);
  }

  public List<CommerceId> getBreadcrumb() {
    return breadcrumb;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExternalBreadcrumbTreeRelation that = (ExternalBreadcrumbTreeRelation) o;
    return breadcrumb.equals(that.breadcrumb);
  }

  @Override
  public int hashCode() {
    return Objects.hash(breadcrumb);
  }

}
