package org.openforis.collect.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.xml.InvalidIdmlException;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class ModelDAOIntegrationTest {
	private final Log log = LogFactory.getLog(ModelDAOIntegrationTest.class);
	
	@Autowired
	protected SurveyDAO surveyDao;
	
	@Autowired
	protected RecordDAO recordDao;
	
	@Autowired
	protected RecordManager recordManager;
	
	@Test
	public void testCRUD() throws Exception  {
//		try {
		// LOAD MODEL
		CollectSurvey survey = surveyDao.load("archenland1");

		if ( survey == null ) {
			// IMPORT MODEL
			survey = importModel();
		}
		
		testLoadAllSurveys("archenland1");

		// SAVE NEW
		CollectRecord record = createTestRecord(survey);
		recordDao.saveOrUpdate(record);
		
		String saved = record.toString();
		log.debug("Saving record:\n"+saved);
		
		// RELOAD
		record = recordDao.load(survey, recordManager, record.getId());
		String reloaded = record.toString();
		log.debug("Reloaded as:\n"+reloaded);
		
		assertEquals(saved, reloaded);
		
		// UPDATE
//		updateRecord(record);
		
//		assertEquals(1, cluster.getCount("time_study"));

//		recordDao.saveOrUpdate(record);
//		} catch (DataAccessException ex){
//			ex.getCause().getCause().getCause().printStackTrace();
//		}
	}

	private void testLoadAllSurveys(String surveyName) {
		List<CollectSurvey> list = this.surveyDao.loadAll();
		assertNotNull(list);
		for (Survey survey : list) {
			if ( survey.getName().equals(surveyName) ) {
				return;
			}
		}
		fail(surveyName+" not loaded by surveyDao.loadAll()");
	}
	
	@Test
	public void testSurveyNotFoundById() {		
		Survey survey = surveyDao.load(-100);
		assertNull(survey);
	}
	
	@Test
	public void testSurveyNotFoundByName() {		
		Survey survey = surveyDao.load("!!!!!!");
		assertNull(survey);
	}

	private CollectSurvey importModel() throws IOException, SurveyImportException, InvalidIdmlException {
		URL idm = ClassLoader.getSystemResource("test.idm.xml");
		InputStream is = idm.openStream();
		CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext();
		SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
		CollectSurvey survey = (CollectSurvey) surveyUnmarshaller.unmarshal(is);
		surveyDao.importModel(survey);
		return survey;
	}

	private CollectRecord createTestRecord(CollectSurvey survey) {
		CollectRecord record = new CollectRecord(recordManager, survey, "2.0");
		Entity cluster = record.createRootEntity("cluster");
		record.setCreationDate(new GregorianCalendar(2011, 12, 31, 23, 59).getTime());
		//record.setCreatedBy("ModelDAOIntegrationTest");
		record.setStep(Step.ENTRY);
		String id = "123_456";
		
		addTestValues(cluster, id);
			
		//set counts
		record.getEntityCounts().add(2);
		
		//set keys
		record.getRootEntityKeys().add(id);
		
		return record;
	}

	private void addTestValues(Entity cluster, String id) {
		cluster.addValue("id", new Code(id));
		cluster.addValue("gps_realtime", Boolean.TRUE);
		cluster.addValue("region", new Code("001"));
		cluster.addValue("district", new Code("002"));
		cluster.addValue("crew_no", 10);
		cluster.addValue("map_sheet", "value 1");
		cluster.addValue("map_sheet", "value 2");
		cluster.addValue("vehicle_location", new Coordinate(432423423l, 4324324l,"srs"));
		cluster.addValue("gps_model", "TomTom 1.232");
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,14));
			ts.addValue("start_time", new Time(8,15));
			ts.addValue("end_time", new Time(15,29));
		}
		{
			Entity ts = cluster.addEntity("time_study");
			ts.addValue("date", new Date(2011,2,15));
			ts.addValue("start_time", new Time(8,32));
			ts.addValue("end_time", new Time(11,20));
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("1"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 54.2);
			tree1.addValue("total_height", 2.0);
//			tree1.addValue("bole_height", (Double) null).setMetadata(new CollectAttributeMetadata('*',null,"No value specified"));
			RealAttribute boleHeight = tree1.addValue("bole_height", (Double) null);
			boleHeight.getField().setSymbol('*');
			boleHeight.getField().setRemarks("No value specified");
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 82.8);
			tree2.addValue("total_height", 3.0);
		}
		{
			Entity plot = cluster.addEntity("plot");
			plot.addValue("no", new Code("2"));
			Entity tree1 = plot.addEntity("tree");
			tree1.addValue("tree_no", 1);
			tree1.addValue("dbh", 34.2);
			tree1.addValue("total_height", 2.0);
			Entity tree2 = plot.addEntity("tree");
			tree2.addValue("tree_no", 2);
			tree2.addValue("dbh", 85.8);
			tree2.addValue("total_height", 4.0);
		}
	}

//	@Test
	public void testLoadRecordSummaries() {
		CollectSurvey survey = surveyDao.load("archenland1");
		//get the first root entity
		EntityDefinition rootEntity = survey.getSchema().getRootEntityDefinitions().get(0);
		String rootEntityName = rootEntity.getName();
		int offset = 0;
		int maxNumberOfRecords = 1;
		String orderByFieldName = "key_id";
		String filter = null;
		List<CollectRecord> list = this.recordDao.loadSummaries(survey, recordManager, rootEntityName, offset, maxNumberOfRecords, orderByFieldName, filter);
		assertNotNull(list);
		assertEquals(1, list.size());
		
		CollectRecord summary = list.get(0);
		assertEquals(Step.ENTRY, summary.getStep());
	}
}
