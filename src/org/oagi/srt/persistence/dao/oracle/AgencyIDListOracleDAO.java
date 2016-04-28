package org.oagi.srt.persistence.dao.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.AgencyIDListVO;

/**
*
* @author Jaehun Lee
* @version 1.0
*
*/

public class AgencyIDListOracleDAO extends SRTDAO {
	private final String _tableName = "agency_id_list";
	
	private final String _FIND_ALL_Agency_ID_List_STATEMENT =
			"SELECT Agency_ID_List_ID, GUID, Enum_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition "
			+ "FROM " + _tableName;
	
	private final String _FIND_Agency_ID_List_STATEMENT =
			"SELECT Agency_ID_List_ID, GUID, Enum_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition "
			+ "FROM " + _tableName;
	
	private final String _INSERT_Agency_ID_List_STATEMENT = 
			"INSERT INTO " + _tableName + " (GUID, Enum_Type_GUID, Name, List_ID, Agency_ID, Version_ID, Definition)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	private final String _UPDATE_Agency_ID_List_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET GUID = ?,"
			+ " Enum_Type_GUID = ?, Name = ?, List_ID = ?, Agency_ID = ?,"
			+ " Version_ID = ?, Definition = ? WHERE Agency_ID_List_ID = ?";
	
	private final String _DELETE_Agency_ID_List_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE Agency_ID_List_ID = ?";
	
	@Override
	public int findMaxId() throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_Agency_ID_List_STATEMENT);
			if( agencyidlistVO.getAgencyIDListGUID()==null ||  agencyidlistVO.getAgencyIDListGUID().length()==0 ||  agencyidlistVO.getAgencyIDListGUID().isEmpty() ||  agencyidlistVO.getAgencyIDListGUID().equals(""))				
				ps.setString(1,"\u00A0");
			else 	
				ps.setString(1, agencyidlistVO.getAgencyIDListGUID());

			if( agencyidlistVO.getEnumerationTypeGUID()==null ||  agencyidlistVO.getEnumerationTypeGUID().length()==0 ||  agencyidlistVO.getEnumerationTypeGUID().isEmpty() ||  agencyidlistVO.getEnumerationTypeGUID().equals(""))				
				ps.setString(2,"\u00A0");
			else 	
				ps.setString(2, agencyidlistVO.getEnumerationTypeGUID());

//			if( agencyidlistVO.getName()==null ||  agencyidlistVO.getName().length()==0 ||  agencyidlistVO.getName().isEmpty() ||  agencyidlistVO.getName().equals(""))				
//				ps.setString(3,"\u00A0");
//			else 	
				ps.setString(3, agencyidlistVO.getName());

//			if( agencyidlistVO.getListID()==null ||  agencyidlistVO.getListID().length()==0 ||  agencyidlistVO.getListID().isEmpty() ||  agencyidlistVO.getListID().equals(""))				
//				ps.setString(4,"\u00A0");
//			else 	
				ps.setString(4, agencyidlistVO.getListID());

			//ps.setInt(5, agencyidlistVO.getAgencyID());
			if(agencyidlistVO.getAgencyID() < 1){
				ps.setNull(5, java.sql.Types.INTEGER);
			}
			else {
				ps.setInt(5, agencyidlistVO.getAgencyID());
			}
//			if( agencyidlistVO.getVersionID()==null ||  agencyidlistVO.getVersionID().length()==0 ||  agencyidlistVO.getVersionID().isEmpty() ||  agencyidlistVO.getVersionID().equals(""))				
//				ps.setString(6,"\u00A0");
//			else 	
				ps.setString(6, agencyidlistVO.getVersionID());

//			if(agencyidlistVO.getDefinition()==null || agencyidlistVO.getDefinition().length()==0 || agencyidlistVO.getDefinition().isEmpty() || agencyidlistVO.getDefinition().equals("")){
//				ps.setString(7, "\u00A0");
//			}
//			else {
				String s = StringUtils.abbreviate(agencyidlistVO.getDefinition(), 4000);
				ps.setString(7, s);
//			}
			ps.executeUpdate();
