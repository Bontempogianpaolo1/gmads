package gmads.it.gmads_lab1.model;



public class Request {
    private String owner;
    private String renter;
    private int state;


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

    public int getState() {
        return state;
    }

    public void setState( int state ) {
        this.state = state;
    }

    public Request( String owner, String renter, int state) {
        this.owner = owner;
        this.renter = renter;
        this.state = state;
    }
}
