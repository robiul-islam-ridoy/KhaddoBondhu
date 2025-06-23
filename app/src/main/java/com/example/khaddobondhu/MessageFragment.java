package com.example.khaddobondhu;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MessageFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        userList = new ArrayList<>();
        userList = new ArrayList<>();
        userList.add(new User("1", "Ridoy", R.drawable.ic_person));
        userList.add(new User("2", "Ayesha", R.drawable.ic_person));
        userList.add(new User("3", "Rahim", R.drawable.ic_person));


        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        return view;
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        intent.putExtra("userName", user.getName());
        startActivity(intent);
    }
}
