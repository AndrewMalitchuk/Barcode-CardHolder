package com.app.cardholder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keiferstone.nonet.NoNet;
import com.pd.chocobar.ChocoBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import petrov.kristiyan.colorpicker.ColorPicker;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.CirclePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal;

/**
 * CardListActivity is activity class for displaying all user's card
 */
public class CardListActivity extends AppCompatActivity {

    private RecyclerView cardsRecyclerView;
    private Toolbar toolbar;
    private List<Card> cards;
    private SwipeRefreshLayout swipeContainer;
    private CardsRecycleViewAdapter adapter = null;
    private EditText dialogBarCodeEditText = null;
    private FloatingActionButton addNewItemFloatingActionButton;

    public static FirebaseAuth auth;
    private FirebaseDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cards = new ArrayList<>();
        db = new FirebaseDB();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_credit_card_black_48dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        addNewItemFloatingActionButton = findViewById(R.id.addNewItemFloatingActionButton);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(true);
        cardsRecyclerView = findViewById(R.id.cardsRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        cardsRecyclerView.setLayoutManager(llm);
        adapter = new CardsRecycleViewAdapter(cards);
        adapter.setContext(getApplicationContext());
        cardsRecyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        Card swiped = cards.get(viewHolder.getAdapterPosition());
                        db.delete(auth.getUid(), swiped);
                        ChocoBar.builder().setActivity(CardListActivity.this)
                                .setText("Item deleted")
                                .setActionText(android.R.string.ok)
                                .green()
                                .setDuration(ChocoBar.LENGTH_SHORT)
                                .show();
                        adapter.onItemSwiped(viewHolder.getAdapterPosition());
                    }

