package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.descriptors.StringPropertyDescriptor;
import com.coremedia.cap.common.descriptors.StructPropertyDescriptor;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.rest.convert.AnnotatedLinkListProperties.STRUCT_LINKS_PROPERTY_NAME;

/**
 * <p>AnchorWriteInterceptor normalizes the value of an <i>anchor</i> property by making sure that - if not empty - the
 * anchor starts with a letter, replacing all spaces with dashes and removing all characters that are not allowed.
 *
 * Allowed characters are alphanumeric characters, dashes, underlines and dots.
 */
public class AnchorWriteInterceptor extends ContentWriteInterceptorBase {

  private String annotatedLinkListPropertyName;
  private String anchorAnnotationName;

  public String getAnnotatedLinkListPropertyName() {
    return annotatedLinkListPropertyName;
  }

  public void setAnnotatedLinkListPropertyName(String propertyName) {
    this.annotatedLinkListPropertyName = propertyName;
  }

  public String getAnchorAnnotationName() {
    return anchorAnnotationName;
  }

  public void setAnchorAnnotationName(String anchorAnnotationName) {
    this.anchorAnnotationName = anchorAnnotationName;
  }

  @Override
  public void intercept(ContentWriteRequest request) {

    final Map<String,Object> properties = request.getProperties();
    final Object oAnnotatedLinkList = properties.get(annotatedLinkListPropertyName);

    if (oAnnotatedLinkList instanceof Struct) {
      Struct annotatedLinkList = (Struct) oAnnotatedLinkList;
      CapPropertyDescriptor linksDescriptor = annotatedLinkList.getType().getDescriptor(STRUCT_LINKS_PROPERTY_NAME);
      if (linksDescriptor instanceof StructPropertyDescriptor && linksDescriptor.isCollection()) {
        StructBuilder annotatedLinkListBuilder = annotatedLinkList.builder();
        List<Struct> links = annotatedLinkList.getStructs(STRUCT_LINKS_PROPERTY_NAME);
        annotatedLinkListBuilder.set(STRUCT_LINKS_PROPERTY_NAME, links.stream().map(link -> {
          CapPropertyDescriptor anchorDescriptor = link.getType().getDescriptor(anchorAnnotationName);
          if (anchorDescriptor instanceof StringPropertyDescriptor && anchorDescriptor.isAtomic()) {
            String hash = link.getString(anchorAnnotationName);
            if (hash != null) {
              StructBuilder linkBuilder = link.builder();
              hash = hash.replaceAll("^[^a-zA-Z]", "");
              hash = hash.replaceAll("\\s", "-");
              hash = hash.replaceAll("[^a-zA-Z0-9-_.]", "");
              linkBuilder.set(anchorAnnotationName, hash);
              return linkBuilder.build();
            }
          }
          return link;
        }).collect(Collectors.toList()));
        properties.put(annotatedLinkListPropertyName, annotatedLinkListBuilder.build());
      }
    }
  }

}
