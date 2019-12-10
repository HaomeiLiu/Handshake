package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;

import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_KEY;
import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_LOCATION;
import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_TIME;
import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment.EXTRA_EVENT;

public class SearchResultActivity extends AppCompatActivity {
    private List<Event> events_holder;
    private EventListAdapter eventListAdapter;
    public ListView listViewEvent;
    private static final String TAG = "SearchResultActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setTitle("Search Result");


        listViewEvent = findViewById(R.id.hsearch_result_listView);
        listViewEvent.setAdapter(eventListAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            refresh(intent.getStringExtra(EXTRA_SEARCH_LOCATION),
                    intent.getStringExtra(EXTRA_SEARCH_TIME),
                    intent.getStringExtra(EXTRA_SEARCH_KEY));
        }




    }

    private class EventListAdapter extends ArrayAdapter<Event> {
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
                    Intent intent = new Intent(SearchResultActivity.this, EventActivity.class);
                    intent.putExtra(EXTRA_EVENT, event);
                    startActivity(intent);
                }
            });
            textViewTitle.setText(event.getTitle());
            textViewTime.setText(event.getTime());
            textViewLocation.setText(event.getLocation());
            imageViewEvent.setImageResource(R.drawable.blank_group_icon);
            return convertView;
        }
    }
    private void refresh(final String location, final String time, final String key){
        events_holder = new ArrayList<>();
        eventListAdapter = new EventListAdapter(SearchResultActivity.this, events_holder);

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
                                if(location.equals("") && time.equals("") && key.equals("")){
                                    events_holder.add(event);
                                }
                                else if(location.equals(event.getLocation()) && time.equals("") && key.equals("")){
                                    events_holder.add(event);
                                }
                                else if(time.equals(event.getTime()) && location.equals("") && key.equals("")){
                                    events_holder.add(event);
                                }
                                else if(key.equals(event.getKey()) && time.equals("") && key.equals("")){
                                    events_holder.add(event);
                                }
                                else if(location.equals(event.getLocation()) && time.equals(event.getTime())){
                                    events_holder.add(event);
                                }
                                else if(location.equals(event.getLocation()) && key.equals(event.getKey())){
                                    events_holder.add(event);
                                }
                                else if(time.equals(event.getTime()) && key.equals(event.getKey())){
                                    events_holder.add(event);
                                }
                                else if(time.equals(event.getTime()) &&
                                        key.equals(event.getKey()) &&
                                        location.equals(event.getLocation())){
                                    events_holder.add(event);
                                }

                                }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        listViewEvent.setAdapter(eventListAdapter);
                    }
                });
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
