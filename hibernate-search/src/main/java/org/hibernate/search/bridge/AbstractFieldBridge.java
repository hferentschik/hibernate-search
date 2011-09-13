package org.hibernate.search.bridge;

/**
 * @author Hardy Ferentschik
 */
public abstract class AbstractFieldBridge implements FieldBridge {
	private String name;
	private LuceneOptions luceneOptions;

	@Override
	public void initialize(String name, LuceneOptions luceneOptions) {
		this.name = name;
		this.luceneOptions = luceneOptions;
	}

	public String getFieldName() {
		return name;
	}

	public LuceneOptions getLuceneOptions() {
		return luceneOptions;
	}
}


