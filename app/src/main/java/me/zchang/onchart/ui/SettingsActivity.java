package me.zchang.onchart.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

public class SettingsActivity extends AppCompatActivity {

    public final static int FLAG_LOGOUT = -1;
    public final static int FLAG_NO_LOGOUT = 0;
    private final static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{
        Intent retIntent;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            retIntent = new Intent();
            getPreferenceManager().setSharedPreferencesName(getResources().getString(me.zchang.onchart.R.string.pref_file_name));
            addPreferencesFromResource(R.xml.preferences);

            android.support.v7.preference.Preference preference = findPreference(getString(me.zchang.onchart.R.string.key_num_of_weekday));
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    retIntent.putExtra(getString(R.string.key_num_of_weekday), Integer.parseInt((String) newValue));
                    getActivity().setResult(RESULT_OK, retIntent);
                    return true;
                }
            });

            Preference logoutPref = findPreference(getString(R.string.key_logout));
            logoutPref.setOnPreferenceClickListener(this);
            Preference licensePref = findPreference(getString(R.string.key_license));
            licensePref.setOnPreferenceClickListener(this);
            Preference aboutPref = findPreference(getString(R.string.key_about));
            aboutPref.setOnPreferenceClickListener(this);
            Preference feedbackPref = findPreference(getString(R.string.key_feedback));
            feedbackPref.setOnPreferenceClickListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            if (preference.getKey().equals(getString(R.string.key_logout))) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.message_logout))
                        .setPositiveButton(getString(R.string.action_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainApp)getActivity().getApplication())
                                        .getConfigManager()
                                        .deleteSchedule()
                                        .saveName("-")
                                        .saveStuNo("")
                                        .savePassword("");
                                retIntent.putExtra(getString(R.string.key_logout), FLAG_LOGOUT);
                                getActivity().setResult(RESULT_OK, retIntent);
                            }
                        })
                        .setNegativeButton(getString(R.string.action_negative), null)
                        .setCancelable(true)
                        .show();
            } else if (preference.getKey().equals(getString(R.string.key_license))) {
                LicenseFragment fragment = new LicenseFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content,
                        fragment).hide(this).addToBackStack("license").commit();
            } else if (preference.getKey().equals(getString(R.string.key_about))) {
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(getActivity().getSupportFragmentManager(), TAG);
            } else if (preference.getKey().equals(getString(R.string.key_feedback))) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + getString(R.string.url_my_email)));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Issue in onChart " + BuildConfig.VERSION_NAME);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(intent);
                else
                    Toast.makeText(getActivity(), getString(R.string.alert_no_email_app), Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    public static class LicenseFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.license_list);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }
    }
}
