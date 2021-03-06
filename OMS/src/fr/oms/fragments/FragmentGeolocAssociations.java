package fr.oms.fragments;

import java.util.ArrayList;
import java.util.List;

import fr.oms.activities.FragmentAssociationActivity;
import fr.oms.activities.R;
import fr.oms.adapter.AssociationGeolocAdapter;
import fr.oms.metier.Association;
import fr.oms.modele.Manager;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentGeolocAssociations extends Fragment implements LocationListener  {

	private List<Association> associationTriesLocalisation;
	private ListView listAssoc;
	private double latitudeUser = 0;
	private double longitudeUser = 0;
	private LocationManager lm;
	private ProgressBar progessBar;

	@Override
	public void onResume() {
		super.onResume();
		lm = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,this);
	}

	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_geoloc_associations, container,false);
		progessBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		associationTriesLocalisation = new ArrayList<Association>();
		listAssoc = (ListView)v.findViewById(R.id.listeGeolocAssociation);
		onTouchItem();
		return v;
	}

	private void onTouchItem(){
		listAssoc.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Association a = associationTriesLocalisation.get(position);
				Intent intent = new Intent(FragmentGeolocAssociations.this.getActivity(), FragmentAssociationActivity.class);
				intent.putExtra("position", a.getId());
				intent.putExtra("adherents", true);
				intent.putExtra("nonAdherents", false);
				intent.putExtra("sport", false);
				intent.putExtra("nomSport", "");
				startActivity(intent);
			}
		});
	}

	private void donneListe(){
		List<Association> listeTemporaire = new ArrayList<Association>();
		for(Association a : rendNouvelleListe()){
			if(a.getListeEquipement() != null){
				listeTemporaire.add(a);
			}
		}
		associationTriesLocalisation.clear();
		for(int i = 0; i < listeTemporaire.size(); i++){
			Association valeurTest = listeTemporaire.get(0);
			for(Association a : listeTemporaire){
				if(donneDistance(a) <= donneDistance(valeurTest)){
					valeurTest = a;
				}
			}
			associationTriesLocalisation.add(valeurTest);
			listeTemporaire.remove(valeurTest);
		}
	}

	private double donneDistance(Association a){
		Location locUser = new Location("Point A");
		locUser.setLatitude(latitudeUser);
		locUser.setLongitude(longitudeUser);

		Location loc = new Location("Point B");
		loc.setLatitude(Double.parseDouble(a.getListeEquipement().get(0).getGeoloc().getLatitude()));
		loc.setLongitude(Double.parseDouble(a.getListeEquipement().get(0).getGeoloc().getLongitude()));

		return locUser.distanceTo(loc);
	}

	private List<Association> rendNouvelleListe(){
		List<Association> assocs = new ArrayList<Association>();
		for(Association a : Manager.getInstance().getListeAssociation()){
			if(a.isAdherent()){
				assocs.add(a);
			}
		}
		return assocs;
	}

	@Override
	public void onLocationChanged(Location location) {
		listAssoc.setVisibility(View.GONE);
		progessBar.setVisibility(View.VISIBLE);
		latitudeUser = location.getLatitude();
		longitudeUser = location.getLongitude();
		donneListe();
		AssociationGeolocAdapter adapterAssoc = new AssociationGeolocAdapter(getActivity(), 0, associationTriesLocalisation, latitudeUser, longitudeUser);
		listAssoc.setAdapter(adapterAssoc);
		progessBar.setVisibility(View.GONE);
		listAssoc.setVisibility(View.VISIBLE);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
}
