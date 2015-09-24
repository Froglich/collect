/**
 * 
 */
package org.openforis.collect.io.data;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openforis.collect.io.SurveyBackupJob;
import org.openforis.collect.io.SurveyBackupJob.OutputFormat;
import org.openforis.collect.io.data.backup.BackupStorageManager;
import org.openforis.collect.io.data.restore.RestoredBackupStorageManager;
import org.openforis.collect.manager.RecordFileManager;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.manager.UserManager;
import org.openforis.collect.model.RecordFilter;
import org.openforis.concurrency.Task;
import org.openforis.concurrency.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author S. Ricci
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataRestoreJob extends DataRestoreBaseJob {
	
	@Autowired
	private RecordFileManager recordFileManager;
	@Autowired
	protected RestoredBackupStorageManager restoredBackupStorageManager;
	@Autowired
	protected BackupStorageManager backupStorageManager;

	//input parameters
	private boolean overwriteAll;
	private boolean restoreUploadedFiles;
	private List<Integer> entryIdsToImport; //ignored when overwriteAll is true
	private boolean storeRestoredFile;
	private File tempFile;

	@Override
	public void createInternalVariables() throws Throwable {
		super.createInternalVariables();
	}
	
	@Override
	protected void buildTasks() throws Throwable {
		super.buildTasks();
		if (storeRestoredFile) {
			if (isBackupNeeded()) {
				addTask(SurveyBackupJob.class);
			}
			addTask(new StoreBackupFileTask());
		}
		addTask(DataRestoreTask.class);
		if ( restoreUploadedFiles && isUploadedFilesIncluded() ) {
			addTask(RecordFileRestoreTask.class);
		}
	}
	
	private boolean isBackupNeeded() {
		if (newSurvey) {
			return false;
		}
		Date lastBackupDate = backupStorageManager.getLastBackupDate(surveyName);
		RecordFilter recordFilter = new RecordFilter(publishedSurvey);
		recordFilter.setModifiedSince(lastBackupDate);
		return recordManager.countRecords(recordFilter) > 0 || publishedSurvey.getModifiedDate().after(lastBackupDate);
	}

	private boolean isUploadedFilesIncluded() throws IOException {
		List<String> dataEntries = backupFileExtractor.listEntriesInPath(SurveyBackupJob.UPLOADED_FILES_FOLDER);
		return ! dataEntries.isEmpty();
	}

	@Override
	protected void initializeTask(Worker task) {
		if (task instanceof SurveyBackupJob) {
			SurveyBackupJob t = (SurveyBackupJob) task;
			t.setFull(true);
			t.setIncludeData(true);
			t.setIncludeRecordFiles(true);
			t.setOutputFormat(OutputFormat.DESKTOP_FULL);
			t.setRecordFilter(new RecordFilter(publishedSurvey));
			t.setSurvey(publishedSurvey);
		} else if ( task instanceof DataRestoreTask ) {
			DataRestoreTask t = (DataRestoreTask) task;
			t.setRecordManager(recordManager);
			t.setUserManager(userManager);
			t.setZipFile(zipFile);
			t.setOldBackupFormat(oldBackupFormat);
			t.setPackagedSurvey(packagedSurvey);
			t.setExistingSurvey(publishedSurvey);
			t.setOverwriteAll(overwriteAll);
			t.setEntryIdsToImport(entryIdsToImport);
		} else if ( task instanceof RecordFileRestoreTask ) {
			RecordFileRestoreTask t = (RecordFileRestoreTask) task;
			t.setRecordManager(recordManager);
			t.setRecordFileManager(recordFileManager);
			t.setZipFile(zipFile);
			t.setOldBackupFormat(oldBackupFormat);
			t.setOverwriteAll(overwriteAll);
			t.setEntryIdsToImport(entryIdsToImport);
			t.setSurvey(publishedSurvey);
		}
		super.initializeTask(task);
	}
	
	@Override
	protected void onCompleted() {
		super.onCompleted();
		if (storeRestoredFile) {
			restoredBackupStorageManager.moveToFinalFolder(surveyName, tempFile);
		}
	}

	public RecordManager getRecordManager() {
		return recordManager;
	}

	public void setRecordManager(RecordManager recordManager) {
		this.recordManager = recordManager;
	}
	
	public RecordFileManager getRecordFileManager() {
		return recordFileManager;
	}

	public void setRecordFileManager(RecordFileManager recordFileManager) {
		this.recordFileManager = recordFileManager;
	}
	
	public UserManager getUserManager() {
		return userManager;
	}
	
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public List<Integer> getEntryIdsToImport() {
		return entryIdsToImport;
	}
	
	public void setEntryIdsToImport(List<Integer> entryIdsToImport) {
		this.entryIdsToImport = entryIdsToImport;
	}
	
	public boolean isRestoreUploadedFiles() {
		return restoreUploadedFiles;
	}
	
	public void setRestoreUploadedFiles(boolean restoreUploadedFiles) {
		this.restoreUploadedFiles = restoreUploadedFiles;
	}

	public boolean isOverwriteAll() {
		return overwriteAll;
	}

	public void setOverwriteAll(boolean overwriteAll) {
		this.overwriteAll = overwriteAll;
	}
	
	public boolean isStoreRestoredFile() {
		return storeRestoredFile;
	}
	
	public void setStoreRestoredFile(boolean storeRestoredFile) {
		this.storeRestoredFile = storeRestoredFile;
	}

	private class StoreBackupFileTask extends Task {
		@Override
		protected void execute() throws Throwable {
			DataRestoreJob.this.tempFile = restoredBackupStorageManager.storeTemporaryFile(surveyName, file);
		}
		
	}
}
