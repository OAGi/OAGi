package org.oagi.srt.web.handler;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.common.util.Utility;
import org.oagi.srt.persistence.dao.DAOFactory;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.ABIEVO;
import org.oagi.srt.persistence.dto.ACCVO;
import org.oagi.srt.persistence.dto.ASBIEPVO;
import org.oagi.srt.persistence.dto.ASBIEVO;
import org.oagi.srt.persistence.dto.ASCCPVO;
import org.oagi.srt.persistence.dto.ASCCVO;
import org.oagi.srt.persistence.dto.BBIEPVO;
import org.oagi.srt.persistence.dto.BBIEVO;
import org.oagi.srt.persistence.dto.BBIE_SCVO;
import org.oagi.srt.persistence.dto.BCCPVO;
import org.oagi.srt.persistence.dto.BCCVO;
import org.oagi.srt.persistence.dto.BDTPrimitiveRestrictionVO;
import org.oagi.srt.persistence.dto.BusinessContextVO;
import org.oagi.srt.persistence.dto.BusinessContextValueVO;
import org.oagi.srt.persistence.dto.CDTAllowedPrimitiveExpressionTypeMapVO;
import org.oagi.srt.persistence.dto.CodeListVO;
import org.oagi.srt.persistence.dto.ContextCategoryVO;
import org.oagi.srt.persistence.dto.ContextSchemeValueVO;
import org.oagi.srt.persistence.dto.DTSCVO;
import org.oagi.srt.persistence.dto.DTVO;
import org.oagi.srt.persistence.dto.UserVO;
import org.oagi.srt.persistence.dto.XSDBuiltInTypeVO;
import org.oagi.srt.web.handler.BusinessContextHandler.BusinessContextValues;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean
@ViewScoped
public class TopLevelABIEHandler implements Serializable {

	private static final long serialVersionUID = -2650693005373031742L;
	
	private DAOFactory df;
	private SRTDAO dao;
	private SRTDAO asccDao;
	private SRTDAO bccDao;
	private SRTDAO accDao;
	private SRTDAO asccpDao;
	private SRTDAO bccpDao;
	private SRTDAO abieDao;
	private SRTDAO asbiepDao;
	private SRTDAO asbieDao;
	private SRTDAO bbiepDao;
	private SRTDAO bbieDao;
	private SRTDAO bbiescDao;
	private SRTDAO dtscDao;
	private SRTDAO dtDao;
	private SRTDAO daoBC;
	private SRTDAO daoBCV;
	private SRTDAO userDao;
	private SRTDAO bdtPrimitiveRestrictionDao;
	private SRTDAO daoCL;
	
	private int abieCount = 0;
	private int bbiescCount = 0;
	private int asbiepCount = 0;
	private int asbieCount = 0;
	private int bbiepCount = 0;
	private int bbieCount = 0;
	
	private BarChartModel barModel;
	
	private String propertyTerm;
	private List<SRTObject> asccpVOs;
	
	private ASCCPVO selected;
	private BusinessContextVO bCSelected;
	
	private int maxABIEId;
	private int maxASBIEPId;
	private int maxBIEPID;
	private int maxBIEID;
	private int maxBBIESCID;
	
	private Connection conn = null;

