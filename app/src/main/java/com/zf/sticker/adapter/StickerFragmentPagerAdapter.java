package com.zf.sticker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zf.sticker.ui.StickerFragment;

import java.util.List;

/**
 *
 * @author Allever
 * @date 2017/12/31
 */

public class StickerFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<StickerFragment> mStickerFragment;
    public StickerFragmentPagerAdapter(FragmentManager fragmentManager, List<StickerFragment> stickerFragmentList){
        super(fragmentManager);
        mStickerFragment = stickerFragmentList;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mStickerFragment.get(position).getSticker().getTitle();
    }

    @Override
    public int getCount() {
        return mStickerFragment.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mStickerFragment.get(position);
    }
}
