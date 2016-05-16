package org.oagi.srt.persistence.dao.oracle;

import org.apache.commons.lang.StringUtils;
import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.BCCPVO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Jaehun Lee
 * @version 1.0
 *
 */
@Repository
public class BCCPOracleDAO extends SRTDAO {

	private final String _tableName = "bccp";

	private final String _FIND_ALL_BCCP_STATEMENT = 
			"SELECT BCCP_ID, GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module, "
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bccp_id, is_deprecated FROM " + _tableName;

	private final String _FIND_BCCP_STATEMENT = 
			"SELECT BCCP_ID, GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module, "
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bccp_id, is_deprecated FROM " + _tableName;

	//start from here
	private final String _INSERT_BCCP_STATEMENT = 
			"INSERT INTO " + _tableName + " (GUID, Property_Term, Representation_Term, BDT_ID, "
					+ "Den, Definition, Created_By, owner_user_id, Last_Updated_By, "
					+ "Creation_Timestamp, Last_Update_Timestamp, State, Module,"
					+ "revision_num, revision_tracking_num, revision_action, release_id, current_bccp_id, is_deprecated) VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?)";

	private final String _UPDATE_BCCP_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET Last_Update_Timestamp = CURRENT_TIMESTAMP, GUID = ?, Property_Term = ?, Representation_Term = ?, BDT_ID = ?, "
			+ "Den = ?, Definition = ?, Created_By = ?, owner_user_id = ?, Last_Updated_By = ?, "
			+ "State = ?, Module = ?, "
			+ "revision_num = ?, revision_tracking_num = ?, revision_action = ?, release_id = ?, current_bccp_id = ?, is_deprecated = ? WHERE BCCP_ID = ?";

	private final String _DELETE_BCCP_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE BCCP_ID = ?";

	public int insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;

