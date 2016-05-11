package org.oagi.srt.persistence.dao.oracle;

import org.chanchan.common.persistence.db.BfPersistenceException;
import org.chanchan.common.persistence.db.DBAgent;
import org.oagi.srt.common.QueryCondition;
import org.oagi.srt.common.SRTObject;
import org.oagi.srt.persistence.dao.SRTDAO;
import org.oagi.srt.persistence.dao.SRTDAOException;
import org.oagi.srt.persistence.dto.CDTAllowedPrimitiveVO;
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
public class CDTAllowedPrimitiveOracleDAO extends SRTDAO {
	private final String _tableName = "cdt_awd_pri";

	private final String _FIND_ALL_CDT_Allowed_Primitive_STATEMENT = 
			"SELECT cdt_awd_pri_id, cdt_id, cdt_pri_id, is_default FROM " + _tableName;

	private final String _FIND_CDT_Allowed_Primitive_STATEMENT = 
			"SELECT cdt_awd_pri_id, cdt_id, cdt_pri_id, is_default FROM " + _tableName;

	private final String _INSERT_CDT_Allowed_Primitive_STATEMENT = 
			"INSERT INTO " + _tableName + " (cdt_id, cdt_pri_id, is_default) VALUES (?, ?, ?)";

	private final String _UPDATE_CDT_Allowed_Primitive_STATEMENT = 
			"UPDATE " + _tableName
			+ " SET cdt_awd_pri_id = ?, cdt_id = ?, cdt_pri_id = ?, is_default = ? WHERE cdt_awd_pri_id = ?";

	private final String _DELETE_CDT_Allowed_Primitive_STATEMENT = 
			"DELETE FROM " + _tableName + " WHERE cdt_awd_pri_id = ?";

	public int insertObject(SRTObject obj) throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;

		CDTAllowedPrimitiveVO cdtallowedprimitiveVO = (CDTAllowedPrimitiveVO) obj;
		try {
			conn = tx.open();
			ps = conn.prepareStatement(_INSERT_CDT_Allowed_Primitive_STATEMENT);
			ps.setInt(1, cdtallowedprimitiveVO.getCDTID());
			ps.setInt(2, cdtallowedprimitiveVO.getCDTPrimitiveID());
			if (cdtallowedprimitiveVO.getisDefault())
				ps.setInt(3, 1);
			else
				ps.setInt(3, 0);


			ps.executeUpdate();

			ResultSet tableKeys = ps.getGeneratedKeys();
			tableKeys.next();
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

		CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
		try {
			conn = tx.open();
			String sql = _FIND_CDT_Allowed_Primitive_STATEMENT;

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
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
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
		return cdtallowedprimitiveVO;
	}

	public SRTObject findObject(QueryCondition qc, Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
		try {
			String sql = _FIND_CDT_Allowed_Primitive_STATEMENT;

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
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
			}
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
		return cdtallowedprimitiveVO;
	}

	public ArrayList<SRTObject> findObjects() throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			conn = tx.open();
			String sql = _FIND_ALL_CDT_Allowed_Primitive_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
				list.add(cdtallowedprimitiveVO);
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
			String sql = _FIND_ALL_CDT_Allowed_Primitive_STATEMENT;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
				list.add(cdtallowedprimitiveVO);
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

		CDTAllowedPrimitiveVO cdtallowedprimitiveVO = (CDTAllowedPrimitiveVO) obj;
		try {
			conn = tx.open();

			ps = conn.prepareStatement(_UPDATE_CDT_Allowed_Primitive_STATEMENT);

			ps.setInt(1, cdtallowedprimitiveVO.getCDTID());
			ps.setInt(2, cdtallowedprimitiveVO.getCDTPrimitiveID());
			if (cdtallowedprimitiveVO.getisDefault())
				ps.setInt(3, 1);
			else
				ps.setInt(3, 0);

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

		CDTAllowedPrimitiveVO cdtallowedprimitiveVO = (CDTAllowedPrimitiveVO) obj;
		try {
			conn = tx.open();

			ps = conn.prepareStatement(_DELETE_CDT_Allowed_Primitive_STATEMENT);
			ps.setInt(1, cdtallowedprimitiveVO.getCDTAllowedPrimitiveID());
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


	@Override
	public ArrayList<SRTObject> findObjects(QueryCondition qc)
			throws SRTDAOException {
		DBAgent tx = new DBAgent();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			conn = tx.open();
			String sql = _FIND_CDT_Allowed_Primitive_STATEMENT;

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
			while (rs.next()) {
				CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
				list.add(cdtallowedprimitiveVO);
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

	public ArrayList<SRTObject> findObjects(QueryCondition qc, Connection conn) throws SRTDAOException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<SRTObject> list = new ArrayList<SRTObject>();
		try {
			String sql = _FIND_CDT_Allowed_Primitive_STATEMENT;

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
			while (rs.next()) {
				CDTAllowedPrimitiveVO cdtallowedprimitiveVO = new CDTAllowedPrimitiveVO();
				cdtallowedprimitiveVO.setCDTAllowedPrimitiveID(rs.getInt("cdt_awd_pri_id"));
				cdtallowedprimitiveVO.setCDTID(rs.getInt("cdt_id"));
				cdtallowedprimitiveVO.setCDTPrimitiveID(rs.getInt("cdt_pri_id"));
				cdtallowedprimitiveVO.setisDefault(rs.getBoolean("is_default"));
				list.add(cdtallowedprimitiveVO);
			}
		} catch (SQLException e) {
			throw new SRTDAOException(SRTDAOException.SQL_EXECUTION_FAILED, e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
		return list;
	}
}
