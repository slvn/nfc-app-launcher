
package fr.slvn.nfc.app.aarwriter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.slvn.nfc.app.aarwriter.tools.NfcUtils;

public class MainActivity extends Activity {

    // Static
    private static final String EXTRA_PACKAGE_NAME = "package_name";
    private static final int REQUEST_CODE_APP_SELECTION = 1;

    // Views
    private EditText mEditText;
    private ProgressBar mProgressBar;

    // Utils
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mWaitTagFilters = new IntentFilter[] {
            new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progress);
        mEditText = (EditText) findViewById(R.id.edit_package);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unFreezeUi();
    }

    public void writeTag(View v) {
        String packageName = retrievePackageName();
        if (packageName != null) {
            freezeUi();
            Toast.makeText(this, getString(R.string.help_toast), Toast.LENGTH_LONG).show();
            mNfcAdapter.enableForegroundDispatch(this, getPendingIntent(packageName),
                    mWaitTagFilters, null);
        }
    }

    public void selectApplication(View v) {
        Intent intent = new Intent(this, ApplicationListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_APP_SELECTION);
    }

    private String retrievePackageName() {
        String packageName = mEditText.getText().toString();

        if (TextUtils.isEmpty(packageName))
            return null;

        return packageName.replaceAll(" ", "");
    }

    private PendingIntent getPendingIntent(String packageName) {
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeApplicationRecordOnTag(packageName, tag);
        }
    }

    private void writeApplicationRecordOnTag(String packageName, Tag tag) {
        NdefMessage msg = NfcUtils.getApplicationRecord(packageName);
        writeNdefMessageToTag(msg, tag);
        unFreezeUi();
    }

    private void writeNdefMessageToTag(NdefMessage message, Tag tag) {
        try {
            NfcUtils.writeTag(message, tag);
            printWritingResult(true, null);
        } catch (Exception e) {
            e.printStackTrace();
            printWritingResult(false, e);
        }
    }

    private void printWritingResult(boolean result, Exception exception) {
        Toast.makeText(this, getErrorMessage(result, exception), Toast.LENGTH_LONG).show();
    }

    private String getErrorMessage(boolean result, Exception exception) {

        StringBuilder sb = new StringBuilder();

        if (result)
            sb.append(getString(R.string.error_success));
        else
            sb.append(getString(R.string.error_fail));

        if (exception != null) {
            sb.append(exception.getMessage());
        }

        return sb.toString();
    }

    private void freezeUi() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void unFreezeUi() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_APP_SELECTION:
                if (resultCode == RESULT_OK) {
                    mEditText.setText(data.getStringExtra(ApplicationListActivity.PACKAGE_NAME));
                }
                break;
        }
    }
}
