package org.alfresco.integrations.snowbound.model;

import org.alfresco.service.namespace.QName;

/**
 * Author: Kyle Adams
 * Date: 8/30/13
 * Time: 12:26 PM
 */
public interface SnowboundContentModel {
    public static final String SNOWBOUND_URI = "http://www.alfresco.org/model/snowbound/1.0";
    public static final String SNOWBOUND_PREFIX = "vv";
    public static final QName SNOWBOUND_MODEL = QName.createQName(SNOWBOUND_URI, "snowboundVirtualViewerModel");

    public static final QName TYPE_ANNOTATION = QName.createQName(SNOWBOUND_URI, "annotation");
    public static final QName PROP_PERMISSION_LEVEL = QName.createQName(SNOWBOUND_URI, "permissionLevel");
    public static final QName PROP_REDACTION_FLAG = QName.createQName(SNOWBOUND_URI, "redactionFlag");

    public static final QName ASPECT_ANNOTABLE = QName.createQName(SNOWBOUND_URI, "annotable");
    public static final QName ASSOC_ANNOTATION = QName.createQName(SNOWBOUND_URI, "annotationAssoc");

    public static final QName ASPECT_BOOKMARKABLE = QName.createQName(SNOWBOUND_URI, "bookmarkable");
    public static final QName ASSOC_BOOKMARK= QName.createQName(SNOWBOUND_URI, "bookmarkAssoc");

}
