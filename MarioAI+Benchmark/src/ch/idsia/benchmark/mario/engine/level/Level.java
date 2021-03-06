package ch.idsia.benchmark.mario.engine.level;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Level implements Serializable, Cloneable
{
private static final long serialVersionUID = -2222762134065697580L;

static public class objCounters implements Serializable
{
    public int deadEndsCount = 0;
    public int cannonsCount = 0;
    public int hillStraightCount = 0;
    public int tubesCount = 0;
    public int blocksCount = 0;
    public int coinsCount = 0;
    public int gapsCount = 0;
    public int hiddenBlocks = 0;
    public int totalCannons;
    public int totalGaps;
    public int totalDeadEnds;
    public int totalBlocks;
    public int totalHiddenBlocks;
    public int totalCoins;
    public int totalHillStraight;
    public int totalTubes;
    // TODO : include in Evalutation info:
    public int totalPowerUps;

    public int mushrooms = 0;
    public int flowers = 0;
    public int creatures = 0;

    private static final long serialVersionUID = 4505050755444159808L;
}

public static final String[] BIT_DESCRIPTIONS = {//
        "BLOCK UPPER", //
        "BLOCK ALL", //
        "BLOCK LOWER", //
        "SPECIAL", //
        "BUMPABLE", //
        "BREAKABLE", //
        "PICKUPABLE", //
        "ANIMATED",//
};

public static byte[] TILE_BEHAVIORS = new byte[256];

public static final int BIT_BLOCK_UPPER = 1 << 0;
public static final int BIT_BLOCK_ALL = 1 << 1;
public static final int BIT_BLOCK_LOWER = 1 << 2;
public static final int BIT_SPECIAL = 1 << 3;
public static final int BIT_BUMPABLE = 1 << 4;
public static final int BIT_BREAKABLE = 1 << 5;
public static final int BIT_PICKUPABLE = 1 << 6;
public static final int BIT_ANIMATED = 1 << 7;

public static objCounters counters;

//private final int FILE_HEADER = 0x271c4178;
public int length;
public int height;
public int randomSeed;
public int type;
public int difficulty;

public byte[][] map;
public byte[][] data;
// Experimental feature: Mario TRACE
public int[][] marioTrace;

public SpriteTemplate[][] spriteTemplates;

public int xExit;
public int yExit;

@Override
public Object clone() throws CloneNotSupportedException
{
	//System.out.println("Cloning level, wh: "+width + " "+height);
	Level l = (Level) super.clone();
	return l;
}

public Level(int length, int height)
{
//        ints = new Vector();
//        booleans = new Vector();
    this.length = length;
    this.height = height;

    xExit = 50;
    yExit = 10;
//        System.out.println("Java: Level: lots of news here...");
//        System.out.println("length = " + length);
//        System.out.println("height = " + height);
    try
    {
        map = new byte[length][height];
//        System.out.println("map = " + map);
        data = new byte[length][height];
//        System.out.println("data = " + data);
        spriteTemplates = new SpriteTemplate[length][height];

        marioTrace = new int[length][height + 1];
    } catch (OutOfMemoryError e)
    {
        System.err.println("Java: MarioAI MEMORY EXCEPTION: OutOfMemory exception. Exiting...");
        e.printStackTrace();
        System.exit(-3);
    }
//        System.out.println("spriteTemplates = " + spriteTemplates);
//        observation = new byte[length][height];
//        System.out.println("observation = " + observation);
}

public static void loadBehaviors(DataInputStream dis) throws IOException
{
    dis.readFully(Level.TILE_BEHAVIORS);
}

public static void saveBehaviors(DataOutputStream dos) throws IOException
{
    dos.write(Level.TILE_BEHAVIORS);
}

public static Level load(ObjectInputStream ois) throws IOException, ClassNotFoundException
{
    Level level = (Level) ois.readObject();
    return level;
}

public static void save(Level lvl, ObjectOutputStream oos) throws IOException
{
    oos.writeObject(lvl);
}

/**
 * Animates the unbreakable brick when smashed from below by Mario
 */
public void tick()
{
    for (int x = 0; x < length; x++)
        for (int y = 0; y < height; y++)
            if (data[x][y] > 0) data[x][y]--;
}

public byte getBlockCapped(int x, int y)
{
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x >= length) x = length - 1;
    if (y >= height) y = height - 1;
    return map[x][y];
}

public byte getBlock(int x, int y)
{
    if (x < 0) x = 0;
    if (y < 0) return 0;
    if (x >= length) x = length - 1;
    if (y >= height) y = height - 1;
    return map[x][y];
}

public void setBlock(int x, int y, byte b)
{
    if (x < 0) return;
    if (y < 0) return;
    if (x >= length) return;
    if (y >= height) return;
    map[x][y] = b;
}

public void setBlockData(int x, int y, byte b)
{
    if (x < 0) return;
    if (y < 0) return;
    if (x >= length) return;
    if (y >= height) return;
    data[x][y] = b;
}

public boolean isBlocking(int x, int y, float xa, float ya)
{
    byte block = getBlock(x, y);
    boolean blocking = ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_ALL) > 0;
    blocking |= (ya > 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_UPPER) > 0;
    blocking |= (ya < 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_LOWER) > 0;

    return blocking;
}

public SpriteTemplate getSpriteTemplate(int x, int y)
{
    if (x < 0) return null;
    if (y < 0) return null;
    if (x >= length) return null;
    if (y >= height) return null;
    return spriteTemplates[x][y];
}

public boolean setSpriteTemplate(int x, int y, SpriteTemplate spriteTemplate)
{
    if (x < 0) return false;
    if (y < 0) return false;
    if (x >= length) return false;
    if (y >= height) return false;
    spriteTemplates[x][y] = spriteTemplate;
    return true;
}

private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
{
    aInputStream.defaultReadObject();
    counters = (Level.objCounters) aInputStream.readObject();
}

private void writeObject(ObjectOutputStream aOutputStream) throws IOException
{
    aOutputStream.defaultWriteObject();
    aOutputStream.writeObject(counters);
}
}


//    public void ASCIIToOutputStream(OutputStream os) throws IOException {
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//        bw.write("\nlength = " + length);
//        bw.write("\nheight = " + height);
//        bw.write("\nMap:\n");
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < length; x++)
//
//            {
//                bw.write(map[x][y] + "\t");
//            }
//            bw.newLine();
//        }
//        bw.write("\nData: \n");
//
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < length; x++)
//
//            {
//                bw.write(data[x][y] + "\t");
//            }
//            bw.newLine();
//        }
//
//        bw.write("\nspriteTemplates: \n");
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < length; x++)
//
//            {
//                if                  (spriteTemplates[x][y] != null)
//                    bw.write(spriteTemplates[x][y].getType() + "\t");
//                else
//                    bw.write("_\t");
//
//            }
//            bw.newLine();
//        }
//
//        bw.write("\n==================\nAll objects: (Map[x,y], Data[x,y], Sprite[x,y])\n");
//        for (int y = 0; y < height; y++)
//        {
//                for (int x = 0; x < length; x++)
//
//            {
//                bw.write("(" + map[x][y] + "," + data[x][y] + ", " + ((spriteTemplates[x][y] == null) ? "_" : spriteTemplates[x][y].getType()) + ")\t");
//            }
//            bw.newLine();
//        }
//
////        bw.close();
//    }
