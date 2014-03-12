package org.mdpnp.qmdi;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.UrlInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayProvideAndRegisterDocumentSetRequestType;
import gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRPortType;
import gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRService;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceClient;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 * @author Jeff Plourde
 *
 */
public class DocumentSubmit {

	private static final LocalizedStringType[] local(String... str) {

		LocalizedStringType[] localString = new LocalizedStringType[str.length];
		for (int i = 0; i < str.length; i++) {
			localString[i] = new LocalizedStringType();
			localString[i].setValue(str[i]);
		}
		return localString;
	}

	private static final void addSlot(List<SlotType1> slots, String name,
			String... values) {
		SlotType1 slot = new SlotType1();
		slot.setName(name);
		ValueListType valueList = new ValueListType();
		slot.setValueList(valueList);
		slot.getValueList().getValue().addAll(Arrays.asList(values));
		slots.add(slot);
	}

	private static final void addSlot(ExtrinsicObjectType eot, String name,
			String... values) {
		addSlot(eot.getSlot(), name, values);
	}

	private static final void addString(InternationalStringType ist,
			String... values) {
		LocalizedStringType[] locals = local(values);
		ist.getLocalizedString().addAll(Arrays.asList(locals));
	}

	private static final InternationalStringType addString(String... values) {
		InternationalStringType ist = new InternationalStringType();
		addString(ist, values);
		return ist;
	}

