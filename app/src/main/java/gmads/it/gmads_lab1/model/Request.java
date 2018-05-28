package gmads.it.gmads_lab1.model;

public class Request {
    private String owner;
    private String renter;
    private String state;


    public String getOwner() {
        return owner;
    }

    public void setOwner( String owner ) {
        this.owner = owner;
    }

    public String getRenter() {
        return renter;
    }

    public void setRenter( String renter ) {
        this.renter = renter;
    }

    public String getState() {
        return state;
    }

    public void setState( String state ) {
        this.state = state;
    }

    public Request( String owner, String renter, String state ) {
        this.owner = owner;
        this.renter = renter;
        this.state = state;
    }
}
