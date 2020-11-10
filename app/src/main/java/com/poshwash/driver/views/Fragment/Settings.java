package com.poshwash.driver.views.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poshwash.driver.views.Activity.ChangePassword;
import com.poshwash.driver.views.Activity.Login;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.views.Activity.StaticPage;
import com.poshwash.driver.Adapter.SettingAdapter;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.DividerItemDecoration;
import com.poshwash.driver.Utils.MyProgressDialog;
import com.poshwash.driver.Utils.MySharedPreferences;
import com.poshwash.driver.Utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by anandj on 3/17/2017
 */

public class Settings extends Fragment {

    static Context context;
    private ImageView toggle_icon;
    private TextView txt_heading;
    private RelativeLayout rr_toolbar;
    private RecyclerView recyclerview;
    private ArrayList<String> setting_name;
    private SettingAdapter sAdapter;
    private MyProgressDialog progressDialog;

    public static Settings newInstance(Context contex) {
        Settings f = new Settings();
        context = contex;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting, container, false);
        context = getActivity();
        progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCancelable(false);
        setting_name = new ArrayList<>();

        initView(view);
        ((MainActivity) getActivity()).setSettingFragment();

        return view;
    }

    private void initView(View view) {
        toggle_icon = (ImageView) view.findViewById(R.id.toggle_icon);
        txt_heading = (TextView) view.findViewById(R.id.txt_heading);
        rr_toolbar = (RelativeLayout) view.findViewById(R.id.rr_toolbar);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        sAdapter = new SettingAdapter(context, setting_name);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(sAdapter);

        recyclerview.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = null;
                        switch (position) {
                            case 0:
                                startActivity(new Intent(getActivity(), ChangePassword.class));
                                break;
                            case 1:
                                intent = new Intent(getActivity(), StaticPage.class);
                                intent.putExtra("pagetype", "privacy_policy");
                                intent.putExtra("pagename", getString(R.string.privacy_policy));
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(getActivity(), StaticPage.class);
                                intent.putExtra("pagetype", "terms_of_use");
                                intent.putExtra("pagename", getString(R.string.terms_conditions));
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(getActivity(), StaticPage.class);
                                intent.putExtra("pagetype", "about_us");
                                intent.putExtra("pagename", getString(R.string.about_us));
                                startActivity(intent);
                                break;

                            case 4:
                                try {
                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@autospa.com"});
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suggestions));
                                    emailIntent.setType("text/plain");
                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                                    final PackageManager pm = getActivity().getPackageManager();
                                    final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                                    ResolveInfo best = null;
                                    for (final ResolveInfo info : matches)
                                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                                            best = info;
                                    if (best != null)
                                        emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                                    startActivity(emailIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case 5:
                                intent = new Intent(getActivity(), StaticPage.class);
                                intent.putExtra("pagetype", "faq");
                                intent.putExtra("pagename", getString(R.string.faq));
                                startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(getActivity(), StaticPage.class);
                                intent.putExtra("pagetype", "how_its_work");
                                intent.putExtra("pagename", getString(R.string.help));
                                startActivity(intent);
                                break;
                            case 7:
                                Intent intent2 = new Intent(Intent.ACTION_SEND);
                                intent2.setType("plain/text");
                                intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@autospa.com"});
                                intent2.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_user));
                                intent2.putExtra(Intent.EXTRA_TEXT, "");
                                startActivity(Intent.createChooser(intent2, getString(R.string.send_email)));
                                break;
                            case 8:
                                Intent intent3 = new Intent(Intent.ACTION_SEND);
                                intent3.setType("plain/text");
                                intent3.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@autospa.com"});
                                intent3.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us));
                                intent3.putExtra(Intent.EXTRA_TEXT, "");
                                startActivity(Intent.createChooser(intent3, getString(R.string.send_email)));
                                break;
                        }
                    }
                })
        );
        fillData();
    }

    private void fillData() {
        setting_name.add(getString(R.string.changepassword));
        setting_name.add(getString(R.string.privacyplicies));
        setting_name.add(getString(R.string.termscondition));
        setting_name.add(getString(R.string.aboutus));
        setting_name.add(getString(R.string.suggestions));
        setting_name.add(getString(R.string.faq));
        setting_name.add(getString(R.string.help));
        setting_name.add(getString(R.string.reportuser));
        setting_name.add(getString(R.string.contactus));


        sAdapter.notifyDataSetChanged();
    }

    public void SetLogout() {
        String deivce_token = MySharedPreferences.getDeviceId(context);
        MySharedPreferences.ClearAll(context);
        MySharedPreferences.setUserEmail(context, "");
        MySharedPreferences.setDeviceId(context, deivce_token);
        Intent intent = new Intent(context, Login.class);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.back_slide_in_up, R.anim.back_slide_out_up);
    }
}
