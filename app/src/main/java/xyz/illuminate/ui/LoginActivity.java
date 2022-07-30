package xyz.illuminate.ui;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.transition.TransitionManager;

import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RadialProgressView;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;

public class LoginActivity extends BaseFragment {

    View view;
    EditText username, pass;
    TextView loginText;
    LinearLayout user_base, pass_base;
    FrameLayout login_btn;
    ImageView paste_btn;
    RadialProgressView progressView;
    private final HashMap<String, String> hashMap = new HashMap<>();

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setAddToContainer(false);
        return actionBar;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View createView(Context context) {
        fragmentView = new SizeNotifierFrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        fragmentView.setOnTouchListener((view, ev) -> true);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.activity_login, (ViewGroup) fragmentView, false);
        ((ViewGroup) fragmentView).addView(view);

        progressView = new RadialProgressView(context);
        progressView.setProgressColor(-1);
        progressView.setVisibility(View.GONE);
        progressView.setSize(AndroidUtilities.dp(20));

        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.pass);
        loginText = (TextView) findViewById(R.id.loginText);
        login_btn = (FrameLayout) findViewById(R.id.login_btn);
        user_base = (LinearLayout) findViewById(R.id.user_base);
        pass_base = (LinearLayout) findViewById(R.id.pass_base);
        paste_btn = (ImageView) findViewById(R.id.paste_btn);

        paste_btn.setOnClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService(CLIPBOARD_SERVICE);
            String[] split = clipboardManager.getText().toString().split(":");
            if (clipboardManager.getText().toString().contains(":")) {
                username.setText(split[0]);
                pass.setText(split[1]);
                AndroidUtilities.hideKeyboard(view);
            } else {
                AndroidUtilities.showToast("invalid key");
            }
        });
        login_btn.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(8), Theme.getColor(Theme.key_buttonBackgroundBlue)));
        login_btn.addView(progressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        if (getUserConfig().isClientActivated()) {
            username.setText(getUserConfig().getUsername());
            pass.setText(getUserConfig().getPassword());
        }

        login_btn.setOnClickListener(_view -> {
            ((LaunchActivity) getParentActivity())._refresh();
            /*setLoading(true);
            login_btn.setClickable(false);
            getConnectionsManager().login(username.getText().toString(), pass.getText().toString(), new ConnectionsManager.RequestListener() {
                @Override
                public void onErrorResponse(String error) {
                    FileLog.d(error);
                    setLoading(false);
                }

                @Override
                public void onResponse(Object response) {
                    try {
                        LoginResponse loginResponse = new Gson().fromJson((String) response, new TypeToken<LoginResponse>() {
                        }.getType());
                        if (loginResponse.getStatus().equals("success")) {
                            HashMap<String, String> userData = new HashMap<>();
                            userData.put("username", username.getText().toString());
                            userData.put("pass", pass.getText().toString());
                            userData.put("key", userData.get("username") + ":" + userData.get("pass"));
                            getUserConfig().saveLoginDetails(userData);
                            userData.clear();
                            //getUserConfig().fetchClientData(LoginActivity.this,userData.get("username"));
                            ((LaunchActivity) getParentActivity())._refresh();
                        } else {
                            HashMap<String, String> error = new HashMap<>();
                            error.put("message", loginResponse.getMessage());
                            error.put("status_code", loginResponse.getStatusCode());
                            AlertsCreator.processError(error, LoginActivity.this);
                            login_btn.setClickable(true);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }

                    setLoading(false);
                }

                @Override
                public void onResponse(byte[] bytes) {
                }
            });*/
        });
        return fragmentView;
    }

    private View findViewById(int id) {
        return view.findViewById(id);
    }

    protected int getThemeColor(String key) {
        return Theme.getColor(key);
    }

    private void setLoading(boolean z) {
        TransitionManager.beginDelayedTransition(login_btn);
        if (z) {
            loginText.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        } else {
            loginText.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }
    }

}
