package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import itp341.liu.haomei.finalprojecthaomeiliu.R;

import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_KEY;
import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_LOCATION;
import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity.EXTRA_SEARCH_TIME;

public class SearchFragment extends Fragment {
    EditText editTextTime;
    EditText editTextLocation;
    EditText editTextKey;
    FloatingActionButton button;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        editTextTime = view.findViewById(R.id.search_editText_time);
        editTextLocation = view.findViewById(R.id.search_editText_location);
        editTextKey = view.findViewById(R.id.search_editText_key);
        button = view.findViewById(R.id.search_floating_action_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTextTime.getText().toString();
                String location = editTextLocation.getText().toString();
                String key = editTextKey.getText().toString();
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra(EXTRA_SEARCH_TIME, time);
                intent.putExtra(EXTRA_SEARCH_LOCATION, location);
                intent.putExtra(EXTRA_SEARCH_KEY, key);
                startActivity(intent);

            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
