package com.pillowapps.liqear.entities;

import android.support.annotation.IdRes;

import com.pillowapps.liqear.R;

public class Mode {
    private int title;
    private int icon;
    private Category category;
    private int categoryTitle;
    private boolean editMode = false;
    private boolean needLastfm = false;
    private boolean visibleByDefault = true;
    private int id;

    public Mode(int title, int icon, Category category, @IdRes int id) {
        createMode(title, icon, category, id);
    }

    public Mode(int title, int icon, Category category, @IdRes int id, boolean needLastfm) {
        createMode(title, icon, category, id);
        this.needLastfm = needLastfm;
    }

    public Mode(int title, int icon, Category category, @IdRes int id,
                boolean needLastfm, boolean visibleByDefault) {
        createMode(title, icon, category, id);
        this.needLastfm = needLastfm;
        this.visibleByDefault = visibleByDefault;
    }

    private void createMode(int title, int icon, Category category, @IdRes int id) {
        this.title = title;
        this.icon = icon;
        this.category = category;
        this.id = id;
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
        return "Mode{" +
                "title=" + title +
                ", icon=" + icon +
                ", category=" + category +
                ", categoryTitle=" + categoryTitle +
                ", editMode=" + editMode +
                ", needLastfm=" + needLastfm +
                ", visibleByDefault=" + visibleByDefault +
                ", id=" + id +
                '}';
    }

    public boolean isVisible() {
//        SharedPreferences modePreferences = SharedPreferencesManager.getModePreferences(context); //todo
//        return modePreferences.getBoolean(Constants.MODE_VISIBLE + getModeEnum(), visibleByDefault);
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
