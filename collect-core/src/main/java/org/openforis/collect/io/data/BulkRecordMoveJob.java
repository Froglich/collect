/**
 * 
 */
package org.openforis.collect.io.data;

import java.util.List;

import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.concurrency.Job;
import org.openforis.concurrency.Task;
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
public class BulkRecordMoveJob extends Job {

	@Autowired
	private RecordManager recordManager;
	
	//input
	private CollectSurvey survey;
	private String rootEntity;
	private User adminUser;
	private Step fromStep;
	private boolean promote;
	
	@Override
	protected void buildTasks() throws Throwable {
		addTask(new BulkRecordMoveTask());
	}

	public void setSurvey(CollectSurvey survey) {
		this.survey = survey;
	}
	
	public void setRootEntity(String rootEntity) {
		this.rootEntity = rootEntity;
	}
	
	public void setAdminUser(User adminUser) {
		this.adminUser = adminUser;
	}
	
	public void setFromStep(Step fromStep) {
		this.fromStep = fromStep;
	}
	
	public void setPromote(boolean promote) {
		this.promote = promote;
	}
	
	private class BulkRecordMoveTask extends Task {

		@Override
		protected long countTotalItems() {
			int count = recordManager.countRecords(survey, rootEntity, fromStep);
			return Integer.valueOf(count).longValue();
		}
		
		@Override
		protected void execute() throws Throwable {
			List<CollectRecord> summaries = recordManager.loadSummaries(survey, rootEntity, fromStep);
			for (CollectRecord summary : summaries) {
				if (promote) {
					recordManager.promote(survey, summary.getId(), summary.getStep(), adminUser);
				} else {
					recordManager.demote(survey, summary.getId(), summary.getStep(), adminUser);
				}
				incrementItemsProcessed();
			}
		}
		
	}
	
}
