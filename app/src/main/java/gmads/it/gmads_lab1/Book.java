package gmads.it.gmads_lab1;

public class Book {
    private String isbn;
    private String name;
    private String descricption;
    private String value;

    public Book(String isbn, String name, String descricption, String value) {
        this.isbn = isbn;
        this.name = name;
        this.descricption = descricption;
        this.value = value;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescricption() {
        return descricption;
    }

    public void setDescricption(String descricption) {
        this.descricption = descricption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
