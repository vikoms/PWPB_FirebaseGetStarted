package com.example.firebasegetstarted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editTextName;
    Button btnAdd;
    Spinner Spinnergenre;

    DatabaseReference databaseArtist;
    ListView listViewArtist;

    List<Artist> artistList;

    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseArtist = FirebaseDatabase.getInstance().getReference("artist");

        listViewArtist = (ListView) findViewById(R.id.container_listView);

        artistList = new ArrayList<>();

        editTextName = (EditText) findViewById(R.id.txtNameArtist);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        Spinnergenre = (Spinner) findViewById(R.id.genre);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Artist artist = artistList.get(i);
                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return false;
            }
        });

        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get(i);

                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);
                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistList.clear();
                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }

                ArtistList adapter = new ArtistList(MainActivity.this, artistList);
                listViewArtist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showUpdateDialog(final String artistId, String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialog = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialog);

        final EditText editTextName = (EditText) dialog.findViewById(R.id.editTextNameDialog);
        final Spinner spinnerGenres = (Spinner) dialog.findViewById(R.id.genreDialog);
        final Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdateDialog);
        final Button btnDelete = (Button) dialog.findViewById(R.id.btnDeleteDialog);

        dialogBuilder.setTitle("Update Artist " + artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if(TextUtils.isEmpty(name)) {
                    editTextName.setError("Name Required");
                    return;
                }

                updateArtist(artistId,name,genre);
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
            }
        });


    }

    private void deleteArtist(String artistId) {
        DatabaseReference artist = FirebaseDatabase.getInstance().getReference("artist").child(artistId);
        DatabaseReference tracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);


        artist.removeValue();
        tracks.removeValue();

        Toast.makeText(this, "Artist is deleted", Toast.LENGTH_SHORT).show();
    }

    private boolean updateArtist(String id, String name, String genre){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(id);
        Artist artist = new Artist(id,name,genre);

        databaseReference.setValue(artist);

        Toast.makeText(this, "Artist Update Successfully", Toast.LENGTH_SHORT).show();

        return true;

    }


    private void addArtist() {
        String name = editTextName.getText().toString();
        String genre = Spinnergenre.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {
            String id = databaseArtist.push().getKey();

            Artist artist = new Artist(id, name, genre);

            databaseArtist.child(id).setValue(artist);

            Toast.makeText(this, "Artist Added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_SHORT).show();
        }
    }
}
