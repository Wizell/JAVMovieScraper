package moviescraper.doctord.controller.amalgamation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moviescraper.doctord.model.ScrapeField;


public class AmalgamationDefinition {

	private final String name;
	private final String type;
	private final String icon;
	private final List<String> availableFields;
	private final Map<String, List<String>> fields;


	public AmalgamationDefinition(String name, Class type, String icon){
		this.name = name;
		this.type = type.getSimpleName();
		this.icon = icon;
		this.fields = new HashMap<>();

		availableFields = new ArrayList<>();
		for(Field field: type.getDeclaredFields()) {
			if(field.getAnnotation(ScrapeField.class) != null) {
				availableFields.add(field.getName());
			}
		}
	}
	
	public static List<Field> getClassFields(Class type) {
		List<Field> possibleFields = new ArrayList<>();
		
		for(Field field: type.getDeclaredFields()) {
			if(field.getAnnotation(ScrapeField.class) != null) {
				possibleFields.add(field);
			}
		}
		
		return possibleFields;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getIcon() {
		return this.icon;
	}

	public void addScraperToField(String fieldName, String scraper) throws IllegalArgumentException {
		List<String> scrapers = this.fields.get(fieldName);
		if(scrapers == null) {
			if(availableFields.contains(fieldName)) {
				this.fields.put(fieldName, new ArrayList<>());
				scrapers = this.fields.get(fieldName);
			}
		}

		if(scrapers == null) {
			throw new IllegalArgumentException("Unknown parameter " + fieldName + " in type "+type+". Available fields: "+this.fields.toString());
		}
		scrapers.add(scraper);
		this.fields.put(fieldName, scrapers);
	}
	
	public Map<String, List<String>> getFields() {
		return this.fields;
	}
}
