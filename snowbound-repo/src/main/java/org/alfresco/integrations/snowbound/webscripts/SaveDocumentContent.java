package org.alfresco.integrations.snowbound.webscripts;

import org.alfresco.integrations.snowbound.entity.Document;
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

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            Document document = new Gson().fromJson(request.getContent().getContent(), Document.class);
            documentNodeRef = new NodeRef(document.getId());
            logger.debug("NodeRef: " + documentNodeRef.toString());

            ContentService contentService = serviceRegistry.getContentService();
            ContentWriter contentWriter = contentService.getWriter(documentNodeRef, ContentModel.PROP_CONTENT, true);
            
            InputStream documentInputStream = null;
            if(document.getContent() != null){
            	documentInputStream = new ByteArrayInputStream(document.getContent());
            }
            contentWriter.putContent(documentInputStream);
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
