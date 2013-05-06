/**
 * 
 */
package org.openforis.collect.relational.model;

import java.sql.Types;

import org.openforis.idm.metamodel.CodeListItem;

/**
 * @author S. Ricci
 *
 */
public class CodeColumn extends AbstractColumn<CodeListItem> {

	private static final int MAX_LENGTH = 255;

	CodeColumn(String name) {
		super(name, Types.VARCHAR, "varchar", MAX_LENGTH, false);
	}

	@Override
	public Object extractValue(CodeListItem item) {
		return item.getCode();
	}

}
