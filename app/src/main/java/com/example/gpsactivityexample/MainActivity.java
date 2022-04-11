package com.example.gpsactivityexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView salida;
    private LocationManager managerloc;
    private final String[] accuracy = {"n/d", "preciso","impreciso"};
    private final String[] power = {"n/d", "bajo","medio","alto","muy alto"};
    private String proveedor;
    private final int seconds = 10;
    private final float meters = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salida = findViewById(R.id.display);

        managerloc = (LocationManager) getSystemService(LOCATION_SERVICE);
        mostrarProveedores();

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        criteria.setAltitudeRequired(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        proveedor = managerloc.getBestProvider(criteria,true);
        @SuppressLint("MissingPermission")
        Location location = managerloc.getLastKnownLocation(proveedor);
        salida.append("\nLastKnowedLocation: \n");
        mostrarLocalizacion(location);

    }

    private void mostrarLocalizacion(Location location) {
        if(location!=null) {
            salida.append("Provider: " + location.getProvider() + "\n");
            salida.append("Lat: " + location.getLatitude() + "\n");
            salida.append("Lon: " + location.getLongitude() + "\n");
            salida.append("Altitude: " + (int)location.getAltitude() + "m.\n");
            salida.append("Accuracy: " + (int)location.getAccuracy() + "m.\n");

            if(location.getTime()!=0) {
                Date date = new Date(location.getTime());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm:ss" , new Locale("es"));
                salida.append("Time: " + simpleDateFormat.format(date) + "\n");
            }
            salida.append("Speed: " + location.getSpeed() + "\n");
        }else
            salida.append("\nLocation unknown");
    }

    private void mostrarProveedores() {
        List<String> proveedores = managerloc.getAllProviders();
        for(String proveedor : proveedores){
            mostrarProveedor(proveedor);
        }
    }

    private void mostrarProveedor(String proveedor) {
        LocationProvider locationProvider = managerloc.getProvider(proveedor);
        salida.append("\n" + "LocationProvider name: " +proveedor +"\n");
        salida.append("isPoviderEnable " + managerloc.isProviderEnabled(proveedor)+"\n");
        salida.append("precision: " + accuracy[locationProvider.getAccuracy()] +"\n");
        salida.append("powerRequeriments: " + power[locationProvider.getPowerRequirement()] +"\n");
        salida.append("support altitude: " +locationProvider.supportsAltitude()+"\n");
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        managerloc.requestLocationUpdates(proveedor,seconds*1000,meters,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        managerloc.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        salida.append("\nNew Location: \n");
        mostrarLocalizacion(location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
        for(Location location : locations) {
            salida.append("\nNew Location: \n");
            mostrarLocalizacion(location);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        salida.append("Proveedor " + provider + " habilitado.");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        salida.append("Proveedor " + provider + " deshabilitado.");
    }
}