package com.thisobeystudio.androidfloatingmenu.menu;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thisobeystudio.androidfloatingmenu.R;

import java.util.ArrayList;

/**
 * Created by thisobeystudio on 9/11/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

public class FloatingMenu {

    //private final String TAG = FloatingMenu.class.getSimpleName();

    /**
     * @param context           context
     * @param parent            parent ConstraintLayout
     * @param menuItemCallbacks menu callbacks
     * @param menuData          menu data
     */
    public void showFloatingMenu(final Context context,
                                 final ConstraintLayout parent,
                                 final FloatingMenuItemsAdapter.MenuItemCallbacks menuItemCallbacks,
                                 final ArrayList<FloatingMenuItem> menuData) {

        if (context == null
                || parent == null
                || menuItemCallbacks == null
                || menuData == null) {
            setVisible(false);
            return;
        }

        setParentConstraintLayout(parent);

        if (!isVisible()) {
            setVisible(true);
            setupFloatingMenuFrameLayout(context);
            setupRecyclerView(
                    context,
                    menuItemCallbacks,
                    menuData);
            setCardView();
            setHeaderTextView();
            setCancelableOnTouchOutside();
            ViewCompat.setElevation(getFrameLayout(), ViewCompat.getElevation(getCardView()));

            // menu dimens are based on dimens.xml if preventMenuBiggerThanParent(true)
            // will fix if at some point menu size is bigger than parent size
            preventMenuBiggerThanParent();

            getCardView().setOnClickListener(null);

            setHeaderTitle("Hello");
            setHeaderElevation(context);
            setHeaderOnClickListener(null);
        }

    }

    /**
     * @param context context
     */
    private void setupFloatingMenuFrameLayout(final Context context) {

        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);

        FrameLayout frameLayout = (FrameLayout) inflater
                .inflate(R.layout.floating_menu, getParentConstraintLayout(), false);

        setFrameLayout(frameLayout);

        getParentConstraintLayout().addView(getFrameLayout());
    }

    /**
     * @param context           host activity
     * @param menuItemCallbacks menu callbacks
     * @param menuData          menu data
     */
    private void setupRecyclerView(
            final Context context,
            final FloatingMenuItemsAdapter.MenuItemCallbacks menuItemCallbacks,
            final ArrayList<FloatingMenuItem> menuData) {

        final RecyclerView mRecyclerView =
                getParentConstraintLayout().findViewById(R.id.floating_menu_recycler_view);

        if (mRecyclerView == null) {
            throw new RuntimeException(
                    "No recycler view found with id = 'floating_menu_recycler_view'");
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        /*
        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false); // horizontal scroll
        SnapHelper snapHelper = new PagerSnapHelper(); // scroll item per item
        snapHelper.attachToRecyclerView(mRecyclerView);
        */
        final RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Makes scroll smoothly
        mRecyclerView.setNestedScrollingEnabled(false);

        // specify an adapter
        FloatingMenuItemsAdapter adapter = new FloatingMenuItemsAdapter(
                context,
                menuData);

        // set Adapter CallBacks
        adapter.setCallbacks(menuItemCallbacks);

        // set recyclerView adapter
        mRecyclerView.setAdapter(adapter);

        // add vertical item decoration
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // set recyclerView VISIBLE
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    /**
     * card view menu container
     */
    private CardView mCardView;

    /**
     * @return card view menu container
     */
    private CardView getCardView() {
        return mCardView;
    }

    /**
     * card view menu container
     */
    private void setCardView() {
        if (getFrameLayout() == null) return;
        this.mCardView = getFrameLayout().findViewById(R.id.menu_card_view);
    }

    /**
     * will check that passed width & height are not bigger than parent width & height
     * if so, will fix it/them, that way menu never will be bigger than parent
     *
     * @param width  menu width
     * @param height menu height
     */
    @SuppressWarnings("WeakerAccess")
    public void setMenuSize(final int width, final int height) {
        if (getParentConstraintLayout() == null || getCardView() == null) return;

        // using post to make sure measured width & height returns a proper value
        getParentConstraintLayout().post(() -> {

            int w = width;
            int h = height;

            // check that passed with is not bigger than parent width and fix it if so
            if (getParentConstraintLayout().getMeasuredWidth() != 0
                    && w > getParentConstraintLayout().getMeasuredWidth())
                w = getParentConstraintLayout().getMeasuredWidth();

            // check that passed height is not bigger than parent height and fix it if so
            if (getParentConstraintLayout().getMeasuredHeight() != 0
                    && h > getParentConstraintLayout().getMeasuredHeight())
                h = getParentConstraintLayout().getMeasuredHeight();

            ViewGroup.LayoutParams params = getCardView().getLayoutParams();
            params.width = w;
            params.height = h;
            getCardView().setLayoutParams(params);

        });

    }

    /**
     * will compare parent view size(width/height) to cardViews size
     * if any dimen is bigger than parent, that dimen will be set as same as parents dimen
     */
    private void preventMenuBiggerThanParent() {
        if (getParentConstraintLayout() == null || getCardView() == null) return;
        // using post to make sure measured width & height returns a proper value
        getCardView().post(() -> {

            int parentViewWidth = getParentConstraintLayout().getMeasuredWidth();
            int parentViewHeight = getParentConstraintLayout().getMeasuredHeight();
            int cardViewWidth = getCardView().getMeasuredWidth();
            int cardViewHeight = getCardView().getMeasuredHeight();

            // check that passed with is not bigger than parent width and fix it if so
            if (cardViewWidth > parentViewWidth)
                cardViewWidth = parentViewWidth;

            // check that passed height is not bigger than parent height and fix it if so
            if (cardViewHeight > parentViewHeight)
                cardViewHeight = parentViewHeight;

            ViewGroup.LayoutParams params = getCardView().getLayoutParams();
            params.width = cardViewWidth;
            params.height = cardViewHeight;
            getCardView().setLayoutParams(params);

        });
    }

    /**
     * @param width               menu width
     * @param height              menu height
     * @param menuCornerRadius    menu corner radius
     * @param menuBackgroundColor menu background color
     * @param menuElevation       menu elevation
     */
    @SuppressWarnings("unused")
    public void setMenuProperties(final int width,
                                  final int height,
                                  final float menuCornerRadius,
                                  final int menuBackgroundColor,
                                  final int menuElevation) {

        if (getParentConstraintLayout() == null || getCardView() == null) return;

        setMenuSize(width, height);
        setMenuCornerRadius(menuCornerRadius);
        setMenuBackGroundColor(menuBackgroundColor);
        setMenuElevation(menuElevation);
    }

    /**
     * @param radius add corner radius to menu
     */
    @SuppressWarnings("WeakerAccess")
    public void setMenuCornerRadius(float radius) {
        if (getCardView() == null) return;
        getCardView().setRadius(radius);
    }

    /**
     * @param color color for menu background view (the outside view of menu)
     */
    @SuppressWarnings("WeakerAccess")
    public void setMenuBackGroundColor(int color) {
        if (getFrameLayout() == null) return;
        getFrameLayout().setBackgroundColor(color);
    }

    /**
     * set Cancelable On Touch Outside
     */
    private void setCancelableOnTouchOutside() {

        if (isCancelableOnTouchOutside()) {
            getFrameLayout().setOnClickListener(view -> removeMenu());
        }

    }

    /**
     * menu visibility
     */
    private boolean isVisible;

    /**
     * @return menu visibility
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @param visible menu visibility
     */
    private void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * parent ConstraintLayout
     */
    private ConstraintLayout mParentConstraintLayout;

    /**
     * @param parentConstraintLayout parent ConstraintLayout
     */
    private void setParentConstraintLayout(ConstraintLayout parentConstraintLayout) {
        this.mParentConstraintLayout = parentConstraintLayout;
    }

    /**
     * @return parent ConstraintLayout
     */
    private ConstraintLayout getParentConstraintLayout() {
        return mParentConstraintLayout;
    }

    /**
     * menu container
     */
    private FrameLayout mFrameLayout;

    /**
     * @return menu container
     */
    private FrameLayout getFrameLayout() {
        return mFrameLayout;
    }

    /**
     * @param frameLayout menu container
     */
    private void setFrameLayout(FrameLayout frameLayout) {
        this.mFrameLayout = frameLayout;
    }

    /**
     * menu elevation
     */
    private int mMenuElevation;

    /**
     * @param menuElevation menu elevation
     */
    @SuppressWarnings("WeakerAccess")
    public void setMenuElevation(int menuElevation) {
        this.mMenuElevation = menuElevation;
    }

    /**
     * @return menu elevation
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public int getMenuElevation() {
        return mMenuElevation;
    }

    /**
     * header TextView
     */
    private TextView mHeaderTextView;

    /**
     * header TextView
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderTextView() {
        if (getParentConstraintLayout() == null) return;
        this.mHeaderTextView =
                getParentConstraintLayout().findViewById(R.id.floating_menu_header_text_view);
    }

    /**
     * @return header TextView
     */
    @SuppressWarnings("WeakerAccess")
    public TextView getHeaderTextView() {
        return mHeaderTextView;
    }

    /**
     * @param onClickListener header onClickListener
     */
    public void setHeaderOnClickListener(@SuppressWarnings("SameParameterValue")
                                                 View.OnClickListener onClickListener) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setOnClickListener(onClickListener);
    }

    /**
     * @param height header title height
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderHeight(int height) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setHeight(height);
        ViewGroup.LayoutParams params = getHeaderTextView().getLayoutParams();
        params.height = height;
        getHeaderTextView().setLayoutParams(params);
    }

    /**
     * header TextView padding
     *
     * @param left   left padding
     * @param top    top padding
     * @param right  right padding
     * @param bottom bottom padding
     */
    @SuppressWarnings("unused")
    public void setHeaderPadding(int left, int top, int right, int bottom) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setPadding(left, top, right, bottom);
    }

    /**
     * header TextView padding
     *
     * @param padding left, top, right and bottom padding all at once
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderPadding(int padding) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setPadding(padding, padding, padding, padding);
    }

    /**
     * @param color header title text color
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderTitleColor(int color) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setTextColor(color);
    }

    /**
     * @param color header background color
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderBackgroundColor(int color) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setBackgroundColor(color);
    }

    private void setHeaderElevation(Context context) {
        float elevation =
                context.getResources().getDimensionPixelSize(R.dimen.menu_header_elevation);
        ViewCompat.setElevation(getHeaderTextView(), elevation);
    }

    /**
     * @param titleColor      header title text color
     * @param backgroundColor header background color
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderTitleAndBackgroundColor(int titleColor, int backgroundColor) {
        if (getHeaderTextView() == null) return;
        setHeaderTitleColor(titleColor);
        setHeaderBackgroundColor(backgroundColor);
    }

    /**
     * @param text menu title text
     *             showHeader(false) will hide menu's header event using setHeaderTitle(title);
     */
    @SuppressWarnings("WeakerAccess")
    public void setHeaderTitle(String text) {
        if (getHeaderTextView() == null) return;
        getHeaderTextView().setVisibility(View.VISIBLE);
        getHeaderTextView().setText(text);
    }

    /**
     * @param text            header title text
     * @param height          header height
     * @param padding         header padding
     * @param titleColor      header title color
     * @param backgroundColor header background color
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setHeader(
            String text,
            int height,
            int padding,
            int titleColor,
            int backgroundColor) {

        if (getHeaderTextView() == null) return;

        showHeader(true);
        setHeaderTitle(text);
        setHeaderHeight(height);
        setHeaderPadding(padding);
        setHeaderTitleAndBackgroundColor(titleColor, backgroundColor);
    }

    /**
     * @param show false to hide menu header
     */
    @SuppressWarnings("WeakerAccess")
    public void showHeader(@SuppressWarnings("SameParameterValue") boolean show) {
        if (getHeaderTextView() == null) return;
        if (show)
            getHeaderTextView().setVisibility(View.VISIBLE);
        else
            getHeaderTextView().setVisibility(View.GONE);
    }

    /**
     * remove menu and update visibility
     */
    public void removeMenu() {
        if (getParentConstraintLayout() == null || getFrameLayout() == null) return;
        getParentConstraintLayout().removeView(getFrameLayout());
        setVisible(false);
    }

    /**
     * handle on Back Pressed removes menu if true
     * default is true
     */
    private boolean isCancelable = true;

    /**
     * @return cancelable on Back Pressed
     */
    public boolean isCancelable() {
        return isCancelable;
    }

    /**
     * @param cancelable cancelable on Back Pressed
     */
    @SuppressWarnings("unused")
    public void setCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
    }

    /**
     * handle On Touch Outside removes menu if true
     * default is true
     */
    private boolean isCancelableOnTouchOutside = true;

    /**
     * if true menu will be removed on any out of menu touch, must be set before showFloatingMenu()
     *
     * @param cancelable cancelable On Touch Outside
     */
    @SuppressWarnings("unused")
    public void setCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelableOnTouchOutside = cancelable;
    }

    /**
     * @return cancelable On Touch Outside
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isCancelableOnTouchOutside() {
        return isCancelableOnTouchOutside;
    }

}
