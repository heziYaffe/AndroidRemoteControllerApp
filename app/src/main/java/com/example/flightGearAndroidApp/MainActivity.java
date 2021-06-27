package com.example.flightGearAndroidApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.flightGearAndroidApp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ViewModel vm = new ViewModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setViewModel(vm);
        activityMainBinding.executePendingBindings();
        //implementation of the functional interface
        Joystick.connector = (float aileron, float elevator) -> {
            vm.setAileron(aileron);
            vm.setElevator(elevator);
        };
    }

    // show the error/succeed message.
    @BindingAdapter({"toastMessage"})
    public static void connectPress(View view, String message) {
        if (message != null)
            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}