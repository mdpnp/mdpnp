package org.mdpnp.connect.docreceive;

import gov.hhs.fha.nhinc.adaptercomponentxdr.AdapterComponentXDRPortType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceContext;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@javax.jws.WebService(
        serviceName = "AdapterComponentXDR_Service",
        portName = "AdapterComponentXDR_Port",
        targetNamespace = "urn:gov:hhs:fha:nhinc:adaptercomponentxdr",
//        wsdlLocation = "classpath:/wsdl/AdapterComponentXDR.wsdl",
        endpointInterface = "gov.hhs.fha.nhinc.adaptercomponentxdr.AdapterComponentXDRPortType")
public class AdapterComponentXDRPortTypeImpl implements AdapterComponentXDRPortType {
	@Resource
	private WebServiceContext context;
	private Logger log = null;
	private static final String DOCSUBMISSION_FILE_DIRECTORY_DEFAULT = "DocSubmission"+File.separator;
	// private static final String PROPERTY_FILE = "adapter";
	// private static final String DOCSUBMISSION_FILE_PROPERTY =
	// "docsubmission.property.directory";
	// private static final String VALIDATION_SOURCE_INCLUDE_PROPERTY =
	// "validation.property.source.include";
	private static final String EBXML_RESPONSE_DOCID_IDENTIFICATION_SCHEME = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";

	// private static final String EBXML_RESPONSE_DOCID_NAME =
	// "XDSDocumentEntry.uniqueId";

	// private static DomainParticipant participant = null;
	// private static Publisher publisher = null;
	// private static Topic topic = null;
	// private static MDCF_HandOffDataWriter writer = null;

	public AdapterComponentXDRPortTypeImpl() {
		log = createLogger();
	}

	protected Logger createLogger() {
		return ((log != null) ? log : LoggerFactory
				.getLogger(AdapterComponentXDRPortTypeImpl.class));

	}

	private String generateFileName(
			AdapterProvideAndRegisterDocumentSetRequestType body,
			String sInternalDocId) {
		String sDocumentId = "";
		String sFileName = "";

		if ((body != null)
				&& (body.getProvideAndRegisterDocumentSetRequest() != null)
				&& (body.getProvideAndRegisterDocumentSetRequest()
						.getSubmitObjectsRequest() != null)
				&& (body.getProvideAndRegisterDocumentSetRequest()
						.getSubmitObjectsRequest().getRegistryObjectList() != null)
				&& (body.getProvideAndRegisterDocumentSetRequest()
						.getSubmitObjectsRequest().getRegistryObjectList()
						.getIdentifiable().size() > 0)) {
			List<JAXBElement<? extends IdentifiableType>> olRegObjList = body
					.getProvideAndRegisterDocumentSetRequest()
					.getSubmitObjectsRequest().getRegistryObjectList()
					.getIdentifiable();

			for (JAXBElement<? extends IdentifiableType> oIdentType : olRegObjList) {
				if ((oIdentType.getValue() != null)
						&& (oIdentType.getValue() instanceof ExtrinsicObjectType)) {
					ExtrinsicObjectType oObjType = (ExtrinsicObjectType) oIdentType
							.getValue();

					// Look for the external identifier for Document ID.
					// ---------------------------------------------------
					if ((oObjType.getId() != null)
							&& (oObjType.getId().equals(sInternalDocId))
							&& (oObjType.getExternalIdentifier() != null)
							&& (oObjType.getExternalIdentifier().size() > 0)) {
						for (ExternalIdentifierType oExtIdent : oObjType
								.getExternalIdentifier()) {
							if ((oExtIdent.getIdentificationScheme() != null)
									&& (oExtIdent.getIdentificationScheme()
											.equals(EBXML_RESPONSE_DOCID_IDENTIFICATION_SCHEME))
									&& (oExtIdent.getValue() != null)) {
								sDocumentId = oExtIdent.getValue();
							}
						}
					}
				}
			}
		}

		// Generate the file name from the document ID and the current timestamp
		// --------------------------------------------------------------------------
		Calendar oCal = Calendar.getInstance();
		Date oDate = oCal.getTime();
		SimpleDateFormat oFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		if (sDocumentId.length() == 0) {
			sDocumentId = "UknownDocId_" + sInternalDocId;
		}
		sFileName = sDocumentId + "_" + oFormat.format(oDate) + ".txt";

		return sFileName;
	}

	private List<Document> extractDocuments(
			AdapterProvideAndRegisterDocumentSetRequestType body) {
		List<Document> olDocs = new ArrayList<Document>();

		if ((body != null)
				&& (body.getProvideAndRegisterDocumentSetRequest() != null)
				&& (body.getProvideAndRegisterDocumentSetRequest()
						.getDocument() != null)
				&& (body.getProvideAndRegisterDocumentSetRequest()
						.getDocument().size() >= 1)) {
			log.debug("DocBox: extracting document");
			olDocs = body.getProvideAndRegisterDocumentSetRequest()
					.getDocument();
		}

		return olDocs;
	}

