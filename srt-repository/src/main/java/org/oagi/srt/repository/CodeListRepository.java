package org.oagi.srt.repository;

import org.oagi.srt.repository.entity.CodeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CodeListRepository extends JpaRepository<CodeList, Integer> {

    public List<CodeList> findByNameContaining(String name);

    @Query("select c from CodeList c where c.name like %?1% and c.state = 'Published' and c.extensibleIndicator = 1")
    public List<CodeList> findByNameContainingAndStateIsPublishedAndExtensibleIndicatorIsTrue(String name);

    @Query("select c from CodeList c where c.guid = ?1 and c.enumTypeGuid = ?2 and c.name = ?3")
    public CodeList findOneByGuidAndEnumTypeGuidAndName(
            String guid, String enumTypeGuid, String name
    );

    @Query("select c from CodeList c where c.guid = ?1")
    public CodeList findOneByGuid(String guid);

    @Query("select c from CodeList c where c.name = ?1")
    public CodeList findOneByName(String name);

    @Query("update CodeList c set c.state = ?1 where c.codeListId = ?2")
    public void updateStateByCodeListId(String state, int codeListId);
}