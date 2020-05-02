package com.example.findmyrhythm.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.findmyrhythm.Model.Exceptions.DuplicatedInstanceException;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class GenericDAO <T extends Entity> {

    private String TAG = "GenericDAO";

    // Class of T for casting entities retrieved from database.
    private Class<T> genericType;

    // Name of the table that a DAO is going to access
    // should be assigned in the constructor of the subclasses
    private final String tableName;

    public GenericDAO(Class<T> genericType, String tableName) {
        this.genericType = genericType;
        this.tableName = tableName;
    }

    // Takes an entity and inserts it with an autogenerated id
    // returns id
    public String insert (T entity) throws DuplicatedInstanceException {
        // Get reference to the table
        DatabaseReference table = getTable();
        // If entity has a preset id, we check if it exists
        if (entity.hasId()) {
            try {
                findById(entity.getId());
                // If id is found, object cant be inserted
                throw new DuplicatedInstanceException();
            } catch (InstanceNotFoundException e) {
                // If id is not found, the entity is inserted with its id
                table.child(entity.getId()).setValue(entity);
                return entity.getId();
            }
        }
        else {
            // Get a new id
            String newId = table.push().getKey();
            // Assign id to entity
            entity.setId(newId);
            // Insert entity in database
            table.child(newId).setValue(entity);
            // Return id of the inserted entity
            return newId;
        }
    }

    public String modify (T entity) throws InstanceNotFoundException {
        // Get reference to the table
        DatabaseReference table = getTable();
        // If entity has a preset id, we check if it exists

                table.child(entity.getId()).setValue(entity);
                // If id is found, object cant be inserted
                return entity.getId();

    }

    //TODO: MIRAR SI HACE FALTA PONER LOCKS. PROBABLEMENTE SI
    public T findById (String entityId) throws InstanceNotFoundException {

        DatabaseReference table = getTable();

        // Placeholder for the data retrieved from the DB
        final ArrayList<T> entity = new ArrayList<T>();

        //Lock to wait for the data
        final CountDownLatch lock = new CountDownLatch(1);

        table.child(entityId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entity.add(dataSnapshot.getValue(genericType));
                // Data retrieved, release lock
                lock.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Cancelled, release lock
                lock.countDown();
            }
        });

        try {
            lock.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "findById thread interrupted");
        }

        // Check that some data was retrieved
        if (entity.get(0) == null) {
            throw new InstanceNotFoundException();
        }

        // Return entity
        return entity.get(0);
    }

    // Overrides a entity in the database
    public void update(T entity) {
        getTable().child(entity.getId()).setValue(entity);
    }

    // Deletes an entity with specified id
    public void delete(String id) {
        getTable().child(id).removeValue();
    }

    // Potected method to get a reference to the db table
    protected DatabaseReference getTable() {
        return FirebaseDatabase.getInstance().getReference(tableName);
    }
}
