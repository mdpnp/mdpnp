package org.mdpnp.connect.docreceive;

import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * @author Jeff Plourde
 *
 */
public class Util {
	private static final String EBXML_RESPONSE_DOCID_IDENTIFICATION_SCHEME = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
	private static final String XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
	private static final String UNKNOWN_DOC_ID = "UNKNOWN_DOC_ID";
	
	private Util() {
		
	}
	
	public static class Document {
		private final File file;
		private final ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document document;
		
		public Document(File file, ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document document) {
			this.file = file;
			this.document = document;
		}
		public void write() throws IOException {
			OutputStream os = null;
			try {
				if(null != document && null != document.getValue()) {
					os = new FileOutputStream(file);
					document.getValue().writeTo(os);
				}
			} finally {
				if (os != null) {
					os.close();
				}
			}
		}
	}
	
	public static String createDocumentDirectory(String docSubmissionPath) {
		File f = new File(docSubmissionPath);
		if (!f.exists()) {
			f.mkdirs();
		} else if (!f.isDirectory()) {
			throw new IllegalStateException("Directory name: '" + docSubmissionPath
					+ "' already exists but is a file and not a directory.  ");
		}

		return docSubmissionPath;
	}
	public static String generateFileName(AdapterProvideAndRegisterDocumentSetRequestType body, String internalId) {
		String externalId = findExternalId(body, internalId);

		// Generate the file name from the document ID and the current timestamp
		if (null == externalId) {
			if(null != internalId) {
				externalId = internalId;
			} else {
				externalId = UNKNOWN_DOC_ID;
			}
		}
		return externalId + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".txt";
	}
	
	public static RegistryResponseType createSuccessResponse() {
		RegistryResponseType response = new RegistryResponseType();
		response.setStatus(XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS);
		return response;
	}
	
	public static String findExternalId(AdapterProvideAndRegisterDocumentSetRequestType body, String internalId) {
		if(null == internalId) {
			return null;
		}
		String externalId = null;
		if (body != null 
			&& body.getProvideAndRegisterDocumentSetRequest() != null
			&& body.getProvideAndRegisterDocumentSetRequest().getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable().size() > 0) {
			List<JAXBElement<? extends IdentifiableType>> olRegObjList = body.getProvideAndRegisterDocumentSetRequest()
					.getSubmitObjectsRequest().getRegistryObjectList().getIdentifiable();
		

			for (JAXBElement<? extends IdentifiableType> oIdentType : olRegObjList) {
				if ((oIdentType.getValue() != null) && (oIdentType.getValue() instanceof ExtrinsicObjectType)) {
					ExtrinsicObjectType oObjType = (ExtrinsicObjectType) oIdentType.getValue();

					// Look for the external identifier for Document ID.
					if ((oObjType.getId() != null) && (oObjType.getId().equals(internalId))
							&& (oObjType.getExternalIdentifier() != null)
							&& (oObjType.getExternalIdentifier().size() > 0)) {
						for (ExternalIdentifierType oExtIdent : oObjType.getExternalIdentifier()) {
							if ((oExtIdent.getIdentificationScheme() != null)
									&& (oExtIdent.getIdentificationScheme()
											.equals(EBXML_RESPONSE_DOCID_IDENTIFICATION_SCHEME))
									&& (oExtIdent.getValue() != null)) {
								externalId = oExtIdent.getValue();
							}
						}
					}
				}
			}
		}
		return externalId;
	}
	
	public static List<Document> extractDocuments(AdapterProvideAndRegisterDocumentSetRequestType body, String docSubmissionPath) {
		File docSubmissionFile = new File(docSubmissionPath);
		List<Document> docs = new ArrayList<Document>();
		if (body != null 
			&& body.getProvideAndRegisterDocumentSetRequest() != null
			&& body.getProvideAndRegisterDocumentSetRequest().getDocument() != null) {
			for(ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document d : body.getProvideAndRegisterDocumentSetRequest().getDocument()) {
				if(d != null) {
					docs.add(new Document(new File(docSubmissionFile, generateFileName(body, d.getId())), d));
				}
			}
		}
		return docs;
	}
	
}
