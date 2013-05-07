package edu.vanderbilt.vm.guide.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.widget.ImageView;

public class ImageDownloader {
    
    private static ImageLoader mImgLoader = null; // TODO add credit 
    
    public static void initImageLoader(Context ctx) {
        if (mImgLoader == null) {
            mImgLoader = ImageLoader.getInstance();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                    .threadPoolSize(5)
                    .defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                            .cacheInMemory()
                            .cacheOnDisc()
                            .build())
                    .build();
            
            mImgLoader.init(config);
        }
    }
    
    public static void dispatchImage(String mediaName, ImageView view) {
        
        mImgLoader.displayImage(mediaName, view);

    }
    
}
