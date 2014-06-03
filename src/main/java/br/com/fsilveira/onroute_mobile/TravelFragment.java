package br.com.fsilveira.onroute_mobile;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.com.fsilveira.onroute_mobile.api.TaskTravelAPI;
import br.com.fsilveira.onroute_mobile.listener.TravelListener;
import br.com.fsilveira.onroute_mobile.model.Travel;
import br.com.fsilveira.onroute_mobile.model.Vehicle;

public class TravelFragment extends Fragment implements TravelListener {

	private static final String ARG_SECTION_NUMBER = "section_number";

	private TaskTravelAPI travelAPI;

	public static TravelFragment newInstance(int sectionNumber, Vehicle vehicle) {
		TravelFragment fragment = new TravelFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable("vehicle", vehicle);
		fragment.setArguments(args);
		return fragment;
	}

	private ListView listView;

	public TravelFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Vehicle vehicle = (Vehicle) getArguments().getSerializable("vehicle");
		travelAPI = new TaskTravelAPI(this);
		travelAPI.execute(vehicle);

		listView = (ListView) inflater.inflate(R.layout.fragment_travel_list, container, false);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Travel travel = (Travel) listView.getItemAtPosition(position);

				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.setTravel(travel);

				mainActivity.getmNavigationDrawerFragment().selectItem(NavigationDrawerFragment.MAP_SCREEN);
			}
		});

		return listView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	public void onRoutingFailure(String msg) {
		// TODO Auto-generated method stub

	}

	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

	public void onRoutingSuccess(List<Travel> travels) {

		listView.setAdapter(new TravelAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, travels));

	}

	public class TravelAdapter extends ArrayAdapter<Travel> {

		private Context context;
		private List<Travel> values;
		private LayoutInflater inflater = null;

		public TravelAdapter(Context context, int textViewResourceId, int text1, List<Travel> values) {
			super(context, textViewResourceId, values);
			this.context = context;
			this.values = values;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return values.size();
		}

		public Travel getItem(int position) {
			return values.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TextView label = new TextView(context);
			// label.setTextColor(Color.BLACK);
			// Travel travel = values.get(position);
			// label.setText(travel.getDate() + " - " + travel.getRoute().getName());
			// return label;

			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.layout.list_row_travel, null);

			TextView name = (TextView) vi.findViewById(R.id.name);
			TextView date = (TextView) vi.findViewById(R.id.date);
			ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);

			Travel travel = values.get(position);

			// Setting all values in listview
			name.setText(travel.getRoute().getName());
			date.setText(travel.getDate());
			// imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
			return vi;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView label = new TextView(context);
			label.setTextColor(Color.BLACK);
			Travel travel = values.get(position);
			label.setText(travel.getDate() + " - " + travel.getRoute().getName());

			return label;
		}
	}

}