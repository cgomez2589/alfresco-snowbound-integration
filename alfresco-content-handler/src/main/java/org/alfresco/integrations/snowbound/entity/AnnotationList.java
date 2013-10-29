package org.alfresco.integrations.snowbound.entity;

import java.util.List;

/**
 * Author: Kyle Adams
 * Date: 10/21/13
 * Time: 3:06 PM
 */
public class AnnotationList {
    public List<Annotation> annotations;

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}
