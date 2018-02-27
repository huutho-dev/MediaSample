package com.htn.samplefragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.htn.samplefragment.pager.AudioFragment;
import com.htn.samplefragment.pager.ImageFragment;
import com.htn.samplefragment.pager.VideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThoNh on 2/27/2018.
 */

public class MediaPagerAdapter extends FragmentStatePagerAdapter {

    List<MediaEntity> mMediaEntities = new ArrayList<>();

    public MediaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<MediaEntity> mMediaEntities) {
        this.mMediaEntities = mMediaEntities;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (mMediaEntities.get(position).mType) {
            case "audio":
                return AudioFragment.newInstance(mMediaEntities.get(position));
            case "video":
                return VideoFragment.newInstance(mMediaEntities.get(position));
            case "image":
                return ImageFragment.newInstance(mMediaEntities.get(position));

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mMediaEntities.size();
    }
}