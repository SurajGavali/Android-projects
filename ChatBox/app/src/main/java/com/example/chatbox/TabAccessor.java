package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessor extends FragmentPagerAdapter {
    public TabAccessor(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){

            case 0 :
                ChatsFragment cf = new ChatsFragment();
                return cf;

            case 1 :
                ContactsFragment cof = new ContactsFragment();
                return cof;

            case 2 :
                RequestFragment rf = new RequestFragment();
                return rf;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){

            case 0 :
                return "CHATS";

            case 1 :
                return "CONTACTS";

            case 2 :
                return "REQUESTS";

            default:
                return null;

        }
    }
}
