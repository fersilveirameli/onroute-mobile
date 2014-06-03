package br.com.fsilveira.onroute_mobile;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.com.fsilveira.onroute_mobile.api.TaskVechicleAPI;
import br.com.fsilveira.onroute_mobile.listener.VehicleListener;
import br.com.fsilveira.onroute_mobile.model.Vehicle;

public class VehicleActivity extends ListActivity implements VehicleListener {

	private TaskVechicleAPI vechicleAPI;
	private VehicleAdapter dataAdapter;
	protected PowerManager.WakeLock mWakeLock;

	@SuppressLint("Wakelock")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		this.mWakeLock.acquire();

		vechicleAPI = new TaskVechicleAPI(this);
		vechicleAPI.execute();

	}

	public void onRoutingFailure(String msg) {
		// TODO Auto-generated method stub

	}

	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

	public void onRoutingSuccess(List<Vehicle> vehicles) {

		dataAdapter = new VehicleAdapter(this, android.R.layout.simple_list_item_2, vehicles);
		setListAdapter(dataAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Vehicle vehicle = (Vehicle) dataAdapter.getItem(position);

		Intent myIntent = new Intent(v.getContext(), MainActivity.class);
		myIntent.putExtra("vehicle", vehicle);
		startActivity(myIntent);
	}

	public class VehicleAdapter extends ArrayAdapter<Vehicle> {

		// Your sent context
		private Context context;
		// Your custom values for the spinner (User)
		private List<Vehicle> values;
		private LayoutInflater inflater = null;

		public VehicleAdapter(Context context, int textViewResourceId, List<Vehicle> values) {
			super(context, textViewResourceId, values);
			this.context = context;
			this.values = values;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return values.size();
		}

		public Vehicle getItem(int position) {
			return values.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		// And the "magic" goes here
		// This is for the "passive" state of the spinner
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.layout.list_row_vehicle, null);

			TextView prefix = (TextView) vi.findViewById(R.id.prefix);
			TextView mark = (TextView) vi.findViewById(R.id.mark);
			TextView type = (TextView) vi.findViewById(R.id.type);

			Vehicle vehicle = values.get(position);

			prefix.setText(vehicle.getPrefix());
			mark.setText(vehicle.getMark());
			type.setText("(" + vehicle.getType() + ")");
			return vi;
		}

		// And here is when the "chooser" is popped up
		// Normally is the same view, but you can customize it if you want
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView label = new TextView(context);
			label.setTextColor(Color.BLACK);
			Vehicle vehicle = values.get(position);
			label.setText(vehicle.getPrefix() + " - " + vehicle.getType() + "/" + vehicle.getMark());

			return label;
		}
	}

}
