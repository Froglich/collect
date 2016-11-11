package org.openforis.collect.io.data.csv;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openforis.commons.lang.Strings;

/**
 * 
 * @author S. Ricci
 *
 */
public abstract class BasicColumnProvider implements ColumnProvider {

	private CSVExportConfiguration config;
	private ColumnProviderChain parentProvider;

	public BasicColumnProvider(CSVExportConfiguration config) {
		super();
		this.config = config;
	}
	
	/**
	 * Returns column headings including ancestors heading prefixes
	 */
	public List<String> generateFinalColumnHeadings() {
		List<String> columnHeadings = getColumnHeadings();
		ColumnProviderChain p = parentProvider;
		if (parentProvider != null) {
			StringBuilder ancestorPrefixSB = new StringBuilder();
			while (p != null) {
				if (StringUtils.isNotBlank(p.getHeadingPrefix())) {
					ancestorPrefixSB.insert(0, p.getHeadingPrefix());
				}
				p = p.getParentProvider();
			}
			columnHeadings = Strings.prependToList(columnHeadings, ancestorPrefixSB.toString());
		}
		return columnHeadings;
	}
	
	public CSVExportConfiguration getConfig() {
		return config;
	}
	
	public ColumnProviderChain getParentProvider() {
		return parentProvider;
	}
	
	public void setParentProvider(ColumnProviderChain p) {
		this.parentProvider = p;
	}
	
}
