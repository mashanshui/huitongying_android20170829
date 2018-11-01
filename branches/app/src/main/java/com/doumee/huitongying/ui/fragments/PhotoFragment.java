package com.huixiangshenghuo.app.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import uk.co.senab.photoview.PhotoView;


public class PhotoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";


    private String imagePath;

    //===========================
//    private Bitmap defaultImage;
    //===========================
    public PhotoFragment() {

    }

    public static PhotoFragment newInstance(String param1) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PhotoView photoView = new PhotoView(this.getActivity());
        ImageLoader.getInstance().displayImage(imagePath,photoView);
        //===========================================================
//        PhotoView photoView = new PhotoView(this.getActivity());
//        //默认图片
//        defaultImage = BitmapFactory.decodeResource(this.getResources(), R.mipmap.business_default);
//        if (TextUtils.isEmpty(imagePath)) {
//            photoView.setImageBitmap(defaultImage);
//        } else {
//            photoView.setImageBitmap(defaultImage);
//            ImageLoader.getInstance().displayImage(imagePath,photoView);
//        }
        //===========================================================
        return photoView;
    }

}
