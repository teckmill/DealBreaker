//Fragment Class that is used to set the User's information such as their name, sex, age, etc.

package roast.app.com.dealbreaker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.RenamingDelegatingContext;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import roast.app.com.dealbreaker.util.Constants;
import java.util.Date;

import roast.app.com.dealbreaker.R;
import roast.app.com.dealbreaker.User;

public class UserAttribute extends Fragment {
   //Class Variables
    private String firstNameUserValue, lastNameUserValue, ageUserValue, heightUserValue, sexUserValue, sexualOrientationUserValue, username = "cindy123";
    private EditText firstNameUserText, lastNameUserText, ageUserText, heightUserText, sexUserText, sexualOrientationUserText;
    Button sendUserValues;
    public static UserAttribute newInstance() {
        UserAttribute fragment = new UserAttribute();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         // Initialize UI elements

        View rootView = inflater.inflate(R.layout.fragment_user_attribute, container, false);
        initializeScreen(rootView);
        sendUserValues.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                grabEditText();
                checkAndSendData();
            }
        });

        /**
         * Create Firebase references
         */
        Firebase refName_Users = new Firebase(Constants.FIREBASE_URL_USERS).child(username);

        //Add the value Event Listener so if data has already been inputted by the user then it will
        //pre-populated with existing data
        refName_Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // You can use getValue to deserialize the data at dataSnapshot
                User user = dataSnapshot.getValue(User.class);
                // If there was no data at the location we added the listener, then
                if (user != null) {
                    ageUserText.setText(user.getAge().toString());
                    firstNameUserText.setText(user.getFirstName());
                    heightUserText.setText(user.getHeight().toString());
                    lastNameUserText.setText(user.getLastName());
                    sexUserText.setText(user.getSex());
                    sexualOrientationUserText.setText(user.getSexualOrientation());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Link the XML to the Activity
    private void initializeScreen(View rootView) {
        firstNameUserText=(EditText)rootView.findViewById(R.id.et_user_first_name);
        lastNameUserText=(EditText)rootView.findViewById(R.id.et_user_last_name);
        ageUserText=(EditText) rootView.findViewById(R.id.et_user_age);
        heightUserText=(EditText) rootView.findViewById(R.id.et_user_height);
        sexUserText=(EditText)rootView.findViewById(R.id.et_user_sex);
        sexualOrientationUserText=(EditText)rootView.findViewById(R.id.et_user_sexual_or);
        sendUserValues = (Button) rootView.findViewById(R.id.user_attribute_finished_button);
    }
    //Grab all of the Edit Text Values
    private void grabEditText() {
        firstNameUserValue = firstNameUserText.getText().toString().trim();
        lastNameUserValue = lastNameUserText.getText().toString().trim();
        ageUserValue = ageUserText.getText().toString().trim();
        heightUserValue = heightUserText.getText().toString().trim();
        sexUserValue = sexUserText.getText().toString().trim();
        sexualOrientationUserValue = sexualOrientationUserText.getText().toString().trim();
    }

    //Check to see if the data entered is in the set of inputs needed or that it is not empty
    private void checkAndSendData(){
        if(TextUtils.isEmpty(firstNameUserValue)){
            firstNameUserText.setError("This field cannot be empty!");
        }
        else if(TextUtils.isEmpty(lastNameUserValue)){
            lastNameUserText.setError("This field cannot be empty!");
        }
        else if (TextUtils.isEmpty(ageUserValue)) {
            ageUserText.setError("This field cannot be empty!");
        }
        else if(TextUtils.isEmpty(sexualOrientationUserValue)||((!sexualOrientationUserValue.equals("straight")&&(!sexualOrientationUserValue.equals("bisexual"))&&(!sexualOrientationUserValue.equals("gay"))))){
            sexualOrientationUserText.setError("Invalid!, Inputs can be straight, gay, or bisexual");
        }
        else if(TextUtils.isEmpty(sexUserValue)||((!sexUserValue.equals("male")&&(!sexUserValue.equals("female"))))){
            sexUserText.setError("Invalid!, Inputs can be wither male or female");
        } else {
            String firstName = firstNameUserValue;
            String lastName = lastNameUserValue;
            Long age = Long.valueOf(ageUserValue);
            Long height = Long.valueOf(heightUserValue);
            String sex = sexUserValue;
            String sexual_orientation = sexualOrientationUserValue;
            User user = new User(username, firstName, lastName, sex, age, sexual_orientation, height);
            addUserAttributes(user);
        }

    }

    //Add the entered data to the database in the specified location
    public void addUserAttributes(User user){
        //Set Reference to Firebase node
        Firebase ref = new Firebase(Constants.FIREBASE_URL_USERS);
        ref.child(username).setValue(user);
    }

}
