package com.example.loginandregister.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.loginandregister.model.Person;

/**
 * 详情页面的ViewModel，负责管理Person对象的数据。
 */
public class DetailViewModel extends ViewModel {
    private static final String TAG = "DetailViewModel";
    // LiveData用于暴露Person对象给UI
    private final MutableLiveData<Person> personLiveData = new MutableLiveData<>();

    /**
     * 设置要展示的Person对象
     * @param person 传递过来的Person对象
     */
    public void setPerson(Person person) {
        Log.d(TAG, "setPerson: 设置Person对象，姓名=" + person.getName() + ", 年龄=" + person.getAge());
        personLiveData.setValue(person);
        Log.d(TAG, "setPerson: Person对象设置完成");
    }

    /**
     * 获取Person对象的LiveData
     */
    public LiveData<Person> getPerson() {
        return personLiveData;
    }
}
