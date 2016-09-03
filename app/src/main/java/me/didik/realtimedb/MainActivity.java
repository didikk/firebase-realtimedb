package me.didik.realtimedb;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeletionListener {
    private NoteAdapter adapter;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NoteActivity.newInstance(view.getContext(), null));
            }
        });

        database = FirebaseDatabase.getInstance().getReference();
        adapter = new NoteAdapter(Collections.<Note>emptyList());

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // Set padding
        final int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = offset;
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new NoteItemTouchHelperCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, MainActivity.this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        database.child("notes").addValueEventListener(listener);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Note> notes = new ArrayList<>();
            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                Note note = noteDataSnapshot.getValue(Note.class);
                notes.add(note);
            }

            adapter.updateList(notes);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onDestroy() {
        database.child("notes").removeEventListener(listener);
        super.onDestroy();
    }

    @Override
    public void itemRemoved(int position) {
        Note note = adapter.getItem(position);
        adapter.removeItem(position);
        database.child("notes").child(note.getUid()).removeValue();
    }
}
