package org.openforis.collect.io.data.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.model.Attribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Field;
import org.openforis.idm.model.Node;

/**
 * @author G. Miceli
 * @author M. Togna
 * @author S. Ricci
 */
public class SingleFieldAttributeColumnProvider extends BasicAttributeColumnProvider<AttributeDefinition> {
	
	public SingleFieldAttributeColumnProvider(CSVExportConfiguration config, AttributeDefinition defn) {
		super(config, defn);
	}
	
	@Override
	protected int getNumberOfColumnsPerAttribute() {
		return 1;
	}
	
	@Override
	protected List<String> generateSingleAttributeColumnHeadings() {
		return generateAttributeColumnHeadings(0);
	}
	
	@Override
	protected List<String> generateAttributeColumnHeadings(int i) {
		return Arrays.asList(generateHeadingPrefix() + generateAttributePositionSuffix(i));
	}
	
	public List<String> extractValues(Node<?> axis) {
		if ( axis == null ) {
			throw new NullPointerException("Axis must be non-null");
		} else if ( axis instanceof Entity ) {
			Entity entity = (Entity) axis;
			int maxAttrValues = getMaxAttributeValues();
			List<String> values = new ArrayList<String>(maxAttrValues);
			Entity nearestParentEntity = getParentEntity(entity);
			int attrCount = nearestParentEntity.getCount(attributeDefinition);
			for (int attrIdx = 0; attrIdx < maxAttrValues; attrIdx++) {
				String val;
				if (attrIdx < attrCount) {
					Attribute<?, ?> attr = (Attribute<?, ?>) nearestParentEntity.getChild(attributeDefinition, attrIdx);
					val = extractValue(attr);
				} else {
					val = "";
				}
				values.add(val);
			}
			return values;
		} else {
			throw new UnsupportedOperationException("Axis must be an Entity");
		}
	}

	/**
	 * Attribute definition can be inside nested single entities inside the axis.
	 * This method will look for the nearest parent entity for attributes to extract value for.
	 */
	private Entity getParentEntity(Entity axis) {
		EntityDefinition entityDef = axis.getDefinition();
		List<EntityDefinition> ancestorEntityDefinitions = attributeDefinition.getAncestorEntityDefinitionsInReverseOrder();
		int indexOfAxis = ancestorEntityDefinitions.indexOf(entityDef);
		Entity nearestParentEntity = axis;
		if (indexOfAxis + 1 < ancestorEntityDefinitions.size()) {
			List<EntityDefinition> relativeSingleEntityDefs = ancestorEntityDefinitions.subList(indexOfAxis + 1, ancestorEntityDefinitions.size());
			for (EntityDefinition parentSingleEntityDef : relativeSingleEntityDefs) {
				nearestParentEntity = (Entity) nearestParentEntity.getChild(parentSingleEntityDef);
			}
		}
		return nearestParentEntity;
	}

	private String extractValue(Attribute<?, ?> attr) {
		String mainFieldName = attr.getDefinition().getMainFieldName();
		Field<?> fld = attr.getField(mainFieldName);
		Object v = fld.getValue();
		String stringVal = v == null ? "" : v.toString();
		return stringVal;
	}

}
