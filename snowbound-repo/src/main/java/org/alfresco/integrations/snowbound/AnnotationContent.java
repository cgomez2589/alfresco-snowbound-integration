package org.alfresco.integrations.snowbound;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kyle Adams
 * Date: 8/30/13
 * Time: 12:10 PM
 */
public class AnnotationContent extends DeclarativeWebScript {
    private static final Log logger = LogFactory.getLog(AnnotationContent.class);


    public Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache){

        try{
            WrappingWebScriptRequest wrappingWebScriptRequest = (WrappingWebScriptRequest) request;
            WebScriptRequest webScriptRequest = wrappingWebScriptRequest.getNext();
            WebScriptServletRequest servletRequest = (WebScriptServletRequest) webScriptRequest;


            //String test = servletRequest.getParameter("targetTenantUser");
            logger.debug("Annotation Mimetype: " + servletRequest.getContent().getMimetype());

            StringWriter writer = new StringWriter();
            IOUtils.copy(servletRequest.getContent().getInputStream(), writer, servletRequest.getContent().getEncoding());
            String annotationString = writer.toString();

            logger.debug("Annotation String: " + annotationString);

//            this.getParameters(servletRequest);
//            this.moveNode(this.nodeRef, this.primaryParentRef, this.sourceFolderNodeRef, this.targetFolderNodeRef);

        }
        catch(Exception e){
            logger.debug("Failed to execute test web script: ", e);
        }

        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("originalUser", originalUser);
//        model.put("targetTenantAdmin", this.targetTenantAdmin);
//        model.put("nodeRef", this.nodeRef.toString());
//        model.put("sourceFolderNodeRef", this.sourceFolderNodeRef.toString());
//        model.put("targetFolderNodeRef", this.targetFolderNodeRef.toString());

        return model;
    }
}

