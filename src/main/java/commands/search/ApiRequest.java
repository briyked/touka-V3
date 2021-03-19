package commands.search;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ApiRequest {

    private static final String BASE_URL = "http://4c3711.xyz:3000/touka/api/v2/getanime/";
    private final String provider = "animekisa";

    private List<String> shows;
    private String query;
    private int showIndex;

    public List<String> fetchShows(String query) throws IOException {
        this.query = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

        String route = String.format("%s/%s/", provider, this.query); //  animekisa/hunter-x-hunter/

        shows = jsonArrayToList(request(route));

        return shows;
    }

    public List<String> fetchEpisodes(int showIndex) throws IOException {
        String route = String.format("%s/%s/%d", provider, query, showIndex);
        this.showIndex = showIndex;

        System.out.println("Fetching episodes...");

        return jsonArrayToList(request(route));
    }

    public String fetchEpisode(int episodeIndex) throws IOException {

        String route = String.format("%s/%s/%d/%d", provider, query, showIndex, episodeIndex);

        return requestSingle(route);
    }

    public String getShowName() {
        return replaceLink(shows.get(showIndex));
    }

    private String replaceLink(String s) {
        return s.replace("https://4anime.to/anime/", "")
                .replace("https://animekisa.tv/", "")
                .replace("-", " ");
    }


    private JSONArray request(String route) throws IOException {
        return readJsonFromUrl(BASE_URL + route);
    }

    private String requestSingle(String route) throws IOException {
        return readContentFromUrl(BASE_URL + route);
    }

    private JSONArray readJsonFromUrl(String url) throws IOException {
        return new JSONArray(readContentFromUrl(url));
    }

    private String readContentFromUrl(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(20000);

        try (InputStream is = conn.getInputStream();
             InputStreamReader rd = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return readAll(rd);
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static List<String> jsonArrayToList(JSONArray arr) {
        return arr.toList().stream()
                .map(o -> (String) o)
                .collect(Collectors.toList());
    }


    public static void main(String[] args) throws IOException {
        String route = String.format("%s/%s", "animekisa", "hunter");
        String route2 = String.format("%s/%s/%d", "animekisa", "hunter", 0);
        String route3 = String.format("%s/%s/%d/%d", "animekisa", "hunter", 0, 0);

        System.out.println(new ApiRequest().request(route));
        System.out.println(new ApiRequest().request(route2));
        System.out.println(new ApiRequest().requestSingle(route3));
    }


}