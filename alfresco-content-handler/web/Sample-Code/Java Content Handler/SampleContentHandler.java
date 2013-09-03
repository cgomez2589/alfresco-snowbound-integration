/**
 * Copyright (C) 2002-2013 by Snowbound Software Corp.
 *
 * This is example code for all SnowBound customers to freely copy
 * and use however they wish.
 *
 * Sample VirtualViewer File Content Handler
 *  Please note this is not a production-quality sample.
 *  You should add error and security checking at the very least.
 *
 * The full VirtualViewer File Content Handler sample is available by request. 
 * Please contact technical support at http://support.snowbound.com for the full sample.
 */
package com.snowbound.snapserv.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.snowbound.common.utils.ClientServerIO;
import com.snowbound.common.utils.Logger;
import com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface;

public class SampleContentHandler implements FlexSnapSIContentHandlerInterface, FlexSnapSISaverInterface, FilenameFilter
{
    protected static final String PARAM_FILE_PATH = "filePath";
    protected static final String PARAM_PREFERENCES_PATH = "preferencesPath";
    protected static final String PARAM_PARSE_PATHS_IN_DOCUMENT_ID = "parsePathsInDocumentId";
    protected static String gFilePath = "c:/imgs/";
    protected static String gPreferencesPath = "c:/imgs/";
    private final static String FORWARD_SLASH = "/";
    private final static String BACK_SLASH = "\\";
    private boolean parsePathsInDocumentKey = false;

    public void init(ServletConfig config)
    {
    	gFilePath = config.getInitParameter(PARAM_FILE_PATH);
        gPreferencesPath = config.getInitParameter(PARAM_PREFERENCES_PATH);
        if (gPreferencesPath == null)
        {
            gPreferencesPath = gFilePath;
        }
    	String parseBooleanString = config.getInitParameter(PARAM_PARSE_PATHS_IN_DOCUMENT_ID);
    	if ("true".equalsIgnoreCase(parseBooleanString))
	{
	   parsePathsInDocumentKey = true;
	}
    }

