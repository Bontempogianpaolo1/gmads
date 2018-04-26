package gmads.it.gmads_lab1;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private String description;
    private String urlimage;
    private String publishDate;
    private String author;
    private String categories;
    private double avgRating;
    private int nRates;
    private double sumRates;
    private String publisher;
    private List<String> comments;
    private String condition;
    private String owner;
    private String holder;
    private List<Bitmap> images;

    public Book(String isbn, String title, String description, String urlimage, String publishDate, String author, String categories, String publisher, String owner) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.urlimage = urlimage;
        this.publishDate = publishDate;
        this.author = author;
        this.categories = categories;
        this.publisher = publisher;
        this.owner = owner;
        comments= Collections.emptyList();
        images=Collections.emptyList();
        avgRating=0;
        nRates=0;
        sumRates=0;
    }
    public List<Bitmap> getImages() {
        return images;
    }

    public void setImages(List<Bitmap> images) {
        this.images = images;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getnRates() {
        return nRates;
    }

    public void setnRates(int nRates) {
        this.nRates = nRates;
    }

    public double getSumRates() {
        return sumRates;
    }

    public void setSumRates(double sumRates) {
        this.sumRates = sumRates;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }
}
