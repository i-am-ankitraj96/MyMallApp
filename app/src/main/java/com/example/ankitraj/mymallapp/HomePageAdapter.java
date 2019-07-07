package com.example.ankitraj.mymallapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
    }

    // following 4 overridden methods as we have extended from RecyclerView.Adapter

    // returns item View Type
    // it runs before layout created so that we know which type of layout is created
    // so that we can inflate accordingly
    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {

            case 0: // BannerSlider
                return HomePageModel.BANNER_SLIDER;
            case 1: // Strip Ad
                return HomePageModel.STRIP_AD_BANNER;
            case 2: // Horizontal Product View
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3: // Grid Product View
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;

        }
    }

    // once we know the type of layout from above method
    // we shall inflate accordingly here in this method using switch statement
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View viewBannerSlider = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(viewBannerSlider);  // see the BannerSlider class created below

            case HomePageModel.STRIP_AD_BANNER:
                View viewStripAd = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdBannerViewHolder(viewStripAd);  // see the BannerSlider class created below

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View viewHorizontalProduct = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(viewHorizontalProduct);  // see the BannerSlider class created below

            case HomePageModel.GRID_PRODUCT_VIEW:
                View viewGridProduct = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(viewGridProduct);  // see the BannerSlider class created below


            default:
                return null;

        }

    }

    //this method also run according to the type of view
    // here binding of data is done in each view of  each layout inflated
    // pass the list
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //bind the data
        switch (homePageModelList.get(position).getType()) {

            case HomePageModel.BANNER_SLIDER:
                // do data binding of BannerSlider

                // take out list from Home Page List
                // we access this from home page(main list)
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                // access viewHolder via casting
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;

            case HomePageModel.STRIP_AD_BANNER:
                // access resource and color
                // resource type var accessed via List
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                // cast the viewHolder
                ((StripAdBannerViewHolder) holder).setStripAd(resource,color);
                break;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                // access title and List
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();
                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                List<WishListModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                // cast the viewHolder
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList,horizontalLayoutTitle,layoutColor,viewAllProductList);
                break;

            case HomePageModel.GRID_PRODUCT_VIEW:
                // access title and List
                String gridLayoutTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder)holder).setGridProductLayout(gridProductScrollModelList,gridLayoutTitle);
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    //hardcoded class for BannerSlider
    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {


        // access all views like textView,ImageView,etc. over here

        private ViewPager bannerSliderViewPager;
        // access the List via model
        private int currentPage = 2; // initial value 2 as the starting page is at index 2
        // refer the sliderModel.add()  method below

        private Timer timer;        // timer to help change Banners Sliders according to change of time
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;  // these two constants created for the task in Thread created below     /////////   Banner Slider

        private List<SliderModel> arrangeList;

        public BannerSliderViewHolder(View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }

        // to dynamically access the modelList
        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {


            arrangeList = new ArrayList<>();
            for(int x = 0; x<sliderModelList.size();x++){
                arrangeList.add(x,sliderModelList.get(x));
            }

            int sz_List = sliderModelList.size();
            if(sz_List>=2)
            arrangeList.add(0,sliderModelList.get(sliderModelList.size()-2));
            if(sz_List>=1)
            arrangeList.add(0,sliderModelList.get(sliderModelList.size()-1));
            if(sz_List>=1)
            arrangeList.add(sliderModelList.get(0));
            if(sz_List>=2)
            arrangeList.add(sliderModelList.get(1));


            // create adapter for Slide show
            SliderAdapter sliderAdapter = new SliderAdapter(arrangeList);
            //  add adapter  to ViewPager
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            // set current Page
            bannerSliderViewPager.setCurrentItem(currentPage);

            // call the automatic slide Show
            // the method is hard coded below
            startBannerSlideShow(arrangeList);

            // cancel slide show when user touches that slide

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // 1st call PageLooper
                    pageLooper(arrangeList);
                    // stop automatic slide Show
                    stopBannerSlideShow();

                    // start again Slide Show of Banners when user lifts his touch up
                    if (event.getAction() == MotionEvent.ACTION_UP) {   // ACTION_UP means user has lifted his finger up
                        // thus again start the automatic slideShow
                        startBannerSlideShow(arrangeList);
                    }
                    return false;
                }
            });

            // page Listener method

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //set value of current Page
                    currentPage = position;

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    // first identify state of ViewPager

                    if (state == ViewPager.SCROLL_STATE_IDLE) {  // i.e., not scrolling at the moment

                        pageLooper(arrangeList);  // call page looper created method when state is idle so that it appears as if changed in continuation as page ends

                    }

                }
            };


            // add the above created listener to the BannerPager Slider

            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);


        }

        // method to infinetly loop ViewPager banners
        // infinitely loops 1st banner with the last one so that user can infinitely loop the banners
        // these are hard coded methods to add speciality function to the UI
        private void pageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false); // animation et to false so that user doesnt get the feel that he actually shifted from different banner with same Image
            }

            if (currentPage == 1) { //same behaviour as above but user scrolls back
                currentPage = sliderModelList.size() - 1;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        // method to slide the banners without user
        private void startBannerSlideShow(final List<SliderModel> sliderModelList) {
            // create new Thread
            // run animation via time access

            final Handler handler = new Handler();
            final Runnable update = new Runnable() {   // add Task here to be run by the thread
                @Override
                public void run() {
                    // after each specific time , Banner must change
                    if (currentPage >= sliderModelList.size()) {  // i.e., currentPage  variable overFlows size of the sliderModelList
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true); //  animation is true
                }
            };

            // define Timer
            timer = new Timer(); // asssign Timer

            // we have to pass two times  delay time and interval time
            // i.e., time gap b/w two tasks and the delay is time elasped before another task starts

            timer.schedule(new TimerTask() {  // adding task to timer
                @Override
                public void run() {
                    handler.post(update);   // pass the Task to be run
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        // method to stop banner Slide Show
        private void stopBannerSlideShow() {
            // cancel timer created above
            timer.cancel();
        }
    }

    //hardcoded class for StripAd
    public class StripAdBannerViewHolder extends RecyclerView.ViewHolder {

        // access all views
        private ImageView stripAdImage;
        private ConstraintLayout stripAdContainer;

        //constructor
        public StripAdBannerViewHolder(View itemView) {
            super(itemView);
            // assign all views
            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
            stripAdContainer = itemView.findViewById(R.id.strip_add_container);
        }

        //set image and background color
        private void setStripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource)
                    .apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(stripAdImage);
            stripAdImage.setBackgroundColor(Color.parseColor(color));
        }
    }

    //hardcoded class for Horizontal Product Scroll
    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout container;
        private TextView horizontal_layout_title;
        private Button horizontal_viewAllButton;
        private RecyclerView horizontalRecyclerView;

        //constructor
        public HorizontalProductViewHolder(View itemView) {
            super(itemView);
            // access all views in this layout
            container = itemView.findViewById(R.id.container);
            horizontal_layout_title = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontal_viewAllButton = itemView.findViewById(R.id.horizontal_scroll_layout_button);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_scroll_layout_recycler_view);

        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String color, final List<WishListModel> viewAllProductList){
            //horizontal_viewAllButton.setVisibility(View.GONE);
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            // set Title
            horizontal_layout_title.setText(title);

            // to show only 8 items
            // if more than that then enable the "show All " button
            if(horizontalProductScrollModelList.size()>8){
                horizontal_viewAllButton.setVisibility(View.VISIBLE);
                horizontal_viewAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishListModelList = viewAllProductList ;
                        Intent viewAllIntent = new Intent(itemView.getContext(),ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("title",title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            }else{
                horizontal_viewAllButton.setVisibility(View.INVISIBLE);
            }

            // create adapter
            // pass the above created list in the adapter

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);

            //  create a layout Manager on recycler View

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            // set orientation
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            //set Layout Manager on recyclerView
            horizontalRecyclerView.setLayoutManager(linearLayoutManager);

            // apply notify changes on adapter
            horizontalProductScrollAdapter.notifyDataSetChanged();

            // set Adapter

            horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
        }
    }

    //hardcoded class for Grid View
    public class GridProductViewHolder extends  RecyclerView.ViewHolder {

        // all views in a placeholder for the grid View
        private TextView gridLayoutTitle;
        private Button gridLayoutViewAllButton;
        private GridView gridView;

        public GridProductViewHolder(View itemView) {
            super(itemView);
            // access all items in a Grid View place holder
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            gridLayoutViewAllButton = itemView.findViewById(R.id.grid_product_layout_viewall_button);
            gridView = itemView.findViewById(R.id.grid_product_layout_gridview);

        }

        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title){
            // set Title
            gridLayoutTitle.setText(title);
            // set Adapter
            gridView.setAdapter(new GridProductLayoutAdapter(horizontalProductScrollModelList));

            gridLayoutViewAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                    Intent viewAllIntent = new Intent(itemView.getContext(),ViewAllActivity.class);
                    viewAllIntent.putExtra("layout_code",1);
                    viewAllIntent.putExtra("title",title);
                    itemView.getContext().startActivity(viewAllIntent);

                }
            });

        }
    }
}