    public static String submitDocument(String urlText, String fileId, String mimeType, String homeCommunityId, byte[] fileContent) {

		try {
		    java.net.URL url = null == urlText ? null : new java.net.URL(urlText);
			AssertionType assertion = AssertionCreator.createAssertion();
			NhinTargetCommunitiesType targets = new NhinTargetCommunitiesType();
			UrlInfoType urlInfo = new UrlInfoType();

			ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

			oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory rim_of = new oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory();
			oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory lcm_of = new oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory();

			SubmitObjectsRequest sor = lcm_of.createSubmitObjectsRequest();

			sor.setComment("Submit Documents Conformance Test Case SDI-1");
			sor.setId("123");

			RegistryObjectListType registryObjectList = new RegistryObjectListType();
			sor.setRegistryObjectList(registryObjectList);

			ExtrinsicObjectType extrinsicObject = new ExtrinsicObjectType();

			extrinsicObject.setId(fileId);
			extrinsicObject.setMimeType(mimeType);
			extrinsicObject
					.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");

			addSlot(extrinsicObject, "creationTime", "20051224");
			addSlot(extrinsicObject, "languageCode", "en-us");
			addSlot(extrinsicObject, "serviceStartTime", "200412230800");
			addSlot(extrinsicObject, "serviceStopTime", "200412230801");
			addSlot(extrinsicObject, "sourcePatientId",
					"ST-1000^^^&amp;1.3.6.1.4.1.21367.2003.3.9&amp;ISO");
			addSlot(extrinsicObject, "sourcePatientInfo",
					"PID-3|ST-1000^^^&amp;1.3.6.1.4.1.21367.2003.3.9&amp;ISO",
					"PID-5|Doe^John^^^", "PID-7|19560527", "PID-8|M",
					"PID-11|100 Main St^^Metropolis^Il^44130^USA");
			extrinsicObject.setName(addString("Physical"));

			{
				ClassificationType cl01 = new ClassificationType();
				cl01.setId("cl01");
				cl01.setClassificationScheme("urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d");
				cl01.setClassifiedObject(fileId);
				addSlot(cl01.getSlot(), "authorPerson", "Gerald Smitty");
				addSlot(cl01.getSlot(), "authorInstitution",
						"Cleveland Clinic", "Parma Community");
				addSlot(cl01.getSlot(), "authorRole", "Attending");
				addSlot(cl01.getSlot(), "authorSpecialty", "Orthopedic");
				extrinsicObject.getClassification().add(cl01);
			}

			{

				ClassificationType cl02 = new ClassificationType();
				cl02.setId("cl02");
				cl02.setClassificationScheme("urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a");
				cl02.setClassifiedObject(fileId);
				cl02.setNodeRepresentation("History and Physical");
				addSlot(cl02.getSlot(), "codingScheme",
						"Connect-a-thon classCodes");
				cl02.setName(addString("History and Physical"));
				extrinsicObject.getClassification().add(cl02);
			}

			{
				ClassificationType cl03 = new ClassificationType();
				cl03.setClassificationScheme("urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f");
				cl03.setClassifiedObject(fileId);
				cl03.setNodeRepresentation("1.3.6.1.4.1.21367.2006.7.101");
				cl03.setId("cl03");
				addSlot(cl03.getSlot(), "codingScheme");
				addSlot(cl03.getSlot(), "Connect-a-thon confidentialityCodes");
				cl03.setName(addString("Clinical-Staff"));
				extrinsicObject.getClassification().add(cl03);
			}
			{
				ClassificationType cl04 = new ClassificationType();
				cl04.setClassificationScheme("urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d");
				cl04.setClassifiedObject(fileId);
				cl04.setNodeRepresentation("CDAR2/IHE 1.0");
				cl04.setId("cl04");
				addSlot(cl04.getSlot(), "codingScheme");
				addSlot(cl04.getSlot(), "Connect-a-thon formatCodes");
				cl04.setName(addString("CDAR2/IHE 1.0"));
				extrinsicObject.getClassification().add(cl04);
			}

			{
				ClassificationType cl05 = new ClassificationType();
				cl05.setClassificationScheme("urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1");
				cl05.setClassifiedObject(fileId);
				cl05.setNodeRepresentation("Outpatient");
				cl05.setId("cl05");
				addSlot(cl05.getSlot(), "codingScheme");
				addSlot(cl05.getSlot(),
						"Connect-a-thon healthcareFacilityTypeCodes");
				cl05.setName(addString("Outpatient"));
				extrinsicObject.getClassification().add(cl05);
			}
			{
				ClassificationType cl06 = new ClassificationType();
				cl06.setClassificationScheme("urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead");
				cl06.setClassifiedObject(fileId);
				cl06.setNodeRepresentation("General Medicine");
				cl06.setId("cl06");
				addSlot(cl06.getSlot(), "codingScheme");
				addSlot(cl06.getSlot(), "Connect-a-thon practiceSettingCodes");
				cl06.setName(addString("General Medicine"));
				extrinsicObject.getClassification().add(cl06);
			}
			{
				ClassificationType cl07 = new ClassificationType();
				cl07.setClassificationScheme("urn:uuid:f0306f51-975f-434e-a61c-c59651d33983");
				cl07.setClassifiedObject(fileId);
				cl07.setNodeRepresentation("34108-1");
				cl07.setId("cl07");
				addSlot(cl07.getSlot(), "codingScheme", "LOINC");
				cl07.setName(addString("Outpatient Evaluation And Management"));
				extrinsicObject.getClassification().add(cl07);
			}

			{
				ExternalIdentifierType ei01 = new ExternalIdentifierType();
				ei01.setId("ei01");
				ei01.setRegistryObject(fileId);
				ei01.setIdentificationScheme("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427");
				ei01.setValue("SELF-5^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO");
				ei01.setName(addString("XDSDocumentEntry.patientId"));
				extrinsicObject.getExternalIdentifier().add(ei01);
			}
			{
				ExternalIdentifierType ei02 = new ExternalIdentifierType();
				ei02.setId("ei02");
				ei02.setRegistryObject(fileId);
				ei02.setIdentificationScheme("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab");
				ei02.setValue("1.3.6.1.4.1.21367.2005.3.9999.32");
				ei02.setName(addString("XDSDocumentEntry.uniqueId"));
				extrinsicObject.getExternalIdentifier().add(ei02);
			}

			RegistryPackageType registryPackage = rim_of
					.createRegistryPackageType();
			registryPackage.setId("SubmissionSet01");
			addSlot(registryPackage.getSlot(), "submissionTime",
					"20041225235050");
			registryPackage.setName(addString("Physical"));
			registryPackage.setDescription(addString("Annual physical"));

			{
				ClassificationType cl08 = new ClassificationType();
				cl08.setClassificationScheme("urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d");
				cl08.setClassifiedObject("SubmissionSet01");
				cl08.setId("cl08");
				addSlot(cl08.getSlot(), "authorPerson", "Sherry Dopplemeyer");
				addSlot(cl08.getSlot(), "authorInstitution",
						"Cleveland Clinic", "Berea Community");
				addSlot(cl08.getSlot(), "authorRole", "Purn4ary Surgon");
				addSlot(cl08.getSlot(), "authorSpecialty", "Orthopedic");
				registryPackage.getClassification().add(cl08);
			}

			{
				ClassificationType cl09 = new ClassificationType();
				cl09.setClassificationScheme("urn:uuid:aa543740-bdda-424e-8c96-df4873be8500");
				cl09.setClassifiedObject("SubmissionSet01");
				cl09.setNodeRepresentation("History and Physical");
				cl09.setId("cl09");
				addSlot(cl09.getSlot(), "codingScheme",
						"Connect-a-thon contentTypeCodes");
				cl09.setName(addString("History and Physical"));
				registryPackage.getClassification().add(cl09);
			}

			{
				ExternalIdentifierType ei03 = new ExternalIdentifierType();
				ei03.setId("ei03");
				ei03.setRegistryObject("SubmissionSet01");
				ei03.setIdentificationScheme("urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8");
				ei03.setValue("1.3.6.1.4.1.21367.2005.3.9999.33");
				ei03.setName(addString("XDSSubmissionSet.uniqueId"));
				registryPackage.getExternalIdentifier().add(ei03);
			}

			{
				ExternalIdentifierType ei04 = new ExternalIdentifierType();
				ei04.setId("ei04");
				ei04.setRegistryObject("SubmissionSet01");
				ei04.setIdentificationScheme("urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832");
				ei04.setValue("3670984664");
				ei04.setName(addString("XDSSubmissionSet.sourceId"));
				registryPackage.getExternalIdentifier().add(ei04);
			}

			{
				ExternalIdentifierType ei05 = new ExternalIdentifierType();
				ei05.setId("ei05");
				ei05.setRegistryObject("SubmissionSet01");
				ei05.setIdentificationScheme("urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446");
				ei05.setValue("${#Project#PatientID}^^^&amp;${#Project#LocalAA}&amp;ISO");
				ei05.setName(addString("XDSSubmissionSet.patientId"));
				registryPackage.getExternalIdentifier().add(ei05);
			}

			ClassificationType cl10 = new ClassificationType();
			cl10.setId("cl10");
			cl10.setClassificationNode("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");

			AssociationType1 as01 = new AssociationType1();
			as01.setId("as01");
			as01.setAssociationType("HasMember");
			as01.setSourceObject("SubmissionSet01");
			as01.setTargetObject(fileId);
			addSlot(as01.getSlot(), "SubmissionSetStatus", "Original");

			registryObjectList.getIdentifiable().add(
					rim_of.createExtrinsicObject(extrinsicObject));
			registryObjectList.getIdentifiable().add(
					rim_of.createRegistryPackage(registryPackage));
			registryObjectList.getIdentifiable().add(
					rim_of.createClassification(cl10));
			registryObjectList.getIdentifiable().add(
					rim_of.createAssociation(as01));

			request.setSubmitObjectsRequest(sor);
			ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document document01 = new ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document();
			document01.setId(fileId);

			document01.setValue(new DataHandler(fileContent, "application/octet-stream")); // DOCUMENT CONTENTS HERE!

			request.getDocument().add(document01);

			// initialize nhin target community
			HomeCommunityType homeCommunity = new HomeCommunityType();
			homeCommunity.setHomeCommunityId(homeCommunityId);
			NhinTargetCommunityType target = new NhinTargetCommunityType();
			target.setHomeCommunity(homeCommunity);
			targets.getNhinTargetCommunity().add(target);

			RegistryResponseType response = provideAndRegisterDocumentSetB(
										       url, request, assertion, targets, urlInfo);

			return response.getStatus();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {

		}

	}

	public static RegistryResponseType provideAndRegisterDocumentSetB(
									  java.net.URL url,
			ProvideAndRegisterDocumentSetRequestType message,
			AssertionType assertion, NhinTargetCommunitiesType targets,
			UrlInfoType urlInfo) {
		WebServiceClient wsc = EntityXDRService.class.getAnnotation(WebServiceClient.class);
	    gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRService service = null == url ? new gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRService() : new gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRService(url, new QName(wsc.targetNamespace(), wsc.name()));
		EntityXDRPortType port = service.getEntityXDRPort();

		
		RespondingGatewayProvideAndRegisterDocumentSetRequestType request = new RespondingGatewayProvideAndRegisterDocumentSetRequestType();
		request.setNhinTargetCommunities(targets);
		request.setProvideAndRegisterDocumentSetRequest(message);
		request.setAssertion(assertion);
		
		return port.provideAndRegisterDocumentSetB(request);
	}

	public static void main(String[] args) throws java.io.IOException {
		if (args.length < 1) {
			JFileChooser jfc = new JFileChooser();
			JFrame frame = new JFrame("Choose a file");
			frame.add(jfc);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//			throw new IllegalArgumentException("Please specify a file name");
		}
		File file = new File(args[0]);

		byte fileContent[] = null;
		FileInputStream fin = null;

		try {
			fin = new FileInputStream(file);
			fileContent = new byte[(int) file.length()];
			fin.read(fileContent);
		} finally {
			fin.close();
		}

		// null will use the default wsdlLocation that wsimport called for code gen
		String urlText = null;
		
//		urlText = "http://localhost:8093/foo";
		urlText = "http://localhost:8080/Gateway/DocumentSubmission/1_1/EntityService/EntityDocSubmissionUnsecured?wsdl";
		// perhaps the same identifier each time?
		String documentId = "DocumentXX";

		// file MIME type
		String mimeType = "text/xml";

		// 2.16.840.1.113883.3.1974.2.1 is the CIMIT lab
//		String homeCommunityId = "2.16.840.1.113883.3.1974.2.1";
		String homeCommunityId = "1.1";
		
		System.out.println(submitDocument(urlText, documentId, mimeType, homeCommunityId, fileContent));
	}
}
