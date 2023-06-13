package mchorse.bbs.ui.game.scripts.highlighting;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class SyntaxStyle implements IMapSerializable
{
    public String title;
    public boolean shadow;
    public int primary;
    public int secondary;
    public int identifier;
    public int special;
    public int strings;
    public int comments;
    public int numbers;
    public int other;
    public int lineNumbers;
    public int background;

    public SyntaxStyle()
    {
        this.title = "Monokai";
        this.shadow = true;
        this.primary = 0xf92472;
        this.secondary = 0x67d8ef;
        this.identifier = 0xa6e22b;
        this.special = 0xfd9622;
        this.strings = 0xe7db74;
        this.comments = 0x74705d;
        this.numbers = 0xac80ff;
        this.other = 0xffffff;
        this.lineNumbers = 0x90918b;
        this.background = 0x282923;
    }

    public SyntaxStyle(MapType data)
    {
        this.fromData(data);
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("title", this.title);
        data.putBool("shadow", this.shadow);
        data.putInt("primary", this.primary);
        data.putInt("secondary", this.secondary);
        data.putInt("identifier", this.identifier);
        data.putInt("special", this.special);
        data.putInt("strings", this.strings);
        data.putInt("comments", this.comments);
        data.putInt("numbers", this.numbers);
        data.putInt("other", this.other);
        data.putInt("lineNumbers", this.lineNumbers);
        data.putInt("background", this.background);
    }

    @Override
    public void fromData(MapType data)
    {
        this.title = data.getString("title");
        this.shadow = data.getBool("shadow");
        this.primary = data.getInt("primary");
        this.secondary = data.getInt("secondary");
        this.identifier = data.getInt("identifier");
        this.special = data.getInt("special");
        this.strings = data.getInt("strings");
        this.comments = data.getInt("comments");
        this.numbers = data.getInt("numbers");
        this.other = data.getInt("other");
        this.lineNumbers = data.getInt("lineNumbers");
        this.background = data.getInt("background");
    }
}