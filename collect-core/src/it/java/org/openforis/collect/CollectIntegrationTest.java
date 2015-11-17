package org.openforis.collect;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.openforis.collect.manager.SurveyManager;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.SurveyImportException;
import org.openforis.idm.metamodel.xml.IdmlParseException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author S. Ricci
 *
 */
public abstract class CollectIntegrationTest extends CollectTest {

	@Autowired
	protected CollectSurveyContext collectSurveyContext;
	@Autowired
	protected SurveyDao surveyDao;
	@Autowired
	protected SurveyManager surveyManager;
	
	protected CollectSurvey loadSurvey() throws IdmlParseException {
		InputStream is = ClassLoader.getSystemResourceAsStream("test.idm.xml");
		CollectSurvey survey = surveyDao.unmarshalIdml(is);
		survey.setName("archenland1");
		return survey;
	}

	@SuppressWarnings("deprecation")
	protected CollectSurvey importModel() throws SurveyImportException, IdmlParseException {
		CollectSurvey survey = (CollectSurvey) loadSurvey();
		surveyManager.importModel(survey);
		return survey;
	}
	
	protected CollectSurvey createSurvey() {
		CollectSurvey createSurvey = (CollectSurvey) collectSurveyContext.createSurvey();
		return createSurvey;
	}
	
	protected File getSystemResourceFile(String fileName) {
		try {
			URL fileUrl = ClassLoader.getSystemResource(fileName);
			return new File(fileUrl.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
