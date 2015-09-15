/**
 * 
 */
package org.openforis.collect.datacleansing.form.validation;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openforis.collect.manager.SessionManager;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.expression.ExpressionValidator;
import org.openforis.idm.metamodel.expression.ExpressionValidator.ExpressionType;
import org.openforis.idm.metamodel.expression.ExpressionValidator.ExpressionValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 
 * @author S. Ricci
 *
 */
public abstract class SimpleValidator<F> implements Validator {

	final Class<F> genericType;

	@Autowired
	protected SessionManager sessionManager;
	@Autowired
	protected ExpressionValidator expressionValidator;
	@Autowired
	protected MessageSource messageSource;

	@SuppressWarnings("unchecked")
	public SimpleValidator() {
		 this.genericType = (Class<F>) GenericTypeResolver.resolveTypeArgument(getClass(), SimpleValidator.class);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return genericType.isAssignableFrom(clazz);
	}
	
	@Override
	public final void validate(Object target, Errors errors) {
		@SuppressWarnings("unchecked")
		F form = (F) target;
		validateForm(form, errors);
	}
	
	public abstract void validateForm(F target, Errors errors);

	protected CollectSurvey getActiveSurvey() {
		return sessionManager.getActiveSurvey();
	}
	
	protected boolean validateRequiredFields(Errors errors, String... fields) {
		boolean result = true;
		for (String field : fields) {
			result = result & validateRequiredField(errors, field);
		}
		return result;
	}
	
	protected boolean validateRequiredField(Errors errors, String field) {
		Assert.notNull(errors, "Errors object must not be null");
		
		Object value = errors.getFieldValue(field);
		if (value == null || StringUtils.isBlank(value.toString())) {
			String errorCode = "validation.required_field";
			String[] messageArgs = new String[0];
			String defaultMessage = messageSource.getMessage(errorCode, messageArgs, Locale.ENGLISH);
			errors.rejectValue(field, errorCode, messageArgs, defaultMessage);
			return false;
		} else {
			return true;
		}
	}

	protected boolean validateBooleanExpression(Errors errors,
			NodeDefinition contextNodeDef, NodeDefinition thisNodeDef,
			String field, String expression) {
		return validateExpression(errors, contextNodeDef, thisNodeDef, field, expression, ExpressionType.BOOLEAN);
	}

	protected boolean validateValueExpression(Errors errors,
			NodeDefinition contextNodeDef, NodeDefinition thisNodeDef,
			String field, String expression) {
		return validateExpression(errors, contextNodeDef, thisNodeDef, field, expression, ExpressionType.VALUE);
	}
	
	protected boolean validateExpression(Errors errors,
			NodeDefinition contextNodeDef, NodeDefinition thisNodeDef,
			String field, String expression, ExpressionType type) {
		ExpressionValidationResult result = expressionValidator.validateExpression(type, contextNodeDef, thisNodeDef, expression);
		if (result.isError()) {
			String errorCode = "validation.invalid_expression";
			String validationMessage = StringUtils.defaultString(result.getDetailedMessage(), result.getMessage());
			String[] errorMessageArgs = new String[] {validationMessage};
			String defaultMessage = messageSource.getMessage(errorCode, errorMessageArgs, Locale.ENGLISH);
			errors.rejectValue(field, errorCode, new String[0], defaultMessage);
		}
		return result.isOk();
	}
	protected void rejectDuplicateValue(Errors errors, String field, Object... args) {
		String errorCode = "validation.duplicate_value";
		errors.rejectValue(field, errorCode, args, messageSource.getMessage(errorCode, args, Locale.ENGLISH));
	}
	
}
