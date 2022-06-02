package listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.smart.agriculture.solutions.vechicle.vehicletracker.BuildConfig;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import activity.Login;
import util.Common;
import util.CommonConst;

public class SplashScreenListener implements Animation.AnimationListener {
    private Activity activity;
    private ProgressBar progressBar;
    String TAG = ">>>" + SplashScreenListener.class.getSimpleName();

    public SplashScreenListener(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress += 1) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(progress);

                if (progress == 100) {
                    try {
                        Thread.sleep(250);
                        activity.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.startActivity(new Intent(activity, Login.class));

//                    versionCheck(activity);
                }
            }
        }).start();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    public void versionCheck(Activity context) {
        VersionChecker versionChecker = new VersionChecker();
        try {
            String appVersionName = BuildConfig.VERSION_NAME;
            String mLatestVersionName = versionChecker.execute().get();
            Common.logd(TAG, mLatestVersionName + " : " + appVersionName);
            if (mLatestVersionName.equals("void")) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else if (!compareVersion(appVersionName, mLatestVersionName)) {
                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                builder1.setMessage(R.string.app_version_msg);
                builder1.setCancelable(true);
                android.app.AlertDialog alert11 = builder1.create();
                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(activity, Login.class));

                    }
                });
                alert11.show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VersionChecker extends AsyncTask<Void, Void, String> {

        private String newVersion = "void";

        @Override
        protected String doInBackground(Void... params) {

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.smart.agriculture.solutions.vechicle.vehicletracker" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
                Common.logd(TAG, newVersion);
                return newVersion;
            } catch (IOException e) {
                e.printStackTrace();
                Common.logd(TAG, "error : " + e.getMessage());
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private boolean compareVersion(String currrentsVersion, String officialVersion) {
        boolean isEqual = false;

        String[] string1 = currrentsVersion.split("[.]");
        String[] string2 = officialVersion.split("[.]");
        Integer[] number2 = new Integer[string2.length];

        Integer[] numbers = new Integer[string2.length];


        for (int i = 0; i < string2.length; i++) {
            if (string1.length - 1 < i)
                numbers[i] = 0;
            else
                numbers[i] = Integer.parseInt(string1[i]);
            Common.logd(TAG, "number1 ::: " + numbers[i]);
        }


        for (int i = 0; i < string2.length; i++) {
            number2[i] = Integer.parseInt(string2[i]);
            Common.logd(TAG, "number2 ::: " + number2[i]);
        }

        for (int i = 0; i < number2.length; i++) {
            if (number2[i] > (numbers[i])) {
                isEqual = false;
                break;
            } else {
                isEqual = true;
            }
        }
        return isEqual;
    }

}
