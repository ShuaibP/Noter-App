package com.uct.noter.noter;

import android.os.Environment;
import android.os.StatFs;

/**
 * Created by Shuaib on 2016-09-21.
 */
public class StorageInformation {

    public StorageInformation() {
    }

    /**
     * Obtains the total memory in external storage
     * @return int total memory
     */
    public long totalMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   total  = (statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
        return total;
    }

    /**
     * Obtains the amount of free memory in exteranl storage
     * @return int free memory
     */
    public long freeMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   free   = (statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
        return free;
    }

    /**
     * Returns the amount of used memory.
     * @return in used memory
     */
    public long busyMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   total  = (statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
        long   free   = (statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
        long   busy   = total - free;
        return busy;
    }
}
