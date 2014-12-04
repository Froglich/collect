/**
 * 
 */
package org.openforis.idm.metamodel;

import org.openforis.idm.geospatial.CoordinateOperations;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.expression.ExpressionEvaluator;
import org.openforis.idm.model.expression.ExpressionFactory;

/**
 * @author M. Togna
 * @author G. Miceli
 * @author S. Ricci
 */
public interface SurveyContext {

	ExpressionFactory getExpressionFactory();
	
	ExpressionEvaluator getExpressionEvaluator();

	Validator getValidator();
	
	CodeListService getCodeListService();
	
	ExternalCodeListProvider getExternalCodeListProvider();
	
	CoordinateOperations getCoordinateOperations();
	
	Survey createSurvey();
}
