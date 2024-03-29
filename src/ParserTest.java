import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParserTest {
    private ArrayList<String> distinctGroups = new ArrayList<String>(); // List of all possible groups
    private ArrayList<String> distinctSubjects = new ArrayList<String>(); // List of all possible subjects
    private ArrayList<String> distinctTypes = new ArrayList<String>() ; // List of all possible class types
    private ArrayList<String> distinctLocation = new ArrayList<String>(); // List of all possible locations

    final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def502000a1ec3e8fa2fae0bf9f59ebb42d6c062a30f8baf4dee868998cca57bff5014c07acf4fb9f52d825a28156f43af4551b9aa2b11b8af5ae4e9578d5baeecbc5fa83fa4fc2e07b085b70e0c22ef6fd2f2bc5d967d2616dd998c034a0e6e40fab347486aa9";
    //final String QUERY_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c7a4509b49242abad75bde9ec3807bc263605e824b95746d79d9f00dfb38346170d195fee1497b1118633b26af8a114f467cb4606a93a4fcdf0bb2cae2ca80d69aeeabea553382292598c6ae151b3d1855e49c2583691d409bd8a1ade68675fa30550f78673b771bb9b0e6ea59c55bd4ad31f2b82c358a5702e8cb68dbca31eda4ce337a6df2dcf202ed32";

    public ArrayList<String> getDistinctGroups() {
        return distinctGroups;
    }

    public ArrayList<String> getDistinctSubjects() {
        return distinctSubjects;
    }

    public ArrayList<String> getDistinctTypes() {
        return distinctTypes;
    }

    public ArrayList<String> getDistinctLocation() {
        return distinctLocation;
    }
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

    public void getCalendarEvents(ArrayList<Event> events) throws IOException, ParseException {
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
                            else {
                                subject = inputLine.substring(inputLine.indexOf("SUMMARY;LANGUAGE=fr:") + 20, inputLine.indexOf("-") - 1);
                            }
                            inputLine = in.readLine();
                        } else if (inputLine.contains("DESCRIPTION")) {
                            String fullDescriptionLine = inputLine;
                            while (!(inputLine = in.readLine()).contains("X-ALT-DESC")) {
                                fullDescriptionLine += inputLine.substring(1); // Le premier charactère d'une nouvelle ligne est toujours un espace
                            }
                            if(fullDescriptionLine.contains("TD : ")) {
                                group = fullDescriptionLine.substring(fullDescriptionLine.indexOf("TD :") + 4, fullDescriptionLine.indexOf("\\n", fullDescriptionLine.indexOf("TD :"))).replace("\\", "");;
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
                    events.add(new Event(startDate, endDate, teacher, location, subject, type, group));
                    getDistinctSubject(subject);
                    getDistinctLocation(location);
                    getDistinctGroup(group);
                    getDistinctType(type);
                }
                inputLine = in.readLine();
            }
            in.close();
        } else {
            System.out.println("GET request didn't work");
        }
    }

    private void getDistinctLocation(String location) {
        if(location == null) {
            return;
        }
        if (location.contains(",")) {
            String[] splitStrings = location.split(",");
            for(String s : splitStrings) {
                if(!distinctLocation.contains(s.strip())) {
                    distinctLocation.add(s.strip()); // Retirer les espaces au début
                }
            }
        } else if(!distinctLocation.contains(location.strip())){
            distinctLocation.add(location.strip());
        }
    }

    private void getDistinctType(String type) {
        if (type == null) {
            return;
        }
        if (type.contains(",")) {
            String[] splitStrings = type.split(",");
            for(String s : splitStrings) {
                if(!distinctTypes.contains(s.strip())) {
                    distinctTypes.add(s.strip()); // Retirer les espaces au début
                }
            }
        } else if(!distinctTypes.contains(type.strip())){
            distinctTypes.add(type.strip());
        }
    }

    private void getDistinctSubject(String subject) {
        if(subject == null) {
            return;
        }
        if (subject.contains(",")) {
            String[] splitStrings = subject.split(",");
            for(String s : splitStrings) {
                if(!distinctSubjects.contains(s.strip())) {
                    distinctSubjects.add(s.strip()); // Retirer les espaces au début
                }
            }
        } else if(!distinctSubjects.contains(subject.strip())){
            distinctSubjects.add(subject.strip());
        }
    }

    private void getDistinctGroup(String group) {
        if(group == null) {
            return;
        }
        if (group.contains(",")) {
            String[] splitStrings = group.split(",");
            for(String s : splitStrings) {
                s = s.replaceFirst("^TD : ", "").replaceAll("\\\\+$", "");
                if(!distinctGroups.contains(s.strip())) {
                    distinctGroups.add(s.strip()); // Retirer les espaces au début
                }
            }
        } else if(!distinctGroups.contains(group.strip())){
            distinctGroups.add(group.strip());
        }
    }

    public void filter(ArrayList<Event> events, String filterType, String filterContent) {
        try {
            switch (filterType) {
                case "Matière":
                    events.removeIf(p -> p.getSubject()!= null && !p.getSubject().contains(filterContent));
                    break;
                case "Groupe":
                    events.removeIf(p -> p.getGroup() != null && !p.getGroup().contains(filterContent));
                    break;
                case "Salle":
                    events.removeIf(p -> p.getLocation() != null && !p.getLocation().contains(filterContent));
                    break;
                case "Type de cours":
                    events.removeIf(p -> p.getType() != null && !p.getType().contains(filterContent));
                    break;
            }
        }
        catch (Exception e) {
            System.out.println("Error with " + filterType + " for this event !");
        }
    }

}