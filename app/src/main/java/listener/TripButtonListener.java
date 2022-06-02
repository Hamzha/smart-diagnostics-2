package listener;

import android.app.Activity;
import android.view.View;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import activity.Trip;
import model.Device;
import util.Common;
import util.CommonConst;
import util.StaticFunction;

@Deprecated
public class TripButtonListener implements View.OnClickListener {
    private Activity context;
    private Device device;

    public TripButtonListener(Activity context, Device device) {
        this.context = context;
        this.device = device;
    }

    @Override
    public void onClick(View view) {
        if (Common.isInternetConnected(context)) {
            Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
        } else {
            StaticFunction.SingleChoiceDialogAndButtonAction(device, context, Trip.class);
        }
    }
}