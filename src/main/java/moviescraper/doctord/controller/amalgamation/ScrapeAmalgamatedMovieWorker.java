package moviescraper.doctord.controller.amalgamation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.view.ScrapeAmalgamatedProgressDialog;

public class ScrapeAmalgamatedMovieWorker extends SwingWorker<Void, Map<SiteParsingProfile, Movie>> {

	boolean promptUserForURLWhenScraping = true; //do we stop to ask the user to pick a URL when scraping

	int progress;
	int amountOfProgressPerSubtask;
	protected SwingWorker<Void, String> worker;
	boolean scrapeCanceled;
	private List<Map<SiteParsingProfile, Movie>> currentPublishedMovies;
	int numberOfScrapesToRun = 0;
	int numberOfScrapesFinished = 0;
	private Map<String, SwingWorker<Void, Void>> runningWorkers;
	private File fileToScrape;
	List<SiteParsingProfile> scrapers;

	private ScrapeAmalgamatedProgressDialog parent;

	/**
	 *
	 * @param scrapers
	 * @param parent
	 * @param fileToScrape - file scraped if no gui (if there is a gui we use the state variable from there wich is the file to scrape)
	 */
	public ScrapeAmalgamatedMovieWorker(List<SiteParsingProfile> scrapers, File fileToScrape, ScrapeAmalgamatedProgressDialog parent) {
		runningWorkers = new HashMap<>();
		progress = 0;
		amountOfProgressPerSubtask = 0;
		scrapeCanceled = false;
		this.scrapers = scrapers;
		this.fileToScrape = fileToScrape;
		this.parent = parent;
		System.out.println("New worker with"+scrapers);
	}

	SwingWorker<Void, Void> getWorkerByScraperName(SiteParsingProfile scraper) {
		SwingWorker<Void, Void> worker = runningWorkers.get(scraper.getDataItemSourceName());
		return worker;
	}

	public void cancelRunningScraper(SiteParsingProfile scraper) {
		SwingWorker<Void, Void> scraperToCancel = runningWorkers.get(scraper.getDataItemSourceName());
		if (scraperToCancel != null) {
			System.out.println("Canceling " + scraper + " + thread.");
			boolean wasThreadCanceled = scraperToCancel.cancel(true);
			if (wasThreadCanceled) {
				numberOfScrapesFinished++;
			}
		}
	}

	public void cancelAllRunningScrapers() {
		for (SwingWorker<Void, Void> currentWorker : runningWorkers.values()) {
			if (currentWorker != null) {
				System.out.println("Canceling " + currentWorker);
				currentWorker.cancel(true);
			}
		}
	}

	private static void failIfInterrupted() throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
		  throw new InterruptedException("Interrupted while searching files");
		}
	  }

	@Override
	protected Void doInBackground() {
		int numberOfScrapes = 0;
		int progressAmountPerWorker;

		setProgress(0);
		//failIfInterrupted();

		//get the latest version of the sraper group preference - if it's not there for whatever reason (usually from a specific scrape), just leave it alone
		/*ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreferenceNew = allAmalgamationOrderingPreferences
				.getScraperGroupAmalgamationPreference(scraperGroupAmalgamationPreference.getScraperGroupName());
		if (scraperGroupAmalgamationPreferenceNew != null)
			scraperGroupAmalgamationPreference = scraperGroupAmalgamationPreferenceNew;
*/
		//LinkedList<DataItemSource> scraperList = scraperGroupAmalgamationPreference.getOverallAmalgamationPreference().getAmalgamationPreferenceOrder();
		//calculate progress amount per worker

		for (DataItemSource currentScraper : scrapers) {
			if (currentScraper instanceof SiteParsingProfile)
				numberOfScrapes++;
		}

		System.out.println(""+numberOfScrapes+" scrapers");
		if (numberOfScrapes == 0) {
			progressAmountPerWorker = 100;
		} else {
			progressAmountPerWorker = 100 / numberOfScrapes;
		}

		for (DataItemSource currentScraper : scrapers) {
			//We don't want to read any leftover properties from our JSON - we want to start fresh so things like scraping language do not get set in our scraper
			currentScraper = currentScraper.createInstanceOfSameType();
			if (currentScraper instanceof SiteParsingProfile) {
					scrapeMovieInBackground(fileToScrape, currentScraper, progressAmountPerWorker);
					numberOfScrapesToRun++;
			}
		}

		//failIfInterrupted();

		//System.out.println("returnMovie is " + returnMovie);

		setProgress(100);

		return null;
	}

	private Movie scrapeMovieInBackground(File fileToScrape, DataItemSource scraper, int amountOfProgress) {
		// failIfInterrupted();
		if (scraper instanceof SiteParsingProfile) {
			final SiteParsingProfile siteScraper = (SiteParsingProfile) scraper;
			final ScrapeAmalgamatedMovieWorker self = this;
			final int amtOfProgressFinal = amountOfProgress;
			final File fileToScrapeFinal = fileToScrape;

			System.out.println(fileToScrapeFinal);

			SwingWorker<Void, Void> scraperWorker = new SwingWorker<Void, Void>() {
				Movie returnMovie;

				@Override
				protected Void doInBackground() throws Exception {
					try {
						//delegate back to the parent, if we have one, to override the URL we are going to scrape with a custom URL provided by the user.
						boolean customURLSet = false;
						if (parent != null) {
							customURLSet = parent.showPromptForUserProvidedURL(siteScraper, fileToScrapeFinal);
						}
						returnMovie = Movie.scrapeMovie(fileToScrapeFinal, siteScraper, "", customURLSet);

						return null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done() {

					self.numberOfScrapesFinished++;
					//System.out.println("Movie scraped = " + returnMovie);
					Map<SiteParsingProfile, Movie> resultToPublish = new HashMap<>();
					resultToPublish.put(siteScraper, returnMovie);
					self.publish(resultToPublish);
					self.progress = amtOfProgressFinal + self.progress;
					self.setProgress(self.progress);
					System.out.println("Scraping complete of siteScraper = " + siteScraper);
					self.runningWorkers.remove(siteScraper);
				}
			};
			self.runningWorkers.put(scraper.getDataItemSourceName(), scraperWorker);
			scraperWorker.execute();
		}

		// failIfInterrupted();
		return null;

	}

	@Override
	protected void done() {

	}

	/**
	 * Enums used to fire properties.
	 * ALL_SCRAPES_FINISHED - used when all scraper workers have finished or been canceled
	 * SCRAPED_MOVIE - One of the scraper threads has finished and is returning back the amalgamated movie it found
	 */
	public enum ScrapeAmalgamatedMovieWorkerProperty {
		ALL_SCRAPES_FINISHED, SCRAPED_MOVIE
	}

	@Override
	protected void process(List<Map<SiteParsingProfile, Movie>> movies) {

		firePropertyChange(ScrapeAmalgamatedMovieWorkerProperty.SCRAPED_MOVIE.toString(), currentPublishedMovies, movies);
		currentPublishedMovies = movies;

		if (numberOfScrapesFinished >= numberOfScrapesToRun)
			firePropertyChange(ScrapeAmalgamatedMovieWorkerProperty.ALL_SCRAPES_FINISHED.toString(), null, numberOfScrapesFinished);
		else {
			System.out.println("Finished " + numberOfScrapesFinished + "/" + numberOfScrapesToRun + " scrape threads.");
		}
	}
}
