package chessboard.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.nd4j.shade.guava.io.Files;

import utils.BitTools;

public class MagicNumbers {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        loadTest();

        // بیت های 1 حرکات ممکن شامل خانه هایی است که
        // مهره می تواند به آنجا برود اگر صفحه شطرنج
        // کاملا خالی باشد یا به عبارت دیگر، بلوکه کننده
        // ای وجود نداشته باشد
        Long[] possiblelMoves = new Long[64];
        for (int i=0; i<64; i++) {
            possiblelMoves[i] = removeBorder(i, getBishopRawMoves(i, 0L));
        }

        // برای تست
        BitTools.print(possiblelMoves[0]);
        BitTools.print(possiblelMoves[28]);

        Random rand = new Random();
        createFolder("/home/khmort/Programming/JAVA projects/MChess/magic numbers/bishop");

        // هر خانه یک عدد جادویی دارد که وقتی در خانه های بلوکه کننده
        // ضرب و نتیجه هش شود، اندیس آرایه map را تولید که مقدار محتوای
        // موجود در آن اندیس، خانه های مجازی است که مهره می تواند برود
        for (int square = 0; square < 64; square++) {

            // چند بیت در حرکات ممکن روشن هستند
            int bitCount = BitTools.bitCount(possiblelMoves[square]);

            // آرایه bright شامل اندیس بیت های روشن است
            int[] brights = getBrightBits(possiblelMoves[square]);

            // تعداد حالات ممکن برای بلوکه کننده ها به اندازه bound است
            // حداکثر تعداد ممکن برای پاسخ بلوکه کننده ها برابر تعداد
            // خود بلوکه کننده هاست
            int bound = (int) Math.pow(2.0, brights.length);
            Long[] map = new Long[bound];

            Long magic = -1L;

            // تعداد پاسخ هایی که باید پیدا کند
            // منظور از پاسخ همان اعداد جادویی است
            for (int n=0; n<1; n++) {


                int i = 0;
                int max_i = -1;
                int hash;
                Long block;

                // تا زمانی که i به bound نرسیده یا به عبارتی دیگر
                // همه حالات بلوکه کننده ها تست نشده است، این حلقه
                // ادامه دارد
                while (i != bound) {

                    // محاسبه عدد جادویی به صورت تصادفی
                    magic = rand.nextLong() & rand.nextLong() & rand.nextLong();

                    // تا زمانی که i کوچکتر از bound است جستجو ادامه دارد
                    while (i < bound) {
                        block = getBlock(brights, i);
                        // وقتی بلوکه کننده بدست آمد آن را هش می کنیم
                        // مقدار هش کلیدی است که وقتی با آن map را باز
                        // کنیم به جواب این بلوکه کننده می رسیم
                        hash = (int) ((magic * block) >>> (64 - bitCount));
                        // بلوکه کننده در i امین حالت ممکن خودش محسابه می شود
                        // اگر خالی باشد این اندیس رزرو نشده است
                        if (map[hash] == null) {
                            map[hash] = getBishopRawMoves(square, block);
                        } else {
                            // اگر خالی نباشد مقدار رزرو باید با جوابی که برای این حالت بلوکه کننده
                            // داریم باید برابر باشد وگرنه مجیک نامبر درست کار نکرده است
                            if (map[hash] != getBishopRawMoves(square, block)) {
                                // مقادیر ریست می شوند
                                map = new Long[bound + 1];
                                i = 0;
                                // بیشترین اندیسی که توانسته است جلو برود برای
                                // نمایش محاسبه می شود
                                max_i = Math.max(max_i, i);
                                break;
                            }
                        }
                        i++;
                    }
                }
                saveMagic("/home/khmort/Programming/JAVA projects/MChess/magic numbers/bishop", square, magic);
                saveMap("/home/khmort/Programming/JAVA projects/MChess/magic numbers/bishop", square, map);
            }

        }
    }

    public static void loadTest() throws IOException, ClassNotFoundException {

        Long moves = removeBorder(5, getBishopRawMoves(5, 0L));
        int[] brights = getBrightBits(moves);
        Long block = 8657588224L;
        BitTools.print(block);

        // load
        String folder = "/home/khmort/Programming/JAVA projects/MChess/magic numbers/bishop";
        Long magic = Long.parseLong(Files.readLines(new File(folder + "/5.txt"), StandardCharsets.UTF_8).get(0));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(folder + "/5.map")));
        Long[] map = (Long[]) ois.readObject();
        ois.close();

        System.out.println("map[31] " + map[30]);
        BitTools.print(map[(int) ((magic * block) >>> (64 - brights.length))]);

        System.exit(0);
    }

    public static int getOccupiedSize(Object[] arr) {
        return -1;
    }

    public static Long getBlock(int[] brights, int ith) {
        Long res = 0L;
        for (int i=0; i<brights.length; i++) {
            if (BitTools.getBit(ith, i) == 1)
                res = BitTools.setBitOn(res, brights[i]);
        }
        return res;
    }

    public static int[] getBrightBits(Long bitboard) {
        int[] brights = new int[BitTools.bitCount(bitboard)];
        int count = 0;
        for (int i=0; i<64; i++) {
            if (BitTools.getBit(bitboard, i) == 1) {
                brights[count] = i;
                count++;
            }
        }
        return brights;
    }
    
    /**
     * خانه هایی که مهره با وجود بلوکه کننده ها می تواند بگذارد
     * خانه های حمله هم محاسبه می شوند
     * این تابع فقط برای رخ درست کار می کند!
     * 
     * @param square خانه ای که مهره در آن قرار دارد
     * @param blocks بلوکه کننده
     */
    public static Long removeBorder(int square, Long board) {

        // حذف حاشیه ها برای سرعت بیشتر محاسبه عدد جادویی
        if (square == 0) {
            board = ~right_down & board;
        }
        else if (square == 7) {
            board = ~down_left & board;
        }
        else if (square == 56) {
            board = ~top_right & board;
        }
        else if (square == 63) {
            board = ~top_left & board;
        }
        else if (between(square, 0, 7)) {
            board = ~(down | right | left) & board;
        } else if (between(square, 56, 63)) {
            board = ~(top | right | left) & board;
        } else if ((BitTools.createBoard(square) & left) != 0) {
            board = ~(top | down | right) & board;
        } else if ((BitTools.createBoard(square) & right) != 0) {
            board = ~(top | down | left) & board;
        } else {
            board = ~(top | right | down | left) & board;
        }

        return board;
    }

    public static Long getRawMoves(int square, Long blocks) {

        int row = square/8, col = square%8;
        Long board = 0L;

        int r=row, c=col;

        while (c > -1) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            c -= 1;
        }

        r=row; c=col;
        while (c < 8) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            c += 1;
        }

        r=row; c=col;
        while (r < 8) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            r += 1;
        }

        r=row; c=col;
        while (r > -1) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            r -= 1;
        }

        return BitTools.setBitOff(board, square);

    }

    public static Long getBishopRawMoves(int square, Long blocks) {

        int row = square/8, col = square%8;
        Long board = 0L;

        int r=row, c=col;
        while (r > -1 && c > -1) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            c--;
            r--;
        }

        r=row; c=col;
        while (r < 8 && c > -1) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            c--;
            r++;
        }

        r=row; c=col;
        while (r < 8 && c < 8) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            r++;
            c++;
        }

        r=row; c=col;
        while (r > -1 && c < 8) {
            if (BitTools.getBit(blocks, 8 * r + c) == 1) {
                board = BitTools.setBitOn(board, 8 * r + c);
                break;
            }
            board = BitTools.setBitOn(board, 8 * r + c);
            r--;
            c++;
        }

        return BitTools.setBitOff(board, square);

    }

    // =============================== //
    //         SAVING RESULTS          //
    // =============================== //

    public static void saveMap(String parentFolder, int square, Long[] map) throws IOException {
        File saveFile = new File(parentFolder + "/" + square + ".map");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
        oos.writeObject(map);
        oos.close();
    }

    public static void createFolder(String parentFolder) throws IOException {
        File parent = new File(parentFolder);
        boolean exists = true;
        if (!parent.exists()) {
            exists = parent.mkdirs();
        }
        if (!exists) {
            throw new IOException();
        }
    }

    public static void saveMagic(String parentFolder, int square, Long magic) throws IOException {
        FileWriter fw = new FileWriter(parentFolder + "/" + square + ".txt", false);
        fw.write(String.valueOf(magic));
        fw.close();
    }

    public static boolean between(int target, int org, int dest) {
        return target > org && target < dest;
    }

    public static Long border = BitTools.createBoard(0, 1, 2, 3, 4, 5, 6, 7, 63, 62, 61, 60, 59, 58, 57, 56, 8, 16, 24, 32, 40, 48, 15, 23, 31, 39, 47, 55);
    public static long top = 0x00000000000000ffL,
                       right = 0x8080808080808080L,
                       down = 0xff00000000000000L,
                       left = 0x0101010101010101L,
                       top_right = 0x80808080808080ffL,
                       top_left = 0x01010101010101ffL,
                       right_down = 0xff80808080808080L,
                       down_left = 0xff01010101010101L;

}
