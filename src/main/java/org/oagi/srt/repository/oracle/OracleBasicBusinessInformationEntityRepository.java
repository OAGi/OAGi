package org.oagi.srt.repository.oracle;

import org.oagi.srt.repository.entity.BasicBusinessInformationEntity;
import org.oagi.srt.repository.impl.BaseBasicBusinessInformationEntityRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OracleBasicBusinessInformationEntityRepository extends BaseBasicBusinessInformationEntityRepository {

    private final String SAVE_STATEMENT = "INSERT INTO bbie (" +
            "bbie_id, guid, based_bcc_id, from_abie_id, to_bbiep_id, bdt_pri_restri_id, code_list_id, " +
            "cardinality_min, cardinality_max, default_value, is_nillable, fixed_value, is_null, " +
            "definition, remark, created_by, last_updated_by, creation_timestamp, last_update_timestamp, " +
            "seq_key, is_used) VALUES (" +
            "bbie_bbie_id_seq.NEXTVAL, :guid, :based_bcc_id, :from_abie_id, :to_bbiep_id, :bdt_pri_restri_id, :code_list_id, " +
            ":cardinality_min, :cardinality_max, :default_value, :is_nillable, :fixed_value, :is_null, " +
            ":definition, :remark, :created_by, :last_updated_by, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, " +
            ":seq_key, :is_used)";

    @Override
    protected int doSave(MapSqlParameterSource namedParameters, BasicBusinessInformationEntity bbie) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(SAVE_STATEMENT, namedParameters, keyHolder, new String[]{"bbie_id"});
        return keyHolder.getKey().intValue();
    }
}