import AbstractService from './AbstractService';
import { Survey } from '../model/Survey';

export default class SurveyService extends AbstractService {

    fetchById(surveyId) {
        return this.get('survey/' + surveyId).then(res => new Survey(res))
    }

    createNewSurvey(name, templateType, defaultLanguageCode, userGroupId) {
        return this.post('survey', {
            name: name,
            templateType: templateType,
            defaultLanguageCode:  defaultLanguageCode,
            userGroupId: userGroupId
        })
    }

    validateSurveyCreation(name, templateType, defaultLanguageCode, userGroupId) {
        return this.post('survey/validatecreation', {
            name: name,
            templateType: templateType,
            defaultLanguageCode:  defaultLanguageCode,
            userGroupId: userGroupId
        })
    }

    changeUserGroup(surveyName, userGroupId, loggedUserId) {
        return this.post(`survey/${surveyName}/changeusergroup`, {
            userGroupId,
            loggedUserId
        })
    }

    createTemporarySurvey(publishedSurveyId) {
        return this.post('survey/cloneintotemporary/' + publishedSurveyId)
    }
    
    fetchAllSummaries() {
        return this.get('survey', {
            includeTemporary: true
        })
    }

    uploadSurveyFile(file) {
        return this.postFormData('survey/prepareimport', {
            file: file
        })
    }

    validateSurveyImport(name, userGroupId) {
        return this.post('survey/validateimport', {
            name: name,
            userGroupId: userGroupId
        })
    }

    startSurveyFileImport(name, userGroupId) {
        return this.post('survey/startimport', {
            name: name,
            userGroupId: userGroupId
        })
    }

    fetchSurveyImportStatus() {
        return this.get('survey/importstatus')
    }

    startExport(surveyId, surveyUri, surveyType, outputFormat, languageCode, skipValidation) {
        return this.post('survey/export/' + surveyId, {
            surveyId: surveyId,
            surveyUri: surveyUri,
            surveyType: surveyType,
            outputFormat: outputFormat,
            languageCode: languageCode,
            skipValidation: skipValidation
        })
    }

    downloadExportResult(surveyId) {
        return this.downloadFile(this.BASE_URL + 'survey/export/' + surveyId + '/result')
    }

    publish(surveyId) {
        return this.post('survey/publish/' + surveyId)
    }

    unpublish(surveyId) {
        return this.post('survey/unpublish/' + surveyId)
    }

    delete(surveyId) {
        return this.post('survey/delete/' + surveyId)
    }

    startClone(originalSurveyName, originalSurveyType, newSurveyName) {
        return this.post('survey/clone', {
            originalSurveyName: originalSurveyName,
            originalSurveyType: originalSurveyType,
		    newSurveyName: newSurveyName
        })
    }

    getClonedSurveyId() {
        return this.get('survey/cloned/id')
    }

    validateClone(originalSurveyName, originalSurveyType, newSurveyName) {
        return this.post('survey/validate/clone', {
            originalSurveyName: originalSurveyName,
            originalSurveyType: originalSurveyType,
            newSurveyName: newSurveyName
        })
    }
}