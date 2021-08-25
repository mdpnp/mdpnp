package org.mdpnp.apps.testapp.dicom;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.ReceivedObjectHandler;

public class ReadDicom {

    public static void main(String arg[]) {
        
        //Summary of what we are doing here:
        
        //1. Perform a C-FIND operation for all studies matching a specific patient
        //2. For each study found, retrieve all DICOM objects belonging to the study
        //3. The C-FIND operation should help us find and later specify the SOP classes 
        //   that we need to provide for the C-GET operation
        //4. As each file is received, write the information about the incoming data to the console

        try {

            // use the default character set for VR encoding - override this as necessary
            SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
            AttributeList identifier = new AttributeList();

            // build the attributes that you would like to retrieve as well as passing in
            // any search criteria
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY"); // specific query root
            identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet).addValue("Bowen*");
            identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientBirthDate);
            identifier.putNewAttribute(TagFromName.PatientSex);
            identifier.putNewAttribute(TagFromName.StudyInstanceUID);
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.SOPClassesInStudy);

            // retrieve all studies belonging to patient with name 'Bowen'
            new FindSOPClassSCU(
            		"www.dicomserver.co.uk",
            		104,
            		"MEDCONN",
            		"OurFindScu",
                    SOPClass.StudyRootQueryRetrieveInformationModelFind,
                    identifier,
                    new OurFindHandler()
                );
            

        } catch (Exception e) {
            e.printStackTrace(System.err); // in real life, do something about this exception
            System.exit(0);
        }
    }

}

class OurFindHandler extends IdentifierHandler {

    private static String GetSCP_Address = "www.dicomserver.co.uk";
    private static String GetSCP_AE_Title = "MEDCONN";
    private static int GetSCP_Port_Number = 104;
    private static String GetSCU_AE_TITLE = "JavaClient";
    File pathToStoreIncomingDicomFiles = new File("dicom-images");

    public static int resultsFound = 0;

    @SuppressWarnings("unchecked")
    @Override
    public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
        resultsFound++;
        System.out.println("Matched result:" + attributeListForFindResult);

        String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID)
                .getSingleStringValueOrEmptyString();
        System.out.println("studyInstanceUID of matched result:" + studyInstanceUID);

        Set<String> setofSopClassesExpected = new HashSet<String>();
        Attribute sopClassesInStudy = attributeListForFindResult.get(TagFromName.SOPClassesInStudy);
        if (sopClassesInStudy != null) {
            String[] sopClassesInStudyList = sopClassesInStudy.getStringValues();
            for (String sopClassInStudy : sopClassesInStudyList) {
                setofSopClassesExpected.add(sopClassInStudy);
            }
        } else {
            //if SOP class data for study is not found, then supply all storage SOP classes
            setofSopClassesExpected = (Set<String>) SOPClass.getSetOfStorageSOPClasses();
        }

        try {

            AttributeList identifier = new AttributeList();
            {
                AttributeTag tag = TagFromName.QueryRetrieveLevel;
                Attribute attribute = new CodeStringAttribute(tag);
                attribute.addValue("STUDY");
                identifier.put(tag, attribute);
            }
            {
                AttributeTag tag = TagFromName.StudyInstanceUID;
                Attribute attribute = new UniqueIdentifierAttribute(tag);
                attribute.addValue(studyInstanceUID);
                identifier.put(tag, attribute);
            }

            //please see PixelMed documentation if you want to dig deeper into the parameters and their relevance
            new GetSOPClassSCU(GetSCP_Address, 
                    GetSCP_Port_Number, 
                    GetSCP_AE_Title, 
                    GetSCU_AE_TITLE,
                    SOPClass.StudyRootQueryRetrieveInformationModelGet, 
                    identifier, 
                    new IdentifierHandler(), //override and provide your own handler if you need to do anything else
                    pathToStoreIncomingDicomFiles, 
                    StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER,
                    new OurCGetOperationStoreHandler(), 
                    setofSopClassesExpected, 
                    0, 
                    true, 
                    false, 
                    false);

        } catch (Exception e) {
            System.out.println("Error during get operation" + e); // in real life, do something about this exception
            e.printStackTrace(System.err);
        }
    }

}

class OurCGetOperationStoreHandler extends ReceivedObjectHandler {
    
    @Override
    public void sendReceivedObjectIndication(String filename, String transferSyntax, String calledAetTitle)
            throws DicomNetworkException, DicomException, IOException {
        
        System.out.println("Incoming data from " + calledAetTitle + "...");
        System.out.println("filename:" + filename);
        System.out.println("transferSyntax:" + transferSyntax);

    }

}

