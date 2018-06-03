package gmads.it.gmads_lab1.ReviewPackage;

public class Review {
    private String user;
    private String comment;
    private float rate;

    public Review( String user, String comment, float rate ) {
        this.user = user;
        this.comment = comment;
        this.rate = rate;
    }

    public Review() {

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

    public float getRate() {
        return rate;
    }

    public void setRate( float rate ) {
        this.rate = rate;
    }
}
