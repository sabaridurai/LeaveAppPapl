package com.example.leaveapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaveRequestfrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaveRequestfrg extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton report,add_days,att_report;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase,firebaseDatabasegetcount;
    private DatabaseReference databaseReference,databaseReferencecount;
    private TextView usercount,employeecount,admincount,leadcount,managercount;


    Set<String> hash_Set = new HashSet<String>();
    Set<String> setforemployee=new HashSet<String>();
    Set<String> setlead=new HashSet<String>();
    Set<String> setmanager=new HashSet<String>();
    Set<String> setadmin=new HashSet<String>();
    String mail;
    ImageButton bottomsheet;

    public LeaveRequestfrg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaveRequestfrg.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaveRequestfrg newInstance(String param1, String param2) {
        LeaveRequestfrg fragment = new LeaveRequestfrg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_leave_requestfrg, container, false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);



        usercount=(TextView)view.findViewById(R.id.totalusercountid);
        employeecount=(TextView)view.findViewById(R.id.employeecountid);
        admincount=(TextView) view.findViewById(R.id.admincountid);
        leadcount=(TextView) view.findViewById(R.id.leadcountid);
        managercount=(TextView) view.findViewById(R.id.managercountid);
//        report = (Button) view.findViewById(R.id.reportgen);
        ImageButton bottomsheet =view.findViewById(R.id.openbottomsheet);
        bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        firebaseDatabasegetcount=FirebaseDatabase.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
//firebaseAuth.signOut();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        mail=firebaseUser.getEmail();

        ProgressDialog progressDialog=new ProgressDialog(getContext());



        databaseReference=firebaseDatabase.getReference("Leave");
        databaseReferencecount=firebaseDatabasegetcount.getReference("Users");


        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            progressDialog.setMessage("Fetching Details...");
//            progressDialog.show();


            if(!(mail.isEmpty())) {
                mail=mail.replaceAll(".","");
                mail=mail.replaceAll("@","");

//    databaseReferencecount.child("userdata").child(mail).addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//
//              String type=  dataSnapshot.child("EmployeeType").getValue().toString();
////              loginstate.setVisibility(View.VISIBLE);
////              loginstate.setText(type);
//
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    });


            }
            //userdata

            databaseReferencecount.child("userdata").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                       usercount.setText(String.valueOf(snapshot.getChildrenCount()));


                    hash_Set.add(snapshot.getKey());
                    int i=hash_Set.size();
                    usercount.setText(String.valueOf(i));

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    hash_Set.remove(snapshot.getKey());
                    int i =hash_Set.size();
                    usercount.setText(String.valueOf(i));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // employee count
            databaseReferencecount.child("EmployeeCategory").child("Employee").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                       usercount.setText(String.valueOf(snapshot.getChildrenCount()));

                    setforemployee.add(snapshot.getKey());
                    int i=setforemployee.size();
                    employeecount.setText(String.valueOf(i));

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    setforemployee.remove(snapshot.getKey());
                    int i=setforemployee.size();
                    employeecount.setText(String.valueOf(i));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            // lead count
            databaseReferencecount.child("EmployeeCategory").child("Lead").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                       usercount.setText(String.valueOf(snapshot.getChildrenCount()));

                    setlead.add(snapshot.getKey());
                    int i=setlead.size();
                    leadcount.setText(String.valueOf(i));

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    setlead.remove(snapshot.getKey());
                    int i=setlead.size();
                    leadcount.setText(String.valueOf(i));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//Manager count
            databaseReferencecount.child("EmployeeCategory").child("Manager").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                       usercount.setText(String.valueOf(snapshot.getChildrenCount()));

                    setmanager.add(snapshot.getKey());
                    int i=setmanager.size();
                    managercount.setText(String.valueOf(i));

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    setmanager.remove(snapshot.getKey());
                    int i=setmanager.size();
                    managercount.setText(String.valueOf(i));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // admin count
            databaseReferencecount.child("EmployeeCategory").child("Admin").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                       usercount.setText(String.valueOf(snapshot.getChildrenCount()));
                    progressDialog.dismiss();
                    setadmin.add(snapshot.getKey());
                    int i=setadmin.size();
                    admincount.setText(String.valueOf(i));

                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    setadmin.remove(snapshot.getKey());
                    int i=setadmin.size();
                    admincount.setText(String.valueOf(i));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




            databaseReference.child("leaveRequest").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

//                    Toast.makeText(getContext(), "New Request add", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Toast.makeText(getContext(), "New Request chang", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Toast.makeText(getContext(), "New Request chil remov", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else {
            Toast.makeText(getContext(), "Please connect to internet", Toast.LENGTH_LONG).show();
        }


        return view;
    }

    private void showBottomSheetDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.sheet,null);
        report= bottomSheetView.findViewById(R.id.reportgen);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),GenerateReport.class);
                startActivity(intent);

            }
        });
        add_days = bottomSheetView.findViewById(R.id.add_days);
        add_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Pl_Add.class);
                startActivity(intent);
            }
        });
        att_report = bottomSheetView.findViewById(R.id.att_rep);
        att_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Attendance.class);
                startActivity(intent);
            }
        });
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        behavior.setPeekHeight(350);
        bottomSheetDialog.show();

    }
}