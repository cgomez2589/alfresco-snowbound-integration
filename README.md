This provide an integration between the Alfresco Share collaboration UI and the Snowbound VirtualViewer.
http://www.alfresco.com/
http://www.snowbound.com/

# alfresco-content-handler
Snowbound Content Handler java class that integration with Alfresco's Web Scripts (RESTful services)

VirtualViewer Reference: http://www.virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm

# snowbound-repo
Repository Web Scripts, content model, global properties

# snowbound-share
Share presentation layer that contains configuration for the Snowbound VirtualViewer iFrame


# Installation
* Checkout snowbound-repo, snowbound-share, and alfresco-content-handler from GitHub.
* Change directory to snowbound-repo and snowbound-share separately.
* Run the following Maven commands for each project:

```bash
$ mvn clean
```

```bash
$ mvn package
```
* Copy the snowbound-repo-*.amp to the <ALF_ROOT>/amps directory.
* Copy the snowbound-share-*.amp to the <ALF_ROOT>/amps_share directory.
* Make sure you Alfresco intance is stopped.
* Change directory to the <ALF_ROOT>/bin directory.
* Run the following command (note that I will remove the -force arg when I restructure the projects):

```bash
$ ./apply_amps -verbose -force
```
* Review the output and confirm that the Snowbound VirtualViewer Integration module was applied successfully.
* Download the VirtualViewer - HTML5 Java version from: http://register.snowbound.com/VirtualViewer_eval.html
* Copy the VirtualViewerJavaHTML5.war to the <ALF_ROOT>/tomcat/webapps directory.
* Change directory to the alfresco-content-handler directory.
* Confirm that the vv.deploy variable in the build.properties is pointing to the correct location in which the VirtualViewerJavaHTML5.war file resides.
* Run the following command:

```bash
$ ant install
```
* Start the tomcat instance
* Upload a PDF, navigate to the document-details view, and confirm that the Snowbound VirtualViewer renders the PDF as opposed to the Alfresco flash web-preview.


# MIME Types Currently Configured
* application/pdf
* image/tiff
* image/bmp
* image/gif
* image/jpeg
* application/msword
* application/vnd.openxmlformats-officedocument.wordprocessingml.document
* application/vnd.ms-excel
* application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
* application/vnd.ms-powerpoint
* application/vnd.openxmlformats-officedocument.presentationml.presentation
* application/vnd.ms-outlook

For the full list of supported file types, refer to Appendix D in the VirtualViewer documentation: http://www.virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm

To add additional MIME types refer to the web-preview.get.config.xml file in the snowbound-share project.


# TODO:
* Write test cases so that we're following proper test-driven development best practices.
* Refactor code so that we're throwing proper exceptions.
* Integrate VirtualViewer permissions with Alfresco permissions via Web Scripts.
* Create new doclib view to render all documents in a given folder as separate tabs (pagination necessary).
* Create a multi-select option to open multiple documents in a new Share UI page.

# Disclaimer
Use the alfresco-snowbound-integration project at your own risk. The alfresco-snowbound-integration is not officially supported by Alfresco and is an open source, community-driven project. 

# License
Copyright (C) 2013 Alfresco Software Limited

This file is part of an unsupported extension to Alfresco.

Alfresco Software Limited licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
