package com.example.moran_lap.projbitmapv11;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private Composer mComposer;

    // ImageView - Preview
    private ImageView mImageView;
    private static int RESULT_LOAD_IMG = 1;

    // DragNDropListView - SurfaceComponents with checkboxes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static SurfaceComponentAdapter SCadapter;
    private ArrayList<SurfaceComponent> mSurfaceComponents;

    // Buttons - StreamButton and plus button to add SurfaceComponents
    private Button mStreamButton;
    private Boolean startStream = true;
    private FloatingActionButton fab;

    CameraSource CameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set MainActivity to be the global context of this application
        ApplicationContext.setActivity(this);

        // Initialize View components
        mImageView = (ImageView)findViewById(R.id.imageView);
        CameraSource = new CameraSource();
        //final Composer mComposer = new Composer();
        mComposer = new Composer();
        mSurfaceComponents = mComposer.getmSurfaceComponents();
        // Start Composer Thread to work - consider activate on first addition of SurfaceComponent or wait and signal
        //((Thread)mComposer).start();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        SCadapter = new SurfaceComponentAdapter(mSurfaceComponents,this);
        mRecyclerView.setAdapter(SCadapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(SCadapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mStreamButton = (Button) findViewById(R.id.streamButton);
        mStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startStream)
                    mStreamButton.setText("Stop Stream");
                else
                    mStreamButton.setText("Start Stream");
                startStream = !startStream;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, fab);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.plus_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case (R.id.camera_source) :
                                mSurfaceComponents.add(new SurfaceComponent(CameraSource,new Position()));
                                //paint.setColor(Color.GREEN);
                                //canvas.drawRect(20F, 300F, 180F, 400F, paint); // left top right bottom
                                //mImageView.invalidate();
                                //mImageView.refreshDrawableState();
                                break;
                            case (R.id.image_source) :
                                loadImageFromGallery();
                                break;
                            case (R.id.text_source) :
                                String text = "Test Text Source";
                                Position pos = new Position(20, 500, 180, 400);
                                ImageSource textSource = new TextSource(text,pos);
                                mSurfaceComponents.add(new SurfaceComponent(textSource,pos));
                                break;
                            case (R.id.screen_source) :
                                SurfaceComponent screenComponent = new SurfaceComponent(new ScreenSource(),new Position());
                                //screenComponent.Enable();
                                mSurfaceComponents.add(screenComponent);
                                //synchronized (mObj) {
                                //    mObj.notify();
                                //}
                                break;
                        }
                        onListViewChanged();
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }

    public void loadImageFromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst(); // Move to first row
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                File image = new File(imgDecodableString);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                PictureSource pictureSource = new PictureSource();
                //set the original source bitmap with imageBitmap
                pictureSource.setOriginalSourceBitmap(imageBitmap);
                Position pos = new Position(0,100,0,100);
                SurfaceComponent pictureComponent = new SurfaceComponent(pictureSource,pos);
                mSurfaceComponents.add(pictureComponent);
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap,pos.getWidth(),pos.getHeight(),true);
                //set the surface component bitmap with imageBitmap
                pictureComponent.setSurfaceComponentBitmap(imageBitmap);
                ((MainActivity)ApplicationContext.getActivity()).onListViewChanged();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onListViewChanged(){
        refreshSurfaceComponentsOnBitmap();
    }

    public void refreshSurfaceComponentsOnBitmap(){
        SCadapter.swap((ArrayList<SurfaceComponent>) mSurfaceComponents.clone());
        mComposer.initBitmap();
        ArrayList<SurfaceComponent> reversedSurfaceComponents = new ArrayList<>(mSurfaceComponents);
        Collections.reverse(reversedSurfaceComponents);
        for (SurfaceComponent sc : reversedSurfaceComponents){
            if (sc.isEnabled()){
                Bitmap bitmapToDraw = sc.DrawSurfaceComponentOnBitmap();
                Canvas canvas = new Canvas(mComposer.getBitmap());
                canvas.drawBitmap(bitmapToDraw, sc.getImagePositionOnSurface().getxStart(), sc.getImagePositionOnSurface().getyStart(), null);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
