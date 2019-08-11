/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviescraper.doctord.controller.amalgamation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.ScrapeData;

/**
 * An amalgamation is the association of a name and a type to a sorted list of scraper for each field of this type
 * e.g. An amalgamation named "asian movies" for type "Movie" will hold a list of scrapers name for plot, one for year and so on
 * 
 * @param <T> Type of content to amalgamate
 */
public class Amalgamation<T extends ScrapeData> {

	private final String name;
	private final Class<T> dataType;
	private final String icon;
	private final Map<String, List<SiteParsingProfile>> fields;
	private final static String MODEL_PACKAGE_NAME = moviescraper.doctord.model.ScrapeField.class.getPackage().getName();
	private final static String PARSER_PACKAGE_NAME = moviescraper.doctord.controller.siteparsingprofile.specific.MovieScraper.class.getPackage().getName();

	public Amalgamation(String name, Class<T> dataType, String iconName) {
		this.name = name;
		this.dataType = dataType;
		this.icon = iconName;
		fields = new HashMap<>();
		for(String fieldName: ScrapeData.getClassScrapeFieldNames(dataType)) {
			fields.put(fieldName, new ArrayList<>());
		}
	}
	
	public Amalgamation(AmalgamationDefinition definition) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.name = definition.getName();
		this.dataType = (Class<T>) Class.forName(String.join(".", MODEL_PACKAGE_NAME, definition.getType()));
		this.icon = definition.getIcon();
		fields = new HashMap<>();
		Map<String, List<String>> fieldMap = definition.getFields();

		for(Map.Entry<String, List<String>> field: fieldMap.entrySet()) {
			// Load scraper
			List<SiteParsingProfile> scrapers = new ArrayList<>();

			for(String scraperName: field.getValue()) {
				SiteParsingProfile scraper = (SiteParsingProfile) Class.forName(String.join(".", PARSER_PACKAGE_NAME, scraperName)).newInstance();
				scrapers.add(scraper);
			}

			fields.put(field.getKey(), scrapers);
		}
	}
	
	public void scraperAdd(String fieldName, SiteParsingProfile a) {
		if(this.fields.containsKey(fieldName)) {
			fields.get(fieldName).add(a);
		}
	}

	public void scraperAddAll(SiteParsingProfile scraper) {
		for(List<SiteParsingProfile> field: this.fields.values()) {
			field.add(scraper);
		}
	}
	
	public AmalgamationDefinition getDefinition() {
			AmalgamationDefinition definition = new AmalgamationDefinition(this.name, this.dataType, this.icon);
			
			for(Map.Entry<String,List<SiteParsingProfile>> field: fields.entrySet()) {
				for(SiteParsingProfile parser: field.getValue())
				definition.addScraperToField(field.getKey(), parser.getClass().getSimpleName());
			}
			
			return definition;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}
	
	public List<SiteParsingProfile> getScrapers() {
		List<SiteParsingProfile> scrapers = new ArrayList<>();
		for(List<SiteParsingProfile> fieldScrapers: fields.values())
		{
			for(SiteParsingProfile scraper: fieldScrapers) {
				if(!scrapers.contains(scraper)) {
					scrapers.add(scraper);
				}
			}
		}
		
		return scrapers;
	}

	@Override
	public String toString() {
		StringBuilder outputString = new StringBuilder();
		outputString.append(this.getName());
		outputString.append("(");
		outputString.append(this.dataType.getSimpleName());
		outputString.append(")");
		outputString.append(" --> ");
		for(Map.Entry<String,List<SiteParsingProfile>> field: fields.entrySet()) {
			outputString.append(field.getKey());
			outputString.append(field.getValue());
			outputString.append("|");
		}

		return outputString.toString();
	}

	public static List<Amalgamation> load(String fileName) {
		List<Amalgamation> groups = new ArrayList<>();

		// Read objects
		File jsonFile = new File(fileName);

		try(FileReader reader = new FileReader(jsonFile)) {
			try(com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(reader)) {
				jsonReader.setLenient(true);
				Gson gson = new Gson();
				while (jsonReader.peek() != JsonToken.END_DOCUMENT) {
					AmalgamationDefinition definition = gson.fromJson(jsonReader, AmalgamationDefinition.class);
					groups.add(new Amalgamation(definition));
				}
				System.out.println("--");
				for(Amalgamation ama: groups) {
					System.out.println(" >" + ama.toString());
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " does not exists");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return groups;
	}

	public static void save(List<Amalgamation> groups, String fileName) {
		// Read objects
		File outputFile = new File(fileName);
		try(FileWriter output = new FileWriter(outputFile)) {
			Gson gson = new Gson();
			for(Amalgamation amalgamation: groups) {
				String json = gson.toJson(amalgamation.getDefinition());
				System.out.println(json);
				output.write(json);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public T mix(List<T> dataItems) {		
		return dataItems.get(0);
	}
}
