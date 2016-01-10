package com.pillowapps.liqear.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MusicServiceManager;
import com.pillowapps.liqear.adapters.ModeGridAdapter;
import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.components.ModeClickListener;
import com.pillowapps.liqear.components.OnItemStartDragListener;
import com.pillowapps.liqear.components.ViewPage;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.home.PhoneHomePresenter;
import com.pillowapps.liqear.models.Tutorial;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PhoneHomeFragment extends HomeFragment {

    private ViewPager pager;
    private UnderlinePageIndicator indicator;
    private View playlistTab;
    private View playbackTab;
    private View modeTab;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return onOptionsItemSelected(menuItem);
        }
    };

    /**
     * Playlists tab
     **/

    private ListView playlistListView;
    private EditText searchPlaylistEditText;
    private Toolbar playlistToolbar;
    private TextView emptyPlaylistTextView;
    private ItemTouchHelper mItemTouchHelper;


    /**
     * Play tab
     */

    private Toolbar playbackToolbar;
    private TextView artistTextView;
    private TextView titleTextView;
    private ImageButton playPauseButton;
    private SeekBar seekBar;
    private TextView timeTextView;
    private TextView timeDurationTextView;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private TextView timePlateTextView;
    private ImageView artistImageView;
    private ImageView albumImageView;
    private TextView albumTextView;
    private FloatingActionButton loveFloatingActionButton;
    private View blackView;
    private View tutorialLayout;
    private Animation tutorialBlinkAnimation;
    private ViewGroup backLayout;
    private ViewGroup bottomControlsLayout;

    /**
     * Modes tab
     */

    private Toolbar modeToolbar;

    private Tutorial tutorial = new Tutorial();
    private ModeGridAdapter modeAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.handset_fragment_layout, container, false);

        presenter = new PhoneHomePresenter(this);
        initUi(v);

        LBApplication.bus.register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LBApplication.bus.unregister(this);
    }

    private void initUi(View v) {
        initViewPager(v);
        changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX);

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        if (tutorial.isEnabled()) {
            showTutorial();
        }
    }

    private void initViewPager(View v) {
        final List<ViewPage> pages = new ArrayList<>();
        Context context = getContext();
        playlistTab = View.inflate(context, R.layout.playlist_tab, null);
        playbackTab = View.inflate(context, R.layout.play_tab, null);
        modeTab = View.inflate(context, R.layout.mode_tab, null);
        pages.add(new ViewPage(context, playlistTab, R.string.playlist_tab));
        pages.add(new ViewPage(context, playbackTab, R.string.play_tab));
        pages.add(new ViewPage(context, modeTab, R.string.mode_tab));
        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(pages.size());
        pager.setAdapter(new PhoneFragmentPagerAdapter(pages));

        playlistToolbar = (Toolbar) playlistTab.findViewById(R.id.toolbar);
        playbackToolbar = (Toolbar) playbackTab.findViewById(R.id.toolbar);
        modeToolbar = (Toolbar) modeTab.findViewById(R.id.toolbar);
        playlistToolbar.setTitle(R.string.playlist_tab);
        playlistToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), playlistToolbar.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        playbackToolbar.setTitle(R.string.app_name);
        modeToolbar.setTitle(R.string.mode_tab);
        updateToolbars();

        indicator = (UnderlinePageIndicator) v.findViewById(R.id.indicator);
        indicator.setSelectedColor(getResources().getColor(R.color.accent));
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
//                invalidateOptionsMenu();
                setHasOptionsMenu(true);
                if (index != PhoneFragmentPagerAdapter.PLAY_TAB_INDEX) {
                    if (tutorial.isEnabled() && tutorialBlinkAnimation != null) {
                        tutorialBlinkAnimation.cancel();
                        tutorialLayout.setVisibility(View.GONE);
                        tutorial.end();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initPlaylistsTab() {
        playlistListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_main_playlist_track, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        onContextItemSelected(item);
                        return true;
                    }
                });

                popup.show();
            }
        };
        OnItemStartDragListener onStartDragListener = new OnItemStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        };
        playlistListView.setAdapter(playlistItemsAdapter);
        searchPlaylistEditText = (EditText) playlistTab.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(SharedPreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ? View.VISIBLE : View.GONE);
        emptyPlaylistTextView = (TextView) playlistTab.findViewById(R.id.empty);
    }

    private void initPlaybackTab() {
        artistTextView = (TextView) playbackTab.findViewById(R.id.artist_text_view_playback_tab);
        titleTextView = (TextView) playbackTab.findViewById(R.id.title_text_view_playback_tab);
        playPauseButton = (ImageButton) playbackTab.findViewById(R.id.play_pause_button_playback_tab);
        backLayout = (ViewGroup) playbackTab.findViewById(R.id.back_layout);
        bottomControlsLayout = (ViewGroup) playbackTab.findViewById(R.id.bottom_controls_layout);
        seekBar = (SeekBar) playbackTab.findViewById(R.id.seek_bar_playback_tab);
        timeTextView = (TextView) playbackTab.findViewById(R.id.time_text_view_playback_tab);
        timeDurationTextView = (TextView) playbackTab.findViewById(R.id.time_inverted_text_view_playback_tab);
        nextButton = (ImageButton) playbackTab.findViewById(R.id.next_button_playback_tab);
        prevButton = (ImageButton) playbackTab.findViewById(R.id.prev_button_playback_tab);
        shuffleButton = (ImageButton) playbackTab.findViewById(R.id.shuffle_button_playback_tab);
        repeatButton = (ImageButton) playbackTab.findViewById(R.id.repeat_button_playback_tab);
        artistImageView = (ImageView) playbackTab.findViewById(R.id.artist_image_view_headset);
        artistImageView.setImageResource(R.drawable.artist_placeholder);
        timePlateTextView = (TextView) playbackTab.findViewById(R.id.time_plate_text_view_playback_tab);
        albumImageView = (ImageView) playbackTab.findViewById(R.id.album_cover_image_view);
        albumTextView = (TextView) playbackTab.findViewById(R.id.album_title_text_view);

        loveFloatingActionButton = (FloatingActionButton) playbackTab.findViewById(R.id.love_button);

        blackView = playbackTab.findViewById(R.id.view);
    }

    private void initModeTab() {
        modeAdapter = new ModeGridAdapter(activity);

        StickyGridHeadersGridView modeGridView = (StickyGridHeadersGridView) modeTab.findViewById(R.id.mode_gridview);
        modeGridView.setOnItemClickListener(new ModeClickListener(this));
        modeGridView.setOnItemLongClickListener(new ModeLongClickListener());
        modeGridView.setAdapter(modeAdapter);
    }

    public void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    public void updateMainPlaylistTitle() {
        String title = Timeline.getInstance().getPlaylist().getTitle();
        if (title == null) {
            title = getString(R.string.playlist_tab);
        }
        playlistToolbar.setTitle(title);
    }

    private void showTutorial() {
        ImageView swipeLeftImageView = (ImageView) playbackTab.findViewById(R.id.swipe_left_image_view);
        ImageView swipeRightImageView = (ImageView) playbackTab.findViewById(R.id.swipe_right_image_view);
        tutorialLayout = playbackTab.findViewById(R.id.tutorial_layout);
        tutorialLayout.setVisibility(View.VISIBLE);
        tutorialBlinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_animation);
        swipeLeftImageView.startAnimation(tutorialBlinkAnimation);
        swipeRightImageView.startAnimation(tutorialBlinkAnimation);
    }

    private void updateToolbars() {
        playlistToolbar.getMenu().clear();
        modeToolbar.getMenu().clear();
        modeToolbar.inflateMenu(R.menu.menu_mode_tab);
        playlistToolbar.inflateMenu(R.menu.menu_playlist_tab);
        playlistToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        modeToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        updatePlaybackToolbar();
    }

    public void updatePlaybackToolbar() {
        playbackToolbar.getMenu().clear();
        int menuLayout = R.menu.menu_play_tab_no_current_track;
        if (Timeline.getInstance().getCurrentTrack() != null) {
            menuLayout = R.menu.menu_play_tab;
        }
        playbackToolbar.inflateMenu(menuLayout);
        playbackToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//        mainActivity.setMainMenu(playbackToolbar.getMenu());
    }


    @Override
    public void changePlaylist(int index, boolean autoPlay) {
        super.changePlaylist(index, autoPlay);

        updateMainPlaylistTitle();
        changeViewPagerItem(0);
    }

    @Override
    public void playTrack(int index) {
        Track track = playlistItemsAdapter.getItem(index);

        artistTextView.setText(track.getArtist());
        titleTextView.setText(track.getTitle());
        playPauseButton.setImageResource(R.drawable.pause_button);

        MusicServiceManager.getInstance().play(track.getRealPosition());
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
//            mainActivity.getModeAdapter().notifyChanges();
            return true;
        }
    }
}
