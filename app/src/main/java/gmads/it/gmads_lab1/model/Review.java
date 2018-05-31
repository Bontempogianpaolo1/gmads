package gmads.it.gmads_lab1.model;

public class Review {
    private String user;
    private String comment;
    private Double rate;

    public Review( String user, String comment, Double rate ) {
        this.user = user;
        this.comment = comment;
        this.rate = rate;
    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate( Double rate ) {
        this.rate = rate;
    }
}
