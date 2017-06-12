package com.fstravassos.sirast.master.view;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fstravassos.sirast.R;
import com.fstravassos.sirast.master.Adapter.PhonesListAdapter;
import com.fstravassos.sirast.master.models.Slavery;
import com.fstravassos.sirast.master.database.MasterDataBase;

import java.util.List;

/**
 * Created by felip_000 on 13/03/2017.
 */

public class RemoveSlaveDialog extends DialogFragment {

    MasterDataBase db;
    List<Slavery> myList;
    ListView mLvPhones;
    String number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_remove_select_phone, container, false);

        db = new MasterDataBase(getActivity());
        mLvPhones = (ListView) v.findViewById(R.id.lv_phonelist);
        loadList();
        loadActions();

        return v;
    }

    private void loadActions() {
        mLvPhones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                number = myList.get(position).getNumber();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Deseja realmente remover: " + number);
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteSlaver(number);
                        loadList();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void loadList() {
        myList = db.getAllSlavers();
        mLvPhones.setAdapter(new PhonesListAdapter(getActivity(), myList));
        mLvPhones.refreshDrawableState();
    }
}
