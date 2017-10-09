package org.openforis.collect.metamodel.view;

import java.util.List;

import org.openforis.collect.designer.metamodel.AttributeType;
import org.openforis.collect.designer.metamodel.NodeType;

public class AttributeDefView extends NodeDefView {

	private AttributeType attributeType;
	private List<String> fieldNames;
	private boolean showInRecordSummaryList;
	
	public AttributeDefView(int id, String name, String label, AttributeType type, 
			List<String> fieldNames, boolean key, boolean multiple, boolean showInRecordSummaryList) {
		super(id, name, label, NodeType.ATTRIBUTE, key, multiple);
		this.attributeType = type;
		this.fieldNames = fieldNames;
		this.showInRecordSummaryList = showInRecordSummaryList;
	}
	
	public AttributeType getAttributeType() {
		return attributeType;
	}
	
	public List<String> getFieldNames() {
		return this.fieldNames;
	}
	
	public boolean isShowInRecordSummaryList() {
		return showInRecordSummaryList;
	}
}