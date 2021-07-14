package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.tree.TreeRelation;

import java.util.Collection;
import java.util.List;

public class ExternalBreadcrumbTreeRelation implements TreeRelation<String> {

  private List<String> breadcrumb;

  public ExternalBreadcrumbTreeRelation(List<String> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  @Override
  public Collection<String> getChildrenOf(String parent) {
    List<String> breadcrumb = getBreadcrumb();
    if (!breadcrumb.contains(parent)) {
      throw new IllegalArgumentException(String.format("Could not find %s in %s", parent, String.join(" / ", breadcrumb)));
    }

    int indexOfParent = breadcrumb.indexOf(parent);
    return indexOfParent + 1 < breadcrumb.size() ? List.of(breadcrumb.get(indexOfParent + 1)) : List.of();
  }

  @Override
  public String getParentOf(String child) {
    List<String> breadcrumb = getBreadcrumb();
    if (!breadcrumb.contains(child)) {
      throw new IllegalArgumentException(String.format("Could not find %s in %s", child, String.join(" / ", breadcrumb)));
    }

    int indexOfChild = breadcrumb.indexOf(child);
    return indexOfChild > 0 ? breadcrumb.get(indexOfChild - 1) : null;
  }

  @Override
  public String getParentUnchecked(String child) {
    return getParentOf(child);
  }

  @Override
  public List<String> pathToRoot(String child) {
    List<String> breadcrumb = getBreadcrumb();
    if (!breadcrumb.contains(child)) {
      return List.of();
    }

    int indexOfChild = breadcrumb.indexOf(child);
    return breadcrumb.subList(0, indexOfChild + 1);
  }

  @Override
  public boolean isRoot(String item) {
    return getBreadcrumb().indexOf(item) == 0;
  }

  @Override
  public boolean isApplicable(String item) {
    return getBreadcrumb().contains(item);
  }

  public List<String> getBreadcrumb() {
    return breadcrumb;
  }

  public void setBreadcrumb(List<String> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }
}
