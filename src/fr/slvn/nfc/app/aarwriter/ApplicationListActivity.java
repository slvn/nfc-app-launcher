
package fr.slvn.nfc.app.aarwriter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationListActivity extends ListActivity {

    public static final String PACKAGE_NAME = "package_name";

    private static final String APP_ICON = "app_icon";
    private static final String APP_NAME = "app_name";
    private static final String APP_PACKAGE_NAME = "app_package_name";

    private List<Map<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init parameters
        mData = new ArrayList<Map<String, Object>>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent, 0);

        SimpleAdapter adapter = new SimpleAdapter(ApplicationListActivity.this, getData(infos),
                R.layout.list_row, new String[] {
                        APP_ICON, APP_NAME, APP_PACKAGE_NAME
                },
                new int[] {
                        R.id.app_icon, R.id.app_name, R.id.app_package_name
                });
        adapter.setViewBinder(new ViewBinder() {

            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView && data instanceof Drawable) {
                    ((ImageView) view).setImageDrawable((Drawable) data);
                }
                return false;
            }
        });

        setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent data = new Intent();
        data.putExtra(PACKAGE_NAME, (String) mData.get(position).get(APP_PACKAGE_NAME));
        setResult(RESULT_OK, data);
        finish();
    }

    protected List<Map<String, Object>> getData(List<ResolveInfo> list) {

        PackageManager pm = getPackageManager();

        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);

            CharSequence temp = info.loadLabel(pm);

            addItem(info.loadIcon(pm),
                    temp != null ? temp.toString() : info.activityInfo.packageName,
                    info.activityInfo.applicationInfo.packageName);
        }

        Collections.sort(mData, sDisplayNameComparator);

        return mData;
    }

    private final static Comparator<Map<String, Object>> sDisplayNameComparator =
            new Comparator<Map<String, Object>>() {
                private final Collator collator = Collator.getInstance();

                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                    return collator.compare(map1.get(APP_NAME), map2.get(APP_NAME));
                }
            };

    protected void addItem(Drawable appIcon, String appName, String appPackageName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(APP_ICON, appIcon);
        map.put(APP_NAME, appName);
        map.put(APP_PACKAGE_NAME, appPackageName);
        mData.add(map);
    }
}
