package org.oagi.srt.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.BCCPVO;

/**
 *
 * @author Jaehun Lee
 * @version 1.0
 *
 */
public class BCCPMysqlDAO extends SRTDAO {

	private final String _tableName = "bccp";

	private final String _FIND_ALL_BCCP_STATEMENT = 
			"SELECT BCCP_ID, BCCP_GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp FROM " + _tableName;

	private final String _FIND_BCCP_STATEMENT = 
			"SELECT BCCP_ID, BCCP_GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp FROM " + _tableName;

	private final String _INSERT_BCCP_STATEMENT = 
			"INSERT INTO " + _tableName + " (BCCP_GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By_User_ID, Last_Updated_By_User_ID, "
					+ "Creation_Timestamp, Last_Update_Timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

	private final String _UPDATE_BCCP_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET Last_Update_Timestamp = CURRENT_TIMESTAMP, BCCP_GUID = ?, Property_Term = ?, Representation_Term = ?, BDT_ID = ?, "
			+ "Den = ?, Definition = ?, Created_By_User_ID = ?, Last_Updated_By_User_ID = ?, "
			+ "Creation_Timestamp = ? WHERE BCCP_ID = ?";

	private final String _DELETE_BCCP_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE BCCP_ID = ?";

	public boolean insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		BCCPVO bccpVO = (BCCPVO)obj;
		try {
			Connection conn = tx.open();
			PreparedStatement ps = null;
			ps = conn.prepareStatement(_INSERT_BCCP_STATEMENT);
			ps.setString(1, bccpVO.getBCCPGUID());
			ps.setString(2, bccpVO.getPropertyTerm());
			ps.setString(3, bccpVO.getRepresentationTerm());
			ps.setInt(4, bccpVO.getBDTID());
			ps.setString(5, bccpVO.getDEN());
			if(bccpVO.getDefinition() == null)
				ps.setString(6, "");
			else
				ps.setString(6, bccpVO.getDefinition());
			ps.setInt(7, bccpVO.getCreatedByUserId());
			ps.setInt(8, bccpVO.getLastUpdatedByUserId());

			ps.executeUpdate();

			//ResultSet tableKeys = ps.getGeneratedKeys();
			//tableKeys.next();
			//int autoGeneratedID = tableKeys.getInt(1);

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
		return true;
	}

	public SRTObject findObject(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		BCCPVO bccpVO = null;
		Connection conn = null;
		try {
			conn = tx.open();
			String sql = _FIND_BCCP_STATEMENT;

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
				bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("BCCP_GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By_User_ID"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By_User_ID"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
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
			try {
				if(conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException e) {}
			tx.close();
		}
		return bccpVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		ArrayList<SRTObject> list = new ArrayList<SRTObject>();

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = tx.open();
			String sql = _FIND_ALL_BCCP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				BCCPVO bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("BCCP_GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By_User_ID"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By_User_ID"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				list.add(bccpVO);
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
		BCCPVO bccpVO = (BCCPVO)obj;
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_BCCP_STATEMENT);

			ps.setString(1, bccpVO.getBCCPGUID());
			ps.setString(2, bccpVO.getPropertyTerm());
			ps.setString(3, bccpVO.getRepresentationTerm());
			ps.setInt(4, bccpVO.getBDTID());
			ps.setString(5, bccpVO.getDEN());
			ps.setString(6, bccpVO.getDefinition());
			ps.setInt(7, bccpVO.getCreatedByUserId());
			ps.setInt(8, bccpVO.getLastUpdatedByUserId());
			ps.setTimestamp(9, bccpVO.getLastUpdateTimestamp());
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
		BCCPVO bccpVO = (BCCPVO)obj;

		DBAgent tx = new DBAgent();
		PreparedStatement ps = null;
		try {
			Connection conn = tx.open();

			ps = conn.prepareStatement(_DELETE_BCCP_STATEMENT);
			ps.setInt(1, bccpVO.getBCCPID());
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
}
