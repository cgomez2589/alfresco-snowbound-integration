package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.Annotation;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
public class DeleteAnnotation extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(DeleteAnnotation.class);

    private ServiceRegistry serviceRegistry;



    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            Annotation annotation = new Gson().fromJson(request.getContent().getContent(), Annotation.class);

            if(annotation != null){
                NodeRef annotationNodeRef = new NodeRef(annotation.getId());
                NodeService nodeService = serviceRegistry.getNodeService();
                nodeService.deleteNode(annotationNodeRef);

                logger.debug("Successfully deleted annotation with noderef: " + annotationNodeRef.toString());
            }
        }
        catch(Exception e){
            logger.debug("Failed to save annotation content: ", e);
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