    /** 
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getDocumentContent(ContentHandlerInput)
     */
    public ContentHandlerResult getDocumentContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin getDocumentContent method...");
    	String clientID = input.getClientInstanceId();
    	String documentID = input.getDocumentId();
    	Logger.getInstance().log(Logger.FINEST, " getDocumentContent, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " getDocumentContent, DOC KEY: " + documentID);
    	
    	String fullFilePath = gFilePath + URLDecoder.decode(documentID);
    	File file = new File(fullFilePath);
    	ContentHandlerResult result = new ContentHandlerResult();

    	try
    	{
            result.put(ContentHandlerResult.KEY_DOCUMENT_CONTENT, ClientServerIO.getFileBytes(file));
    	}
        catch (FileNotFoundException fnfe)
        {
            Logger.getInstance().log(Logger.SEVERE, fnfe.getMessage());
            throw new FlexSnapSIAPIException("Document not found: " + documentID);
        }
        catch (Exception e)
    	{
    		return null;
    	}
    	Logger.getInstance().log(Logger.FINEST, "End getDocumentContent method...");
    	return result;
    }
     
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getAnnotationNames(ContentHandlerInput)
     */   
    /* GET ANNOTATION NAME
     * This method returns an array of annotation keys. For each key returned, getAnnotationContent is called
     */
    public ContentHandlerResult getAnnotationNames(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log("Begin getAnnotationNames method...");
        String clientID = input.getClientInstanceId();
        String documentKey = input.getDocumentId();
        Vector vNames = new Vector();
        String documentPath = "";
        String documentFile = documentKey;
    	Logger.getInstance().log(Logger.FINEST, " getAnnotationNames, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " getAnnotationNames, DOC KEY: " + documentKey);
    	
        if (parsePathsInDocumentKey)
        {
            boolean pathFound = false;
            String pathSeparator = "";
            if (documentKey.indexOf(FORWARD_SLASH) != -1)
            {
                pathFound = true;
                pathSeparator = FORWARD_SLASH;
            }
            else if (documentKey.indexOf(BACK_SLASH) != -1)
            {
                pathFound = true;
                pathSeparator = BACK_SLASH;
            }
            if (pathFound)
            {
                int pathPoint = documentKey.lastIndexOf(pathSeparator) + 1;
                documentPath = documentKey.substring(0, pathPoint);
                documentFile = documentKey.substring(pathPoint);
            }
        }
        String annPath = gFilePath + documentPath;
        File imgDirectory = new File(annPath);
        String[] files = imgDirectory.list();
        for (int i = 0; i < files.length; i++)
        {
            String fileName = files[i];
            if (!fileName.equals(documentFile)
                && fileName.indexOf(documentFile) == 0
                && fileName.endsWith(".ann")
            /*
             * && fileName.indexOf("-redactionBurn") == -1 ||
             * clientID.toUpperCase().indexOf("SUPER") != -1)
             */)
            {
                int nameBegin = documentFile.length() + 1;
                int nameEnd = fileName.lastIndexOf(".ann");
                if (fileName.indexOf("-redactionBurn") != -1)
                {
                    nameEnd = fileName.lastIndexOf("-redactionBurn");
                }
                else if (fileName.indexOf("-redactionEdit") != -1)
                {
                	nameEnd = fileName.lastIndexOf("-redactionEdit");
                }
                String annName = fileName.substring(nameBegin, nameEnd);
                vNames.addElement(annName);
            }
        }
        String[] arrayNames = new String[vNames.size()];
        for (int i = 0; i < arrayNames.length; i++)
        {
            arrayNames[i] = (String) vNames.elementAt(i);
        }
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.KEY_ANNOTATION_NAMES, arrayNames);
        Logger.getInstance().log("End getAnnotationNames method...");
        return result;
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getAnnotationContent(ContentHandlerInput)
     */
    /* GET ANNOTATIONS
     * This method will get called for each annotation that was returned in getAnnotationNames.
     * It returns the annotation file as a byte array.
     */
    public ContentHandlerResult getAnnotationContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin getAnnotationContent method...");
    	String clientID = input.getClientInstanceId();
    	String documentKey = input.getDocumentId();
    	String annotationKey = input.getAnnotationId();
    	Logger.getInstance().log(Logger.FINEST, "  ");
    	Logger.getInstance().log(Logger.FINEST, " getAnnotationContent, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " getAnnotationContent, DOC KEY: " + documentKey);
    	Logger.getInstance().log(Logger.FINEST, "  getAnnotationContent, ANN KEY: " + annotationKey);
    	
		if (clientID.equals("crashGetAnnCont")){	
			throw new FlexSnapSIAPIException ("-Get Ann Content crashed!!!");
		}
    	
    	String annotationFilename = documentKey + "." + annotationKey + ".ann";
    	String fullFilePath = gFilePath + annotationFilename;
    	Logger.getInstance().log(Logger.FINEST, " Annotation file: " + fullFilePath);
    	Hashtable props = null;
    	ContentHandlerResult propsResult = getAnnotationProperties(input);
    	if (propsResult != null)
    	{
    		Logger.getInstance().log(Logger.FINEST, " Calling getAnnotationProperties method... ");
    		props = propsResult.getAnnotationProperties();
                Logger.getInstance().log(Logger.FINEST, " Back from getAnnotationProperties method... ");
    	}
    	Boolean tmpRedactionFlag = (Boolean) props.get("redactionFlag");
    	Integer tmpPermissionLevel = (Integer) props.get("permissionLevel");
    	boolean redactionFlag = false;
    	int permissionLevel = PERM_DELETE.intValue();

    	if (tmpRedactionFlag != null)
    	{
    		redactionFlag = tmpRedactionFlag.booleanValue();
    	}

    	if (tmpPermissionLevel != null)
    	{
    		permissionLevel = tmpPermissionLevel.intValue();
    	}

    	// this is a burned in redaction
    	if ((redactionFlag == true) && (permissionLevel < PERM_VIEW.intValue()))
    	{
    		annotationFilename = documentKey + "." + annotationKey + "-redactionBurn.ann";
    		fullFilePath = gFilePath + annotationFilename;
    	}

    	// this is an editable redaction
    	else if ((redactionFlag == true)
    			&& (permissionLevel >= PERM_VIEW.intValue()))
    	{
    		annotationFilename = documentKey + "." + annotationKey + "-redactionEdit.ann";
    		fullFilePath = gFilePath + annotationFilename;
    	}

    	try
    	{
    		File file = new File(fullFilePath);
    		Logger.getInstance().log(Logger.FINEST, " Getting file: " + fullFilePath);
    		byte[] bytes = ClientServerIO.getFileBytes(file);
    		ContentHandlerResult result = new ContentHandlerResult();
    		result.put(ContentHandlerResult.KEY_ANNOTATION_CONTENT, bytes);
                result.put(ContentHandlerResult.KEY_ANNOTATION_DISPLAY_NAME, input.getAnnotationId());
                Logger.getInstance().log(Logger.FINEST, "End of getAnnotationContent method...");
    		return result;
    	}
        catch (Exception e)
    	{
    		return null;
    	}
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getAnnotationProperties(ContentHandlerInput)
     */    
    /* GET ANNOTATION PROPERTIES
     * This method will get called from inside getAnnotationContent. It is used to return 
     * a permission level to the servlet for that annotation layer.
     * 
     * In this example, the permission level is based on the file name, with files either
     * having Delete permission or Redaction permission. The redaction flag determines if 
     * the user may edit a layer that would normally be burned in for other users.
     * 
     */
    public ContentHandlerResult getAnnotationProperties(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
        Logger.getInstance().log(Logger.FINEST, "  Begin getAnnotationProperties method...");
        String clientID = input.getClientInstanceId();
        String documentKey = input.getDocumentId();
        String annotationKey = input.getAnnotationId();
        Logger.getInstance().log(Logger.FINEST, "   getAnnotationProperties, CLIENT ID: " + clientID);
        Logger.getInstance().log(Logger.FINEST, "   getAnnotationProperties, DOC KEY: " + documentKey);
        Logger.getInstance().log(Logger.FINEST, "   getAnnotationProperties, ANN KEY: " + annotationKey);
        Hashtable properties = new Hashtable();

        String baseAnnFilename = documentKey + "." + annotationKey;
        String annFilename = gFilePath + baseAnnFilename + ".ann";
        String redactionEditFilename = gFilePath + baseAnnFilename + "-redactionEdit.ann";
        String redactionBurnFilename = gFilePath + baseAnnFilename + "-redactionBurn.ann";

        // Is it a regular annotation layer?
        File file = new File(annFilename);

        if (file.exists() == true)
        {
            properties.put("permissionLevel", PERM_DELETE);
            properties.put("redactionFlag", new Boolean(false));
            Logger.getInstance().log(Logger.FINEST, "   Permission: DELETE, false");
        }

        // Is it a redaction layer the user can edit?
        file = new File(redactionEditFilename);

        if (file.exists() == true)
        {
            properties.put("permissionLevel", PERM_DELETE);
            properties.put("redactionFlag", new Boolean(true));
            Logger.getInstance().log(Logger.FINEST, "   Permission: DELETE, true");
        }

        // Is it a redaction layer the user can NOT edit?
        file = new File(redactionBurnFilename);

        if (file.exists() == true)
        {
            properties.put("permissionLevel", PERM_REDACTION);
            properties.put("permissionLevel", FlexSnapSIContentHandlerInterface.PERM_PRINT_WATERMARK)
            properties.put("redactionFlag", new Boolean(true));
            Logger.getInstance().log(Logger.FINEST, "   Permission: REDACT, true");
        }
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.KEY_ANNOTATION_PROPERTIES, properties);
        Logger.getInstance().log(Logger.FINEST, "  End of getAnnotationProperties method...");
        return result;
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getBookmarkContent(ContentHandlerInput)
     */
    /*  GET BOOKMARKS
     * This method is called to retrieve the bookmark file. It returns the file as a byte array.
     * 
     * In this example, it gets the bookmark file from the file system based on the name of the 
     * document.
     */
    public ContentHandlerResult getBookmarkContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin getBookmarkContent...");
    	String clientID = input.getClientInstanceId();
    	String documentKey = input.getDocumentId();
    	Logger.getInstance().log(Logger.FINEST, " getBookmarkContent, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " getBookmarkContent, DOC KEY: " + documentKey);
    	String bookmarkFilename = documentKey + ".bookmarks.xml";
    	String fullFilePath = gFilePath + bookmarkFilename;
    	Logger.getInstance().log(Logger.FINEST, " getBookmarkContent, Retrieving bookmarks: " + fullFilePath);

    	try
    	{
    		File file = new File(fullFilePath);
    		byte[] bytes = ClientServerIO.getFileBytes(file);
    		ContentHandlerResult result = new ContentHandlerResult();
    		result.put(ContentHandlerResult.KEY_BOOKMARK_CONTENT, bytes);
    		Logger.getInstance().log(Logger.FINEST, "End getBookmarkContent...");
    		return result;
    	}
        catch (Exception e)
    	{
    		return null;
    	}
    }
    
    /**
     * SAVE METHODS
     */

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#saveDocumentComponents(ContentHandlerInput)
     */
    /* SAVE DOCUMENT COMPONENTS
     * This method gets called when using the Save Document command in the File menu 
     * of the applet. It saves the file, any annotations, and the bookmarks, by calling
     * the appropriate methods.
     *
     * If Page Manipulations is enabled in the applet, this is the recommended method 
     * to be used, and the individual Save Annotation and Save Bookmarks menu choices
     * should be disabled in the applet.
     */    
    public ContentHandlerResult saveDocumentComponents(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin saveDocumentComponents method...");
    	HttpServletRequest request = input.getHttpServletRequest();
    	String clientID = input.getClientInstanceId();
    	String documentID = input.getDocumentId();
    	if( documentID.equalsIgnoreCase("<EMPTY DOCUMENT>"))
    	{
    		documentID = "NewDocument";
    	}
    	byte[] data = input.getDocumentContent();
    	AnnotationLayer[] annotations = input.getAnnotationLayers();
    	byte[] bookmarkBytes = input.getBookmarkContent();  
    	
    	Logger.getInstance().log(" saveDocumentComponents, CLIENT ID: " + clientID);
    	Logger.getInstance().log(" saveDocumentComponents, DOC ID: " + documentID);
    	
    	if (data != null)
    	{
    		Logger.getInstance().log(" Calling saveDocumentContents method...");
    		saveDocumentContent(input);
    	}else{
    		Logger.getInstance().log(Logger.FINEST, " Document not changed, not calling saveDocumentContent.");
    	}
    	int annIndex = 0;
    	if (annotations != null)
        {
            for (annIndex = 0; annIndex < annotations.length; annIndex++)
            {
            	input.put(ContentHandlerInput.KEY_CLIENT_INSTANCE_ID, clientID);
            	input.put(ContentHandlerInput.KEY_DOCUMENT_ID, documentID);
            	input.put(ContentHandlerInput.KEY_ANNOTATION_ID, annotations[annIndex].getLayerName());
            	input.put(ContentHandlerInput.KEY_ANNOTATION_CONTENT, annotations[annIndex].getData());
            	input.put(ContentHandlerInput.KEY_ANNOTATION_PROPERTIES, annotations[annIndex].getProperties());
            	Logger.getInstance().log(Logger.FINEST, " Calling saveAnnotationContent, layer number " + annIndex +"...");
    			saveAnnotationContent(input);
            }
        }else{
    		Logger.getInstance().log(Logger.FINEST, " Annotation layer not changed: " + annotations[annIndex].getLayerName());
    	}

    	if (bookmarkBytes != null)
    	{
    		Logger.getInstance().log(Logger.FINEST, " Calling saveBookmarkContent method...");
    		saveBookmarkContent(input);
    	}else{
    		Logger.getInstance().log(Logger.FINEST, " Bookmarks not changed.");
    	}

    	ContentHandlerResult result = new ContentHandlerResult();
    	result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentID);
    	Logger.getInstance().log(Logger.FINEST, "End saveDocumentComponents method...");
    	return result;
    }


    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#saveDocumentComponentsAs(ContentHandlerInput)
     */
    /* SAVE DOCUMENT COMPONETS AS
     * This method gets called when using the Save Document As command in the File menu 
     * of the applet. It can be used for an alternate saving procedure, if so desired.
     */
    public ContentHandlerResult saveDocumentComponentsAs(ContentHandlerInput input)
        throws FlexSnapSIAPIException
    {
        Logger.getInstance().log(Logger.FINEST, "Begin saveDocumentComponentsAs method...");
        String clientID = input.getClientInstanceId();
	    String documentKey = input.getDocumentId();
    	if (documentKey.equals("<EMPTY DOCUMENT>"))
    	{
    		documentKey = "NewDocument";
    	}
    	Logger.getInstance().log(Logger.FINEST, " saveDocumentComponentsAs, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " saveDocumentComponentsAs, DOC KEY: " + documentKey);
        Logger.getInstance().log(Logger.FINEST, " Call saveDocumentComponents");
        return saveDocumentComponents(input);
    }


    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#saveDocumentContent(ContentHandlerInput)
     */
    /* SAVE DOCUMENT CONTENTS
     * This method saves the document itself.
     */
    public ContentHandlerResult saveDocumentContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, " Begin saveDocumentContent method...");
    	HttpServletRequest request = input.getHttpServletRequest();
    	String clientID = input.getClientInstanceId();
    	String documentKey = input.getDocumentId();
    	byte[] data = input.getDocumentContent();
    	
    	Logger.getInstance().log(Logger.FINEST, "  saveDocumentContents, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, "  saveDocumentContents, DOC ID: " + documentKey);

    	if (data == null)
    	{
    		return null;
    	}

    	File saveFile = new File(gFilePath + documentKey);
    	ClientServerIO.saveFileBytes(data, saveFile);
    	ContentHandlerResult result = new ContentHandlerResult();
    	result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentKey);
    	Logger.getInstance().log(Logger.FINEST, " End saveDocumentContent method...");
    	return result;
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#saveAnnotationContent(ContentHandlerInput)
     */
    /* SAVE ANNOTATIONS
     * This method saves an annotation layer. This is called once for each layer that
     * is associated with the document. It is invoked by the saveDocumentComponents 
     * method or by choosing Save Annotation from the File menu in the applet.
     * 
     * This example saves an annotation layer to the file system, with each filename 
     * dependent on whether it is considered a redaction layer or a normal layer.
     */
    public ContentHandlerResult saveAnnotationContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, " Begin saveAnnotationContent method...");
        HttpServletRequest request = input.getHttpServletRequest();
        String clientID = input.getClientInstanceId();
        String documentKey = input.getDocumentId();
        String annotationKey = input.getAnnotationId();
        byte[] data = input.getAnnotationContent();
        Hashtable annProperties = input.getAnnotationProperties();
        Logger.getInstance().log(Logger.FINEST, "  saveAnnotationContent, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, "  saveAnnotationContent, DOC ID: " + documentKey);
    	Logger.getInstance().log(Logger.FINEST, "  saveAnnotationContent, ANN ID: " + annotationKey);
    	
    	if (data == null)
    	{
    		return null;
    	}

    	String baseFilePath = gFilePath + documentKey + "." + annotationKey;
    	String annFilePath = baseFilePath + ".ann";
    	String editFilePath = baseFilePath + "-redactionEdit.ann";
    	String burnFilePath = baseFilePath + "-redactionBurn.ann";
    	String fullFilePath = annFilePath;

    	if (annProperties != null)
    	{
    		Boolean tmpRedactionFlag = (Boolean) annProperties.get("redactionFlag");
    		Integer tmpPermissionLevel = (Integer) annProperties.get("permissionLevel");
    		boolean redactionFlag = false;
    		int permissionLevel = PERM_DELETE.intValue();

    		if (tmpRedactionFlag != null)
    		{
    			redactionFlag = tmpRedactionFlag.booleanValue();
    		}

    		if (tmpPermissionLevel != null)
    		{
    			permissionLevel = tmpPermissionLevel.intValue();
    		}

    		if (permissionLevel <= PERM_REDACTION.intValue())
    		{
    			fullFilePath = burnFilePath;
    		}
    		else if (redactionFlag == true)
    		{
    			fullFilePath = editFilePath;
    		}
    	}

        // Make sure any existing ann files are deleted
    	File file = new File(annFilePath);
    	if (file.exists())
    	{
    		file.delete();
    	}

    	file = new File(burnFilePath);
    	if (file.exists())
    	{
    		file.delete();
    	}

    	file = new File(editFilePath);
    	if (file.exists())
    	{
    		file.delete();
    	}

    	Logger.getInstance().log(Logger.FINEST, "  saveAnnotationContent, Saving layer: " + annotationKey);

    	file = new File(fullFilePath);
    	try
    	{
            ClientServerIO.saveFileBytes(data, file);
    	}
    	catch (Exception e)
    	{
    		Logger.getInstance().printStackTrace(e);
    	}
        Logger.getInstance().log(Logger.FINEST, " End saveAnnotationContent...");
    	return new ContentHandlerResult();
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#saveBookmarkContent(ContentHandlerInput)
     */
    /* SAVE BOOKMARKS
     * This method saves the bookmarks.
     * It is invoked by the saveDocumentComponents method or by choosing Save Bookmarks from
     * the Bookmark menu in the applet. 
     */
    public ContentHandlerResult saveBookmarkContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, " Begin saveBookmarkContent method...");
        HttpServletRequest request = input.getHttpServletRequest();
        String clientID = input.getClientInstanceId();
        String documentID = input.getDocumentId();
        byte[] data = input.getBookmarkContent();
        Logger.getInstance().log(Logger.FINEST, "  saveBookmarkContents, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, "  saveBookmarkContents, DOC ID: " + documentID);
    	    	
    	if (data == null)
    	{
    		return null;
    	}

    	String fullFilePath = gFilePath + documentID + ".bookmarks.xml";
    	File file = new File(fullFilePath);
    	try
    	{
            ClientServerIO.saveFileBytes(data, file);
    	}
    	catch (Exception e)
    	{
    		Logger.getInstance().printStackTrace(e);
    	}

        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentID);
        Logger.getInstance().log(Logger.FINEST, " End saveBookmarkContents...");
    	return new ContentHandlerResult();
    }

    /** 
     * DELETE METHODS
     */
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#deleteAnnotation(ContentHandlerInput)
     */
    /* DELETE ANNOTATIONS
     * This method is called when an annotation layer is deleted in the applet and then a save
     * is invoked.
     * 
     * This example simply deletes an annotation file from the file system.
     */
    public ContentHandlerResult deleteAnnotation(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin deleteAnnotation method...");
    	String clientID = input.getClientInstanceId();
    	String documentID = input.getDocumentId();
    	String annotationKey = input.getAnnotationId();
    	String annotationFilename = documentID + "." + annotationKey + ".ann";
    	String fullFilePath = gFilePath + annotationFilename;
    	Logger.getInstance().log(Logger.FINEST, " deleteAnnotation, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " deleteAnnotation, DOC ID: " + documentID);
        Logger.getInstance().log(Logger.FINEST, " deleteAnnotation, Deleting annotation file: " + fullFilePath);
    	try
    	{
    		File file = new File(fullFilePath);
    		file.delete();
    	}
    	catch (Throwable e)
    	{
    		Logger.getInstance().log(Logger.INFO, "Error deleting layer " + annotationKey
    				+ ": " + e.getMessage());
    	}
    	return null;
    }
    
    public boolean accept(File dir, String filename)
    {
        if (filename.toLowerCase().endsWith(".tif"))
        {
            return true;
        }
        return false;
    }


    /**
     * PUBLISH METHODS
     */
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#sendDocumentContent(ContentHandlerInput)
     */
    /*
     * SEND DOCUMENT
     * This method is used to pass a document, with or without visible annotation layers burned 
     * in, to some external process as defined within this method. It is invoked when choosing 
     * Send Document or Send Document With Annotation from the File menu in the applet.
     */
    public ContentHandlerResult sendDocumentContent(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, " Begin sendDocumentContent method...");
    	HttpServletRequest request = input.getHttpServletRequest();
    	String clientID = input.getClientInstanceId();
    	String documentID = input.getDocumentId();
    	if (documentID.equals("<EMPTY DOCUMENT>"))
    	{
    		documentID = "NewDocument";
    	}
    	boolean mergeAnnotations = input.mergeAnnotations();
    	byte[] data = input.getDocumentContent();
    	Logger.getInstance().log(Logger.FINEST, "CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, "DOC KEY: " + documentID);
    	Logger.getInstance().log(Logger.FINEST, "MERGED ANN?: " + mergeAnnotations);

        File saveFile = new File(gFilePath + "sendDocument-" + documentID);
        ClientServerIO.saveFileBytes(data, saveFile);
        return ContentHandlerResult.VOID;
    }

    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSISaverInterface#publishDocument(ContentHandlerInput)
     */
    /*
     * PUBLISH DOCUMENT
     * This method is for future implementations, and not currently used.
     */
    public ContentHandlerResult publishDocument(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, "Begin publishDocument method...");
    	HttpServletRequest request = input.getHttpServletRequest();
    	String clientID = input.getClientInstanceId();
    	String documentID = input.getDocumentId();
    	if (documentID.equals("<EMPTY DOCUMENT>"))
    	{
    		documentID = "NewDocument";
    	}
    	byte[] data = input.getDocumentContent();
    	Logger.getInstance().log(Logger.FINEST, " publishDocument, CLIENT ID: " + clientID);
    	Logger.getInstance().log(Logger.FINEST, " publishDocument, DOC ID: " + documentID);

    	if (data == null)
    	{
    		return null;
    	}

    	File saveFile = new File(gFilePath + "published" + documentID);
        ClientServerIO.saveFileBytes(data, saveFile);
        Logger.getInstance().log(Logger.FINEST, "End publishDocument method...");
    	return ContentHandlerResult.VOID;
    }

    /**
     * PREFERENCES METHODS
     */
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getClientPreferencesXML(ContentHandlerInput)
     */
    /*
     * GET CLIENT PREFERENCES XML
     * This method is used 
     */
    public ContentHandlerResult getClientPreferencesXML(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
    	String clientID = input.getClientInstanceId();
    	Logger.getInstance().log(Logger.FINEST,	"Begin getClientPreferencesXML...");
        Logger.getInstance().log(Logger.FINEST,	" getClientPreferencesXML, CLIENT ID: " + clientID);
    	String preferencesFilename = clientID + ".preferences.xml";
        String fullFilePath = gPreferencesPath + preferencesFilename;
    	Logger.getInstance().log(Logger.FINEST, " getClientPreferencesXML, Retrieve pref file: " + fullFilePath);

    	try
    	{
    		File file = new File(fullFilePath);
    		String xmlString = new String(ClientServerIO.getFileBytes(file));
    		ContentHandlerResult result = new ContentHandlerResult();
    		result.put(ContentHandlerResult.KEY_CLIENT_PREFERENCES_XML, xmlString);
                Logger.getInstance().log(Logger.FINEST,	"End getClientPreferencesXML...");
    		return result;
    	}
        catch (Exception e)
    	{
    		return null;
    	}
    }
    
    /*
     * SAVE CLIENT PREFERENCES XML
     * This method is used 
     */
    public ContentHandlerResult saveClientPreferencesXML(ContentHandlerInput input)
    throws FlexSnapSIAPIException
    {
        Logger.getInstance().log(Logger.FINEST, "Begin saveClientPreferencesXML method...");
    	String clientID = input.getClientInstanceId();
    	String preferencesFilename = clientID + ".preferences.xml";
        String fullFilePath = gPreferencesPath + preferencesFilename;
        Logger.getInstance().log(Logger.FINEST, " Saving pref file: " + fullFilePath);

    	try
    	{
    	    File file = new File(fullFilePath);
    	    String xmlString = input.getClientPreferencesXML();
            ClientServerIO.saveFileBytes(xmlString.getBytes(), file);
            Logger.getInstance().log(Logger.FINEST, "End saveClientPreferencesXML method...");
    	    return ContentHandlerResult.VOID;
    	}
    	catch (Exception e)
    	{
    		return null;
    	}
    }
    
    
    /**
     * MISCELLANEOUS METHODS
     */
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#getAvailableDocumentIds(ContentHandlerInput)
     */
    /*
     * GET AVAILABLE DOCUMENT IDS
     * This method is used when the Multiple Document Mode specifies is "availableDocuments"
     */
    public ContentHandlerResult getAvailableDocumentIds(ContentHandlerInput input) 
    throws FlexSnapSIAPIException
    {
    	Logger.getInstance().log(Logger.FINEST, " Begin getAvailableDocumentIds method...");
        String clientID = input.getClientInstanceId();
        Logger.getInstance().log(Logger.FINEST, "  getAvailableDocumentIds, CLIENT ID: " + clientID);

        File imgDirectory = new File(gFilePath);
        String[] myArray = imgDirectory.list(this);
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.KEY_AVAILABLE_DOCUMENT_IDS, myArray);
        Logger.getInstance().log(Logger.FINEST, " End getAvailableDocumentIds method...");
        return result;
    }    
    
    /**
     * NOT CURRENTLY USED
     */
    /**
     * @see com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface#hasAnnotations(ContentHandlerInput)
     */
    /*
     * HAS ANNOTATIONS
     * This method is for future implementations, and not currently used.
     */
    public boolean hasAnnotations(ContentHandlerInput input)
    {
	return false;
    }
}