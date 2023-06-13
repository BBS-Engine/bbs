package mchorse.bbs.utils.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Global file tree 
 */
public class GlobalTree extends FileTree
{
    /**
     * You can register your own file trees
     */
    public static final GlobalTree TREE = new GlobalTree();

    /**
     * Registered trees
     */
    protected List<FileTree> trees = new ArrayList<FileTree>();

    public void register(FileTree tree)
    {
        this.trees.add(tree);
        this.root.getEntries().add(tree.root);

        tree.root.parent = this.root;
    }

    public List<FileTree> getTrees()
    {
        return Collections.unmodifiableList(this.trees);
    }
}