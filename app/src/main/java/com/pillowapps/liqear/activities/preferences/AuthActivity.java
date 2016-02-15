package com.pillowapps.liqear.activities.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.HomeActivity;
import com.pillowapps.liqear.activities.base.TrackedBaseActivity;
import com.pillowapps.liqear.adapters.pagers.AuthPagerAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.callbacks.retrofit.LastfmErrorCallback;
import com.pillowapps.liqear.entities.Page;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmSession;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmAuthModel;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;
import com.pillowapps.liqear.models.vk.VkUserModel;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AuthActivity extends TrackedBaseActivity {
    private static final int VK_INDEX = 0;
    private static final int LASTFM_INDEX = 1;
    private View vkTab;
    private ViewPager pager;
    private View lastfmTab;
    private View authorizeVkButton;
    private View authorizeLastfmButton;
    private EditText loginLastfmEditText;
    private EditText passwordLastfmEditText;
    private View authVkPanel;
    private View authLastfmPanel;
    private TextView vkNameTextView;
    private TextView lastfmNameTextView;
    private ImageView avatarVkImageView;
    private ImageView avatarLastfmImageView;

    private boolean firstStart;
    private TextView errorVkTextView;
    private TextView errorLastfmTextView;
    private final ImageModel imageModel = new ImageModel();
    private View signOutVkButton;
    private View signOutLastfmButton;
    private ProgressBar lastfmProgressBar;

    @Inject
    LastfmAuthModel authModel;

    @Inject
    VkUserModel vkUserModel;

    @Inject
    LastfmUserModel lastfmUserModel;

    @Inject
    AuthorizationInfoManager authorizationInfoManager;

    public static Intent startIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setContentView(R.layout.auth_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.authorizations);
        firstStart = getIntent().getBooleanExtra(Constants.SHOW_AUTHSCREEN_AUTO, false);
        initViewPager();
        initUi();
        initListeners();
        showSaves();
        int authProblemsState = getIntent().getIntExtra(Constants.AUTH_PROBLEMS, 0);
        switch (authProblemsState) {
            case 1 /*Lastfm*/:
                pager.setCurrentItem(LASTFM_INDEX);
                errorLastfmTextView.setVisibility(View.VISIBLE);
                break;
            case 2 /*VK*/:
                pager.setCurrentItem(VK_INDEX);
                errorVkTextView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        if (!firstStart) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void showSaves() {
        if (authorizationInfoManager.isAuthorizedOnVk()) {
            avatarVkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageModel.loadImage(authorizationInfoManager.getVkAvatar(), avatarVkImageView);
            vkNameTextView.setText(authorizationInfoManager.getVkName());
            authVkPanel.setVisibility(View.VISIBLE);
            signOutVkButton.setVisibility(View.VISIBLE);
            authorizeVkButton.setVisibility(View.GONE);
        } else {
            avatarVkImageView.setScaleType(ImageView.ScaleType.CENTER);
            avatarVkImageView.setImageResource(R.drawable.user);
            signOutVkButton.setVisibility(View.GONE);
            authorizeVkButton.setVisibility(View.VISIBLE);
        }
        if (authorizationInfoManager.isAuthorizedOnLastfm()) {
            avatarLastfmImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageModel.loadImage(authorizationInfoManager.getLastfmAvatar(), avatarLastfmImageView);
            lastfmNameTextView.setText(authorizationInfoManager.getLastfmName());
            loginLastfmEditText.setText(authorizationInfoManager.getLastfmName());
            authLastfmPanel.setVisibility(View.VISIBLE);
            signOutLastfmButton.setVisibility(View.VISIBLE);
            authorizeLastfmButton.setVisibility(View.GONE);
            loginLastfmEditText.setVisibility(View.GONE);
            passwordLastfmEditText.setVisibility(View.GONE);
        } else {
            avatarLastfmImageView.setScaleType(ImageView.ScaleType.CENTER);
            avatarLastfmImageView.setImageResource(R.drawable.user);
            signOutLastfmButton.setVisibility(View.GONE);
            authorizeLastfmButton.setVisibility(View.VISIBLE);
            loginLastfmEditText.setVisibility(View.VISIBLE);
            passwordLastfmEditText.setVisibility(View.VISIBLE);
        }
    }

    private void initViewPager() {
        final List<View> views = new ArrayList<>();
        vkTab = View.inflate(this, R.layout.auth_vk_layout, null);
        views.add(vkTab);
        lastfmTab = View.inflate(this, R.layout.auth_lastfm_layout, null);
        views.add(lastfmTab);
        pager = (ViewPager) findViewById(R.id.pager);
        List<Page> pages = new ArrayList<>();
        pages.add(new Page(vkTab, getString(R.string.vk)));
        pages.add(new Page(lastfmTab, getString(R.string.last_fm)));
        AuthPagerAdapter adapter = new AuthPagerAdapter(this, pages);

        pager.setAdapter(adapter);
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(0);
        indicator.setBackgroundColor(ContextCompat.getColor(AuthActivity.this, R.color.primary));
        indicator.setFooterColor(ContextCompat.getColor(AuthActivity.this, R.color.accent));
        indicator.setTextColor(ContextCompat.getColor(AuthActivity.this, R.color.icons));
        indicator.setSelectedColor(ContextCompat.getColor(AuthActivity.this, R.color.icons));

        Resources resources = AuthActivity.this.getResources();
        boolean isTablet = resources.getBoolean(R.bool.isTablet);
        if (isTablet && resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            indicator.setVisibility(View.GONE);
        }

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                // no-op
            }

            @Override
            public void onPageSelected(int i) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // no-op
            }
        });
    }

    private void initUi() {
        authorizeVkButton = vkTab.findViewById(R.id.authorize_vk_button);
        signOutVkButton = vkTab.findViewById(R.id.sign_out_vk_button);
        authorizeLastfmButton = lastfmTab.findViewById(R.id.authorize_lastfm_button);
        signOutLastfmButton = lastfmTab.findViewById(R.id.sign_out_lastfm_button);
        authVkPanel = vkTab.findViewById(R.id.auth_panel);
        loginLastfmEditText = (EditText) lastfmTab.findViewById(R.id.login_edit_text);
        passwordLastfmEditText = (EditText) lastfmTab.findViewById(R.id.password_edit_text);
        authLastfmPanel = lastfmTab.findViewById(R.id.auth_panel);
        vkNameTextView = (TextView) vkTab.findViewById(R.id.name_text_view);
        lastfmNameTextView = (TextView) lastfmTab.findViewById(R.id.name_text_view);
        avatarVkImageView = (ImageView) vkTab.findViewById(R.id.avatar);
        avatarLastfmImageView = (ImageView) lastfmTab.findViewById(R.id.avatar);
        errorVkTextView = (TextView) vkTab.findViewById(R.id.auth_error_text_view);
        errorLastfmTextView = (TextView) lastfmTab.findViewById(R.id.auth_error_text_view);
        lastfmProgressBar = (ProgressBar) lastfmTab.findViewById(R.id.lastfm_progress_bar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.AUTH_VK_REQUEST) {
            vkUserModel.getUserInfoVk(authorizationInfoManager.getVkUserId(),
                    new VkSimpleCallback<VkUser>() {
                        @Override
                        public void success(VkUser vkUser) {
                            authorizationInfoManager.setVkAvatar(vkUser.getPhotoMedium());
                            authorizationInfoManager.setVkName(vkUser.getName());
                            invalidateOptionsMenu();
                            showSaves();
                            if (firstStart && authorizationInfoManager.isAuthorizedOnLastfm()) {
                                startMainActivity(AuthActivity.this);
                                finish();
                            }
                        }

                        @Override
                        public void failure(VkError error) {
                            //todo
                        }
                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initListeners() {
        authorizeVkButton.setOnClickListener(view -> {
            errorVkTextView.setVisibility(View.GONE);
            authorizationInfoManager.signOutVk();
            authVkPanel.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(AuthActivity.this, VkLoginActivity.class);
            startActivityForResult(intent, Constants.AUTH_VK_REQUEST);
        });
        signOutVkButton.setOnClickListener(view -> {
            authorizationInfoManager.signOutVk();
            authVkPanel.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
            showSaves();
        });

        signOutLastfmButton.setOnClickListener(view -> {
            authorizationInfoManager.signOutLastfm();
            authLastfmPanel.setVisibility(View.INVISIBLE);
            loginLastfmEditText.setVisibility(View.VISIBLE);
            passwordLastfmEditText.setVisibility(View.VISIBLE);
            loginLastfmEditText.setText("");
            passwordLastfmEditText.setText("");
            showSaves();
        });
        authorizeLastfmButton.setOnClickListener(view -> {
            errorLastfmTextView.setVisibility(View.GONE);
            authorizationInfoManager.signOutLastfm();
            authLastfmPanel.setVisibility(View.INVISIBLE);
            loginLastfmEditText.setVisibility(View.VISIBLE);
            passwordLastfmEditText.setVisibility(View.VISIBLE);

            hideKeyBoard();

            String username = loginLastfmEditText.getText().toString().trim();
            String password = passwordLastfmEditText.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                new MaterialDialog.Builder(AuthActivity.this)
                        .title(R.string.last_fm)
                        .content(R.string.enter_login_password)
                        .positiveText(android.R.string.ok)
                        .show();
                return;
            }


            lastfmProgressBar.setVisibility(View.VISIBLE);
            authModel.getMobileSession(
                    username,
                    password,
                    new LastfmErrorCallback<LastfmSession>() {
                        @Override
                        public void success(LastfmSession session) {
                            lastfmProgressBar.setVisibility(View.GONE);
                            String name = session.getName();
                            SharedPreferences.Editor editor = SharedPreferencesManager
                                    .getLastfmPreferences(AuthActivity.this).edit();
                            editor.putString(Constants.LASTFM_NAME, name);
                            editor.putString(Constants.LASTFM_KEY, session.getKey());
                            editor.apply();
                            lastfmNameTextView.setText(name);
                            authLastfmPanel.setVisibility(View.VISIBLE);
                            lastfmUserModel.getUserInfo(name, new SimpleCallback<LastfmUser>() {
                                @Override
                                public void success(LastfmUser user) {
                                    List<LastfmImage> images = user.getImages();
                                    String url = images.get(images.size() - 1).getUrl();
                                    authorizationInfoManager.setLastfmAvatar(url);
                                    imageModel.loadImage(url, avatarLastfmImageView);
                                    showSaves();
                                }

                                @Override
                                public void failure(String errorMessage) {
                                    //todo
                                }
                            });
                            invalidateOptionsMenu();
                            if (firstStart && authorizationInfoManager.isAuthorizedOnVk()) {
                                finish();
                                startMainActivity(AuthActivity.this);
                            }
                        }

                        @Override
                        public void failure(int code, String errorMessage) {
                            lastfmProgressBar.setVisibility(View.GONE);
                            if (code == 4) { // Lastfm Invalid Password
                                ErrorNotifier.showError(AuthActivity.this, getString(R.string.invalid_password));
                                return;
                            }
                            ErrorNotifier.showError(AuthActivity.this, errorMessage);
                        }
                    });
        });
    }

    public void startMainActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void signUpVk() {
        String url = "http://vk.com/join";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void signUpLastfm() {
        String url = "http://www.lastfm.ru/join";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        switch (pager.getCurrentItem()) {
            case VK_INDEX: {
                if (authorizationInfoManager.isAuthorizedOnVk()) {
                    inflater.inflate(R.menu.menu_auth, menu);
                } else {
                    inflater.inflate(R.menu.menu_sign_up, menu);
                }
            }
            break;
            case LASTFM_INDEX:
                if (authorizationInfoManager.isAuthorizedOnLastfm()) {
                    inflater.inflate(R.menu.menu_auth, menu);
                } else {
                    inflater.inflate(R.menu.menu_sign_up, menu);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        int currentPage = pager.getCurrentItem();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.sign_up_button: {
                if (currentPage == VK_INDEX) {
                    signUpVk();
                } else if (currentPage == LASTFM_INDEX) {
                    signUpLastfm();
                }
            }
            return true;
            case R.id.skip_button: {
                authorizationInfoManager.skipAuth();
                startMainActivity(AuthActivity.this);
                finish();
            }
            return true;
            default:
                break;
        }
        return false;
    }
}
