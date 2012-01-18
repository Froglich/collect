/**
 * This class is generated by jOOQ
 */
package org.openforis.collect.persistence.jooq;

/**
 * This class is generated by jOOQ.
 *
 * Convenience access to all sequences in collect
 */
@javax.annotation.Generated(value    = {"http://www.jooq.org", "2.0.1"},
                            comments = "This class is generated by jOOQ")
public final class Sequences {

	/**
	 * The sequence collect.collect.data_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> DATA_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("data_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.record_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> RECORD_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("record_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.schema_definition_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> SCHEMA_DEFINITION_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("schema_definition_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.survey_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> SURVEY_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("survey_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.taxon_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> TAXON_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("taxon_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.taxon_name_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> TAXON_NAME_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("taxon_name_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.taxonomy_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> TAXONOMY_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("taxonomy_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.user_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> USER_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("user_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * The sequence collect.collect.user_role_id_seq
	 */
	public static final org.jooq.Sequence<java.lang.Long> USER_ROLE_ID_SEQ = new org.jooq.impl.SequenceImpl<java.lang.Long>("user_role_id_seq", org.openforis.collect.persistence.jooq.Collect.COLLECT, org.jooq.impl.SQLDataType.BIGINT);

	/**
	 * No instances
	 */
	private Sequences() {}
}
