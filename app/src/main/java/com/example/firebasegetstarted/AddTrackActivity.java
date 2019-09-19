package com.example.firebasegetstarted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    TextView txtViewArtistName;
    EditText editTextTrack;
    SeekBar seekBarRating;
    Button btnAddTrack;
    ListView listViewTracks;
    List<Track> trackList;

    DatabaseReference databaseTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        listViewTracks = (ListView) findViewById(R.id.container_listView_track);
        txtViewArtistName = (TextView) findViewById(R.id.textViewArtistName);
        editTextTrack = (EditText) findViewById(R.id.editTextTrackName);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);
        btnAddTrack = (Button) findViewById(R.id.btnAddTrack);
        Intent intent = getIntent();

        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        txtViewArtistName.setText(name);

        trackList = new ArrayList<>();
        databaseTrack = FirebaseDatabase.getInstance().getReference("tracks").child(id);


        btnAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTrack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trackList.clear();
                for (DataSnapshot trackSnapshot: dataSnapshot.getChildren()) {
                    Track track =  trackSnapshot.getValue(Track.class);
                    trackList.add(track);
                }

                TrackList adapter = new TrackList(AddTrackActivity.this, trackList);
                listViewTracks.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void saveTrack() {
        String trackName = editTextTrack.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)) {
            String id = databaseTrack.push().getKey();

            Track track  = new Track(id,trackName,rating);

            databaseTrack.child(id).setValue(track);

            Toast.makeText(this, "Track saved Successfully ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Track name should not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
