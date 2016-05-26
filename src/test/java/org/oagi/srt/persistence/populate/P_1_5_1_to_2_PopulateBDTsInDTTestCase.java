package org.oagi.srt.persistence.populate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.Application;
import org.oagi.srt.common.SRTConstants;
import org.oagi.srt.common.util.XPathHandler;
import org.oagi.srt.repository.BusinessDataTypePrimitiveRestrictionRepository;
import org.oagi.srt.repository.CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository;
import org.oagi.srt.repository.CoreDataTypeAllowedPrimitiveRepository;
import org.oagi.srt.repository.CoreDataTypePrimitiveRepository;
import org.oagi.srt.repository.DataTypeRepository;
import org.oagi.srt.repository.XSDBuiltInTypeRepository;
import org.oagi.srt.repository.entity.BusinessDataTypePrimitiveRestriction;
import org.oagi.srt.repository.entity.CoreDataTypeAllowedPrimitive;
import org.oagi.srt.repository.entity.CoreDataTypeAllowedPrimitiveExpressionTypeMap;
import org.oagi.srt.repository.entity.CoreDataTypePrimitive;
import org.oagi.srt.repository.entity.DataType;
import org.oagi.srt.repository.entity.XSDBuiltInType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javassist.compiler.ast.Pair;

