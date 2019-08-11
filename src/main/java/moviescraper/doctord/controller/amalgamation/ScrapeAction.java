package moviescraper.doctord.controller.amalgamation;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.util.logging.Logger;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.view.GUIMain;
import moviescraper.doctord.view.ScrapeAmalgamatedProgressDialog;

public class ScrapeAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ScrapeAction.class.getName());

	//a reference to the gui if we need to update the view in any workers. Can be null if we have no gui to update.
	private GUIMain guiMain;

	public static final String SCRAPE_KEY = "SCRAPE_KEY";

	Amalgamation definition;

	public ScrapeAction(GUIMain guiMain, String name, Icon icon, Amalgamation definition) {
		super(name, icon);
		this.guiMain = guiMain;

		this.definition = definition;
	}

	//Used for just scraping from one specific site. Allows us to reuse code, even though we are just amalgamating from one movie source
	public ScrapeAction(GUIMain guiMain, SiteParsingProfile scraper) {
		this.guiMain = guiMain;
		definition = new Amalgamation(scraper.getParserName(), Movie.class, null);
		definition.scraperAddAll(scraper);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (guiMain.getCurrentlySelectedMovieFileList() == null || guiMain.getCurrentlySelectedMovieFileList().size() == 0) {
			JOptionPane.showMessageDialog(null, "You must select a file before clicking scrape.", "No File Selected", JOptionPane.ERROR_MESSAGE);
			return;

		}

		LOGGER.log(Level.INFO, "Scraping with the following scrapers: {0}", definition.getScrapers());

		if (guiMain != null) {

			guiMain.setMainGUIEnabled(false);
			guiMain.movieToWriteToDiskList.clear();
			guiMain.removeOldScrapedMovieReferences();
		}

		ScrapeAmalgamatedProgressDialog scraperWindow = new ScrapeAmalgamatedProgressDialog(guiMain, definition);
		//scraperWindow.setVisible(true);
		guiMain.setMainGUIEnabled(true);
	}

}
