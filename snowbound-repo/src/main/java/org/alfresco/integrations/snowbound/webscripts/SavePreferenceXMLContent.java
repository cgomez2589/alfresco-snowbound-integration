package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.PreferenceXML;
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
public class SavePreferenceXMLContent extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(SavePreferenceXMLContent.class);

    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private NodeRef documentNodeRef = null;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            nodeService = serviceRegistry.getNodeService();

            PreferenceXML preferenceXML = new Gson().fromJson(request.getContent().getContent(), PreferenceXML.class);

            if(!nodeService.hasAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE)){
                addAnnotableAspect();
            }

            if(preferenceXML != null){
                documentNodeRef = new NodeRef(preferenceXML.getParentNodeRef());
                logger.debug("Document NodeRef: " + documentNodeRef.toString());

                String preferenceXMLName = null;
                if(preferenceXML.getName() != null){
                    preferenceXMLName = preferenceXML.getName();
                    logger.debug("Retrieved annotation: " + preferenceXMLName);
                }

                InputStream preferenceXMLInputStream = null;
                if(preferenceXML.getContent() != null){
                    preferenceXMLInputStream = new ByteArrayInputStream(preferenceXML.getContent());
                }

                String bookmarkNodeRefString = preferenceXML.getId();
                logger.debug("Bookmark NodeRef Empty: " + bookmarkNodeRefString.isEmpty());

                if(bookmarkNodeRefString.isEmpty()){
                    createPreferenceXML(preferenceXMLName, preferenceXMLInputStream);
                }
                else{
                    NodeRef bookmarkNodeRef = new NodeRef(preferenceXML.getId());
                    setPreferenceXMLContent(bookmarkNodeRef, preferenceXMLInputStream);
                }
            }
        }
        catch(Exception e){
            logger.debug("Failed to save annotation content: ", e);
        }
    }

    private void createPreferenceXML(String preferenceXMLName, InputStream preferenceXMLInputStream){
        try{
            Map<QName, Serializable> preferenceXMLProperties = new HashMap<QName, Serializable>();
            preferenceXMLProperties.put(ContentModel.PROP_NAME, preferenceXMLName);

            NodeRef preferenceXMLNodeRef = nodeService.createNode(
                documentNodeRef,
                SnowboundContentModel.ASSOC_PREFERENCE,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, preferenceXMLName),
                SnowboundContentModel.TYPE_PREFERENCE,
                    preferenceXMLProperties).getChildRef();
            logger.debug("Successfully created preference XML: " + nodeService.getProperty(preferenceXMLNodeRef, ContentModel.PROP_NAME));

            Map<QName, Serializable> hiddenAspectProperties = new HashMap<QName, Serializable>();
            hiddenAspectProperties.put(ContentModel.PROP_VISIBILITY_MASK, 32768);
            nodeService.addAspect(preferenceXMLNodeRef, ContentModel.ASPECT_HIDDEN, hiddenAspectProperties);

            if(preferenceXMLNodeRef != null){
                setPreferenceXMLContent(preferenceXMLNodeRef, preferenceXMLInputStream);
            }
        }
        catch (Exception e){
            logger.error("Failed to create preference XML: ", e);
        }
    }

    private void setPreferenceXMLContent(NodeRef preferenceXMLNodeRef, InputStream preferenceXMLInputStream){
        try{
            ContentService contentService = serviceRegistry.getContentService();
            ContentWriter contentWriter = contentService.getWriter(preferenceXMLNodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter.setMimetype("text/xml");
            contentWriter.putContent(preferenceXMLInputStream);
            logger.debug("Successfully wrote preference XML content with url: " + contentWriter.getContentUrl());
        }
        catch (Exception e){
            logger.error("Failed to set preference XML content.", e);
        }
    }

    private void addAnnotableAspect(){
        nodeService.addAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE, null);
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