//			ResultSet tableKeys = ps.getGeneratedKeys();
//			tableKeys.next();
//			int autoGeneratedID = tableKeys.getInt(1);
			ps.close();
			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.DAO_INSERT_ERROR, e);
		} catch (SQLException e) {
			e.printStackTrace();
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			tx.close();
		}
		return 1;

	}

	public SRTObject findObject(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		AgencyIDListVO agencyidlistVO = new AgencyIDListVO();
		try {
			Connection conn = tx.open();
			String sql = _FIND_Agency_ID_List_STATEMENT;

			String WHERE_OR_AND = " WHERE ";
			int nCond = qc.getSize();
			if (nCond > 0) {
				for (int n = 0; n < nCond; n++) {
					sql += WHERE_OR_AND + qc.getField(n) + " = ?";
					WHERE_OR_AND = " AND ";
				}
			}
			ps = conn.prepareStatement(sql);
			if (nCond > 0) {
				for (int n = 0; n < nCond; n++) {
					Object value = qc.getValue(n);
					if (value instanceof String) {
						ps.setString(n+1, (String) value);
					} else if (value instanceof Integer) {
						ps.setInt(n+1, ((Integer) value).intValue());
					}
				}
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				agencyidlistVO.setAgencyIDListID(rs.getInt("Agency_ID_List_ID"));
				agencyidlistVO.setAgencyIDListGUID(rs.getString("GUID"));
				agencyidlistVO.setEnumerationTypeGUID(rs.getString("Enum_Type_GUID"));
				agencyidlistVO.setName(rs.getString("Name"));
				agencyidlistVO.setListID(rs.getString("List_ID"));
				agencyidlistVO.setAgencyID(rs.getInt("Agency_ID"));
				agencyidlistVO.setVersionID(rs.getString("Version_ID"));
				agencyidlistVO.setDefinition(rs.getString("Definition"));
			}
			tx.commit();
			conn.close();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}
		return agencyidlistVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_Agency_ID_List_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				AgencyIDListVO agencyidlistVO = new AgencyIDListVO();
				agencyidlistVO.setAgencyIDListID(rs.getInt("Agency_ID_List_ID"));
				agencyidlistVO.setAgencyIDListGUID(rs.getString("GUID"));
				agencyidlistVO.setEnumerationTypeGUID(rs.getString("Enum_Type_GUID"));
				agencyidlistVO.setName(rs.getString("Name"));
				agencyidlistVO.setListID(rs.getString("List_ID"));
				agencyidlistVO.setAgencyID(rs.getInt("Agency_ID"));
				agencyidlistVO.setVersionID(rs.getString("Version_ID"));
				agencyidlistVO.setDefinition(rs.getString("Definition"));
				list.add(agencyidlistVO);
			}
			tx.commit();
			conn.close();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return list;
			
	}

	public boolean updateObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_Agency_ID_List_STATEMENT);
			if( agencyidlistVO.getAgencyIDListGUID()==null ||  agencyidlistVO.getAgencyIDListGUID().length()==0 ||  agencyidlistVO.getAgencyIDListGUID().isEmpty() ||  agencyidlistVO.getAgencyIDListGUID().equals(""))				
				ps.setString(1,"\u00A0");
			else 	
				ps.setString(1, agencyidlistVO.getAgencyIDListGUID());

			if( agencyidlistVO.getEnumerationTypeGUID()==null ||  agencyidlistVO.getEnumerationTypeGUID().length()==0 ||  agencyidlistVO.getEnumerationTypeGUID().isEmpty() ||  agencyidlistVO.getEnumerationTypeGUID().equals(""))				
				ps.setString(2,"\u00A0");
			else 	
				ps.setString(2, agencyidlistVO.getEnumerationTypeGUID());

//			if( agencyidlistVO.getName()==null ||  agencyidlistVO.getName().length()==0 ||  agencyidlistVO.getName().isEmpty() ||  agencyidlistVO.getName().equals(""))				
//				ps.setString(3,"\u00A0");
//			else 	
				ps.setString(3, agencyidlistVO.getName());

//			if( agencyidlistVO.getListID()==null ||  agencyidlistVO.getListID().length()==0 ||  agencyidlistVO.getListID().isEmpty() ||  agencyidlistVO.getListID().equals(""))				
//				ps.setString(4,"\u00A0");
//			else 	
				ps.setString(4, agencyidlistVO.getListID());
			if(agencyidlistVO.getAgencyID() < 1){
				ps.setNull(5, java.sql.Types.INTEGER);
			}
			else {
				ps.setInt(5, agencyidlistVO.getAgencyID());
			}
//			if( agencyidlistVO.getVersionID()==null ||  agencyidlistVO.getVersionID().length()==0 ||  agencyidlistVO.getVersionID().isEmpty() ||  agencyidlistVO.getVersionID().equals(""))				
//				ps.setString(6,"\u00A0");
//			else 	
				ps.setString(6, agencyidlistVO.getVersionID());
//			if( agencyidlistVO.getDefinition()==null ||  agencyidlistVO.getDefinition().length()==0 ||  agencyidlistVO.getDefinition().isEmpty() ||  agencyidlistVO.getDefinition().equals(""))				
//				ps.setString(7,"\u00A0");
//			else 	{
				String s = StringUtils.abbreviate(agencyidlistVO.getDefinition(), 4000);
				ps.setString(7, s);
//			}
			ps.setInt(8, agencyidlistVO.getAgencyIDListID());
			ps.executeUpdate();

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.DAO_UPDATE_ERROR, e);
		} catch (SQLException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return true;
	}

	
	public boolean deleteObject(SRTObject obj) throws SRTDAOException {
		AgencyIDListVO agencyidlistVO = (AgencyIDListVO) obj;
		
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_Agency_ID_List_STATEMENT);
			ps.setInt(1, agencyidlistVO.getAgencyIDListID());
			ps.executeUpdate();

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.DAO_DELETE_ERROR, e);
		} catch (SQLException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			tx.close();
		}

		return true;


	}

	@Override
	public ArrayList<SRTObject> findObjects(QueryCondition qc)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRTObject findObject(QueryCondition qc, Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SRTObject> findObjects(QueryCondition qc, Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SRTObject> findObjects(Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertObject(SRTObject obj, Connection conn)
			throws SRTDAOException {
		// TODO Auto-generated method stub
		return 0;
	}
}
