package org.openforis.collect.remoting.service;

import java.io.File;

import org.openforis.collect.io.data.CSVDataImportProcess;
import org.openforis.collect.io.data.CSVDataImportProcess.CSVDataImportSettings;
import org.openforis.collect.io.exception.DataImportExeption;
import org.openforis.collect.manager.RecordSessionManager;
import org.openforis.collect.manager.process.ProcessStatus;
import org.openforis.collect.manager.referencedataimport.proxy.ReferenceDataImportStatusProxy;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.web.session.SessionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.annotation.Secured;

/**
 * 
 * @author S. Ricci
 *
 */
public class CSVDataImportService extends ReferenceDataImportService<ReferenceDataImportStatusProxy, CSVDataImportProcess> { 
	
	@Autowired
	private RecordSessionManager sessionManager;
	@Autowired
	private ApplicationContext applicationContext;
	
	@Secured("ROLE_ADMIN")
	public ReferenceDataImportStatusProxy start(String tempFileName, int parentEntityId, CollectRecord.Step step, 
			boolean transactional, boolean validateRecords, 
			boolean insertNewRecords, String newRecordVersionName,
			boolean deleteExistingEntities) throws DataImportExeption {
		if ( importProcess == null || ! importProcess.getStatus().isRunning() ) {
			File importFile = new File(tempFileName);
			SessionState sessionState = sessionManager.getSessionState();
			CollectSurvey survey = sessionState.getActiveSurvey();
			importProcess = (CSVDataImportProcess) applicationContext.getBean(
					transactional ? "transactionalCsvDataImportProcess": "csvDataImportProcess");
			importProcess.setFile(importFile);
			importProcess.setSurvey(survey);
			importProcess.setParentEntityDefinitionId(parentEntityId);
			importProcess.setStep(step);
			CSVDataImportSettings settings = new CSVDataImportProcess.CSVDataImportSettings();
			settings.setRecordValidationEnabled(validateRecords);
			settings.setInsertNewRecords(insertNewRecords);
			settings.setNewRecordVersionName(newRecordVersionName);
			settings.setDeleteExistingEntities(deleteExistingEntities);
			importProcess.setSettings(settings);
			importProcess.init();
			ProcessStatus status = importProcess.getStatus();
			if ( status != null && ! importProcess.getStatus().isError() ) {
				startProcessThread();
			}
		}
		return getStatus();
	}

	@Override
	@Secured("ROLE_ADMIN")
	public ReferenceDataImportStatusProxy getStatus() {
		if ( importProcess == null ) {
			return null;
		} else {
			return new ReferenceDataImportStatusProxy(importProcess.getStatus());
		}
	}
	
}
