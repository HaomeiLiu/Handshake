package itp341.liu.haomei.finalprojecthaomeiliu.activity;

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
import android.widget.TextView;

import java.util.List;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;

import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment.EXTRA_EVENT;

public class SearchResultActivity extends AppCompatActivity {
    private List<Event> events_holder;
    private EventListAdapter eventListAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


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
}
