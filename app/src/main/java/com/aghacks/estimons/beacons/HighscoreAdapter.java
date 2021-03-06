package com.aghacks.estimons.beacons;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aghacks.estimons.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 24.10.15.
 */
public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreAdapter.VH> {
    public static final String TAG = HighscoreAdapter.class.getSimpleName();
    List<String> dataSet = new ArrayList<>();

    public HighscoreAdapter(List<String> list) {
        dataSet.addAll(list);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {


        View vh = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscores_item, parent, false);

        return new VH(vh, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public void onBindViewHolder(VH vh, int position) {
        vh.text.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        public TextView text;
        public RelativeLayout parent;

        public VH(View v, int viewType) {
            super(v);
            text = (TextView) v.findViewById(R.id.highscores_text);
            parent = (RelativeLayout) v.findViewById(R.id.parent);
            if (viewType == 0)
                parent.setBackgroundResource(R.drawable.cz_health1);
            else
                parent.setBackgroundResource(R.drawable.n_health1);
        }
    }
}
