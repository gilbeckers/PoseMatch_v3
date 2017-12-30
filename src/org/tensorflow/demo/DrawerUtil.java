package org.tensorflow.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by Gil on 28/12/2017.
 */


// https://android.jlelse.eu/android-using-navigation-drawer-across-multiple-activities-the-easiest-way-b011f152aebd
// https://github.com/mikepenz/MaterialDrawer
public class DrawerUtil {

    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseUser mFirebaseUser;
    private static Drawer result;
    private static AccountHeader headerResult;
    private static ProfileDrawerItem profile = new ProfileDrawerItem().withIdentifier(1); // = loadProfile();

    public static void getDrawer(final Activity activity, Toolbar toolbar){
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_settings);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_login);
        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_settings);

        if(isSingedIn()){
            // Disable Sing-in Button
            item3.withSelectable(false);
        }

        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(4)
                .withName(R.string.settings).withIcon(FontAwesome.Icon.faw_binoculars);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(5)
                .withName(R.string.about).withIcon(FontAwesome.Icon.faw_pencil);
        SecondaryDrawerItem drawerItemHelp = new SecondaryDrawerItem().withIdentifier(6)
                .withName(R.string.help).withIcon(FontAwesome.Icon.faw_wifi);
        SecondaryDrawerItem drawerItemDonate = new SecondaryDrawerItem().withIdentifier(7)
                .withName(R.string.donate).withIcon(FontAwesome.Icon.faw_magic);
        SecondaryDrawerItem drawerSignOut = new SecondaryDrawerItem().withIdentifier(8)
                .withName(R.string.signout).withIcon(FontAwesome.Icon.faw_sign_out);

        item1.withName("A new name for this drawerItem").withBadge("19").withBadgeStyle(new BadgeStyle().withTextColor(Color.BLACK).withColorRes(R.color.md_red_700));
        //item1.withEnabled(false);
        item1.withSelectable(false);

        loadProfile();

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.drawer)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.e("Drawer", "in listener --- LOOGEEED OUT");
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .withCloseOnClick(true)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        new DividerDrawerItem(),
                        drawerItemSettings,
                        drawerItemAbout,
                        drawerItemHelp,
                        drawerItemDonate,
                        drawerSignOut
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.e("Drawer", "##clicked position:  " + position);

                        if(drawerItem.getIdentifier() == 3){
                            // Login
                            // Initialize Firebase Auth
                            // Initialize Firebase Auth
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();

                            if (mFirebaseUser == null) {
                                // Not logged in, launch the Log In activity
                                loadLogInView(activity);
                            }

                        }

                        if(drawerItem.getIdentifier() == 8){
                            handleSignOut();
                        }

                        return true;
                    }
                })
                .build();
    }

    private static void handleSignOut() {
        Log.e("Drawer", "####in singOUTT --LOOGEEED OUT");
        mFirebaseAuth.signOut();


        // Update profile to guest account
        profile.withIdentifier(1).
                withName("Guest").
                withEmail("Not logged in").
                withIcon(R.drawable.guest);

        // Update headerView in Drawer
        headerResult.updateProfile(profile);

        result.getDrawerItem(3).withSelectable(true);


    }

    public static void loadProfile(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not logged in
            profile.withIdentifier(1).
                    withName("Guest").
                    withEmail("Not logged in").
                    withIcon(R.drawable.guest);

        }
        else {
            profile.withIdentifier(1).
                    withName(mFirebaseAuth.getCurrentUser().getDisplayName()).
                    withEmail(mFirebaseAuth.getCurrentUser().getEmail()).
                    withIcon(R.drawable.avatar1);
        }

    }

    private static boolean isSingedIn(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null){
            return  false;
        }

        return  true;
    }

    // Called from LogInActivity when login succes
    private static void signalLoggedIn(){

    }

    private static void loadLogInView(Activity activity) {
        // Navigates to the Login view and clears the activity stack.
        // This prevents the user going back to the main activity
        // when they press the Back button from the login view.
        // TODO: mss probleem voor camera view?
        Intent intent = new Intent(activity, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