		BCCPVO bccpVO = (BCCPVO) obj;
		try {
			conn = tx.open();
			ps = conn.prepareStatement(_INSERT_BCCP_STATEMENT);
			if (bccpVO.getBCCPGUID() == null || bccpVO.getBCCPGUID().length() == 0 || bccpVO.getBCCPGUID().isEmpty() || bccpVO.getBCCPGUID().equals(""))
				ps.setString(1, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(1, bccpVO.getBCCPGUID());

			if (bccpVO.getPropertyTerm() == null || bccpVO.getPropertyTerm().length() == 0 || bccpVO.getPropertyTerm().isEmpty() || bccpVO.getPropertyTerm().equals(""))
				ps.setString(2, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(2, bccpVO.getPropertyTerm());

			if (bccpVO.getRepresentationTerm() == null || bccpVO.getRepresentationTerm().length() == 0 || bccpVO.getRepresentationTerm().isEmpty() || bccpVO.getRepresentationTerm().equals(""))
				ps.setString(3, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(3, bccpVO.getRepresentationTerm());

			ps.setInt(4, bccpVO.getBDTID());
			if (bccpVO.getDEN() == null || bccpVO.getDEN().length() == 0 || bccpVO.getDEN().isEmpty() || bccpVO.getDEN().equals(""))
				ps.setString(5, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(5, bccpVO.getDEN());

//			if(bccpVO.getDefinition() == null || bccpVO.getDefinition().isEmpty() || bccpVO.getDefinition().equals("") || bccpVO.getDefinition().length()==0)   
//				ps.setString(6, "\u00A0");
//			else {
				String s = StringUtils.abbreviate(bccpVO.getDefinition(), 4000);
				ps.setString(6, s);
//			}
			ps.setInt(7, bccpVO.getCreatedByUserId());
			ps.setInt(8, bccpVO.getOwnerUserId());
			ps.setInt(9, bccpVO.getLastUpdatedByUserId());
			ps.setInt(10, bccpVO.getState());
//			if( bccpVO.getModule()==null ||  bccpVO.getModule().length()==0 ||  bccpVO.getModule().isEmpty() ||  bccpVO.getModule().equals(""))				
//				ps.setString(11,"\u00A0");
//			else 	
				ps.setString(11, bccpVO.getModule());


			if (bccpVO.getRevisionNum() < 0) {
				ps.setNull(12, java.sql.Types.INTEGER);
			} else {
				ps.setInt(12, bccpVO.getRevisionNum());
			}

			if (bccpVO.getRevisionTrackingNum() < 0) {
				ps.setNull(13, java.sql.Types.INTEGER);
			} else {
				ps.setInt(13, bccpVO.getRevisionTrackingNum());
			}

			if (bccpVO.getRevisionAction() < 1) {
				ps.setNull(14, java.sql.Types.INTEGER);
			} else {
				ps.setInt(14, bccpVO.getRevisionAction());
			}

			if (bccpVO.getReleaseId() < 1) {
				ps.setNull(15, java.sql.Types.INTEGER);
			} else {
				ps.setInt(15, bccpVO.getReleaseId());
			}

			if (bccpVO.getCurrentBccpId() < 1) {
				ps.setNull(16, java.sql.Types.INTEGER);
			} else {
				ps.setInt(16, bccpVO.getCurrentBccpId());
			}

			if (bccpVO.getIs_deprecated())
				ps.setInt(17, 1);
			else
				ps.setInt(17, 0);

			ps.executeUpdate();

			//ResultSet tableKeys = ps.getGeneratedKeys();
			//tableKeys.next();
			//int autoGeneratedID = tableKeys.getInt(1);

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.DAO_INSERT_ERROR, e);
		} catch (SQLException e) {
			tx.rollback();
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(ps);
			closeQuietly(conn);
			closeQuietly(tx);
		}
		return 1;
	}

	public SRTObject findObject(QueryCondition qc) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		BCCPVO bccpVO = null;
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
						ps.setString(n + 1, (String) value);
					} else if (value instanceof Integer) {
						ps.setInt(n + 1, ((Integer) value).intValue());
					}
				}
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By"));
				bccpVO.setOwnerUserId(rs.getInt("owner_user_id"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				bccpVO.setState(rs.getInt("State"));
				bccpVO.setModule(rs.getString("Module"));
				bccpVO.setRevisionNum(rs.getInt("revision_num"));
				bccpVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				bccpVO.setRevisionAction(rs.getInt("revision_action"));
				bccpVO.setReleaseId(rs.getInt("release_id"));
				bccpVO.setCurrentBccpId(rs.getInt("current_bccp_id"));
				bccpVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
			}
			tx.commit();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(conn);
			closeQuietly(tx);
		}
		return bccpVO;
	}

	public SRTObject findObject(QueryCondition qc, Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		BCCPVO bccpVO = null;
		try {
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
						ps.setString(n + 1, (String) value);
					} else if (value instanceof Integer) {
						ps.setInt(n + 1, ((Integer) value).intValue());
					}
				}
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By"));
				bccpVO.setOwnerUserId(rs.getInt("owner_user_id"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				bccpVO.setState(rs.getInt("State"));
				bccpVO.setModule(rs.getString("Module"));
				bccpVO.setRevisionNum(rs.getInt("revision_num"));
				bccpVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				bccpVO.setRevisionAction(rs.getInt("revision_action"));
				bccpVO.setReleaseId(rs.getInt("release_id"));
				bccpVO.setCurrentBccpId(rs.getInt("current_bccp_id"));
				bccpVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
			}
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
		return bccpVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			conn = tx.open();
			String sql = _FIND_ALL_BCCP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				BCCPVO bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By"));
				bccpVO.setOwnerUserId(rs.getInt("owner_user_id"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				bccpVO.setState(rs.getInt("State"));
				bccpVO.setModule(rs.getString("Module"));
				bccpVO.setRevisionNum(rs.getInt("revision_num"));
				bccpVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				bccpVO.setRevisionAction(rs.getInt("revision_action"));
				bccpVO.setReleaseId(rs.getInt("release_id"));
				bccpVO.setCurrentBccpId(rs.getInt("current_bccp_id"));
				bccpVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(bccpVO);
			}
			tx.commit();
		} catch (BfPersistenceException e) {
			throw new SRTDAOException(SRTDAOException.DAO_FIND_ERROR, e);
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(conn);
			closeQuietly(tx);
		}

		return list;
	}

	public ArrayList<SRTObject> findObjects(Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			String sql = _FIND_ALL_BCCP_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				BCCPVO bccpVO = new BCCPVO();
				bccpVO.setBCCPID(rs.getInt("BCCP_ID"));
				bccpVO.setBCCPGUID(rs.getString("GUID"));
				bccpVO.setPropertyTerm(rs.getString("Property_Term"));
				bccpVO.setRepresentationTerm(rs.getString("Representation_Term"));
				bccpVO.setBDTID(rs.getInt("BDT_ID"));
				bccpVO.setDEN(rs.getString("DEN"));
				bccpVO.setDefinition(rs.getString("Definition"));
				bccpVO.setCreatedByUserId(rs.getInt("Created_By"));
				bccpVO.setOwnerUserId(rs.getInt("owner_user_id"));
				bccpVO.setLastUpdatedByUserId(rs.getInt("Last_Updated_By"));
				bccpVO.setCreationTimestamp(rs.getTimestamp("Creation_Timestamp"));
				bccpVO.setLastUpdateTimestamp(rs.getTimestamp("Last_Update_Timestamp"));
				bccpVO.setState(rs.getInt("State"));
				bccpVO.setModule(rs.getString("Module"));
				bccpVO.setRevisionNum(rs.getInt("revision_num"));
				bccpVO.setRevisionTrackingNum(rs.getInt("revision_tracking_num"));
				bccpVO.setRevisionAction(rs.getInt("revision_action"));
				bccpVO.setReleaseId(rs.getInt("release_id"));
				bccpVO.setCurrentBccpId(rs.getInt("current_bccp_id"));
				bccpVO.setIs_deprecated(rs.getBoolean("is_deprecated"));
				list.add(bccpVO);
			}
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}

		return list;
	}

	public boolean updateObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;

		BCCPVO bccpVO = (BCCPVO) obj;
		try {
			conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_BCCP_STATEMENT);

			if (bccpVO.getBCCPGUID() == null || bccpVO.getBCCPGUID().length() == 0 || bccpVO.getBCCPGUID().isEmpty() || bccpVO.getBCCPGUID().equals(""))
				ps.setString(1, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(1, bccpVO.getBCCPGUID());

			if (bccpVO.getPropertyTerm() == null || bccpVO.getPropertyTerm().length() == 0 || bccpVO.getPropertyTerm().isEmpty() || bccpVO.getPropertyTerm().equals(""))
				ps.setString(2, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(2, bccpVO.getPropertyTerm());

			if (bccpVO.getRepresentationTerm() == null || bccpVO.getRepresentationTerm().length() == 0 || bccpVO.getRepresentationTerm().isEmpty() || bccpVO.getRepresentationTerm().equals(""))
				ps.setString(3, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(3, bccpVO.getRepresentationTerm());

			ps.setInt(4, bccpVO.getBDTID());
			if (bccpVO.getDEN() == null || bccpVO.getDEN().length() == 0 || bccpVO.getDEN().isEmpty() || bccpVO.getDEN().equals(""))
				ps.setString(5, "**SOMETHING WRONG THIS VALUE CANNOT BE NULL**");
			else
				ps.setString(5, bccpVO.getDEN());

//			if(bccpVO.getDefinition() == null)
//				ps.setString(6, "");
//			else {
				String s = StringUtils.abbreviate(bccpVO.getDefinition(), 4000);
				ps.setString(6, s);
//			}
			ps.setInt(7, bccpVO.getCreatedByUserId());
			ps.setInt(8, bccpVO.getOwnerUserId());
			ps.setInt(9, bccpVO.getLastUpdatedByUserId());
			//ps.setTimestamp(10, bccpVO.getCreationTimestamp());
			ps.setInt(10, bccpVO.getState());
//			if( bccpVO.getModule()==null ||  bccpVO.getModule().length()==0 ||  bccpVO.getModule().isEmpty() ||  bccpVO.getModule().equals(""))				
//				ps.setString(11,"\u00A0");
//			else 	
				ps.setString(11, bccpVO.getModule());


			if (bccpVO.getRevisionNum() < 0) {
				ps.setNull(12, java.sql.Types.INTEGER);
			} else {
				ps.setInt(12, bccpVO.getRevisionNum());
			}

			if (bccpVO.getRevisionTrackingNum() < 0) {
				ps.setNull(13, java.sql.Types.INTEGER);
			} else {
				ps.setInt(13, bccpVO.getRevisionTrackingNum());
			}

			if (bccpVO.getRevisionAction() < 1) {
				ps.setNull(14, java.sql.Types.INTEGER);
			} else {
				ps.setInt(14, bccpVO.getRevisionAction());
			}

			if (bccpVO.getReleaseId() < 1) {
				ps.setNull(15, java.sql.Types.INTEGER);
			} else {
				ps.setInt(15, bccpVO.getReleaseId());
			}

			if (bccpVO.getCurrentBccpId() < 1) {
				ps.setNull(16, java.sql.Types.INTEGER);
			} else {
				ps.setInt(16, bccpVO.getCurrentBccpId());
			}

			if (bccpVO.getIs_deprecated())
				ps.setInt(17, 1);
			else
				ps.setInt(17, 0);

			ps.setInt(18, bccpVO.getBCCPID());
			ps.executeUpdate();

			tx.commit();
		} catch (BfPersistenceException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.DAO_UPDATE_ERROR, e);
		} catch (SQLException e) {
			tx.rollback(e);
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(ps);
			closeQuietly(conn);
			closeQuietly(tx);
		}

		return true;
	}

	public boolean deleteObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;

		BCCPVO bccpVO = (BCCPVO) obj;
		try {
			conn = tx.open();

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
			closeQuietly(ps);
			closeQuietly(conn);
			closeQuietly(tx);
		}

		return true;
	}
}