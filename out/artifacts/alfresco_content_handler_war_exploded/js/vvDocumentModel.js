function DocumentModel(A){if(!A){return null}this.model=A;this.selection=[];this.selection.length=this.getDocumentLength();this.setPagesModifiedSinceInit(false);this.lastSelectedPageNumber=0}DocumentModel.prototype.addPageToSelection=function(A){this.lastSelectedPageNumber=A;this.selection[A]=true;var B=$("#vvPageThumbs td div:eq("+A+")");B.addClass("selectedThumbBox");B.find("img").addClass("selectedThumb")};DocumentModel.prototype.removePageFromSelection=function(A){this.selection[A]=false;var B=$("#vvPageThumbs td div:eq("+A+")");B.removeClass("selectedThumbBox");B.find("img").removeClass("selectedThumb")};DocumentModel.prototype.togglePageSelection=function(A){var B=$("#vvPageThumbs td div:eq("+A+")");if(this.selection[A]===true){this.selection[A]=undefined;B.removeClass("selectedThumbBox");B.find("img").removeClass("selectedThumb")}else{this.selection[A]=true;B.addClass("selectedThumbBox");B.find("img").addClass("selectedThumb")}};DocumentModel.prototype.isPageSelected=function(A){if(this.selection[A]===true){return true}else{return false}};DocumentModel.prototype.selectRange=function(C){var E=this.lastSelectedPageNumber;var B=C+1;if(B<=E){E=C;B=this.lastSelectedPageNumber}for(var A=E;A<B;A+=1){this.selection[A]=true;var D=$("#vvPageThumbs td div:eq("+A+")");D.addClass("selectedThumbBox");D.find("img").addClass("selectedThumb")}};DocumentModel.prototype.selectAll=function(){this.lastSelectedPageNumber=0;this.selectRange(this.selection.length-1)};DocumentModel.prototype.clearPageSelection=function(){this.selection=[];this.selection.length=this.model.pageData.length;$(".selectedThumbBox").removeClass("selectedThumbBox");$(".selectedThumb").removeClass("selectedThumb")};DocumentModel.prototype.cleanAfterSave=function(C){var B=this.model.pageData;if(C){this.model.documentId=C}this.setPagesModifiedSinceInit(false);for(var A=0;A<B.length;A+=1){var D=B[A];D.pageIndex=A;if(C){var E=this.model.layerManager.layers;D.documentId=C;if(D.annotationHash){$.each(D.annotationHash,function(G,F){F.documentId=C})}}D.rotateAngle=0;D.invertImage=false}};DocumentModel.prototype.cutSelection=function(){var B=[];var C=[];this.setPagesModifiedSinceInit(true);for(var A=0;A<this.model.pageData.length;A+=1){if((this.selection[A]===false)||(this.selection[A]===undefined)){B.push(this.model.pageData[A])}else{C.push(this.model.pageData[A])}}this.model.pageData=B;this.clearPageSelection();return C};DocumentModel.prototype.copySelection=function(){var C=[];for(var A=0;A<this.selection.length;A+=1){if(this.selection[A]===true){var D=this.model.pageData[A];var B=deepObjCopy(D);C.push(B)}}return C};DocumentModel.prototype.paste=function(B,D){var C=0;this.setPagesModifiedSinceInit(true);for(var A=0;A<B.length;A+=1){this.model.pageData.splice(D+C,0,B[A]);C+=1}this.clearPageSelection()};DocumentModel.prototype.getPagesModifiedSinceInit=function(){return this.model.pagesModifiedSinceInit};DocumentModel.prototype.setPagesModifiedSinceInit=function(A){this.model.pagesModifiedSinceInit=A};DocumentModel.prototype.getSelectedPageNumbers=function(){var B=[];for(var A=0;A<this.selection.length;A+=1){if(this.selection[A]===true){B.push(A)}}return B};DocumentModel.prototype.renameLayer=function(G,D){var C=this.model.layerManager.layers;var E=null;for(var I=0;I<C.length;I+=1){E=C[I];if(decodeURIComponent(E.annotationId)===G){E.annotationId=D;break}}var A=this.model.pageData;for(var H=0;H<A.length;H+=1){var F=A[H];var B=F.annotationHash;E=B[G];if(E){E.layerAnnotationId=D;B[D]=E;B[G]=undefined}}};DocumentModel.prototype.getDocumentLength=function(){return this.model.pageData.length};DocumentModel.prototype.markLayerDirty=function(B){var C=this.model.layerManager.layers;for(var A=0;A<C.length;A+=1){if(B.layerName===decodeURIComponent(C[A].annotationId)){C[A].dirty=true;break}}};DocumentModel.prototype.isLayerDirty=function(A){this.isLayerDirtyByName(A.layerName)};DocumentModel.prototype.isLayerDirtyByName=function(B){var C=this.model.layerManager.layers;for(var A=0;A<C.length;A+=1){if(B===decodeURIComponent(C[A].annotationId)){if(C[A].dirty){return true}else{return false}}}};function AnnotationLayerManager(A){var B=[];A.find("AnnotationLayerInfo").each(function(){B.push(new AnnotationLayerInfo(this))});this.layers=B}AnnotationLayerManager.prototype.toXML=function(){var B="";B+="<AnnotationLayerManager>";B+="<layers>";for(var A=0;A<this.layers.length;A+=1){B+=this.layers[A].toXML()}B+="</layers>";B+="</AnnotationLayerManager>";return B};function AnnotationLayerInfo(A){this.annotationId=unescape(decodeURIComponent($(A).find("annotationId").first().text()));this.newLayer=fixBooleanString($(A).find("new").first().text());this.visible=fixBooleanString($(A).find("visible").first().text());this.owner=$(A).find("owner").first().text();this.isDeletable=fixBooleanString($(A).find("isDeletable").first().text());this.isRedaction=fixBooleanString($(A).find("isRedaction").first().text());this.permissionLevel=parseInt($(A).find("permissionLevel").first().text(),10)}AnnotationLayerInfo.prototype.toXML=function(){var A="";A+="<AnnotationLayerInfo>";A+="<annotationId>"+xmlEncodeEntities(decodeURIComponent(this.annotationId))+"</annotationId>";A+="<new>"+this.newLayer+"</new>";A+="<visible>"+this.visible+"</visible>";A+="<owner>"+xmlEncodeEntities(this.owner)+"</owner>";A+="<isDeletable>"+this.isDeletable+"</isDeletable>";A+="<isRedaction>"+this.isRedaction+"</isRedaction>";A+="<permissionLevel>"+this.permissionLevel+"</permissionLevel>";A+="</AnnotationLayerInfo>";return A};function Pages(B){var A=[];B.find("PageData").each(function(){A.push(new PageData(this))});this.pages=A}Pages.prototype.toXML=function(){var B="";B+="<pages>";for(var A=0;A<this.pages.length;A+=1){B+=this.pages[A].toXML()}B+="</pages>";return B};function PageData(A){this.documentId=$(A).find("documentId").first().text();this.rotateAngle=parseInt($(A).find("rotateAngle").first().text(),10)/100;this.invertImage=$(A).find("invertImage").first().text();if((this.invertImage==="true")||(this.invertImage==="True")){this.invertImage=true}else{this.invertImage=false}this.pageIndex=parseInt($(A).find("pageIndex").first().text(),10);this.annotationHash=new AnnotationHash($(A).find("AnnotationHash").first())}PageData.prototype.toXML=function(){var A="";A+='<PageData class="ServerReferencePageData">';A+="<rotateAngle>"+this.rotateAngle*100+"</rotateAngle>";A+="<invertImage>"+this.invertImage+"</invertImage>";A+=this.annotationHash.toXML();A+="<documentId>"+xmlEncodeEntities(decodeURIComponent(this.documentId))+"</documentId>";A+="<pageIndex>"+this.pageIndex+"</pageIndex>";A+="</PageData>";return A};function AnnotationHash(A){var B={};var C=[];$(A).find("element").each(function(){var D=$(this).find("key").first().text();var E=new AnnotationPageLayer($(this).find("AnnotationPageLayer").first());B[D]=E;C.push(D)});this.layers=B;this.layerNames=C}AnnotationHash.prototype.toXML=function(){var C="";C+="<AnnotationHash>";for(var A=0;A<this.layerNames.length;A+=1){var B=this.layers[this.layerNames[A]];C+="<element>";C+="<key>"+xmlEncodeEntities(decodeURIComponent(this.layerNames[A]))+"</key>";C+="<value>";C+=B.toXML();C+="</value>";C+="</element>"}C+="</AnnotationHash>";return C};function AnnotationPageLayer(A){this.documentId=$(A).find("documentId").first().text();this.layerAnnotationId=decodeURIComponent($(A).find("layerAnnotationId").first().text());this.pageIndex=parseInt($(A).find("pageIndex").first().text(),10);this.annExists=fixBooleanString($(A).find("annExists").first().text());this.rotateAngle=parseInt($(A).find("rotateAngle").first().text(),10)/100}AnnotationPageLayer.prototype.toXML=function(){var A="";A+="<AnnotationPageLayer>";A+="<documentId>"+xmlEncodeEntities(decodeURIComponent(this.documentId))+"</documentId>";A+="<layerAnnotationId>"+xmlEncodeEntities(decodeURIComponent(this.layerAnnotationId))+"</layerAnnotationId>";A+="<layersToMerge/>";A+="<pageIndex>"+this.pageIndex+"</pageIndex>";A+="<annExists>"+this.annExists+"</annExists>";A+="<rotateAngle>"+this.rotateAngle*100+"</rotateAngle>";A+="</AnnotationPageLayer>";return A};