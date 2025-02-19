package io.agora.education;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;
//隐私协议弹框
public class PrivacyTermsDialog {
    private final Context mContext;
    private CheckBox checkBox;
    private FrameLayout frameLayout;
    private AlertDialog mDialog;
    private OnPrivacyTermsDialogListener mDialogListener;

    private void initView() {
        View customView = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_privacy_terms, null);
        this.checkBox = customView.findViewById(R.id.termsCheck);
        FrameLayout flRoot = new FrameLayout(this.mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int margin = convertDpToPixel(mContext, 8f);
        layoutParams.setMargins(margin, margin, margin, 0);
        flRoot.addView(customView, layoutParams);
        initWebView(customView.findViewById(R.id.webview));
        this.frameLayout = flRoot;
    }

    private void initWebView(WebView webView){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (getLocalLanguage().equalsIgnoreCase("cn")){
            webView.loadUrl("https://agora-adc-artifacts.s3.cn-north-1.amazonaws.com.cn/demo/education/privacy.html");
        }else {
            webView.loadUrl("https://agora-adc-artifacts.s3.cn-north-1.amazonaws.com.cn/demo/education/privacy_en.html");
        }
//        webView.loadUrl(String.format(Locale.US, "file:android_asset/privacy_%s.html", getLocalLanguage()));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(mContext, R.style.CustomMaterialAlertDialog)
                        .setTitle(R.string.setting_title_user_agreement)
                        .setOnDismissListener(it -> PrivacyTermsDialog.this.clearUI())
                        .setView(frameLayout)
                        .setCancelable(false)
                        .setPositiveButton(R.string.accept, (dialog, which) -> {
                            if (mDialogListener != null) {
                                mDialogListener.onPositiveClick();
                            }
                        })
                        .setNegativeButton(R.string.decline, (dialog, which) -> {
                            if (mDialogListener != null) {
                                mDialogListener.onNegativeClick();
                            }
                        });
                if (mDialog==null){
                    mDialog = mDialogBuilder.create();
                    mDialog.show();
                }
                Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positive.setEnabled(false);
                negative.setTextColor(ContextCompat.getColor(mContext, R.color.teams_color_error));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> positive.setEnabled(isChecked));
                checkBox.requestFocus();
            }
        });
    }

    private String getLocalLanguage(){
        Locale locale = Locale.getDefault();
        if (!Locale.SIMPLIFIED_CHINESE.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
            return "en";
        }
        return "cn";
    }

    public final void show() {
        this.initView();
    }

    private void clearUI() {
        ViewParent parent = null;
        if (frameLayout != null) {
            parent = frameLayout.getParent();
        }
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeAllViews();
        }
    }

    public final void dismiss() {
        AlertDialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void setPrivacyTermsDialogListener(OnPrivacyTermsDialogListener listener) {
        this.mDialogListener = listener;
    }

    public PrivacyTermsDialog(Context context) {
        this.mContext = context;
    }

    private int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public interface OnPrivacyTermsDialogListener {
        void onPositiveClick();

        void onNegativeClick();
    }

}
