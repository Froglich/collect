package org.openforis.collect.persistence.xml;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.SAXParser;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.persistence.xml.DataHandler.NodeErrorItem;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author G. Miceli
 * @author S. Ricci
 *
 */
public class DataUnmarshaller {

	private DataHandler handler;
	
	private final Log log = LogFactory.getLog(getClass());

	public DataUnmarshaller(DataHandler handler) {
		this.handler = handler;
	}
	
	private ParseRecordResult parse(InputSource source) {
		ParseRecordResult result = new ParseRecordResult();
		SAXParser p = new SAXParser();
		p.setContentHandler(handler);
		try {
			p.parse(source);
			List<NodeErrorItem> failures = handler.getFailures();
			if ( failures.isEmpty() ) {
				CollectRecord record = handler.getRecord();
				result.setRecord(record);
				List<NodeErrorItem> warns = handler.getWarnings();
				if (warns.size() > 0) {
					result.setMessage("Processed with errors: " + warns.toString());
					result.setWarnings(warns);
				}
				result.setSuccess(true);
			} else {
				result.setFailures(failures);
			}
		} catch (SAXException e) {
			result.setMessage("Unable to process: " + e.getMessage());
		} catch (IOException e) {
			result.setMessage("Unable to process: " + e.getMessage());
		}
		return result;
	}
	
	public ParseRecordResult parse(String filename) throws DataUnmarshallerException {
		FileReader reader = null;
		try {
			reader = new FileReader(filename);
			ParseRecordResult result = parse(reader); 
			reader.close();
			return result;
		} catch (IOException e) {
			throw new DataUnmarshallerException(e);
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					log.warn("Failed to close Reader: "+e);
				}
			}
		}
	}
	
	public ParseRecordResult parse(Reader reader) {
		InputSource is = new InputSource(reader);
		return parse(is);
	}

	public class ParseRecordResult {
		
		private boolean success;
		private String message;
		private List<NodeErrorItem> warnings;
		private List<NodeErrorItem> failures;
		private CollectRecord record;

		public ParseRecordResult() {
		}
		
		public ParseRecordResult(CollectRecord record) {
			this();
			this.record = record;
		}

		public boolean hasWarnings() {
			return warnings != null && warnings.size() > 0;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public CollectRecord getRecord() {
			return record;
		}

		public void setRecord(CollectRecord record) {
			this.record = record;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public List<NodeErrorItem> getWarnings() {
			return warnings;
		}

		public void setWarnings(List<NodeErrorItem> warnings) {
			this.warnings = warnings;
		}

		public List<NodeErrorItem> getFailures() {
			return failures;
		}

		public void setFailures(List<NodeErrorItem> failures) {
			this.failures = failures;
		}

	}
	
}
