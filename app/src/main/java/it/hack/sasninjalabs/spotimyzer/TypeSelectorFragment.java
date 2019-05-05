package it.hack.sasninjalabs.spotimyzer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TypeSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TypeSelectorFragment extends Fragment {

    public static final String ARG_TYPE_SELECTED_LISTNER = "TYPE_HAS_BEEN_SELECTED_LISTNER";

    public enum VehicleType{
        BIKE,
        TUKTUK,
        MINI,
        CAR,
        VAN,
        DIMO,
        BUS,
        LUXURY
    }

    @BindView(R.id.typeList)
    public RecyclerView typeList;

    public OnTypeSelected listner;

    public TypeSelectorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TypeSelectorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TypeSelectorFragment newInstance(OnTypeSelected typeSelected) {
        TypeSelectorFragment fragment = new TypeSelectorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE_SELECTED_LISTNER, typeSelected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.listner = (OnTypeSelected) getArguments().getSerializable(ARG_TYPE_SELECTED_LISTNER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_type_selector, container, false);
        ButterKnife.bind(this, view);

        ArrayList<Integer> resIDs = new ArrayList<Integer>(){{
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_menu_manage);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_rickshaw);
            add(R.drawable.ic_menu_camera);
            add(R.drawable.ic_menu_gallery);
        }};

        TypeAdapter adapter = new TypeAdapter(resIDs);
        typeList.setLayoutManager(new LinearLayoutManager(getContext()));
        typeList.setAdapter(adapter);

        return view;
    }

    public class TypeAdapter extends RecyclerView.Adapter<TypeItemViewHolder>{

        public ArrayList<Integer> resIDs;

        public TypeAdapter(ArrayList<Integer> resIDs) {
            this.resIDs = resIDs;
        }

        @Override
        public TypeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TypeItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.type_item, parent, false));
        }

        @Override
        public void onBindViewHolder(TypeItemViewHolder holder, int position) {
            holder.bind(VehicleType.values()[position].name(), resIDs.get(position));
        }

        @Override
        public int getItemCount() {
            return VehicleType.values().length - 1;
        }
    }

    public class TypeItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.nav_item_icon)
        public ImageView icon;

        @BindView(R.id.nav_item_label)
        public TextView label;

        public TypeItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(final String label, final int resIcon){
            icon.setImageResource(resIcon);
            this.label.setText(label);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.TypeSelected(VehicleType.valueOf(label), resIcon);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(TypeSelectorFragment.this).commit();
                }
            });
        }
    }

    public interface OnTypeSelected extends Serializable{
        void TypeSelected(VehicleType type, int resID);
    }

}
