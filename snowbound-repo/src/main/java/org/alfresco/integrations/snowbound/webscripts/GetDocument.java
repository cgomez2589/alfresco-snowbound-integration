package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.Document;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * Author: Kyle Adams
 * Date: 8/7/13
 * Time: 11:36 AM
 */
public class GetDocument extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(GetDocument.class);

    private ServiceRegistry serviceRegistry;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            NodeService nodeService = serviceRegistry.getNodeService();
            NodeRef documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + documentNodeRef.toString());

            Document document = new Document();
            document.setId(documentNodeRef.toString());

            String name = (String) nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME);
            document.setName(name);

            ContentService contentService = serviceRegistry.getContentService();
            ContentReader contentReader = contentService.getReader(documentNodeRef, ContentModel.PROP_CONTENT);
            document.setContent(IOUtils.toByteArray(contentReader.getContentInputStream()));

            String jsonString = new Gson().toJson(document);
            logger.trace("Document JSON Object: " + jsonString);
            response.getWriter().write(jsonString);
        }
        catch(Exception e){
            logger.debug("Failed to serialize JSON object: ", e);
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
