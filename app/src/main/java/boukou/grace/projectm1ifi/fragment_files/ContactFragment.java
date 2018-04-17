package boukou.grace.projectm1ifi.fragment_files;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import boukou.grace.projectm1ifi.R;
import boukou.grace.projectm1ifi.adapter_files.MyContactAdapter;
import boukou.grace.projectm1ifi.java_files.MyContact;

/**
 *
 */
public class ContactFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_ITEM_NUMBER = "section_number";

    private String mParam1;
    private String mParam2;
    private int mSectionNumber;

    private ArrayList<MyContact> myContacts = new ArrayList<>();

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    public static ContactFragment newInstance(String param1, String param2, int sectionNumber) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_ITEM_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mSectionNumber = getArguments().getInt(ARG_ITEM_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.container_contacts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        MyContactAdapter adapter = new MyContactAdapter(getMyContacts());
        recyclerView.setAdapter(adapter);

        //TextView textView = view.findViewById(R.id.nb_contact);
        //textView.setText(getString(R.string.section_format, Objects.requireNonNull(getArguments()).getInt(ARG_ITEM_NUMBER)));
        return view;
    }

    private List<MyContact> getMyContacts() {
        Set<MyContact> contactSet = new HashSet<>();

        Map<String, String> map = new LinkedHashMap<>();

        @SuppressLint("Recycle") Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        Objects.requireNonNull(cursor).moveToFirst();

        while (cursor.moveToNext()) {
            /*
            contactSet.add(new MyContact(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
             */
            map.put(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }

        Set<Map.Entry<String, String>> setMap = map.entrySet();
        for (Map.Entry<String, String> entry : setMap) {
            Log.e("____", entry.getKey() + " -- " + entry.getValue());

            contactSet.add(new MyContact(entry.getKey(), entry.getValue()));
        }

        Log.e("NB_CONTACT", ""+contactSet.size());

        List<MyContact> listContact = new ArrayList<>(contactSet);

        Collections.sort(listContact, new Comparator<MyContact>() {
            @Override
            public int compare(MyContact o1, MyContact o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return listContact;
    }




    private List<MyContact> getMyContactsOld() {
        final Set<MyContact> contactSet = new HashSet<>();
        Map<String, String> contactMap = new HashMap<>();

        @SuppressLint("Recycle") Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER},
                null, null, null);

        if (cursor == null) {
            Log.e("CONTACTS", "Pas de contact trouve");
            return null;
        }

        Objects.requireNonNull(cursor).moveToFirst();

        while (cursor.moveToNext()) {
            final long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID)));
            final String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            final int has_phone_number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (has_phone_number > 0) {
                contactSet.add(new MyContact(username, ""+has_phone_number));
            }

            //contactSet.add(new MyContact(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
            //      cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))));

            //contactMap.put(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
            //      cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            //contactSet.add(new MyContact(contactMap.keySet(), contactMap.values()));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        //final List<MyContact>

        Log.e("NB_CONTACT", ""+contactSet.size());

        return new ArrayList<>(contactSet);
    }

}
