package uber.example.uber.parent;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentMap extends FragmentActivity implements OnMapReadyCallback , com.google.android.gms.location.LocationListener  ,RoutingListener
{
    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    MediaPlayer bus_start ;
    MediaPlayer com_not ;
    Marker ali ;
    LatLng schoolLatLang;
    LatLng homeLatLang;
    String id ;
    List<LatLng> points ;
    Marker bus ;
    Location busloc;
    Location home;
    Button btn;
    Boolean flag = false ;
    String child ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        id= intent.getStringExtra("id");
        polylines = new ArrayList<>();
        points = new ArrayList<>();
        btn = (Button) findViewById(R.id.btnbtn);
        bus_start = MediaPlayer.create(this , R.raw.bus_start);
        busloc = new Location("bus");
        com_not = MediaPlayer.create(this , R.raw.cararr);
        RequestQueue nnnrequestQueue =Volley.newRequestQueue(this);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://bus.smartapp-eg.com/api/bus/getChildsOfParent?parentId="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject =jsonArray.getJSONObject(0);
                    child = jsonObject.get("name").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        nnnrequestQueue.add(stringRequest);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        schoolLatLang = new LatLng(29.8080425,39.8548355);
        bus = mMap.addMarker(new MarkerOptions().position(schoolLatLang).title("OurBus").icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus)));
        mMap.addMarker(new MarkerOptions().position(schoolLatLang).title("OurSchool")).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.school));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(schoolLatLang));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        points.add(schoolLatLang);
        makeRequest();
        DatabaseReference startn = FirebaseDatabase.getInstance().getReference("SchoolBus").child("Buses").child("Bus1").child("Status");
        startn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.getValue().toString().equals("true"))
                {
                    bus_start.start();
                    driverProprties();

                    DatabaseReference startn = FirebaseDatabase.getInstance().getReference("SchoolBus").child("Buses").child("Bus1").child("Location");
                    startn.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            String str = dataSnapshot.getValue().toString();

                            String [] point = new String[2];
                            point =str.split(",");
                            LatLng latLngla = new LatLng(Double.valueOf(point[0]),Double.valueOf(point[1]));

                           busloc.setLongitude(latLngla.longitude);
                           busloc.setLatitude(latLngla.latitude);
                           float dis = busloc.distanceTo(home);
                           if(dis<500)
                           {
                               if(!flag)
                               {
                                    btn.setClickable(true);
                                    btn.setVisibility(View.VISIBLE);
                                    com_not.start();
                                    flag = false ;
                               }
                           }

                            bus.setPosition(latLngla);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngla));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                        }
                    });

                }

                if(dataSnapshot.getValue().toString().equals("false"))
                {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(schoolLatLang).title("OurSchool")).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.school));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRoutingFailure(RouteException e)
    {
        driverProprties();
    }

    @Override
    public void onRoutingStart()
    {

    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++)
        {
            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.RED);
            polyOptions.width(4);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

    }

    @Override
    public void onRoutingCancelled()
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {

    }

    private void makeRequest ()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String link = "http://bus.smartapp-eg.com/api/bus/get_parent_route?parentId="+id;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = id;
                    homeLatLang = new LatLng(Double.valueOf(jsonObject.get("home_lat").toString()),Double.valueOf(jsonObject.get("home_lang").toString()));
                    points.add(homeLatLang);
                    home = new Location("home");
                    home.setLatitude(homeLatLang.latitude);
                    home.setLongitude(homeLatLang.longitude);
                    mMap.addMarker(new MarkerOptions().position(homeLatLang).title(child)).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.home));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext() , error.toString() , Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(stringRequest);
    }


    private void driverProprties ()
    {
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(points).build();
        routing.execute();

    }


    public void sonAtHome(View view)
    {

        Toast.makeText(getApplicationContext() ,"Thank You" , Toast.LENGTH_LONG).show();
        DatabaseReference sonHomeRef = FirebaseDatabase.getInstance().getReference().child("SchoolBus").child("Children").child(child);
        sonHomeRef.setValue("false");
        mMap.clear();
        this.finish();
    }
}
