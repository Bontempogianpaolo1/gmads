package gmads.it.gmads_lab1.model;



public class Request {

    private int reviewStatusOwner;
    private int reviewStatusRenter;
    private int requestStatus;
    private String ownerId;
    private String renterId;
    private String ownerName;
    private String renterName;
    private String urlBookImage;

    public Request(int reviewStatusOwner, int reviewStatusRenter, int requestStatus, String ownerId, String renterId, String ownerName, String renterName, String urlBookImage) {
        this.reviewStatusOwner = reviewStatusOwner;
        this.reviewStatusRenter = reviewStatusRenter;
        this.requestStatus = requestStatus;
        this.ownerId = ownerId;
        this.renterId = renterId;
        this.ownerName = ownerName;
        this.renterName = renterName;
        this.urlBookImage = urlBookImage;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getUrlBookImage() {
        return urlBookImage;
    }

    public void setUrlBookImage(String urlBookImage) {
        this.urlBookImage = urlBookImage;
    }
}
