import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ParserTest {

    final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c81a68c12a4acc32822b8773e379ac7b489b9799138dbeba79f24e4a65fbdc51ed192440883a797fb60703bff239a539c87f12da5dca57b5336934b6055684e40cf673254768cdbd863aaf704cd3a6ab75233190";
    //final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c7a4509b49242abad75bde9ec3807bc263605e824b95746d79d9f00dfb38346170d195fee1497b1118633b26af8a114f467cb4606a93a4fcdf0bb2cae2ca80d69aeeabea553382292598c6ae151b3d1855e49c2583691d409bd8a1ade68675fa30550f78673b771bb9b0e6ea59c55bd4ad31f2b82c358a5702e8cb68dbca31eda4ce337a6df2dcf202ed32";

    public CalendarCERI getCalendarHeader() throws IOException, ParseException {
        Date startDate = null;
        Date endDate = null;
        String course = null;

        URL url = new URL(QUERY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null && !inputLine.contains("BEGIN:VEVENT")) {
                if (inputLine.contains("X-CALSTART")) { // date de début du calendrier
                    String myTimestamp = inputLine.substring(11, inputLine.length() - 1); // on isole la date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss"); // le format de la date en "timestamp"
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Date récupérée en UTC +0 (London)
                    startDate = dateFormat.parse(myTimestamp);

                    /* Affichage propre de la date

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = outputFormat.format(startDate);

                     */
                } else if (inputLine.contains("X-CALEND")) { // date de fin du calendrier
                    String myTimestamp = inputLine.substring(9, inputLine.length() - 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Date récupérée en UTC +0 (London)
                    endDate = dateFormat.parse(myTimestamp);
                } else if (inputLine.contains("X-WR-CALNAME")) { // nom de la formation
                    int firstIndex = inputLine.indexOf("<") + 1;
                    int lastIndex = inputLine.indexOf(">");
                    if (lastIndex == -1) { // si la formation est écrite sur plusieurs lignes
                        String firstPart = inputLine.substring(firstIndex);
                        inputLine = in.readLine();
                        lastIndex = inputLine.indexOf(">");
                        course = firstPart.concat(inputLine.substring(1, lastIndex));
                    } else {
                        course = inputLine.substring(inputLine.indexOf("<") + 1, inputLine.indexOf(">"));
                    }
                }
            }
            in.close();
            return new CalendarCERI(startDate, endDate, course);
        } else {
            System.out.println("GET request didn't work");
        }
        return null;
    }

    public void getCalendarEvents(CalendarCERI calendarCERI) throws IOException, ParseException {
        URL url = new URL(QUERY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine = in.readLine();

            while ((inputLine != null)) {
                if (inputLine.contains("BEGIN:VEVENT")) {
                    Date startDate = null;
                    Date endDate = null;
                    String location = null;

                    String subject = null;
                    String teacher = null;
                    String type = null;
                    String group = null;

                    while (!(inputLine.contains("END:VEVENT"))) {
                        if (inputLine.contains("DTSTART") && !inputLine.contains("VALUE")) {
                            String myTimestamp = inputLine.substring(8, inputLine.length() - 1);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Date récupérée en UTC +0 (London)
                            startDate = dateFormat.parse(myTimestamp);
                            inputLine = in.readLine();
                        } else if (inputLine.contains("DTEND") && !inputLine.contains("VALUE")) {
                            String myTimestamp = inputLine.substring(6, inputLine.length() - 1);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Date récupérée en UTC +0 (London)
                            endDate = dateFormat.parse(myTimestamp);
                            inputLine = in.readLine();
                        } else if (inputLine.contains("LOCATION")) {
                            String fullLocationLine = inputLine; // Si représenté sur plusieurs lignes
                            while (!(inputLine = in.readLine()).contains("DESCRIPTION")) {
                                fullLocationLine += inputLine;
                            }
                            location = fullLocationLine.substring(fullLocationLine.indexOf(":") + 1).replace("\\", "");
                        } else if (inputLine.contains("SUMMARY")) {
                            if (!(inputLine.contains("-"))) { // Jours feriés
                                subject = inputLine.substring(inputLine.indexOf(":") + 1);
                            }
                            inputLine = in.readLine();
                        } else if (inputLine.contains("DESCRIPTION")) {
                            String fullDescriptionLine = inputLine;
                            while (!(inputLine = in.readLine()).contains("X-ALT-DESC")) {
                                fullDescriptionLine += inputLine.substring(1); // Le premier charactère d'une nouvelle ligne est toujours un espace
                            }
                            if(fullDescriptionLine.indexOf("TD : ") != -1) {
                                group = fullDescriptionLine.substring(fullDescriptionLine.indexOf("TD :"), fullDescriptionLine.indexOf("\\n", fullDescriptionLine.indexOf("TD :")));
                            }
                            else if(fullDescriptionLine.contains("Promotions :")) {
                                group = fullDescriptionLine.substring(fullDescriptionLine.indexOf("Promotions :") + 13, fullDescriptionLine.indexOf("\\n", fullDescriptionLine.indexOf("Promotions :"))).replace("\\", "");
                            }
                            subject = fullDescriptionLine.substring(fullDescriptionLine.indexOf("Matière :") + 10, fullDescriptionLine.indexOf("\\n"));
                            int teacherIndex = fullDescriptionLine.indexOf("Enseignant");
                            if (teacherIndex != -1) { // Si la liste d'enseignants est renseignée
                                if (fullDescriptionLine.contains("Enseignants")) {
                                    teacher = fullDescriptionLine.substring(teacherIndex + 14, fullDescriptionLine.indexOf("\\n", teacherIndex)).replace("\\", "");
                                } else {
                                    teacher = fullDescriptionLine.substring(teacherIndex + 13, fullDescriptionLine.indexOf("\\n", teacherIndex)).replace("\\", "");
                                }
                            }
                            int typeIndex = fullDescriptionLine.indexOf("Type");
                            if (typeIndex != -1) {
                                type = fullDescriptionLine.substring(typeIndex + 7, fullDescriptionLine.indexOf("\\n", typeIndex));
                            }
                            inputLine = in.readLine();
                        } else {
                            inputLine = in.readLine();
                        }
                    }
                    calendarCERI.getEvents().add(new Event(startDate, endDate, teacher, location, subject, type, group));
                }
                inputLine = in.readLine();
            }
            in.close();
        } else {
            System.out.println("GET request didn't work");
        }
    }

    public void filterBySubject(CalendarCERI calendarCERI, String filterType, String filterContent) {
        try {
            switch (filterType) {
                case "Subject":
                    calendarCERI.getEvents().removeIf(p -> !p.getSubject().contains(filterContent));
                    break;
                case "Group":
                    calendarCERI.getEvents().removeIf(p -> p.getGroup() != null && !p.getGroup().contains(filterContent));
                    break;
                case "Location":
                    calendarCERI.getEvents().removeIf(p -> !p.getLocation().contains(filterContent));
                    break;
                case "Type":
                    calendarCERI.getEvents().removeIf(p -> !p.getType().contains(filterContent));
                    break;
            }
        }
        catch (Exception e) {
            System.out.println("Error with " + filterType + " for this event !");
        }
        for(Event e : calendarCERI.getEvents()) {
            System.out.println(e.toString());
        }
    }

}