	@PostConstruct
	private void init() {
		try {
			df = DAOFactory.getDAOFactory();
			dao = df.getDAO("ASCCP");
			asccDao = df.getDAO("ASCC");
			bccDao = df.getDAO("BCC");
			accDao = df.getDAO("ACC");
			abieDao = df.getDAO("ABIE");
			asbiepDao = df.getDAO("ASBIEP");
			asbieDao = df.getDAO("ASBIE");
			asccpDao = df.getDAO("ASCCP");
			bccpDao = df.getDAO("BCCP");
			bbiepDao = df.getDAO("BBIEP");
			bbieDao = df.getDAO("BBIE");
			bbiescDao = df.getDAO("BBIE_SC");
			dtscDao = df.getDAO("DTSC");
			dtDao = df.getDAO("DT");
			daoBC = df.getDAO("BusinessContext");
			daoBCV = df.getDAO("BusinessContextValue");
			userDao = df.getDAO("User");
			bdtPrimitiveRestrictionDao = df.getDAO("BDTPrimitiveRestriction");
			daoCL = df.getDAO("CodeList");
			
			maxABIEId = asbieDao.findMaxId();
			maxASBIEPId = asbiepDao.findMaxId();
			maxBIEPID = bbiepDao.findMaxId();
			maxBIEID = bbieDao.findMaxId();
			maxBBIESCID = bbiescDao.findMaxId();
			
			try {
				asccpVOs = dao.findObjects();
			} catch (SRTDAOException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BarChartModel getBarModel() {
        return barModel;
    }
	
	private int barCount = 20;
	
	private int getMax() {
		int max = 0;
		if(abieCount > max)
			max = abieCount;
		if(bbiescCount > max)
			max = bbiescCount;
		if(asbiepCount > max)
			max = asbiepCount;
		if(asbieCount > max)
			max = asbieCount;
		if(bbiepCount > max)
			max = bbiepCount;
		if(bbieCount > max)
			max = bbieCount;
		return max;
	}
	
	private void createBarModel() {
		barCount = getMax();
				
        barModel = initBarModel();
         
        barModel.setTitle("Number of items created");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Tables");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("");
        yAxis.setMin(0);
        yAxis.setMax(barCount + barCount/10);
        yAxis.setTickInterval(String.valueOf(barCount/10));
    }
	
	
	private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries tabie = new ChartSeries();
        tabie.setLabel("ABIE");
        tabie.set("", abieCount);
        
        ChartSeries tasbiep = new ChartSeries();
        tasbiep.setLabel("ASBIEP");
        tasbiep.set("", asbiepCount);
        
        ChartSeries tasbie = new ChartSeries();
        tasbie.setLabel("ASBIE");
        tasbie.set("", asbieCount);
        
        ChartSeries tbbie = new ChartSeries();
        tbbie.setLabel("BBIE");
        tbbie.set("", bbieCount);
        
        ChartSeries tbbiep = new ChartSeries();
        tbbiep.setLabel("BBIEP");
        tbbiep.set("", bbiepCount);

        ChartSeries tbbiesc = new ChartSeries();
        tbbiesc.setLabel("BBIE_SC");
        tbbiesc.set("", bbiescCount);
 
        model.addSeries(tabie);
        model.addSeries(tasbiep);
        model.addSeries(tasbie);
        
        model.addSeries(tbbie);
        model.addSeries(tbbiep);
        model.addSeries(tbbiesc);
         
        return model;
    }
	
	public void itemSelect(ItemSelectEvent event) {
		String str = "";
		switch (event.getSeriesIndex()) {
			case 0:
				str = "ABIE: " +  abieCount;
				break;
			case 1:
				str = "ASBIEP: " +  asbiepCount;
				break;
			case 2:
				str = "ASBIE: " +  asbieCount;
				break;
			case 3:
				str = "BBIE: " +  bbieCount;
				break;
			case 4:
				str = "BBIEP: " +  bbiepCount;
				break;
			case 5:
				str = "BBIE_SC: " +  bbiescCount;
				break;
			default:
				str = "";
				break;
		}
			
        FacesMessage msg = new FacesMessage(str,
                        "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
         
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	private boolean skip;
	
	private List<ABIEView> bodList = new ArrayList<ABIEView>();
	private ABIEView selectedBod;
	
	public ABIEView getSelectedBod() {
		return selectedBod;
	}

	public void setSelectedBod(ABIEView selectedBod) {
		this.selectedBod = selectedBod;
	}

	public List<ABIEView> getBodList() {
		if(bodList.size() == 0) {
			QueryCondition qc = new QueryCondition();
			qc.add("isTop_level", 1);
			
			try {
				List<SRTObject> list = abieDao.findObjects(qc);
				
				for(SRTObject obj : list) {
					ABIEVO abieVO = (ABIEVO)obj;
					QueryCondition qc_01 = new QueryCondition();
					qc_01.add("role_of_abie_id", abieVO.getABIEID());
					ASBIEPVO asbiepVO = (ASBIEPVO)asbiepDao.findObject(qc_01);
					
					QueryCondition qc_02 = new QueryCondition();
					qc_02.add("asccp_id", asbiepVO.getBasedASCCPID());
					ASCCPVO asccpVO = (ASCCPVO)asccpDao.findObject(qc_02);
					
					QueryCondition qc_03 = new QueryCondition();
					qc_03.add("business_context_id", abieVO.getBusinessContextID());
					BusinessContextVO aBusinessContextVO = (BusinessContextVO)daoBC.findObject(qc_03);
					abieVO.setBusinessContextName(aBusinessContextVO.getName());
					
					ABIEView aABIEView = new ABIEView(asccpVO.getPropertyTerm(), abieVO.getABIEID(), "ABIE");
					aABIEView.setAbieVO(abieVO);
					bodList.add(aABIEView);
				}
				
				//root = new DefaultTreeNode(aABIEView, null);
				
			} catch (SRTDAOException e) {
				e.printStackTrace();
			}
		}
		return bodList;
	}

	public void setBodList(List<ABIEView> bodList) {
		this.bodList = bodList;
	}

	public void save() {        
		FacesMessage msg = new FacesMessage("Successful", "Welcome :" + "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public void search() {
		try {
			QueryCondition qc = new QueryCondition();
			qc.addLikeClause("Property_Term", "%" + getPropertyTerm() + "%");
			asccpVOs = dao.findObjects(qc);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}

		//return asccpVOs;
	}
	
	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	private ABIEVO topAbieVO;
	private ASBIEPVO asbiepVO;
	private BBIEVO bieVO;
	
	public String onFlowProcess(FlowEvent event) {
		
		if(event.getNewStep().equals(SRTConstants.TAB_TOP_LEVEL_ABIE_SELECT_BC)) {
			
		} else if(event.getNewStep().equals(SRTConstants.TAB_TOP_LEVEL_ABIE_CREATE_UC_BIE)) {
			// TODO if go back from the confirmation page? avoid that situation
			
			DBAgent tx = new DBAgent();
			try {
				conn = tx.open();
				
				QueryCondition qc = new QueryCondition();
				qc.add("acc_id", selected.getRoleOfACCID());
				ACCVO accVO = (ACCVO)accDao.findObject(qc, conn);
				topAbieVO = createABIE(accVO, bCSelected.getBusinessContextID(), 1, 0);
				// int abieId = getABIEID("abie_guid", abieVO.getAbieGUID());
				int abieId = topAbieVO.getABIEID();
				
				ABIEView rootABIEView = new ABIEView(selected.getPropertyTerm(), abieId, "ABIE");
				rootABIEView.setAbieVO(topAbieVO);
				root = new DefaultTreeNode(rootABIEView, null);
				
				asbiepVO = createASBIEP(selected, abieId, -1);
				
				aABIEView = new ABIEView(selected.getPropertyTerm(), abieId, "ABIE");
				aABIEView.setAbieVO(topAbieVO);
				aABIEView.setColor("blue");
				aABIEView.setAccVO(accVO);
				aABIEView.setAsbiepVO(asbiepVO);
				TreeNode toplevelNode = new DefaultTreeNode(aABIEView, root);
				
				createBIEs(selected.getRoleOfACCID(), abieId, -1, toplevelNode);
				
				createBarModel();
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				tx.close();
			}
		} 
		
		return event.getNewStep();
	}
	
	
	public String onFlowProcess_Copy(FlowEvent event) {
		
		if(event.getNewStep().equals(SRTConstants.TAB_TOP_LEVEL_ABIE_SELECT_BC)) {
			
		} else if(event.getNewStep().equals(SRTConstants.TAB_TOP_LEVEL_ABIE_COPY_UC_BIE)) {
			// TODO if go back from the confirmation page? avoid that situation
			
			DBAgent tx = new DBAgent();
			try {
				
				conn = tx.open();
				QueryCondition qc = new QueryCondition();
				ABIEVO selected_abievo = selectedBod.getAbieVO();
				qc.add("ACC_ID", selected_abievo.getBasedACCID());
				ACCVO accVO = (ACCVO)accDao.findObject(qc, conn);
				QueryCondition qc2 = new QueryCondition();
				qc2.add("Role_Of_ACC_ID", accVO.getACCID());
				ASCCPVO asccpVO = (ASCCPVO)asccpDao.findObject(qc2, conn);
				topAbieVO = copyABIE(selected_abievo, bCSelected.getBusinessContextID(), 1);
				int abieId = topAbieVO.getABIEID();
				
				ABIEView rootABIEView = new ABIEView(asccpVO.getPropertyTerm(), abieId, "ABIE");
				rootABIEView.setAbieVO(topAbieVO);
				root = new DefaultTreeNode(rootABIEView, null);
				QueryCondition qc3 = new QueryCondition();
				qc3.add("Role_Of_ABIE_ID", selected_abievo.getABIEID());
				ASBIEPVO oasbiepvo = (ASBIEPVO)asbiepDao.findObject(qc3, conn);
				ASBIEPVO nasbiepvo = copyASBIEP(oasbiepvo, abieId);
				aABIEView = new ABIEView(asccpVO.getPropertyTerm(), abieId, "ABIE");
				aABIEView.setAbieVO(topAbieVO);
				aABIEView.setColor("blue");
				//aABIEView.setAccVO(accVO);
				aABIEView.setAsbiepVO(nasbiepvo);
				TreeNode toplevelNode = new DefaultTreeNode(aABIEView, root);
				
				copyBIEs(selected_abievo, topAbieVO, nasbiepvo, -1, toplevelNode);
				
				createBarModel();
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				tx.close();
			}
		} 
		
		return event.getNewStep();
	}
	
	public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Progress Completed"));
    }
	
	private Integer progress;
	 
    public Integer getProgress() {
        if(progress == null) {
            progress = 0;
        }
        else {
            progress = progress + (int)(Math.random() * 35);
             
            if(progress > 100)
                progress = 100;
        }
         
        return progress;
    }
 
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
	
	private void createBIEs(int accId, int abie, int groupPosition, TreeNode tNode) throws SRTDAOException {
		Stack<ACCVO> accList = new Stack<ACCVO>();
		ACCVO acc = getACC(accId);
		accList.push(acc);
		while(acc.getBasedACCID() > 0) {
			acc = getACC(acc.getBasedACCID());
			accList.push(acc);
		}
		
		int seq_base = 1;
		while(!accList.isEmpty()) {
			ACCVO accVO = accList.pop();
			
			ArrayList<SRTObject> bccObjects = getBCC(accVO.getACCID());
			for(SRTObject bccObject : bccObjects) {
				BCCVO bccVO = (BCCVO)bccObject;
				//BCCPVO bccpVO = getBCCP(bccVO.getAssocToBCCPID());
				
				String seqKey = "";
				if(groupPosition > 0) { // Group
					seqKey = groupPosition + "." + bccVO.getSequencingKey();
				} else { // not group
					seqKey = seq_base + "." + bccVO.getSequencingKey();
				}
				
				QueryCondition qc = new QueryCondition();
				qc.add("bccp_id", bccVO.getAssocToBCCPID());
				BCCPVO bccpVO = (BCCPVO)bccpDao.findObject(qc, conn);
				
				BBIEPVO bbiepVO = createBBIEP(bccpVO, abie);
				
				QueryCondition qc_02 = new QueryCondition();
				qc_02.add("bdt_id", bccpVO.getBDTID());
				qc_02.add("isDefault", 1);
				BDTPrimitiveRestrictionVO aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)bdtPrimitiveRestrictionDao.findObject(qc_02, conn);
				int bdtPrimitiveRestrictionId = aBDTPrimitiveRestrictionVO.getBDTPrimitiveRestrictionID();
				
				BBIEVO bbieVO = createBBIE(bccVO, abie, bbiepVO.getBBIEPID(), seqKey, bdtPrimitiveRestrictionId);
				
				ABIEView av = new ABIEView(bccpVO.getPropertyTerm(), bbieVO.getBBIEID(), "BBIE");
				av.setBccVO(bccVO);
				av.setBbiepVO(bbiepVO);
				av.setBbieVO(bbieVO);
				av.setBccpVO(bccpVO);
				
				QueryCondition qc_01 = new QueryCondition();
				qc_01.add("dt_id", bccpVO.getBDTID());
				DTVO dtVO = (DTVO)dtDao.findObject(qc_01, conn);
				av.setBdtName(dtVO.getDEN());
				
				av.setColor("green");
				TreeNode tNode2 = new DefaultTreeNode(av, tNode);
				
				int bbieID = bbieVO.getBBIEID();
				createBBIESC(bbieID, bccpVO.getBDTID(), tNode2);
			}
			
			ArrayList<SRTObject> asccObjects = getASCC(accVO.getACCID());
			for(SRTObject asccObject : asccObjects) {
				ASCCVO asccVO = (ASCCVO)asccObject;
				ASCCPVO asccpVO = getASCCP(asccVO.getAssocToASCCPID());
				ACCVO accVOFromASCCP = getACC(asccpVO.getRoleOfACCID());
				
				if(accVOFromASCCP.getOAGISComponentType() == 3) {
					createBIEs(accVOFromASCCP.getACCID(), abie, seq_base, tNode);
				} else {
					ABIEVO abieVO = createABIE(accVOFromASCCP, bCSelected.getBusinessContextID(), 0, abie);
					ASBIEPVO asbiepVO = createASBIEP(asccpVO, abieVO.getABIEID(), abie);
					
					String seqKey = "";
					if(groupPosition > 0) { // Group
						seqKey = groupPosition + "." + asccVO.getSequencingKey();
					} else { // not group
						seqKey = seq_base + "." + asccVO.getSequencingKey();
					}
					
					ASBIEVO asbieVO = createASBIE(asccVO, abie, asbiepVO.getASBIEPID(), seqKey);
					
					ABIEView av = new ABIEView(asccpVO.getPropertyTerm(), abieVO.getABIEID(), "ASBIE");
					av.setColor("blue");
					av.setAsccVO(asccVO);
					av.setAsccpVO(asccpVO);
					av.setAccVO(accVOFromASCCP);
					
					av.setAbieVO(abieVO);
					av.setAsbiepVO(asbiepVO);
					av.setAsbieVO(asbieVO);
					TreeNode tNode2 = new DefaultTreeNode(av, tNode);
					
					createBIEs(accVOFromASCCP.getACCID(), abieVO.getABIEID(), -1, tNode2);
				}
			}
			seq_base++;
		}
		

	}
	
	private ArrayList<SRTObject> getBCC(int accId) {
		QueryCondition qc = new QueryCondition();
		qc.add("assoc_from_acc_id", accId);
		ArrayList<SRTObject> res = new ArrayList<SRTObject>();
		try {
			res = bccDao.findObjects(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private ArrayList<SRTObject> getASCC(int accId) {
		QueryCondition qc = new QueryCondition();
		qc.add("assoc_from_acc_id", accId);
		ArrayList<SRTObject> res = new ArrayList<SRTObject>();
		try {
			res = asccDao.findObjects(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private ASCCPVO getASCCP(int asccpId) {
		QueryCondition qc = new QueryCondition();
		qc.add("asccp_id", asccpId);
		ASCCPVO asccpVO = null;
		try {
			asccpVO = (ASCCPVO)asccpDao.findObject(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return asccpVO;
	}
	
	private BCCPVO getBCCP(int bccpId) {
		QueryCondition qc = new QueryCondition();
		qc.add("bccp_id", bccpId);
		BCCPVO bccpVO = null;
		try {
			bccpVO = (BCCPVO)bccpDao.findObject(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return bccpVO;
	}
	
	private ACCVO getACC(int accId) {
		QueryCondition qc = new QueryCondition();
		qc.add("ACC_ID", accId);
		ACCVO accVO = null;
		try {
			accVO = (ACCVO)accDao.findObject(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return accVO;
	}
	
	private ArrayList<SRTObject> getASBIEsFromABIE(int abieid) {
		QueryCondition qc = new QueryCondition();
		qc.add("Assoc_From_ABIE_ID", abieid);
		ArrayList<SRTObject> asbieVO = null;
		try {
			asbieVO = (ArrayList<SRTObject>)asbieDao.findObjects(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return asbieVO;
	}
	
	private ArrayList<SRTObject> getBBIEsFromABIE(int abieid) {
		QueryCondition qc = new QueryCondition();
		qc.add("Assoc_From_ABIE_ID", abieid);
		ArrayList<SRTObject> bbieVO = null;
		try {
			bbieVO = (ArrayList<SRTObject>)bbieDao.findObjects(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return bbieVO;
	}
	
	private ArrayList<SRTObject> getBBIESCsFromBBIE(int bbieid) {
		QueryCondition qc = new QueryCondition();
		qc.add("BBIE_ID", bbieid);
		ArrayList<SRTObject> bbiescVO = null;
		try {
			bbiescVO = (ArrayList<SRTObject>)bbiescDao.findObjects(qc, conn);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return bbiescVO;
	}
	
	
	private ABIEVO createABIE(ACCVO accVO, int bc, int topLevel, int abie) throws SRTDAOException {
		ABIEVO abieVO = new ABIEVO();
		String abieGuid = Utility.generateGUID();
		abieVO.setAbieGUID(abieGuid);
		abieVO.setBasedACCID(accVO.getACCID());
		abieVO.setDefinition(accVO.getDefinition());
		abieVO.setIsTopLevel(topLevel);
		abieVO.setBusinessContextID(bc);
		int userId = getUserId();
		abieVO.setCreatedByUserID(userId); 
		abieVO.setLastUpdatedByUserID(userId); 
		if(topLevel == 1)
			abieVO.setState(SRTConstants.TOP_LEVEL_ABIE_STATE_EDITING);
		//abieVO.setABIEID(Utility.getRandomID(maxABIEId));
		try {
			int key = abieDao.insertObject(abieVO, conn);
			abieVO.setABIEID(key);
			abieCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//abieCount++;
		return abieVO;
	}
	
	private ASBIEPVO createASBIEP(ASCCPVO asccpVO, int tAabie, int gAabie) throws SRTDAOException {
		ASBIEPVO asbiepVO = new ASBIEPVO();
		asbiepVO.setASBIEPGUID(Utility.generateGUID());
		asbiepVO.setBasedASCCPID(asccpVO.getASCCPID());
		asbiepVO.setRoleOfABIEID(tAabie);
		int userId = getUserId();
		asbiepVO.setCreatedByUserID(userId); 
		asbiepVO.setLastUpdatedByUserID(userId); 
		asbiepVO.setDefinition(asccpVO.getDefinition());
		//asbiepVO.setASBIEPID(Utility.getRandomID(maxASBIEPId));
		try {
			int key = asbiepDao.insertObject(asbiepVO, conn);
			asbiepVO.setASBIEPID(key);
			asbiepCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//asbiepCount++;
		
		return asbiepVO;
	}

	private ASBIEVO createASBIE(ASCCVO asccVO, int abie, int asbiep, String seqKey) throws SRTDAOException {
		ASBIEVO asbieVO = new ASBIEVO();
		asbieVO.setAsbieGuid(Utility.generateGUID());
		asbieVO.setAssocFromABIEID(abie);
		asbieVO.setAssocToASBIEPID(asbiep);
		asbieVO.setBasedASCC(asccVO.getASCCID());
		asbieVO.setCardinalityMax(asccVO.getCardinalityMax());
		asbieVO.setCardinalityMin(asccVO.getCardinalityMin());
		asbieVO.setDefinition(asccVO.getDefinition());
		int userId = getUserId();
		asbieVO.setCreatedByUserId(userId); 
		asbieVO.setLastUpdatedByUserId(userId); 
		asbieVO.setSequencingKey(Double.parseDouble(seqKey));
		try {
			int key = asbieDao.insertObject(asbieVO, conn);
			asbieVO.setASBIEID(key);
			asbieCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		//asbieCount++;
		return asbieVO;
	}
	
	private BBIEPVO createBBIEP(BCCPVO bccpVO, int abie) throws SRTDAOException {
		BBIEPVO bbiepVO = new BBIEPVO();
		bbiepVO.setBBIEPGUID(Utility.generateGUID());
		bbiepVO.setBasedBCCPID(bccpVO.getBCCPID());
		int userId = getUserId();
		bbiepVO.setCreatedByUserID(userId); 
		bbiepVO.setLastUpdatedbyUserID(userId); 
		bbiepVO.setDefinition(bccpVO.getDefinition());
		//bbiepVO.setBBIEPID(Utility.getRandomID(maxBIEPID));
		
		try {
			int key = bbiepDao.insertObject(bbiepVO, conn);
			bbiepVO.setBBIEPID(key);
			bbiepCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//bbiepCount++;
		return bbiepVO;
	}
	
	
	private BBIEVO createBBIE(BCCVO bccVO, int abie, int bbiep, String seqKey, int bdtPrimitiveRestrictionId) throws SRTDAOException {
		BBIEVO bbieVO = new BBIEVO();
		bbieVO.setBbieGuid(Utility.generateGUID());
		bbieVO.setBasedBCCID(bccVO.getBCCID());
		bbieVO.setAssocFromABIEID(abie);
		bbieVO.setAssocToBBIEPID(bbiep);
		bbieVO.setNillable(0);
		bbieVO.setCardinalityMax(bccVO.getCardinalityMax());
		bbieVO.setCardinalityMin(bccVO.getCardinalityMin());
		bbieVO.setBdtPrimitiveRestrictionId(bdtPrimitiveRestrictionId);
		int userId = getUserId();
		bbieVO.setCreatedByUserId(userId); 
		bbieVO.setLastUpdatedByUserId(userId); 
		//bbieVO.setBBIEID(Utility.getRandomID(maxBIEID));
		bbieVO.setSequencing_key(Double.parseDouble(seqKey));
		
		try {
			int key = bbieDao.insertObject(bbieVO, conn);
			bbieVO.setBBIEID(key);
			bbieCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//bbieCount++;
		return bbieVO;
	}
	
	private void createBBIESC(int bbie, int bdt, TreeNode tNode) {
		QueryCondition qc = new QueryCondition();
		qc.add("owner_dt_id", bdt);
		try {
			List<SRTObject> list = dtscDao.findObjects(qc, conn);
			HashMap<String, String> hm = new HashMap<String, String>();
			for(SRTObject obj : list) {
				DTSCVO dtsc = (DTSCVO) obj;
				BBIE_SCVO bbiescVO = new BBIE_SCVO();
				bbiescVO.setBBIEID(bbie);
				bbiescVO.setDTSCID(dtsc.getDTSCID()); 
				//bbiescVO.setBBIESCID(Utility.getRandomID(maxBBIESCID));
				
				int key = bbiescDao.insertObject(bbiescVO, conn);
				bbiescCount++;
				
				ABIEView av = new ABIEView(dtsc.getPropertyTerm(), key, "BBIESC");
				bbiescVO.setMaxCardinality(dtsc.getMaxCardinality());
				bbiescVO.setMinCardinality(dtsc.getMinCardinality());
				bbiescVO.setDefinition(dtsc.getDefinition());
				av.setDtscVO(dtsc);
				
				av.setBbiescVO(bbiescVO);
				av.setColor("orange");
				TreeNode tNode1 = new DefaultTreeNode(av, tNode);
				
				hm.put(dtsc.getPropertyTerm(), dtsc.getDTSCGUID());
				
			}

		} catch (SRTDAOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private void copyBIEs(ABIEVO oabieVO, ABIEVO nabieVO, ASBIEPVO nasbiepVO, int groupPosition, TreeNode tNode) throws SRTDAOException {
		ArrayList<SRTObject> bbie = getBBIEsFromABIE(oabieVO.getABIEID());
		for(SRTObject bSRTObject : bbie){
			BBIEVO oBBIEVO = (BBIEVO)bSRTObject;
			
			QueryCondition qc_11 = new QueryCondition();
			qc_11.add("BBIEP_ID", oBBIEVO.getAssocToBBIEPID());
			BBIEPVO oBBIEPVO = (BBIEPVO)bbiepDao.findObject(qc_11, conn);
			BBIEPVO nBBIEPVO = copyBBIEP(oBBIEPVO); 
			BBIEVO nBBIEVO = copyBBIE(oBBIEVO, nabieVO.getABIEID(), nBBIEPVO.getBBIEPID());
			
			QueryCondition qc_12 = new QueryCondition();
			qc_12.add("BCCP_ID", nBBIEPVO.getBasedBCCPID());
			BCCPVO bccpVO = (BCCPVO)bccpDao.findObject(qc_12, conn);
			
			ABIEView av = new ABIEView(bccpVO.getPropertyTerm(), nBBIEVO.getBBIEID(), "BBIE");
			av.setBbiepVO(nBBIEPVO);
			av.setBbieVO(nBBIEVO);
			av.setBccpVO(bccpVO);
			
			QueryCondition qc_13 = new QueryCondition();
			qc_13.add("dt_id", bccpVO.getBDTID());
			DTVO dtVO = (DTVO)dtDao.findObject(qc_13, conn);
			av.setBdtName(dtVO.getDEN());
			av.setColor("green");
			TreeNode tNode2 = new DefaultTreeNode(av, tNode);
			ArrayList<SRTObject> bbiesc = getBBIESCsFromBBIE(oBBIEVO.getBBIEID());
			for(SRTObject bbSRTObject : bbiesc){
				BBIE_SCVO oBBIESCVO = (BBIE_SCVO)bbSRTObject;
				copyBBIESC(oBBIESCVO, nBBIEVO.getBBIEID(), tNode2);
			}			
		}
		ArrayList<SRTObject> asbie = getASBIEsFromABIE(oabieVO.getABIEID());
		for(SRTObject aSRTObject : asbie){
			ASBIEVO oASBIEVO = (ASBIEVO)aSRTObject;
	
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("ASBIEP_ID", oASBIEVO.getAssocToASBIEPID());
			ASBIEPVO o_next_asbiepVO = (ASBIEPVO)asbiepDao.findObject(qc_01, conn);
			QueryCondition qc_02 = new QueryCondition();
			qc_02.add("ABIE_ID", o_next_asbiepVO.getRoleOfABIEID());
			ABIEVO o_next_abieVO = (ABIEVO)abieDao.findObject(qc_02, conn);
			ABIEVO n_next_abieVO = copyABIE(o_next_abieVO, bCSelected.getBusinessContextID(), 0);
			ASBIEPVO n_next_asbiepVO = copyASBIEP(o_next_asbiepVO, n_next_abieVO.getABIEID());
			ASBIEVO nASBIEVO = copyASBIE(oASBIEVO, nabieVO.getABIEID(), n_next_asbiepVO.getASBIEPID());
		
			QueryCondition qc_03 = new QueryCondition();
			qc_03.add("ASCCP_ID", n_next_asbiepVO.getBasedASCCPID());
			ASCCPVO asccpVO = (ASCCPVO)asccpDao.findObject(qc_03, conn);

			ABIEView av = new ABIEView(asccpVO.getPropertyTerm(), nabieVO.getABIEID(), "ASBIE");
			av.setColor("blue");
			av.setAbieVO(n_next_abieVO);
			av.setAsbieVO(nASBIEVO);
			av.setAsbiepVO(n_next_asbiepVO);
			TreeNode tNode2 = new DefaultTreeNode(av, tNode);
			copyBIEs(o_next_abieVO, n_next_abieVO, n_next_asbiepVO, -1, tNode2);
		}				
	}
	
	private ABIEVO copyABIE(ABIEVO oabieVO, int bc, int topLevel) throws SRTDAOException {
		ABIEVO abieVO = new ABIEVO();
		String abieGuid = Utility.generateGUID();
		abieVO.setAbieGUID(abieGuid);
		abieVO.setBasedACCID(oabieVO.getBasedACCID());
		abieVO.setDefinition(oabieVO.getDefinition());
		abieVO.setIsTopLevel(topLevel);
		abieVO.setBusinessContextID(bc);
		abieVO.setClientID(oabieVO.getClientID());
		abieVO.setVersion(oabieVO.getVersion());
		abieVO.setStatus(oabieVO.getStatus());
		abieVO.setRemark(oabieVO.getRemark());
		abieVO.setBusinessTerm(oabieVO.getBusinessTerm());
		int userId = getUserId();
		abieVO.setCreatedByUserID(userId); 
		abieVO.setLastUpdatedByUserID(userId); 
		if(topLevel == 1)
			abieVO.setState(SRTConstants.TOP_LEVEL_ABIE_STATE_EDITING);
		//abieVO.setABIEID(Utility.getRandomID(maxABIEId));
		try {
			int key = abieDao.insertObject(abieVO, conn);
			abieVO.setABIEID(key);
			abieCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//abieCount++;
		return abieVO;
	}
	
		private ASBIEPVO copyASBIEP(ASBIEPVO oasbiepVO, int tAabie) throws SRTDAOException {
		ASBIEPVO asbiepVO = new ASBIEPVO();
		asbiepVO.setASBIEPGUID(Utility.generateGUID());
		asbiepVO.setBasedASCCPID(oasbiepVO.getBasedASCCPID());
		asbiepVO.setRoleOfABIEID(tAabie);
		int userId = getUserId();
		asbiepVO.setCreatedByUserID(userId); 
		asbiepVO.setLastUpdatedByUserID(userId); 
		asbiepVO.setDefinition(oasbiepVO.getDefinition());
		//asbiepVO.setASBIEPID(Utility.getRandomID(maxASBIEPId));
		try {
			int key = asbiepDao.insertObject(asbiepVO, conn);
			asbiepVO.setASBIEPID(key);
			asbiepCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//asbiepCount++;
		
		return asbiepVO;
	}
	
	private ASBIEVO copyASBIE(ASBIEVO oasbieVO, int abieid, int asbiepid) throws SRTDAOException {
			ASBIEVO asbieVO = new ASBIEVO();
			asbieVO.setAsbieGuid(Utility.generateGUID());
			asbieVO.setAssocFromABIEID(abieid);
			asbieVO.setAssocToASBIEPID(asbiepid);
			asbieVO.setBasedASCC(oasbieVO.getBasedASCC());
			asbieVO.setCardinalityMax(oasbieVO.getCardinalityMax());
			asbieVO.setCardinalityMin(oasbieVO.getCardinalityMin());
			asbieVO.setDefinition(oasbieVO.getDefinition());
			int userId = getUserId();
			asbieVO.setCreatedByUserId(userId); 
			asbieVO.setLastUpdatedByUserId(userId); 
			asbieVO.setSequencingKey(oasbieVO.getSequencingKey());
			try {
				int key = asbieDao.insertObject(asbieVO, conn);
				asbieVO.setASBIEID(key);
				asbieCount++;
			} catch (SRTDAOException e) {
				e.printStackTrace();
			}
			//asbieCount++;
			return asbieVO;
	}
	
	private BBIEPVO copyBBIEP(BBIEPVO obbiepVO) throws SRTDAOException {
		BBIEPVO nbbiepVO = new BBIEPVO();
		nbbiepVO.setBasedBCCPID(obbiepVO.getBasedBCCPID());
		nbbiepVO.setDefinition(obbiepVO.getDefinition());
		nbbiepVO.setRemark(obbiepVO.getRemark());
		nbbiepVO.setBusinessTerm(obbiepVO.getBusinessTerm());
		nbbiepVO.setBBIEPGUID(Utility.generateGUID());
		int userId = getUserId();
		nbbiepVO.setCreatedByUserID(userId); 
		nbbiepVO.setLastUpdatedbyUserID(userId); 
		
		try {
			int key = bbiepDao.insertObject(nbbiepVO, conn);
			nbbiepVO.setBBIEPID(key);
			bbiepCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//bbiepCount++;
		return nbbiepVO;
	}
	
	private BBIEVO copyBBIE(BBIEVO obbieVO, int abie, int bbiep) throws SRTDAOException {
		BBIEVO nbbieVO = new BBIEVO();
		nbbieVO.setBasedBCCID(obbieVO.getBasedBCCID());
		nbbieVO.setBdtPrimitiveRestrictionId(obbieVO.getBdtPrimitiveRestrictionId());
		nbbieVO.setCodeListId(obbieVO.getCodeListId());
		nbbieVO.setCardinalityMax(obbieVO.getCardinalityMax());
		nbbieVO.setCardinalityMin(obbieVO.getCardinalityMin());
		nbbieVO.setDefaultText(obbieVO.getDefaultText());
		nbbieVO.setNillable(obbieVO.getNillable());
		nbbieVO.setFixedValue(obbieVO.getFixedValue());
		nbbieVO.setIsNull(obbieVO.getIsNull());
		nbbieVO.setDefinition(obbieVO.getDefinition());
		nbbieVO.setRemark(obbieVO.getRemark());

		nbbieVO.setBbieGuid(Utility.generateGUID());
		nbbieVO.setAssocFromABIEID(abie);
		nbbieVO.setAssocToBBIEPID(bbiep);//come back
		int userId = getUserId();
		nbbieVO.setCreatedByUserId(userId); 
		nbbieVO.setLastUpdatedByUserId(userId); 
		try {
			int key = bbieDao.insertObject(nbbieVO, conn);
			nbbieVO.setBBIEID(key);
			bbieCount++;
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		//bbieCount++;
		return nbbieVO;
	}
	
	private void copyBBIESC(BBIE_SCVO obbiescvo, int bbie, TreeNode tNode) {
		try {
			BBIE_SCVO nbbiescVO = new BBIE_SCVO();
			nbbiescVO.setDTSCID(obbiescvo.getDTSCID());
			nbbiescVO.setDTSCPrimitiveRestrictionID(obbiescvo.getDTSCPrimitiveRestrictionID());
			nbbiescVO.setCodeListId(obbiescvo.getCodeListId());
			nbbiescVO.setAgencyIdListId(obbiescvo.getAgencyIdListId());
			nbbiescVO.setMaxCardinality(obbiescvo.getMaxCardinality());
			nbbiescVO.setMinCardinality(obbiescvo.getMinCardinality());
			nbbiescVO.setDefaultText(obbiescvo.getDefaultText());
			nbbiescVO.setFixedValue(obbiescvo.getFixedValue());
			nbbiescVO.setDefinition(obbiescvo.getDefinition());
			nbbiescVO.setRemark(obbiescvo.getRemark());
			nbbiescVO.setBusinessTerm(obbiescvo.getBusinessTerm());

			nbbiescVO.setBBIEID(bbie);
			int key = bbiescDao.insertObject(nbbiescVO, conn);
			bbiescCount++;
			
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("DT_SC_ID", nbbiescVO.getDTSCID());
			DTSCVO dtscvo = (DTSCVO)dtscDao.findObject(qc_01, conn);
			
			ABIEView av = new ABIEView(dtscvo.getPropertyTerm(), key, "BBIESC");
			//av.setDtscVO(dtscvo);
			av.setBbiescVO(nbbiescVO);
			av.setColor("orange");
			TreeNode tNode1 = new DefaultTreeNode(av, tNode);
			} catch (SRTDAOException e1) {
			e1.printStackTrace();
		}
	}
	
	public List<SRTObject> getAsccpVOs() {
		return asccpVOs;
	}
	
	public List<String> completeInput(String query) {
		List<String> results = new ArrayList<String>();

		try {
			asccpVOs = dao.findObjects();
			for(SRTObject obj : asccpVOs) {
				ASCCPVO ccVO = (ASCCPVO)obj;
				if(ccVO.getPropertyTerm().contains(query)) {
					results.add(ccVO.getPropertyTerm());
				}
			}
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public ASCCPVO getSelected() {
        return selected;
    }
 
    public void setSelected(ASCCPVO selected) {
        this.selected = selected;
    }
    
	public BusinessContextVO getbCSelected() {
		return bCSelected;
	}

	public void setbCSelected(BusinessContextVO bCSelected) {
		this.bCSelected = bCSelected;
	}

	public String getPropertyTerm() {
		return propertyTerm;
	}

	public void setPropertyTerm(String propertyTerm) {
		this.propertyTerm = propertyTerm;
	}
	
	public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(((ASCCPVO) event.getObject()).getPropertyTerm(), String.valueOf(((ASCCPVO) event.getObject()).getASCCPID()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selected = (ASCCPVO) event.getObject();
    }
	
	public void onBODRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(((ABIEView) event.getObject()).getName(), String.valueOf(((ABIEView) event.getObject()).getName()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedBod = (ABIEView) event.getObject();
        //root = new DefaultTreeNode(selectedBod, null);
        
        System.out.println("#### " + selectedBod.getName());
        ABIEView rootABIEView = new ABIEView(selectedBod.getName(), selectedBod.getId(), "ROOT");
		rootABIEView.setAbieVO(selectedBod.getAbieVO());
		root = new DefaultTreeNode(rootABIEView, null);
		
		aABIEView = new ABIEView(selectedBod.getName(), selectedBod.getId(), "ABIE");
		aABIEView.setAbieVO(selectedBod.getAbieVO());
		aABIEView.setColor("blue");
		TreeNode toplevelNode = new DefaultTreeNode(aABIEView, root);
        
        try {
			createBIEChildren(selectedBod.getId(), toplevelNode);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
    }
	
	private void createBIEChildren(int abieId, TreeNode tNode) throws SRTDAOException {
		
		QueryCondition qc_01 = new QueryCondition();
		qc_01.add("assoc_from_abie_id", abieId);
		List<SRTObject> list_01 = bbieDao.findObjects(qc_01);
		for(SRTObject obj : list_01) {
			BBIEVO bbieVO = (BBIEVO)obj;
			
			QueryCondition qc_02 = new QueryCondition();
			qc_02.add("bbiep_id", bbieVO.getAssocToBBIEPID());
			BBIEPVO bbiepVO = (BBIEPVO)bbiepDao.findObject(qc_02);
			
			QueryCondition qc_03 = new QueryCondition();
			qc_03.add("bccp_id", bbiepVO.getBasedBCCPID());
			BCCPVO bccpVO = (BCCPVO)bccpDao.findObject(qc_03);
			
			QueryCondition qc_04 = new QueryCondition();
			qc_04.add("bcc_id", bbieVO.getBasedBCCID());
			BCCVO bccVO = (BCCVO)bccDao.findObject(qc_04);
			
			ABIEView av = new ABIEView(bccpVO.getPropertyTerm(), bbieVO.getBBIEID(), "BBIE");
			av.setBccVO(bccVO);
			av.setBbiepVO(bbiepVO);
			av.setBbieVO(bbieVO);
			av.setBccpVO(bccpVO);
			
			QueryCondition qc_05 = new QueryCondition();
			qc_05.add("dt_id", bccpVO.getBDTID());
			DTVO dtVO = (DTVO)dtDao.findObject(qc_05);
			av.setBdtName(dtVO.getDEN());
			
			av.setColor("green");
			TreeNode tNode2 = new DefaultTreeNode(av, tNode);
		}
		
		QueryCondition qc_02 = new QueryCondition();
		qc_02.add("assoc_from_abie_id", abieId);
		List<SRTObject> list_02 = asbieDao.findObjects(qc_02);
		for(SRTObject obj : list_02) {
			ASBIEVO asbieVO = (ASBIEVO)obj;
			
			QueryCondition qc_03 = new QueryCondition();
			qc_03.add("asbiep_id", asbieVO.getAssocToASBIEPID());
			ASBIEPVO asbiepVO = (ASBIEPVO)asbiepDao.findObject(qc_03);
			
			QueryCondition qc_04 = new QueryCondition();
			qc_04.add("asccp_id", asbiepVO.getBasedASCCPID());
			ASCCPVO asccpVO = (ASCCPVO)asccpDao.findObject(qc_04);
			
			QueryCondition qc_05 = new QueryCondition();
			qc_05.add("ascc_id", asbieVO.getBasedASCC());
			ASCCVO asccVO = (ASCCVO)asccDao.findObject(qc_05);
			
			QueryCondition qc_06 = new QueryCondition();
			qc_06.add("abie_id", asbiepVO.getRoleOfABIEID());
			ABIEVO abieVO = (ABIEVO)abieDao.findObject(qc_06);
			
			QueryCondition qc_07 = new QueryCondition();
			qc_07.add("acc_id", abieVO.getBasedACCID());
			ACCVO accVO = (ACCVO)accDao.findObject(qc_07);
			
			ABIEView av = new ABIEView(asccpVO.getPropertyTerm(), abieVO.getABIEID(), "ASBIE");
			av.setColor("blue");
			av.setAsccVO(asccVO);
			av.setAsccpVO(asccpVO);
			av.setAccVO(accVO);
			av.setAbieVO(abieVO);
			av.setAsbiepVO(asbiepVO);
			av.setAsbieVO(asbieVO);
			TreeNode tNode2 = new DefaultTreeNode(av, tNode);
			
			//createBIEChildren(abieVO.getABIEID(), tNode2);
		}
	}
	
	private void createBBIESCChild(BBIEVO bbieVO, TreeNode parent) {
		try {
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("bbie_id", bbieVO.getBBIEID());
			List<SRTObject> list_01 = bbiescDao.findObjects(qc_01);
			
			for(SRTObject obj : list_01) {
				BBIE_SCVO bbiescVO = (BBIE_SCVO)obj;
				
				QueryCondition qc_02 = new QueryCondition();
				qc_02.add("dt_sc_id", bbiescVO.getDTSCID());
				DTSCVO dtscVO = (DTSCVO)dtscDao.findObject(qc_02);
				
				ABIEView av_01 = new ABIEView(dtscVO.getPropertyTerm(), bbiescVO.getBBIESCID(), "BBIESC");
				av_01.setDtscVO(dtscVO);
				av_01.setBbiescVO(bbiescVO);
				av_01.setColor("orange");
				TreeNode tNode1 = new DefaultTreeNode(av_01, parent);
			}
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	private HashSet<Integer> opnedNodes = new HashSet<Integer>();
	
	public void expand() {
		try {
			ABIEView abieView = (ABIEView)selectedTreeNode.getData();
			if(!opnedNodes.contains(abieView.getId())) {
				opnedNodes.add(abieView.getId());
				if("ASBIE".equalsIgnoreCase(abieView.getType()))
					createBIEChildren(abieView.getAbieVO().getABIEID(), selectedTreeNode);
				else if("BBIE".equalsIgnoreCase(abieView.getType()))
						createBBIESCChild(abieView.getBbieVO(), selectedTreeNode);
			}
			
			aABIEView = abieView;
			codeListVO = null;
			
			if(aABIEView.getType().equalsIgnoreCase("BBIE")) {
				try {
					aABIEView.getBdtPrimitiveRestrictions();
					
					QueryCondition qc_01 = new QueryCondition();
					qc_01.add("bdt_primitive_restriction_id", aABIEView.getBdtPrimitiveRestrictionId());
					BDTPrimitiveRestrictionVO  aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)bdtPrimitiveRestrictionDao.findObject(qc_01);
					
					QueryCondition qc = new QueryCondition();
			        qc.add("based_code_list_id", aBDTPrimitiveRestrictionVO.getCodeListID());
			        codeLists = daoCL.findObjects(qc);
			        
				} catch (SRTDAOException e) {
					e.printStackTrace();
				}
			}
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	public void onEdit() {
		FacesContext context = FacesContext.getCurrentInstance();
	    String objectId = context.getExternalContext().getRequestParameterMap().get("objectId");
	    System.out.println("###objectId " + objectId);
//	    
//	    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//		try {
//			System.out.println("### " + selectedBod.getName());
//			
//			ABIEView av = new ABIEView("AAA", 1, "ABIE");
//			root = new DefaultTreeNode(av, null);
//			ABIEView av1 = new ABIEView("AAA", 1, "ABIE");
//			selectedBod
//			DefaultTreeNode node = new DefaultTreeNode(av1, root);
//			context.setRequest(root);
//			context.redirect(context.getRequestContextPath() + "/top_level_abie_edit.xhtml");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
    public void onBCRowSelect(SelectEvent event) {
        System.out.println(((BusinessContextVO) event.getObject()).getBusinessContextID() + "Business context Selected");
    }
    
	
	public void onBCSelect(BusinessContextVO bcVO) {
		bCSelected = bcVO;
		FacesMessage msg = new FacesMessage(bCSelected.getName(), bCSelected.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        System.out.println(bCSelected.getBusinessContextID() +" is selected");
    }
	
	public void onBCSelect(BusinessContextHandler bcH) {
		try {
			BusinessContextVO bcVO = new BusinessContextVO();
			bcVO.setName(bcH.getName());
			String guid = Utility.generateGUID();
			bcVO.setBusinessContextGUID(guid);
			daoBC.insertObject(bcVO);
			
			QueryCondition qc = new QueryCondition();
			qc.add("Business_Context_GUID", guid);
			BusinessContextVO bvVO1 = (BusinessContextVO)daoBC.findObject(qc);
			
			for(BusinessContextValues bcv : bcH.getBcValues()) {
				for(ContextSchemeValueVO cVO : bcv.getCsList()) {
					BusinessContextValueVO bcvVO = new BusinessContextValueVO();
					bcvVO.setBusinessContextID(bvVO1.getBusinessContextID());
					bcvVO.setContextSchemeValueID(cVO.getContextSchemeValueID());
					daoBCV.insertObject(bcvVO);
				}
			}
			
			bCSelected = bcVO;
			FacesMessage msg = new FacesMessage(bCSelected.getName(), bCSelected.getName());
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Item Unselected", String.valueOf(((ASCCPVO) event.getObject()).getASCCPID()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void onBODRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Item Unselected", String.valueOf(((ABIEView) event.getObject()).getName()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
    private ABIEView selectedDocument;
         
    private TreeNode root;
     
    public TreeNode getRoot() {
    	if(root == null)
    		root = new DefaultTreeNode();
        return root;
    }
 
    public ABIEView getSelectedDocument() {
        return selectedDocument;
    }
 
    public void setSelectedDocument(ABIEView selectedDocument) {
        this.selectedDocument = selectedDocument;
    }
    
    private int getUserId() throws SRTDAOException {
    	QueryCondition qc = new QueryCondition();
    	qc.add("user_name", "oagis");
    	return ((UserVO)userDao.findObject(qc, conn)).getUserID();
    }
    
    private int min;
    
    public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	private TreeNode selectedTreeNode;
	
	private ABIEView aABIEView;
	
	public ABIEView getaABIEView() {
		if(aABIEView == null)
			aABIEView = new ABIEView();
		return aABIEView;
	}

	public void setaABIEView(ABIEView aABIEView) {
		this.aABIEView = aABIEView;
	}

	public TreeNode getSelectedTreeNode() {
		if(selectedTreeNode == null)
			selectedTreeNode = root;
		
		aABIEView = (ABIEView)selectedTreeNode.getData();
		return selectedTreeNode;
    }
 
    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }

	public void updateTree() {
		ABIEView aABIEView = (ABIEView)selectedTreeNode.getData();
		System.out.println("### " + aABIEView.getName());
    }
	
	public void showDetails() {
		aABIEView = (ABIEView)selectedTreeNode.getData();
		codeListVO = null;
		
		if(aABIEView.getType().equalsIgnoreCase("BBIE")) {
			try {
				aABIEView.getBdtPrimitiveRestrictions();
				
				QueryCondition qc_01 = new QueryCondition();
				qc_01.add("bdt_primitive_restriction_id", aABIEView.getBdtPrimitiveRestrictionId());
				BDTPrimitiveRestrictionVO  aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)bdtPrimitiveRestrictionDao.findObject(qc_01);
				
				QueryCondition qc = new QueryCondition();
		        qc.add("based_code_list_id", aBDTPrimitiveRestrictionVO.getCodeListID());
		        codeLists = daoCL.findObjects(qc);
		        
			} catch (SRTDAOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveChanges() {
		aABIEView = (ABIEView)selectedTreeNode.getData();
		if(aABIEView.getType().equals("ASBIE")) {
			saveASBIEChanges(aABIEView);
		} else if(aABIEView.getType().equals("BBIE")) {
			saveBBIEChanges(aABIEView);
		} else if(aABIEView.getType().equals("BBIESC")) {
			saveBBIESCChanges(aABIEView);
		} else if(aABIEView.getType().equals("ABIE")) {
			saveABIEChanges(aABIEView);
		}
		
		FacesMessage msg = new FacesMessage("Changes on '" + aABIEView.getName() + "' are just saved!", "Changes on '" + aABIEView.getName() + "' are just saved!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	private void saveBBIEChanges(ABIEView aABIEView) {
		BBIEVO bbieVO = aABIEView.getBbieVO();
		BBIEPVO bbiepVO = aABIEView.getBbiepVO();
		try {
			
			if(aABIEView.getRestrictionType().equalsIgnoreCase("Primitive")) {
				bbieVO.setBdtPrimitiveRestrictionId(aABIEView.getBdtPrimitiveRestrictionId());
				bbieVO.setCodeListId(0);
			} else if(aABIEView.getRestrictionType().equalsIgnoreCase("Code")) {
				if(codeListVO != null) {
					bbieVO.setCodeListId(codeListVO.getCodeListID());
					bbieVO.setBdtPrimitiveRestrictionId(0);
				}
			}
			bbieDao.updateObject(bbieVO);
			bbiepDao.updateObject(bbiepVO);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	public void chooseCodeForTLBIE() {
		Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 800);
        RequestContext.getCurrentInstance().openDialog("top_level_abie_create_code_select", options, null);
    }
	
	private List<SRTObject> codeLists = new ArrayList<SRTObject>();
	
	private List<SRTObject> codeLists2 = new ArrayList<SRTObject>();
	
	public List<SRTObject> getCodeLists() {
		return codeLists;
	}

	public void setCodeLists(List<SRTObject> codeLists) {
		this.codeLists = codeLists;
	}
	
	public List<SRTObject> getCodeLists2() {
		return codeLists2;
	}

	public void setCodeLists2(List<SRTObject> codeLists2) {
		this.codeLists2 = codeLists2;
	}

	public void chooseDerivedCodeForTLBIE(int bdtPrimitiveRestrictionId) {
		Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 800);
        
		try {
			
			QueryCondition qc_01 = new QueryCondition();
			qc_01.add("bdt_primitive_restriction_id", bdtPrimitiveRestrictionId);
			BDTPrimitiveRestrictionVO  aBDTPrimitiveRestrictionVO = (BDTPrimitiveRestrictionVO)bdtPrimitiveRestrictionDao.findObject(qc_01);
			
			QueryCondition qc = new QueryCondition();
	        qc.add("based_code_list_id", aBDTPrimitiveRestrictionVO.getCodeListID());
	        codeLists = daoCL.findObjects(qc);
	        
	        System.out.println("##### " + codeLists);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.openDialog("top_level_abie_create_derived_code_select", options, null);
    }
	
	CodeListVO codeListVO;
	CodeListVO selectedCodeList;
	
	public CodeListVO getSelectedCodeList() {
		return selectedCodeList;
	}

	public void setSelectedCodeList(CodeListVO selectedCodeList) {
		this.selectedCodeList = selectedCodeList;
	}

	public void onCodeListRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage(((CodeListVO) event.getObject()).getName(), String.valueOf(((CodeListVO) event.getObject()).getCodeListID()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        codeListVO = (CodeListVO) event.getObject();
        System.out.println("######## " + codeListVO.getName());
    }
 
    public void onCodeListRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Item Unselected", String.valueOf(((CodeListVO) event.getObject()).getCodeListID()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //codeListVO = null;
    }
	
	public CodeListVO getCodeListVO() {
		return codeListVO;
	}

	public void setCodeListVO(CodeListVO codeListVO) {
		this.codeListVO = codeListVO;
	}

	public void onCodeListChosen(SelectEvent event) {
		CodeListHandler ch = (CodeListHandler) event.getObject();
		codeListVO = (CodeListVO)ch.getSelected();
		System.out.println(codeListVO.getName());
    }
	
	public void onDerivedCodeListChosen(SelectEvent event) {
		TopLevelABIEHandler ch = (TopLevelABIEHandler) event.getObject();
		codeListVO = (CodeListVO)ch.getSelectedCodeList();
    }
	
	String codeListName;
	
	public String getCodeListName() {
		return codeListName;
	}

	public void setCodeListName(String codeListName) {
		this.codeListName = codeListName;
	}
	
	public void searchCodeList() {
		try {
			QueryCondition qc = new QueryCondition();
			qc.addLikeClause("name", "%" + getCodeListName() + "%");
			qc.add("state", SRTConstants.CODE_LIST_STATE_PUBLISHED);
			qc.add("extensible_indicator", 1);
			codeLists2 = daoCL.findObjects(qc);
			if(codeLists2.size() == 0) {
				FacesMessage msg = new FacesMessage("[" + getCodeListName() + "] No such Code List exists or not yet published or not extensible", "[" + getCodeListName() + "] No such Code List exists or not yet published or not extensible");
		        FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> completeCodeListInput(String query) {
		List<String> results = new ArrayList<String>();

		try {
			QueryCondition qc = new QueryCondition();
			qc.addLikeClause("name", "%" + query + "%");
			codeLists2 = daoCL.findObjects(qc);
			for(SRTObject obj : codeLists2) {
				CodeListVO clVO = (CodeListVO)obj;
				results.add(clVO.getName());
			}
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private void saveBBIESCChanges(ABIEView aABIEView) {
		BBIE_SCVO bbiescVO = aABIEView.getBbiescVO();
		try {
			bbiescDao.updateObject(bbiescVO);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeDialog() {
        RequestContext.getCurrentInstance().closeDialog(this);
    }
	
	private void saveASBIEChanges(ABIEView aABIEView) {
		ASBIEVO asbieVO = aABIEView.getAsbieVO();
		ABIEVO abieVO = aABIEView.getAbieVO();
		ASBIEPVO asbiepVO = aABIEView.getAsbiepVO();
		
		try {
			asbieDao.updateObject(asbieVO);
			asbiepDao.updateObject(asbiepVO);
			abieDao.updateObject(abieVO);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveABIEChanges(ABIEView aABIEView) {
		ABIEVO abieVO = aABIEView.getAbieVO();
		try {
			abieDao.updateObject(abieVO);
		} catch (SRTDAOException e) {
			e.printStackTrace();
		}
	}
	
}