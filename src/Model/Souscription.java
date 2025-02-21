package Model;

import java.util.Date;

public class Souscription {
    private int id;
    private int idAbonne;      // Foreign key referencing the subscriber
    private int idAbonnement;   // Foreign key referencing the subscription plan
    private Date dateDebut;    // Start date of the subscription
    private String status;      // Status of the subscription (actif, expiré, annulé)
    private String nomAbonnement; // Name of the subscription
    private String nomAbonne; // Name of the subscriber

    // Constructors
    public Souscription() {}

    public Souscription(int id, int idAbonne, int idAbonnement, Date dateDebut, String status, String nomAbonnement) {
        this.id = id;
        this.idAbonne = idAbonne;
        this.idAbonnement = idAbonnement;
        this.dateDebut = dateDebut;
        this.status = status;
        this.nomAbonnement = nomAbonnement;
    }

    public Souscription(int id, int idAbonne, int idAbonnement, Date dateDebut, String status, String nomAbonnement, String nomAbonne) {
        this.id = id;
        this.idAbonne = idAbonne;
        this.idAbonnement = idAbonnement;
        this.dateDebut = dateDebut;
        this.status = status;
        this.nomAbonnement = nomAbonnement;
        this.nomAbonne = nomAbonne;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAbonne() {
        return idAbonne;
    }

    public void setIdAbonne(int idAbonne) {
        this.idAbonne = idAbonne;
    }

    public int getIdAbonnement() {
        return idAbonnement;
    }

    public void setIdAbonnement(int idAbonnement) {
        this.idAbonnement = idAbonnement;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNomAbonnement() {
        return nomAbonnement;
    }

    public void setNomAbonnement(String nomAbonnement) {
        this.nomAbonnement = nomAbonnement;
    }

    public String getNomAbonne() {
        return nomAbonne;
    }

    public void setNomAbonne(String nomAbonne) {
        this.nomAbonne = nomAbonne;
    }

    @Override
    public String toString() {
        return "Souscription{" +
                "id=" + id +
                ", idAbonne=" + idAbonne +
                ", idAbonnement=" + idAbonnement +
                ", dateDebut=" + dateDebut +
                ", status='" + status + '\'' +
                ", nomAbonnement='" + nomAbonnement + '\'' +
                ", nomAbonne='" + nomAbonne + '\'' +
                '}';
    }
}