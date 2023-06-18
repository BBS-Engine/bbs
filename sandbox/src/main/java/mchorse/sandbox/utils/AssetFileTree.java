package mchorse.sandbox.utils;

import mchorse.bbs.utils.files.FileTree;
import mchorse.bbs.utils.files.entries.FolderImageEntry;

import java.io.File;

public class AssetFileTree extends FileTree
{
    public AssetFileTree(File folder)
    {
        this.root = new FolderImageEntry("assets", folder, null);
    }
}