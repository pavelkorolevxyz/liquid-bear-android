package com.pillowapps.liqear.entities;

import android.content.SharedPreferences;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

public class Mode {
    private int title;
    private int icon;
    private Category category;
    private ModeEnum modeEnum;
    private int categoryTitle;
    private boolean editMode = false;
    private boolean needLastfm = false;
    private boolean visibleByDefault = true;

    public Mode(int title, int icon, Category category, ModeEnum modeEnum) {
        createMode(title, icon, category, modeEnum);
    }

    public Mode(int title, int icon, Category category, ModeEnum modeEnum, boolean needLastfm) {
        createMode(title, icon, category, modeEnum);
        this.needLastfm = needLastfm;
    }
    public Mode(int title, int icon, Category category, ModeEnum modeEnum,
                boolean needLastfm, boolean visibleByDefault) {
        createMode(title, icon, category, modeEnum);
        this.needLastfm = needLastfm;
        this.visibleByDefault = visibleByDefault;
    }

    private void createMode(int title, int icon, Category category, ModeEnum modeEnum) {
        this.title = title;
        this.icon = icon;
        this.category = category;
        this.modeEnum = modeEnum;
        switch (category) {
            case LAST_FM: {
                categoryTitle = R.string.last_fm;
            }
            break;
            case VK: {
                categoryTitle = R.string.vk;
            }
            break;
            case OTHER: {
                categoryTitle = R.string.other;
            }
            break;
            case LOCAL: {
                categoryTitle = R.string.local;
            }
            break;
            default: {
                categoryTitle = R.string.other;
            }
            break;
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ModeEnum getModeEnum() {
        return modeEnum;
    }

    public void setModeEnum(ModeEnum modeEnum) {
        this.modeEnum = modeEnum;
    }

    public int getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(int categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public boolean isNeedLastfm() {
        return needLastfm;
    }

    public void setNeedLastfm(boolean needLastfm) {
        this.needLastfm = needLastfm;
    }

    @Override
    public String toString() {
        return modeEnum.toString();
    }

    public boolean isVisible(){
        SharedPreferences modePreferences = SharedPreferencesManager.getModePreferences();
        return modePreferences.getBoolean(Constants.MODE_VISIBLE + getModeEnum(), visibleByDefault);
    }
}
