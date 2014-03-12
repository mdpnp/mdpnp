package org.mdpnp.connect.docreceive;

import gov.hhs.fha.nhinc.adaptercomponentxdr.AdapterComponentXDRPortType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;


/**
 * Example of creating an alternative implementation of the CONNECT ComponentXDR adapter.
 * 
 * This implementation writes received documents to the file system.
 * 
 * @author afiore
 * @author jplourde
 *
 */
@javax.jws.WebService(serviceName = "AdapterComponentXDR_Service", portName = "AdapterComponentXDR_Port", targetNamespace = "urn:gov:hhs:fha:nhinc:adaptercomponentxdr", endpointInterface = "gov.hhs.fha.nhinc.adaptercomponentxdr.AdapterComponentXDRPortType")
/**
 * @author Jeff Plourde
 *
 */
public class AdapterComponentXDRPortTypeImpl implements AdapterComponentXDRPortType {
	@Resource
	private WebServiceContext context;
	private static final String DOCSUBMISSION_FILE_DIRECTORY_DEFAULT = "DocSubmission" + File.separator;
	private String docSubmissionPath = DOCSUBMISSION_FILE_DIRECTORY_DEFAULT;

	public AdapterComponentXDRPortTypeImpl() {
	}

	@javax.xml.ws.Action(input = "urn:gov:hhs:fha:nhinc:adaptercomponentxdr:ProvideAndRegisterDocumentSet-b", output = "urn:gov:hhs:fha:nhinc:adaptercomponentxdr:ProvideAndRegisterDocumentSet-bResponse")
	public oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType provideAndRegisterDocumentSetb(
			gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType body) {

		try {
			List<Util.Document> docs = Util.extractDocuments(body, docSubmissionPath);
			for(Util.Document doc : docs) {
				doc.write();
			}
			return Util.createSuccessResponse();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
