package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Mode;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModeAdapter implements StickyGridHeadersBaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final SharedPreferences modePreferences = SharedPreferencesManager.getModePreferences();
    private List<Mode> modes;
    private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
    private ModeItemsHelper modeItemsHelper = new ModeItemsHelper();

    public ModeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        modeItemsHelper.calcNewModesList();
        modes = modeItemsHelper.getModes();
    }

    @Override
    public int getCountForHeader(int header) {
        return modeItemsHelper.getItemsPerCategory().get(header);
    }

    @Override
    public int getNumHeaders() {
        return modeItemsHelper.getCategories().size();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeadersHolder holder = new HeadersHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.header_layout, parent, false);
            holder.headerName = (TextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (HeadersHolder) convertView.getTag();
        }
        int sumCountForHeader = getSumCountForHeader(position);
        int categoryTitle = modes.get(sumCountForHeader).getCategoryTitle();
        holder.headerName.setText(categoryTitle);
        return convertView;
    }

    private int getSumCountForHeader(int aim) {
        int sum = 0;
        for (int i = 0; i < aim; i++) {
            sum += getCountForHeader(i);
        }
        return sum;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observers.remove(dataSetObserver);
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public Mode getItem(int i) {
        return modes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ItemsHolder holder = new ItemsHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mode_item_layout, null, false);
            holder.modeButton = (Button) convertView.findViewById(R.id.mode_button);
            holder.switchVisibilityButton =
                    (ImageButton) convertView.findViewById(R.id.edit_mode_button);
            convertView.setTag(holder);
        } else {
            holder = (ItemsHolder) convertView.getTag();
        }

        final Mode mode = modes.get(i);
        int icon = mode.getIcon();

        Resources res = context.getResources();

        Drawable drawable = res.getDrawable(icon);
        boolean modeEnabled = ModeItemsHelper.isModeEnabled(mode);
        int iconsColor = modeEnabled ? R.color.icons : R.color.accent;
        drawable.setColorFilter(res.getColor(iconsColor), PorterDuff.Mode.MULTIPLY);

        holder.modeButton.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        String label = context.getString(mode.getTitle()).toLowerCase();
        holder.modeButton.setText(label);
        holder.modeButton.setContentDescription(label);

        final boolean modeVisible = mode.isVisible();
        holder.modeButton.setEnabled(modeEnabled);
        holder.switchVisibilityButton.setVisibility(
                ModeItemsHelper.isEditMode() ? View.VISIBLE : View.GONE);
        holder.switchVisibilityButton.setImageResource(modeVisible
                        ? R.drawable.minus
                        : R.drawable.plus
        );
        holder.switchVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = modePreferences.edit();
                editor.putBoolean(Constants.MODE_VISIBLE + mode.getModeEnum(), !modeVisible);
                editor.apply();
                notifyChanges();
            }
        });
        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public List<Mode> getValues() {
        return modes;
    }

    public void notifyChanges() {
        modeItemsHelper.calcNewModesList();
        modes = modeItemsHelper.getModes();
        notifyDataSetChanged();
    }

    static class HeadersHolder {
        TextView headerName;
    }

    static class ItemsHolder {
        Button modeButton;
        ImageButton switchVisibilityButton;
    }
}
