package mchorse.bbs.l10n.keys;

public class FormatKey implements IKey
{
    public IKey lang;
    public Object[] args;

    public FormatKey(IKey lang, Object... args)
    {
        this.lang = lang;
        this.args = args;
    }

    @Override
    public String get()
    {
        String format = this.lang.get();

        try
        {
            return this.args.length == 0 ? format : String.format(format, this.args);
        }
        catch (Exception e)
        {
            String key = this.lang instanceof LangKey ? ((LangKey) this.lang).key : this.lang.get();
            System.out.println("Failed to format string: " + key);
            e.printStackTrace();

            return key;
        }
    }
}