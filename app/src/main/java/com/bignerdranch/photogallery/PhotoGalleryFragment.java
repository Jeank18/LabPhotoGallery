package com.bignerdranch.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader; // se crea en página 505

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true); // se crea en página 528
     //   new FetchItemsTask().execute(); se quita en página 531
        updateItems(); // se crea en página 531

        Handler responseHandler = new Handler(); // cambio de página 515
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler); //se crea en página 505
        mThumbnailDownloader.setThumbnailDownloadListener( // cambio de página 515
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>(){ // cambio de página 515
                    @Override // cambio de página 515
                    public void onThumbnailDownloaded(PhotoHolder photoHolder,
                                                      Bitmap bitmap){ // cambio de página 515
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);// cambio de página 515
                        photoHolder.bindDrawable(drawable);
                    }
                }
        ); // cambio de página 515, hasta aquí
        mThumbnailDownloader.start(); //se crea en página 505
        mThumbnailDownloader.getLooper(); //se crea en página 505
        Log.i(TAG, "Background thread started"); //se crea en página 505
    }

    private class PhotoHolder extends  RecyclerView.ViewHolder {
        // private TextView mTitleTextView;
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.gallery_item_imageView);
           // mTitleTextView = (TextView) itemView;
        }
        /*public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }*/

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
           //TextView textView = new TextView(getActivity());
           //return new PhotoHolder(textView);
             LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {
           GalleryItem galleryItem = mGalleryItems.get(position);
            // photoHolder.bindGalleryItem(galleryItem); se cambia en página 501
            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getmUrl()); // Se cambia en página 506 aquí había problema con el getUrl, lo cambié por getmUrl

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
           // return new FlickrFetchr().fetchItems(); se quita en página 525
            String query = "robot"; // solamente para probar en pagina 525

            if (query == null){ // cambios de página 525
                return new FlickrFetchr().fetchRecentPhotos(); // cambios de página 525
            }else{
                return new FlickrFetchr().searchPhotos(query); // cambios de página 525
            }
        } // cambios de página 525, hasta aquí

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }


    }

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView= v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return v;
    }

    @Override // cambios de página 517
    public void onDestroyView(){ // cambios de página 517
        super.onDestroyView(); // cambios de página 517
        mThumbnailDownloader.clearQueue(); // cambios de página 517
    }

    @Override // se crea en página 505
    public void onDestroy(){ //se crea en página 505
        super.onDestroy(); //se crea en página 505
        mThumbnailDownloader.quit(); //se crea en página 505
        Log.i(TAG, "Background thread destroyed"); //se crea en página 505
    }

    @Override// se crea en página 528
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search); // se crea en página 530
        final SearchView searchView = (SearchView) searchItem.getActionView(); // se crea en página 530

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // se crea en página 530
            @Override // se crea en página 530
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                updateItems();
                return true;
            }

            @Override // se crea en página 530
            public boolean onQueryTextChange(String s){
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        }); // se crea en página 530 hasta aquí
    }

    private void updateItems(){
        new FetchItemsTask().execute();
    }

    private void setupAdapter() {
        if(isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
}
