package javinator9889.bitcoinpools.AppUpdaterManager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import javinator9889.bitcoinpools.BitCoinApp;
import javinator9889.bitcoinpools.Constants;

/**
 * Created by Javinator9889 on 24/01/2018.
 * Check for updates based on GitHub releases
 */

public class CheckUpdates {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";
    private static String CONNECTION_URL;
    private static String APP_VERSION;
    private static JSONArray RETRIEVED_DATA;
    private static String LATEST_VERSION;
    private static String DOWNLOAD_URL = null;
    private static String MORE_INFO;
    private static String APK_NAME;
    private static boolean HTML_PAGE = false;

    public CheckUpdates(@NonNull String GitHub_User, @NonNull String GitHub_Repo) {
        CONNECTION_URL = GITHUB_API_URL + GitHub_User + "/" + GitHub_Repo + "/releases";
        try {
            APP_VERSION = BitCoinApp.getAppContext().getPackageManager().getPackageInfo(BitCoinApp.getAppContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.LOG.CTAG, Constants.LOG.NO_INFO + e.getMessage(), new PackageManager.NameNotFoundException());
        }
        getData();
    }

    public void checkForUpdates(final Context dialogContext, String title, String description, String positiveText, String negativeText, String neutralText) {
        if (!APP_VERSION.equals(LATEST_VERSION) && (Float.parseFloat(APP_VERSION) < Float.parseFloat(LATEST_VERSION))) {
            Log.d(Constants.LOG.CTAG, Constants.LOG.NEW_VERSION + APP_VERSION + " | " + LATEST_VERSION);
            MaterialDialog materialDialog;
            if (!HTML_PAGE) {
                Log.d(Constants.LOG.CTAG, Constants.LOG.DOW_NOTIFICATION);
                materialDialog = new MaterialDialog.Builder(dialogContext)
                        .title(title)
                        .content(description)
                        .positiveText(positiveText)
                        .negativeText(negativeText)
                        .neutralText(neutralText)
                        .cancelable(false)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                switch (which) {
                                    case POSITIVE:
                                        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));
                                        downloadRequest.setTitle(APK_NAME);
                                        downloadRequest.allowScanningByMediaScanner();
                                        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        downloadRequest.setVisibleInDownloadsUi(true);
                                        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_NAME);
                                        final DownloadManager manager = (DownloadManager) dialogContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                        assert manager != null;
                                        manager.enqueue(downloadRequest);
                                        break;
                                    case NEUTRAL:
                                        Uri webUri = Uri.parse(MORE_INFO);
                                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, webUri);
                                        dialogContext.startActivity(launchBrowser);
                                        break;
                                    case NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .build();
            } else {
                Log.d(Constants.LOG.CTAG, Constants.LOG.NDOW_NOTIFICATION);
                materialDialog = new MaterialDialog.Builder(dialogContext)
                        .title(title)
                        .content(description)
                        .positiveText(neutralText)
                        .negativeText(negativeText)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Uri webUri = Uri.parse(MORE_INFO);
                                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, webUri);
                                dialogContext.startActivity(launchBrowser);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .build();
            }
            materialDialog.show();
        }
    }

    private void getData() {
        NetworkConnection connection = new NetworkConnection();
        connection.execute(CONNECTION_URL);
        try {
            RETRIEVED_DATA = connection.get();
            int jsonLength = RETRIEVED_DATA.length();
            boolean noPreRelease = false;
            for (int i = 0; (i < jsonLength) && !noPreRelease; ++i) {
                if (!RETRIEVED_DATA.getJSONObject(i).getBoolean("prerelease")) {
                    noPreRelease = true;
                    LATEST_VERSION = RETRIEVED_DATA.getJSONObject(i).getString("tag_name");
                    boolean apkFound = false;
                    for (int j = 0; (j < RETRIEVED_DATA.getJSONObject(i).getJSONArray("assets").length()) && !apkFound; ++j) {
                        if (!(RETRIEVED_DATA.getJSONObject(i).getJSONArray("assets").isNull(j) && (RETRIEVED_DATA.getJSONObject(i).getJSONArray("assets").getJSONObject(j).getString("name").contains(".apk")))) {
                            apkFound = true;
                            DOWNLOAD_URL = RETRIEVED_DATA.getJSONObject(i).getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                            APK_NAME = RETRIEVED_DATA.getJSONObject(i).getJSONArray("assets").getJSONObject(j).getString("name");
                        }
                    }
                    if (DOWNLOAD_URL == null) {
                        HTML_PAGE = true;
                    }
                    MORE_INFO = RETRIEVED_DATA.getJSONObject(i).getString("html_url");
                }
            }
        } catch (InterruptedException | ExecutionException | JSONException | NullPointerException e) {
            Log.e(Constants.LOG.CTAG, Constants.LOG.NO_INFO + e.getMessage());
        }
    }
}