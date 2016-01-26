package com.pillowapps.liqear.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ModeListAdapter;

public class Header implements Item {
    private final String name;

    public Header(Context context, int name) {
        this.name = context.getString(name);
    }

    @Override
    public int getViewType() {
        return ModeListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.header_list_layout, null);
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.header_text_view);
        text.setText(name);

        return view;
    }

}
