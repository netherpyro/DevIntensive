package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    TextView mFullNameNav;
    TextView mEmailNav;
    ImageView mAvatar;

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.repository_et, R.id.about_self_et})
    List<EditText> mUserInfoViews;
    @BindViews({R.id.call_img, R.id.sendto_img, R.id.view_vk_profile_img, R.id.view_git_img})
    List<ImageView> mImageViews;
    @BindViews({R.id.rating_value, R.id.coded_lines_value, R.id.projects_value})
    List<TextView> mUserValueViews;

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        for (ImageView imageView : mImageViews) {
            imageView.setOnClickListener(this);
        }
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        /*mUserInfoViews.get(0).addTextChangedListener(new MaskedWatcher("+7(###) ###-##-##"));
        mUserInfoViews.get(2).addTextChangedListener(new MaskedWatcher("vk.com******************"));
        mUserInfoViews.get(3).addTextChangedListener(new MaskedWatcher("github.com******************"));*/

        mFullNameNav = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_full_name_tv);
        mEmailNav = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email_txt);
        mAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);

        setupToolbar();
        initImgs();
        setupDrawer();
        initUserFields();
        initUserInfoValue();
        initUserFullNameAndEmail();

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
        changeEditMode(mCurrentEditMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    mCurrentEditMode = 1;
                } else {
                    mCurrentEditMode = 0;
                }
                changeEditMode(mCurrentEditMode);
                break;
            case R.id.profile_placeholder:
                //// TODO: make choice from load the picture
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_img:
                String number = mUserInfoViews.get(0).getText().toString();
                if (!number.equals("") || !number.equals("null")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", number, null));

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{
                                android.Manifest.permission.CALL_PHONE
                        }, ConstantManager.PERMISSION_CALL_REQUEST_CODE);

                        Snackbar.make(mCoordinatorLayout, R.string.permission_alert_message, Snackbar.LENGTH_LONG)
                                .setAction("Разрешить", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openApplicationSettings();
                                    }
                                }).show();
                    } else {
                        try {
                            startActivity(callIntent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case R.id.sendto_img:
                String email = mUserInfoViews.get(1).getText().toString();
                if (!email.equals("") || !email.equals("null")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
                    } catch (ActivityNotFoundException e) {
                        showSnackBar(getString(R.string.permission_alert_message));
                    }
                }

                break;

            case R.id.view_vk_profile_img:
                String vkUrl = mUserInfoViews.get(2).getText().toString();
                if (!vkUrl.equals("") || !vkUrl.equals("null")) {
                    Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + vkUrl));

                    try {
                        startActivity(vkIntent);
                    } catch (ActivityNotFoundException e) {
                        showSnackBar(getString(R.string.permission_alert_message));
                    }
                }
                break;

            case R.id.view_git_img:
                String gitUrl = mUserInfoViews.get(3).getText().toString();
                if (!gitUrl.equals("") || !gitUrl.equals("null")) {
                    Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + gitUrl));

                    try {
                        startActivity(gitIntent);
                    } catch (ActivityNotFoundException e) {
                        showSnackBar(getString(R.string.permission_alert_message));
                    }
                }
                break;
        }
    }


    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.userphoto);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);
        roundedBitmapDrawable.setCircular(true);
        ImageView imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        imageView.setImageDrawable(roundedBitmapDrawable);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackBar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из другой Activity (фото из камеры или галереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * Переключает режим редактирования
     *
     * @param mode если 1 режим редактирования, если 0 режим просмотра
     */
    private void changeEditMode(int mode) {

        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
                mUserInfoViews.get(0).requestFocus();


            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }

            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().
                    getColor(R.color.color_white));

            saveUserFields();
        }
    }

    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserFields() {
        List<String> userData = new ArrayList<>();

        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValue();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    private void initUserFullNameAndEmail() {
        mFullNameNav.setText(mDataManager.getPreferencesManager().loadFullName());
        mEmailNav.setText(mDataManager.getPreferencesManager().loadUserProfileData().get(1));
    }

    private void initImgs() {
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg) // TODO: 01.07.16 сделать placeholder, transform + crop
                .into(mProfileImage);

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadAvatar())
                .placeholder(R.drawable.userphoto)
                .into(mAvatar);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START))
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        takeGalleryIntent.setType("/image/*");
        startActivityForResult(
                Intent.createChooser(takeGalleryIntent, getString(R.string.profile_choose_message)),
                ConstantManager.REQUEST_GALLERY_PICTURE
        );
    }

    private void loadPhotoFromCamera() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 01.07.16 обработать ошибку
            }

            if (mPhotoFile != null) {
                // TODO: 01.07.16 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.PERMISSION_CAMERA_REQUEST_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.permission_alert_message, Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.PERMISSION_CAMERA_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 01.07.16 тут обрабатываем разрешение (разрешение получено), например,
                // вывести сообщение или обработать какой-то логикой, если нужно
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 01.07.16 тут обрабатываем разрешение (разрешение получено), например,
                // вывести сообщение или обработать какой-то логикой, если нужно
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        );
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {
                        getString(R.string.profile_dialog_gallery),
                        getString(R.string.profile_dialog_camera),
                        getString(R.string.profile_dialog_cancel)
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 3:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        showSnackBar("insertImage" + selectedImage);
        Picasso.with(this)
                .load(selectedImage)
                // TODO: 01.07.16 сделать placeholder, transform + crop
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_SETTINGS_REQUEST_CODE);
    }
}
