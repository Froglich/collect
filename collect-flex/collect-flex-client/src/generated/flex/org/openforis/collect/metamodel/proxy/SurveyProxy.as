/**
 * Generated by Gas3 v2.2.0 (Granite Data Services).
 *
 * NOTE: this file is only generated if it does not exist. You may safely put
 * your custom code here.
 */

package org.openforis.collect.metamodel.proxy {
	
	/**
	 * @author S. Ricci
	 */
    [Bindable]
    [RemoteClass(alias="org.openforis.collect.metamodel.proxy.SurveyProxy")]
    public class SurveyProxy extends SurveyProxyBase {
		
		public function init():void {
			schema.survey = this;
			schema.init();
		}
		
		public function getProjectName(lang:String = "en", firstIfNotFound:Boolean = true):String {
			for each (var p:LanguageSpecificTextProxy in projectNames) {
				if(p.language == lang) {
					return p.text;
				}
			}
			if(firstIfNotFound && projectNames.length > 0) {
				var langText:LanguageSpecificTextProxy = LanguageSpecificTextProxy(projectNames.getItemAt(0));
				return langText.text;
			} else {
				return name;
			}
		}
		
		
	}
}