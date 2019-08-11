/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviescraper.doctord.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ScrapeData {
	
	public List<Field> getScrapeFields() {
		return ScrapeData.getClassScrapeFields(this.getClass());
	}

	public List<String> getScrapeFieldNames() {
		return ScrapeData.getClassScrapeFieldNames(this.getClass());
	}

	public static List<Field> getClassScrapeFields(Class<? extends ScrapeData> klass) {
		List<Field> availableFields = new ArrayList<>();

		for(Field field: klass.getDeclaredFields()) {
			if(field.getAnnotation(ScrapeField.class) != null) {
				availableFields.add(field);
			}
		}
		
		return availableFields;
	}
	
	public static List<String> getClassScrapeFieldNames(Class<? extends ScrapeData> klass) {
		List<String> availableFieldNames = new ArrayList<>();

		for(Field field: ScrapeData.getClassScrapeFields(klass)) {
			if(field.getAnnotation(ScrapeField.class) != null) {
				availableFieldNames.add(field.getName());
			}
		}
		
		return availableFieldNames;
	}
}