                    @Override
                    public void onMoved(
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            int fromPos,
                            RecyclerView.ViewHolder target,
                            int toPos, int x, int y) {
                        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(cardsRecyclerView);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                reload();
                if (cards.size() == 0 || cards.isEmpty()) {
                    ChocoBar.builder().setActivity(CardListActivity.this)
                            .setText(R.string.emptyCardWarning)
                            .setDuration(ChocoBar.LENGTH_INDEFINITE)
                            .setActionText(android.R.string.ok)
                            .orange()
                            .show();
                } else {
                    ChocoBar.builder().setActivity(CardListActivity.this)
                            .setText(R.string.reloadWarning)
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .green()
                            .show();
                }

            }
        });
        cardsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && addNewItemFloatingActionButton.isShown())
                    addNewItemFloatingActionButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    addNewItemFloatingActionButton.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        reload();
        boolean firstrun = getIntent().getBooleanExtra("firstrun", false);
        if (firstrun == true) {
            showTapTargetPrompt();
        }
        NoNet.monitor(this)
                .poll()
                .snackbar();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        scoresRef.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!auth.getCurrentUser().isEmailVerified()) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.emailVerification)
                    .setMessage(R.string.emailVerificationContent)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.sendAgain, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            auth.getCurrentUser().sendEmailVerification();
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        reload();
        boolean firstrun = getIntent().getBooleanExtra("firstrun", false);
        if (firstrun == true) {
            showTapTargetPrompt();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.searchMenuItem).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        final SearchView.OnQueryTextListener queryTextListener
                = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Card> search = new ArrayList<>();
                for (Card card : cards) {
                    if (card.getName().contains(newText)) {
                        search.add(card);
                    }
                }
                if (search.size() != 0) {
                    reload(search);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        };
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                reload();
                return false;
            }
        });
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchMenuItem:
                aboutProgramInfo();
                break;
            case R.id.aboutMenuItem:
                aboutProgramInfo();
                break;
            case R.id.logoutMenuItem:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launching AboutActivity
     */
    private void aboutProgramInfo() {
        Intent intent = new Intent(CardListActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * FAB's handler method for launching New Card Dialog
     *
     * @param view - View object
     */
    public void onAddNewItemFloatingActionButtonClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_new_item_dialog_window);
        final EditText colorEditText = dialog.findViewById(R.id.colorEditText);
        ImageButton colorPickerImageButton = dialog.findViewById(R.id.colorPickerImageButton);
        colorPickerImageButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker(CardListActivity.this);
                colorPicker.setColors(R.array.colorPickerArray);
                colorPicker.getPositiveButton()
                        .setTextColor(getResources().getColor(R.color.textColor));
                colorPicker.getNegativeButton()
                        .setTextColor(getResources().getColor(R.color.textColor));
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        colorEditText.setText(String.format("#%06X", (0xFFFFFF & color)));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                colorPicker.setColumns(5);
                colorPicker.show();
            }
        });
        ImageButton barCodePickerImageButton = dialog.findViewById(R.id.barCodePickerImageButton);
        barCodePickerImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CardListActivity.this, CodeScannerActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        final EditText dialogNameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText dialogPlaceEditText = dialog.findViewById(R.id.placeEditText);
        final EditText dialogColorEditText = dialog.findViewById(R.id.colorEditText);
        dialogBarCodeEditText = dialog.findViewById(R.id.barCodeEditText);
        Button dialogConfirmButton = dialog.findViewById(R.id.dialogConfirmButton);
        dialogConfirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean exist = false;
                for (Card card : cards) {
                    if (dialogNameEditText.getText().toString().equals(card.getName())) {
                        exist = true;
                        break;
                    }
                }
                if (exist) {
                    ChocoBar.builder().setActivity(CardListActivity.this)
                            .setText(R.string.existWarning)
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .red()
                            .show();
                } else {
                    Card card = new Card(
                            dialogNameEditText.getText().toString(),
                            dialogPlaceEditText.getText().toString(),
                            dialogColorEditText.getText().toString(),
                            dialogBarCodeEditText.getText().toString());
                    cards.add(card);
                    db.write(auth.getUid(), card);
                    dialog.cancel();
                    reload();
                    ChocoBar.builder().setActivity(CardListActivity.this)
                            .setText(R.string.cardAddWarning)
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .green()
                            .show();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String barCode = data.getStringExtra("barcode");
        String barCodeType = data.getStringExtra("barType");
        dialogBarCodeEditText.setText(barCodeType + " | " + barCode);
    }

    /**
     * Loading data for certain user
     *
     * @param uid - user id
     */
    private void loadData(String uid) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance().getReference().child("users").child(uid).child("card");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            collectCards((Map<String, Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void collectCards(Map<String, Object> cardsDataBase) {
        cards.clear();
        for (Map.Entry<String, Object> entry : cardsDataBase.entrySet()) {
            Map temp = (Map) entry.getValue();
            Card card = new Card(
                    temp.get("name").toString(),
                    temp.get("place").toString(),
                    temp.get("color").toString(),
                    temp.get("code").toString());
            cards.add(card);
        }
        adapter = new CardsRecycleViewAdapter(cards);
        adapter.setContext(getApplicationContext());
        cardsRecyclerView.setAdapter(adapter);
    }

    /**
     * End current connection
     */
    private void logout() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.logoutTitle)
                .setMessage(R.string.logoutContent)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        Intent intent = new Intent(
                                CardListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Reload user content
     */
    private void reload() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    loadData(auth.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        swipeContainer.setRefreshing(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        cardsRecyclerView.setLayoutManager(llm);
        adapter = new CardsRecycleViewAdapter(cards);
        adapter.setContext(getApplicationContext());
        cardsRecyclerView.setAdapter(adapter);
        swipeContainer.setRefreshing(false);
    }

    /**
     * Reload user content
     *
     * @param cards - card list
     */
    private void reload(List<Card> cards) {
        swipeContainer.setRefreshing(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        cardsRecyclerView.setLayoutManager(llm);
        adapter = new CardsRecycleViewAdapter(cards);
        adapter.setContext(getApplicationContext());
        cardsRecyclerView.setAdapter(adapter);
        swipeContainer.setRefreshing(false);
    }

    /**
     * Show TapTarget dialog for CardListActivity
     */
    private void showTapTargetPrompt() {
        new MaterialTapTargetPrompt.Builder(CardListActivity.this)
                .setTarget(R.id.addNewItemFloatingActionButton)
                .setPrimaryText(R.string.addNewCardTitle)
                .setSecondaryText(R.string.addNewCardContent)
                .setPromptBackground(new CirclePromptBackground())
                .setPromptFocal(new CirclePromptFocal())
                .show();
    }
}
