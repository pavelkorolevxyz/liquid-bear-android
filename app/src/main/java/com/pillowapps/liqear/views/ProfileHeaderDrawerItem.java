package com.pillowapps.liqear.views;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.model.utils.ViewHolderFactory;
import com.pillowapps.liqear.R;

public class ProfileHeaderDrawerItem extends AbstractDrawerItem<ProfileHeaderDrawerItem> {
    @Override
    public String getType() {
        return "lb_profile_header";
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.side_menu_header;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder) {
        onPostBindView(this, holder.itemView);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder factory(View v) {
            return new ViewHolder(v);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
