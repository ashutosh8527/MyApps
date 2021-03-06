package my.ak8527.ashu.supersearch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import my.ak8527.ashu.supersearch.Adaptor.MediaAdaptor;
import my.ak8527.ashu.supersearch.AsyncWork.AppTask;
import my.ak8527.ashu.supersearch.AsyncWork.ContactTask;
import my.ak8527.ashu.supersearch.AsyncWork.MediaTask;
import my.ak8527.ashu.supersearch.AsyncWork.StorageTask;

import my.ak8527.ashu.supersearch.BuildConfig;

import my.ak8527.ashu.supersearch.Info.InfoList;
import my.ak8527.ashu.supersearch.Interface.MediaResponse;
import my.ak8527.ashu.supersearch.Media.MediaInfo;

import my.ak8527.ashu.supersearch.R;

import my.ak8527.ashu.supersearch.setting.SettingActivity;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static my.ak8527.ashu.supersearch.setting.SettingActivity.MY_SETTING_PREF;

//import AppTask;


@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MediaResponse {
    private static final int MY_STORAGE_REQUEST_CODE = 222;
    private static final int MY_CONTACT_REQUEST_CODE = 333;
    private static final int MY_STORAGE_AND_CONTACT_REQUEST_CODE = 444;


    @BindView(R.id.filesRecyclerView)
    RecyclerView filesRecyclerView;
    @BindView(R.id.appRecyclerView)
    RecyclerView appRecyclerView;
    @BindView(R.id.contactRecyclerView)
    RecyclerView contactRecyclerView;
    @BindView(R.id.songRecyclerView)
    RecyclerView songRecyclerView;
    @BindView(R.id.videoRecyclerView)
    RecyclerView videoRecyclerView;
    @BindView(R.id.settingRecyclerView)
    RecyclerView settingRecyclerView;
    @BindView(R.id.searchAppRecyclerView)
    RecyclerView searchAppRecyclerView;


    private MediaAdaptor storageAdapter;
    private MediaAdaptor appAdaptor;
    private MediaAdaptor searchAppAdaptor;
    private MediaAdaptor settingAdaptor;
    private MediaAdaptor contactAdaptor;
    private MediaAdaptor songAdaptor;
    private MediaAdaptor videoAdaptor;


    private final ArrayList<MediaInfo> mySongArrayList = new ArrayList<>();
    private final ArrayList<MediaInfo> myContactList = new ArrayList<>();
    private final ArrayList<MediaInfo> myAppArrayList = new ArrayList<>();
    private final ArrayList<MediaInfo> myVideoArrayList = new ArrayList<>();
    private final ArrayList<MediaInfo> myStorageList = new ArrayList<>();
    private final ArrayList<MediaInfo> mySettingList = new ArrayList<>();


    private PopupMenu popupMenu;


    @BindView(R.id.moreFileView)
    TextView moreTv;
    @BindView(R.id.permissionLayout)
    ConstraintLayout permissionLayout;
    @BindView(R.id.contactName)
    TextView contactView;
    @BindView(R.id.appName)
    TextView appView;
    @BindView(R.id.songName)
    TextView songView;
    @BindView(R.id.filesName)
    TextView fileView;
    @BindView(R.id.videoName)
    TextView videoView;
    @BindView(R.id.settingName)
    TextView settingView;
    @BindView(R.id.searchName)
    TextView searchName;
    @BindView(R.id.permissionBtn)
    Button permissionBtn;
    @BindView(R.id.threeDotMenu)
    ImageView settingMenu;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.lineView) View view;
    public static boolean layout = true;
    @BindView(R.id.scrollV)
    ScrollView scrollV;




    SharedPreferences sharedPreferences;
    public static final int MY_TELEPHONE_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBarPosition();
        ButterKnife.bind(this);
        InfoList infoList = new InfoList(this);
        if (layout){
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_SETTING_PREF,MODE_PRIVATE).edit();
                editor.putString("background_switch", "false");
                editor.apply();
                layout = false;
        }

        setHelperText();



        /*
         * Setting Layout Manager for recyclerView.
         */

        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int browserListSize = infoList.getBrowserList().size();
        if (browserListSize > 6) {
            browserListSize = 6;
        }
        GridLayoutManager searchLayoutManager = new GridLayoutManager(this, browserListSize);
        searchAppRecyclerView.setLayoutManager(searchLayoutManager);
        int pixel = dpToPixel(12);
        searchAppRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(pixel));
        GridLayoutManager appLayout = new GridLayoutManager(this, 5);
        appRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(pixel));
        appRecyclerView.setLayoutManager(appLayout);


        /*
         * Checking contact and storage permission and executing task according to them.
         */
        if (isContactPermission() && isStoragePermission()) {
            isContactTaskExecute();
            isStorageTaskExecute();
            isPermissionLayoutVisible();
        } else if (isContactPermission() && !isStoragePermission()) {
            isContactTaskExecute();
        } else if (!isContactPermission() && isStoragePermission()) {
            isStorageTaskExecute();
        }



        searchAppAdaptor = new MediaAdaptor(this, (ArrayList<MediaInfo>) infoList.getBrowserList(), MediaAdaptor.SEARCH_APP_ID);
        searchAppRecyclerView.setAdapter(searchAppAdaptor);
        searchAppRecyclerView.setVisibility(View.GONE);


        /*
         * Calling SearchView widget.
         */

        searchView.setOnQueryTextListener(this);

        /*
         * Calling methods for setting contact, storage and app adaptor.
         */
        setAppAdaptor();
        setStorageAdapter();
        setContactAdaptor();

        popupMenu = new PopupMenu(this,settingMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());








    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * @param newText is text which is search on searchView widget.
     * @return false
     */

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            searchAppRecyclerView.setVisibility(View.VISIBLE);
            searchName.setVisibility(View.VISIBLE);
            searchAppAdaptor.filterSearchApp(newText);
            setSearchApp(newText);
        } else {
            searchAppRecyclerView.setVisibility(View.GONE);
            searchName.setVisibility(View.GONE);
        }

        boolean appAns, settingAns;

        settingAns = settingAdaptor.filter(newText);
        settingAdaptor.notifyDataSetChanged();
        if (settingAns) {
            settingView.setVisibility(View.VISIBLE);
        } else {
            settingView.setVisibility(View.GONE);
        }
        appAns = appAdaptor.filter(newText);
        appAdaptor.notifyDataSetChanged();
        if (appAns) {
            appView.setVisibility(View.VISIBLE);
        } else {
            appView.setVisibility(View.GONE);
        }


        if (isContactPermission()) {
            boolean contactAns = contactAdaptor.filter(newText);
            contactAdaptor.notifyDataSetChanged();
            if (contactAns) {
                contactView.setVisibility(View.VISIBLE);
            } else {
                contactView.setVisibility(View.GONE);
            }
        }


        if (isStoragePermission()) {

            boolean storageAns = storageAdapter.filter(newText);

            storageAdapter.notifyDataSetChanged();
            if (storageAns) {
                boolean moreStorage = storageAdapter.isMoreFiles();
                if (moreStorage) {
                    moreTv.setVisibility(View.VISIBLE);
                    storageAdapter.moreFiles(false);
                } else {
                    moreTv.setVisibility(View.GONE);

                    storageAdapter.moreFiles(false);
                }
                fileView.setVisibility(View.VISIBLE);
            } else {
                moreTv.setVisibility(View.GONE);
                fileView.setVisibility(View.GONE);
            }


            boolean songAns = songAdaptor.filter(newText);
            songAdaptor.notifyDataSetChanged();
            if (songAns) {
                songView.setVisibility(View.VISIBLE);
            } else {
                songView.setVisibility(View.GONE);
            }

            boolean videoAns = videoAdaptor.filter(newText);
            videoAdaptor.notifyDataSetChanged();
            if (videoAns) {
                videoView.setVisibility(View.VISIBLE);
            } else {
                videoView.setVisibility(View.GONE);
            }


        }
        scrollV.requestChildFocus(view,view);
        return false;
    }


    /**
     * initializing and setting adaptor for song, video and files.
     */

    private void setStorageAdapter() {
        storageAdapter = new MediaAdaptor(this, myStorageList, MediaAdaptor.FILE_ID);
        filesRecyclerView.setAdapter(storageAdapter);

        songAdaptor = new MediaAdaptor(this, mySongArrayList, MediaAdaptor.AUDIO_ID);
        songRecyclerView.setAdapter(songAdaptor);

        videoAdaptor = new MediaAdaptor(this, myVideoArrayList, MediaAdaptor.VIDEO_ID);
        videoRecyclerView.setAdapter(videoAdaptor);

    }


    /**
     * initializing and setting adaptor for contact.
     */

    private void setContactAdaptor() {

        contactAdaptor = new MediaAdaptor(this, myContactList, MediaAdaptor.CONTACT_ID);
        contactRecyclerView.setAdapter(contactAdaptor);


    }


    private void setAppAdaptor() {
        /*
           Set recyclerView for App.
           (ArrayList<App>) infoList.GetAllInstalledApkInfo()
         */
        appAdaptor = new MediaAdaptor(this, myAppArrayList, MediaAdaptor.APP_ID);
        appRecyclerView.setNestedScrollingEnabled(false);
        appRecyclerView.setAdapter(appAdaptor);

        settingAdaptor = new MediaAdaptor(this, mySettingList, MediaAdaptor.SETTING_ID);
        settingRecyclerView.setAdapter(settingAdaptor);

        new AppTask(this, this).execute();


    }

    private void isPermissionGranted() {
        if (!isContactPermission() && !isStoragePermission()) {

            /*
             * Requesting Permission for both Contact and Storage.
             */

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_AND_CONTACT_REQUEST_CODE);

        } else if (!isContactPermission()) {

            /*
             * Requesting Contact Permission.
             */

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_CONTACT_REQUEST_CODE);

        } else if (!isStoragePermission()) {
            /*
             * Requesting Storage Permission.
             */

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);

        }
    }

    /**
     * @param requestCode  for checking which runtime permission is called.
     * @param permissions  for requesting the desired permission.
     * @param grantResults for checking if the permission is granted.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_TELEPHONE_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactAdaptor.calling();
                }
                return;
            }


            case MY_STORAGE_AND_CONTACT_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        isContactTaskExecute();
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        isStorageTaskExecute();
                }
                isPermissionLayoutVisible();
                return;
            }

            case MY_CONTACT_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    new ContactTask(this, this).execute();
                isPermissionLayoutVisible();
                return;
            }

            case MY_STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    isStorageTaskExecute();
                isPermissionLayoutVisible();
            }

        }
    }



    /**
     * @return storagePermission status.
     */

    private boolean isStoragePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * @return contactPermission status.
     */


    private boolean isContactPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Method for execution of SongTask,VideoTask and StorageTask.
     */

    private void isStorageTaskExecute() {
        new MediaTask(this, this).execute();
        new StorageTask(this, this).execute();
    }

    /**
     * Method for execution of ContactTask.
     */

    private void isContactTaskExecute() {
        new ContactTask(this, this).execute();
    }

    /**
     * Method for setting the permissionLayout invisible.
     */

    private void isPermissionLayoutVisible() {
        if (isStoragePermission() && isContactPermission()) {
            permissionLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void getAppResponse(ArrayList<MediaInfo> appArrayList, ArrayList<MediaInfo> settingArrayList) {
        myAppArrayList.clear();
        myAppArrayList.addAll(appArrayList);
        appAdaptor.notifyDataSetChanged();

        mySettingList.clear();
        mySettingList.addAll(settingArrayList);
        settingAdaptor.notifyDataSetChanged();
    }

    @Override
    public void getContactResponse(ArrayList<MediaInfo> contactArrayList) {
        myContactList.clear();
        myContactList.addAll(contactArrayList);
        contactAdaptor.notifyDataSetChanged();
    }

    @Override
    public void getStorageResponse(ArrayList<MediaInfo> storageArrayList) {
        myStorageList.clear();
        myStorageList.addAll(storageArrayList);
        storageAdapter.notifyDataSetChanged();

    }

    @Override
    public void getMediaResponse(ArrayList<MediaInfo> songArrayList, ArrayList<MediaInfo> videoArrayList) {
        mySongArrayList.clear();
        mySongArrayList.addAll(songArrayList);
        songAdaptor.notifyDataSetChanged();

        myVideoArrayList.clear();
        myVideoArrayList.addAll(videoArrayList);
        videoAdaptor.notifyDataSetChanged();
    }


    private void setSearchApp(String text) {
        String searchStart = "Search ";
        String newText = searchStart + text;
        SpannableString str = new SpannableString(newText);
        int startingIndex = searchStart.length();
        int endingIndex = startingIndex + text.length();
        str.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchName.setText(str);
    }

    private void setHelperText() {
        appView.setText(R.string.apps);
        contactView.setText(R.string.contact);
        fileView.setText(R.string.files);
        songView.setText(R.string.song);
        videoView.setText(R.string.videos);
        settingView.setText(R.string.settings);
    }


    public void getBarPosition(){
        sharedPreferences = getSharedPreferences(MY_SETTING_PREF,MODE_PRIVATE);
        int radioId = sharedPreferences.getInt("radio_id",0);
        if (R.id.topBar == radioId){
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.reverse_activity_main);
        }
    }


    @OnClick(R.id.moreFileView)
    public void moreFileView(){
        storageAdapter.moreFiles(true);
        storageAdapter.notifyDataSetChanged();
        moreTv.setVisibility(View.GONE);
    }


    @OnClick(R.id.permissionBtn)
    public void permissionReq(){
        isPermissionGranted();
    }



    @OnClick(R.id.threeDotMenu)
    public void setSettingMenu(){
        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingOption:
                        Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        return true;

                    case R.id.helpFeedbackMenu:
                        helpDialog();
                        return true;


                    default:
                        return MainActivity.super.onOptionsItemSelected(item);

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == MediaAdaptor.APP_UNINSTALL_REQUEST_CODE)
            {
                String packageName = sharedPreferences.getString("app_package_name",null);
                if (!isAppPersist(packageName)){
                    int position = sharedPreferences.getInt("app_position",0);
                    Iterator<MediaInfo> appIterator = myAppArrayList.iterator();
                    while (appIterator.hasNext())
                    {
                        if (appIterator.next().getMediaPath().equals(packageName)){
                            appIterator.remove();
                            appAdaptor.notifyChange(position);
                        }
                    }
                }

            }
    }

    private boolean isAppPersist(String packageName){
        ApplicationInfo applicationInfo;

        try {
            applicationInfo = this.getPackageManager().getApplicationInfo(packageName,0);
            return applicationInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void helpDialog(){
//        final View view = LayoutInflater.from(this).inflate(R.layout.help_setting_dialog,null);
        View view = View.inflate(this,R.layout.help_setting_dialog,null);

       final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Help & Feedback")
                .setView(view)
                .show();
        ConstraintLayout helpSettingDialog = view.findViewById(R.id.helpSettingDialog);
        helpSettingDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.dismiss();
                String subject = "Feedback for Super Search";
                String bodyText = "App Version : " + BuildConfig.VERSION_NAME
                        + "\nAndroid Version : " + Build.VERSION.SDK_INT
                        + "\nDevice : " + Build.BRAND + " " +Build.MODEL;
                String mailto = "mailto:ashutoshkumar1320@gmail.com" +
                        "?subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(bodyText);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (Exception e){
                    Toast.makeText(getBaseContext(),"Sorry, Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private int dpToPixel(int size) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) size * density);
    }

    public class EqualSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;
        private int displayMode;

        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;
        public static final int GRID = 2;

        public EqualSpacingItemDecoration(int spacing) {
            this(spacing, -1);
        }

        public EqualSpacingItemDecoration(int spacing, int displayMode) {
            this.spacing = spacing;
            this.displayMode = displayMode;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildViewHolder(view).getAdapterPosition();
            int itemCount = state.getItemCount();
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            setSpacingForDirection(outRect, layoutManager, position, itemCount);
        }

        private void setSpacingForDirection(Rect outRect,
                                            RecyclerView.LayoutManager layoutManager,
                                            int position,
                                            int itemCount) {

            // Resolve display mode automatically
            if (displayMode == -1) {
                displayMode = resolveDisplayMode(layoutManager);
            }

            switch (displayMode) {
                case HORIZONTAL:
                    outRect.left = spacing;
                    outRect.right = position == itemCount - 1 ? spacing : 0;
                    outRect.top = spacing;
                    outRect.bottom = spacing;
                    break;
                case VERTICAL:
                    outRect.left = spacing;
                    outRect.right = spacing;
                    outRect.top = spacing;
                    outRect.bottom = position == itemCount - 1 ? spacing : 0;
                    break;
                case GRID:
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        int cols = gridLayoutManager.getSpanCount();
                        int rows = itemCount / cols;
                        int space = (spacing + dpToPixel(4))/2;
                        outRect.left = spacing;
                        outRect.right = position % cols == cols - 1 ? spacing : 0;
                        outRect.top = space;
                        outRect.bottom = position / cols == rows - 1 ? space : 0;
                    }
                    break;
            }
        }

        private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
            if (layoutManager instanceof GridLayoutManager) return GRID;
            if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
            return VERTICAL;
        }
    }


}
