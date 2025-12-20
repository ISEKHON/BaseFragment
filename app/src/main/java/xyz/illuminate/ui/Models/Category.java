package xyz.illuminate.ui.Models;

import java.util.ArrayList;

public class Category {
    public static final int TYPE_Ad = 7;
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_GRID = 8;
    public static final int TYPE_LIST = 6;
    public static final int TYPE_VIEWPAGER = 5;

    public String category;
    public transient ArrayList<Project> content = new ArrayList<>();
    public int content_type;
    public boolean error = false;
    public boolean hideSeeMore = false;
    public int id;
    public String name;
    public String scope;
    public int type;

    public String getContentType() {
        switch (content_type) {
            case TYPE_Ad:
                return "Advertisement";
            case TYPE_GRID:
                return "GridList";
            case TYPE_LIST:
                return "HorizontalList";
            case TYPE_VIEWPAGER:
                return "ViewPagerList";
            default:
                return "";
        }
    }
}
