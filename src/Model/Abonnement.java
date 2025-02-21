package Model;

public class Abonnement {
    private int id;
    private String libelle;
    private int dureeMois;
    private double prixMensuel;

    // Constructors
    public Abonnement() {}

    public Abonnement(int id, String libelle, int dureeMois, double prixMensuel) {
        this.id = id;
        this.libelle = libelle;
        this.dureeMois = dureeMois;
        this.prixMensuel = prixMensuel;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(int dureeMois) {
        this.dureeMois = dureeMois;
    }

    public double getPrixMensuel() {
        return prixMensuel;
    }

    public void setPrixMensuel(double prixMensuel) {
        this.prixMensuel = prixMensuel;
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                ", dureeMois=" + dureeMois +
                ", prixMensuel=" + prixMensuel +
                '}';
    }
}