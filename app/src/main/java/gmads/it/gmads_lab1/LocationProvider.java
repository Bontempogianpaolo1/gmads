package gmads.it.gmads_lab1;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;


public class LocationProvider {
    private LocationManager lm;
    boolean gps_enabled=false;
    boolean network_enabled=false;

    public Location getLocation(Context context){

        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if(!gps_enabled && !network_enabled)
            return null;

        Location net_loc=null, gps_loc=null;

        if(!(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)){

            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION, context,this);
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }

        if(gps_enabled)
            gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(network_enabled)
            net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(gps_loc!=null && net_loc!=null){
            if(gps_loc.getTime() > net_loc.getTime())
                return gps_loc;
            else
                return net_loc;
        }

        if(gps_loc!=null){
            return gps_loc;
        }
        if(net_loc!=null) {
            return net_loc;
        }
        return null;
    }

}
