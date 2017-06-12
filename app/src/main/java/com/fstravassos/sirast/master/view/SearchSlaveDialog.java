package com.fstravassos.sirast.master.view;

import android.app.DialogFragment;
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
 * Created by felip_000 on 09/03/2017.
 */

public class SearchSlaveDialog extends DialogFragment {

    ListView mLvPhones;
    MasterDataBase db;
    List<Slavery> myList;
    String number;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static SearchSlaveDialog newInstance() {
        SearchSlaveDialog f = new SearchSlaveDialog();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_track_select_phone, container, false);


        mLvPhones = (ListView) v.findViewById(R.id.lv_phonelist);
        db = new MasterDataBase(getActivity());

        loadList();

        return v;
    }

    private void loadList() {
        myList = db.getAllSlavers();
        mLvPhones.setAdapter(new PhonesListAdapter(getActivity(), myList));

        mLvPhones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                number = myList.get(position).getNumber();
                ((StartMasterActivity) getActivity()).getLocation(number);
            }
        });
    }
}
