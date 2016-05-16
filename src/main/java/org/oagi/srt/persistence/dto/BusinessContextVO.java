package org.oagi.srt.persistence.dto;

import org.oagi.srt.common.SRTObject;
import org.oagi.srt.repository.entity.BusinessContext;

import java.sql.Timestamp;

/**
 *
 * @version 1.0
 * @author Nasif Sikder
 */
public class BusinessContextVO extends SRTObject {

	private int businessContextID;
	private String businessContextGUID;
	private String name;
	private int createdByUserId;
	private int lastUpdatedByUserId;
	private Timestamp CreationTimestamp;
	private Timestamp LastUpdateTimestamp;
	
	public int getBusinessContextID(){
		return businessContextID;
	}
	
	public void setBusinessContextID(int businessContextID){
		this.businessContextID = businessContextID;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getBusinessContextGUID() {
		return businessContextGUID;
	}

	public void setBusinessContextGUID(String businessContextGUID) {
		this.businessContextGUID = businessContextGUID;
	}

	public int getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(int createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public int getLastUpdatedByUserId() {
		return lastUpdatedByUserId;
	}

	public void setLastUpdatedByUserId(int lastUpdatedByUserId) {
		this.lastUpdatedByUserId = lastUpdatedByUserId;
	}
	
	public Timestamp getCreationTimestamp() {
		return CreationTimestamp;
	}
	
	public void setCreationTimestamp(Timestamp creationTimestamp) {
		CreationTimestamp = creationTimestamp;
	}
	
	public Timestamp getLastUpdateTimestamp() {
		return LastUpdateTimestamp;
	}
	
	public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
		LastUpdateTimestamp = lastUpdateTimestamp;
	}

	public static BusinessContextVO valueOf(BusinessContext businessContext) {
		BusinessContextVO businessContextVO = new BusinessContextVO();
		businessContextVO.setBusinessContextID(businessContext.getBizCtxId());
		businessContextVO.setBusinessContextGUID(businessContext.getGuid());
		businessContextVO.setName(businessContext.getName());
		businessContextVO.setCreatedByUserId(businessContext.getCreatedBy());
		businessContextVO.setLastUpdatedByUserId(businessContext.getLastUpdatedBy());
		businessContextVO.setCreationTimestamp(new Timestamp(businessContext.getCreationTimestamp().getTime()));
		businessContextVO.setLastUpdateTimestamp(new Timestamp(businessContext.getLastUpdateTimestamp().getTime()));
		return businessContextVO;
	}
}