	private String extractDocumentAsString(Document oDoc)
			throws UnsupportedFlavorException, IOException {
		// TODO use default encoding like this?
//		return new String(oDoc.getValue());
		DataHandler dataHandler = oDoc.getValue();

		if (dataHandler.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return (String) dataHandler
					.getTransferData(DataFlavor.stringFlavor);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			dataHandler.writeTo(baos);
			return new String(baos.toByteArray());
		}

	}

	private String createDocumentDirectory() {
		String sDirectory = "";

		// String sValue = PropertyAccessor.getProperty(PROPERTY_FILE,
		// DOCSUBMISSION_FILE_PROPERTY);
		// if ((sValue != null) && (sValue.trim().length() > 0))
		// {
		// sDirectory = sValue.trim();
		// }
		// else
		// {
		sDirectory = DOCSUBMISSION_FILE_DIRECTORY_DEFAULT;
		// }

//		String sFileSeparator = System.getProperty("file.separator");
//		if (!sDirectory.endsWith(sFileSeparator)) {
//			sDirectory = sDirectory + sFileSeparator;
//		}

		File fDirectory = new File(sDirectory);
		if (!fDirectory.exists()) {
			fDirectory.mkdirs();
		} else if (!fDirectory.isDirectory()) {
			throw new IllegalStateException("Directory name: '" + sDirectory
					+ "' already exists but is a file and not a directory.  ");
		}

		return sDirectory;
	}

	private PrintWriter getPrintWriter(String sFileName)
			throws FileNotFoundException {
		PrintWriter pwOutput = null;
		String sDirectory = "";

		sDirectory = createDocumentDirectory();

		pwOutput = new PrintWriter(sDirectory + sFileName);

		return pwOutput;
	}

	private void outputDocument(String sFileName, String sInternalDocId,
			String sDoc) throws FileNotFoundException {
		PrintWriter pwOutput = null;

		try {
			// pwOutput = getPrintWriter(sFileName);
			pwOutput = getPrintWriter("mdcf_document.xml");

			if (log.isDebugEnabled()) {
				log.debug("XML Document: ");
				log.debug(sDoc);
			}

			// pwOutput.println("XML Document: ");
			pwOutput.println(sDoc);

			// pwOutput.println("Document[" + sInternalDocId + "]");
			// pwOutput.println("----------------------------------------------");
			pwOutput.close();
			pwOutput = null;
		} finally {
			// Make sure that our PrintWriter is closed.
			// -------------------------------------------
			if (pwOutput != null) {
				pwOutput.close();
				pwOutput = null;
			}
		}
	}


	// private static final String XDS_RETRIEVE_RESPONSE_STATUS_FAILURE =
	// "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
	private static final String XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";

	private static final RegistryResponseType createPositiveAck() {
		RegistryResponseType result = new RegistryResponseType();

		result.setStatus(XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS);

		return result;
	}
	public static void main(String[] args) {
		Endpoint.publish("http://localhost:8093/foo", new AdapterComponentXDRPortTypeImpl());
	}

	@javax.xml.ws.Action(input="urn:gov:hhs:fha:nhinc:adaptercomponentxdr:ProvideAndRegisterDocumentSet-b",
						 output="urn:gov:hhs:fha:nhinc:adaptercomponentxdr:ProvideAndRegisterDocumentSet-bResponse")
//		  @javax.jws.WebMethod(operationName="ProvideAndRegisterDocumentSetb")
	public oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType provideAndRegisterDocumentSetb(gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType body) { 

			try {
				return _provideAndRegisterDocumentSetb(body);
			} catch (UnsupportedFlavorException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

	}
	
	private RegistryResponseType _provideAndRegisterDocumentSetb(
			AdapterProvideAndRegisterDocumentSetRequestType body) throws UnsupportedFlavorException, IOException {
		RegistryResponseType oResponse = new RegistryResponseType();

		log.debug("Entered DocBox doc submission adapter");

		// Get the list of documents in the XDR message.
		// -------------------------------------------------
		List<Document> olDocs = extractDocuments(body);

		log.debug("DocBox: documents extracted");

		// Loop through each document and process it.
		// --------------------------------------------
		for (Document oDoc : olDocs) {
			if (oDoc.getValue() != null) {
				// Generate the name of the file based on the Document ID
				// of the document. If the document ID does not exist, we
				// will use a default file name.
				// ---------------------------------------------------------
				String sFileName = "";
				String sInternalDocId = "";
				if (oDoc.getId() != null) {
					sFileName = generateFileName(body, oDoc.getId());
					sInternalDocId = oDoc.getId();
					log.debug("DocBox: obtained document ID" + sFileName);
				} else {
					sFileName = "UNKNOWN_DOC_ID.txt";
					sInternalDocId = "UNKNOWN";
				}

				log.debug("DocBox: extracting document as string");
				String sDoc = extractDocumentAsString(oDoc);
				log.debug(sDoc);

				// write the document to a file
				outputDocument(sFileName, sInternalDocId, sDoc);
				log.debug("DocBox: output xml document complete");
			} else {
				String sErrorMessage = "The document was empty.  Nothing to process...";
				log.error(sErrorMessage);
			}
		}

		// Return "success"
		// ------------------
		oResponse = createPositiveAck();
		log.debug("DocBox: doc submission adapter returning response");
		return oResponse;
	}
}
