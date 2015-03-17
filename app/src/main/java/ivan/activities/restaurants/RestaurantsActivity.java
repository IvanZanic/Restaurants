package ivan.activities.restaurants;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ivan.database.DBHelper;
import ivan.rest.domain.Restaurant;
import ivan.rest.services.RestService;


public class RestaurantsActivity extends ActionBarActivity {

    private List<Restaurant> restaurantList;
    private DBHelper db;
    private GoogleMap map;
    private Marker lastOpened = null;
    private Map<Marker,Integer> restaurantHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        File database=getApplicationContext().getDatabasePath("restaurants.db");
        db = new DBHelper(this);

        // ako baza ne postoji, potrebno je dohvatiti podatke s REST servisa
        if (!database.exists()) {
            new HttpRequestTask().execute();
        } else {
            // ako baza postoji ali u tablici Restaurants ne postoji niti jedan zapis, potrebno je dohvatiti podatke s REST servisa
            if (db.numberOfRows() == 0) {
                new HttpRequestTask().execute();
            } else {
                showMap();
            }
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                if (lastOpened != null) {
                    lastOpened.hideInfoWindow();

                    if (lastOpened.equals(marker)) {
                        lastOpened = null;
                        return true;
                    }
                }
                marker.showInfoWindow();
                lastOpened = marker;
                return true;
            }
        });

        map.setInfoWindowAdapter(new CustomInfoWindow(RestaurantsActivity.this));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                System.console();
            }
        });
    }

    public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

        private final Context context;

        public CustomInfoWindow (Context context) {
            this.context = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            Restaurant restaurant = db.getRestaurant(restaurantHashMap.get(marker));

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View window = inflater.inflate(R.layout.custom_info_window, null);
            TextView name = (TextView) window.findViewById(R.id.name);
            TextView address = (TextView) window.findViewById(R.id.address);
            ImageView photo = (ImageView) window.findViewById(R.id.photo);

            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            int id;
            try {
                id = getResources().getIdentifier("google_maps_logo", "drawable", getPackageName());
                photo.setImageResource(id);
//                photo.setEnabled(true);
            } catch (NullPointerException ex) {
                photo.setEnabled(false);
//                photo.invalidate();
            }

            LatLng geoPoint = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(geoPoint)
                                .zoom(map.getCameraPosition().zoom)
                                .build());
            map.animateCamera(camUpd, null);
            return window;
        }
    }

    private void showMap() {

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.restaurantMap))
                .getMap();

        restaurantHashMap = new HashMap<>();
        restaurantList = db.getAllRestaurants();
        Marker marker;
        for (Restaurant restaurant : restaurantList) {
            LatLng geoPoint = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            marker = map.addMarker(new MarkerOptions().position(geoPoint));
            restaurantHashMap.put(marker, restaurant.getId());
        }
        LatLng geoPoint = new LatLng(restaurantList.get(0).getLatitude(), restaurantList.get(0).getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(geoPoint, 40));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {

        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Void... params) {
            try {

                String url = "http://www.mocky.io/v2/54ef80f5a11ac4d607752717";
                RestService rs = new RestService();
                restaurantList = rs.getList(url, Restaurant[].class);
                restaurantList.get(0);

            } catch (Exception e) {
                Log.e("RestaurantsActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            DBHelper db = new DBHelper(RestaurantsActivity.this);
            db.insertListOfRestaurants(restaurantList);

            showMap();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurants, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.addNew) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
