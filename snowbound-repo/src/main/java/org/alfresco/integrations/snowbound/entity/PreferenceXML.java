package org.alfresco.integrations.snowbound.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Author: Kyle Adams
 * Date: 8/31/13
 * Time: 11:36 PM
 */
@XmlRootElement
public class PreferenceXML {
    public String id;
    public String name;
    public byte[] content;
    public String parentNodeRef;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getParentNodeRef() {
        return parentNodeRef;
    }

    public void setParentNodeRef(String parentNodeRef) {
        this.parentNodeRef = parentNodeRef;
    }
}
