package com.unl.addressvalidator.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.unl.addressvalidator.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> images = new ArrayList<String>();

    LayoutInflater mLayoutInflater;

    public ImageAdapter(Context context) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.dashboard_banner_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
      //  imageView.setImageResource(GalImages[position]);
        Glide.with(imageView).load(images.get(position)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Utility.Companion.addEvent(context, "Banner Click", "Dashboard");
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("title", "NextFin");
                intent.putExtra("url", URL[position]);
                context.startActivity(intent);*/
            }
        });
        container.addView(itemView);

        return itemView;
    }

    public void setData(ArrayList<String> imageslist)
    {
        images = imageslist;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}