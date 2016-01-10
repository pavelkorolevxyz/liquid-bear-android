/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedActivity;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.components.TouchImageView;
import com.pillowapps.liqear.helpers.FileUtils;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.network.ImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImagePagerActivity extends TrackedActivity {

    public static final String ARTIST = "artist";
    public static final String PAGE_FORMAT = "[%d/%d] ";
    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String IMAGE_POSITION = "image_position";
    private ViewPager pager;
    private ActionBar actionBar;
    private List<String> imageUrls;
    private String artist;
    private boolean loading = false;
    private int page = 1;
    private ImagePagerActivity.ImagePagerAdapter adapter;
    private ImageModel imageModel = new ImageModel();
    private ProgressBar pageProgressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        imageUrls = new ArrayList<>();
        int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        artist = bundle.getString(ARTIST);
        if (artist == null) artist = "";
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(artist);
        }

        pageProgressBar = (ProgressBar) findViewById(R.id.pageProgressBar);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImagePagerAdapter(imageUrls);
        pager.setAdapter(adapter);
        pager.setCurrentItem(pagerPosition);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getImages(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(final int i) {
                actionBar.setTitle(String.format(PAGE_FORMAT, i + 1, imageUrls.size()) + artist);
                if (!loading && i == imageUrls.size() - 1) {
                    if (imageUrls.size() == 36 * (page - 1)) {
                        getImages(i);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(ImagePagerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.download_image_button: {
                if (imageUrls.size() <= pager.getCurrentItem()) {
                    break;
                }
                String imageUrl = imageUrls.get(pager.getCurrentItem());

                String root = String.format("%s/%s", Environment.getExternalStorageDirectory(),
                        Environment.DIRECTORY_PICTURES);
                File myDir = new File(root);
                if (!myDir.exists()) {
                    boolean directoryCreated = myDir.mkdirs();
                    if (!directoryCreated) {
                        break;
                    }
                }
                String fileName = String.format("%s_%s.jpg", artist,
                        TimeUtils.formatMillisForFileName(System.currentTimeMillis()));

                File fileForImage = new File(myDir, fileName);

                InputStream sourceStream = null;
                OutputStream targetStream = null;
                File cachedImage = ImageLoader.getInstance().getDiskCache().get(imageUrl);
                try {
                    if (cachedImage.exists()) {
                        sourceStream = new FileInputStream(cachedImage);
                    } else {
                        ImageDownloader downloader = new BaseImageDownloader(ImagePagerActivity.this);
                        sourceStream = downloader.getStream(imageUrl, null);
                    }

                    targetStream = new FileOutputStream(fileForImage);
                    FileUtils.copyStream(sourceStream, targetStream);
                    Toast.makeText(ImagePagerActivity.this, getString(R.string.saved),
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (targetStream != null) {
                        try {
                            targetStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (sourceStream != null) {
                        try {
                            sourceStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
            break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_pager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getImages(final int page) {
        loading = true;
        pageProgressBar.setVisibility(View.VISIBLE);
        new LastfmArtistModel().getArtistImages(artist, this.page++, new SimpleCallback<List<String>>() {
            @Override
            public void success(List<String> images) {
                pageProgressBar.setVisibility(View.GONE);
                if (images == null) return;
                if (imageUrls == null) {
                    imageUrls = images;
                } else {
                    imageUrls.addAll(images);
                }
                adapter.notifyDataSetChanged();
                actionBar.setTitle(String.format(PAGE_FORMAT, page + 1, imageUrls.size()) + artist);
            }

            @Override
            public void failure(String errorMessage) {
                pageProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> images;
        private LayoutInflater inflater;

        ImagePagerAdapter(List<String> images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageModel.loadImage(images.get(position), imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted() {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String message) {
                    Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled() {
                    spinner.setVisibility(View.GONE);
                }
            });
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {
        }
    }
}