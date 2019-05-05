package it.hack.sasninjalabs.spotimyzer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.hack.sasninjalabs.spotimyzer.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_CURRENT_USER = "USER_OBJECT";
    public static final String ARG_NAV_SELECT_LISTNER = "NAV_SELECTED_INTERFACE_LISTNER";

    // TODO: Rename and change types of parameters
    private User user;

    @BindView(R.id.nav_list)
    public RecyclerView navList;

    @BindView(R.id.username)
    public TextView username;

    @BindView(R.id.phoneNumber)
    public TextView phoneNumber;

    private OnNavigationSelected listner;

    public enum NavAction{
        CLOSE
    }

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Current User of App.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(User user, OnNavigationSelected selected) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CURRENT_USER, user);
        args.putSerializable(ARG_NAV_SELECT_LISTNER, selected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_CURRENT_USER);
            listner = (OnNavigationSelected) getArguments().getSerializable(ARG_NAV_SELECT_LISTNER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        ButterKnife.bind(this, view);

        ArrayList<String> labels = new ArrayList<String>(){{
            add("Profile");
            add("Parking History");
            add("Notification");
            add("Payment");
            add("Emergency");
            add("About Us");
            add("Support");
        }};

        ArrayList<Integer> resIDs = new ArrayList<Integer>(){{
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
            add(R.drawable.ic_rickshaw_white);
        }};

        NavAdapter adapter = new NavAdapter(labels, resIDs);
        navList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        navList.setAdapter(adapter);

        username.setText(user.getName());
        phoneNumber.setText(user.getPhoneNumber());

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @OnClick(R.id.closeBtn)
    public void closeNav(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        listner.selected(NavAction.CLOSE);
    }


    public class NavAdapter extends RecyclerView.Adapter<NavItemViewHolder>{

        public ArrayList<String> labels;

        public ArrayList<Integer> resIDs;

        public NavAdapter(ArrayList<String> labels, ArrayList<Integer> resIDs) {
            this.labels = labels;
            this.resIDs = resIDs;
        }

        @Override
        public NavItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NavItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent, false));
        }

        @Override
        public void onBindViewHolder(NavItemViewHolder holder, int position) {
            holder.bind(labels.get(position), resIDs.get(position));
        }

        @Override
        public int getItemCount() {
            return labels.size();
        }
    }

    public class NavItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.nav_item_icon)
        public ImageView icon;

        @BindView(R.id.nav_item_label)
        public TextView label;

        public NavItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String label, int resIcon){
            icon.setImageResource(resIcon);
            this.label.setText(label);
        }
    }

    public interface OnNavigationSelected extends Serializable{
        void selected(NavAction action);
    }
}