import org.oagi.srt.common.util.Utility;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class P_1_5_1_to_2_PopulateBDTsInDTTestCase extends AbstractTransactionalJUnit4SpringContextTests {
   @Autowired
    private DataTypeRepository dataTypeRepository;
   
   @Autowired
   private BusinessDataTypePrimitiveRestrictionRepository businessDataTypePrimitiveRestrictionRepository;
  
   @Autowired
   private CoreDataTypeAllowedPrimitiveExpressionTypeMapRepository coreDataTypeAllowedPrimitiveExpressionTypeMapRepository;
   @Autowired
   private CoreDataTypeAllowedPrimitiveRepository coreDataTypeAllowedPrimitiveRepository;
   @Autowired
   private CoreDataTypePrimitiveRepository coreDataTypePrimitiveRepository;
   @Autowired
   private XSDBuiltInTypeRepository xsdBuiltInTypeRepository;
   
	private class ExpectedDataType {
        private String guid;
        private String typeName;

        private String baseGuid;
        private String definition;
        private String ccDefinition;

        
        public ExpectedDataType(String guid, String typeName) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {           
            this.guid = guid;
            this.typeName = typeName;
            
            boolean defaultBDT=false;
            
            XPathHandler fieldsXP = new XPathHandler(SRTConstants.FILEDS_XSD_FILE_PATH);
            XPathHandler bizDTXP = new XPathHandler(SRTConstants.BUSINESS_DATA_TYPE_XSD_FILE_PATH);
            
            Node aDTNode = fieldsXP.getNode("//xsd:simpleType[@id='"+guid+"'] | //xsd:complexType[@id='"+guid+"']");
            if(aDTNode==null){
            	aDTNode = bizDTXP.getNode("//xsd:simpleType[@id='"+guid+"'] | //xsd:complexType[@id='"+guid+"']");
            	defaultBDT=true;
            }
            
            if(defaultBDT){
            	
            	if(typeName.contains("AmountType")) { baseGuid = "oagis-id-3bfbbc07cffc47a886496961b0f6b292";}
            	else if(typeName.contains("BinaryObjectType")) { baseGuid = "oagis-id-689935c2c40445dab6ca3d19043cf71d";}
            	else if(typeName.contains("CodeType")) { baseGuid = "oagis-id-efd26bf9a65b429294356dcc9d22c4fe";}
            	else if(typeName.contains("DateType")) { baseGuid = "oagis-id-9ff8dc0294a24f7292b9fe6a8ab3a3eb";}
            	else if(typeName.contains("DateTimeType")) { baseGuid = "oagis-id-c15c79d7332a47939f717943e5ed9e67";}
            	else if(typeName.contains("DurationType")) { baseGuid = "oagis-id-fc2c841359814eb08104f29e02673f65";}
            	else if(typeName.contains("GraphicType")) { baseGuid = "oagis-id-a308345fa0614437b2593fd7cb0d9f20";}
            	else if(typeName.contains("IDType")) { baseGuid = "oagis-id-6b35560a5ca14a1f801e9d32ff5eb502";}
            	else if(typeName.contains("IndicatorType")) { baseGuid = "oagis-id-df7fdf7c96394523a533b51854544269";}
            	else if(typeName.contains("MeasureType")) { baseGuid = "oagis-id-e9f34d8fe0a34d37aca628b612c2b3ce";}
            	else if(typeName.contains("NameType")) { baseGuid = "oagis-id-8710513002c241dca953ddee1695ff1f";}
            	else if(typeName.contains("NumberType")) { baseGuid = "oagis-id-d90b1011eb27451e9fedccbe7af51e76";}
            	else if(typeName.contains("OrdinalType")) { baseGuid = "oagis-id-6f55788519e94b9fb41c371d5c98c0ca";}
            	else if(typeName.contains("PercentType")) { baseGuid = "oagis-id-32ef31dcc7d94cc1ac34dd0067a626ed";}
            	else if(typeName.contains("PictureType")) { baseGuid = "oagis-id-1ed683abe54f496f9ba86f7ae35b703d";}
            	else if(typeName.contains("QuantityType")) { baseGuid = "oagis-id-d05b0412061b477a82bfa4e14c2d5216";}
            	else if(typeName.contains("RateType")) { baseGuid = "oagis-id-4238ca186cf6482c94b1b95fd18f2305";}
            	else if(typeName.contains("RatioType")) { baseGuid = "oagis-id-783f8d5944054871b574e75da7dd258a";}
            	else if(typeName.contains("SoundType")) { baseGuid = "oagis-id-b75fa46a2b884d57a46a4934cbd208c7";}
            	else if(typeName.contains("TextType")) { baseGuid = "oagis-id-e5a81582d7604fc2a441d57fd6cdf5ab";}
            	else if(typeName.contains("TimeType")) { baseGuid = "oagis-id-e80da4c59dbb441b915d70255427f33e";}
            	else if(typeName.contains("ValueType")) { baseGuid = "oagis-id-a06e726d98274399bd6c76fe82a5fbdc";}
            	else if(typeName.contains("VideoType")) { baseGuid = "oagis-id-ce7635625e75420d973bcda56bb80a9f";}
            	
            	Node aDefinitionNode = bizDTXP.getNode("//xsd:complexType[@id='"+guid+"']/xsd:annotation/xsd:documentation/*[local-name()=\"ccts_Definition\"] | //xsd:simpleType[@id='"+guid+"']/xsd:annotation/xsd:documentation/*[local-name()=\"ccts_Definition\"]");
            	Node aCCDefinitionNode = bizDTXP.getNode("//xsd:complexType[@id='"+guid+"']/xsd:simpleContent/xsd:extension/xsd:annotation/xsd:documentation//*[local-name()=\"ccts_Definition\"] | //xsd:simpleType[@id='"+guid+"']/xsd:union/xsd:annotation/xsd:documentation//*[local-name()=\"ccts_Definition\"] | //xsd:simpleType[@id='"+guid+"']/xsd:restriction/xsd:annotation/xsd:documentation//*[local-name()=\"ccts_Definition\"]");
            	definition=null;
            	ccDefinition=null;
            	if(aDefinitionNode!=null){
            		definition=aDefinitionNode.getTextContent();
            	}
            	if(aCCDefinitionNode!=null){
            		ccDefinition=aCCDefinitionNode.getTextContent();
            	}
            }
            else {
            	Element aDTElem = (Element) aDTNode;
            	String baseName =""; 
            	Element restriction = (Element) aDTElem.getElementsByTagName("xsd:restriction").item(0);
            	if(restriction!=null){
            		baseName = restriction.getAttribute("base");
            	}
            	else {
            		Element simpleContent = (Element) aDTElem.getElementsByTagName("xsd:simpleContent").item(0);
                	if(simpleContent!=null){
                		Element extension = (Element) simpleContent.getElementsByTagName("xsd:extension").item(0);
                		baseName = extension.getAttribute("base");
                	}
            	}
            	
            	Node baseNode = bizDTXP.getNode("//xsd:simpleType[@name='"+baseName+"'] | //xsd:complexType[@name='"+baseName+"']");
            	Element baseElem = (Element) baseNode;           	
            	baseGuid = baseElem.getAttribute("id");
            	definition=null;
            	ccDefinition=null;
            	
            }
        }

        public String getTypeName() {
            return typeName;
        }

		public String getBaseGuid() {
			return baseGuid;
		}



		public String getDefinition() {
			return definition;
		}



		public String getCcDefinition() {
			return ccDefinition;
		}



    }
	
	
	
	
	@Test
	public void testPopulateDTTable() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		
        Map<String, ExpectedDataType> expectedDataTypes = new HashMap();
        //Unqualified DataTypes mapped with Core Data Type (20)
        expectedDataTypes.put("oagis-id-109055a967bd4cf19ee3320755b01f8d", new ExpectedDataType("oagis-id-109055a967bd4cf19ee3320755b01f8d","AmountType"));
        expectedDataTypes.put("oagis-id-f2c5dcba0088440d866ea23a81876280", new ExpectedDataType("oagis-id-f2c5dcba0088440d866ea23a81876280","BinaryObjectType"));
        expectedDataTypes.put("oagis-id-9ec6be30dabf45d5b53b765634be2412", new ExpectedDataType("oagis-id-9ec6be30dabf45d5b53b765634be2412","GraphicType"));
        expectedDataTypes.put("oagis-id-dc994532fe464847acf84a54548276ff", new ExpectedDataType("oagis-id-dc994532fe464847acf84a54548276ff","SoundType"));
        expectedDataTypes.put("oagis-id-83d4cc94be8249a3b8cbe7e1c2ecb417", new ExpectedDataType("oagis-id-83d4cc94be8249a3b8cbe7e1c2ecb417","VideoType"));
        expectedDataTypes.put("oagis-id-3318aed9165847e3afb907724db2b65c", new ExpectedDataType("oagis-id-3318aed9165847e3afb907724db2b65c","CodeType"));
        expectedDataTypes.put("oagis-id-bea4dcd433d54aa698db2176cab33c19", new ExpectedDataType("oagis-id-bea4dcd433d54aa698db2176cab33c19","IDType"));
        expectedDataTypes.put("oagis-id-3cbb2f0b87254ff696e9315cd863f613", new ExpectedDataType("oagis-id-3cbb2f0b87254ff696e9315cd863f613","MeasureType"));
        expectedDataTypes.put("oagis-id-bf66e0afea2c4c2da7bc69af14ca23c9", new ExpectedDataType("oagis-id-bf66e0afea2c4c2da7bc69af14ca23c9","NameType"));
        expectedDataTypes.put("oagis-id-5212437eb6f045f98b072db0ce971409", new ExpectedDataType("oagis-id-5212437eb6f045f98b072db0ce971409","QuantityType"));
        expectedDataTypes.put("oagis-id-d97b8cf6a26f408db148163485796d15", new ExpectedDataType("oagis-id-d97b8cf6a26f408db148163485796d15","TextType"));
        expectedDataTypes.put("oagis-id-dd0c8f86b160428da3a82d2866a5b48d", new ExpectedDataType("oagis-id-dd0c8f86b160428da3a82d2866a5b48d","DateTimeType"));
        expectedDataTypes.put("oagis-id-ee2b3bf53bd44b21960ff8575891c638", new ExpectedDataType("oagis-id-ee2b3bf53bd44b21960ff8575891c638","DurationType"));
        expectedDataTypes.put("oagis-id-ef32205ede95407f981064a45ffa652c", new ExpectedDataType("oagis-id-ef32205ede95407f981064a45ffa652c","IndicatorType"));
        expectedDataTypes.put("oagis-id-06083bfba01d4213a852830000e939b9", new ExpectedDataType("oagis-id-06083bfba01d4213a852830000e939b9","NumberType"));
        expectedDataTypes.put("oagis-id-06083bfba01d4213a852830000e12051", new ExpectedDataType("oagis-id-06083bfba01d4213a852830000e12051","OrdinalType"));
        expectedDataTypes.put("oagis-id-57efecfe17e64e20b83adccce3159a9e", new ExpectedDataType("oagis-id-57efecfe17e64e20b83adccce3159a9e","PercentType"));
        expectedDataTypes.put("oagis-id-83ea28a4218e447ebe99113901b2c70f", new ExpectedDataType("oagis-id-83ea28a4218e447ebe99113901b2c70f","TimeType"));
        expectedDataTypes.put("oagis-id-d006eb3550364c61bc10cac70763e677", new ExpectedDataType("oagis-id-d006eb3550364c61bc10cac70763e677","ValueType"));
        expectedDataTypes.put("oagis-id-3ecb2fb750c144c58c1b68d2fa2a36ae", new ExpectedDataType("oagis-id-3ecb2fb750c144c58c1b68d2fa2a36ae","DateType"));
        
        //Default DataTypes which are base of previous 20 unqualified DataTypes (20)
        expectedDataTypes.put("oagis-id-e6f93bd0dc934ab2af11bd46888c1233", new ExpectedDataType("oagis-id-e6f93bd0dc934ab2af11bd46888c1233","AmountType_0723C8"));
        expectedDataTypes.put("oagis-id-6eae247688734e6da1dfdadb89c3e43a", new ExpectedDataType("oagis-id-6eae247688734e6da1dfdadb89c3e43a","BinaryObjectType_4277E5"));
        expectedDataTypes.put("oagis-id-0c482df00c2343cd99b1b5c1637a199d", new ExpectedDataType("oagis-id-0c482df00c2343cd99b1b5c1637a199d","CodeType_1DEB05"));
        expectedDataTypes.put("oagis-id-f074a322acff4705bbef417505f9bf11", new ExpectedDataType("oagis-id-f074a322acff4705bbef417505f9bf11","DateType_238C51"));
        expectedDataTypes.put("oagis-id-a5cfd20385314a63afc1ffcf6357a08b", new ExpectedDataType("oagis-id-a5cfd20385314a63afc1ffcf6357a08b","DateTimeType_AD9DD9"));
        expectedDataTypes.put("oagis-id-f16cdbda66d2441cac9e99615c99e70e", new ExpectedDataType("oagis-id-f16cdbda66d2441cac9e99615c99e70e","DurationType_JJ5401"));
        expectedDataTypes.put("oagis-id-a3101ac1f4734f408d0b01e2ba182648", new ExpectedDataType("oagis-id-a3101ac1f4734f408d0b01e2ba182648","GraphicType_3FDF3D"));
        expectedDataTypes.put("oagis-id-6e141689c22944a083798b9dbba8b47f", new ExpectedDataType("oagis-id-6e141689c22944a083798b9dbba8b47f","IDType_D995CD"));
        expectedDataTypes.put("oagis-id-c62bf0f41c964349b874b8f397f673ec", new ExpectedDataType("oagis-id-c62bf0f41c964349b874b8f397f673ec","IndicatorType_CVW231"));
        expectedDataTypes.put("oagis-id-bf1305892fcb4e2e9eccec5ae693d73d", new ExpectedDataType("oagis-id-bf1305892fcb4e2e9eccec5ae693d73d","MeasureType_671290"));
        expectedDataTypes.put("oagis-id-8ef2aeaecfa645088c4bf4b424905596", new ExpectedDataType("oagis-id-8ef2aeaecfa645088c4bf4b424905596","NameType_02FC2Z"));
        expectedDataTypes.put("oagis-id-e57e1a2b7be44356a2256bc46f61ec36", new ExpectedDataType("oagis-id-e57e1a2b7be44356a2256bc46f61ec36","NumberType_BE4776"));
        expectedDataTypes.put("oagis-id-23a9c386c4304400a8f1dd620b26035b", new ExpectedDataType("oagis-id-23a9c386c4304400a8f1dd620b26035b","OrdinalType_PQALZM"));
        expectedDataTypes.put("oagis-id-af8d69bed6454c0eb9cc494977993b03", new ExpectedDataType("oagis-id-af8d69bed6454c0eb9cc494977993b03","PercentType_481002"));
        expectedDataTypes.put("oagis-id-6d24dd8e55414acc915b1a2c7e358552", new ExpectedDataType("oagis-id-6d24dd8e55414acc915b1a2c7e358552","QuantityType_201330"));
        expectedDataTypes.put("oagis-id-7242ea809d804f9ab4e08766a6117710", new ExpectedDataType("oagis-id-7242ea809d804f9ab4e08766a6117710","SoundType_697AE6"));
        expectedDataTypes.put("oagis-id-89be97039be04d6f9cfda107d75926b4", new ExpectedDataType("oagis-id-89be97039be04d6f9cfda107d75926b4","TextType_62S0B4"));
        expectedDataTypes.put("oagis-id-6659e405ac8d43268d2a10d451eea261", new ExpectedDataType("oagis-id-6659e405ac8d43268d2a10d451eea261","TimeType_100DCA"));
        expectedDataTypes.put("oagis-id-641fdee1d3114629a15902311d895ca2", new ExpectedDataType("oagis-id-641fdee1d3114629a15902311d895ca2","ValueType_D19E7B"));
        expectedDataTypes.put("oagis-id-3292eaa5630b48ecb7c4249b0ddc760e", new ExpectedDataType("oagis-id-3292eaa5630b48ecb7c4249b0ddc760e","VideoType_539B44"));
	
        //3.1.1.8.1.1 Exceptions (13)
        expectedDataTypes.put("oagis-id-d26b22f9103744edb0a4d3728aefc26e", new ExpectedDataType("oagis-id-d26b22f9103744edb0a4d3728aefc26e","NormalizedStringType"));
        expectedDataTypes.put("oagis-id-e28a3e09aa6e42339e11a0c740362ca9", new ExpectedDataType("oagis-id-e28a3e09aa6e42339e11a0c740362ca9","TokenType"));
        expectedDataTypes.put("oagis-id-310d5bf351c143ed80c16ee9ef837271", new ExpectedDataType("oagis-id-310d5bf351c143ed80c16ee9ef837271","StringType"));
        expectedDataTypes.put("oagis-id-5a2ed18041c04f3e995c773480b0076d", new ExpectedDataType("oagis-id-5a2ed18041c04f3e995c773480b0076d","DayDateType"));
        expectedDataTypes.put("oagis-id-af5bfda6510443a5a468bd1df713ff4c", new ExpectedDataType("oagis-id-af5bfda6510443a5a468bd1df713ff4c","MonthDateType"));
        expectedDataTypes.put("oagis-id-86ce40c683224461a3d4747410c551c3", new ExpectedDataType("oagis-id-86ce40c683224461a3d4747410c551c3","MonthDayDateType"));
        expectedDataTypes.put("oagis-id-a83c14386c7547669c8c9a516ff4c54e", new ExpectedDataType("oagis-id-a83c14386c7547669c8c9a516ff4c54e","YearDateType"));
        expectedDataTypes.put("oagis-id-004a7da25119417ba44a1f43a2585d0d", new ExpectedDataType("oagis-id-004a7da25119417ba44a1f43a2585d0d","YearMonthDateType"));
        expectedDataTypes.put("oagis-id-3d47ac271e344e6094791b6e93fbce26", new ExpectedDataType("oagis-id-3d47ac271e344e6094791b6e93fbce26","URIType"));
        expectedDataTypes.put("oagis-id-10ef9f34e0504a71880c967c82ac039f", new ExpectedDataType("oagis-id-10ef9f34e0504a71880c967c82ac039f","DurationMeasureType"));
        expectedDataTypes.put("oagis-id-9b8fed621a7148a5b7fb2f04b80381be", new ExpectedDataType("oagis-id-9b8fed621a7148a5b7fb2f04b80381be","IntegerNumberType"));
        expectedDataTypes.put("oagis-id-ec9821a975e84ad9804265b0f082a36b", new ExpectedDataType("oagis-id-ec9821a975e84ad9804265b0f082a36b","PositiveIntegerNumberType"));
        expectedDataTypes.put("oagis-id-5a2ed18041c04f3e995c7734386ae380", new ExpectedDataType("oagis-id-5a2ed18041c04f3e995c7734386ae380","DayOfWeekHourMinuteUTCType"));
        /*these are queried are already tested!
        //expectedDataTypes.put("oagis-id-3ecb2fb750c144c58c1b68d2fa2a36ae", new ExpectedDataType("oagis-id-3ecb2fb750c144c58c1b68d2fa2a36ae","DateType"));
        //expectedDataTypes.put("oagis-id-dd0c8f86b160428da3a82d2866a5b48d", new ExpectedDataType("oagis-id-dd0c8f86b160428da3a82d2866a5b48d","DateTimeType"));
        //expectedDataTypes.put("oagis-id-ee2b3bf53bd44b21960ff8575891c638", new ExpectedDataType("oagis-id-ee2b3bf53bd44b21960ff8575891c638","DurationType"));
        //expectedDataTypes.put("oagis-id-ef32205ede95407f981064a45ffa652c", new ExpectedDataType("oagis-id-ef32205ede95407f981064a45ffa652c","IndicatorType"));
        //expectedDataTypes.put("oagis-id-06083bfba01d4213a852830000e939b9", new ExpectedDataType("oagis-id-06083bfba01d4213a852830000e939b9","NumberType"));
        //expectedDataTypes.put("oagis-id-06083bfba01d4213a852830000e12051", new ExpectedDataType("oagis-id-06083bfba01d4213a852830000e12051","OrdinalType"));
        //expectedDataTypes.put("oagis-id-57efecfe17e64e20b83adccce3159a9e", new ExpectedDataType("oagis-id-57efecfe17e64e20b83adccce3159a9e","PercentType"));
        //expectedDataTypes.put("oagis-id-83ea28a4218e447ebe99113901b2c70f", new ExpectedDataType("oagis-id-83ea28a4218e447ebe99113901b2c70f","TimeType"));
        //expectedDataTypes.put("oagis-id-d006eb3550364c61bc10cac70763e677", new ExpectedDataType("oagis-id-d006eb3550364c61bc10cac70763e677","ValueType"));
		*/

        //3.1.1.8.1.1 Base of Exceptions (11)
        expectedDataTypes.put("oagis-id-3049eed90b924d699f1102b946843725", new ExpectedDataType("oagis-id-3049eed90b924d699f1102b946843725","DateType_DB95C8"));
        expectedDataTypes.put("oagis-id-3bc40b222d994d9b9fb5d4a33319a146", new ExpectedDataType("oagis-id-3bc40b222d994d9b9fb5d4a33319a146","DateType_5B057B"));
        expectedDataTypes.put("oagis-id-52df32ab374440f0ac456a1abe66cb94", new ExpectedDataType("oagis-id-52df32ab374440f0ac456a1abe66cb94","DateType_0C267D"));
        expectedDataTypes.put("oagis-id-6712fbca652e49ac9739396377b090cb", new ExpectedDataType("oagis-id-6712fbca652e49ac9739396377b090cb","DateType_BBCC14"));
        expectedDataTypes.put("oagis-id-0a7f3544ea954099aa06afe488417136", new ExpectedDataType("oagis-id-0a7f3544ea954099aa06afe488417136","DateType_57D5E1"));
        expectedDataTypes.put("oagis-id-ff84535456d44233b6f0976d993b442d", new ExpectedDataType("oagis-id-ff84535456d44233b6f0976d993b442d","IDType_B3F14E"));
        expectedDataTypes.put("oagis-id-d614ed8726ff482c9c5a8183d735d9ed", new ExpectedDataType("oagis-id-d614ed8726ff482c9c5a8183d735d9ed","NumberType_B98233"));
        expectedDataTypes.put("oagis-id-6b81b03c96cc47f08ccb26838853012d", new ExpectedDataType("oagis-id-6b81b03c96cc47f08ccb26838853012d","NumberType_201301"));
        expectedDataTypes.put("oagis-id-89be97039be04d6f9cfda107d75926b5", new ExpectedDataType("oagis-id-89be97039be04d6f9cfda107d75926b5","TextType_62S0C1"));
        expectedDataTypes.put("oagis-id-42a03ed19450453da6c87fe8eadabfa4", new ExpectedDataType("oagis-id-42a03ed19450453da6c87fe8eadabfa4","TextType_0VCBZ5"));
        expectedDataTypes.put("oagis-id-d5cb8551edf041389893fee25a496395", new ExpectedDataType("oagis-id-d5cb8551edf041389893fee25a496395","TextType_0F0ZX1"));
        /*these are queried are already tested!        
        //expectedDataTypes.put("oagis-id-f074a322acff4705bbef417505f9bf11", new ExpectedDataType("oagis-id-f074a322acff4705bbef417505f9bf11","DateType_238C51"));
        //expectedDataTypes.put("oagis-id-a5cfd20385314a63afc1ffcf6357a08b", new ExpectedDataType("oagis-id-a5cfd20385314a63afc1ffcf6357a08b","DateTimeType_AD9DD9"));
        //expectedDataTypes.put("oagis-id-f16cdbda66d2441cac9e99615c99e70e", new ExpectedDataType("oagis-id-f16cdbda66d2441cac9e99615c99e70e","DurationType_JJ5401"));
        //expectedDataTypes.put("oagis-id-c62bf0f41c964349b874b8f397f673ec", new ExpectedDataType("oagis-id-c62bf0f41c964349b874b8f397f673ec","IndicatorType_CVW231"));
        //expectedDataTypes.put("oagis-id-e57e1a2b7be44356a2256bc46f61ec36", new ExpectedDataType("oagis-id-e57e1a2b7be44356a2256bc46f61ec36","NumberType_BE4776"));
        //expectedDataTypes.put("oagis-id-23a9c386c4304400a8f1dd620b26035b", new ExpectedDataType("oagis-id-23a9c386c4304400a8f1dd620b26035b","OrdinalType_PQALZM"));
        //expectedDataTypes.put("oagis-id-af8d69bed6454c0eb9cc494977993b03", new ExpectedDataType("oagis-id-af8d69bed6454c0eb9cc494977993b03","PercentType_481002"));       
        //expectedDataTypes.put("oagis-id-6659e405ac8d43268d2a10d451eea261", new ExpectedDataType("oagis-id-6659e405ac8d43268d2a10d451eea261","TimeType_100DCA"));
        //expectedDataTypes.put("oagis-id-641fdee1d3114629a15902311d895ca2", new ExpectedDataType("oagis-id-641fdee1d3114629a15902311d895ca2","ValueType_D19E7B"));
		*/
        
        
        
        //3.1.1.8.1.2 Additional Default BDTs (2)
        expectedDataTypes.put("oagis-id-d2f721a297684b538e7dbb88cf5526bc", new ExpectedDataType("oagis-id-d2f721a297684b538e7dbb88cf5526bc","CodeType_1E7368"));
        expectedDataTypes.put("oagis-id-0fb76e8565244977b1239327ca436f76", new ExpectedDataType("oagis-id-0fb76e8565244977b1239327ca436f76","ValueType_039C44"));
        /* IDType_B3F14E is already tested!
        //expectedDataTypes.put("oagis-id-ff84535456d44233b6f0976d993b442d", new ExpectedDataType("oagis-id-ff84535456d44233b6f0976d993b442d","IDType_B3F14E"));
        */
        
        //3.1.1.8.1.3 CodeContentType (1)
        expectedDataTypes.put("oagis-id-5646bf52a97b48adb50ded6ff8c38354", new ExpectedDataType("oagis-id-5646bf52a97b48adb50ded6ff8c38354","CodeContentType"));

        //3.1.1.8.1.4 IDContentType (1)
        expectedDataTypes.put("oagis-id-08d6ade226fd42488b53c0815664e246", new ExpectedDataType("oagis-id-08d6ade226fd42488b53c0815664e246","IDContentType"));
        
        
        //number of total tested DT case is 68
        
        for(Map.Entry<String, ExpectedDataType> entry: expectedDataTypes.entrySet()){
        	String key = entry.getKey();
        	ExpectedDataType value = entry.getValue();
        	
        	DataType getDT = dataTypeRepository.findOneByGuid(key);
        	if(getDT==null){
        		assertTrue(getDT!=null);
        	}
        	else {
        		DataType baseDT = dataTypeRepository.findOne(getDT.getBasedDtId());
        		assertEquals(value.getBaseGuid(),baseDT.getGuid());
        		
        		String DTTypeName = Utility.denToTypeName(getDT.getDen());       		
        		assertEquals(value.getTypeName(),DTTypeName);         		
        		assertEquals(value.getDefinition(),getDT.getDefinition());        
        		assertEquals(value.getCcDefinition(),getDT.getContentComponentDefinition());
        	}
        }

	}
	 private class ExpectedBDTPrimitiveRestriction{
		   
		   private String bdtTypeName;
		   private int cdtId;
		   private ArrayList<String> cdtPris;
		   private ArrayList<String> xbts;
		   private int defaultIndex;
		   
		   public ExpectedBDTPrimitiveRestriction(String bdtTypeName, int cdtId){
			   this.bdtTypeName = bdtTypeName;
			   this.setCdtId(cdtId);
			   cdtPris = new ArrayList<String>();
			   xbts = new ArrayList<String>();
			   setDefaultIndex(-1);
			   
			   if(bdtTypeName.contains("AmountType") 
				||(bdtTypeName.contains("MeasureType") && !bdtTypeName.equals("DurationMeasureType"))
				||bdtTypeName.contains("NumberType") 
				||bdtTypeName.contains("PercentType")
				||bdtTypeName.contains("QuantityType")){
				   cdtPris.add("Decimal"); xbts.add("xsd:decimal"); setDefaultIndex(0);
				   cdtPris.add("Double"); xbts.add("xsd:double");
				   cdtPris.add("Double"); xbts.add("xsd:float");
				   cdtPris.add("Float"); xbts.add("xsd:float");
				   cdtPris.add("Integer"); xbts.add("xsd:integer");
				   cdtPris.add("Integer"); xbts.add("xsd:nonNegativeInteger");
				   cdtPris.add("Integer"); xbts.add("xsd:positiveInteger");
				   
				   if(bdtTypeName.equals("IntegerNumberType") || bdtTypeName.equals("NumberType_B98233")){
					   setDefaultIndex(4);//xsd:integer
				   }
				   if(bdtTypeName.equals("PositiveIntegerNumberType") || bdtTypeName.equals("NumberType_201301")){
					   setDefaultIndex(6);//xsd:positiveInteger				   
				   }
			   }
			   else if (bdtTypeName.contains("BinaryObjectType") 
					 || bdtTypeName.contains("GraphicType") 
					 || bdtTypeName.contains("SoundType")
					 || bdtTypeName.contains("VideoType")){
				   cdtPris.add("Binary"); xbts.add("xsd:base64Binary"); setDefaultIndex(0);
				   cdtPris.add("Binary"); xbts.add("xsd:hexBinary");
			   }
			   else if (bdtTypeName.contains("CodeType") || bdtTypeName.equals("CodeContentType") 
					 || bdtTypeName.contains("IDType") || bdtTypeName.equals("URIType") || bdtTypeName.equals("IDContentType")
					 || bdtTypeName.contains("NameType")
					 || bdtTypeName.contains("TextType") || bdtTypeName.equals("NormalizedStringType") || bdtTypeName.equals("StringType") || bdtTypeName.equals("TokenType")){
				   cdtPris.add("NormalizedString"); xbts.add("xsd:normalizedString"); 
				   cdtPris.add("String"); xbts.add("xsd:string");
				   cdtPris.add("Token"); xbts.add("xsd:token");
				   
				   if(bdtTypeName.equals("CodeType") || bdtTypeName.equals("CodeType_1DEB05")
				   || bdtTypeName.equals("IDType")  || bdtTypeName.equals("IDType_D995CD") || bdtTypeName.equals("URIType") || bdtTypeName.equals("IDContentType") || bdtTypeName.equals("IDType_B3F14E")
				   || bdtTypeName.equals("NormalizedStringType") || bdtTypeName.equals("TextType_0VCBZ5")){
					   setDefaultIndex(0); //xsd:normalizedString
				   }
				   if(bdtTypeName.equals("NameType") || bdtTypeName.equals("NameType_02FC2Z")
				   || bdtTypeName.equals("StringType") || bdtTypeName.equals("TextType") || bdtTypeName.equals("TextType_62S0B4") || bdtTypeName.equals("TextType_62S0C1")){
					   setDefaultIndex(1); //xsd:string
				   }
				   if(bdtTypeName.equals("TokenType") || bdtTypeName.equals("TextType_0F0ZX1") || bdtTypeName.equals("CodeType_1E7368") || bdtTypeName.equals("CodeContentType")){
					   setDefaultIndex(2); //xsd:token
				   }
			   }
			   
			   else if (bdtTypeName.contains("DateTimeType")){
				   cdtPris.add("TimePoint"); xbts.add("xsd:dateTime");
				   cdtPris.add("TimePoint"); xbts.add("xsd:time");
				   cdtPris.add("TimePoint"); xbts.add("xsd:date");
				   cdtPris.add("TimePoint"); xbts.add("xsd:gYearMonth");
				   cdtPris.add("TimePoint"); xbts.add("xsd:gYear");
				   cdtPris.add("TimePoint"); xbts.add("xsd:gMonthDay");
				   cdtPris.add("TimePoint"); xbts.add("xsd:gDay");
				   cdtPris.add("TimePoint"); xbts.add("xsd:gMonth");
				   cdtPris.add("TimePoint"); xbts.add("xsd:token");	setDefaultIndex(8);
			   }	
			   else if (bdtTypeName.contains("TimeType") || bdtTypeName.equals("DayOfWeekHourMinuteUTCType")){
				   cdtPris.add("TimePoint"); xbts.add("xsd:time");
				   cdtPris.add("TimePoint"); xbts.add("xsd:token");	setDefaultIndex(1);
			   }
			   else if (bdtTypeName.contains("DateType")){
				   cdtPris.add("TimePoint"); xbts.add("xsd:time");		//0
				   cdtPris.add("TimePoint"); xbts.add("xsd:date");		//1
				   cdtPris.add("TimePoint"); xbts.add("xsd:gYearMonth");//2
				   cdtPris.add("TimePoint"); xbts.add("xsd:gYear");		//3
				   cdtPris.add("TimePoint"); xbts.add("xsd:gMonthDay");	//4
				   cdtPris.add("TimePoint"); xbts.add("xsd:gDay");		//5
				   cdtPris.add("TimePoint"); xbts.add("xsd:gMonth");	//6
				   cdtPris.add("TimePoint"); xbts.add("xsd:token");		//7
				   
				   if(bdtTypeName.equals("DateType") || bdtTypeName.equals("DateType_238C51")){
					   setDefaultIndex(7); //xsd:token
				   }
				   if(bdtTypeName.equals("DayDateType") || bdtTypeName.equals("DateType_DB95C8")){
					   setDefaultIndex(5); //xsd:gDay
				   }
				   if(bdtTypeName.equals("MonthDateType") || bdtTypeName.equals("DateType_0C267D")){
					   setDefaultIndex(6); //xsd:gMonth
				   }
				   if(bdtTypeName.equals("MonthDayDateType") || bdtTypeName.equals("DateType_5B057B")){
					   setDefaultIndex(4); //xsd:gMonthDay
				   }
				   if(bdtTypeName.equals("YearDateType") || bdtTypeName.equals("DateType_57D5E1")){
					   setDefaultIndex(3); //xsd:gYear
				   }
				   if(bdtTypeName.equals("YearMonthDateType") || bdtTypeName.equals("DateType_BBCC14")){
					   setDefaultIndex(2); //xsd:gYearMonth
				   }
			   }
			   else if (bdtTypeName.contains("DurationType") || bdtTypeName.equals("DurationMeasureType")){
				   cdtPris.add("TimeDuration"); xbts.add("xsd:duration");
				   cdtPris.add("TimeDuration"); xbts.add("xsd:token"); setDefaultIndex(1);
			   }
			   else if (bdtTypeName.contains("IndicatorType")){
				   cdtPris.add("Boolean"); xbts.add("xsd:boolean"); setDefaultIndex(0);
			   }
			   else if (bdtTypeName.contains("OrdinalType")){
				   cdtPris.add("Integer"); xbts.add("xsd:integer"); setDefaultIndex(0);
				   cdtPris.add("Integer"); xbts.add("xsd:nonNegativeInteger");
				   cdtPris.add("Integer"); xbts.add("xsd:positiveInteger");
			   }
			   else if (bdtTypeName.contains("ValueType")){
				   cdtPris.add("Decimal"); xbts.add("xsd:decimal");
				   cdtPris.add("Double"); xbts.add("xsd:double");
				   cdtPris.add("Double"); xbts.add("xsd:float");
				   cdtPris.add("Float"); xbts.add("xsd:float");
				   cdtPris.add("Integer"); xbts.add("xsd:integer");
				   cdtPris.add("Integer"); xbts.add("xsd:nonNegativeInteger");
				   cdtPris.add("Integer"); xbts.add("xsd:positiveInteger");
				   cdtPris.add("NormalizedString"); xbts.add("xsd:normalizedString");
				   cdtPris.add("String"); xbts.add("xsd:string"); setDefaultIndex(8);
				   cdtPris.add("Token"); xbts.add("xsd:token");
				   
				   if(bdtTypeName.equals("ValueType_039C44")){
					   setDefaultIndex(4);//xsd:integer
				   }
			   } 
		   }


		public int getCdtId() {
			return cdtId;
		}

		public void setCdtId(int cdtId) {
			this.cdtId = cdtId;
		}

		public int getDefaultIndex() {
			return defaultIndex;
		}

		public void setDefaultIndex(int defaultIndex) {
			this.defaultIndex = defaultIndex;
		}
		   
	   }
	@Test
	public void testPopulateBDTPriRestriTable() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		 	
		 Map<String, ExpectedBDTPrimitiveRestriction> expectedBDTPriRestris = new HashMap();
		 	// cdt_id	TypeName
			// 1		AmountType
		 	// 2		BinaryObjectType
		 	// 3		CodeType
			// 4		DateType
		 	// 5		DateTimeType
		 	// 6 		DurationType
		 	// 7 		GraphicType
	        // 8		IDType
		 	// 9		IndicatorType
		 	//10		MeasureType
		 	//11		NameType
		 	//12		NumberType
		 	//13		OrdinalType
		 	//14		PercentType
		 	//15		PictureType
		 	//16		QuantityType
		 	//17		RateType
		 	//18		RatioType
		 	//19		SoundType
		 	//20		TextType
		 	//21		TimeType
		 	//22		ValueType
		 	//23		VideoType		 
		 expectedBDTPriRestris.put("oagis-id-109055a967bd4cf19ee3320755b01f8d", new ExpectedBDTPrimitiveRestriction("AmountType",1));
		 expectedBDTPriRestris.put("oagis-id-f2c5dcba0088440d866ea23a81876280", new ExpectedBDTPrimitiveRestriction("BinaryObjectType",2));
		 expectedBDTPriRestris.put("oagis-id-9ec6be30dabf45d5b53b765634be2412", new ExpectedBDTPrimitiveRestriction("GraphicType",7));
		 expectedBDTPriRestris.put("oagis-id-dc994532fe464847acf84a54548276ff", new ExpectedBDTPrimitiveRestriction("SoundType",19));
		 expectedBDTPriRestris.put("oagis-id-83d4cc94be8249a3b8cbe7e1c2ecb417", new ExpectedBDTPrimitiveRestriction("VideoType",23));
		 expectedBDTPriRestris.put("oagis-id-3318aed9165847e3afb907724db2b65c", new ExpectedBDTPrimitiveRestriction("CodeType",3));
		 expectedBDTPriRestris.put("oagis-id-bea4dcd433d54aa698db2176cab33c19", new ExpectedBDTPrimitiveRestriction("IDType",8));
		 expectedBDTPriRestris.put("oagis-id-3cbb2f0b87254ff696e9315cd863f613", new ExpectedBDTPrimitiveRestriction("MeasureType",10));
		 expectedBDTPriRestris.put("oagis-id-bf66e0afea2c4c2da7bc69af14ca23c9", new ExpectedBDTPrimitiveRestriction("NameType",11));
		 expectedBDTPriRestris.put("oagis-id-5212437eb6f045f98b072db0ce971409", new ExpectedBDTPrimitiveRestriction("QuantityType",16));
		 expectedBDTPriRestris.put("oagis-id-d97b8cf6a26f408db148163485796d15", new ExpectedBDTPrimitiveRestriction("TextType",20));
		 expectedBDTPriRestris.put("oagis-id-dd0c8f86b160428da3a82d2866a5b48d", new ExpectedBDTPrimitiveRestriction("DateTimeType",5));
		 expectedBDTPriRestris.put("oagis-id-ee2b3bf53bd44b21960ff8575891c638", new ExpectedBDTPrimitiveRestriction("DurationType",6));
		 expectedBDTPriRestris.put("oagis-id-ef32205ede95407f981064a45ffa652c", new ExpectedBDTPrimitiveRestriction("IndicatorType",9));
		 expectedBDTPriRestris.put("oagis-id-06083bfba01d4213a852830000e939b9", new ExpectedBDTPrimitiveRestriction("NumberType",12));
		 expectedBDTPriRestris.put("oagis-id-06083bfba01d4213a852830000e12051", new ExpectedBDTPrimitiveRestriction("OrdinalType",13));
		 expectedBDTPriRestris.put("oagis-id-57efecfe17e64e20b83adccce3159a9e", new ExpectedBDTPrimitiveRestriction("PercentType",14));
		 expectedBDTPriRestris.put("oagis-id-83ea28a4218e447ebe99113901b2c70f", new ExpectedBDTPrimitiveRestriction("TimeType",21));
		 expectedBDTPriRestris.put("oagis-id-d006eb3550364c61bc10cac70763e677", new ExpectedBDTPrimitiveRestriction("ValueType",22));
		 expectedBDTPriRestris.put("oagis-id-3ecb2fb750c144c58c1b68d2fa2a36ae", new ExpectedBDTPrimitiveRestriction("DateType",4));
		 expectedBDTPriRestris.put("oagis-id-e6f93bd0dc934ab2af11bd46888c1233", new ExpectedBDTPrimitiveRestriction("AmountType_0723C8",1));
		 expectedBDTPriRestris.put("oagis-id-6eae247688734e6da1dfdadb89c3e43a", new ExpectedBDTPrimitiveRestriction("BinaryObjectType_4277E5",2));
		 expectedBDTPriRestris.put("oagis-id-0c482df00c2343cd99b1b5c1637a199d", new ExpectedBDTPrimitiveRestriction("CodeType_1DEB05",3));
		 expectedBDTPriRestris.put("oagis-id-f074a322acff4705bbef417505f9bf11", new ExpectedBDTPrimitiveRestriction("DateType_238C51",4));
		 expectedBDTPriRestris.put("oagis-id-a5cfd20385314a63afc1ffcf6357a08b", new ExpectedBDTPrimitiveRestriction("DateTimeType_AD9DD9",5));
		 expectedBDTPriRestris.put("oagis-id-f16cdbda66d2441cac9e99615c99e70e", new ExpectedBDTPrimitiveRestriction("DurationType_JJ5401",6));
		 expectedBDTPriRestris.put("oagis-id-a3101ac1f4734f408d0b01e2ba182648", new ExpectedBDTPrimitiveRestriction("GraphicType_3FDF3D",7));
		 expectedBDTPriRestris.put("oagis-id-6e141689c22944a083798b9dbba8b47f", new ExpectedBDTPrimitiveRestriction("IDType_D995CD",8));
		 expectedBDTPriRestris.put("oagis-id-c62bf0f41c964349b874b8f397f673ec", new ExpectedBDTPrimitiveRestriction("IndicatorType_CVW231",9));
		 expectedBDTPriRestris.put("oagis-id-bf1305892fcb4e2e9eccec5ae693d73d", new ExpectedBDTPrimitiveRestriction("MeasureType_671290",10));
		 expectedBDTPriRestris.put("oagis-id-8ef2aeaecfa645088c4bf4b424905596", new ExpectedBDTPrimitiveRestriction("NameType_02FC2Z",11));
		 expectedBDTPriRestris.put("oagis-id-e57e1a2b7be44356a2256bc46f61ec36", new ExpectedBDTPrimitiveRestriction("NumberType_BE4776",12));
		 expectedBDTPriRestris.put("oagis-id-23a9c386c4304400a8f1dd620b26035b", new ExpectedBDTPrimitiveRestriction("OrdinalType_PQALZM",13));
		 expectedBDTPriRestris.put("oagis-id-af8d69bed6454c0eb9cc494977993b03", new ExpectedBDTPrimitiveRestriction("PercentType_481002",14));
		 expectedBDTPriRestris.put("oagis-id-6d24dd8e55414acc915b1a2c7e358552", new ExpectedBDTPrimitiveRestriction("QuantityType_201330",16));
		 expectedBDTPriRestris.put("oagis-id-7242ea809d804f9ab4e08766a6117710", new ExpectedBDTPrimitiveRestriction("SoundType_697AE6",19));
		 expectedBDTPriRestris.put("oagis-id-89be97039be04d6f9cfda107d75926b4", new ExpectedBDTPrimitiveRestriction("TextType_62S0B4",20));
		 expectedBDTPriRestris.put("oagis-id-6659e405ac8d43268d2a10d451eea261", new ExpectedBDTPrimitiveRestriction("TimeType_100DCA",21));
		 expectedBDTPriRestris.put("oagis-id-641fdee1d3114629a15902311d895ca2", new ExpectedBDTPrimitiveRestriction("ValueType_D19E7B",22));
		 expectedBDTPriRestris.put("oagis-id-3292eaa5630b48ecb7c4249b0ddc760e", new ExpectedBDTPrimitiveRestriction("VideoType_539B44",23));
		 expectedBDTPriRestris.put("oagis-id-d26b22f9103744edb0a4d3728aefc26e", new ExpectedBDTPrimitiveRestriction("NormalizedStringType",20));
		 expectedBDTPriRestris.put("oagis-id-e28a3e09aa6e42339e11a0c740362ca9", new ExpectedBDTPrimitiveRestriction("TokenType",20));
		 expectedBDTPriRestris.put("oagis-id-310d5bf351c143ed80c16ee9ef837271", new ExpectedBDTPrimitiveRestriction("StringType",20));
		 expectedBDTPriRestris.put("oagis-id-5a2ed18041c04f3e995c773480b0076d", new ExpectedBDTPrimitiveRestriction("DayDateType",4));
		 expectedBDTPriRestris.put("oagis-id-af5bfda6510443a5a468bd1df713ff4c", new ExpectedBDTPrimitiveRestriction("MonthDateType",4));
		 expectedBDTPriRestris.put("oagis-id-86ce40c683224461a3d4747410c551c3", new ExpectedBDTPrimitiveRestriction("MonthDayDateType",4));
		 expectedBDTPriRestris.put("oagis-id-a83c14386c7547669c8c9a516ff4c54e", new ExpectedBDTPrimitiveRestriction("YearDateType",4));
		 expectedBDTPriRestris.put("oagis-id-004a7da25119417ba44a1f43a2585d0d", new ExpectedBDTPrimitiveRestriction("YearMonthDateType",4));
		 expectedBDTPriRestris.put("oagis-id-3d47ac271e344e6094791b6e93fbce26", new ExpectedBDTPrimitiveRestriction("URIType",8));
		 expectedBDTPriRestris.put("oagis-id-10ef9f34e0504a71880c967c82ac039f", new ExpectedBDTPrimitiveRestriction("DurationMeasureType",6));
		 expectedBDTPriRestris.put("oagis-id-9b8fed621a7148a5b7fb2f04b80381be", new ExpectedBDTPrimitiveRestriction("IntegerNumberType",12));
		 expectedBDTPriRestris.put("oagis-id-ec9821a975e84ad9804265b0f082a36b", new ExpectedBDTPrimitiveRestriction("PositiveIntegerNumberType",12));
		 expectedBDTPriRestris.put("oagis-id-5a2ed18041c04f3e995c7734386ae380", new ExpectedBDTPrimitiveRestriction("DayOfWeekHourMinuteUTCType",21));
		 expectedBDTPriRestris.put("oagis-id-3049eed90b924d699f1102b946843725", new ExpectedBDTPrimitiveRestriction("DateType_DB95C8",4));
		 expectedBDTPriRestris.put("oagis-id-3bc40b222d994d9b9fb5d4a33319a146", new ExpectedBDTPrimitiveRestriction("DateType_5B057B",4));
		 expectedBDTPriRestris.put("oagis-id-52df32ab374440f0ac456a1abe66cb94", new ExpectedBDTPrimitiveRestriction("DateType_0C267D",4));
		 expectedBDTPriRestris.put("oagis-id-6712fbca652e49ac9739396377b090cb", new ExpectedBDTPrimitiveRestriction("DateType_BBCC14",4));
		 expectedBDTPriRestris.put("oagis-id-0a7f3544ea954099aa06afe488417136", new ExpectedBDTPrimitiveRestriction("DateType_57D5E1",4));
		 expectedBDTPriRestris.put("oagis-id-ff84535456d44233b6f0976d993b442d", new ExpectedBDTPrimitiveRestriction("IDType_B3F14E",8));
		 expectedBDTPriRestris.put("oagis-id-d614ed8726ff482c9c5a8183d735d9ed", new ExpectedBDTPrimitiveRestriction("NumberType_B98233",12));
		 expectedBDTPriRestris.put("oagis-id-6b81b03c96cc47f08ccb26838853012d", new ExpectedBDTPrimitiveRestriction("NumberType_201301",12));
		 expectedBDTPriRestris.put("oagis-id-89be97039be04d6f9cfda107d75926b5", new ExpectedBDTPrimitiveRestriction("TextType_62S0C1",20));
		 expectedBDTPriRestris.put("oagis-id-42a03ed19450453da6c87fe8eadabfa4", new ExpectedBDTPrimitiveRestriction("TextType_0VCBZ5",20));
		 expectedBDTPriRestris.put("oagis-id-d5cb8551edf041389893fee25a496395", new ExpectedBDTPrimitiveRestriction("TextType_0F0ZX1",20));
		 expectedBDTPriRestris.put("oagis-id-d2f721a297684b538e7dbb88cf5526bc", new ExpectedBDTPrimitiveRestriction("CodeType_1E7368",3));
		 expectedBDTPriRestris.put("oagis-id-0fb76e8565244977b1239327ca436f76", new ExpectedBDTPrimitiveRestriction("ValueType_039C44",22));
		 expectedBDTPriRestris.put("oagis-id-5646bf52a97b48adb50ded6ff8c38354", new ExpectedBDTPrimitiveRestriction("CodeContentType",3));
		 expectedBDTPriRestris.put("oagis-id-08d6ade226fd42488b53c0815664e246", new ExpectedBDTPrimitiveRestriction("IDContentType",8));

		 for(Map.Entry<String, ExpectedBDTPrimitiveRestriction> entry: expectedBDTPriRestris.entrySet()){
	        	String key = entry.getKey();
	        	ExpectedBDTPrimitiveRestriction value = entry.getValue();
	        	System.out.println(key);
	        	DataType getDT = dataTypeRepository.findOneByGuid(key);
	        	if(getDT==null){
	        		assertTrue(getDT!=null);
	        	}
	        	else {
	        		
	        		List<BusinessDataTypePrimitiveRestriction> bdtPris = businessDataTypePrimitiveRestrictionRepository.findByBdtId(getDT.getDtId());
	        		
	        		ArrayList<String> cdtPriNames = new ArrayList<String>();
	        		ArrayList<String> xbtNames = new ArrayList<String>();
	        		int defaultInd = -1;
	        		for(int i=0; i<bdtPris.size(); i++){
	        			int find = bdtPris.get(i).getCdtAwdPriXpsTypeMapId();
	        			CoreDataTypeAllowedPrimitiveExpressionTypeMap map = coreDataTypeAllowedPrimitiveExpressionTypeMapRepository.findOne(find);
	        			CoreDataTypeAllowedPrimitive cdtAwdPri = coreDataTypeAllowedPrimitiveRepository.findOne(map.getCdtAwdPriId());
	        			CoreDataTypePrimitive cdtPri = coreDataTypePrimitiveRepository.findOne(cdtAwdPri.getCdtPriId());
	        			XSDBuiltInType xbt = xsdBuiltInTypeRepository.findOne(map.getXbtId());
	        			
	        			cdtPriNames.add(cdtPri.getName());
	        			xbtNames.add(xbt.getBuiltInType());
	        			if(bdtPris.get(i).isDefault()){
	        				defaultInd = i;
	        			}
	        		}
	        		
	        		assertTrue(xbtNames.size()==cdtPriNames.size());
	        		assertTrue(defaultInd!=-1);
	        		assertTrue(value.cdtPris.size()==cdtPriNames.size());
	        		assertTrue(value.xbts.size()==xbtNames.size());
	        		
	        		int testedXBTNum = 0;
	        		for(int i=0; i<value.xbts.size(); i++){	   
	        			String expected = value.cdtPris.get(i)+" "+value.xbts.get(i);
	        			for(int j=0; j<xbtNames.size(); j++){	        				
		        			String actual = cdtPriNames.get(j)+" "+xbtNames.get(j);	        				
	        				if(expected.equals(actual)){
	        					if(value.getDefaultIndex()==i){
	        						assertTrue(defaultInd == j);
	        					}
	        					testedXBTNum++;
	        					break;
	        				}	        				
	        			}		
	        		}	        		
	        		assertTrue(value.xbts.size()==testedXBTNum);
	        	}
        	}
        }
	      
	}