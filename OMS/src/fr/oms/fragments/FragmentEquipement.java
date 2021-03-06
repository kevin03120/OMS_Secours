package fr.oms.fragments;

import java.util.ArrayList;
import java.util.List;

import fr.oms.activities.FragmentAssociationActivity;
import fr.oms.activities.MapPane;
import fr.oms.activities.R;
import fr.oms.adapter.AssociationAdapter;
import fr.oms.metier.Association;
import fr.oms.metier.Equipement;
import fr.oms.modele.Manager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentEquipement extends Fragment {

	private static final String GEOLOCNULL = "0.00000";
	private Equipement equipement;
	private ListView listAssociation;
	private TextView txtPasAssoc;
	private List<Association> lesAssocsEquipement;

	public static FragmentEquipement newInstance(Equipement e) {
		Bundle extras = new Bundle();
		extras.putInt("id", e.getUid());
		FragmentEquipement fragment = new FragmentEquipement();
		fragment.setArguments(extras);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		for(Equipement e : Manager.getInstance().getListeEquipement()){
			if(e.getUid() == getArguments().getInt("id")){
				equipement = e;
			}
		}
		if(compteListAssociation() != 0){
			v = inflater.inflate(R.layout.detail_equipement, container, false);
			View header = getLayoutInflater(getArguments()).inflate(R.layout.header_equipement, null);
			recupererToutesViews(header);
			txtPasAssoc.setVisibility(View.GONE);
			listAssociation = (ListView)v.findViewById(R.id.listAssocEquipement);
			listAssociation.addHeaderView(header);
			adapterPourListAssociation();
			clicItemListAssoc();
			getActivity().setTitle(getResources().getString(R.string.titreDetailEquipement));
		}
		else{
			v = inflater.inflate(R.layout.header_equipement, container, false);
			recupererToutesViews(v);
		}
		return v;
	}

	private void adapterPourListAssociation(){
		lesAssocsEquipement = new ArrayList<Association>();
		for(Association a : Manager.getInstance().getListeAssociation()){
			if(a.getListeEquipement()!=null){
				for(Equipement e : a.getListeEquipement()){
					if(e.equals(equipement)){
						lesAssocsEquipement.add(a);
					}
				}
				AssociationAdapter adapterAssoc = new AssociationAdapter(getActivity(), 0, lesAssocsEquipement);
				listAssociation.setAdapter(adapterAssoc);
				listAssociation.setVisibility(0);
			}
		}
	}

	private int compteListAssociation(){
		lesAssocsEquipement = new ArrayList<Association>();
		for(Association a : Manager.getInstance().getListeAssociation()){
			if(a.getListeEquipement()!=null){
				for(Equipement e : a.getListeEquipement()){
					if(e.equals(equipement)){
						lesAssocsEquipement.add(a);
					}
				}
			}
		}
		return lesAssocsEquipement.size();
	}

	private void clicItemListAssoc(){
		listAssociation.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Association a = lesAssocsEquipement.get(position-1);
				Intent intent = new Intent(getActivity(), FragmentAssociationActivity.class);
				intent.putExtra("position", a.getId());
				intent.putExtra("adherents", true);
				intent.putExtra("nonAdherents", true);
				intent.putExtra("sport", false);
				intent.putExtra("idSport", 0);
				startActivity(intent);
			}
		});
	}

	private void recupererToutesViews(View v){
		TextView txtTitreEquipement = (TextView)v.findViewById(R.id.nomEquipement);
		txtTitreEquipement.setText(equipement.getNom());
		TextView txtTel = (TextView)v.findViewById(R.id.telFixEquipement);
		if(!equipement.getTelephone().equals("")){
			txtTel.setText(equipement.getTelephone());
		}
		else{
			//txtTel.setText("Aucun numero de telephone affili�");
			txtTel.setVisibility(4);
		}
		TextView txtAdresse = (TextView)v.findViewById(R.id.adresse);
		if(!equipement.getAdresse().equals("")){
			txtAdresse.setText("Adresse : " + equipement.getAdresse());
		}
		else{
			//txtAdresse.setText("Aucune adresse affili�e");
			txtAdresse.setVisibility(4);
		}
		TextView txtCodePostal = (TextView)v.findViewById(R.id.codePostal);
		if(!equipement.getCodePostal().equals("")){
			txtCodePostal.setText("Code Postal : " + equipement.getCodePostal());
		}
		else{
			//txtCodePostal.setText("Aucun code postal affili�");
			txtCodePostal.setVisibility(4);
		}
		TextView txtVille = (TextView)v.findViewById(R.id.ville);
		if(!equipement.getVille().equals("")){
			txtVille.setText("Ville : " + equipement.getVille());
		}
		else{
			//txtVille.setText("Aucune ville affili�e");
			txtVille.setVisibility(4);
		}
		TextView txtQuartier = (TextView)v.findViewById(R.id.quartier);
		if(equipement.getQuartier() != null){
			txtQuartier.setText("Quartier : " + equipement.getQuartier().getNom());
		}
		else{
			//txtQuartier.setText("Aucun quartier affili� � cet �quipement");
			txtQuartier.setVisibility(4);
		}
		Button btnGoMap = (Button)v.findViewById(R.id.btn_map);
		if((equipement.getGeoloc().getLatitude().equals(GEOLOCNULL))&&(equipement.getGeoloc().getLatitude().equals(GEOLOCNULL))){
			btnGoMap.setVisibility(4);
		}
		txtPasAssoc = (TextView)v.findViewById(R.id.txtPasAssoc);
		goMap(btnGoMap);
	}

	private void goMap(Button b){
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MapPane.class);
				intent.putExtra("nom", equipement.getNom());
				intent.putExtra("latitude", equipement.getGeoloc().getLatitude());
				intent.putExtra("longitude", equipement.getGeoloc().getLongitude());
				startActivity(intent);
			}
		});
	}
}
