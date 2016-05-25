package com.example.moran_lap.projbitmapv11;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gili on 08/04/2016.
 */
public class Composer extends Thread {

    private Bitmap mBitmap;
    private ImageView mImageView;
    private ArrayList<SurfaceComponent> mSurfaceComponents;
    private Object mObj;

    public Composer(){

        mSurfaceComponents = new ArrayList();
        mObj = new Object();
        initPreview();
    }

    private void initPreview(){
        mImageView = (ImageView)ApplicationContext.getActivity().findViewById(R.id.imageView);
        initBitmap();

    }

    void initBitmap(){
        mBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.BLACK);
        mImageView.setImageBitmap(mBitmap);
        //mImageView.invalidate();
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public ArrayList<SurfaceComponent> getmSurfaceComponents() {
        return mSurfaceComponents;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

//    public void run() {
//        try {
//            synchronized (mObj) {
//                mObj.wait();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        while (true) {
//            if (mSurfaceComponents != null) {
//                for (SurfaceComponent surfaceComponent : mSurfaceComponents) {
//                    if (surfaceComponent.isEnabled()) {
//                        surfaceComponent.start();
//                    }
//                }
//                for (SurfaceComponent surfaceComponent : mSurfaceComponents) {
//                    try {
//                        surfaceComponent.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//        /*
//        for(int i=0; i<1280; i++)
//            for(int j=0; j<720; j++) {
//                for (SurfaceComponent surfaceComponent : mSurfaceComponents) {
//                    if (surfaceComponent.isEnabled()) {
//                        if (i>=)
//                    }
//                }
//            }
//            */
//                for (SurfaceComponent surfaceComponent : mSurfaceComponents) {
//                    if (surfaceComponent.isEnabled()) {
//                       // paint.setColor(Color.GREEN);
//                       // canvas.drawRect(20F, 300F, 180F, 400F, paint);
//                        //mBitmap=surfaceComponent.getBitmap();
//                    }
//                }
//            }
//            try {
//                sleep(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}


