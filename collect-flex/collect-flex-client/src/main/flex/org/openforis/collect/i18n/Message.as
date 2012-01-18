package org.openforis.collect.i18n {
	import mx.resources.ResourceManager;

	/**
	 * @author Mino Togna
	 * */
	public class Message {
		
		public function Message() {
		}
		
		public static function get(resource:String, parameters:Array=null, bundle:String="messages"):String {
			return ResourceManager.getInstance().getString(bundle, resource,parameters);
		} 
	}
}