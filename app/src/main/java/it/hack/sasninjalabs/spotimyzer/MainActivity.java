package it.hack.sasninjalabs.spotimyzer;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.hack.sasninjalabs.spotimyzer.model.Slot;
import it.hack.sasninjalabs.spotimyzer.model.Spot;
import it.hack.sasninjalabs.spotimyzer.model.User;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.selected_type)
    public ImageView selectedType;

    @BindView(R.id.type_selector)
    public Button tyepSelector;

    @BindView(R.id.nav_icon)
    public Button navIcon;

    @BindView(R.id.search_bar)
    public EditText searchBar;

    @BindView(R.id.mainView)
    public ConstraintLayout mainLayout;

    private boolean close = false;

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent() != null){
            if(!getIntent().getBooleanExtra("IS_RENTER", true)){
                searchBar.setVisibility(View.GONE);
                /*FirebaseDatabase.getInstance().getReference().child("owner-spot").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                        ArrayList<Long> spots = dataSnapshot.getValue(t);
                        for (Long spot : spots){
                            FirebaseDatabase.getInstance().getReference().child("spot-slot").child(String.valueOf(spot)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                                    ArrayList<Long> slots = dataSnapshot.getValue(t);
                                    for (Long slot : slots){
                                        FirebaseDatabase.getInstance().getReference().child("slots").child(String.valueOf(slot)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Slot slot = dataSnapshot.getValue(Slot.class);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                FirebaseDatabase.getInstance().getReference().child("owner-alerts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    @Override
    public void onBackPressed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.midnight_map_style));
            this.gMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 231);
                return;
            }
            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMarkerClickListener(this);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if(!getIntent().getBooleanExtra("IS_RENTER", false)) {
                FirebaseDatabase.getInstance().getReference().child("owner-spot").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>() {
                        };
                        ArrayList<Long> spots = dataSnapshot.getValue(t);
                        for (Long spot : spots) {
                            FirebaseDatabase.getInstance().getReference().child("spots").child(String.valueOf(spot)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Spot spot = dataSnapshot.getValue(Spot.class);
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLongitude(), spot.getLatitude())).title(spot.getNickname()));
                                    marker.setTag(spot);
                                    marker.setIcon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
           /* FirebaseDatabase.getInstance().getReference("spots").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Spot spot = snapshot.getValue(Spot.class);
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongitude())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/



            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    @OnClick(R.id.type_selector)
    public void typeSelectorClicked(){
        close = true;
        tyepSelector.setVisibility(View.GONE);
        selectedType.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final TypeSelectorFragment fragment = TypeSelectorFragment.newInstance(new TypeSelectorFragment.OnTypeSelected() {
            @Override
            public void TypeSelected(TypeSelectorFragment.VehicleType type, int resID) {
                tyepSelector.setVisibility(View.VISIBLE);
                selectedType.setVisibility(View.VISIBLE);
                selectedType.setImageResource(resID);
                close = false;

            }
        });
        transaction.replace(R.id.type_selector_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(close) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    tyepSelector.setVisibility(View.VISIBLE);
                    selectedType.setVisibility(View.VISIBLE);
                    close = false;
                }
            }
        });
    }

    @OnClick(R.id.nav_icon)
    public void navIconClicked(){
        navIcon.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainView, NavigationFragment.newInstance((User) getIntent().getSerializableExtra("CURRENT_USER"), new NavigationFragment.OnNavigationSelected() {
            @Override
            public void selected(NavigationFragment.NavAction action) {
                switch (action){

                    case CLOSE:
                        navIcon.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.search_bar)
    public void searchBarCLicked(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                searchBar.setText(place.getName());
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                gMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                FirebaseDatabase.getInstance().getReference().child("spots").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Spot spot = snapshot.getValue(Spot.class);
                            Marker marker = gMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLongitude(), spot.getLatitude())).title(spot.getNickname()));
                            marker.setTag(spot);
                            marker.setIcon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            Log.d(TAG, "onDataChange: SPOT is: "+spot.getNickname() + " LAT: "+spot.getLatitude()+" LONG: "+spot.getLongitude());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "onActivityResult: cancelled");
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag() != null){
            Spot spot = (Spot) marker.getTag();
            User current = (User) getIntent().getSerializableExtra("CURRENT_USER");
            Log.d(TAG, "onMarkerClick: Spot: "+spot.getNickname()+" CUrrent: "+current.getName());
            if(getIntent().getBooleanExtra("IS_RENTER", false))
                SpotSnackBar.make((ViewGroup) findViewById(R.id.map)).setSpot(spot,current).show();
            else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View dialogView = LayoutInflater.from(this).inflate(R.layout.rental_list, null);
                builder.setView(dialogView);

                final RecyclerView recyclerView = dialogView.findViewById(R.id.slotList);
                final RentalAdapter adapter = new RentalAdapter(new ArrayList<String>(), new ArrayList<Slot>());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                FirebaseDatabase.getInstance().getReference().child("owner-spot").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                        ArrayList<Long> spots = dataSnapshot.getValue(t);
                        for (Long spot : spots){
                            FirebaseDatabase.getInstance().getReference().child("spot-slot").child(String.valueOf(spot)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                                    ArrayList<Long> slots = dataSnapshot.getValue(t);
                                    for (Long slot : slots){
                                        FirebaseDatabase.getInstance().getReference().child("slots").child(String.valueOf(slot)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final Slot slot = dataSnapshot.getValue(Slot.class);
                                                if(slot.isAvailability()){
                                                    adapter.addSlot("UNKNOWN", slot);
                                                    return;
                                                }
                                                FirebaseDatabase.getInstance().getReference().child("rentals").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                            final String key = snapshot.getKey();
                                                            String value = snapshot.getValue(String.class);
                                                            FirebaseDatabase.getInstance().getReference().child("slots").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    final Slot slotInner = dataSnapshot.getValue(Slot.class);
                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            User user = dataSnapshot.getValue(User.class);
                                                                            if(slot.getId() == slotInner.getId())
                                                                                adapter.addSlot(user.getPhotoUrl(), slotInner);
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                final AlertDialog dialog = builder.create();
                ((Button) dialogView.findViewById(R.id.reserveBtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                        dialog.show();
            }

            return true;
        }
        return false;
    }

    public class RentalAdapter extends RecyclerView.Adapter<RentalViewHolder>{

        ArrayList<String> urls;
        ArrayList<Slot> slots;

        public RentalAdapter(ArrayList<String> urls, ArrayList<Slot> slots) {
            this.urls = urls;
            this.slots = slots;
        }

        @Override
        public RentalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RentalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rented_slot_list, parent, false));
        }

        @Override
        public void onBindViewHolder(RentalViewHolder holder, int position) {
            holder.bind(urls.get(position), slots.get(position));
        }

        @Override
        public int getItemCount() {
            return slots.size();
        }

        public void addSlot(String url, Slot slot){
            urls.add(url);
            slots.add(slot);
            notifyDataSetChanged();
        }
    }

    public class RentalViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.renters_image)
        public ImageView image;

        @BindView(R.id.slotNo)
        public TextView slotNo;

        @BindView(R.id.startTime)
        public TextView startTime;

        @BindView(R.id.currentRunningTime)
        public TextView crt;

        public RentalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(String renter, Slot slot){
            if(slot.isAvailability()){
                image.setImageResource(R.drawable.ic_close);
                slotNo.setText(String.valueOf(slot.getId()));
                startTime.setText("AVAILABLE");
                crt.setText("--");
            }else{
                Picasso.get().load(renter).into(image);
                slotNo.setText(String.valueOf(slot.getId()));
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = format.format(new Date(slot.getStart()));
                startTime.setText(formattedDate);
                crt.setText("Minutes: "+(new Date().getTime() - slot.getStart())/(60 * 1000.0));
            }
        }
    }
}
