package edu.sdsu.cs.shruti.sharedlist;

import android.app.Application;

import com.parse.Parse;


public class SharedGroceryListApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // Add your initialization code here
        Parse.initialize(this, "EfuwhBHe1KXVvi8yucsmbdT1wzN7qClJoDLMJ77H", "YUxsLQORR6RZnaiEuP5VBwUUVyR3RAUrv9XzU5mA");
    }
}