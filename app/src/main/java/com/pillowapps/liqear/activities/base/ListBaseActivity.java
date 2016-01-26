package com.pillowapps.liqear.activities.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.views.HintMaterialEditText;
import com.pillowapps.liqear.views.LoadMoreRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class ListBaseActivity extends ResultTrackedBaseActivity {

    @Bind(R.id.search_edit_text_quick_search_layout)
    protected HintMaterialEditText editText;
    @Bind(R.id.list)
    protected LoadMoreRecyclerView recycler;
    @Bind(R.id.empty)
    protected TextView emptyTextView;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);
    }

    protected void updateEmptyTextView() {
        emptyTextView.setVisibility(recycler.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}
