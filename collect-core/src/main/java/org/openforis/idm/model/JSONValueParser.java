package org.openforis.idm.model;

import static org.openforis.idm.metamodel.CoordinateAttributeDefinition.SRS_FIELD_NAME;
import static org.openforis.idm.metamodel.CoordinateAttributeDefinition.X_FIELD_NAME;
import static org.openforis.idm.metamodel.CoordinateAttributeDefinition.Y_FIELD_NAME;
import static org.openforis.idm.metamodel.TaxonAttributeDefinition.CODE_FIELD_NAME;
import static org.openforis.idm.metamodel.TaxonAttributeDefinition.LANGUAGE_CODE_FIELD_NAME;
import static org.openforis.idm.metamodel.TaxonAttributeDefinition.LANGUAGE_VARIETY_FIELD_NAME;
import static org.openforis.idm.metamodel.TaxonAttributeDefinition.SCIENTIFIC_NAME_FIELD_NAME;
import static org.openforis.idm.metamodel.TaxonAttributeDefinition.VERNACULAR_NAME_FIELD_NAME;
import static org.openforis.idm.metamodel.TimeAttributeDefinition.HOUR_FIELD;
import static org.openforis.idm.metamodel.TimeAttributeDefinition.MINUTE_FIELD;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.Unit;

/**
 * 
 * @author S. Ricci
 *
 */
public class JSONValueParser {

	public BooleanValue parseBoolean(String value) {
		Map<String, Object> map = parseJSONToMap(value, CodeAttributeDefinition.CODE_FIELD);
		return map == null ? null: new BooleanValue((String) map.get(BooleanValue.VALUE_FIELD));
	}
	
	public Code parseCode(String value) {
		Map<String, Object> map = parseJSONToMap(value, CodeAttributeDefinition.CODE_FIELD);
		return map == null ? null: new Code(
				(String) map.get(CodeAttributeDefinition.CODE_FIELD), 
				(String) map.get(CodeAttributeDefinition.QUALIFIER_FIELD)
		);
	}
	
	public Coordinate parseCoordinate(String value) {
		Map<String, Object> map = parseJSONToMap(value);
		if (map == null) {
			return null;
		}
		return new Coordinate(
				getDouble(map, X_FIELD_NAME), 
				getDouble(map, Y_FIELD_NAME), 
				(String) map.get(SRS_FIELD_NAME)
			);
	}
	
	public Date parseDate(String value) {
		Map<String, Object> map = parseJSONToMap(value, null);
		if (map == null) {
			return null;
		}
		return new Date(
				getInteger(map, Date.YEAR_FIELD), 
				getInteger(map, Date.MONTH_FIELD), 
				getInteger(map, Date.DAY_FIELD)
		);
	}
	
	public File parseFile(String value) {
		Map<String, Object> map = parseJSONToMap(value, File.FILENAME_FIELD);
		if (map == null) {
			return null;
		}
		return new File(
				(String) map.get(File.FILENAME_FIELD), 
				getLong(map, File.SIZE_FIELD)
		);
	}
	
	public IntegerValue parseInteger(NumberAttributeDefinition attrDef, String value) {
		Map<String, Object> map = parseJSONToMap(value, NumberValue.VALUE_FIELD);
		if (map == null) {
			return null;
		}
		Integer unitId = getInteger(map, NumberValue.UNIT_ID_FIELD);
		Unit unit = attrDef.getActualUnit(unitId);
		return new IntegerValue(getInteger(map, NumberValue.VALUE_FIELD), unit);
	}
	
	public IntegerRange parseIntegerRange(RangeAttributeDefinition attrDef, String value) {
		Map<String, Object> map = parseJSONToMap(value);
		if (map == null) {
			return null;
		}
		Integer from = getInteger(map, IntegerRange.FROM_FIELD);
		Integer to = getInteger(map, IntegerRange.TO_FIELD);
		if (to == null) {
			to = from;
		}
		Integer unitId = getInteger(map, NumberValue.UNIT_ID_FIELD);
		Unit unit = attrDef.getActualUnit(unitId);
		return new IntegerRange(from, to, unit);
	}
	
	public RealValue parseReal(NumberAttributeDefinition attrDef, String value) {
		Map<String, Object> map = parseJSONToMap(value, RealValue.VALUE_FIELD);
		if (map == null) {
			return null;
		}
		Integer unitId = getInteger(map, RealValue.UNIT_ID_FIELD);
		Unit unit = attrDef.getActualUnit(unitId);
		return new RealValue(getDouble(map, RealValue.VALUE_FIELD), unit);
	}
	
	public RealRange parseRealRange(RangeAttributeDefinition attrDef, String value) {
		Map<String, Object> map = parseJSONToMap(value);
		if (map == null) {
			return null;
		}
		Double from = getDouble(map, RealRange.FROM_FIELD);
		Double to = getDouble(map, RealRange.TO_FIELD);
		if (to == null) {
			to = from;
		}
		Integer unitId = getInteger(map, RealRange.UNIT_ID_FIELD);
		Unit unit = attrDef.getActualUnit(unitId);
		return new RealRange(from, to, unit);
	}
	
	public TaxonOccurrence parseTaxonOccurrence(String value) {
		Map<String, Object> map = parseJSONToMap(value, TaxonAttributeDefinition.CODE_FIELD_NAME);
		if (map == null) {
			return null;
		}
		return new TaxonOccurrence(
				(String) map.get(CODE_FIELD_NAME), 
				(String) map.get(SCIENTIFIC_NAME_FIELD_NAME), 
				(String) map.get(VERNACULAR_NAME_FIELD_NAME), 
				(String) map.get(LANGUAGE_CODE_FIELD_NAME), 
				(String) map.get(LANGUAGE_VARIETY_FIELD_NAME)
		);
	}

	public Time parseTime(String value) {
		Map<String, Object> map = parseJSONToMap(value);
		if (map == null) {
			return null;
		}
		return new Time(getInteger(map, HOUR_FIELD), getInteger(map, MINUTE_FIELD));
	}
	
	private Map<String, Object> parseJSONToMap(final String value) {
		return parseJSONToMap(value, null);
	}
	
	@SuppressWarnings("serial")
	private Map<String, Object> parseJSONToMap(final String value, final String singleValueField) {
		if ( StringUtils.isBlank(value) ) {
			return null;
		}
		if (value.startsWith("{")) { //is JSON
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) new JSONParser().parse(value);
				return map;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return new HashMap<String, Object>() {{
				put(StringUtils.trimToEmpty(singleValueField), value);
			}};
		}
	}
	
	protected static Integer getInteger(Map<String, Object> map, String field) {
		Object val = map.get(field);
		if (val == null) {
			return null;
		} else if (val instanceof Number) {
			return Integer.valueOf(((Number) val).intValue());
		} else {
			return Integer.parseInt(val.toString());
		}
	}

	protected static Double getDouble(Map<String, Object> map, String field) {
		Object val = map.get(field);
		if (val == null) {
			return null;
		} else if (val instanceof Number) {
			return Double.valueOf(((Number) val).doubleValue());
		} else {
			return Double.parseDouble(val.toString());
		}
	}
	
	protected static Long getLong(Map<String, Object> map, String field) {
		Object val = map.get(field);
		if (val == null) {
			return null;
		} else if (val instanceof Number) {
			return Long.valueOf(((Number) val).longValue());
		} else {
			return Long.parseLong(val.toString());
		}
	}
	
}
