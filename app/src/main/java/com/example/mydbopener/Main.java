package com.example.mydbopener;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * PackageName of com.example.mydbopener
 * Author by Zero.
 * Date on 2019/5/22.
 */
public class Main implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam){
        try {
            ClassLoader classLoader = lpparam.classLoader;
            Class PackageManagerService = classLoader.loadClass("com.android.server.pm.PackageManagerService");
            //Class PackageManagerService = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", lpparam.classLoader);
            XposedBridge.hookAllMethods(PackageManagerService, "getPackageInfo", new XC_MethodHook() {
                /* Access modifiers changed, original: protected */
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //super.afterHookedMethod(param);
                    PackageInfo packageInfo = (PackageInfo) param.getResult();
                    if (packageInfo != null) {
                        ApplicationInfo appinfo = packageInfo.applicationInfo;
                        int flags = appinfo.flags;
                        Log.i("BDOpener", "Load App : " + appinfo.packageName);
                        Log.i("BDOpener", "==== After Hook ====");
                        if ((flags & 32768) == 0) {
                            flags |= 32768;
                        }
                        if ((flags & 2) == 0) {
                            flags |= 2;
                        }
                        appinfo.flags = flags;
                        param.setResult(packageInfo);
                        Log.i("BDOpener", "flags = " + flags);
                        Main.isDebugable(appinfo);
                        Main.isBackup(appinfo);
                    }
                }
            });
        }catch (Exception e){

        }
    }

    public static boolean isDebugable(ApplicationInfo info) {
        try {
            if ((info.flags & 2) != 0) {
                Log.i("BDOpener", "Open Debugable");
                return true;
            }
        } catch (Exception e) {
        }
        Log.i("BDOpener", "Close Debugable");
        return false;
    }

    public static boolean isBackup(ApplicationInfo info) {
        try {
            if ((info.flags & 32768) != 0) {
                Log.i("BDOpener", "Open Backup");
                return true;
            }
        } catch (Exception e) {
        }
        Log.i("BDOpener", "Close Backup");
        return false;
    }
}