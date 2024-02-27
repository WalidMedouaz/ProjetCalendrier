import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParserTest {
    Calendar myCalendar;
    public static void main(String[] args) throws IOException, ParseException {
        Date startDate = null;
        Date endDate = null;
        String course;

        final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c81a68c12a4acc32822b8773e379ac7b489b9799138dbeba79f24e4a65fbdc51ed192440883a797fb60703bff239a539c87f12da5dca57b5336934b6055684e40cf673254768cdbd863aaf704cd3a6ab75233190";
        //final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c7a4509b49242abad75bde9ec3807bc263605e824b95746d79d9f00dfb38346170d195fee1497b1118633b26af8a114f467cb4606a93a4fcdf0bb2cae2ca80d69aeeabea553382292598c6ae151b3d1855e49c2583691d409bd8a1ade68675fa30550f78673b771bb9b0e6ea59c55bd4ad31f2b82c358a5702e8cb68dbca31eda4ce337a6df2dcf202ed32";

        URL url = new URL(QUERY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null && !inputLine.contains("BEGIN:VEVENT")) {
                if(inputLine.contains("X-CALSTART")) { // date de début du calendrier
                    String myTimestamp = inputLine.substring(11, inputLine.length() - 1); // on isole la date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss"); // le format de la date en "timestamp"
                    startDate = dateFormat.parse(myTimestamp);

                    /* Affichage propre de la date

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = outputFormat.format(startDate);

                     */
                }
                else if(inputLine.contains("X-CALEND")) { // date de fin du calendrier
                    String myTimestamp = inputLine.substring(9, inputLine.length() - 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    endDate = dateFormat.parse(myTimestamp);
                }
                else if(inputLine.contains("X-WR-CALNAME")) { // nom de la formation
                    int firstIndex = inputLine.indexOf("<") + 1;
                    int lastIndex = inputLine.indexOf(">");
                    if(lastIndex == - 1) { // si la formation est écrite sur plusieurs lignes
                        String firstPart = inputLine.substring(firstIndex);
                        inputLine = in.readLine();
                        lastIndex = inputLine.indexOf(">");
                        course = firstPart.concat(inputLine.substring(1, lastIndex));
                    }
                    else {
                        course = inputLine.substring(inputLine.indexOf("<") + 1, inputLine.indexOf(">"));
                    }
                    System.out.println(course);
                }
            }
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("END:VEVENT")) {
                    break;
                }
                //else if(inputLine.contains("END:VEVENT"))
                //System.out.println(inputLine);
            }
            in.close();

        }
        else {
            System.out.println("GET request didn't work");
        }
    }
}