package com.htn.samplefragment.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.htn.samplefragment.MediaEntity;
import com.htn.samplefragment.R;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class ImageFragment extends Fragment {
    public static final String TAG = ImageFragment.class.getSimpleName();
    public static final String EXTRA_MEDIA = "EXTRA_MEDIA";

    public static ImageFragment newInstance(MediaEntity entity) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDIA, entity);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean mIsLoaded = false;

    private MediaEntity mMediaEntity;

    private ImageView mImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(false);
        super.onCreate(savedInstanceState);
        mMediaEntity = getArguments().getParcelable(EXTRA_MEDIA);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_image, container, false);
        mImageView = view.findViewById(R.id.mImageView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        fillContent();
    }

    /*Fragment Active in Here if meniVisible=true*/
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        fillContent();
        Log.e(TAG, "setMenuVisibility -> " + menuVisible);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
    }

    public void fillContent(){
        if (!mIsLoaded || (mImageView != null && mImageView.getDrawable() == null)) {
            if (getContext() != null){
                Glide.with(getContext()).load(mMediaEntity.mUrlContent).into(mImageView);
                mIsLoaded = true;
            }
        }
    }
}
