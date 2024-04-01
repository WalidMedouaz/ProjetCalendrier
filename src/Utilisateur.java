import org.bson.Document;

import java.util.List;

public class Utilisateur {
    public String id;
    public String nom;
    public String prenom;
    public String filiere;
    public String groupe;
    public boolean isEnseignant;
    public String modeFavori;
    public List<Document> eventPerso;
    public List<Document> reservations;

    public Utilisateur(String id, String nom, String prenom, boolean isEnseignant, String modeFavori, List<Document> eventPerso, List<Document> reservations) { // constructeur enseignant
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.isEnseignant = isEnseignant;
        this.modeFavori = modeFavori;
        this.eventPerso = eventPerso;
        this.reservations = reservations;
    }

    public Utilisateur(String id, String nom, String prenom, String filiere, String groupe, boolean isEnseignant, String modeFavori, List<Document> eventPerso) { // constructeur étudiant
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.filiere = filiere;
        this.groupe = groupe;
        this.isEnseignant = isEnseignant;
        this.modeFavori = modeFavori;
        this.eventPerso = eventPerso;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", filiere='" + filiere + '\'' +
                ", groupe='" + groupe + '\'' +
                ", isEnseignant=" + isEnseignant +
                ", modeFavori='" + modeFavori + '\'' +
                ", eventPerso=" + eventPerso +
                ", reservations=" + reservations +
                '}';
    }
}
