/**
 * TermListing displays the list of terms associated with the degree plan
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class TermListing extends AppCompatActivity {
    private MenuItem menuItemDelete;
    static final int REQUEST_ID = 1111;
    static final String TERM_LISTING = "TERM_LISTING";
    static final int TERM_REQUEST_ID = 2222;
    static final String TERM_ID = "TERM_ID";
    static final String CURRENT_TERM = "CURRENT_TERM";
    ListAdapter adapter;
    private ArrayList<Term> terms;
    private AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Term Listing");
        terms = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        terms.addAll(bundle.<Term>getParcelableArrayList(MainActivity.TERM_LISTING));

        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        adapter = new ListAdapter(
                this, R.layout.list_item, terms);


        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermListing.this, TermDetail.class);

                Term term = terms.get(position);
                intent.putExtra(CURRENT_TERM, term);
                intent.putExtra("selectedTermPosition", position);
                intent.putExtra("Terms", terms);
                startActivityForResult(intent, TERM_REQUEST_ID);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.handleLongPress(position, view);
                if(adapter.getListSelectedItems().size() > 0){
                    showDeleteMenu(true);
                } else {
                    showDeleteMenu(false);
                }
                return true;
            }
        });






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermListing.this, AddNewTerm.class);
                intent.putExtra(TERM_LISTING, terms);
                startActivityForResult(intent, REQUEST_ID);
                Intent data = new Intent();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                terms.clear();
                terms.addAll(data.<Term>getParcelableArrayListExtra(TERM_LISTING));
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == TERM_REQUEST_ID){
            if (resultCode == RESULT_OK){
                int termPosition = data.getIntExtra("selectedTermPosition", -1);
                terms.remove(termPosition);
                terms.add(termPosition, data.<Term>getParcelableExtra("selectedTerm"));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuItemSearch = menu.findItem(R.id.action_search);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemDelete.setVisible(false);

        MenuItem editButton = menu.findItem(R.id.action_edit);

        editButton.setVisible(false);





        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Term> deleteTerms = adapter.deleteSelectedItems();

                for (Term term : deleteTerms)
                {
                    database.termDao().deleteTerm(term);
                }

                for (Term term : terms)
                {
                    int pos = term.getName().length()-1;
                    StringBuilder termName = new StringBuilder();
                    termName.append(term.getName());
                    termName.replace(pos,pos+1,""+(terms.indexOf(term)+1));
                    term.setName(termName.toString());
                    database.termDao().updateTerm(term);
                }

                adapter.notifyDataSetChanged();
                showDeleteMenu(false);
                return true;
            }
        });



        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(TermListing.this, "Click on a item to view it's details, long press a item to select and then click the trash can to delete selected items.", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra(MainActivity.TERM_LISTING, terms);
        setResult(RESULT_OK, data);
        finish();
    }
}
