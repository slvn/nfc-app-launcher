
package fr.slvn.nfc.app.aarwriter.tools;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;

public class NfcUtils {

    public static NdefMessage getApplicationRecord(String packageName) {
        return new NdefMessage(new NdefRecord[] {
                NdefRecord.createApplicationRecord(packageName)
        });
    }

    public static void writeTag(NdefMessage message, Tag tag) throws Exception {
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            writeInNdef(message, ndef);
        } else {
            formatNdef(message, tag);
        }
    }

    private static void writeInNdef(NdefMessage message, Ndef ndef) throws Exception {
        ndef.connect();
        if (!ndef.isWritable()) {
            throw new IOException("Tag is read-only.");
        }
        if (ndef.getMaxSize() < message.toByteArray().length) {
            throw new IOException("Tag capacity is " + ndef.getMaxSize() + " bytes, message is "
                    + message.toByteArray().length + " bytes.");
        }
        ndef.writeNdefMessage(message);
    }

    private static void formatNdef(NdefMessage message, Tag tag) throws Exception {
        NdefFormatable format = NdefFormatable.get(tag);
        if (format != null) {
            format.connect();
            format.format(message);
        } else {
            throw new FormatException("Tag doesn't support NDEF.");
        }
    }

}
