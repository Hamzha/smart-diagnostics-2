package listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import model.Position;
import util.Common;
import util.CommonConst;

public class RouteToButtonListener implements View.OnClickListener {
    private Activity context;
    private Position position;

    public RouteToButtonListener(Activity activity, Position positionTmp) {
        this.context = activity;
        this.position = positionTmp;
    }

    @Override
    public void onClick(View view) {
        if (Common.isInternetConnected(context)) {
            Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
        } else {
            Uri.Builder viewUri = new Uri.Builder().scheme("https").authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1");
            String str = "destination";
            String stringBuilder = position.getLatitude() +
                    "," +
                    position.getLongitude();
            context.startActivity(new Intent("android.intent.action.VIEW", viewUri.appendQueryParameter(str, stringBuilder).build()));
        }
    }
}
