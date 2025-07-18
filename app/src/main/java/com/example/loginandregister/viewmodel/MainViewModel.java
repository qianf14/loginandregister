package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<String> currentUsername = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        loadCurrentUser();
    }

    public LiveData<String> getCurrentUsername() { return currentUsername; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }

    public void loadCurrentUser() {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", "");
        currentUsername.setValue(user);
    }

    public void logout() {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().remove("current_user").apply();
        logoutEvent.setValue(true);
    }
} 