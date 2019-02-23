package com.app.cardholder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class for executing simple queries to Firebase DB
 *
 */
public class FirebaseDB {

    private DatabaseReference mDatabase;

    /**
     * Class for initializing
     */
    public FirebaseDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Create new user by UID
     *
     * @param uid - user id value
     */
    public void writeUser(String uid) {
        mDatabase
                .child("users")
                .child("uid").setValue(uid);
    }

    /**
     * Save user's card to DB
     *
     * @param uid - user id value
     * @param card - card for saving
     */
    public void write(String uid, Card card) {
        String cardId = new Integer(card.getName().hashCode()).toString();
        mDatabase
                .child("users")
                .child(uid)
                .child("card")
                .child(cardId).setValue(card);
    }

    /**
     * Delete card from DB
     *
     * @param uid - user id value
     * @param card - card for deleting
     */
    public void delete(String uid, Card card) {
        String cardId = new Integer(card.getName().hashCode()).toString();
        mDatabase.child("users").child(uid).child("card").child(cardId).removeValue();
    }

}
