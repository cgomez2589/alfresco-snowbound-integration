package org.alfresco.integrations.snowbound.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * Author: Kyle Adams
 * Date: 9/3/13
 * Time: 10:35 PM
 */
public class SaveDocumentContent  extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(SaveDocumentContent.class);

    private ServiceRegistry serviceRegistry;
    private NodeRef documentNodeRef = null;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + documentNodeRef.toString());

            ContentService contentService = serviceRegistry.getContentService();
            ContentWriter contentWriter = contentService.getWriter(documentNodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter.putContent(request.getContent().getInputStream());
            logger.debug("Successfully wrote content for: " + documentNodeRef + " with content url: " + contentWriter.getContentUrl());
        }
        catch(Exception e){
            logger.debug("Failed to save document content: ", e);
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
