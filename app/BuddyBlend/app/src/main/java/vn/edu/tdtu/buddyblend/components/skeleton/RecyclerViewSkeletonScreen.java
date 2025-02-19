package vn.edu.tdtu.buddyblend.components.skeleton;


import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.tdtu.buddyblend.R;


public class RecyclerViewSkeletonScreen implements SkeletonScreen {

    private final RecyclerView mRecyclerView;
    private final RecyclerView.Adapter mActualAdapter;
    private final SkeletonAdapter mSkeletonAdapter;
    private final boolean mRecyclerViewFrozen;

    private RecyclerViewSkeletonScreen(Builder builder) {
        mRecyclerView = builder.mRecyclerView;
        mActualAdapter = builder.mActualAdapter;
        mSkeletonAdapter = new SkeletonAdapter();
        mSkeletonAdapter.setItemCount(builder.mItemCount);
        mSkeletonAdapter.setLayoutReference(builder.mItemResID);
        mSkeletonAdapter.setArrayOfLayoutReferences(builder.mItemsResIDArray);
        mSkeletonAdapter.shimmer(builder.mShimmer);
        mSkeletonAdapter.setShimmerColor(builder.mShimmerColor);
        mSkeletonAdapter.setShimmerAngle(builder.mShimmerAngle);
        mSkeletonAdapter.setShimmerDuration(builder.mShimmerDuration);
        mRecyclerViewFrozen = builder.mFrozen;
    }

    @Override
    public void show() {
        mRecyclerView.setAdapter(mSkeletonAdapter);
        if (!mRecyclerView.isComputingLayout() && mRecyclerViewFrozen) {
            mRecyclerView.setLayoutFrozen(true);
        }
    }

    @Override
    public void hide() {
        mRecyclerView.setAdapter(mActualAdapter);
    }

    public static class Builder {
        private RecyclerView.Adapter mActualAdapter;
        private final RecyclerView mRecyclerView;
        private boolean mShimmer = true;
        private int mItemCount = 10;
        private int mItemResID;
        private int[] mItemsResIDArray;
        private int mShimmerColor;
        private int mShimmerDuration = 1000;
        private int mShimmerAngle = 20;
        private boolean mFrozen = true;

        public Builder(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            this.mShimmerColor = ContextCompat.getColor(recyclerView.getContext(), R.color.shimmer_color);
        }

        public Builder adapter(RecyclerView.Adapter adapter) {
            this.mActualAdapter = adapter;
            return this;
        }

        public Builder count(int itemCount) {
            this.mItemCount = itemCount;
            return this;
        }

        public Builder shimmer(boolean shimmer) {
            this.mShimmer = shimmer;
            return this;
        }

        public Builder duration(int shimmerDuration) {
            this.mShimmerDuration = shimmerDuration;
            return this;
        }

        public Builder color(@ColorRes int shimmerColor) {
            this.mShimmerColor = ContextCompat.getColor(mRecyclerView.getContext(), shimmerColor);
            return this;
        }

        public Builder angle(@IntRange(from = 0, to = 30) int shimmerAngle) {
            this.mShimmerAngle = shimmerAngle;
            return this;
        }

        public Builder load(@LayoutRes int skeletonLayoutResID) {
            this.mItemResID = skeletonLayoutResID;
            return this;
        }


        public Builder loadArrayOfLayouts(@ArrayRes int[] skeletonLayoutResIDs) {
            this.mItemsResIDArray = skeletonLayoutResIDs;
            return this;
        }

        public Builder frozen(boolean frozen) {
            this.mFrozen = frozen;
            return this;
        }

        public RecyclerViewSkeletonScreen show() {
            RecyclerViewSkeletonScreen recyclerViewSkeleton = new RecyclerViewSkeletonScreen(this);
            recyclerViewSkeleton.show();
            return recyclerViewSkeleton;
        }
    }
}
