package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.Bookmark;
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
public class SaveBookmarkContent extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(SaveBookmarkContent.class);

    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private NodeRef documentNodeRef = null;

    public SaveBookmarkContent(){
        nodeService = serviceRegistry.getNodeService();
    }

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            Bookmark bookmark = new Gson().fromJson(request.getContent().getContent(), Bookmark.class);

            if(!nodeService.hasAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE)){
                addAnnotableAspect();
            }

            if(bookmark != null){
                documentNodeRef = new NodeRef(bookmark.getParentNodeRef());
                logger.debug("Document NodeRef: " + documentNodeRef.toString());

                String bookmarkName = null;
                if(bookmark.getName() != null){
                    bookmarkName = bookmark.getName();
                    logger.debug("Retrieved annotation: " + bookmarkName);
                }

                InputStream bookmarkInputStream = null;
                if(bookmark.getContent() != null){
                    bookmarkInputStream = new ByteArrayInputStream(bookmark.getContent());
                }

                String bookmarkNodeRefString = bookmark.getId();
                logger.debug("Bookmark NodeRef Empty: " + bookmarkNodeRefString.isEmpty());

                if(bookmarkNodeRefString.isEmpty()){
                    createBookmark(bookmarkName,  bookmarkInputStream);
                }
                else{
                    NodeRef bookmarkNodeRef = new NodeRef(bookmark.getId());
                    setBookmarkContent(bookmarkNodeRef, bookmarkInputStream);
                }
            }
        }
        catch(Exception e){
            logger.debug("Failed to save annotation content: ", e);
        }
    }

    private void createBookmark(String bookmarkName, InputStream bookmarkInputStream){
        try{
            Map<QName, Serializable> bookmarkProperties = new HashMap<QName, Serializable>();
            bookmarkProperties.put(ContentModel.PROP_NAME, bookmarkName);

            NodeRef bookmarkNodeRef = nodeService.createNode(
                documentNodeRef,
                SnowboundContentModel.ASSOC_BOOKMARK,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, bookmarkName),
                SnowboundContentModel.TYPE_BOOKMARK,
                    bookmarkProperties).getChildRef();
            logger.debug("Successfully create bookmark: " + nodeService.getProperty(bookmarkNodeRef, ContentModel.PROP_NAME));

            Map<QName, Serializable> hiddenAspectProperties = new HashMap<QName, Serializable>();
            hiddenAspectProperties.put(ContentModel.PROP_VISIBILITY_MASK, 32768);
            nodeService.addAspect(bookmarkNodeRef, ContentModel.ASPECT_HIDDEN, hiddenAspectProperties);

            if(bookmarkNodeRef != null){
                setBookmarkContent(bookmarkNodeRef, bookmarkInputStream);
            }
        }
        catch (Exception e){
            logger.error("Failed to create bookmark: ", e);
        }
    }

    private void setBookmarkContent(NodeRef bookmarkNodeRef, InputStream bookmarkInputStream){
        try{
            ContentService contentService = serviceRegistry.getContentService();
            ContentWriter contentWriter = contentService.getWriter(bookmarkNodeRef, ContentModel.PROP_CONTENT, true);
            contentWriter.setMimetype("text/xml");
            contentWriter.putContent(bookmarkInputStream);
            logger.debug("Successfully wrote bookmark content with url: " + contentWriter.getContentUrl());
        }
        catch (Exception e){
            logger.error("Failed to set bookmark content.", e);
        }
    }

    private void addAnnotableAspect(){
        nodeService.addAspect(documentNodeRef, SnowboundContentModel.ASPECT_ANNOTABLE, null);
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
