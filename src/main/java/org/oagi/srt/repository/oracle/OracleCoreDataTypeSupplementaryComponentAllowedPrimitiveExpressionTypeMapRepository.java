package org.oagi.srt.repository.oracle;

import org.oagi.srt.repository.entity.CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap;
import org.oagi.srt.repository.impl.BaseCoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OracleCoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository
        extends BaseCoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMapRepository implements OracleRepository {

    @Override
    public String getSequenceName() {
        return "CDT_SC_AW_PR_XPS_TYP_MP_ID_SEQ";
    }

    private final String SAVE_STATEMENT = "INSERT INTO cdt_sc_awd_pri_xps_type_map (" +
            "cdt_sc_awd_pri_xps_type_map_id, cdt_sc_awd_pri, xbt_id) VALUES (" +
            getSequenceName() + ".NEXTVAL, :cdt_sc_awd_pri, :xbt_id)";

    @Override
    protected int doSave(MapSqlParameterSource namedParameters,
                         CoreDataTypeSupplementaryComponentAllowedPrimitiveExpressionTypeMap cdtScAwdPriXpsTypeMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(SAVE_STATEMENT, namedParameters, keyHolder, new String[]{"cdt_sc_awd_pri_xps_type_map_id"});
        return keyHolder.getKey().intValue();
    }
}