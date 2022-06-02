package adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import org.jetbrains.annotations.NotNull;

public class CustomPagerAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;

    private int[] mResources = {
            R.drawable._1,
            R.drawable._2,
            R.drawable._3,
            R.drawable._4,
            R.drawable._5,
            R.drawable._6,
            R.drawable._7,
            R.drawable._8,
            R.drawable._9,
            R.drawable._10,
            R.drawable._11,
            R.drawable._12,
            R.drawable._13,
            R.drawable._14,
            R.drawable._15,
            R.drawable._16,
            R.drawable._17,
            R.drawable._18,
            R.drawable._19,
            R.drawable._20,
            R.drawable._21,
            R.drawable._22,
            R.drawable._23,
            R.drawable._24,
            R.drawable._25,
            R.drawable._26,
            R.drawable._27,
            R.drawable._28,
            R.drawable._29,
            R.drawable._30,
    };

    public CustomPagerAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mResources.length;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull final ViewGroup container, int position) {
        @SuppressLint("ResourceType") View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        final ImageView imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageResource(mResources[position]);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
