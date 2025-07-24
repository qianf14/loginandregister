package com.example.loginandregister.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.loginandregister.model.Person;

/**
 * 详情页面的ViewModel，负责管理Person对象的数据。
 */
public class DetailViewModel extends ViewModel {
    // LiveData用于暴露Person对象给UI
    private final MutableLiveData<Person> personLiveData = new MutableLiveData<>();

    /**
     * 设置要展示的Person对象
     * @param person 传递过来的Person对象
     */
    public void setPerson(Person person) {
        personLiveData.setValue(person);
    }

    /**
     * 获取Person对象的LiveData
     */
    public LiveData<Person> getPerson() {
        return personLiveData;
    }
} 