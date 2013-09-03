package org.alfresco.integrations.snowbound;

import com.snowbound.common.utils.ClientServerIO;
import com.snowbound.common.utils.Logger;
import com.snowbound.snapserv.servlet.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.alfresco.integrations.snowbound.entity.Annotation;
import org.alfresco.integrations.snowbound.entity.AnnotationList;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Author: Kyle Adams
 * Date: 2/11/13
 * Time: 5:11 PM
 */
public class RestContentHandler implements FlexSnapSIContentHandlerInterface, FlexSnapSISaverInterface, FilenameFilter {
    Logger logger = Logger.getInstance();

    private String nodeRef;
    private String ticket;
    private Properties properties = null;
    private AnnotationList annotationList = null;
    private Map<String,Annotation> annotationHashMap = null;


    private String alfrescoBaseUrl = null;


    public RestContentHandler(){
        this.setPropertiesFile();
    }


    private void setPropertiesFile(){

        logger.log(Logger.FINEST, "Begin setPropertiesFile method...");
        properties = new Properties();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("snowbound.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.printStackMessage(Logger.SEVERE, e);
        }
    }



    public void init(ServletConfig servletConfig) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Using servlet: " + servletConfig.getServletName());
        alfrescoBaseUrl = properties.getProperty("alfresco.base.url");
    }

    public ContentHandlerResult getAvailableDocumentIds(ContentHandlerInput contentHandlerInput) {
        logger.log(Logger.FINEST, "Entering getAvailableDocumentIds method...");
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();

        String[] availableDocuments = new String[1];
        availableDocuments[0]=contentHandlerInput.getDocumentId();
        try{
            contentHandlerResult.put("KEY_AVAILABLE_DOCUMENT_IDS", availableDocuments);
        }
        catch (Exception e){
            logger.printStackMessage(Logger.SEVERE, e);
        }
        return contentHandlerResult;
    }

    public ContentHandlerResult getDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {

        logger.log(Logger.FINEST, "Entering getDocumentContent method...");
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
        try{
            if(this.nodeRef == null){
                this.nodeRef = contentHandlerInput.getDocumentId();
            }
            this.ticket = contentHandlerInput.getClientInstanceId();

            String contentURL = alfrescoBaseUrl + "/service/api/node/" + nodeRef + "/content?alf_ticket=" + ticket;
            logger.log(Logger.FINEST, "Alfresco Content URL: " + contentURL);

            contentHandlerResult.put(ContentHandlerResult.KEY_DOCUMENT_CONTENT, ClientServerIO.getURLBytes(contentURL));
            contentHandlerResult.put(ContentHandlerResult.KEY_DOCUMENT_DISPLAY_NAME, "Test123");
        }
        catch (Exception e){
            logger.printStackMessage(Logger.SEVERE, e);
            return null;
        }
        logger.log(Logger.FINEST, "End getDocumentContent method...");
        return contentHandlerResult;
    }

    public ContentHandlerResult eventNotification(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public ContentHandlerResult getAnnotationNames(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();

        try {
            logger.log(Logger.FINEST, "Entering getAnnotationNames method...");
            String annotationPropertiesUrl = this.alfrescoBaseUrl + "/service/integrations/snowbound/AnnotationProperties";

            Client client = Client.create();
            WebResource webResource = client.resource(annotationPropertiesUrl);
            MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("nodeRef", nodeRef.replace("workspace/", "workspace://"));
            queryParams.add("alf_ticket", contentHandlerInput.getClientInstanceId());
            queryParams.add("format", "json");
            String annotationJsonResponse = webResource.queryParams(queryParams).get(String.class);
            logger.log(Logger.FINEST, "Annotation Properties JSON Response: " + annotationJsonResponse);

            annotationList = new ObjectMapper().readValue(annotationJsonResponse, AnnotationList.class);
            annotationHashMap = new HashMap<String, Annotation>();

            String[] annotationNames = new String[annotationList.getAnnotations().size()];
            if (annotationList != null) {
                for (int i=0; i<annotationList.getAnnotations().size(); i++){
                    Annotation annotation = annotationList.getAnnotations().get(i);
                    String name = annotation.getName();
                    annotationHashMap.put(name, annotation);

                    logger.log(Logger.FINEST, "Found Annotation Name: " + name);
                    annotationNames[i] = name;
                }
            }
            contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_NAMES, annotationNames);
        }
        catch (Exception e) {
            logger.log(Logger.SEVERE, "Failed to get annotation names: " + e.getMessage());
        }
        logger.log(Logger.FINEST, "End getAnnotationNames method...");
        return contentHandlerResult;
    }

    public ContentHandlerResult getAnnotationProperties(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();

        try{
            logger.log(Logger.FINEST, "Entering getAnnotationProperties method...");
            Hashtable properties = new Hashtable();

            Annotation annotation = annotationHashMap.get(contentHandlerInput.getAnnotationId());
            logger.log(Logger.FINEST, "Retrieved Annotation: " + annotation.getName());

            properties.put("permissionLevel", annotation.getPermissionLevel());
            properties.put("redactionFlag", annotation.getRedactionFlag());

            contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_PROPERTIES, properties);
        }
        catch(Exception e){
            logger.log(Logger.SEVERE, "Failed to get annotation properties: " + e.getMessage());
        }
        logger.log(Logger.FINEST, "  End of getAnnotationProperties method...");
        return contentHandlerResult;
    }

    public ContentHandlerResult getAnnotationContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
        try{
            logger.log(Logger.FINEST, "Entering getAnnotationContent method...");
            Annotation annotation = annotationHashMap.get(contentHandlerInput.getAnnotationId());
            String annotationNodeRef = annotation.getId().replace("workspace://", "workspace/");

            String contentURL = alfrescoBaseUrl + "/service/api/node/" + annotationNodeRef + "/content?alf_ticket=" + ticket;
            logger.log(Logger.FINEST, "Alfresco Content URL: " + contentURL);

            contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_CONTENT, ClientServerIO.getURLBytes(contentURL));
            contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_DISPLAY_NAME, contentHandlerInput.getAnnotationId());
        }
        catch (Exception e){
            logger.log(Logger.SEVERE, "Failed to get annotation content: " + e.getMessage());
        }
        logger.log(Logger.FINEST, "End of getAnnotationContent method...");
        return contentHandlerResult;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult getBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();

        try{
            logger.log(Logger.FINEST, "Entering getBookmarkContent method...");

            String bookmarkContentUrl = alfrescoBaseUrl + "/service/integrations/snowbound/BookmarkContent?nodeRef=" + nodeRef;
            contentHandlerResult.put(ContentHandlerResult.KEY_BOOKMARK_CONTENT, ClientServerIO.getURLBytes(bookmarkContentUrl));
        }
        catch(Exception e){
            logger.log(Logger.SEVERE, "Failed to get bookmark content: " + e.getMessage());
        }
        logger.log(Logger.FINEST, "End getBookmarkContent...");
        return contentHandlerResult;
    }

    public ContentHandlerResult saveDocumentComponents(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
        try{
            logger.log(Logger.FINEST, "Begin saveDocumentComponents method...");
            String clientID = contentHandlerInput.getClientInstanceId();
            String documentID = contentHandlerInput.getDocumentId();
            if( documentID.equalsIgnoreCase("<EMPTY DOCUMENT>")){
                documentID = "NewDocument";
            }

            byte[] documentContent = contentHandlerInput.getDocumentContent();
            AnnotationLayer[] annotations = contentHandlerInput.getAnnotationLayers();
            byte[] bookmarkContent = contentHandlerInput.getBookmarkContent();

            logger.log(" saveDocumentComponents, CLIENT ID: " + clientID);
            logger.log(" saveDocumentComponents, DOC ID: " + documentID);

            if (documentContent != null){
                logger.log(" Calling saveDocumentContents method...");
                saveDocumentContent(contentHandlerInput);
            }else{
                logger.log(Logger.FINEST, " Document not changed, not calling saveDocumentContent.");
            }
            int annIndex = 0;
            if (annotations != null){
                for (annIndex = 0; annIndex < annotations.length; annIndex++){
                    contentHandlerInput.put(ContentHandlerInput.KEY_CLIENT_INSTANCE_ID, clientID);
                    contentHandlerInput.put(ContentHandlerInput.KEY_DOCUMENT_ID, documentID);
                    contentHandlerInput.put(ContentHandlerInput.KEY_ANNOTATION_ID, annotations[annIndex].getLayerName());
                    contentHandlerInput.put(ContentHandlerInput.KEY_ANNOTATION_CONTENT, annotations[annIndex].getData());
                    contentHandlerInput.put(ContentHandlerInput.KEY_ANNOTATION_PROPERTIES, annotations[annIndex].getProperties());
                    logger.log(Logger.FINEST, " Calling saveAnnotationContent, layer number " + annIndex +"...");
                    saveAnnotationContent(contentHandlerInput);
                }
            }else{
                logger.log(Logger.FINEST, " Annotation layer not changed: " + annotations[annIndex].getLayerName());
            }

            if (bookmarkContent != null){
                logger.log(Logger.FINEST, " Calling saveBookmarkContent method...");
                saveBookmarkContent(contentHandlerInput);
            }else{
                logger.log(Logger.FINEST, " Bookmarks not changed.");
            }

            contentHandlerResult.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentID);
        }
        catch (Exception e){
            logger.log(Logger.SEVERE, "Failed to save document components: " + e.getMessage());
        }
        logger.log(Logger.FINEST, "End saveDocumentComponents method...");
        return contentHandlerResult;
    }

    public ContentHandlerResult saveDocumentComponentsAs(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Begin saveDocumentComponentsAs method...");
        String clientID = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        if (documentKey.equals("<EMPTY DOCUMENT>"))
        {
            documentKey = "NewDocument";
        }
        logger.log(Logger.FINEST, " saveDocumentComponentsAs, CLIENT ID: " + clientID);
        logger.log(Logger.FINEST, " saveDocumentComponentsAs, DOC KEY: " + documentKey);
        logger.log(Logger.FINEST, " Call saveDocumentComponents");
        return saveDocumentComponents(contentHandlerInput);
    }

    public ContentHandlerResult saveDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
        try{
            logger.log(Logger.FINEST, " Begin saveDocumentContent method...");
            String clientID = contentHandlerInput.getClientInstanceId();
            String documentKey = contentHandlerInput.getDocumentId();
            byte[] documentContent = contentHandlerInput.getDocumentContent();

            logger.log(Logger.FINEST, "  saveDocumentContents, CLIENT ID: " + clientID);
            logger.log(Logger.FINEST, "  saveDocumentContents, DOC ID: " + documentKey);

            if (documentContent == null)
            {
                return null;
            }

            File saveFile = new File(gFilePath + documentKey);
            ClientServerIO.saveFileBytes(data, saveFile);
            ClientServerIO.
            contentHandlerResult.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentKey);
        }
        catch (Exception e){
            logger.log(Logger.SEVERE, "Failed to save document content: " + e.getMessage());
        }
        logger.log(Logger.FINEST, " End saveDocumentContent method...");
        return contentHandlerResult;
    }

    public ContentHandlerResult deleteAnnotation(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering deleteAnnotation method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult sendDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering sendDocumentContent method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasAnnotations(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering hasAnnotations method...");

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult getClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering getClientPreferencesXML method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult saveClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering saveClientPreferencesXML method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult saveBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering saveBookmarkContent method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult saveAnnotationContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering saveAnnotationContent method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentHandlerResult publishDocument(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
        logger.log(Logger.FINEST, "Entering publishDocument method...");

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean accept(File dir, String name) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
