package Model;

import java.util.Date;


public class Abonne {
    private int id;
    private String nom;
    private String prenom;
    private Date dateInscription;
    private String numeroTelephone;
    private boolean statutSouscription;

    // Constructors
    public Abonne() {}

    public Abonne(int id, String nom, String prenom, Date dateInscription, String numeroTelephone, boolean statutSouscription) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateInscription = dateInscription;
        this.numeroTelephone = numeroTelephone;
        this.statutSouscription = statutSouscription;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (nom.length() > 50) {
            throw new IllegalArgumentException("Le nom ne peut pas dépasser 50 caractères");
        }
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (prenom.length() > 50) {
            throw new IllegalArgumentException("Le prénom ne peut pas dépasser 50 caractères");
        }
        this.prenom = prenom;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
    if (numeroTelephone == null || numeroTelephone.trim().isEmpty()) {
        throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être vide");
    }
    if (!numeroTelephone.matches("\\d+")) {
        throw new IllegalArgumentException("Le numéro de téléphone doit contenir uniquement des chiffres");
    }
    this.numeroTelephone = numeroTelephone;
    }

    public boolean isStatutSouscription() {
        return statutSouscription;
    }

    public void setStatutSouscription(boolean statutSouscription) {
        this.statutSouscription = statutSouscription;
    }

    @Override
    public String toString() {
        return "Abonne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateInscription=" + dateInscription +
                ", numeroTelephone='" + numeroTelephone + '\'' +
                ", statutSouscription=" + statutSouscription +
                '}';
    }
}
