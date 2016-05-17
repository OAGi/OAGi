package org.oagi.srt.repository.oracle;

import org.oagi.srt.repository.entity.BasicBusinessInformationEntityProperty;
import org.oagi.srt.repository.impl.BaseBasicBusinessInformationEntityPropertyRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OracleBasicBusinessInformationEntityPropertyRepository
        extends BaseBasicBusinessInformationEntityPropertyRepository {

    private final String FIND_GREATEST_ID_STATEMENT = "SELECT NVL(MAX(bbiep_id), 0) FROM bbiep";

    @Override
    public int findGreatestId() {
        return getJdbcTemplate().queryForObject(FIND_GREATEST_ID_STATEMENT, Integer.class);
    }

    private final String SAVE_STATEMENT = "INSERT INTO bbiep (" +
            "bbiep_id, guid, based_bccp_id, definition, remark, biz_term, " +
            "created_by, last_updated_by, creation_timestamp, last_update_timestamp) VALUES (" +
            "bbiep_bbiep_id_seq.NEXTVAL, :guid, :based_bccp_id, :definition, :remark, :biz_term, " +
            ":created_by, :last_updated_by, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    @Override
    protected int doSave(MapSqlParameterSource namedParameters, BasicBusinessInformationEntityProperty bbiep) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(SAVE_STATEMENT, namedParameters, keyHolder, new String[]{"bbiep_id"});
        return keyHolder.getKey().intValue();
    }
}
