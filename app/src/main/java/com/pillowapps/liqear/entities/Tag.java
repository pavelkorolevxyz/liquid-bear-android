package com.pillowapps.liqear.entities;

public class Tag {
    private String name;
    private int reach;
    private long taggings;

    public Tag(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReach() {
        return reach;
    }

    public void setReach(int reach) {
        this.reach = reach;
    }

    public long getTaggings() {
        return taggings;
    }

    public void setTaggings(long taggings) {
        this.taggings = taggings;
    }

    @Override
    public String toString() {
        return "Tag [name=" + name + ", reach=" + reach + ", taggings="
                + taggings + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return name.equals(tag.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
