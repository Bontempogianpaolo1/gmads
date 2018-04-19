package gmads.it.gmads_lab1;

import android.media.Image;

public class Profile {
    String name;
    String surname;
    String email;
    Image image;
    private long valutation;
    private int GivenBooks;
    private int TakenBooks;

    public Profile(String name, String surname, String email, Image image) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.image = image;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public long getValutation() {
        return valutation;
    }

    public void setValutation(long valutation) {
        this.valutation = valutation;
    }

    public int getGivenBooks() {
        return GivenBooks;
    }

    public void setGivenBooks(int givenBooks) {
        GivenBooks = givenBooks;
    }

    public int getTakenBooks() {
        return TakenBooks;
    }

    public void setTakenBooks(int takenBooks) {
        TakenBooks = takenBooks;
    }
}
