package gmads.it.gmads_lab1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Profile implements Serializable{
    public String name;
    public String surname;
    public String email;
    public String description;
    public String image;
    private long valutation;
    private int npublishedBooks;
    private int ntakenBooks;
    private HashMap<String, String> myBooks = new HashMap<>();
    private List<String> publishedBooks= new ArrayList<>();
    private List<String> takenBooks= new ArrayList<>();
    private int nrates;
    private String CAP; //"CAP, ITA"
    private double lat;
    private double lng;

    public List<String> getRegistrationTokens() {
        return registrationTokens;
    }

    public void setRegistrationTokens(List<String> registrationTokens) {
        this.registrationTokens = registrationTokens;
    }

    private List<String> registrationTokens;

    public String getCAP() {
        return CAP;
    }

    public void setCAP( String CAP ) {
        this.CAP = CAP;
    }
    public boolean hasUploaded(){
        return myBooks != null;
    }
    public int takennBooks(){ return myBooks.size(); }
    public double getLat() {
        return lat;
    }

    public void setLat( double lat ) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng( double lng ) {
        this.lng = lng;
    }

    public Profile( String name, String surname, String email, String description) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.description= description;
        this.CAP = "";
        valutation=0;
        npublishedBooks=0;
        ntakenBooks=0;
        nrates=0;
        lat = 0;
        lng = 0;

    }

    public Profile() {
        valutation=0;
        npublishedBooks=0;
        ntakenBooks=0;
        nrates=0;
    }

    public HashMap<String, String> getMyBooks() {
        return myBooks;
    }

    public void setMyBooks(HashMap<String, String> myBooks) {
        this.myBooks = myBooks;
    }

    public String getName() {
        if(name!=null)
            return name;
        else
            return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getValutation() {
        return valutation;
    }

    public void setValutation(long valutation) {
        this.valutation = valutation;
    }

    public int getNpublishedBooks() {
        return npublishedBooks;
    }

    public void setNpublishedBooks(int npublishedBooks) {
        this.npublishedBooks = npublishedBooks;
    }

    public int getNtakenBooks() {
        return ntakenBooks;
    }

    public void setNtakenBooks(int ntakenBooks) {
        this.ntakenBooks = ntakenBooks;
    }

    public List<String> getPublishedBooks() {
        return publishedBooks;
    }

    public void setPublishedBooks(List<String> publishedBooks) { this.publishedBooks = publishedBooks; }

    public List<String> getTakenBooks() {
        return takenBooks;
    }

    public void setTakenBooks(List<String> takenBooks) {
        this.takenBooks = takenBooks;
    }

    public int getNrates() {
        return nrates;
    }

    public void setNrates(int nrates) {
        this.nrates = nrates;
    }
}
