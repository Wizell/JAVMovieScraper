package moviescraper.doctord.scraper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.logging.Logger;

public class SillyHeadlessBrowser {

	private final String userAgent;
	private Map<String, Map<String, String>> cookies;
	private final int timeout;
	private static final Logger LOGGER = Logger.getLogger(SillyHeadlessBrowser.class.getName());

	public SillyHeadlessBrowser(String userAgent, int timeout) {
		this.userAgent = userAgent;
		this.timeout = timeout;
		this.cookies = new HashMap<>();
		LOGGER.log(Level.INFO, "Build browser with U: {0}", this.userAgent);
	}

	public SillyHeadlessBrowser() {
		this(UserAgent.getRandomUserAgent(), 10000);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(this.userAgent);
		string.append(", timeout: ");
		string.append(this.timeout);
		if(this.cookies != null) {
			string.append(", cookies: ");
			string.append(this.cookies);
		}
		return string.toString();
	}

	public Map<String, String> getCookies(String realm) {
		if(!cookies.containsKey(realm)) {
			cookies.put(realm, new HashMap<>());
		}

		return cookies.get(realm);
	}

	public Map<String, String> getCookies(URL url) {
		return getCookies(url.getHost());
	}

	public void addCookie(String realm, String key, String value) {
		getCookies(realm).put(key, value);
	}

	public void addCookies(String realm, Map<String, String> newCookies) {
		getCookies(realm).putAll(newCookies);
	}


	public Document get(URL url) throws IOException {
		LOGGER.log(Level.INFO, "Get request on {0}", url.toString());
		Connection connection = Jsoup.connect(url.toString())
			.userAgent(userAgent)
			.ignoreHttpErrors(true)
			.timeout(timeout)
			.followRedirects(true)
			.method(Connection.Method.GET);

		connection = connection.cookies(this.getCookies(url));

		Response response = connection.execute();
		if(response.cookies().size() > 0) {
			addCookies(url.getHost(), response.cookies());
		}


		return response.parse();
	}

}
