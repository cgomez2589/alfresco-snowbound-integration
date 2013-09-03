package org.alfresco.integrations.snowbound.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Author: Kyle Adams
 * Date: 8/31/13
 * Time: 11:36 PM
 */
@XmlRootElement
public class Annotation {
    public String id;
    public String name;
    public int permissionLevel;
    public boolean redactionFlag;

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

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public boolean getRedactionFlag() {
        return redactionFlag;
    }

    public void setRedactionFlag(boolean redactionFlag) {
        this.redactionFlag = redactionFlag;
    }
}
