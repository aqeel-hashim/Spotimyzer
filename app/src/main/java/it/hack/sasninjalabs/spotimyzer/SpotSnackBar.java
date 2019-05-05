package it.hack.sasninjalabs.spotimyzer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.hack.sasninjalabs.spotimyzer.model.Slot;
import it.hack.sasninjalabs.spotimyzer.model.Spot;
import it.hack.sasninjalabs.spotimyzer.model.User;

/**
 * Created by Aqeel Hashim on 24-Mar-18.
 */

public class SpotSnackBar extends BaseTransientBottomBar<SpotSnackBar> {
    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent              The parent for this transient bottom bar.
     * @param content             The content view for this transient bottom bar.
     * @param contentViewCallback The content view callback for this transient bottom bar.
     */
    protected SpotSnackBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }

    private static class ContentViewCallback implements
            BaseTransientBottomBar.ContentViewCallback {

        // view inflated from custom layout
        private View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            // add custom *in animations for your views
            // e.g. original snackbar uses alpha animation, from 0 to 1
            ViewCompat.setScaleY(content, 0f);
            ViewCompat.animate(content)
                    .scaleY(1f)
                    .translationX(0)
                    .translationY(0)
                    .setDuration(1000)
                    .setStartDelay(100);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            // add custom *out animations for your views
            // e.g. original snackbar uses alpha animation, from 1 to 0
            ViewCompat.setScaleY(content, 1f);
            ViewCompat.animate(content)
                    .scaleY(0f)
                    .translationX(0)
                    .translationY(0)
                    .setDuration(500)
                    .setStartDelay(50);
        }
    }

    public static SpotSnackBar make(ViewGroup parent) {
        // inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View content = inflater.inflate(R.layout.spot_snackbar, parent, false);

        // create snackbar with custom view
        ContentViewCallback callback= new ContentViewCallback(content);
        SpotSnackBar customSnackbar = new SpotSnackBar(parent, content, callback);

        // set snackbar duration
        customSnackbar.setDuration(-2);


        return customSnackbar;
    }

    public SpotSnackBar setSpot(final Spot spot, final User current){
        final String[] key = new String[1];
        FirebaseDatabase.getInstance().getReference().child("owner-spot").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    key[0] = snapshot.getKey();
                    GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                    ArrayList<Long> spots = snapshot.getValue(t);
                    for(Long s : spots){
                        if(spot.getId() == s){
                            FirebaseDatabase.getInstance().getReference().child("owner").child(key[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String url = dataSnapshot.child("photoUrl").getValue(String.class);
                                    Picasso.get().load(url).transform(new CircleTransform()).into(((ImageView)getView().findViewById(R.id.ownerImage)));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<Integer> resIDs = new ArrayList<Integer>(){{
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_menu_manage);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_menu_camera);
            add(R.drawable.ic_menu_gallery);
        }};

        final SpotIconAdapter adapter = new SpotIconAdapter(resIDs);
        RecyclerView view = getView().findViewById(R.id.iconSet);
        view.setLayoutManager(new GridLayoutManager(getContext(), 5));
        view.setAdapter(adapter);

        ((TextView) getView().findViewById(R.id.price)).setText(spot.getPrice()+" Rs/= per hour");

        getView().findViewById(R.id.reserveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Reserve Clicked", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.available_slots, null);
                builder.setView(dialogView);

                final RecyclerView recyclerView = dialogView.findViewById(R.id.slotList);

                final boolean[] check = {true};
                final String[] lastAvailableSlot = new String[1];
                final Slot[] lastAvailableSlotVar = new Slot[1];
                FirebaseDatabase.getInstance().getReference().child("spot-slot").child(Long.toString(spot.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                        ArrayList<Long> spots = dataSnapshot.getValue(t);
                        final ArrayList<Slot> slots = new ArrayList<>();
                        final SlotAdapter slotAdapter = new SlotAdapter(slots);
                        for(Long s : spots){
                            if(spot.getId() == Long.parseLong(key)) {
                                FirebaseDatabase.getInstance().getReference().child("slots").child(Long.toString(s)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Slot slot = dataSnapshot.getValue(Slot.class);
                                        if(slot != null) {
                                            if (check[0] && slot.isAvailability()) {
                                                lastAvailableSlot[0] = Long.toString(slot.getId());
                                                check[0] = false;
                                                lastAvailableSlotVar[0] = slot;
                                            }
                                            slotAdapter.addSlot(slot);
                                        }else{
                                            Toast.makeText(getContext(), "Slots Unavailable at the moment", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(slotAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                check[0] = true;
                final AlertDialog dialog = builder.create();
                ((Button) dialogView.findViewById(R.id.reserveBtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dismiss();
                        FirebaseDatabase.getInstance().getReference().child("spot-slot").child(Long.toString(spot.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String keySpot = dataSnapshot.getKey();
                                GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                                ArrayList<Long> spots = dataSnapshot.getValue(t);
                                final ArrayList<Slot> slots = new ArrayList<>();
                                final SlotAdapter slotAdapter = new SlotAdapter(slots);
                                final int[] end = {-1};
                                for(Long s : spots){
                                    if(spot.getId() == Long.parseLong(keySpot)) {
                                        FirebaseDatabase.getInstance().getReference().child("slots").child(Long.toString(s)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Slot slot = dataSnapshot.getValue(Slot.class);
                                                if(slot != null) {
                                                    if (slot.isAvailability()) {

                                                    end[0] = 0;

                                                    slot.setAvailability(false);
                                                    slot.setStart(new Date().getTime());
                                                    slot.setEnd(0);
                                                    slot.setArrived(false);
                                                    FirebaseDatabase.getInstance().getReference().child("rentals").child(current.getUUID()).setValue(String.valueOf(slot.getId()));
                                                    FirebaseDatabase.getInstance().getReference().child("slots").child(String.valueOf(slot.getId())).setValue(slot);
                                                    FirebaseDatabase.getInstance().getReference().child("slots").child(String.valueOf(slot.getId())).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    final Slot slot = dataSnapshot.getValue(Slot.class);
                                                                    if(slot.isArrived()){
                                                                        new AlertDialog.Builder(getContext()).setTitle("A Vehicle Has Arrived").setMessage("Did You Arrive At Your Parking Slot?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();

                                                                            }
                                                                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();
                                                                                FirebaseDatabase.getInstance().getReference().child("owner-spot").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                                                            String key = snapshot.getKey();
                                                                                            GenericTypeIndicator<ArrayList<Long>> t = new GenericTypeIndicator<ArrayList<Long>>(){};
                                                                                            ArrayList<Long> spots = snapshot.getValue(t);
                                                                                            for(Long s : spots){
                                                                                                if(spot.getId() == s){
                                                                                                    FirebaseDatabase.getInstance().getReference().child("owner-alerts").child(key).child(Long.toString(spot.getId()))
                                                                                                            .child(Long.toString(slot.getId())).setValue("Someone besides the renter has arrived at the parking spot");
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                            }
                                                                        }).create().show();
                                                                    }

                                                                    if(slot.isAvailability()){

                                                                        Date dateStart = new Date(slot.getStart());
                                                                        Date dateEnd = new Date(slot.getEnd());

                                                                        double hours = ((dateEnd.getTime() - dateStart.getTime()) / (60 * 1000));
                                                                        double charge = hours * spot.getPrice();
                                                                        if(key[0] != null && key[0].length() > 0)
                                                                            FirebaseDatabase.getInstance().getReference().child("owner-alerts").child(key[0]).child(Long.toString(spot.getId()))
                                                                                    .child(Long.toString(slot.getId())).setValue(current.getName()+" has completed his parking duration with a total charge of "+charge+" Rs/= and a total time of "+hours+" minutes");


                                                                        new AlertDialog.Builder(getContext()).setTitle("A Vehicle Has Arrived").setMessage("You have successfully completed the booking. Your Time: "+hours+" minutes, Your Charge: "+charge+" Rs/=. Thank You For Using SPOTYMIZER!").setPositiveButton("YOUR WELCOME", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();

                                                                            }
                                                                        }).create().show();


                                                                        slot.setArrived(false);
                                                                        slot.setEnd(0);
                                                                        slot.setStart(0);
                                                                        slot.setAvailability(true);

                                                                        FirebaseDatabase.getInstance().getReference().child("slots").child(String.valueOf(slot.getId())).setValue(slot);
                                                                        FirebaseDatabase.getInstance().getReference().child("slots").child(lastAvailableSlot[0]).removeEventListener(this);

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                    }
                                                }else{
                                                    Toast.makeText(getContext(), "Slots Unavailable at the moment", Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    if(end[0] > 0 )
                                        break;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                dialog.show();

            }
        });


        return this;
    }

    public class SpotIconAdapter extends RecyclerView.Adapter<SpotIconViewHolder>{

        private ArrayList<Integer> resIDs;

        public SpotIconAdapter(ArrayList<Integer> resIDs) {
            this.resIDs = resIDs;
        }

        @Override
        public SpotIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SpotIconViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_icon_itm, parent, false));
        }

        @Override
        public void onBindViewHolder(SpotIconViewHolder holder, int position) {
            holder.bind(resIDs.get(position));
        }

        @Override
        public int getItemCount() {
            return resIDs.size();
        }
    }

    public class SlotAdapter extends RecyclerView.Adapter<SlotViewHolder>{

        private ArrayList<Slot> slots;

        public SlotAdapter(ArrayList<Slot> slots) {
            this.slots = slots;
        }

        @Override
        public SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SlotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_item, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(SlotViewHolder holder, int position) {
            holder.bind(slots.get(position));
        }

        @Override
        public int getItemCount() {
            return slots.size();
        }

        public void addSlot(Slot slot){
            if(!slots.contains(slot)) {
                slots.add(slot);
                notifyDataSetChanged();
            }
        }
    }

    public class SpotIconViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.icon)
        ImageView icon;

        public SpotIconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int resID){
            Picasso.get().load(resID).into(icon);
        }
    }


    public class SlotViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.slotNo)
        public TextView slotNo;

        @BindView(R.id.availability)
        public TextView availibility;

        public SlotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void bind(Slot slot){
            this.slotNo.setText(Long.toString(slot.getId()));
            if(slot.isAvailability()){
                this.availibility.setBackgroundColor(getContext().getColor(android.R.color.holo_green_dark));
                this.availibility.setText("AVAILABLE");
            }else{
                this.availibility.setBackgroundColor(getContext().getColor(android.R.color.holo_red_dark));
                this.availibility.setText("UNAVAILABLE");
            }
        }
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
