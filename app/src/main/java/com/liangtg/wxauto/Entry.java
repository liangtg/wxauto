package com.liangtg.wxauto;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.util.Iterator;

/**
 * @ProjectName: wxauto
 * @ClassName: Entry
 * @Description: java类作用描述
 * @Author: liangtg
 * @CreateDate: 19-7-22 下午1:46
 * @UpdateUser: 更新者
 * @UpdateDate: 19-7-22 下午1:46
 * @UpdateRemark: 更新说明
 */
public class Entry implements IXposedHookLoadPackage {
    private static final String tag = "wxx";

    private static void d(Object object) {
        Log.d(tag, "" + object);
    }

    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam)
        throws Throwable {
        //com.tencent.mm.plugin.voip.ui.VideoActivity
        if (!lpparam.packageName.equals("com.tencent.mm")) return;
        if (!lpparam.isFirstApplication) return;
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Application app = (Application) param.thisObject;
                app.registerActivityLifecycleCallbacks(
                    new Application.ActivityLifecycleCallbacks() {
                        @Override public void onActivityCreated(Activity activity,
                            Bundle savedInstanceState) {
                        }

                        @Override public void onActivityStarted(Activity activity) {
                        }

                        @Override public void onActivityResumed(Activity activity) {
                            d("resumed: " + activity);
                            dumpActivity(activity);
                        }

                        @Override public void onActivityPaused(Activity activity) {
                        }

                        @Override public void onActivityStopped(Activity activity) {
                        }

                        @Override public void onActivitySaveInstanceState(Activity activity,
                            Bundle outState) {
                        }

                        @Override public void onActivityDestroyed(Activity activity) {
                        }
                    });
            }
        });
    }

    private void dumpActivity(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Intent intent = activity.getIntent();
        d(intent);
        Bundle extra = intent.getExtras();
        if (null != extra) {
            Iterator<String> it = extra.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                d(key + " : " + extra.get(key));
            }
        }
        if (null != view) {
            dumpGroup("", (ViewGroup) view);
        }
    }

    private void dumpGroup(String pre, ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            int id = child.getId();
            String name = " ";
            try {
                name = child.getContext().getResources().getResourceName(id);
            } catch (Exception e) {
            }
            d(pre + String.format("%s: %s(%d)", child.getClass().getName(), name, id));
            if (child instanceof TextView) {
                d(pre + ((TextView) child).getText().toString());
            }
            if (child instanceof ViewGroup) {
                dumpGroup(pre + "->", (ViewGroup) child);
            }
        }
    }
}
