package org.oagi.srt.persistence.populate;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class RunAll {

    public static void main(String args[]) throws Exception {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(ImportApplication.class, args)) {

            PopulateModules populateModules = ctx.getBean(PopulateModules.class);
            populateModules.run(ctx);

            PopulateBlobContents populateBlobContents = ctx.getBean(PopulateBlobContents.class);
            populateBlobContents.run(ctx);

            P_1_3_PopulateAgencyIDList p1 = ctx.getBean(P_1_3_PopulateAgencyIDList.class);
            p1.run(ctx);

            P_1_4_PopulateCodeList p2 = ctx.getBean(P_1_4_PopulateCodeList.class);
            p2.run(ctx);

            P_1_5_1_to_2_PopulateBDTsInDT p3 = ctx.getBean(P_1_5_1_to_2_PopulateBDTsInDT.class);
            p3.run(ctx);

            P_1_5_3_to_5_PopulateSCInDTSC p4 = ctx.getBean(P_1_5_3_to_5_PopulateSCInDTSC.class);
            p4.run(ctx);

            P_1_5_6_PopulateBDTSCPrimitiveRestriction p6 = ctx.getBean(P_1_5_6_PopulateBDTSCPrimitiveRestriction.class);
            p6.run(ctx);

            P_1_6_1_to_2_PopulateDTFromMetaXSD p7 = ctx.getBean(P_1_6_1_to_2_PopulateDTFromMetaXSD.class);
            p7.run(ctx);

            P_1_7_PopulateQBDTInDT p13 = ctx.getBean(P_1_7_PopulateQBDTInDT.class);
            p13.run(ctx);

            P_1_8_1_PopulateAccAsccpBccAscc p14 = ctx.getBean(P_1_8_1_PopulateAccAsccpBccAscc.class);
            p14.run(ctx);
        }
    }
}