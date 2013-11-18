package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.Annotation;
import org.alfresco.integrations.snowbound.entity.AnnotationList;
import org.alfresco.integrations.snowbound.model.SnowboundContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kyle Adams
 * Date: 8/7/13
 * Time: 11:36 AM
 */
public class GetAnnotations extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(GetAnnotations.class);

    private ServiceRegistry serviceRegistry;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            NodeService nodeService = serviceRegistry.getNodeService();

            NodeRef documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + documentNodeRef.toString());

            List<Annotation> annotationList = new ArrayList<Annotation>();
            List<ChildAssociationRef> annotationAssocList = nodeService.getChildAssocs(
                    documentNodeRef,
                    SnowboundContentModel.ASSOC_ANNOTATION,
                    RegexQNamePattern.MATCH_ALL);
            logger.debug("Child List Size: " + annotationAssocList.size());

            AnnotationList annotations = new AnnotationList();
            Annotation annotation = null;
            for(ChildAssociationRef annotationAssocNodeRef : annotationAssocList){
                logger.debug("Annotation Assoc NodeRef: " + annotationAssocNodeRef.toString());
                annotation = new Annotation();

                NodeRef annotationNodeRef = annotationAssocNodeRef.getChildRef();
                logger.debug("Annotation NodeRef: " + annotationNodeRef);
                annotation.setId(annotationNodeRef.toString());

                String name = (String) nodeService.getProperty(annotationNodeRef, ContentModel.PROP_NAME);
                logger.debug("Annotation Name: " + name);
                annotation.setName(name);

                Object permissionLevel =  nodeService.getProperty(annotationNodeRef, SnowboundContentModel.PROP_PERMISSION_LEVEL);
                if(permissionLevel != null){
                    annotation.setPermissionLevel((Integer) permissionLevel);
                }

                Object redactionFlag = nodeService.getProperty(annotationNodeRef, SnowboundContentModel.PROP_REDACTION_FLAG);
                if(redactionFlag != null){
                    annotation.setRedactionFlag((Boolean) redactionFlag);
                }
                ContentService contentService = serviceRegistry.getContentService();
                ContentReader contentReader = contentService.getReader(annotationNodeRef, ContentModel.PROP_CONTENT);
                annotation.setContent(IOUtils.toByteArray(contentReader.getContentInputStream()));
                annotationList.add(annotation);
            }
            annotations.setAnnotations(annotationList);

            String jsonString = new Gson().toJson(annotations);
            logger.trace("Snowbound JSON Object: " + jsonString);
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
