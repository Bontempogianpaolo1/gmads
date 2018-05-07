package gmads.it.gmads_lab1.Map.common.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Route {

    @SerializedName("legs") List<Leg> legs;
    @SerializedName("overview_polyline") Polyline polyline;
    @SerializedName("bounds") gmads.it.gmads_lab1.Map.common.model.Bounds bounds;

    public List<Leg> getLegs() {
        return legs;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public Polyline getOverviewPolyline() {
        return polyline;
    }
}
