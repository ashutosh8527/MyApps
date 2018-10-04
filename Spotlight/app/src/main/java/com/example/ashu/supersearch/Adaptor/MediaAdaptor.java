package com.example.ashu.supersearch.Adaptor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ashu.supersearch.Media.MediaInfo;
import com.example.ashu.supersearch.MyDialog.MyPopDialog;
import com.example.ashu.supersearch.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class MediaAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int APP_ID = 0;
    public static final int CONTACT_ID = 1;
    public static final int SETTING_ID = 2;
    public static final int AUDIO_ID = 3;
    public static final int VIDEO_ID = 4;
    public static final int FILE_ID = 5;
    public static final int SEARCH_APP_ID = 6;
    private String spannableText;
    private final ArrayList<MediaInfo> myAppArrayList = new ArrayList<>();
    private static final int MY_TELEPHONE_REQUEST_CODE = 111;
    private String searchText = "";
    private boolean moreFiles = false;


    private final Context context;
    private final ArrayList<MediaInfo> mediaInfoArrayList;
    private final int mediaId;
    private String phoneNumber;

    public MediaAdaptor(Context context, ArrayList<MediaInfo> mediaInfoArrayList, int mediaId) {
        this.context = context;
        this.mediaInfoArrayList = mediaInfoArrayList;
        this.mediaId = mediaId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;

        switch (mediaId) {
            case APP_ID:
            case SEARCH_APP_ID:
                view = layoutInflater.inflate(R.layout.app_list_item, parent, false);
                return new AppHolder(view);
            case SETTING_ID:
            case AUDIO_ID:
            case VIDEO_ID:
            case FILE_ID:
            case CONTACT_ID:

                view = layoutInflater.inflate(R.layout.media_list_item, parent, false);
                return new MediaHolder(view);

            default:
                view = layoutInflater.inflate(R.layout.media_list_item, parent, false);
                return new MediaHolder(view);
        }

    }

    private SpannableString str;


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MediaInfo mediaInfo;
        int itemId = getItemViewType(position);
        if (itemId == SEARCH_APP_ID) {
            mediaInfo = mediaInfoArrayList.get(position);
        } else {
            mediaInfo = myAppArrayList.get(position);
            String mediaName = mediaInfo.getMediaName();
            str = new SpannableString(mediaName);
            String testText = mediaName.toLowerCase(Locale.getDefault());
            String testTextToBold = spannableText.toLowerCase(Locale.getDefault());
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            str.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        switch (itemId) {



            case APP_ID: {
                AppHolder appHolder = (AppHolder) holder;
                final String packageName = mediaInfo.getMediaPath();
                appHolder.imageView.setImageDrawable(getAppIconByPackageName(packageName));
                appHolder.textView.setText(str);
                appHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                        context.startActivity(intent);
                    }
                });

                appHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenu(v,mediaInfo,APP_ID);
                        return false;
                    }
                });
                break;
            }




            case CONTACT_ID: {
                MediaHolder contactHolder = (MediaHolder) holder;
                contactHolder.mediaNameTv.setText(str);
                phoneNumber = mediaInfo.getMediaPath();
                contactHolder.callImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(intent);

                        } else {
                            requestPermissions(
                                    (Activity) context, new String[]{Manifest.permission.CALL_PHONE}, MY_TELEPHONE_REQUEST_CODE);


                        }
                    }
                });

                contactHolder.chatImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                    }
                });

                contactHolder.callImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                            context.startActivity(intent);

                        } else {
                            requestPermissions(
                                    (Activity) context, new String[]{Manifest.permission.CALL_PHONE},MY_TELEPHONE_REQUEST_CODE);


                        }
                    }
                });

                break;
            }



            case SETTING_ID: {
                MediaHolder mediaHolder = (MediaHolder) holder;
                mediaHolder.mediaNameTv.setText(str);
                final String packageName = mediaInfo.getMediaPath();
                mediaHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(packageName);
                        context.startActivity(intent);
                    }
                });
                break;
            }




            case AUDIO_ID: {
                final MediaHolder mediaHolder = (MediaHolder) holder;

                mediaHolder.mediaNameTv.setText(str);
                mediaHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(mediaInfo.getMediaPath());
                        mediaOpen("audio/*", file);
                    }
                });

                mediaHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenu(v,mediaInfo,AUDIO_ID);
                        return false;
                    }
                });

                break;
            }
            case VIDEO_ID: {
                final MediaHolder mediaHolder = (MediaHolder) holder;

                mediaHolder.mediaNameTv.setText(str);
                Glide.with(context)
                        .asBitmap()
                        .load(mediaInfo.getMediaPath())
                        .into(mediaHolder.mediaIv);

                mediaHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(mediaInfo.getMediaPath());
                        mediaOpen("video/*", file);
                    }
                });

                mediaHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenu(v,mediaInfo,VIDEO_ID);
                        return false;
                    }
                });
                break;
            }
            case FILE_ID: {
                MediaHolder mediaHolder = (MediaHolder) holder;
                mediaHolder.mediaNameTv.setText(str);
                final File file = new File(mediaInfo.getMediaPath());

                String extension = extensionFind(mediaInfo.getMediaName());
                switch (extension) {
                    case "image":
                        mediaHolder.mediaIv.setImageResource(R.drawable.ic_action_image);
                        mediaHolder.mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.imageColor));
                        break;
                    case "text":
                        mediaHolder.mediaIv.setImageResource(R.drawable.ic_action_file);
                        mediaHolder.mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.fileColor));
                        break;
                    case "video":
                        mediaHolder.mediaIv.setImageResource(R.drawable.ic_action_movie);
                        mediaHolder.mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.movieColor));
                        break;
                    case "audio":
                        mediaHolder.mediaIv.setImageResource(R.drawable.ic_action_song);
                        mediaHolder.mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.audioColor));
                        break;
                    default:
                        mediaHolder.mediaIv.setImageResource(R.drawable.ic_action_folder);
                        mediaHolder.mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.folderColor));

                        break;
                }
                final String filePath = mediaInfo.getMediaPath();
                mediaHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (file.isFile()) {
                            resultDialog(filePath);
                           } else {
                            openFolder(filePath);
                        }
                    }
                });


                mediaHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (file.isFile()) {
                            showMenu(v,mediaInfo,FILE_ID);
                        } else {
                            showPropDialog(mediaInfo);
                        }
                        return false;
                    }
                });

                break;
            }

            case SEARCH_APP_ID: {
                AppHolder mediaHolder = (AppHolder) holder;
                Drawable drawable = getAppIconByPackageName(mediaInfo.getMediaPath());
                mediaHolder.imageView.setImageDrawable(drawable);
                mediaHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchAppOpen(mediaInfo);
                    }
                });
            }
        }
    }

    public void calling() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }


    public boolean filter(String text) {
        spannableText = text;
        boolean ans = false;
        text = text.toLowerCase(Locale.getDefault());
        myAppArrayList.clear();
        if (!text.isEmpty()) {
            for (MediaInfo mediaInfo : mediaInfoArrayList) {
                if (mediaInfo.getMediaName().toLowerCase(Locale.getDefault()).contains(text)) {
                    myAppArrayList.add(mediaInfo);
                    ans = true;
                }
            }
            notifyDataSetChanged();
        }
        return ans;
    }

    public void filterSearchApp(String text) {
        searchText = text;
        notifyDataSetChanged();
    }


    private Drawable getAppIconByPackageName(String ApkTempPackageName) {
        Drawable drawable;
        try {
            drawable = context.getPackageManager().getApplicationIcon(ApkTempPackageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        }
        return drawable;
    }


    @Override
    public int getItemViewType(int position) {
        return mediaId;
    }

    @Override
    public int getItemCount() {
        if (mediaId == APP_ID) {
            return myAppArrayList.size() <= 4 ? myAppArrayList.size() : 5;
        } else if (mediaId == SEARCH_APP_ID) {
            return mediaInfoArrayList.size() <= 6 ? mediaInfoArrayList.size() : 6;
        } else if (mediaId == FILE_ID) {
            return myAppArrayList.size() < 4 ? myAppArrayList.size() : moreFiles ? myAppArrayList.size() : 4;
        } else {
            return myAppArrayList.size() <= 3 ? myAppArrayList.size() : 3;
        }
    }

    public void moreFiles(boolean moreFiles) {
        this.moreFiles = moreFiles;
        notifyDataSetChanged();
    }


    public boolean isMoreFiles() {
        return myAppArrayList.size() > 4;
    }


    class MediaHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mediaName)
        TextView mediaNameTv;
        @BindView(R.id.mediaImage)
        CircleImageView mediaIv;
        @BindView(R.id.callIcon)
        ImageView callImageView;
        @BindView(R.id.chatIcon)
        ImageView chatImageView;

        MediaHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mediaId == CONTACT_ID) {
                mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.phoneColor));
                mediaIv.setImageResource(R.drawable.ic_action_contact);
                callImageView = itemView.findViewById(R.id.callIcon);
                callImageView.setVisibility(View.VISIBLE);
                chatImageView = itemView.findViewById(R.id.chatIcon);
                chatImageView.setVisibility(View.VISIBLE);
            } else if (mediaId == SETTING_ID) {
                mediaIv.setImageResource(R.drawable.ic_action_setting);
                mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            } else if (mediaId == AUDIO_ID) {
                mediaIv.setImageResource(R.drawable.ic_action_song);
                mediaIv.setCircleBackgroundColor(context.getResources().getColor(R.color.audioColor));

            }
        }
    }


    class AppHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.appNameTv) TextView textView;
        @BindView(R.id.appImageView) ImageView imageView;

        AppHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if (mediaId == SEARCH_APP_ID) {
                textView.setVisibility(View.GONE);
                imageView.getLayoutParams().height = dpToPixel();
                imageView.getLayoutParams().width = dpToPixel();
            }

        }
    }

    private int dpToPixel() {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) 32 * density);
    }


    private String extensionFind(String text) {
        String[] image = new String[]{".jpg", ".jpeg", ".png", ".gif"};
        String[] video = new String[]{".mkv", ".mp4", ".3gp", ".avi"};
        String[] audio = new String[]{".mp3", ".aac", ".wav", ".wma", ".ogg"};
        String[] doc = new String[]{".txt", ".doc", ".pdf", ".docs", ".xls"};

        for (String extension : image) {
            if (text.toLowerCase(Locale.getDefault()).endsWith(extension))
                return "image";
        }

        for (String extension : video) {
            if (text.toLowerCase(Locale.getDefault()).endsWith(extension))
                return "video";
        }

        for (String extension : doc) {
            if (text.toLowerCase(Locale.getDefault()).endsWith(extension))
                return "text";
        }

        for (String extension : audio) {
            if (text.toLowerCase(Locale.getDefault()).endsWith(extension))
                return "audio";
        }

        return "empty";
    }

    private void resultDialog(final String path) {

        @SuppressLint("InflateParams") final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null, true);
        final AlertDialog builder = new AlertDialog.Builder(context)
                .setTitle("Open As")
                .setView(dialogView)
                .show();
        TextView imageView, audioView, videoView, textView;
        imageView = dialogView.findViewById(R.id.dialogImageView);
        audioView = dialogView.findViewById(R.id.dialogAudioView);
        videoView = dialogView.findViewById(R.id.dialogVideoView);
        textView = dialogView.findViewById(R.id.dialogTextView);

        final File file = new File(path);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaOpen("image/*", file);
                builder.dismiss();
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaOpen("video/*", file);
                builder.dismiss();

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaOpen("text/*", file);
                builder.dismiss();

            }
        });

        audioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaOpen("audio/*", file);
                builder.dismiss();

            }
        });
    }




    private void mediaOpen(String type, File file) {
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, "com.example.ashu.supersearch.fileprovider", file);
        mediaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mediaIntent.setDataAndType(uri, type);
        context.startActivity(mediaIntent);

    }




    private void searchAppOpen(MediaInfo mediaInfo) {
        Intent intent;
        switch (mediaInfo.getMediaName()) {
            case "Google Play Store":
            case "YouTube":
                intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage(mediaInfo.getMediaPath());
                intent.putExtra("query", searchText);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                break;
            case "Google":
                intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, searchText);
                break;
            default:
                Uri uri = Uri.parse("http://www.google.com/search?q=" + searchText);
                intent = context.getPackageManager().getLaunchIntentForPackage(mediaInfo.getMediaPath());
                if (intent != null) {
                    intent.setData(uri);
                }
                break;
        }
        context.startActivity(intent);
    }

    private void openFolder(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "resource/folder");
        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        }
    }

    private void shareMedia(File file) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = FileProvider.getUriForFile(context, "com.example.ashu.supersearch.fileprovider", file);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.setType("*/*");
        context.startActivity(sharingIntent);
    }

    private void showPropDialog(MediaInfo mediaInfo) {
        MyPopDialog newFragment = MyPopDialog.newInstance(mediaInfo);
        Activity activity = (Activity) context;
        newFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "dialog");
    }


    @SuppressWarnings("WeakerAccess")
    private void showMenu(View v, final MediaInfo mediaInfo, final int id) {
        Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);

        final PopupMenu popupMenu = new PopupMenu(context, v);
        if (id == APP_ID){
            popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());


        } else {
            popupMenu.inflate(R.menu.media_popup_menu);

//            popupMenu.getMenuInflater().inflate(R.menu.media_popup_menu, popupMenu.getMenu());

        }

//        popupMenu.inflate(R.menu.media_popup_menu);
        popupMenu.show();

        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                File file = new File(mediaInfo.getMediaPath());
                switch (item.getItemId()) {
                    case R.id.folderMenu:
                        if (id == APP_ID){
                        openAppInfoPage(mediaInfo);
                    } else {
                        openFolder(file.getParent());
                    }
                        return true;

                    case R.id.shareMenu:
                        if (id == APP_ID){
                            openAppPlayStorePage(mediaInfo);
                        } else {
                            shareMedia(file);
                        }
                        return true;

                    case R.id.propMenu:
                        if (id == APP_ID){
                            uninstallApp(mediaInfo);
                        } else {
                            showPropDialog(mediaInfo);
                        }
                        return true;

                    default:
                        return false;

                }
            }
        });


    }

    private void openAppInfoPage(MediaInfo mediaInfo){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mediaInfo.getMediaPath()));
        context.startActivity(intent);
    }

    private void openAppPlayStorePage(MediaInfo mediaInfo){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + mediaInfo.getMediaPath());
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void uninstallApp(MediaInfo mediaInfo){
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + mediaInfo.getMediaPath()));
        context.startActivity(intent);
    }
}