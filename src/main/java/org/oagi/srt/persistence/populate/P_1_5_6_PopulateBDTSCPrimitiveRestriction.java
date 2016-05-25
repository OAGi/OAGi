package org.oagi.srt.persistence.populate;

import org.oagi.srt.Application;
import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.common.util.Utility;
import org.oagi.srt.common.util.XPathHandler;
import org.oagi.srt.repository.*;
import org.oagi.srt.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yunsu Lee
 * @version 1.0
 */
@Component
public class P_1_5_6_PopulateBDTSCPrimitiveRestriction {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private AgencyIdListRepository agencyIdListRepository;

    @Autowired
    private CodeListRepository codeListRepository;

    @Autowired
    private DataTypeRepository dataTypeRepository;

    public int getAgencyListID() throws Exception {
        AgencyIdList agencyIdList = agencyIdListRepository.findOneByName("Agency Identification");
        return agencyIdList.getAgencyIdListId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void run(ApplicationContext applicationContext) throws Exception {
        XPathHandler xh = new XPathHandler(SRTConstants.BUSINESS_DATA_TYPE_XSD_FILE_PATH);
        XPathHandler xh2 = new XPathHandler(SRTConstants.FILEDS_XSD_FILE_PATH);
        System.out.println("### 1.5.6 Start");
        populateBDTSCPrimitiveRestriction(xh, xh2, true);
        System.out.println("### 1.5.6 End");
    }

    public void populateBDTSCPrimitiveRestriction(XPathHandler xh, XPathHandler xh2, boolean is_fields_xsd) throws Exception {
        DataTypeSupplementaryComponentRepository dao = repositoryFactory.dataTypeSupplementaryComponentRepository();
        BusinessDataTypeSupplementaryComponentPrimitiveRestrictionRepository aBDTSCPrimitiveRestrictionDAO = repositoryFactory.businessDataTypeSupplementaryComponentPrimitiveRestrictionRepository();
        CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository aCDTSCAllowedPrimitiveExpressionTypeMapDAO = repositoryFactory.coreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository aCDTAllowedPrimitiveExpressionTypeMapDAO = repositoryFactory.coreDataTypeAllowedPrimitiveExpressionTypeMapRepository();
        CoreDataTypeSupplementaryComponentAllowedPrimitiveRepository aCDTSCAllowedPrimitiveDAO = repositoryFactory.coreDataTypeSupplementaryComponentAllowedPrimitiveRepository();
        CoreDataTypeAllowedPrimitiveRepository aCDTAllowedPrimitiveDAO = repositoryFactory.coreDataTypeAllowedPrimitiveRepository();
        CoreDataTypePrimitiveRepository aCDTPrimitiveDAO = repositoryFactory.coreDataTypePrimitiveRepository();
        XSDBuiltInTypeRepository aXBTDAO = repositoryFactory.xsdBuiltInTypeRepository();

        List<DataTypeSupplementaryComponent> al = dao.findAll();
        List<DataTypeSupplementaryComponent> al_meta = new ArrayList();
        if (is_fields_xsd) {

        } else {
            for (DataTypeSupplementaryComponent aDataTypeSupplementaryComponent : al) {
                DataType dtVO = dataTypeRepository.findOne(aDataTypeSupplementaryComponent.getOwnerDtId());

                String metalist[] = {"ExpressionType", "ActionExpressionType", "ResponseExpressionType"};
                for (int k = 0; k < metalist.length; k++) {
                    if (dtVO.getDen().equalsIgnoreCase(Utility.typeToDen(metalist[k])))
                        al_meta.add(k, aDataTypeSupplementaryComponent);
                }
            }
            al = new ArrayList(al_meta);
        }

        for (DataTypeSupplementaryComponent aDataTypeSupplementaryComponent : al) {
            String tmp_guid = null;
            if (isBDTSC(aDataTypeSupplementaryComponent.getOwnerDtId())) {

                Node result = xh.getNode("//xsd:attribute[@id='" + aDataTypeSupplementaryComponent.getGuid() + "']");
                tmp_guid = aDataTypeSupplementaryComponent.getGuid();
                if (result == null) { // if result is null, then look up its based default BDT and get guid
                    result = xh2.getNode("//xsd:attribute[@id='" + aDataTypeSupplementaryComponent.getGuid() + "']");
                    tmp_guid = aDataTypeSupplementaryComponent.getGuid();

                    if (aDataTypeSupplementaryComponent.getBasedDtScId() != 0 && result == null) {
                        DataTypeSupplementaryComponent dtscVO = getDTSC(aDataTypeSupplementaryComponent.getBasedDtScId());
                        result = xh.getNode("//xsd:attribute[@id='" + dtscVO.getGuid() + "']");
                        tmp_guid = dtscVO.getGuid();
                        if (result == null) {
                            result = xh2.getNode("//xsd:attribute[@id='" + dtscVO.getGuid() + "']");
                            tmp_guid = dtscVO.getGuid();
                        }
                    }
                }

                Element ele = (Element) result;
                int codeListId = 0;
                String ele_name = "";

                System.out.print("***** " + aDataTypeSupplementaryComponent.getPropertyTerm() + "_" + aDataTypeSupplementaryComponent.getRepresentationTerm() + " Start! " + aDataTypeSupplementaryComponent.getGuid());

                if (ele != null && ele.getAttribute("type") != null) {
                    String attrTypeName = ele.getAttribute("type").replaceAll("ContentType", "");
                    String typeName = ele.getAttribute("type").replaceAll("ContentType", "");
                    try {
                        codeListId = codeListRepository.findOneByName(typeName).getCodeListId();
                    } catch (EmptyResultDataAccessException e) {
                        codeListId = 0;
                    }
                    ele_name = ele.getAttribute("name");
                    System.out.println(" attrTypeName= " + attrTypeName + " codelist=" + codeListId);
                } else {
                    System.out.println("");
                }

                if (!is_fields_xsd) {
                    DataType dtVO = dataTypeRepository.findOne(aDataTypeSupplementaryComponent.getOwnerDtId());
                    DataType defaultTextBDT = dataTypeRepository.findOne(dtVO.getBasedDtId());
                    List<DataTypeSupplementaryComponent> baseDTSCs = dao.findByOwnerDtId(defaultTextBDT.getDtId());

                    for (int i = 0; i < baseDTSCs.size(); i++) {
                        DataTypeSupplementaryComponent adtscVO = baseDTSCs.get(i);
                        List<BusinessDataTypeSupplementaryComponentPrimitiveRestriction> baseBDTSCPris = aBDTSCPrimitiveRestrictionDAO.findByBdtScId(adtscVO.getDtScId());

                        for (int j = 0; j < baseBDTSCPris.size(); j++) {
                            BusinessDataTypeSupplementaryComponentPrimitiveRestriction aBDTSCPri = baseBDTSCPris.get(j);
                            BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();

                            bVO.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());
                            bVO.setCdtScAwdPriXpsTypeMapId(aBDTSCPri.getCdtScAwdPriXpsTypeMapId());

                            CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap CDTSCAPX = aCDTSCAllowedPrimitiveExpressionTypeMapDAO.findOneByCdtScAwdPriXpsTypeMapId(aBDTSCPri.getCdtScAwdPriXpsTypeMapId());

                            XSDBuiltInType xbt = aXBTDAO.findOneByBuiltInType("xsd:token");

                            if (CDTSCAPX.getXbtId() == xbt.getXbtId()) {//if it is token
                                bVO.setDefault(true);
                            } else {
                                bVO.setDefault(false);
                            }

                            XSDBuiltInType xbtName = aXBTDAO.findOneByXbtId(CDTSCAPX.getXbtId());
                            System.out.println("     %%%%% Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " xbt = " + xbtName.getBuiltInType() + " is default = " + bVO.isDefault());
                            aBDTSCPrimitiveRestrictionDAO.save(bVO);
                        }
                    }

                    if (aDataTypeSupplementaryComponent.getPropertyTerm().equals("Language")) {
                        BusinessDataTypeSupplementaryComponentPrimitiveRestriction bLanguageVO = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
                        bLanguageVO.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());

                        CodeList clVO = codeListRepository.findOneByName("clm56392A20081107_LanguageCode");
                        bLanguageVO.setCodeListId(clVO.getCodeListId());
                        bLanguageVO.setDefault(false);
                        System.out.println("     %%%%% Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " code list id = " + bLanguageVO.getCodeListId() + " is default = " + bLanguageVO.isDefault());
                        aBDTSCPrimitiveRestrictionDAO.save(bLanguageVO);
                    } else if (aDataTypeSupplementaryComponent.getPropertyTerm().equals("Action Code")) {
                        BusinessDataTypeSupplementaryComponentPrimitiveRestriction bActionCodeVO = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
                        bActionCodeVO.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());

                        CodeList clVO = null;
                        if (dtVO.getDen().equals("Action Expression. Type")) {
                            clVO = codeListRepository.findOneByName("oacl_ActionCode");
                        } else if (dtVO.getDen().equals("Response Expression. Type")) {
                            clVO = codeListRepository.findOneByName("oacl_ResponseActionCode");
                        }

                        if (clVO != null) {
                            bActionCodeVO.setCodeListId(clVO.getCodeListId());
                            bActionCodeVO.setDefault(false);
                            System.out.println("     %%%%% Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " code list id = " + bActionCodeVO.getCodeListId() + " is default = " + bActionCodeVO.isDefault());
                            aBDTSCPrimitiveRestrictionDAO.save(bActionCodeVO);
                        }
                    }
                } else if ((aDataTypeSupplementaryComponent.getRepresentationTerm().contains("Code") && codeListId > 0) || ele_name.contains("AgencyID")) {

                    BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
                    bVO.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());
                    bVO.setDefault(false);

                    if (ele.getAttribute("name").contains("AgencyID")) {
                        bVO.setAgencyIdListId(getAgencyListID());
                        System.out.println("     $$$$$ Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " agency id list id = " + bVO.getAgencyIdListId() + " is default = " + bVO.isDefault());
                    } else {
                        bVO.setCodeListId(codeListId);
                        System.out.println("     $$$$$ Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " code list id = " + bVO.getCodeListId() + " is default = " + bVO.isDefault());
                    }
                    //System.out.println("Populating bdt sc primitive restriction for bdt sc = "+aDataTypeSupplementaryComponent.getPropertyTerm()+aDataTypeSupplementaryComponent.getRepresentationTerm()+" owner dt den = "+getDen(aDataTypeSupplementaryComponent.getOwnerDtId())+" code list id = "+bVO.getCodeListId()+" agency id list id = "+bVO.getAgencyIDListID()+ " is default = "+bVO.isDefault());
                    aBDTSCPrimitiveRestrictionDAO.save(bVO);

                    int CDT_Primitive_id = aCDTPrimitiveDAO.findOneByName("Token").getCdtPriId();

                    int xbt_id = aXBTDAO.findOneByBuiltInType("xsd:token").getXbtId();

                    int cdt_id = 0;
                    DataTypeSupplementaryComponent stscVO = null;
                    try {
                        stscVO = dao.findOneByDtScId(aDataTypeSupplementaryComponent.getBasedDtScId());
                    } catch (EmptyResultDataAccessException e) {
                        stscVO = new DataTypeSupplementaryComponent();
                    }
                    if (stscVO.getBasedDtScId() < 1) {
                        cdt_id = aDataTypeSupplementaryComponent.getBasedDtScId();
                    } else {
                        cdt_id = stscVO.getBasedDtScId();
                    }

                    int cdt_sc_awd_pri_id = 0;
                    if (cdt_id > 0) {
                        try {
                            cdt_sc_awd_pri_id = aCDTSCAllowedPrimitiveDAO.findOneByCdtScIdAndCdtPriId(cdt_id, CDT_Primitive_id).getCdtScAwdPriId();
                        } catch (EmptyResultDataAccessException e) {
                        }
                    }

                    int CDTSCAllowedPrimitiveExpressionTypeMapID = 0;
                    if (cdt_sc_awd_pri_id > 0) {
                        CDTSCAllowedPrimitiveExpressionTypeMapID = aCDTSCAllowedPrimitiveExpressionTypeMapDAO.
                                findOneByCdtScAwdPriAndXbtId(cdt_sc_awd_pri_id, xbt_id).getCdtScAwdPriXpsTypeMapId();
                    }

                    BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO1 = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
                    bVO1.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());

                    bVO1.setCdtScAwdPriXpsTypeMapId(CDTSCAllowedPrimitiveExpressionTypeMapID);
                    bVO1.setDefault(true);

                    CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCPAX = null;
                    if (CDTSCAllowedPrimitiveExpressionTypeMapID > 0) {
                        aCDTSCPAX = aCDTSCAllowedPrimitiveExpressionTypeMapDAO.findOneByCdtScAwdPriXpsTypeMapId(
                                bVO1.getCdtScAwdPriXpsTypeMapId());
                    }

                    XSDBuiltInType aXBT;
                    if (aCDTSCPAX == null) {
                        aXBT = new XSDBuiltInType();
                    } else {
                        aXBT = aXBTDAO.findOneByXbtId(aCDTSCPAX.getXbtId());
                    }
                    System.out.println("     $$$$$ Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " xbt = " + aXBT.getBuiltInType() + "  is default = " + bVO1.isDefault());
                    aBDTSCPrimitiveRestrictionDAO.save(bVO1);


//					QueryCondition qc021 = new QueryCondition();
//					qc021.add("dt_sc_id", aDataTypeSupplementaryComponent.getBasedDtScId());
//					DataTypeSupplementaryComponent stscVO = (DataTypeSupplementaryComponent)dao.findObject(qc021, conn);
//					int cdt_id = 0;
//					if(stscVO == null){ //New
//						QueryCondition qc031 = new QueryCondition();
//						qc031.add("name", ele.getAttribute("type").substring(0, ele.getAttribute("type").lastIndexOf("ContentType")));
//						CodeList codelistVO = (CodeList)aCodeListDAO.findObject(qc031, conn);
//						BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO1 = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
//						bVO1.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());
//						bVO1.setCodeListId(codelistVO.getCodeListId());
//						bVO1.setDefault(false);
//						System.out.println("Populating bdt sc primitive restriction for bdt sc = "+aDataTypeSupplementaryComponent.getPropertyTerm()+aDataTypeSupplementaryComponent.getRepresentationTerm()+" owner dt den = "+getDen(aDataTypeSupplementaryComponent.getOwnerDtId())+" code list id = "+bVO1.getCodeListId()+" code list name = "+codelistVO.getName()+"  is default = "+bVO1.isDefault());
//						aBDTSCPrimitiveRestrictionDAO.save(bVO1);
//					}
//					else {
//						if(stscVO.getBasedDtScId() < 1)
//							cdt_id = aDataTypeSupplementaryComponent.getBasedDtScId();
//						else
//							cdt_id = stscVO.getBasedDtScId();
//
//						QueryCondition qc03 = new QueryCondition();
//						qc03.add("cdt_sc_id", cdt_id);
//						qc03.add("CDT_Pri_id", CDT_Primitive_id);
//						int cdt_sc_awd_pri_id = ((CDTSCAllowedPrimitiveVO)aCDTSCAllowedPrimitiveDAO.findObject(qc03, conn)).getCDTSCAllowedPrimitiveID();
//
//						QueryCondition qc04 = new QueryCondition();
//						qc04.add("CDT_SC_awd_pri", cdt_sc_awd_pri_id);
//						qc04.add("xbt_id", xbt_id);
//						int CDTSCAllowedPrimitiveExpressionTypeMapID = ((CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap)aCDTSCAllowedPrimitiveExpressionTypeMapDAO.findObject(qc04, conn)).getCTSCAllowedPrimitiveExpressionTypeMapID();
//
//						BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO1 = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
//						bVO1.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());
//
//						bVO1.setCDTSCAllowedPrimitiveExpressionTypeMapID(CDTSCAllowedPrimitiveExpressionTypeMapID);
//						bVO1.setDefault(true);
//						System.out.println("Populating bdt sc primitive restriction for bdt sc = "+aDataTypeSupplementaryComponent.getPropertyTerm()+aDataTypeSupplementaryComponent.getRepresentationTerm()+" owner dt den = "+getDen(aDataTypeSupplementaryComponent.getOwnerDtId())+" cdt_sc_allowed_pri_xps_type_map_id = "+bVO1.getCDTSCAllowedPrimitiveExpressionTypeMapID()+"  is default = "+bVO1.isDefault());
//						aBDTSCPrimitiveRestrictionDAO.save(bVO1);
//					}
                } else {
                    List<CoreDataTypeSupplementaryComponentAllowedPrimitive> al3;
                    if (aDataTypeSupplementaryComponent.getBasedDtScId() < 1) { //new attributes SC
                        al3 = aCDTSCAllowedPrimitiveDAO.findByCdtScId(aDataTypeSupplementaryComponent.getDtScId());
                    } else { // DTSC is based on other dt_sc
                        al3 = aCDTSCAllowedPrimitiveDAO.findByCdtScId(aDataTypeSupplementaryComponent.getBasedDtScId());
                        if (al3.isEmpty()) {
                            DataTypeSupplementaryComponent dataTypeSupplementaryComponent = dao.findOneByDtScId(aDataTypeSupplementaryComponent.getBasedDtScId());
                            al3 = aCDTSCAllowedPrimitiveDAO.findByCdtScId(dataTypeSupplementaryComponent.getBasedDtScId());
                        }
                    }

                    for (CoreDataTypeSupplementaryComponentAllowedPrimitive aCDTSCAllowedPrimitiveVO : al3) {//Loop retrieved cdt_sc_awd_pri\
                        List<CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap> al4 =
                                aCDTSCAllowedPrimitiveExpressionTypeMapDAO.findByCdtScAwdPri(aCDTSCAllowedPrimitiveVO.getCdtScAwdPriId());
                        for (CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCAllowedPrimitiveExVO : al4) {

                            BusinessDataTypeSupplementaryComponentPrimitiveRestriction bVO = new BusinessDataTypeSupplementaryComponentPrimitiveRestriction();
                            bVO.setBdtScId(aDataTypeSupplementaryComponent.getDtScId());

                            bVO.setCdtScAwdPriXpsTypeMapId(aCDTSCAllowedPrimitiveExVO.getCdtScAwdPriXpsTypeMapId());


                            XSDBuiltInType xbtVO = getXbtId(aCDTSCAllowedPrimitiveExVO.getXbtId());
                            String xdtName = xbtVO.getBuiltInType();

                            int cdtPrimitiveId = aCDTSCAllowedPrimitiveVO.getCdtPriId();

                            String representationTerm = aDataTypeSupplementaryComponent.getRepresentationTerm();
                            if (representationTerm.equalsIgnoreCase("Code") && xdtName.equalsIgnoreCase("xsd:token") && "Token".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Identifier") && xdtName.equalsIgnoreCase("xsd:normalizedString") && "NormalizedString".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Name") && xdtName.equalsIgnoreCase("xsd:normalizedString") && "NormalizedString".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Indicator") && xdtName.equalsIgnoreCase("xsd:boolean") && "Boolean".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Value") && xdtName.equalsIgnoreCase("xsd:decimal") && "Decimal".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Text") && xdtName.equalsIgnoreCase("xsd:normalizedString") && "NormalizedString".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Number") && xdtName.equalsIgnoreCase("xsd:decimal") && "Decimal".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else if (representationTerm.equalsIgnoreCase("Date Time") && xdtName.equalsIgnoreCase("xsd:token") && "Timepoint".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                bVO.setDefault(true);
                            } else {
                                bVO.setDefault(false);
                            }

                            CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap aCDTSCPAX =
                                    aCDTSCAllowedPrimitiveExpressionTypeMapDAO.findOneByCdtScAwdPriXpsTypeMapId(bVO.getCdtScAwdPriXpsTypeMapId());


                            XSDBuiltInType aXBT = aXBTDAO.findOneByXbtId(aCDTSCPAX.getXbtId());

                            //exceptional case
                            if (ele != null && ele.getAttribute("type") != null && ele.getAttribute("type").equalsIgnoreCase("NumberType_B98233")) {
                                if (representationTerm.equalsIgnoreCase("Number") && xdtName.equalsIgnoreCase("xsd:integer") && "Integer".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                    bVO.setDefault(true);
                                    System.out.println("     ##### Populating bdt sc primitive restriction(NumberType_B98233) for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " xbt = " + aXBT.getBuiltInType() + "  is default = " + bVO.isDefault());
                                } else if (representationTerm.equalsIgnoreCase("Number") && xdtName.equalsIgnoreCase("xsd:decimal") && "Decimal".equalsIgnoreCase(getCDTPrimitiveName(cdtPrimitiveId))) {
                                    bVO.setDefault(false);
                                    System.out.println("     ##### Populating bdt sc primitive restriction(NumberType_B98233) for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " xbt = " + aXBT.getBuiltInType() + "  is default = " + bVO.isDefault());
                                }
                            }
                            //System.out.println("representation term = "+representationTerm+" xbt name = "+xdtName+" cdt primitive name = "+getCDTPrimitiveName(cdtPrimitiveId));
                            System.out.println("     ##### Populating bdt sc primitive restriction for bdt sc = " + aDataTypeSupplementaryComponent.getPropertyTerm() + aDataTypeSupplementaryComponent.getRepresentationTerm() + " owner dt den = " + getDen(aDataTypeSupplementaryComponent.getOwnerDtId()) + " xbt = " + aXBT.getBuiltInType() + "  is default = " + bVO.isDefault());
                            aBDTSCPrimitiveRestrictionDAO.save(bVO);
                        }
                    }
                }
            }
        }
    }

    public DataTypeSupplementaryComponent getDTSC(int id) throws Exception {
        DataTypeSupplementaryComponentRepository aDTDAO = repositoryFactory.dataTypeSupplementaryComponentRepository();
        return aDTDAO.findOneByDtScId(id);
    }

    public String getDen(int id) throws Exception {
        return dataTypeRepository.findOne(id).getDen();
    }

    public boolean isBDTSC(int id) throws Exception {
        DataType tmp = dataTypeRepository.findOne(id);

        if (tmp != null && tmp.getType() == 1)
            return true;
        return false;
    }

    public String getCDTPrimitiveName(int id) throws Exception {
        CoreDataTypePrimitiveRepository aCDTPrimitiveDAO = repositoryFactory.coreDataTypePrimitiveRepository();
        return aCDTPrimitiveDAO.findOneByCdtPriId(id).getName();
    }

    public XSDBuiltInType getXbtId(int id) throws Exception {
        XSDBuiltInTypeRepository aXSDBuiltInTypeDAO = repositoryFactory.xsdBuiltInTypeRepository();
        return aXSDBuiltInTypeDAO.findOneByXbtId(id);
    }

    public static void main(String args[]) throws Exception {
        try (AbstractApplicationContext ctx = (AbstractApplicationContext)
                SpringApplication.run(Application.class, args);) {
            P_1_5_6_PopulateBDTSCPrimitiveRestriction populateBDTSCPrimitiveRestriction = ctx.getBean(P_1_5_6_PopulateBDTSCPrimitiveRestriction.class);
            populateBDTSCPrimitiveRestriction.run(ctx);
        }
    }
}