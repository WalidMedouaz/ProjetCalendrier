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

    // formations :
    final String QUERY_URL_L1 = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200c7a4509b49242abad75bde9ec3807bc263605e824b95746d79d9f00dfb38346170d195fee1497b1118633b26af8a114f467cb4606a93a4fcdf0bb2cae2ca80d69aeeabea553382292598c6ae151b3d1855e49c2583691d409bd8a1ade68675fa30550f78673b771bb9b0e6ea59c55bd4ad31f2b82c358a5702e8cb68dbca31eda4ce337a6df2dcf202ed32";
    final String QUERY_URL_L2 = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020029058315962e559dcd2445953ad5198b5848d7b0b536ac88272798434ac788b8456223777fbed8a55b73894959e9ef4fc31a0bd57c510d9e0da95814b23193672e4ea133134536354892cccabc84dfea4227941b61644398f58eec140badc5169e78ccdd889ddea7a6c9994e003e809dc7faa07d87518044c9be5094c4d2a974509571f70e8246f2b61c875254ef5eaeabc4aea88671945b75da45e4a15039";
    final String QUERY_URL_L3 = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200b0789e9d4865108f2ea4f33a683ac59b5b0fcd69a6338f8dc0a5f3879b7004b5f90263d195840255d485e9ac9d5e11c002158b5d3dc614594ba01914e63359c74427114f59c342f886c12de2f61d8450c47afaef8c998d063ccec680075dbc930edd5f20de0f5d61921e529504df16a3122a084c923f7353418eb02392e0259509";
    final String QUERY_URL_M1_ILSEN = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def502000a1ec3e8fa2fae0bf9f59ebb42d6c062a30f8baf4dee868998cca57bff5014c07acf4fb9f52d825a28156f43af4551b9aa2b11b8af5ae4e9578d5baeecbc5fa83fa4fc2e07b085b70e0c22ef6fd2f2bc5d967d2616dd998c034a0e6e40fab347486aa9";
    final String QUERY_URL_M1_IA = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200aff62a04115f529b8d68e14ee86075841ee2f442fff0a4f79027778daec1c6cd841854e77bed6d1d442964ad9272427d7c3cc60e8bd29776b166b1b7d6bda0bb2d502ebb03ae92031f9de569fc7a17b20d74190ae07e348e86";
    final String QUERY_URL_M1_SICOM = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def502006ede665507b6fa638f51e6aaa9e46fe8b81a74b1ae738e45e42045fcd2e552a8b215982e263823a72851e5384caf57df98b4bca74d3d87c6c459b756007fdd89495e4d75a517b64b19acd55055fb6ecd01b4f2e2a0d419bf2b";
    // salles :

    final String QUERY_URL_STAT1 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def5020035971d25333ec6ba4d5117be29315b790d7504f47dd939fab78ee41002add189cd33de0f7b43afb52c391b42aa2647a7d75084541bd09bc24e3abdfdf42e20789ba5845283b9c2a6283f3c344e4625af3f2532ad41a4f64662b6";
    final String QUERY_URL_STAT2 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200fc6ccc30fc1bea7c93a13990eb0a10fd57dc6a0b507a0e4d3c07dad3b075a60fc304c4bfa94afd55e9761d0781a22e659f960b9ef6eee036e3f9d2de7f75ce359d39a16b8c4daaf83eae4a3cc917ceb490aafca5907bc89543b6";
    final String QUERY_URL_STAT3 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200bd230f83290c0473542749a4c53748408dbd91975bb16508646193e6a8020e2c010eef8ea777ee3d51befe98f5fcb6c1b692b835f7e6f52f1a29b3007edaae3e4588527a20a57ff0c51c57719cc47242d64ece467dd290f99c93";
    final String QUERY_URL_STAT4 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200840505319c2f05c80ed5253313c21818d5ddc5261fa4f1b4a568c880d5aa1369092b8fd878f12c9a7590623168431caa8c4015cb5826e4bbed31b0aa87119d2c4b7c84407d2717d5300683099cfcfc6ebc273b05468de79b2e1f";
    final String QUERY_URL_STAT5 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200bedac8ce8bed178b8a6990fae942e07eefbe4b8627f7abf7d0e001bfbc667fc6a64c008d3e54151da48407c149b3653a56360e8eb3f8fc1d78089887276bacfe810f9c1303d651ae6d33602854cd6696df10befdf39a14e6a09c";
    final String QUERY_URL_STAT6 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200e2a5d81ff0611471ff5bece8e6af4d659b2e9f4ad18a718385206d4b9aa380ea6a744b0ee6a3952e6b654bf952191838c0d648271170e5c44dfb5672bd339b289f2f3154815897905d5e72483224c4988295a3e692868cb9ad1b";
    final String QUERY_URL_STAT7 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200db41928d044e500a4df66813d75a0d7ad781e9541dc07ae4ab4814c0f7cdb3b3a497adfa5807368c9f4db327cd6d773df16b82882c622b981e84ae18cdb73003790cb95aa5a5800fd666cc9d75c7d7691fc214058ea27a21d0d0";
    final String QUERY_URL_STAT8 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200807d25e66755ed466ad7984fa1c3c364e8da4f8005960bd8a132cd85cdcd7cecc64ee4ff1c7753ab483cd6eebedd8093039a17434c137182c38d494b79bc53599029c23ad47c580288da3d06d35dd7c40f8c30a2ee0e4ad77612";
    final String QUERY_URL_STAT9 = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200d1fcb9e1b8b1f27520a5532cc0c975aed89328aa0be573565c7407f02efa8bc8f48d6f3310714d8a968b9a63ceb0399c4c5f2e3dc0fece923edead8bfd54bb02bbc7171ab58a7343738f6df049be1a70abce286746bc14409891";

    // enseignants :
    final String QUERY_URL_CECILLON_NOE = "https://edt-api.univ-avignon.fr/api/exportAgenda/enseignant/def5020014cf744f63f7181931e243c5139c5d8427de488f3da5b30b52905edfe9de85e8da750e291f852c095f6fd05f93658cbbf3260bf1308a84c444accdb9ab8f67de5f5758e0b59200e3c78068a677fc5055644c4635";
    final String QUERY_URL_AMALVY_ARTHUR = "https://edt-api.univ-avignon.fr/api/exportAgenda/enseignant/def502001e3795b29e379709bfc2fd4ddeb12e5483199b08e545dd64087bc2fec0fd10d33bdac3784ec51248e6d1f7615b53eb572179264f74b796ffb1a512da93a1d7954384c2f73388323ccda9e8dacafb6d229a95cb01";

    // choix final :
    final String QUERY_URL = QUERY_URL_M1_ILSEN;
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