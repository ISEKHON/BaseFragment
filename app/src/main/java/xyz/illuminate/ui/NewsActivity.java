package xyz.illuminate.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.R;
import xyz.illuminate.server.ConnectionsManager;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.Cells.AnnouncementsCell;
import xyz.illuminate.ui.Components.AlertsCreator;
import xyz.illuminate.ui.Components.FontTextView;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;

public class NewsActivity extends BaseFragment {

    View inflatedView;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> data;
    LinearLayoutManager linearLayoutManager;
    LinearLayout linear_root;
    FontTextView title;
    RecyclerListView listView;

    String TAG = "NewsActivity";


    public NewsActivity() {
        data = new ArrayList<>();
    }

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setAddToContainer(false);
        return actionBar;
    }

    private void initialize(View view) {
        linear_root = view.findViewById(R.id.page_layout);
        title = view.findViewById(R.id.text1);
        title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    @Override
    public View createView(Context context) {
        fragmentView = new SizeNotifierFrameLayout(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflatedView = inflater.inflate(R.layout.activity_news, (ViewGroup) fragmentView, false);
        initialize(inflatedView);

        listView = new RecyclerListView(context);
        linearLayoutManager = new LinearLayoutManager(context);
        listView.setLayoutManager(linearLayoutManager);

        listView.setNestedScrollingEnabled(true);
        listView.setPadding(0, 0, 0, AndroidUtilities.dp(58));
        HashMap hashMap = new HashMap();
        hashMap.put("sha256", AndroidUtilities.getCertificateSHA256Fingerprints());
        ConnectionsManager.getInstance(context).post("https://icheat.illuminatii.xyz/getNews.php", hashMap, new ConnectionsManager.RequestListener() {
            @Override
            public void onErrorResponse(String error) {
                FileLog.e(error);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    AnnouncementsResponse announcement = new Gson().fromJson((String) response, new TypeToken<AnnouncementsResponse>() {
                    }.getType());
                    if (announcement.getStatus().equals("success")) {
                        data.clear();
                        data.addAll(announcement.getNews());
                        adapter = new ListAdapter(context, data);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        HashMap<String, String> error = new HashMap<>();
                        error.put("message", announcement.getMessage());
                        error.put("status_code", announcement.status_code);
                        AlertsCreator.processError(error, NewsActivity.this);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
            }
        }, TAG);
        linear_root.addView(listView, LayoutHelper.createFrame(-1, -1.0f));
        ((ViewGroup) fragmentView).addView(inflatedView);
        return fragmentView;
    }

    @Override
    public boolean onFragmentCreate() {
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        getConnectionsManager().cancelAll(TAG);
    }

    private int getUnReadCount() {
        return 0;
    }

    private void saveReadCount() {

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{AnnouncementsCell.class}, new String[]{"title"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{AnnouncementsCell.class}, new String[]{"message"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{AnnouncementsCell.class}, new String[]{"time"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        return themeDescriptions;
    }

    public static class AnnouncementsResponse {
        String message;
        ArrayList<HashMap<String, String>> news;
        String status;
        String status_code;

        public String getMessage() {
            return message;
        }

        public ArrayList<HashMap<String, String>> getNews() {
            return news;
        }

        public String getStatus() {
            return status;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {

        Context context;
        ArrayList<HashMap<String, String>> data;

        public ListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AnnouncementsCell announcementsCell = new AnnouncementsCell(context);
            announcementsCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(announcementsCell);
        }

        @Override
        public int getItemCount() {
            ArrayList<HashMap<String, String>> data = this.data;
            if (data != null && !data.isEmpty()) {
                return this.data.size();
            }
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            ((AnnouncementsCell) holder.itemView).bind(this.data.get(i));
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }
    }
}
