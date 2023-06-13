package mchorse.tests;

import mchorse.bbs.data.DataParser;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.graphics.text.FontRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataParserTest
{
    @Test
    public void testBackslashParsing()
    {
        String string = "Test \\";
        String toParse = DataToString.escapeQuoted(string) + ", ABC";
        DataParser parser = new DataParser(toParse);

        String parsed = parser.parseUntil(',');

        parsed = DataToString.unescape(parsed.substring(1, parsed.lastIndexOf('"')));

        Assertions.assertEquals(string, parsed);
    }

    @Test
    public void testSimpleMap()
    {
        MapType map = new MapType();

        map.putString("a", "ABC");
        map.putString("b", "DEF");
        map.putByte("c", (byte) 10);
        map.putShort("d", (short) 20);
        map.putInt("e", 30);
        map.putFloat("f", 40F);
        map.putLong("g", 50L);
        map.putDouble("h", 60D);

        BaseType parsed = DataParser.parse("{a:\"ABC\",b:\"DEF\",c:10b,d:20s,e:30,f:40F,g:50L,h:60D}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testSimpleList()
    {
        ListType list = new ListType();

        list.addString("ABC");
        list.addString("DEF");
        list.addByte((byte) 10);
        list.addShort((short) 20);
        list.addInt(30);
        list.addFloat(40F);
        list.addLong(50L);
        list.addDouble(60D);

        BaseType parsed = DataParser.parse("[\"ABC\",\"DEF\",10b,20s,30,40F,50L,60D]");

        Assertions.assertEquals(list, parsed);
    }

    @Test
    public void testEscapedStrings()
    {
        StringType string = new StringType("\"ABC\"");

        BaseType parsed = DataParser.parse("\"\\\"ABC\\\"\"");

        Assertions.assertEquals(string, parsed);
    }

    @Test
    public void testEscapedStrings2()
    {
        MapType data = new MapType();

        data.putString("message", "Hello traveler ${state(\"name\")}!");
        data.putByte("test", (byte) 2);

        BaseType parsed = DataParser.parse("{message:\"Hello traveler ${state(\\\"name\\\")}!\",test:2b}");

        Assertions.assertEquals(data, parsed);
    }

    @Test
    public void testMixedData()
    {
        MapType map = new MapType();
        ListType a = new ListType();

        a.addInt(1);
        a.addInt(2);
        a.addInt(3);

        MapType b = new MapType();

        b.putDouble("x", 0D);
        b.putDouble("y", 0D);
        b.putDouble("z", 0D);

        map.put("a", a);
        map.put("b", b);
        map.putString("c", "Hi!");

        BaseType parsed = DataParser.parse("{a:[1, 2, 3],b:{x:0D,y:0D,z:0D},c:\"Hi!\"}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testNewlinesData()
    {
        MapType map = new MapType();

        map.putString("a", "Hi!\nHow are you?");
        map.putString("b", "I'm fine, thank you!");

        BaseType parsed = DataParser.parse("          {\n         a:\"Hi!\nHow are you?\",\n\n    b:\"I'm fine, thank you!\"\n\n}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testQuotedKeys()
    {
        MapType map = new MapType();

        map.putString("a", "a");
        map.putString("b", "b");

        BaseType parsed = DataParser.parse("{\"a\":\"a\",\"b\":\"b\"}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testExtraOptions()
    {
        MapType map = new MapType();

        map.putBool("a", false);
        map.putBool("b", true);
        map.putDouble("c", 42.42D);

        BaseType parsed = DataParser.parse("{\"a\":null,\"b\":true,\"c\":42.42}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testNestedQuotes()
    {
        MapType map = new MapType();

        map.putString("a", "{id:\"model\",model:\"normie\",name:\"\\\"boss\\\"\"}");

        BaseType parsed = DataParser.parse("{a:\"{id:\\\"model\\\",model:\\\"normie\\\",name:\\\"\\\\\"boss\\\\\"\\\"}\"}");

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testEscapeCharacters()
    {
        MapType map = new MapType();
        String json = "{a:\"\\n\\u00A7\\\\\"}";

        map.putString("a", "\n" + FontRenderer.FORMATTING_STRING + "\\");

        BaseType parsed = DataParser.parse(json);

        Assertions.assertEquals(map, parsed);
    }

    @Test
    public void testNewlines()
    {
        MapType map = new MapType();
        String value = "Hello,\nWorld!";

        map.putString("test", value);

        MapType parsed = (MapType) DataParser.parse("{test:\"Hello,\\nWorld!\"}");

        Assertions.assertEquals(map.toString(), parsed.toString());
        Assertions.assertEquals(value, parsed.getString("test"));
    }

    @Test
    public void testNewlineEscaping()
    {
        String toEscape = "Hello,\nWorld!";
        String expected = "Hello,\\nWorld!";

        Assertions.assertEquals(expected, DataToString.escape(toEscape));
    }
}