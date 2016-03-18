package com.shenkar.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ex2.shenkar.todolist.Contact;
import com.ex2.shenkar.todolist.R;
import com.ex2.shenkar.todolist.RegisteredUser;
import com.ex2.shenkar.todolist.SelectContactCallback;
import com.ex2.shenkar.todolist.SelectContactDialog;
import com.ex2.shenkar.todolist.Team;
import com.ex2.shenkar.todolist.TeamLoadCallback;
import com.ex2.shenkar.todolist.Welcome;
import com.shenkar.managy.adapters.TeammateListAdapter;
import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

import java.util.ArrayList;

/**
 * Created by shnizle on 2/28/2016.
 */
public class CreateTeamFragment extends android.support.v4.app.Fragment {

    private static final int REQUEST_READ_CONTACTS = 0;
    SelectContactDialog selectContactDialog;
    RegisteredUser manager;
    Team team;
    ListView teammateListView;
    Welcome mainActivity;
    View rootView;
    EditText teamNameEdit;
    Button teamNameSaveBtn;
    ArrayList<RegisteredUser> teamMates;
    TeammateListAdapter teammateListAdapter;

    public void setTeam(Team team){

        this.team = team;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_create_team, container, false);

        teammateListView = (ListView) rootView.findViewById(R.id.teammateList);

        mainActivity = (Welcome) getActivity();

        manager = mainActivity.getUser();
        team = mainActivity.getTeam();

        teamNameSaveBtn = (Button) rootView.findViewById(R.id.teamNameSaveBtn);
        teamNameSaveBtn.setVisibility(View.INVISIBLE);

        if(manager != null && team != null){

            // load list with team users
            //teamMates = team.getTeamMates();
            teammateListAdapter = new TeammateListAdapter(getActivity(), team);
            teammateListView.setAdapter(teammateListAdapter);
            teamNameEdit = (EditText) rootView.findViewById(R.id.teamNameInput);

            teamNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    teamNameSaveBtn.setVisibility(teamNameEdit.getText().toString().equals(team.getName()) || teamNameEdit.getText().toString().length() < 5 ? View.GONE : View.VISIBLE );
                }
            });

            teamNameEdit.setText(team.getName());

            teamNameSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    teamNameSaveBtn.setEnabled(false);

                    /**
                     * update team name
                     */
                    team.updateTeamName(teamNameEdit.getText().toString(), getContext(), new GetRequestCallback() {
                        @Override
                        public void success(JSONObject jsonObject) {

                            teamNameSaveBtn.setEnabled(true);
                            mainActivity.notifyUser("Team name updated");
                            teamNameSaveBtn.setVisibility(View.GONE);
                        }

                        @Override
                        public void failed(Exception error) {

                            teamNameSaveBtn.setEnabled(true);
                            mainActivity.notifyUser("update failed");
                        }
                    });
                }
            });

            /**
             * if contact permissions exist load dialog
             *
             */
            if (mayRequestContacts()) {

                selectContactDialog = new SelectContactDialog(getActivity(), new SelectContactCallback() {

                    @Override
                    public void onSelected(final ArrayList<Contact> selectedContacts) {

                        mainActivity.loading(true);

                        String contect_list = "";

                        for(Contact contact : selectedContacts){

                            contect_list += contact.getEmail()+",";
                        }

                        /**
                         * user has selected contacts
                         */
                        GetRequest.send("MODEL=Users&COMMAND=addNewTeammates&emails=" + contect_list + "&attrs[team_id]=" + team.getTeamId() + "&attrs[type]=TEAMMATE",
                                getContext(), new GetRequestCallback() {
                                    @Override
                                    public void success(JSONObject jsonObject) {

                                        mainActivity.notifyUser("Teammates added successfully");
                                        mainActivity.loading(false);

                                        // reload team
                                        team.reloadTeamMembers(getContext(), new TeamLoadCallback() {
                                            @Override
                                            public void successful(Team team) {

                                                teammateListAdapter.updateTeam(team);
                                            }

                                            @Override
                                            public void failed() {

                                            }
                                        });
                                    }

                                    @Override
                                    public void failed(Exception error) {
                                        error.printStackTrace();
                                        mainActivity.loading(false);
                                        mainActivity.notifyUser("update failed");
                                    }
                                });
                    }
                });

            }
            // This Build is < 6 , you can Access contacts here.

            rootView.findViewById(R.id.addNewTeammateBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(selectContactDialog != null)
                        selectContactDialog.show();
                }
            });

        }else{

            ((Welcome)getActivity()).notifyUser(getString(R.string.failed_loading_team));
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(getContext(), READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted , Access contacts here or do whatever you need.
                // load managers team
                selectContactDialog = new SelectContactDialog(getActivity(), new SelectContactCallback() {

                    @Override
                    public void onSelected(ArrayList<Contact> selectedContacts) {

                        /**
                         * user has selected contacts
                         */
                    }
                });

            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
