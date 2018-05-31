package gmads.it.gmads_lab1.model;



public class Request {

    private int reviewStatusOwner;
    private int reviewStatusRenter;
    private int requestStatus;
    private String ownerId;
    private String renterId;

    public Request(int reviewStatusOwner, int reviewStatusRenter, int requestStatus, String ownerId, String renterId) {
        this.reviewStatusOwner = reviewStatusOwner;
        this.reviewStatusRenter = reviewStatusRenter;
        this.requestStatus = requestStatus;
        this.ownerId = ownerId;
        this.renterId = renterId;

    }

    public int getReviewStatusOwner() {
        return reviewStatusOwner;
    }

    public void setReviewStatusOwner(int reviewStatusOwner) {
        this.reviewStatusOwner = reviewStatusOwner;
    }

    public int getReviewStatusRenter() {
        return reviewStatusRenter;
    }

    public void setReviewStatusRenter(int reviewStatusRenter) {
        this.reviewStatusRenter = reviewStatusRenter;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }
}
