package moviescraper.doctord;

import java.util.ArrayList;
import java.util.List;

import moviescraper.doctord.controller.amalgamation.Amalgamation;
import moviescraper.doctord.controller.amalgamation.AmalgamationDefinition;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.view.GUIMain;
import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws Exception {

		long freeMem = Runtime.getRuntime().freeMemory();
		long heapSize = Runtime.getRuntime().maxMemory();
		String jvmSpecVersion = System.getProperty("java.specification.version");
		String jvmSpecVendor = System.getProperty("java.specification.vendor");
		String jvmVendor = System.getProperty("java.vendor");
		String jvmVersion = System.getProperty("java.runtime.version");
		String jvmName = System.getProperty("java.runtime.name");

		System.out.println(jvmName + " " + jvmVersion + "(" + jvmVendor + ") -- " + jvmSpecVersion + "(" + jvmSpecVendor + ")");
		System.out.println("Heap: " + FileUtils.byteCountToDisplaySize(heapSize));
		System.out.println("Free mem: " + FileUtils.byteCountToDisplaySize(freeMem));

		List<Amalgamation> amalgamations = new ArrayList<>();
		AmalgamationDefinition def1 = new AmalgamationDefinition("JAV - Movies", Movie.class, "Japan");
		def1.addScraperToField("actors", "ActionJavParsingProfile");
		def1.addScraperToField("actors", "JavBusParsingProfile");
		def1.addScraperToField("actors", "JavLibraryParsingProfile");
		amalgamations.add(new Amalgamation(def1));
		AmalgamationDefinition def2 = new AmalgamationDefinition("JAV - Webclip", Movie.class, "Japan");
		def2.addScraperToField("actors", "ActionJavParsingProfile");
		def2.addScraperToField("actors", "JavBusParsingProfile");
		amalgamations.add(new Amalgamation(def2));
		AmalgamationDefinition def3 = new AmalgamationDefinition("IDOL - Webclip", Movie.class, "Japan");
		def3.addScraperToField("actors", "ActionJavParsingProfile");
		def3.addScraperToField("actors", "JavBusParsingProfile");
		amalgamations.add(new Amalgamation(def3));
		Amalgamation.save(amalgamations, "test.json");
		//List<Amalgamation> a = Amalgamation.load("/tmp/test.json");
		//for(Amalgamation ab: a) {
		//	System.out.println(""+ab);
		//}
		//AmalgamationGroup.save(a, "/tmp/test2.json");

		if (args == null || args.length == 0) {
			//Start the GUI version of the program
			MainGUI.run(args);
		} else {
			MainCLI.run(args);
		}
	}
}
