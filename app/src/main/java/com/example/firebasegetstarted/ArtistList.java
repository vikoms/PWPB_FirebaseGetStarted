package com.example.firebasegetstarted;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArtistList  extends ArrayAdapter<Artist> {

    private Activity context;
    private List<Artist> artistList;

    public ArtistList(Activity context, List<Artist> artistList) {
        super(context,R.layout.list_layout, artistList);
        this.context = context;
        this.artistList = artistList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater infalater = context.getLayoutInflater();
        View listViewItem = infalater.inflate(R.layout.list_layout, null, true);
        TextView txtViewName = (TextView) listViewItem.findViewById(R.id.txtName);
        TextView txtViewGenre  = (TextView) listViewItem.findViewById(R.id.txtGenre);

        Artist artist = artistList.get(position);

        txtViewName.setText(artist.getArtistName());
        txtViewGenre.setText(artist.getArtistGenre());

        return listViewItem;
    }
}


