package org.oagi.srt.generate.standalone;

import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.common.util.Utility;
import org.oagi.srt.common.util.Zip;
import org.oagi.srt.repository.*;
import org.oagi.srt.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

@Component
public class StandaloneXMLSchema {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private AgencyIdListRepository agencyIdListRepository;

    @Autowired
    private AgencyIdListValueRepository agencyIdListValueRepository;

    @Autowired
    private CodeListRepository codeListRepository;

    @Autowired
    private CodeListValueRepository codeListValueRepository;

    public static List<Integer> abie_ids = new ArrayList();
    public static boolean schema_package_flag = false;
    private List<String> StoredCC = new ArrayList();
    private List<String> Guid_array_list = new ArrayList();

    public String writeXSDFile(Document doc, String filename) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        String filepath = SRTConstants.BOD_FILE_PATH + filename + ".xsd";
        StreamResult result = new StreamResult(new File(filepath));
        transformer.transform(source, result);
        System.out.println(filepath + " is generated");
        return filepath;
    }

    public Element generateSchema(Document doc) {
        Element schemaNode = doc.createElement("xsd:schema");
        schemaNode.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        schemaNode.setAttribute("xmlns", "http://www.openapplications.org/oagis/10");
        schemaNode.setAttribute("xmlns:xml", "http://www.w3.org/XML/1998/namespace");
        schemaNode.setAttribute("targetNamespace", "http://www.openapplications.org/oagis/10");
        schemaNode.setAttribute("elementFormDefault", "qualified");
        schemaNode.setAttribute("attributeFormDefault", "unqualified");
        doc.appendChild(schemaNode);
        return schemaNode;
    }

    public Document generateTopLevelABIE(AssociationBusinessInformationEntityProperty tlASBIEP, Document tlABIEDOM, Element schemaNode) throws Exception {

        Element rootEleNode = generateTopLevelASBIEP(tlASBIEP, schemaNode);
        AggregateBusinessInformationEntity aABIE = queryTargetABIE(tlASBIEP);
        Element rootSeqNode = generateABIE(aABIE, rootEleNode, schemaNode);
        schemaNode = generateBIEs(aABIE, rootSeqNode, schemaNode);
        return tlABIEDOM;
    }

    public Element generateTopLevelASBIEP(AssociationBusinessInformationEntityProperty tlASBIEP, Element gSchemaNode) throws Exception {

        AssociationCoreComponentProperty asccpVO = queryBasedASCCP(tlASBIEP);

        //serm: What does this do?
        if (isCCStored(tlASBIEP.getGuid()))
            return gSchemaNode;

        Element rootEleNode = gSchemaNode.getOwnerDocument().createElement("xsd:element");
        gSchemaNode.appendChild(rootEleNode);
        rootEleNode.setAttribute("name", asccpVO.getPropertyTerm().replaceAll(" ", ""));
        rootEleNode.setAttribute("id", tlASBIEP.getGuid()); //rootEleNode.setAttribute("id", asccpVO.getASCCPGuid());
        //rootEleNode.setAttribute("type", Utility.second(asccpVO.getDen()).replaceAll(" ", "")+"Type");
        Element annotation = gSchemaNode.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = gSchemaNode.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10");
        //documentation.setTextContent(asccpVO.getDefinition());
        rootEleNode.appendChild(annotation);
        annotation.appendChild(documentation);

        //serm: what does this do?
        StoredCC.add(tlASBIEP.getGuid());

        return rootEleNode;
    }

    public Element generateABIE(AggregateBusinessInformationEntity gABIE, Element gElementNode, Element gSchemaNode) throws Exception {
        //AggregateCoreComponent gACC = queryBasedACC(gABIE);

        if (isCCStored(gABIE.getGuid()))
            return gElementNode;
        Element complexType = gElementNode.getOwnerDocument().createElement("xsd:complexType");
        complexType.setAttribute("id", gABIE.getGuid());
        gElementNode.appendChild(complexType);
        //serm: why is this one called generateACC - the function name is not sensible.
        Element PNode = generateACC(gABIE, complexType, gElementNode);
        return PNode;
    }

    public Element generateACC(AggregateBusinessInformationEntity gABIE, Element complexType, Element gElementNode) throws Exception {

        AggregateCoreComponent gACC = queryBasedACC(gABIE);
        Element PNode = complexType.getOwnerDocument().createElement("xsd:sequence");
        //***complexType.setAttribute("id", Utility.generateGUID()); 		
        StoredCC.add(gACC.getGuid());
        Element annotation = gElementNode.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = gElementNode.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10/platform/2");
        //documentation.setTextContent(gACC.getDefinition());
        complexType.appendChild(annotation);
        annotation.appendChild(documentation);
        complexType.appendChild(PNode);

        return PNode;
    }

    public Element generateBIEs(AggregateBusinessInformationEntity gABIE, Element gPNode, Element gSchemaNode) throws Exception {
        AggregateCoreComponent gACC = queryBasedACC(gABIE);

        if (gACC.getDen().equalsIgnoreCase("Any User Area. Details") == true || gACC.getDen().equalsIgnoreCase("Signature. Details") == true) {
            Element any = gPNode.getOwnerDocument().createElement("xsd:any");
            any.setAttribute("namespace", "##any");
            gPNode.appendChild(any);
        }

        List<BusinessInformationEntity> childBIEs = queryChildBIEs(gABIE);
        Element node = null;

        for (BusinessInformationEntity aSRTObject : childBIEs) {
            if (aSRTObject instanceof BasicBusinessInformationEntity) {
                BasicBusinessInformationEntity childBIE = (BasicBusinessInformationEntity) aSRTObject;
                DataType aBDT = queryAssocBDT(childBIE);
                generateBBIE(childBIE, aBDT, gPNode, gSchemaNode);
            } else {
                AssociationBusinessInformationEntity childBIE = (AssociationBusinessInformationEntity) aSRTObject;
                node = generateASBIE(childBIE, gPNode);
                AssociationBusinessInformationEntityProperty anASBIEP = queryAssocToASBIEP(childBIE);
                node = generateASBIEP(anASBIEP, node);
                AggregateBusinessInformationEntity anABIE = queryTargetABIE2(anASBIEP);
                node = generateABIE(anABIE, node, gSchemaNode);
                node = generateBIEs(anABIE, node, gSchemaNode);
            }

        }
        return gSchemaNode;
    }

    public Element generateBDT(BasicBusinessInformationEntity gBBIE, Element eNode, Element gSchemaNode, CodeList gCL) throws Exception {

        DataType bDT = queryBDT(gBBIE);
//		if(isCCStored(bDT.getGuid()))
//			return eNode;

        Element complexType = eNode.getOwnerDocument().createElement("xsd:complexType");
        Element simpleContent = eNode.getOwnerDocument().createElement("xsd:simpleContent");
        Element extNode = eNode.getOwnerDocument().createElement("xsd:extension");
        //complexType.setAttribute("name", Utility.DenToName(bDT.getDen())); 
        complexType.setAttribute("id", Utility.generateGUID()); //complexType.setAttribute("id", bDT.getGuid());
        if (bDT.getDefinition() != null) {
            Element annotation = eNode.getOwnerDocument().createElement("xsd:annotation");
            Element documentation = eNode.getOwnerDocument().createElement("xsd:documentation");
            documentation.setAttribute("source", "http://www.openapplications.org/oagis/10");
            //documentation.setTextContent(bDT.getDefinition());
            complexType.appendChild(annotation);
        }
        StoredCC.add(bDT.getGuid());

        complexType.appendChild(simpleContent);
        simpleContent.appendChild(extNode);

        Attr base = extNode.getOwnerDocument().createAttribute("base");
        base.setNodeValue(getCodeListTypeName(gCL));
        extNode.setAttributeNode(base);
        eNode.appendChild(complexType);
        return eNode;
    }


    public Attr setBDTBase(DataType gBDT, Attr base) throws Exception {
        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                dao3.findOneByBdtIdAndDefault(gBDT.getDtId(), true);

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());

        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());
        base.setValue(aXSDBuiltInType.getBuiltInType());

        return base;
    }

    public Attr setBDTBase(BasicBusinessInformationEntity gBBIE, Attr base) throws Exception {
        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                dao3.findOneByBdtPriRestriId(gBBIE.getBdtPriRestriId());

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());

        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());

        base.setValue(aXSDBuiltInType.getBuiltInType());

        return base;
    }

    public Element setBBIE_Attr_Type(DataType gBDT, Element gNode) throws Exception {
        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                dao3.findOneByBdtIdAndDefault(gBDT.getDtId(), true);

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());

        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());
        if (aXSDBuiltInType.getBuiltInType() != null) {
            Attr type = gNode.getOwnerDocument().createAttribute("type");
            type.setValue(aXSDBuiltInType.getBuiltInType()); //type.setValue(Utility.toCamelCase(aXSDBuiltInType.getName())+"Type");
            gNode.setAttributeNode(type);
        }
        return gNode;
    }

    public Element setBBIE_Attr_Type(BasicBusinessInformationEntity gBBIE, Element gNode) throws Exception {
        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                dao3.findOneByBdtPriRestriId(gBBIE.getBdtPriRestriId());

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());
        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());
        if (aXSDBuiltInType.getBuiltInType() != null) {
            Attr type = gNode.getOwnerDocument().createAttribute("type");
            type.setValue(aXSDBuiltInType.getBuiltInType()); //type.setValue(Utility.toCamelCase(aXSDBuiltInType.getName())+"Type");
            gNode.setAttributeNode(type);
        }
        return gNode;
    }

    public Element generateBDT(BasicBusinessInformationEntity gBBIE, Element eNode) throws Exception {

        DataType bDT = queryBDT(gBBIE);
//		if(isCCStored(bDT.getGuid()))
//			return eNode;

        Element complexType = eNode.getOwnerDocument().createElement("xsd:complexType");
        Element simpleContent = eNode.getOwnerDocument().createElement("xsd:simpleContent");
        Element extNode = eNode.getOwnerDocument().createElement("xsd:extension");
        //complexType.setAttribute("name", Utility.DenToName(bDT.getDen())); 
        complexType.setAttribute("id", Utility.generateGUID()); //complexType.setAttribute("id", bDT.getGuid());
        if (bDT.getDefinition() != null) {
            Element annotation = eNode.getOwnerDocument().createElement("xsd:annotation");
            Element documentation = eNode.getOwnerDocument().createElement("xsd:documentation");
            documentation.setAttribute("source", "http://www.openapplications.org/oagis/10");
            //documentation.setTextContent(bDT.getDefinition());
            complexType.appendChild(annotation);
        }
        StoredCC.add(bDT.getGuid());

        complexType.appendChild(simpleContent);
        simpleContent.appendChild(extNode);

        DataType gBDT = queryAssocBDT(gBBIE);

        Attr base = extNode.getOwnerDocument().createAttribute("base");

        if (gBBIE.getBdtPriRestriId() == 0)
            base = setBDTBase(gBDT, base);
        else {
            base = setBDTBase(gBBIE, base);
        }
        extNode.setAttributeNode(base);
        eNode.appendChild(complexType);
        return eNode;
    }

    public Element generateASBIE(AssociationBusinessInformationEntity gASBIE, Element gPNode) throws Exception {

        AssociationCoreComponent gASCC = queryBasedASCC(gASBIE);

        Element element = gPNode.getOwnerDocument().createElement("xsd:element");
        element.setAttribute("id", gASBIE.getGuid()); //element.setAttribute("id", gASCC.getASCCGuid());
        element.setAttribute("minOccurs", String.valueOf(gASBIE.getCardinalityMin()));
        if (gASBIE.getCardinalityMax() == -1)
            element.setAttribute("maxOccurs", "unbounded");
        else
            element.setAttribute("maxOccurs", String.valueOf(gASBIE.getCardinalityMax()));
        if (gASBIE.isNillable())
            element.setAttribute("nillable", String.valueOf(gASBIE.isNillable()));

        while (!gPNode.getNodeName().equals("xsd:sequence")) {
            gPNode = (Element) gPNode.getParentNode();
        }
        Element annotation = element.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = element.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10/platform/2");
        //documentation.setTextContent(gASBIE.getDefinition());
        annotation.appendChild(documentation);
        element.appendChild(annotation);
        gPNode.appendChild(element);
        StoredCC.add(gASCC.getGuid());//check
        return element;
    }

    public Element generateASBIEP(AssociationBusinessInformationEntityProperty gASBIEP, Element gElementNode) throws Exception {
        AssociationCoreComponentPropertyRepository dao = repositoryFactory.associationCoreComponentPropertyRepository();
        AssociationCoreComponentProperty asccp = dao.findOneByAsccpId(gASBIEP.getBasedAsccpId());
        gElementNode.setAttribute("name", Utility.first(asccp.getDen(), true));
        //gElementNode.setAttribute("type", Utility.second(asccp.getDen())+"Type");
        return gElementNode;

    }

    public BasicCoreComponent queryBasedBCC(BasicBusinessInformationEntity gBBIE) throws Exception {
        BasicCoreComponentRepository dao = repositoryFactory.basicCoreComponentRepository();
        BasicCoreComponent bccVO = dao.findOneByBccId(gBBIE.getBasedBccId());
        return bccVO;
    }

    public Element handleBBIE_Elementvalue(BasicBusinessInformationEntity gBBIE, Element eNode) throws Exception {

        BasicCoreComponent bccVO = queryBasedBCC(gBBIE);
        Attr nameANode = eNode.getOwnerDocument().createAttribute("name");
        nameANode.setValue(Utility.second(bccVO.getDen(), true));
        eNode.setAttributeNode(nameANode);
        eNode.setAttribute("id", gBBIE.getGuid()); //eNode.setAttribute("id", bccVO.getGuid());
        StoredCC.add(bccVO.getGuid());
        if (gBBIE.getDefaultValue() != null && gBBIE.getFixedValue() != null) {
            System.out.println("Error");
        }
        if (gBBIE.isNillable()) {
            Attr nillable = eNode.getOwnerDocument().createAttribute("nillable");
            nillable.setValue("true");
            eNode.setAttributeNode(nillable);
        }
        if (gBBIE.getDefaultValue() != null && gBBIE.getDefaultValue().length() != 0) {
            Attr defaulta = eNode.getOwnerDocument().createAttribute("default");
            defaulta.setValue(gBBIE.getDefaultValue());
            eNode.setAttributeNode(defaulta);
        }
        if (gBBIE.getFixedValue() != null && gBBIE.getFixedValue().length() != 0) {
            Attr fixedvalue = eNode.getOwnerDocument().createAttribute("fixed");
            fixedvalue.setValue(gBBIE.getFixedValue());
            eNode.setAttributeNode(fixedvalue);
        }

        eNode.setAttribute("minOccurs", String.valueOf(gBBIE.getCardinalityMin()));
        if (gBBIE.getCardinalityMax() == -1)
            eNode.setAttribute("maxOccurs", "unbounded");
        else
            eNode.setAttribute("maxOccurs", String.valueOf(gBBIE.getCardinalityMax()));
        if (gBBIE.isNillable())
            eNode.setAttribute("nillable", String.valueOf(gBBIE.isNillable()));

        Element annotation = eNode.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = eNode.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10/platform/2");
        //documentation.setTextContent(gBBIE.getDefinition());
        annotation.appendChild(documentation);
        eNode.appendChild(annotation);

        return eNode;
    }

    public Element handleBBIE_Attributevalue(BasicBusinessInformationEntity gBBIE, Element eNode) throws Exception {

        BasicCoreComponent bccVO = queryBasedBCC(gBBIE);
        Attr nameANode = eNode.getOwnerDocument().createAttribute("name");
        nameANode.setValue(Utility.second(bccVO.getDen(), false));
        eNode.setAttributeNode(nameANode);
        eNode.setAttribute("id", gBBIE.getGuid()); //eNode.setAttribute("id", bccVO.getGuid());
        StoredCC.add(bccVO.getGuid());
        if (gBBIE.getDefaultValue() != null && gBBIE.getFixedValue() != null) {
            System.out.println("Error");
        }
        if (gBBIE.isNillable()) {
            Attr nillable = eNode.getOwnerDocument().createAttribute("nillable");
            nillable.setValue("true");
            eNode.setAttributeNode(nillable);
        }
        if (gBBIE.getDefaultValue() != null && gBBIE.getDefaultValue().length() != 0) {
            Attr defaulta = eNode.getOwnerDocument().createAttribute("default");
            defaulta.setValue(gBBIE.getDefaultValue());
            eNode.setAttributeNode(defaulta);
        }
        if (gBBIE.getFixedValue() != null && gBBIE.getFixedValue().length() != 0) {
            Attr fixedvalue = eNode.getOwnerDocument().createAttribute("fixed");
            fixedvalue.setValue(gBBIE.getFixedValue());
            eNode.setAttributeNode(fixedvalue);
        }
        if (gBBIE.getCardinalityMin() >= 1)
            eNode.setAttribute("use", "required");
        else
            eNode.setAttribute("use", "optional");
        Element annotation = eNode.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = eNode.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10/platform/2");
        //documentation.setTextContent(gBBIE.getDefinition());
        annotation.appendChild(documentation);
        eNode.appendChild(annotation);
        return eNode;
    }

    public CodeList getCodeList(BasicBusinessInformationEntity gBBIE, DataType gBDT) throws Exception {
        CodeList aCL = null;

        if (gBBIE.getCodeListId() != 0) {
            try {
                aCL = codeListRepository.findOne(gBBIE.getCodeListId());
            } catch (EmptyResultDataAccessException e) {
            }
        }

        if (aCL == null) {
            BusinessDataTypePrimitiveRestrictionRepository dao2 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
            BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                    dao2.findOneByBdtPriRestriId(gBBIE.getBdtPriRestriId());
            if (aBDTPrimitiveRestriction.getCodeListId() != 0) {
                try {
                    aCL = codeListRepository.findOne(aBDTPrimitiveRestriction.getCodeListId());
                } catch (EmptyResultDataAccessException e) {
                }
            }
        }

        if (aCL == null) {
            BusinessDataTypePrimitiveRestrictionRepository dao2 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
            try {
                BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                        dao2.findOneByBdtIdAndDefault(gBDT.getDtId(), true);

                if (aBDTPrimitiveRestriction.getCodeListId() != 0)
                    aCL = codeListRepository.findOne(aBDTPrimitiveRestriction.getCodeListId());
                else
                    aCL = null;

            } catch (EmptyResultDataAccessException e) {
                aCL = null;
            }
        }
        return aCL;
    }

    public Element setBBIEType(DataType gBDT, Element gNode) throws Exception {
        Attr tNode = gNode.getOwnerDocument().createAttribute("type");

        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction =
                dao3.findOneByBdtIdAndDefault(gBDT.getDtId(), true);

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());

        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());

        tNode.setValue(aXSDBuiltInType.getBuiltInType());
        if (aXSDBuiltInType.getBuiltInType() != null)
            gNode.setAttributeNode(tNode);
        return gNode;
    }

    public Element setBBIEType(BasicBusinessInformationEntity gBBIE, Element gNode) throws Exception {
        Attr tNode = gNode.getOwnerDocument().createAttribute("type");

        BusinessDataTypePrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction aBDTPrimitiveRestriction = dao3.findOneByBdtPriRestriId(gBBIE.getBdtPriRestriId());

        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao4 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMap aDTAllowedPrimitiveExpressionTypeMap =
                dao4.findOneByCdtAwdPriXpsTypeMapId(aBDTPrimitiveRestriction.getCdtAwdPriXpsTypeMapId());

        XSDBuiltInTypeRepository dao5 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao5.findOneByXbtId(aDTAllowedPrimitiveExpressionTypeMap.getXbtId());

        tNode.setValue(aXSDBuiltInType.getBuiltInType());
        if (aXSDBuiltInType.getBuiltInType() != null)
            gNode.setAttributeNode(tNode);

        return gNode;
    }

    public Element generateBBIE(BasicBusinessInformationEntity gBBIE, DataType gBDT, Element gPNode, Element gSchemaNode) throws Exception {

        BasicCoreComponent gBCC = queryBasedBCC(gBBIE);
        Element eNode = null;
        eNode = gPNode.getOwnerDocument().createElement("xsd:element");
        eNode = handleBBIE_Elementvalue(gBBIE, eNode);
        if (gBCC.getEntityType() == 1) {
            while (!gPNode.getNodeName().equals("xsd:sequence")) {
                gPNode = (Element) gPNode.getParentNode();
            }

            gPNode.appendChild(eNode);

            List<BasicBusinessInformationEntitySupplementaryComponent> SCs = queryBBIESCs(gBBIE);

            CodeList aCL = getCodeList(gBBIE, gBDT);

            if (aCL == null) {
                if (gBBIE.getBdtPriRestriId() == 0) {
                    if (SCs.size() == 0) {
                        eNode = setBBIEType(gBDT, eNode);
                        return eNode;
                    } else {
                        eNode = generateBDT(gBBIE, eNode);
                        eNode = generateSCs(gBBIE, eNode, SCs, gSchemaNode);
                        return eNode;
                    }
                } else {
                    if (SCs.size() == 0) {
                        eNode = setBBIEType(gBBIE, eNode);
                        return eNode;
                    } else {
                        eNode = generateBDT(gBBIE, eNode);
                        eNode = generateSCs(gBBIE, eNode, SCs, gSchemaNode);
                        return eNode;
                    }
                }
            } else { //is aCL null?
                if (!isCodeListGenerated(aCL)) {
                    generateCodeList(aCL, gBDT, gSchemaNode);
                }
                if (SCs.size() == 0) {
                    Attr tNode = eNode.getOwnerDocument().createAttribute("type");
                    tNode.setNodeValue(getCodeListTypeName(aCL));
                    eNode.setAttributeNode(tNode);
                    return eNode;
                } else {
                    eNode = generateBDT(gBBIE, eNode, gSchemaNode, aCL);
                    eNode = generateSCs(gBBIE, eNode, SCs, gSchemaNode);
                    return eNode;
                }
            }


        } else {
            List<BasicBusinessInformationEntitySupplementaryComponent> SCs = queryBBIESCs(gBBIE);
            CodeList aCL = getCodeList(gBBIE, gBDT);
            if (aCL == null) {
                if (gBBIE.getBdtPriRestriId() == 0) {
                    if (SCs.size() == 0) {
                        eNode = gPNode.getOwnerDocument().createElement("xsd:attribute");
                        eNode = handleBBIE_Attributevalue(gBBIE, eNode);

                        while (!gPNode.getNodeName().equals("xsd:complexType")) {
                            gPNode = (Element) gPNode.getParentNode();
                        }

                        gPNode.appendChild(eNode);
                        eNode = setBBIE_Attr_Type(gBDT, eNode);
                        return eNode;
                    } else {
                        //eNode = setBBIE_Attr_Type(gBBIE, eNode);
                        eNode = generateBDT(gBBIE, eNode);
                        return eNode;
                    }
                } else {
                    if (SCs.size() == 0) {
                        eNode = gPNode.getOwnerDocument().createElement("xsd:attribute");
                        eNode = handleBBIE_Attributevalue(gBBIE, eNode);

                        while (!gPNode.getNodeName().equals("xsd:complexType")) {
                            gPNode = (Element) gPNode.getParentNode();
                        }

                        gPNode.appendChild(eNode);
                        eNode = setBBIE_Attr_Type(gBBIE, eNode);
                        return eNode;
                    } else {
                        //eNode = setBBIE_Attr_Type(gBBIE, eNode);
                        eNode = generateBDT(gBBIE, eNode);
                        return eNode;
                    }
                }
            } else { //is aCL null?
                eNode = gPNode.getOwnerDocument().createElement("xsd:attribute");
                eNode = handleBBIE_Attributevalue(gBBIE, eNode);

                while (!gPNode.getNodeName().equals("xsd:complexType")) {
                    gPNode = (Element) gPNode.getParentNode();
                }

                gPNode.appendChild(eNode);

                if (!isCodeListGenerated(aCL)) {
                    generateCodeList(aCL, gBDT, gSchemaNode);
                }
                if (SCs.size() == 0) {
                    if (getCodeListTypeName(aCL) != null) {
                        Attr tNode = eNode.getOwnerDocument().createAttribute("type");
                        tNode.setNodeValue(getCodeListTypeName(aCL));
                        eNode.setAttributeNode(tNode);
                    }
                    return eNode;
                } else {
                    if (gBBIE.getBdtPriRestriId() == 0) {
                        eNode = setBBIE_Attr_Type(gBDT, eNode);
                        return eNode;
                    } else {
                        if (getCodeListTypeName(aCL) != null) {
                            Attr tNode = eNode.getOwnerDocument().createAttribute("type");
                            tNode.setNodeValue(getCodeListTypeName(aCL));
                            eNode.setAttributeNode(tNode);
                        }
                        return eNode;
                    }
                }
            }
        }
    }

    public String getCodeListTypeName(CodeList gCL) throws Exception { //confirm
        //String CodeListTypeName ="xsd:string";
        String CodeListTypeName = gCL.getName() + (gCL.getName().endsWith("Code") == true ? "" : "Code") + "ContentType" + "_" + gCL.getAgencyId() + "_" + gCL.getListId() + "_" + gCL.getVersionId();
        CodeListTypeName = CodeListTypeName.replaceAll(" ", "");
        return CodeListTypeName;
    }

    public Attr setCodeListRestrictionAttr(DataType gBDT, Attr base) throws Exception {
        BusinessDataTypePrimitiveRestrictionRepository dao = repositoryFactory.businessDataTypePrimitiveRestrictionRepository();
        BusinessDataTypePrimitiveRestriction dPrim = dao.findOneByBdtIdAndDefault(gBDT.getDtId(), true);
        if (dPrim.getCodeListId() != 0) {
            base.setNodeValue("xsd:token");
        } else {
            CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository dao2 = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
            CoreDataTypeAllowedPrimitiveExpressionTypeMap aCDTAllowedPrimitiveExpressionTypeMap =
                    dao2.findOneByCdtAwdPriXpsTypeMapId(dPrim.getCdtAwdPriXpsTypeMapId());

            XSDBuiltInTypeRepository dao3 = repositoryFactory.xsdBuiltInTypeRepository();
            XSDBuiltInType aXSDBuiltInType = dao3.findOneByXbtId(aCDTAllowedPrimitiveExpressionTypeMap.getXbtId());
            base.setNodeValue(aXSDBuiltInType.getBuiltInType());
        }
        return base;
    }

    public Attr setCodeListRestrictionAttr(DataTypeSupplementaryComponent gSC, Attr base) throws Exception {
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository dao = repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();
        BusinessDataTypeSupplementaryComponentPrimitiveRestriction dPrim =
                dao.findOneByBdtScIdAndDefault(gSC.getDtScId(), true);
        if (dPrim.getCodeListId() != 0) {
            base.setNodeValue("xsd:token");
        } else {
            CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository dao2 =
                    repositoryFactory.coreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository();
            CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCAllowedPrimitiveExpressionTypeMap =
                    dao2.findOneByCdtScAwdPriXpsTypeMapId(dPrim.getCdtScAwdPriXpsTypeMapId());

            XSDBuiltInTypeRepository dao3 = repositoryFactory.xsdBuiltInTypeRepository();
            XSDBuiltInType aXSDBuiltInType = dao3.findOneByXbtId(aCDTSCAllowedPrimitiveExpressionTypeMap.getXbtId());
            base.setNodeValue(aXSDBuiltInType.getBuiltInType());
        }
        return base;
    }

    public List<CodeListValue> getCodeListValues(CodeList gCL) throws Exception {
        List<CodeListValue> codelistid = codeListValueRepository.findByCodeListId(gCL.getCodeListId());
        List<CodeListValue> gCLVs = new ArrayList();

        for (int i = 0; i < codelistid.size(); i++) {
            CodeListValue aCodeListValue = codelistid.get(i);
            if (aCodeListValue.isUsedIndicator())
                gCLVs.add(aCodeListValue);
        }

        return gCLVs;
    }

    public Element generateCodeList(CodeList gCL, DataType gBDT, Element gSchemaNode) throws DOMException, Exception {
        Element stNode = gSchemaNode.getOwnerDocument().createElement("xsd:simpleType");
        Attr stNameNode = gSchemaNode.getOwnerDocument().createAttribute("name");
        stNameNode.setValue(getCodeListTypeName(gCL));
        stNode.setAttributeNode(stNameNode);

        Attr stIdNode = gSchemaNode.getOwnerDocument().createAttribute("id");
        stIdNode.setValue(gCL.getGuid());
        stNode.setAttributeNode(stIdNode);

        Element rtNode = stNode.getOwnerDocument().createElement("xsd:restriction");
        Attr base = gSchemaNode.getOwnerDocument().createAttribute("base");
        base = setCodeListRestrictionAttr(gBDT, base);
        rtNode.setAttributeNode(base);
        stNode.appendChild(rtNode);

        List<CodeListValue> gCLVs = getCodeListValues(gCL);
        for (int i = 0; i < gCLVs.size(); i++) {
            CodeListValue bCodeListValue = (CodeListValue) gCLVs.get(i);
            Element enumeration = stNode.getOwnerDocument().createElement("xsd:enumeration");
            Attr value = stNode.getOwnerDocument().createAttribute("value");
            value.setNodeValue(bCodeListValue.getValue());
            enumeration.setAttributeNode(value);
            rtNode.appendChild(enumeration);
        }
        Guid_array_list.add(gCL.getGuid());
        gSchemaNode.appendChild(stNode);
        return stNode;
    }

    public Element generateCodeList(CodeList gCL, DataTypeSupplementaryComponent gSC, Element gSchemaNode) throws DOMException, Exception {
        Element stNode = gSchemaNode.getOwnerDocument().createElement("xsd:simpleType");
        Attr stNameNode = gSchemaNode.getOwnerDocument().createAttribute("name");
        stNameNode.setValue(getCodeListTypeName(gCL));
        stNode.setAttributeNode(stNameNode);

        Attr stIdNode = gSchemaNode.getOwnerDocument().createAttribute("id");
        stIdNode.setValue(gCL.getGuid());
        stNode.setAttributeNode(stIdNode);

        Element rtNode = gSchemaNode.getOwnerDocument().createElement("xsd:restriction");

        Attr base = gSchemaNode.getOwnerDocument().createAttribute("base");
        base = setCodeListRestrictionAttr(gSC, base);
        rtNode.setAttributeNode(base);
        stNode.appendChild(rtNode);

        List<CodeListValue> gCLVs = getCodeListValues(gCL);
        for (int i = 0; i < gCLVs.size(); i++) {
            CodeListValue bCodeListValue = (CodeListValue) gCLVs.get(i);
            Element enumeration = stNode.getOwnerDocument().createElement("xsd:enumeration");
            Attr value = stNode.getOwnerDocument().createAttribute("value");
            value.setNodeValue(bCodeListValue.getValue());
            enumeration.setAttributeNode(value);
            rtNode.appendChild(enumeration);
        }
        Guid_array_list.add(gCL.getGuid());
        gSchemaNode.appendChild(stNode);
        return stNode;
    }

    public Element handleBBIESCvalue(BasicBusinessInformationEntitySupplementaryComponent aBBIESC, Element aNode) throws Exception {
        //Handle gSC[i]
        if (aBBIESC.getDefaultValue() != null && aBBIESC.getFixedValue() != null) {
            System.out.println("default and fixed value options handling error");
        } else if (aBBIESC.getDefaultValue() != null && aBBIESC.getDefaultValue().length() != 0) {
            Attr default_att = aNode.getOwnerDocument().createAttribute("default");
            default_att.setNodeValue(aBBIESC.getDefaultValue());
            aNode.setAttributeNode(default_att);
        } else if (aBBIESC.getFixedValue() != null && aBBIESC.getFixedValue().length() != 0) {
            Attr fixed_att = aNode.getOwnerDocument().createAttribute("fixed");
            fixed_att.setNodeValue(aBBIESC.getFixedValue());
            aNode.setAttributeNode(fixed_att);
        }
        // Generate a DOM Attribute node
        Attr aNameNode = aNode.getOwnerDocument().createAttribute("name");
        DataTypeSupplementaryComponentRepository dao = repositoryFactory.dataTypeSupplementaryComponentRepository();
        DataTypeSupplementaryComponent aDTSC = dao.findOneByDtScId(aBBIESC.getDtScId());
        if (aDTSC.getRepresentationTerm().equalsIgnoreCase("Text"))
            aNameNode.setNodeValue(Utility.toLowerCamelCase(aDTSC.getPropertyTerm()));
        else if (aDTSC.getRepresentationTerm().equalsIgnoreCase("Identifier"))
            aNameNode.setNodeValue(Utility.toLowerCamelCase(aDTSC.getPropertyTerm()).concat("Id"));
        else
            aNameNode.setNodeValue(Utility.toLowerCamelCase(aDTSC.getPropertyTerm()));
        //aNameNode.setNodeValue(Utility.toLowerCamelCase(aDTSC.getPropertyTerm()).concat(Utility.toCamelCase(aDTSC.getRepresentationTerm())));
        aNode.setAttributeNode(aNameNode);
        if (aBBIESC.getMinCardinality() >= 1)
            aNode.setAttribute("use", "required");
        else
            aNode.setAttribute("use", "optional");
        Element annotation = aNode.getOwnerDocument().createElement("xsd:annotation");
        Element documentation = aNode.getOwnerDocument().createElement("xsd:documentation");
        documentation.setAttribute("source", "http://www.openapplications.org/oagis/10/platform/2");
        //documentation.setTextContent(aBBIESC.getDefinition());
        annotation.appendChild(documentation);
        aNode.appendChild(annotation);

        return aNode;
    }

    public CodeList getCodeList(BasicBusinessInformationEntitySupplementaryComponent gBBIESC) throws Exception {
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();

        DataTypeSupplementaryComponentRepository dao4 = repositoryFactory.dataTypeSupplementaryComponentRepository();
        DataTypeSupplementaryComponent gDTSC = dao4.findOneByDtScId(gBBIESC.getDtScId());

        CodeList aCL = new CodeList();
        try {
            aCL = codeListRepository.findOne(gBBIESC.getCodeListId());
        } catch (EmptyResultDataAccessException e) {
        }
        BusinessDataTypeSupplementaryComponentPrimitiveRestriction aBDTSCPrimitiveRestriction =
                new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
        try {
            aBDTSCPrimitiveRestriction = dao3.findOneByBdtScPriRestriId(gBBIESC.getDtScPriRestriId());
        } catch (EmptyResultDataAccessException e) {
        }

        if (aCL.getCodeListId() != 0) {

        } else if (aBDTSCPrimitiveRestriction.getBdtScPriRestriId() != 0) {
            aCL = codeListRepository.findOne(aBDTSCPrimitiveRestriction.getCodeListId());
        } else if (aCL.getCodeListId() == 0 && aBDTSCPrimitiveRestriction.getBdtScPriRestriId() == 0) {
            BusinessDataTypeSupplementaryComponentPrimitiveRestriction bBDTSCPrimitiveRestriction =
                    dao3.findOneByBdtScIdAndDefault(gDTSC.getDtScId(), true);
            try {
                aCL = codeListRepository.findOne(bBDTSCPrimitiveRestriction.getCodeListId());
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
        return aCL;
    }

    public AgencyIdList getAgencyIdList(BasicBusinessInformationEntitySupplementaryComponent gBBIESC) throws Exception {
        AgencyIdList aAL = null;
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository dao3 = repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();
        BusinessDataTypeSupplementaryComponentPrimitiveRestriction aBDTSCPrimitiveRestriction =
                dao3.findOneByBdtScPriRestriId(gBBIESC.getDtScPriRestriId());
        DataTypeSupplementaryComponentRepository dao5 = repositoryFactory.dataTypeSupplementaryComponentRepository();
        DataTypeSupplementaryComponent gDTSC = dao5.findOneByDtScId(gBBIESC.getDtScId());

        boolean firstCheck = true;
        try {
            aAL = agencyIdListRepository.findOne(gBBIESC.getAgencyIdListId());
        } catch (EmptyResultDataAccessException e) {
            firstCheck = false;
        }

        boolean secondCheck = true;
        if (aAL == null) {
            try {
                aAL = agencyIdListRepository.findOne(aBDTSCPrimitiveRestriction.getAgencyIdListId());
            } catch (EmptyResultDataAccessException e) {
                secondCheck = false;
            }
        }

        if (!firstCheck && !secondCheck) {
            BusinessDataTypeSupplementaryComponentPrimitiveRestriction bBDTSCPrimitiveRestriction =
                    dao3.findOneByBdtScIdAndDefault(gDTSC.getDtScId(), true);
            try {
                aAL = agencyIdListRepository.findOne(bBDTSCPrimitiveRestriction.getAgencyIdListId());
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
        return aAL;
    }

    public Element setBBIESCType(BasicBusinessInformationEntitySupplementaryComponent gBBIESC, Element gNode) throws Exception {
        DataTypeSupplementaryComponentRepository dao2 = repositoryFactory.dataTypeSupplementaryComponentRepository();
        DataTypeSupplementaryComponent gDTSC = dao2.findOneByDtScId(gBBIESC.getDtScId());
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository dao3 =
                repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();
        BusinessDataTypeSupplementaryComponentPrimitiveRestriction bBDTSCPrimitiveRestriction =
                dao3.findOneByBdtScIdAndDefault(gDTSC.getDtScId(), true);
        CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository dao5 =
                repositoryFactory.coreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCAllowedPrimitiveExpressionTypeMap =
                dao5.findOneByCdtScAwdPriXpsTypeMapId(bBDTSCPrimitiveRestriction.getCdtScAwdPriXpsTypeMapId());
        XSDBuiltInTypeRepository dao6 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao6.findOneByXbtId(aCDTSCAllowedPrimitiveExpressionTypeMap.getXbtId());
        if (aXSDBuiltInType.getBuiltInType() != null) {
            Attr aTypeNode = gNode.getOwnerDocument().createAttribute("type");
            aTypeNode.setNodeValue(aXSDBuiltInType.getBuiltInType());
            gNode.setAttributeNode(aTypeNode);
        }
        return gNode;

    }

    public Element setBBIESCType2(BasicBusinessInformationEntitySupplementaryComponent gBBIESC, Element gNode) throws Exception {
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository dao3 =
                repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();
        BusinessDataTypeSupplementaryComponentPrimitiveRestriction aBDTSCPrimitiveRestriction = dao3.findOneByBdtScPriRestriId(gBBIESC.getDtScPriRestriId());
        CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository dao5 =
                repositoryFactory.coreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCAllowedPrimitiveExpressionTypeMap =
                        dao5.findOneByCdtScAwdPriXpsTypeMapId(aBDTSCPrimitiveRestriction.getCdtScAwdPriXpsTypeMapId());
        XSDBuiltInTypeRepository dao6 = repositoryFactory.xsdBuiltInTypeRepository();
        XSDBuiltInType aXSDBuiltInType = dao6.findOneByXbtId(aCDTSCAllowedPrimitiveExpressionTypeMap.getXbtId());
        if (aXSDBuiltInType.getBuiltInType() != null) {
            Attr aTypeNode = gNode.getOwnerDocument().createAttribute("type");
            aTypeNode.setNodeValue(aXSDBuiltInType.getBuiltInType());
            gNode.setAttributeNode(aTypeNode);
        }
        return gNode;

    }

    public Element generateSCs(BasicBusinessInformationEntity gBBIE, Element gBBIENode, List<BasicBusinessInformationEntitySupplementaryComponent> gSCs, Element gSchemaNode) throws DOMException, Exception {
        Element tNode = (Element) gBBIENode;
        while (true) {
            if (tNode.getNodeName().equals("xsd:simpleType") || tNode.getNodeName().equals("xsd:complexType"))
                break;
            tNode = (Element) tNode.getLastChild();
            //tNode = (Element) tNode.getParentNode();
        }
        Node tmp = tNode.getFirstChild();
        do {
            if (tmp.getNodeName().equals("xsd:simpleContent")) {
                tNode = (Element) tmp.getFirstChild();
                break;
            }
            if (tmp.getNextSibling() != null)
                tmp = tmp.getNextSibling();
        } while (tmp != null);

//		if(tNode.getFirstChild().getNodeName().equals("xsd:simpleContent"))
//			tNode = (Element) tNode.getFirstChild().getFirstChild();
        //here
        for (int i = 0; i < gSCs.size(); i++) {
            BasicBusinessInformationEntitySupplementaryComponent aBBIESC = gSCs.get(i);
            if (aBBIESC.getMaxCardinality() == 0)
                continue;
            Element aNode = tNode.getOwnerDocument().createElement("xsd:attribute");
            aNode = handleBBIESCvalue(aBBIESC, aNode); //Generate a DOM Element Node, handle values

            //Get a code list object
            CodeList aCL = getCodeList(aBBIESC);
            if (aCL != null) {
                Attr stIdNode = aNode.getOwnerDocument().createAttribute("id");
                stIdNode.setNodeValue(Utility.generateGUID());
                aNode.setAttributeNode(stIdNode);
            }

            AgencyIdList aAL = new AgencyIdList();

            if (aCL == null) { //aCL = null?
                aAL = getAgencyIdList(aBBIESC);

                if (aAL != null) {
                    Attr stIdNode = aNode.getOwnerDocument().createAttribute("id");
                    stIdNode.setNodeValue(aAL.getGuid());
                    aNode.setAttributeNode(stIdNode);
                }

                if (aAL == null) { //aAL = null?
                    int primRestriction = aBBIESC.getDtScPriRestriId();
                    if (primRestriction == 0)
                        aNode = setBBIESCType(aBBIESC, aNode);
                    else
                        aNode = setBBIESCType2(aBBIESC, aNode);
                } else { //aAL = null?
                    if (!isAgencyListGenerated(aAL))  //isAgencyListGenerated(aAL)?
                        generateAgencyList(aAL, aBBIESC, gSchemaNode);
                    if (getAgencyListTypeName(aAL) != null) {
                        Attr aTypeNode = aNode.getOwnerDocument().createAttribute("type");
                        aTypeNode.setNodeValue(getAgencyListTypeName(aAL));
                        aNode.setAttributeNode(aTypeNode);
                    }
                }
            } else { //aCL = null?
                if (!isCodeListGenerated(aCL)) {
                    DataTypeSupplementaryComponentRepository dao = repositoryFactory.dataTypeSupplementaryComponentRepository();
                    DataTypeSupplementaryComponent aDTSC = dao.findOneByDtScId(aBBIESC.getDtScId());
                    generateCodeList(aCL, aDTSC, gSchemaNode);
                }
                if (getCodeListTypeName(aCL) != null) {
                    Attr aTypeNode = aNode.getOwnerDocument().createAttribute("type");
                    aTypeNode.setNodeValue(getCodeListTypeName(aCL));
                    aNode.setAttributeNode(aTypeNode);
                }
            }
//			if(isCCStored(aNode.getAttribute("id")))
//				continue;
//			StoredCC.add(aNode.getAttribute("id"));
            tNode.appendChild(aNode);
        }
        return tNode;
    }

    public boolean isAgencyListGenerated(AgencyIdList gAL) throws Exception {
        for (int i = 0; i < Guid_array_list.size(); i++) {
            if (gAL.getGuid().equals(Guid_array_list.get(i)))
                return true;
        }
        return false;
    }

    public String getAgencyListTypeName(AgencyIdList gAL) throws Exception {
        String AgencyListTypeName = "clm" + gAL.getAgencyId() + gAL.getListId() + gAL.getVersionId() + "_" + Utility.toCamelCase(gAL.getName()) + "ContentType";
        AgencyListTypeName = AgencyListTypeName.replaceAll(" ", "");
        return AgencyListTypeName;
    }

    public Element generateAgencyList(AgencyIdList gAL, BasicBusinessInformationEntitySupplementaryComponent gSC, Element gSchemaNode) throws Exception {
        Element stNode = gSchemaNode.getOwnerDocument().createElement("xsd:simpleType");

        Attr stNameNode = gSchemaNode.getOwnerDocument().createAttribute("name");
        stNameNode.setNodeValue(getAgencyListTypeName(gAL));
        stNode.setAttributeNode(stNameNode);

        Attr stIdNode = gSchemaNode.getOwnerDocument().createAttribute("id");
        stIdNode.setNodeValue(gAL.getGuid());
        stNode.setAttributeNode(stIdNode);

        Element rtNode = gSchemaNode.getOwnerDocument().createElement("xsd:restriction");

        Attr base = gSchemaNode.getOwnerDocument().createAttribute("base");
        base.setNodeValue("xsd:token");
        stNode.appendChild(rtNode);
        rtNode.setAttributeNode(base);

        List<AgencyIdListValue> gALVs = agencyIdListValueRepository.findByOwnerListId(gAL.getAgencyIdListId());

        for (int i = 0; i < gALVs.size(); i++) {
            AgencyIdListValue aAgencyIdListValue = gALVs.get(i);
            Element enumeration = stNode.getOwnerDocument().createElement("xsd:enumeration");
            stNode.appendChild(enumeration);
            Attr value = stNode.getOwnerDocument().createAttribute("value");
            value.setNodeValue(aAgencyIdListValue.getValue());
            stNode.setAttributeNode(value);
        }
        gSchemaNode.appendChild(stNode);
        Guid_array_list.add(gAL.getGuid());
        return stNode;
    }

    public AssociationBusinessInformationEntityProperty receiveASBIEP(int ABIE_Id) throws Exception {
        AssociationBusinessInformationEntityPropertyRepository dao = repositoryFactory.associationBusinessInformationEntityPropertyRepository();
        AssociationBusinessInformationEntityProperty aAssociationBusinessInformationEntityProperty = dao.findOneByRoleOfAbieId(ABIE_Id);
        return aAssociationBusinessInformationEntityProperty;
    }

    public List<AggregateBusinessInformationEntity> receiveABIE(List<Integer> abie_ids) throws Exception {
        AggregateBusinessInformationEntityRepository dao = repositoryFactory.aggregateBusinessInformationEntityRepository();

        List<AggregateBusinessInformationEntity> aAggregateBusinessInformationEntity = new ArrayList();
        for (int i = 0; i < abie_ids.size(); i++) {
            aAggregateBusinessInformationEntity.add(dao.findOneByAbieId(abie_ids.get(i)));
        }
        return aAggregateBusinessInformationEntity;
    }

    public DataType queryBDT(BasicBusinessInformationEntity gBBIE) throws Exception {
        BasicCoreComponentRepository dao3 = repositoryFactory.basicCoreComponentRepository();
        BasicCoreComponent gBCC = dao3.findOneByBccId(gBBIE.getBasedBccId());
        BasicCoreComponentPropertyRepository dao = repositoryFactory.basicCoreComponentPropertyRepository();
        BasicCoreComponentProperty aBasicCoreComponentProperty = dao.findOneByBccpId(gBCC.getToBccpId());
        DataTypeRepository dao2 = repositoryFactory.dataTypeRepository();
        DataType bDT = dao2.findOneByDtId(aBasicCoreComponentProperty.getBdtId());
        return bDT;
    }

    public AssociationCoreComponentProperty queryBasedASCCP(AssociationBusinessInformationEntityProperty gASBIEP) throws Exception {
        AssociationCoreComponentPropertyRepository dao = repositoryFactory.associationCoreComponentPropertyRepository();
        AssociationCoreComponentProperty asccpVO = dao.findOneByAsccpId(gASBIEP.getBasedAsccpId());
        return asccpVO;
    }

    public AggregateCoreComponent queryBasedACC(AggregateBusinessInformationEntity gABIE) throws Exception {
        AggregateCoreComponentRepository dao = repositoryFactory.aggregateCoreComponentRepository();
        AggregateCoreComponent gACC = dao.findOneByAccId(gABIE.getBasedAccId());
        return gACC;
    }

    public AssociationCoreComponent queryBasedASCC(AssociationBusinessInformationEntity gASBIE) throws Exception {
        AssociationCoreComponentRepository dao = repositoryFactory.associationCoreComponentRepository();
        AssociationCoreComponent gASCC = dao.findOneByAsccId(gASBIE.getBasedAscc());
        return gASCC;
    }

    public AggregateBusinessInformationEntity queryTargetABIE(AssociationBusinessInformationEntityProperty gASBIEP) {
        AggregateBusinessInformationEntityRepository dao = repositoryFactory.aggregateBusinessInformationEntityRepository();
        AggregateBusinessInformationEntity abievo = dao.findOneByAbieId(gASBIEP.getRoleOfAbieId());
        return abievo;
    }

    public AggregateCoreComponent queryTargetACC(AssociationBusinessInformationEntityProperty gASBIEP) {
        AggregateBusinessInformationEntityRepository dao = repositoryFactory.aggregateBusinessInformationEntityRepository();
        AggregateBusinessInformationEntity abievo = dao.findOneByAbieId(gASBIEP.getRoleOfAbieId());

        AggregateCoreComponentRepository dao2 = repositoryFactory.aggregateCoreComponentRepository();
        AggregateCoreComponent aAggregateCoreComponent = dao2.findOneByAccId(abievo.getBasedAccId());
        return aAggregateCoreComponent;
    }

    public AggregateBusinessInformationEntity queryTargetABIE2(AssociationBusinessInformationEntityProperty gASBIEP) {
        AggregateBusinessInformationEntityRepository dao = repositoryFactory.aggregateBusinessInformationEntityRepository();
        AggregateBusinessInformationEntity abieVO = dao.findOneByAbieId(gASBIEP.getRoleOfAbieId());
        return abieVO;
    }

    //Get only Child BIEs whose is_used flag is true
    public List<BusinessInformationEntity> queryChildBIEs(AggregateBusinessInformationEntity gABIE) {
        Map<BusinessInformationEntity, Integer> sequence = new HashMap();
        ValueComparator bvc = new ValueComparator(sequence);
        Map<BusinessInformationEntity, Integer> ordered_sequence = new TreeMap(bvc);

        AssociationBusinessInformationEntityRepository dao = repositoryFactory.associationBusinessInformationEntityRepository();
        List<AssociationBusinessInformationEntity> asbievo = dao.findByFromAbieIdAndUsed(gABIE.getAbieId(), true);
        BasicBusinessInformationEntityRepository dao2 = repositoryFactory.basicBusinessInformationEntityRepository();
        List<BasicBusinessInformationEntity> bbievo = dao2.findByFromAbieIdAndUsed(gABIE.getAbieId(), true);
        List<BusinessInformationEntity> result = new ArrayList();

        for (BasicBusinessInformationEntity aBasicBusinessInformationEntity : bbievo) {
            if (aBasicBusinessInformationEntity.getCardinalityMax() != 0) //modify
                sequence.put(aBasicBusinessInformationEntity, aBasicBusinessInformationEntity.getSeqKey());
        }

        for (AssociationBusinessInformationEntity aAssociationBusinessInformationEntity : asbievo) {
            if (aAssociationBusinessInformationEntity.getCardinalityMax() != 0)
                sequence.put(aAssociationBusinessInformationEntity, aAssociationBusinessInformationEntity.getSeqKey());
        }

        ordered_sequence.putAll(sequence);
        Set set = ordered_sequence.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            result.add((BusinessInformationEntity) me.getKey());
        }
        return result;
    }

    class ValueComparator implements Comparator<BusinessInformationEntity> {

        Map<BusinessInformationEntity, Integer> base;

        public ValueComparator(Map<BusinessInformationEntity, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(BusinessInformationEntity a, BusinessInformationEntity b) {
            if (base.get(a) <= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }

    }

    public AssociationBusinessInformationEntityProperty queryAssocToASBIEP(AssociationBusinessInformationEntity gASBIE) {
        AssociationBusinessInformationEntityPropertyRepository dao = repositoryFactory.associationBusinessInformationEntityPropertyRepository();
        AssociationBusinessInformationEntityProperty asbiepVO = dao.findOneByAsbiepId(gASBIE.getToAsbiepId());
        return asbiepVO;
    }

    public DataType queryAssocBDT(BasicBusinessInformationEntity gBBIE) throws Exception {
        BasicCoreComponentRepository dao = repositoryFactory.basicCoreComponentRepository();
        BasicCoreComponent bccVO = dao.findOneByBccId(gBBIE.getBasedBccId());

        BasicCoreComponentPropertyRepository dao2 = repositoryFactory.basicCoreComponentPropertyRepository();
        BasicCoreComponentProperty bccpVO = dao2.findOneByBccpId(bccVO.getToBccpId());

        DataTypeRepository dao3 = repositoryFactory.dataTypeRepository();
        DataType aBDT = dao3.findOneByDtId(bccpVO.getBdtId());

        return aBDT;
    }

    //Get only SCs whose is_used is true.
    public List<BasicBusinessInformationEntitySupplementaryComponent> queryBBIESCs(BasicBusinessInformationEntity gBBIE) {
        BasicBusinessInformationEntitySupplementaryComponentRepository dao = repositoryFactory.basicBusinessInformationEntitySupplementaryComponentRepository();
        return dao.findByBbieIdAndUsed(gBBIE.getBbieId(), true);
    }

    public boolean isCodeListGenerated(CodeList gCL) throws Exception {
        for (int i = 0; i < Guid_array_list.size(); i++) {
            if (gCL.getGuid().equals(Guid_array_list.get(i)))
                return true;
        }
        return false;
    }

    public boolean isCCStored(String id) throws Exception {
        for (int i = 0; i < StoredCC.size(); i++) {
            if (StoredCC.get(i).equals(id)) {
                return true;
            }
        }
        return false;
    }

    public String generateXMLSchema(List<Integer> abie_ids, boolean schema_package_flag) throws Exception {
        List<AggregateBusinessInformationEntity> gABIE = receiveABIE(abie_ids);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element schemaNode = generateSchema(doc);
        String filepath = null, filename = null;
        if (schema_package_flag == true) {
            for (AggregateBusinessInformationEntity aAggregateBusinessInformationEntity : gABIE) {
                AssociationBusinessInformationEntityProperty aAssociationBusinessInformationEntityProperty = receiveASBIEP(aAggregateBusinessInformationEntity.getAbieId());
                System.out.println("Generating Top Level ABIE w/ given AssociationBusinessInformationEntityProperty Id: " + aAssociationBusinessInformationEntityProperty.getAsbiepId());
                doc = generateTopLevelABIE(aAssociationBusinessInformationEntityProperty, doc, schemaNode);
                AssociationCoreComponentPropertyRepository dao = repositoryFactory.associationCoreComponentPropertyRepository();
                AssociationCoreComponentProperty asccpvo = dao.findOneByAsccpId(aAssociationBusinessInformationEntityProperty.getBasedAsccpId());
                filename = asccpvo.getPropertyTerm().replaceAll(" ", "");
            }
            filepath = writeXSDFile(doc, filename + "_standalone");
        } else {
            for (AggregateBusinessInformationEntity aAggregateBusinessInformationEntity : gABIE) {
                AssociationBusinessInformationEntityProperty aAssociationBusinessInformationEntityProperty = receiveASBIEP(aAggregateBusinessInformationEntity.getAbieId());
                doc = docBuilder.newDocument();
                schemaNode = generateSchema(doc);
                doc = generateTopLevelABIE(aAssociationBusinessInformationEntityProperty, doc, schemaNode);
                writeXSDFile(doc, "Package/" + aAggregateBusinessInformationEntity.getGuid());
            }
            filepath = Zip.compression(Utility.generateGUID());
        }

        return filepath;
    }

    public static void main(String args[]) throws Exception {
        StandaloneXMLSchema aa = new StandaloneXMLSchema();
        abie_ids.add(2871957);
        aa.generateXMLSchema(abie_ids, true);
        System.out.println("###END###");
    }
}