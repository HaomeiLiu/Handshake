package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;


public class HomeFragment extends Fragment {
    public ListView listViewEvent;
    public FloatingActionButton buttonAdd;
    private static final String TAG = "HomeFragment";
    public static final String EXTRA_EVENT = HomeActivity.class.getPackage().getName() + "EventObject";
    private static final int REQUEST_CODE_CREATE = 128;


    private List<Event> events_holder;
    private EventListAdapter eventListAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listViewEvent = view.findViewById(R.id.home_listView);
        listViewEvent.setAdapter(eventListAdapter);
        buttonAdd = view.findViewById(R.id.home_fab);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Home FAB clicked");
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE);
            }
        });

        /*DocumentReference docRef = db.collection("events").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                events_holder.add(event);
                eventListAdapter = new EventListAdapter(getActivity(), events_holder);
                listViewEvent.setAdapter(eventListAdapter);            }
        });*/
        refresh();

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);

        refresh();
    }

    private class EventListAdapter extends ArrayAdapter<Event>{
        private List<Event> events;
        public EventListAdapter(Context context, List<Event> events){
            super(context, R.layout.layout_list_event, events);
            this.events = events;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_event, parent, false);
            }
            ImageView imageViewEvent = convertView.findViewById(R.id.listView_event_image);
            TextView textViewTitle = convertView.findViewById(R.id.listView_event_title);
            TextView textViewTime = convertView.findViewById(R.id.listView_event_time);
            TextView textViewLocation = convertView.findViewById(R.id.listView_event_location);
            Button buttonDetail = convertView.findViewById(R.id.listView_event_button_detail);
            buttonDetail.setTag(position);

            final Event event = events.get(position);
            buttonDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtra(EXTRA_EVENT, event);
                    startActivity(intent);
                }
            });
            textViewTitle.setText(event.getTitle());
            Log.d(TAG, "title: "+event.getTitle());
            textViewTime.setText(event.getTime());
            textViewLocation.setText(event.getLocation());
            imageViewEvent.setImageResource(R.drawable.blank_group_icon);
            return convertView;
        }
    }

    public void refresh(){
        events_holder = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getActivity(), events_holder);

        //Firebase to load events
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Event event = document.toObject(Event.class);
                                event.setId(UUID.nameUUIDFromBytes(document.getId().getBytes()).getMostSignificantBits());
                                events_holder.add(event);
                                listViewEvent.setAdapter(eventListAdapter);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        eventListAdapter.notifyDataSetChanged();
    }
}
