import React, { Component } from 'react';
import { Switch, Route, Redirect } from 'react-router-dom'

import Header from 'components/Header';
import Sidebar from 'components/Sidebar';
import Breadcrumb from 'components/Breadcrumb';
import Aside from 'components/Aside';
import Footer from 'components/Footer';
import CurrentJobMonitorModal from 'containers/CurrentJobMonitorModal'

import HomePage from 'containers/HomePage'
import BackupDataExportPage from 'containers/datamanagement/BackupDataExportPage'
import BackupDataImportPage from 'containers/datamanagement/BackupDataImportPage'
import CsvDataExportPage from 'containers/datamanagement/CsvDataExportPage'
import CsvDataImportPage from 'containers/datamanagement/CsvDataImportPage'
import DashboardPage from 'containers/DashboardPage'
import DataCleansingPage from 'containers/DataCleansingPage'
import DataManagementPage from 'containers/datamanagement/DataManagementPage'
import MapPage from 'containers/MapPage'
import OldClientRecordEditPage from 'containers/datamanagement/OldClientRecordEditPage'
import SaikuPage from 'containers/SaikuPage'
import SurveyDesignerPage from 'containers/SurveyDesignerPage'
import SurveysListPage from 'containers/surveydesigner/SurveysListPage'
import UsersPage from 'containers/users/UsersPage'
import UserGroupsPage from 'containers/users/UserGroupsPage'
import UserGroupDetailsPage from 'containers/users/UserGroupDetailsPage'

class App extends Component {
  render() {
    return (
        <div className="app">
          <Header />
          <div className="app-body">
            <Sidebar {...this.props}/>
            <main className="main">
              <Breadcrumb />
              <div className="main-content-wrapper">
                <Switch>
                  <Route path="/" exact name="HomePage" component={HomePage}/>
                  <Route path="/dashboard" exact name="Dashboard" component={DashboardPage}/>
                  <Route path="/datamanagement" exact name="DataManagement" component={DataManagementPage}/>
                  <Route path="/datamanagement/csvexport" exact name="CsvDataExport" component={CsvDataExportPage}/>
                  <Route path="/datamanagement/backup" exact name="BackupDataExport" component={BackupDataExportPage}/>
                  <Route path="/datamanagement/backupimport" exact name="BackupDataImport" component={BackupDataImportPage}/>
                  <Route path="/datamanagement/csvimport" exact name="CsvDataImport" component={CsvDataImportPage}/>
                  <Route path="/datamanagement/:id" name="RecordDetails" component={OldClientRecordEditPage}/>
                  <Route path="/datacleansing" exact name="DataCleansing" component={DataCleansingPage}/>
                  <Route path="/map" exact name="Map" component={MapPage}/>
                  <Route path="/saiku" exact name="Saiku" component={SaikuPage}/>
                  <Route path="/surveydesigner" exact name="Survey Designer" component={SurveyDesignerPage}/>
                  <Route path="/surveydesigner/surveys" exact name="List of Surveys" component={SurveysListPage}/>
                  <Route path="/users" exact name="Users" component={UsersPage}/>
                  <Route path="/usergroups" exact name="User Groups" component={UserGroupsPage}/>
                  <Route path="/usergroups/:id" name="User Group" component={UserGroupDetailsPage}/>
                </Switch>
              </div>
            </main>
            <Aside />
          </div>
          <Footer />
          <CurrentJobMonitorModal />
        </div>
    );
  }
}

export default App;
