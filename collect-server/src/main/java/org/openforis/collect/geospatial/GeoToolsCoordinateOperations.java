/**
 * 
 */
package org.openforis.collect.geospatial;


import static org.geotools.referencing.CRS.parseWKT;
import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.AbstractSingleCRS;
import org.openforis.idm.geospatial.CoordinateOperations;
import org.openforis.idm.metamodel.SpatialReferenceSystem;
import org.openforis.idm.model.Coordinate;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * @author M. Togna
 * 
 */
public class GeoToolsCoordinateOperations implements CoordinateOperations {

	private static final String WGS84_ID = "EPSG:4326";

	private static final Log LOG = LogFactory.getLog(GeoToolsCoordinateOperations.class);

	// private static CoordinateOperationFactory CO_FACTORY;
	// private static Map<String, CoordinateReferenceSystem> SYSTEMS;
	private static Map<String, MathTransform> TO_WGS84_TRANSFORMS;
	private static GeodeticCalculator CALCULATOR;

	static {
		init();
	}

	/**
	 * Returns the orthodromic distance between two points
	 * 
	 * @param startingPosition
	 * @param destinationPosition
	 * @return
	 * @throws TransformException
	 */
	public synchronized double orthodromicDistance(Position startingPosition, Position destinationPosition) {
		try {
			CALCULATOR.setStartingPosition(startingPosition);
			CALCULATOR.setDestinationPosition(destinationPosition);
			double result = CALCULATOR.getOrthodromicDistance();
			return result;
		} catch (TransformException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized double orthodromicDistance(double startX, double startY, String startSRSId, double destX, double destY, String destSRSId) {
		Position startingPosition = toWgs84(startX, startY, startSRSId);
		Position destinationPosition = toWgs84(destX, destY, destSRSId);
		return orthodromicDistance(startingPosition, destinationPosition);
	}

	@Override
	public synchronized double orthodromicDistance(Coordinate startingCoordinate, Coordinate destinationCoordinate) {
		double startX = startingCoordinate.getX();
		double startY = startingCoordinate.getY();
		String startSRSId = startingCoordinate.getSrsId();

		double destX = destinationCoordinate.getX();
		double destY = destinationCoordinate.getY();
		String destSRSId = destinationCoordinate.getSrsId();

		return orthodromicDistance(startX, startY, startSRSId, destX, destY, destSRSId);
	}

	@Override
	public void parseSRS(List<SpatialReferenceSystem> list) {
		for (SpatialReferenceSystem srs : list) {
			parseSRS(srs);
		}
	}
	
	@Override
	public SpatialReferenceSystem fetchSRS(String code) {
		return fetchSRS(code, new HashSet<String>(Arrays.asList("en")));
	}

	@Override
	public SpatialReferenceSystem fetchSRS(String code, Set<String> labelLanguages) {
		try {
			CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
			CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
			SpatialReferenceSystem result = new SpatialReferenceSystem();
			result.setId(code);
			result.setWellKnownText(crs.toWKT());
			String label = getLabel(code, crs);
			String scope = crs.getScope().toString();
			for (String lang : labelLanguages) {
				result.setLabel(lang, label);
				result.setDescription(lang, scope);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching SRS with code: " + code, e);
		}
	}

	private String getLabel(String code, CoordinateReferenceSystem crs) {
		String label;
		if ( crs instanceof AbstractSingleCRS ) {
			Datum datum = ((AbstractSingleCRS) crs).getDatum();
			String datumName = datum.getName().toString();
			label = datumName;
		} else {
			List<String> aliasNames = new ArrayList<String>();
			for (GenericName genericName : crs.getAlias()) {
				aliasNames.add(genericName.toString());
			}
			label = StringUtils.join(aliasNames, " / ");
		}
		if ( StringUtils.isBlank(label) ) {
			label = code;
		}
		return label;
	}
	
	@Override
	public Set<String> getAvailableSRSs() {
		Set<String> result = new HashSet<String>();
		String authorityCode = "EPSG";
		String codePrefix = authorityCode + ":";
		Set<String> supportedCodes = CRS.getSupportedCodes(authorityCode);
		for (String code : supportedCodes) {
			if ( code.startsWith(codePrefix) ) {
				result.add(code);
			} else {
				result.add(codePrefix + code);
			}
		}
		return result;
	}
	
	@Override
	public void parseSRS(SpatialReferenceSystem srs) {
		String srsId = srs.getId();
		MathTransform transform = TO_WGS84_TRANSFORMS.get(srsId);
		if (transform == null) {
			String wkt = srs.getWellKnownText();
			transform = findMathTransform(srsId, wkt);
			TO_WGS84_TRANSFORMS.put(srsId, transform);
		}
	}

	private static Position toWgs84(double x, double y, String srsId) {
		try {
			DirectPosition src = new DirectPosition2D(x, y);
			MathTransform transform = TO_WGS84_TRANSFORMS.get(srsId);
			if (transform == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unknown CRS: " + srsId);
				}
				return new DirectPosition2D(0, 0);
			}

			DirectPosition directPosition = transform.transform(src, null);
			return directPosition;
		} catch (Throwable t) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error converting: x=" + x + " y=" + y + " srs=" + srsId, t);
				throw new RuntimeException(t);
			}
			return new DirectPosition2D(0, 0);
		}
	}

	private static MathTransform findMathTransform(String srsId, String wkt) {
		try {
			CoordinateReferenceSystem crs = parseWKT(wkt);
			// SYSTEMS.put(srsId, crs);
			MathTransform mathTransform = CRS.findMathTransform(crs, WGS84);
			// TO_WGS84_TRANSFORMS.put(srsId, mathTransform);
			return mathTransform;
		} catch (Throwable t) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error while parsing srsid " + srsId, t);
			}
			throw new RuntimeException(t);
		}
	}

	private static void init() {
		try {
			// SYSTEMS = new HashMap<String, CoordinateReferenceSystem>();
			TO_WGS84_TRANSFORMS = new HashMap<String, MathTransform>();
			CALCULATOR = new GeodeticCalculator();

			MathTransform wgs84Transform = CRS.findMathTransform(WGS84, WGS84);
			TO_WGS84_TRANSFORMS.put(WGS84_ID, wgs84Transform);
		} catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error while initializing CoordinateOperations", e);
			}
			throw new RuntimeException(e);
		}
	}

}