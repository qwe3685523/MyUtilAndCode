package zy.my.com.myutilandcode.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by eryan on 2016/11/16.
 *
 *----文件工具----
 *
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static int FILE_SIZE = 4 * 1024;

    /**
     * 获取缓存file
     * @param imageUri
     * @return
     */
    public static File getCacheFile(String imageUri) {
        File cacheFile = null;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                String fileName = getFileName(imageUri);
                File dir = new File(sdCardDir.getCanonicalPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                cacheFile = new File(dir, fileName);
                Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getCacheFileError:" + e.getMessage());
        }

        return cacheFile;
    }


    /**
     * 判断sd卡是否存在
     *
     * @return
     */
    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    /**
     * 创建文件路径
     *
     * @param path
     * @return
     */
    public static boolean createPath(String path) {
        File f = new File(path);
        if (!f.exists()) {
            Boolean o = f.mkdirs();
            Log.i(TAG, "create dir:" + path + ":" + o.toString());
            return o;
        }
        return true;
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean createfile(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            Boolean o = f.createNewFile();
            Log.i(TAG, "create dir:" + path + ":" + o.toString());
            return o;
        }
        return true;
    }

    /**
     * 判断文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean exists(String file) {
        return new File(file).exists();
    }

    /***
     * 通过io流保存文件
     *
     * @param file
     * @param inputStream
     * @return
     */
    public static File saveFile(String file, InputStream inputStream) {
        File f = null;
        OutputStream outSm = null;

        try {
            f = new File(file);
            String path = f.getParent();
            if (!createPath(path)) {
                Log.e(TAG, "can't create dir:" + path);
                return null;
            }

            if (!f.exists()) {
                f.createNewFile();
            }

            outSm = new FileOutputStream(f);
            byte[] buffer = new byte[FILE_SIZE];
            while ((inputStream.read(buffer)) != -1) {
                outSm.write(buffer);
            }
            outSm.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;

        } finally {
            try {
                if (outSm != null) outSm.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Log.v(TAG, "[FileUtil]save file:" + file + ":" + Boolean.toString(f.exists()));

        return f;
    }

    /**
     * 获取图片
     * @param file
     * @return
     */
    public static Drawable getImageDrawable(String file) {
        if (!exists(file)) return null;
        try {
            InputStream inp = new FileInputStream(new File(file));
            return BitmapDrawable.createFromStream(inp, "img");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    /**
     * Bitmap转Drawable
     */
    public static Drawable convertBitmap2(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        return bd;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/formats/";

    public static void saveBitmap(Bitmap bm, String picName) {
        Log.e("", "保存图片");
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("saveBitmap", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    /**
     * 是否存在
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    /**
     * 删除文件
     * @param fileName
     */
    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    /**
     * 删除文件夹
     */
    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    /*******************************
     * 内部存储文件的读写操作
     ****************************************/

    // 读文件在./data/data/com.tt/files/下面
    public static String readFileData(String fileName, Context context) {
        FileInputStream fin = null;
        StringBuffer res = new StringBuffer("");
        try {
            fin = context.openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res.setLength(0);
            res.append(new String(buffer, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("读取文件异常", "读取文件异常");
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res.toString();
    }

    // 写文件在./data/data/com.tt/files/下面
    public static void writeFileData(String fileName, String message, Context context) {
        FileOutputStream fout = null;
        try {
            fout = context.openFileOutput(fileName, context.MODE_PRIVATE);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("writeFileDataError", e.toString());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void write(String fileName, Context context, String json) {
        FileOutputStream fout = null;
        try {
            delFile(fileName);
            fout = context.openFileOutput(fileName, context.MODE_PRIVATE);
            byte[] bytes = json.getBytes();
            fout.write(bytes);
            fout.close();
            Log.e("写入完成", "---------------------");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("写入文件异常", e.toString());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 读取
     * @param fileName
     * @param context
     * @return
     */
    public static String read(String fileName, Context context) {

        FileInputStream fin = null;

        StringBuffer res = new StringBuffer("");
        try {
            fin = context.openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res.setLength(0);
            res.append(new String(buffer, "UTF-8"));
            Log.e("读取完成", "---------------------");
            return res.toString();
        } catch (Exception e) {
            Log.e("读取文件异常", e.toString());
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "fail";
    }
}
