package org.alfresco.integrations.snowbound.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Author: Kyle Adams
 * Date: 9/1/13
 * Time: 10:59 PM
 */
@XmlRootElement
public class AnnotationList {
    public List<Annotation> annotations;

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}
