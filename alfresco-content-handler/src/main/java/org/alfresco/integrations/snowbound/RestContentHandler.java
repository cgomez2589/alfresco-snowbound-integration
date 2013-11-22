 package org.alfresco.integrations.snowbound;

 import com.google.api.client.http.*;
 import com.google.api.client.http.javanet.NetHttpTransport;
 import com.google.api.client.json.JsonObjectParser;
 import com.google.api.client.json.jackson.JacksonFactory;
 import com.google.gson.Gson;
 import com.snowbound.common.utils.Logger;
 import com.snowbound.common.utils.ClientServerIO;
 import com.snowbound.snapserv.servlet.*;
 import org.alfresco.integrations.snowbound.entity.*;

 import javax.servlet.ServletConfig;
 import javax.servlet.ServletContext;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.*;

 /**
  * Author: Kyle Adams
  * Date: 2/11/13
  * Time: 5:11 PM
  */
 public class RestContentHandler implements FlexSnapSIContentHandlerInterface, FlexSnapSISaverInterface {
     Logger logger = Logger.getInstance();

     public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
     private HttpRequestFactory requestFactory;

     private String nodeRef;
     private String authenticationTicket;
     private Properties properties = null;
     private Document document = null;
     private List<Annotation> annotationList = null;
     private Bookmark bookmark = null;
     private PreferenceXML preferenceXML = null;
     private Map<String,Annotation> annotationHashMap = null;

     protected static final String PARAM_FILE_PATH = "filePath";
     protected static String gFilePath = "c:/imgs/";

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

	     String pathParam = servletConfig.getInitParameter(PARAM_FILE_PATH);
	     	if (pathParam != null)
	         {
	             setFilePath(pathParam, servletConfig.getServletContext());
       		 }

         }


         public static void setFilePath(String pathParam, ServletContext context)
         {
             if ((pathParam.startsWith("./") || pathParam.startsWith(".\\"))
                 && context != null)
             {
                 gFilePath = context.getRealPath(pathParam.substring(2))
                     + File.separator;
                 Logger.getInstance().log(Logger.INFO,
                                          "File path for documents is configured to "
                                              + gFilePath);
             }
             else
             {
                 gFilePath = pathParam;
             }
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
             nodeRef = contentHandlerInput.getDocumentId();
             authenticationTicket = contentHandlerInput.getClientInstanceId();

             GenericUrl documentUrl = new GenericUrl(
                     this.alfrescoBaseUrl +
                             "/service/integrations/snowbound/GetDocument?alf_ticket=" +
                             authenticationTicket +
                             "&nodeRef=" + nodeRef.replace("workspace/", "workspace://") +
                             "&format=json");

             String documentJsonResponse = sendHttpRequest(documentUrl).parseAsString();
             document = new Gson().fromJson(documentJsonResponse, Document.class);

             contentHandlerResult.put(ContentHandlerResult.KEY_DOCUMENT_CONTENT, document.getContent());
             contentHandlerResult.put(ContentHandlerResult.KEY_DOCUMENT_DISPLAY_NAME, document.getName());
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
             nodeRef = contentHandlerInput.getDocumentId();
             authenticationTicket = contentHandlerInput.getClientInstanceId();


             GenericUrl annotationsUrl = new GenericUrl(
             this.alfrescoBaseUrl +
                     "/service/integrations/snowbound/GetAnnotations?alf_ticket=" +
                     authenticationTicket +
                     "&nodeRef=" + nodeRef.replace("workspace/", "workspace://") +
                     "&format=json");
             logger.log(Logger.FINEST, "Annotation Request Url: " + annotationsUrl);


             String annotationsJsonResponse = sendHttpRequest(annotationsUrl).parseAsString();
             AnnotationList annotations = new Gson().fromJson(annotationsJsonResponse, AnnotationList.class);
             logger.log(Logger.FINEST, "Annotation From Json: " + annotations);


             annotationHashMap = new HashMap<String, Annotation>();
             annotationList = annotations.getAnnotations();

             String[] annotationNames = new String[annotations.getAnnotations().size()];
             logger.log(Logger.FINEST, "Annotation List Size: " + annotationList.size());


             if (annotationList != null) {
                 for (int i=0; i< annotationList.size(); i++){
                     Annotation annotation = annotationList.get(i);
                     String name = annotation.getName();
                     annotationHashMap.put(name, annotation);

                     logger.log(Logger.FINEST, "Found Annotation Name: " + name + " with id: " + annotation.getId());
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
             logger.log(Logger.FINEST, "Retrieved Annotation: " + annotation.getName() + " with NodeRef: " + annotation.getId());

             properties.put(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL, annotation.getPermissionLevel());
             logger.log(Logger.FINEST, "Annotation Permission Level: " + properties.get(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL));

             properties.put(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG, new Boolean(annotation.getRedactionFlag()));
             logger.log(Logger.FINEST, "Annotation Redaction Flag: " + properties.get(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG));


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

             if(!isNullOrEmpty(annotationHashMap)){
                 Annotation annotation = annotationHashMap.get(contentHandlerInput.getAnnotationId());
                 logger.log(Logger.FINEST, "Annotation Content: " + new String(annotation.getContent()));

                 contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_CONTENT, annotation.getContent());
                 contentHandlerResult.put(ContentHandlerResult.KEY_ANNOTATION_DISPLAY_NAME, annotation.getName());
             }
             else{
             	return null;
             }


         }
         catch (Exception e){
             logger.log(Logger.SEVERE, "Failed to get annotation content: " + e.getMessage());
         }
         logger.log(Logger.FINEST, "End of getAnnotationContent method...");
         return contentHandlerResult;
     }

     public ContentHandlerResult getBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         ContentHandlerResult contentHandlerResult = new ContentHandlerResult();

         try{
             logger.log(Logger.FINEST, "Entering getBookmarkContent method...");

             GenericUrl bookmarkUrl = new GenericUrl(
                     this.alfrescoBaseUrl +
                             "/service/integrations/snowbound/GetBookmark?alf_ticket=" +
                             authenticationTicket +
                             "&nodeRef=" + nodeRef.replace("workspace/", "workspace://") +
                             "&format=json");

             String documentJsonResponse = sendHttpRequest(bookmarkUrl).parseAsString();
             bookmark = new Gson().fromJson(documentJsonResponse, Bookmark.class);
             contentHandlerResult.put(ContentHandlerResult.KEY_BOOKMARK_CONTENT, bookmark.getContent());
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
             String documentID = contentHandlerInput.getDocumentId();
             if( documentID.equalsIgnoreCase("<EMPTY DOCUMENT>")){
                 documentID = "NewDocument";
             }

             byte[] documentContent = contentHandlerInput.getDocumentContent();
             AnnotationLayer[] annotations = contentHandlerInput.getAnnotationLayers();
             byte[] bookmarkContent = contentHandlerInput.getBookmarkContent();

             logger.log(" saveDocumentComponents, CLIENT ID: " + authenticationTicket);
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
                     contentHandlerInput.put(ContentHandlerInput.KEY_CLIENT_INSTANCE_ID, authenticationTicket);
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
         String documentKey = contentHandlerInput.getDocumentId();
         if (documentKey.equals("<EMPTY DOCUMENT>"))
         {
             documentKey = "NewDocument";
         }
         logger.log(Logger.FINEST, " saveDocumentComponentsAs, CLIENT ID: " + authenticationTicket);
         logger.log(Logger.FINEST, " saveDocumentComponentsAs, DOC KEY: " + documentKey);
         logger.log(Logger.FINEST, " Call saveDocumentComponents");
         return saveDocumentComponents(contentHandlerInput);
     }

     public ContentHandlerResult saveDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
         try{
             logger.log(Logger.FINEST, " Begin saveDocumentContent method...");
             String documentKey = contentHandlerInput.getDocumentId();
             byte[] documentContent = contentHandlerInput.getDocumentContent();

             logger.log(Logger.FINEST, "  saveDocumentContents, CLIENT ID: " + authenticationTicket);
             logger.log(Logger.FINEST, "  saveDocumentContents, DOC ID: " + documentKey);

             if (documentContent == null){
                 return null;
             }

             GenericUrl saveDocumentContentUrl = new GenericUrl(
                     this.alfrescoBaseUrl +
                             "/service/integrations/snowbound/SaveDocumentContent?alf_ticket=" +
                             authenticationTicket);
             Document document = new Document();
             document.setId(nodeRef.replace("workspace/", "workspace://"));
             document.setContent(documentContent);

             String jsonString = new Gson().toJson(document);
             postJsonHttpRequest(saveDocumentContentUrl, jsonString.getBytes());

             contentHandlerResult.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentKey);
         }
         catch (Exception e){
             logger.log(Logger.SEVERE, "Failed to save document content: " + e.getMessage());
         }
         logger.log(Logger.FINEST, " End saveDocumentContent method...");
         return contentHandlerResult;
     }

     public ContentHandlerResult saveAnnotationContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, " Begin saveAnnotationContent method...");

         String annotationLayer = contentHandlerInput.getAnnotationId();
         byte[] annotationContent = contentHandlerInput.getAnnotationContent();
         Hashtable annProperties = contentHandlerInput.getAnnotationProperties();
         logger.log(Logger.FINEST, "  saveAnnotationContent, CLIENT ID: " + authenticationTicket);
         logger.log(Logger.FINEST, "  saveAnnotationContent, ANN ID: " + annotationLayer);

         try{
             if (annotationContent != null){
                 if (!annProperties.isEmpty()){
                     Annotation annotation = new Annotation();

                     Boolean tmpRedactionFlag = (Boolean) annProperties.get(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG);
                     logger.log(Logger.FINEST, " Annotation Redaction Flag: " + tmpRedactionFlag);

                     Integer tmpPermissionLevel = (Integer) annProperties.get(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL);
                     logger.log(Logger.FINEST, " Annotation Permission Level: " + tmpPermissionLevel);

                     boolean redactionFlag = false;
                     int permissionLevel = PERM_DELETE.intValue();

                     if (tmpRedactionFlag != null){
                         redactionFlag = tmpRedactionFlag.booleanValue();
                         annotation.setRedactionFlag(redactionFlag);
                     }
                     if(tmpPermissionLevel != null){
                     	permissionLevel = tmpPermissionLevel.intValue();
                     }

                     if (redactionFlag == true){
                     	annotation.setPermissionLevel(PERM_REDACTION.intValue());
                     }
                     else{
                         annotation.setPermissionLevel(PERM_DELETE.intValue());
                     }

                     annotation.setContent(annotationContent);
                     annotation.setName(annotationLayer);
                     annotation.setParentNodeRef(nodeRef.replace("workspace/", "workspace://"));

                     if(isNullOrEmpty(annotationHashMap)){
                         annotation.setId("");
                     }
                     else{
                         if(annotationHashMap.containsKey(annotationLayer)){
                             annotation.setId(annotationHashMap.get(annotationLayer).getId());
                         }
                         else{
                             annotation.setId("");
                         }
                     }

                     String jsonString = new Gson().toJson(annotation);
                     GenericUrl saveAnnotationContentUrl = new GenericUrl(
                             this.alfrescoBaseUrl + "/service/integrations/snowbound/SaveAnnotationContent?alf_ticket=" + authenticationTicket);
                     postJsonHttpRequest(saveAnnotationContentUrl, jsonString.getBytes());
                     logger.log(Logger.FINEST, "  saveAnnotationContent, Saving layer: " + annotationLayer);
                 }
             }
             else{
                 return null;
             }
         }
         catch (Exception e){
             logger.printStackTrace(e);
         }
         logger.log(Logger.FINEST, " End saveAnnotationContent...");
         return new ContentHandlerResult();
     }

     public ContentHandlerResult saveBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering saveBookmarkContent method...");

         String documentID = contentHandlerInput.getDocumentId();
         byte[] bookmarkContent = contentHandlerInput.getBookmarkContent();
         Logger.getInstance().log(Logger.FINEST, "  saveBookmarkContents, CLIENT ID: " + authenticationTicket);
         Logger.getInstance().log(Logger.FINEST, "  saveBookmarkContents, DOC ID: " + documentID);


         ContentHandlerResult contentHandlerResult = new ContentHandlerResult();
         if (bookmarkContent == null)
         {
             return null;
         }
         else{
             Bookmark myBookmark = new Bookmark();
             myBookmark.setParentNodeRef(nodeRef.replace("workspace/", "workspace://"));
             myBookmark.setName(documentID + ".bookmarks.xml");
             myBookmark.setContent(bookmarkContent);

             if(isNullOrEmpty(annotationHashMap)){
                 myBookmark.setId("");
             }
             else{
                 myBookmark.setId(bookmark.getId());
             }
             String jsonString = new Gson().toJson(myBookmark);
             GenericUrl saveBookmarkContentUrl = new GenericUrl(
                     this.alfrescoBaseUrl + "/service/integrations/snowbound/SaveBookmarkContent?alf_ticket=" + authenticationTicket);
             postJsonHttpRequest(saveBookmarkContentUrl, jsonString.getBytes());

             contentHandlerResult.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentID);
         }


         Logger.getInstance().log(Logger.FINEST, " End saveBookmarkContents...");
         return contentHandlerResult;
     }

     public ContentHandlerResult deleteAnnotation(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering deleteAnnotation method...");

         String clientID = contentHandlerInput.getClientInstanceId();
         String documentID = contentHandlerInput.getDocumentId();
         String annotationName = contentHandlerInput.getAnnotationId();
         logger.log(Logger.FINEST, " deleteAnnotation, CLIENT ID: " + clientID);
         logger.log(Logger.FINEST, " deleteAnnotation, DOC ID: " + documentID);
         logger.log(Logger.FINEST, " deleteAnnotation, Deleting annotation: " + annotationName);


         try{
             Annotation annotation = annotationHashMap.get(annotationName);

             String jsonString = new Gson().toJson(annotation);
             GenericUrl deleteAnnotationUrl = new GenericUrl(
                     this.alfrescoBaseUrl + "/service/integrations/snowbound/DeleteAnnotation?alf_ticket=" + authenticationTicket);
             postJsonHttpRequest(deleteAnnotationUrl, jsonString.getBytes());
         }
         catch (Throwable e){
             logger.log(Logger.FINEST, "Error deleting layer " + annotationName + " : " + e.getMessage());
         }
         return null;
     }

     public boolean hasAnnotations(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering hasAnnotations method...");

         return isNullOrEmpty(annotationHashMap);
     }

     public ContentHandlerResult getClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering getClientPreferencesXML method...");

         ContentHandlerResult result = new ContentHandlerResult();
         try{
             GenericUrl preferenceXMLUrl = new GenericUrl(
                     this.alfrescoBaseUrl +
                             "/service/integrations/snowbound/GetBookmark?alf_ticket=" +
                             authenticationTicket +
                             "&nodeRef=" + nodeRef.replace("workspace/", "workspace://") +
                             "&format=json");

             String preferenceXMJsonResponse = sendHttpRequest(preferenceXMLUrl).parseAsString();
             preferenceXML = new Gson().fromJson(preferenceXMJsonResponse, PreferenceXML.class);
             String xmlString = new String(preferenceXML.getContent());

             result.put(ContentHandlerResult.KEY_CLIENT_PREFERENCES_XML, xmlString);
         }
         catch (Exception e){
             logger.log(Logger.SEVERE, "Failed to get client preferences XML: " + e.getMessage());

             return null;
         }
         Logger.getInstance().log(Logger.FINEST,	"End getClientPreferencesXML...");
         return result;

     }

     public ContentHandlerResult saveClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering saveClientPreferencesXML method...");

         try{
             preferenceXML.setContent(contentHandlerInput.getClientPreferencesXML().getBytes());
             String jsonString = new Gson().toJson(preferenceXML);

             GenericUrl savePreferenceXMLContentUrl = new GenericUrl(
                     this.alfrescoBaseUrl + "/service/integrations/snowbound/SavePreferenceXMLContent?alf_ticket=" + authenticationTicket);
             logger.log(Logger.FINEST, " Using url: " + savePreferenceXMLContentUrl);

             postJsonHttpRequest(savePreferenceXMLContentUrl, jsonString.getBytes());
         }
         catch (Exception e){

         }

         return ContentHandlerResult.VOID;
     }

     public ContentHandlerResult sendDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering sendDocumentContent method...");

         // This method can be changed to save the document to their desired target directory.
	     ContentHandlerResult retVal = new ContentHandlerResult();
	     //HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
	     //String clientInstanceId = contentHandlerInput.getClientInstanceId();
	     //String documentKey = contentHandlerInput.getDocumentId();
	     boolean mergeAnnotations = contentHandlerInput.mergeAnnotations();
	     byte[] data = contentHandlerInput.getDocumentContent();
	     File saveFile = new File(gFilePath + "sendDocument-" + document.getName() );
	     ClientServerIO.saveFileBytes(data, saveFile);
	     return retVal;

     }

     public ContentHandlerResult publishDocument(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException {
         logger.log(Logger.FINEST, "Entering publishDocument method...");

         return null;  //To change body of implemented methods use File | Settings | File Templates.
     }

     public HttpRequestFactory getRequestFactory() {
         if (this.requestFactory == null) {
             this.requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                 public void initialize(HttpRequest request) throws IOException {
                     request.setParser(new JsonObjectParser(new JacksonFactory()));
                 }
             });
         }
         return this.requestFactory;
     }

     private HttpResponse sendHttpRequest(GenericUrl getUrl){
         HttpResponse response = null;
         try {
             HttpRequest request = getRequestFactory().buildGetRequest(getUrl);
             response = request.execute();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return response;
     }

     private void postJsonHttpRequest(GenericUrl postUrl, byte[] jsonContent){
         try {

             HttpContent body = new ByteArrayContent("application/json", jsonContent);
             HttpRequest request = getRequestFactory().buildPostRequest(postUrl, body);
             request.setConnectTimeout(120000);
             request.execute();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
     private static boolean isNullOrEmpty(final Map<String,Annotation> annotationHashMap){
         return annotationHashMap == null || annotationHashMap.isEmpty();
     }
}