package kr.co.company.locationtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by CJHM on 2016-08-25.
 */
public class MapActivity extends Activity implements OnMapReadyCallback {
    private LatLng latLng;
    private GoogleMap googleMap;
    private Intent intent;
    LatLng latLug2;


    @Override
    public void onMapReady(final GoogleMap map) {
        GpsInfo gpsInfo = new GpsInfo(this);

        googleMap = map;
        Location location = gpsInfo.getLocation();
        //Toast.makeText(this,"현재 : " +location.getLatitude()+"/"+location.getLongitude(),Toast.LENGTH_SHORT).show();
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        Marker seoul = googleMap.addMarker(new MarkerOptions().position(latLng)
                .title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    CircleOptions circleOptions = new CircleOptions();
    //유모차 위치
    circleOptions.center(latLug2);
    circleOptions.radius(2);
    circleOptions.fillColor(Color.BLUE);
    circleOptions.strokeColor(Color.BLUE);
    googleMap.addCircle(circleOptions);
    //핸드폰 위치
    circleOptions.center(latLng);
    circleOptions.radius(1);
    circleOptions.fillColor(Color.RED);
    circleOptions.strokeColor(Color.RED);
    googleMap.addCircle(circleOptions);

}


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_alram);
        intent = getIntent();
//       intent.getExtras().getDouble("strollLat");
        latLug2 = new LatLng( intent.getExtras().getDouble("strollLat"), intent.getExtras().getDouble("strollLong"));
//    Toast.makeText(this, intent.getStringExtra("strollLat")+"",Toast.LENGTH_SHORT).show();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

}
