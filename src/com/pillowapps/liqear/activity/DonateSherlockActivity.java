package com.pillowapps.liqear.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.R;
import net.robotmedia.billing.BillingController.BillingStatus;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingActivity;
import net.robotmedia.billing.model.Transaction.PurchaseState;

@SuppressWarnings("UnusedParameters")
public class DonateSherlockActivity extends AbstractBillingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate_layout);
    }

    @Override
    protected void onStart() {
        if(getResources().getBoolean(R.bool.analytics_enabled)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public void on1(View v) {
        purchase("donate_1");
    }

    public void on3(View v) {
        purchase("donate_3");
    }

    public void on5(View v) {
        purchase("donate_5");
    }

    public void on10(View v) {
        purchase("donate_10");
    }

    public void on20(View v) {
        purchase("donate_20");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onNumberClicked(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy number", ((Button) v).getText()
                .toString());
        Toast.makeText(DonateSherlockActivity.this, getResources().getString(R.string.copied), Toast.LENGTH_SHORT).show();
        clipboard.setPrimaryClip(clip);
    }

    public void purchase(String item) {
        if (checkBillingSupported() != BillingStatus.SUPPORTED) {
            showBillingNotSupportedDialog();
        } else {
            requestPurchase(item);
        }
    }

    private void showBillingNotSupportedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.billing_not_supported).setTitle(
                R.string.sorry);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //HIDDEN FOR REPOSITORY
    public byte[] getObfuscationSalt() {
        return new byte[]{1, 2, 3};
    }

    //HIDDEN FOR REPOSITORY
    public String getPublicKey() {
        return "";
    }

    @Override
    public void onBillingChecked(boolean supported) {
        // No operations.
    }

    @Override
    public void onSubscriptionChecked(boolean supported) {
        // No operations.
    }

    @Override
    public void onPurchaseStateChanged(String itemId, PurchaseState state) {
        if (state == PurchaseState.PURCHASED) {
            showThanksDialog();
        }

    }

    private void showThanksDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.thanks_message).setTitle(
                R.string.sincere_thanks);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent send = new Intent(Intent.ACTION_SENDTO);
                        String uriText;
                        uriText = "mailto:pillowapps@gmail.com"
                                + "?subject=Liquid Bear Donator message";
                        uriText = uriText.replace(" ", "%20");
                        Uri uri = Uri.parse(uriText);

                        send.setData(uri);
                        startActivity(Intent.createChooser(send, "Send mail..."));
                    }
                });
        builder.setNegativeButton(R.string.exit,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        DonateSherlockActivity.this.finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPurchaseResponse(String itemId, ResponseCode response) {
        // No operations.
    }
}
