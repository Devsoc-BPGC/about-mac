package com.macbitsgoa.about;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.macbitsgoa.about.models.AndroidApp;
import com.macbitsgoa.about.models.Person;
import com.macbitsgoa.about.models.SocialLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.macbitsgoa.about.models.Person.FIELD_POST_NAME;

@SuppressWarnings("NullableProblems")
public class AboutAppActivity extends AppCompatActivity {

    public static final String EXTRA_APP_DESC = "appDescription";
    public static final String EXTRA_APP_DEV = "appDevelopers";
    public static final String EXTRA_APP_NAME = "appName";
    public static final String TAG_PREFIX = "mac.";
    public static final String TAG = TAG_PREFIX + AboutAppActivity.class.getSimpleName();
    public static final String MAC_API_BASE_URL = "https://us-central1-mac-bpgc.cloudfunctions.net";
    private final Realm realm = Realm.getDefaultInstance();
    private String appDesc = "";
    private String appName = "";
    private List<Person> appDevelopers = new ArrayList<>(0);
    private RecyclerView cocoRv;
    private Browser browser;

    /**
     * Method to launch this activity conveniently.
     *
     * @param context        from which this activity is to be launched.
     * @param appDescription of corresponding app.
     * @param appDevelopers  of corresponding app.
     */
    public static void launchAboutAppActivity(@NonNull final Context context,
                                              final String appDescription,
                                              final List<Person> appDevelopers,
                                              final String appName) {
        final Intent launchIntent = new Intent(context, AboutAppActivity.class);
        if (appDescription != null) {
            launchIntent.putExtra(EXTRA_APP_DESC, appDescription);
        }
        if (appDevelopers != null) {
            launchIntent.putExtra(EXTRA_APP_DEV, (new Gson()).toJson(appDevelopers.toArray()));
        }
        if (appName != null) {
            launchIntent.putExtra(EXTRA_APP_NAME, appName);
        }
        context.startActivity(launchIntent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireData(savedInstanceState, getIntent());
        browser = new Browser(this);
        initViews();
        realm.where(Person.class).isNotNull(FIELD_POST_NAME).findAllAsync().addChangeListener(people -> {
            final Collection<Person> newList = new ArrayList<>(0);
            for (final Person person : people) {
                newList.add(realm.copyFromRealm(person));
            }
            if (cocoRv.getAdapter() instanceof PersonAdapter) {
                ((PersonAdapter) cocoRv.getAdapter()).setPersonList(newList);
            } else {
                Log.e(TAG, "Adapter of cocoRv should support setPersonList");
            }
        });
        final MacApi api = new Retrofit.Builder()
                .baseUrl(MAC_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MacApi.class);
        api.getAndroidApps().enqueue(new Callback<List<AndroidApp>>() {
            @Override
            public void onResponse(@ParametersAreNonnullByDefault final Call<List<AndroidApp>> call,
                                   @ParametersAreNonnullByDefault final Response<List<AndroidApp>> response) {
                final List<AndroidApp> body = response.body();
                if (body == null) return;
                realm.executeTransactionAsync(realm -> {
                    for (final AndroidApp app : body) {
                        realm.insertOrUpdate(app);
                    }
                });
            }

            @Override
            public void onFailure(final Call<List<AndroidApp>> call, final Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
            }
        });
        api.getPostHolders().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(final Call<List<Person>> call, final Response<List<Person>> response) {
                final List<Person> people = response.body();
                if (people == null) {
                    return;
                }
                realm.executeTransactionAsync(realm -> {
                    for (final Person p : people) {
                        realm.insertOrUpdate(p);
                    }
                });
            }

            @Override
            public void onFailure(final Call<List<Person>> call, final Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
            }
        });

        api.getSocialLinks().enqueue(new Callback<List<SocialLink>>() {
            @Override
            public void onResponse(final Call<List<SocialLink>> call, final Response<List<SocialLink>> response) {
                final List<SocialLink> socialLinks = response.body();
                if (socialLinks == null) {
                    return;
                }
                realm.executeTransactionAsync(realm -> {
                    for (final SocialLink link : socialLinks) {
                        realm.insertOrUpdate(link);
                    }
                });
            }

            @Override
            public void onFailure(final Call<List<SocialLink>> call, final Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    private void initViews() {
        setContentView(R.layout.activity_about_app);
        final Toolbar toolbar = findViewById(R.id.about_app_toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.tv_app_name)).setText(appName);
        ((TextView) findViewById(R.id.tv_app_desc)).setText(appDesc);
        final RecyclerView socialRv = findViewById(R.id.rv_social_links);
        socialRv.setLayoutManager(new GridLayoutManager(this, getSpanCount(100)));
        socialRv.setAdapter(new SocialAdapter(browser));
        final RecyclerView contributorsRv = findViewById(R.id.rv_contributors);
        contributorsRv.setLayoutManager(new LinearLayoutManager(this));
        contributorsRv.setAdapter(new PersonAdapter(appDevelopers, browser));
        cocoRv = findViewById(R.id.rv_coco);
        cocoRv.setLayoutManager(new LinearLayoutManager(this));
        cocoRv.setAdapter(new PersonAdapter(realm.where(Person.class).isNotNull(FIELD_POST_NAME).findAll(), browser));
        final RecyclerView androidAppsRv = findViewById(R.id.rv_android_apps);
        androidAppsRv.setLayoutManager(new LinearLayoutManager(this));
        androidAppsRv.setAdapter(new AndroidAppAdapter(browser));
    }

    public static int getSpanCount(final int viewWidthInDp) {
        final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        final int displayWidthInDp = displayMetrics.widthPixels / displayMetrics.densityDpi;
        return displayWidthInDp / viewWidthInDp;
    }

    private void acquireData(final Bundle bundle, final Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_APP_DESC)) {
                appDesc = intent.getStringExtra(EXTRA_APP_DESC);
            }
            if (intent.hasExtra(EXTRA_APP_DEV)) {
                appDevelopers = Arrays.asList((new Gson()).fromJson(intent.getStringExtra(EXTRA_APP_DEV), Person[].class));
            }
            if (intent.hasExtra(EXTRA_APP_NAME)) {
                appName = intent.getStringExtra(EXTRA_APP_NAME);
            }
        } else if (bundle != null) {
            if (bundle.containsKey(EXTRA_APP_DESC)) {
                appDesc = bundle.getString(EXTRA_APP_DESC);
            }
            if (bundle.containsKey(EXTRA_APP_DEV)) {
                appDevelopers = Arrays.asList((new Gson()).fromJson(bundle.getString(EXTRA_APP_DEV), Person[].class));
            }
            if (bundle.containsKey(EXTRA_APP_NAME)) {
                appName = bundle.getString(EXTRA_APP_NAME);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putString(EXTRA_APP_DESC, appDesc);
        outState.putString(EXTRA_APP_DEV, (new Gson()).toJson(appDevelopers.toArray()));
        outState.putString(EXTRA_APP_NAME, appName);
        super.onSaveInstanceState(outState);
    }
}
