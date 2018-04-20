package gmads.it.gmads_lab1;

import android.graphics.Bitmap;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class Profile {
    private String name;
    private String surname;
    private String email;
    private Bitmap image;
    private long valutation;
    private int npublishedBooks;
    private int ntakenBooks;
    private List<Book> publishedBooks;
    private List<Book> takenBooks;
    private int nrates;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users/");

    public Profile(String name, String surname, String email, Bitmap image) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.image = image;
        valutation=0;
        npublishedBooks=0;
        ntakenBooks=0;
        nrates=0;
        publishedBooks= Collections.emptyList();
        takenBooks= Collections.emptyList();
    }

    public void addValutation(long val){
        valutation+= val;
        nrates++;
    }
    public List<Book> getPublishedBooks() {
        return publishedBooks;
    }

    public void setPublishedBooks(List<Book> publishedBooks) {
        this.publishedBooks = publishedBooks;
    }

    public String getName() {
        return name;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public long getValutation() {
        if(nrates==0)return 0;

        return valutation/nrates;
    }

    public void setValutation(long valutation) {
        this.valutation = valutation;
        nrates=1;
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

    public List<Book> getTakenBooks() {
        return takenBooks;
    }

    public void setTakenBooks(List<Book> takenBooks) {
        this.takenBooks = takenBooks;
    }
    
    public void addProfiletoFire(){

    }
}
