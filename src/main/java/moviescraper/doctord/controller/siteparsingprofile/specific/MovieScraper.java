package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.util.ArrayList;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

public interface MovieScraper {
	
	public abstract Title scrapeTitle();
	public abstract OriginalTitle scrapeOriginalTitle();
	public abstract SortTitle scrapeSortTitle();
	public abstract Set scrapeSet();
	public abstract Rating scrapeRating();
	public abstract ReleaseDate scrapeReleaseDate();
	public abstract Year scrapeYear();
	public abstract Top250 scrapeTop250();
	public abstract Votes scrapeVotes();
	public abstract Outline scrapeOutline();
	public abstract Plot scrapePlot();
	public abstract Tagline scrapeTagline();
	public abstract moviescraper.doctord.model.dataitem.Runtime scrapeRuntime();
	public abstract Thumb[] scrapePosters();
	public abstract Thumb[] scrapeFanart();
	public abstract Thumb[] scrapeExtraFanart();
	public abstract MPAARating scrapeMPAA();
	public abstract ID scrapeID();
	public abstract ArrayList<Genre> scrapeGenres();
	public abstract ArrayList<Actor> scrapeActors();
	public abstract ArrayList<Director> scrapeDirectors();
	public abstract Studio scrapeStudio();
	public abstract String createSearchString(File file);
	public abstract Trailer scrapeTrailer();
	public abstract ArrayList<Tag> scrapeTags();
}
