package com.noatnoat.pianoapp.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.noatnoat.pianoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Helper {


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static long lastClickTime;

    public static boolean isDoubleClick() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = currentTimeMillis;
        return false;
    }

    public static boolean isGrantPermssion(Context context, String permission) {
        try {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
                return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void showToast(Context context, String t, boolean isShort) {
        if (isShort) Toast.makeText(context, t, Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, t, Toast.LENGTH_LONG).show();
    }

    public static void myLog(String t) {
        Log.d("myLog", t);
    }


    public static boolean isConnectedInternet(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setColorStatusBarWithColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }
    
    public static String formatDateToday(String typeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(typeFormat);
        Date date = new Date();
        return format.format(date);
    }

    public static String formatDateToday(String typeFormat, long timeMiliseconds) {
        SimpleDateFormat format = new SimpleDateFormat(typeFormat);
        Date date = new Date(timeMiliseconds);
        return format.format(date);
    }

    public static void shareApp(Context context) {
        try {
            int applicationNameId = context.getApplicationInfo().labelRes;
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(applicationNameId));
            String text = "";
            String link = context.getString(R.string.PLAY_STORE_APP_URL) + context.getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            context.startActivity(Intent.createChooser(i, "Share App"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void callPublisherPlayStore(Context context, String publisherName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + publisherName)));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.PLAY_STORE_DEV_URL) + "" + publisherName)));
        }
    }

    public static void feedback(Context context) {
        try {
            context.startActivity(createEmailIntent(context));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Err!!!\nPlease try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent createEmailIntent(Context context) {
        try {
            String toEmail = context.getString(R.string.EMAIL_DEVELOPER);

            PackageManager manager = context.getPackageManager();
            String subject = "FeedBack from Android";
            String message = "Content : ";

            Intent sendTo = new Intent(Intent.ACTION_SENDTO);
            String uriText = "mailto:" + Uri.encode(toEmail) +
                    "?subject=" + Uri.encode(subject) +
                    "&body=" + Uri.encode(message);
            Uri uri = Uri.parse(uriText);
            sendTo.setData(uri);
            List<ResolveInfo> resolveInfos =
                    context.getPackageManager().queryIntentActivities(sendTo, 0);

            if (!resolveInfos.isEmpty()) {
                return sendTo;
            }

            Intent send = new Intent(Intent.ACTION_SEND);

            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{toEmail});
            send.putExtra(Intent.EXTRA_SUBJECT, subject);
            send.putExtra(Intent.EXTRA_TEXT, message);
            return Intent.createChooser(send, "send feedback for developer");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void callPlayStore(Context context, String packageName) {
        if (packageName.contains("https://")) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(packageName)));
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }
    }

    public static float getDensity(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    public static int convertDiptoPix(Context context, int dip) {
        float scale = getDensity(context);
        return (int) (dip * scale + 0.5f);
    }

    public static int convertPixtoDip(Context context, int pixel) {
        float scale = getDensity(context);
        return (int) ((pixel - 0.5f) / scale);
    }
}