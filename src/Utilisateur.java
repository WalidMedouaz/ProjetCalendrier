public class Utilisateur {
    public String id;
    public String nom;
    public String prenom;
    public String isEnseignant;
    public String modeFavori;
    public String eventPerso;

    public Utilisateur(String id, String nom, String prenom, String isEnseignant, String modeFavori, String eventPerso) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.isEnseignant = isEnseignant;
        this.modeFavori = modeFavori;
        this.eventPerso = eventPerso;
    }
}
