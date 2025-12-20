package xyz.illuminate.ui.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Objects;

import xyz.illuminate.app.Utilities;

public class Project implements Parcelable {

    public static final int TYPE_APK = 35;
    public static final int TYPE_SK_PROJECT = 33;
    public String category;
    public String comments;
    public String description;
    public String downloads;
    public String icon;
    public String id;
    public String isEditorChoice, isEditorChoice2;
    public String isLiked;
    public String isPublic;
    public String isVerified;
    public String likes;
    public String projectSize;
    public String projectType;
    public String releasedTime;
    public String ss1, ss2, ss3, ss4, ss5;
    public String timestamps;
    public String title;
    public String uid;
    public String userName, userName2;
    public String whatsnew;

    public Project(Parcel in) {
        id = in.readString();
        title = in.readString();
        icon = in.readString();
        description = in.readString();
        category = in.readString();
        whatsnew = in.readString();
        uid = in.readString();
        isEditorChoice = in.readString();
        isEditorChoice2 = in.readString();
        isVerified = in.readString();
        likes = in.readString();
        comments = in.readString();
        downloads = in.readString();
        projectType = in.readString();
        timestamps = in.readString();
        releasedTime = in.readString();
        userName = in.readString();
        userName2 = in.readString();
        projectSize = in.readString();
        ss1 = in.readString();
        ss2 = in.readString();
        ss3 = in.readString();
        ss4 = in.readString();
        ss5 = in.readString();
        isLiked = in.readString();
        isPublic = in.readString();
    }

    public Project() {

    }

    public int getId() {
        return id == null ? -1 : Integer.parseInt(id);
    }

    public int getUid() {
        if (uid == null || uid.equals("")) {
            return -1;
        }
        return Integer.parseInt(uid);
    }

    public String getFileSize() {
        return projectSize == null ? "Unknown size" : projectSize;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title==null?"Unknown project":title;
    }

    public String getDescription() {
        return description;
    }

    public String getWhatsNew() {
        return whatsnew;
    }

    public int getLikes() {
        return Utilities.parseInt(likes).intValue();
    }

    public int getCommentsCount() {
        return comments == null ? 0 : Integer.parseInt(comments);
    }

    public int getDownloadsCount() {
        return downloads == null ? 0 : Utilities.parseInt(downloads).intValue();
    }

    public String getUserName() {
        if (userName != null) {
            return userName;
        }
        if (userName2 != null) {
            return userName2;
        }
        return "Unknown user";
    }

    public boolean isEditorChoice() {
        if (isEditorChoice != null) {
            return isEditorChoice.equals("1");
        }
        if (isEditorChoice2 != null) {
            return isEditorChoice2.equals("1");
        }
        return false;
    }

    public boolean isVerified() {
        if (isVerified != null) {
            return isVerified.equals("1");
        }
        return false;
    }

    public boolean isPublic() {
        if (isPublic == null) {
            return true;
        }
        return isPublic.equals("1");
    }

    public boolean isLiked() {
        return Objects.equals(isLiked, "1");
    }

    public String getIcon() {
        return icon;
    }

    public String getProjectType() {
        return projectType == null ? "Unknown project" : projectType;
    }

    public int getType() {
        return 0;
    }

    public ArrayList<String> getScreenShots() {
        ArrayList<String> screenshots = new ArrayList<>();
        if (!checkNullOrEmpty(ss1)) {
            screenshots.add(ss1);
        }
        if (!checkNullOrEmpty(ss2)) {
            screenshots.add(ss2);
        }
        if (!checkNullOrEmpty(ss3)) {
            screenshots.add(ss3);
        }
        if (!checkNullOrEmpty(ss4)) {
            screenshots.add(ss4);
        }
        if (!checkNullOrEmpty(ss5)) {
            screenshots.add(ss5);
        }
        return screenshots;
    }

    public boolean checkNullOrEmpty(String item) {
        if (item == null) {
            return true;
        }
        return TextUtils.isEmpty(item.trim());
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(icon);
        parcel.writeString(description);
        parcel.writeString(category);
        parcel.writeString(whatsnew);
        parcel.writeString(uid);
        parcel.writeString(isEditorChoice);
        parcel.writeString(isEditorChoice2);
        parcel.writeString(isVerified);
        parcel.writeString(likes);
        parcel.writeString(comments);
        parcel.writeString(downloads);
        parcel.writeString(timestamps);
        parcel.writeString(releasedTime);
        parcel.writeString(userName);
        parcel.writeString(userName2);
        parcel.writeString(projectSize);
        parcel.writeString(ss1);
        parcel.writeString(ss2);
        parcel.writeString(ss3);
        parcel.writeString(ss4);
        parcel.writeString(ss5);
        parcel.writeString(isLiked);
        parcel.writeString(isPublic);
    }

    public void readToParcel(Parcel in) {
        id = in.readString();
        title = in.readString();
        icon = in.readString();
        description = in.readString();
        category = in.readString();
        whatsnew = in.readString();
        uid = in.readString();
        isEditorChoice = in.readString();
        isEditorChoice2 = in.readString();
        isVerified = in.readString();
        likes = in.readString();
        comments = in.readString();
        downloads = in.readString();
        projectType = in.readString();
        timestamps = in.readString();
        releasedTime = in.readString();
        userName = in.readString();
        userName2 = in.readString();
        projectSize = in.readString();
        ss1 = in.readString();
        ss2 = in.readString();
        ss3 = in.readString();
        ss4 = in.readString();
        ss5 = in.readString();
        isLiked = in.readString();
        isPublic = in.readString();
    }
}
