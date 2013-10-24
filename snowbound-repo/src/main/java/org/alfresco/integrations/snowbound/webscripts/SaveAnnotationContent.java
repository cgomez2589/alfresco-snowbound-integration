package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.Annotation;
import org.alfresco.integrations.snowbound.model.SnowboundContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kyle Adams
 * Date: 9/3/13
 * Time: 10:35 PM
 */
public class SaveAnnotationContent  extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(SaveAnnotationContent.class);

    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private NodeRef documentNodeRef = null;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            nodeService = serviceRegistry.getNodeService();

            Annotation annotation = new Gson().fromJson(request.getContent().getContent(), Annotation.class);

            if(annotation != null){
                documentNodeRef = new NodeRef(annotation.getParentNodeRef());
                logger.debug("Document NodeRef: " + documentNodeRef.toString());

                if(!nodeService.hasAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE)){
                    addAnnotableAspect();
                }

                String annotationName = null;
                if(annotation.getName() != null){
                    annotationName = annotation.getName();
                    logger.debug("Retrieved annotation: " + annotationName);
                }
                boolean redactionFlag = annotation.getRedactionFlag();
                logger.debug("Retrieved annotation redactionFlag: " + redactionFlag);

                int permissionLevel = annotation.getPermissionLevel();
                logger.debug("Retrieved annotation permissionLevel: " + permissionLevel);

                InputStream annotationInputStream = null;
                if(annotation.getContent() != null){
                    annotationInputStream = new ByteArrayInputStream(annotation.getContent());
                }

                String annotationNodeRefString = annotation.getId();
                logger.debug("Annotation NodeRef Empty: " + annotationNodeRefString.isEmpty());

                if(annotationNodeRefString.isEmpty()){
                    createAnnotation(annotationName, redactionFlag, permissionLevel, annotationInputStream);
                }
                else{
                    NodeRef annotationNodeRef = new NodeRef(annotation.getId());
                    setAnnotationContent(annotationNodeRef, annotationInputStream);
                }
            }
        }
        catch(Exception e){
            logger.debug("Failed to save annotation content: ", e);
        }
    }

    private void createAnnotation(String annotationName,boolean redactionFlag, int permissionLevel, InputStream annotationInputStream){
        try{
            Map<QName, Serializable> annotationProperties = new HashMap<QName, Serializable>();
            annotationProperties.put(ContentModel.PROP_NAME, annotationName);
            annotationProperties.put(SnowboundContentModel.PROP_REDACTION_FLAG, redactionFlag);
            annotationProperties.put(SnowboundContentModel.PROP_PERMISSION_LEVEL, permissionLevel);

            NodeRef annotationNodeRef = nodeService.createNode(
                documentNodeRef,
                SnowboundContentModel.ASSOC_ANNOTATION,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, annotationName),
                SnowboundContentModel.TYPE_ANNOTATION,
                annotationProperties).getChildRef();
            logger.debug("Successfully create annotation: " + nodeService.getProperty(annotationNodeRef, ContentModel.PROP_NAME));

            Map<QName, Serializable> hiddenAspectProperties = new HashMap<QName, Serializable>();
            hiddenAspectProperties.put(ContentModel.PROP_VISIBILITY_MASK, 32768);
            nodeService.addAspect(annotationNodeRef, ContentModel.ASPECT_HIDDEN, hiddenAspectProperties);

            if(annotationNodeRef != null){
                setAnnotationContent(annotationNodeRef, annotationInputStream);
            }
        }
        catch (Exception e){
            logger.error("Failed to create annotation: ", e);
        }

    }

    private void setAnnotationContent(NodeRef annotationNodeRef, InputStream annotationInputStream){
        try{
            ContentService contentService = serviceRegistry.getContentService();
            ContentWriter contentWriter = contentService.getWriter(annotationNodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter.setMimetype("text/xml");
            contentWriter.putContent(annotationInputStream);
            logger.debug("Successfully wrote annotation content with url: " + contentWriter.getContentUrl());
        }
        catch (Exception e){
            logger.error("Failed to set annotation content.", e);
        }
    }

    private void addAnnotableAspect(){
        nodeService.addAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE, null);
    }
}
