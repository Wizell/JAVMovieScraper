package moviescraper.doctord.scraper;

import java.net.CookieManager;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.logging.Logger;

public class CloudflareScraper {

	private static final Logger LOGGER = Logger.getLogger( CloudflareScraper.class.getName() );
	
    private String UA;
    private final String url;
	CookieManager cm;
	private ScriptEngine engine;

	public CloudflareScraper(String url) {
        this.url = url;

	}

    public static String handleCloudflare(URL url, Response response) {

        try {
			Document document = response.parse();
			Element form = document.select("#challenge-form").first();
			String jschl_vc = form.select("[name=jschl_vc]").first().val();
			String pass = form.select("[name=pass]").first().val();
			String str = response.parse().html();
			LOGGER.log(Level.FINE, "Body: {0}", str);
			String jschl_answer = get_answer(url, str);
			LOGGER.log(Level.FINE, "JS Challenge response: {0}", jschl_answer);


			Thread.sleep(4000);

			URIBuilder builder = new URIBuilder(url.toString())
				.setPath(form.attributes().get("action"))
				.addParameter("jschl_vc", jschl_vc)
				.addParameter("pass", pass)
				.addParameter("jschl_answer", jschl_answer);
			LOGGER.log(Level.INFO, "Cloudflare answer: {0}", builder.toString());
			return builder.toString();
        } catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Cannot bypass cloudflare protection", ex);
			return null;
		}
    }

    private static String get_answer(URL url, String body) {
        double a = 0;
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine engine = engineManager.getEngineByName("nashorn");

		try {
			List<String> s = regex(body, "var s,t,o,p,b,r,e,a,k,i,n,g,f, (.+?)=\\{\"(.+?)\"");
			String varA = Objects.requireNonNull(s).get(0);
			String varB = s.get(1);
			StringBuilder sb = new StringBuilder();
			sb.append("a=");
			sb.append(Objects.requireNonNull(regex(body, varA + "=\\{\"" + varB + "\":(.+?)\\}")).get(0));
			sb.append(";");
			List<String> b = regex(body, varA + "\\." + varB + "(.+?)\\;");
			for (int i = 0; i < Objects.requireNonNull(b).size() - 1; i++) {
				sb.append("a");
				sb.append(b.get(i));
				sb.append(";");
			}
			LOGGER.log(Level.FINE, "Rebuilt JS: {0}", sb.toString());
			Bindings bindings = engine.createBindings();
			bindings.put("a", 0);
			engine.eval(sb.toString(), bindings);
			Double value = (Double) bindings.get("a");
			a = Math.round(value * 10000000000.d) / 10000000000.0d;
			a += url.getHost().length();
			
        } catch (ScriptException e) {
            LOGGER.log(Level.WARNING, "Cannot resolv JS challenge", e);
			throw new RuntimeException(e);
        }
		DecimalFormat df = new DecimalFormat("#.##########"); 
        return df.format(a).replace(',', '.');
    }

    private static List<String> regex(String text, String pattern) {
		Pattern pt = Pattern.compile(pattern);
		Matcher mt = pt.matcher(text);
		List<String> group = new ArrayList<>();

		while (mt.find()) {
			if (mt.groupCount() >= 1) {
				if (mt.groupCount() > 1) {
					group.add(mt.group(1));
					group.add(mt.group(2));
				} else {
					group.add(mt.group(1));
				}
			}
		}
		return group;
    }
}





