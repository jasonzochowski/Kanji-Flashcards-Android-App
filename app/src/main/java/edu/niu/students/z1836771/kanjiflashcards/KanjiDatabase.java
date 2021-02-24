package edu.niu.students.z1836771.kanjiflashcards;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class KanjiDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "KanjiDatabase";
    private static final String TABLE_NAME = "Kanji";

    public KanjiDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);

        //check if table is empty
        String sqlQuery = "select * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() == 0)
            insertAll();
    }

    //onCreate, create table
    public void onCreate(SQLiteDatabase db)
    {
        //create table
        String createTable = "create table " + TABLE_NAME + "( id integer primary key autoincrement, ";
        createTable += "kanji text, chapter integer, favorite integer default 0)";
        db.execSQL(createTable);
    }

    //onUpgrade, drop table if exists
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //drop old table if exists
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    //delete table
    public void deleteAll()
    {
        String sqlQuery = "delete from " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    //insert item into table
    public void insertItem(int id, String kanji, int chapter)
    {
        //sql statement
        String sqlQuery = "insert into " + TABLE_NAME + " values(" + id + ", '" + kanji + "', " + chapter + ", 0)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    //retrieve all kanji in the table and return an ArrayList
    public ArrayList<String> retrieveKanji()
    {
        //sql query
        String sqlQuery = "select kanji from " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arraylist with kanji from table
        ArrayList<String> arrayList = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            arrayList.add(cursor.getString(cursor.getColumnIndex("kanji")));
        }
        return arrayList;
    }

    //retreve all distinct chapters
    public ArrayList<String> retrieveChapters()
    {
        //sql query
        String sqlQuery = "select distinct chapter from " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arraylist with kanji from table
        ArrayList<String> arrayList = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            arrayList.add("Lesson: " + cursor.getString(cursor.getColumnIndex("chapter")));
        }
        return arrayList;
    }

    //retrieve all chapters
    public ArrayList<String> retrieveAllChapters()
    {
        //sql query
        String sqlQuery = "select chapter from " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arraylist with kanji from table
        ArrayList<String> arrayList = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            arrayList.add("Lesson: " + cursor.getString(cursor.getColumnIndex("chapter")));
        }
        return arrayList;
    }

    //retrieve favorite chapters
    public ArrayList<String> retrieveFavoriteChapters()
    {
        //sql query
        String sqlQuery = "select chapter from " + TABLE_NAME + " where favorite = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arraylist with kanji from table
        ArrayList<String> arrayList = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            arrayList.add("Lesson: " + cursor.getString(cursor.getColumnIndex("chapter")));
        }
        return arrayList;
    }

    //retrieve kanji by chapter
    public ArrayList<Integer> retrieveChapter(int chapter) {
        //sql query
        String sqlQuery = "select id from " + TABLE_NAME + " where chapter = " + chapter;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arrayList with items from table
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        while (cursor.moveToNext()) {
            arrayList.add(cursor.getInt(cursor.getColumnIndex("id")));
        }
        return arrayList;
    }

    //retrieve kanji by favorites
    public ArrayList<String> retrieveFavorites() {
        //sql query
        String sqlQuery = "select kanji from " + TABLE_NAME + " where favorite = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        //create and build arrayList with items from table
        ArrayList<String> arrayList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex("kanji")));
        }
        return arrayList;
    }

    //retrieve id by kanji
    public int retrieveId(String kanjiName) {
        //sql query
        String sqlQuery = "select id from " + TABLE_NAME + " where kanji = '" + kanjiName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        return id;
    }

    //update favorites
    public void updateFavorites(String kanjiName, int newFavorite)
    {
        //sql query
        String sqlQuery = "update " + TABLE_NAME + " set favorite = " + newFavorite + " where kanji = '" + kanjiName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    //update favorites by chapter
    public void updateFavoritesLesson(int chapter, int newFavorite)
    {
        //sql query
        String sqlQuery = "update " + TABLE_NAME + " set favorite = " + newFavorite + " where chapter = " + chapter;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    //remove all favorites
    public void removeAllFavorites()
    {
        //sql query
        String sqlQuery = "update " + TABLE_NAME + " set favorite = 0";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    //return true if selected kanji is favorited
    public boolean isFavorite(String kanjiName)
    {
        //sql query
        String sqlQuery = "select kanji from " + TABLE_NAME + " where kanji = '" + kanjiName + "' and favorite = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() != 0)
            return true;
        else
            return false;
    }

    //return true if one of the kanji in a chapter is favorited
    public boolean isFavoriteChapter(int chapter)
    {
        //sql query
        String sqlQuery = "select kanji from " + TABLE_NAME + " where chapter = " + chapter + " and favorite = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.getCount() != 0)
            return true;
        else
            return false;
    }

    //insert all data into database if table is empty
    public void insertAll()
    {
        //Lesson 3 Kanji
        insertItem(0, "一", 3);
        insertItem(1, "二", 3);
        insertItem(2, "三", 3);
        insertItem(3, "四", 3);
        insertItem(4, "五", 3);
        insertItem(5, "六", 3);
        insertItem(6, "七", 3);
        insertItem(7, "八", 3);
        insertItem(8, "九", 3);
        insertItem(9, "十", 3);
        insertItem(10, "百", 3);
        insertItem(11, "千", 3);
        insertItem(12, "万", 3);
        insertItem(13, "円", 3);
        insertItem(14, "時", 3);
        //Lesson 4 Kanji
        insertItem(15, "日", 4);
        insertItem(16, "本", 4);
        insertItem(17, "人", 4);
        insertItem(18, "月", 4);
        insertItem(19, "火", 4);
        insertItem(20, "水", 4);
        insertItem(21, "木", 4);
        insertItem(22, "金", 4);
        insertItem(23, "土", 4);
        insertItem(24, "曜", 4);
        insertItem(25, "上", 4);
        insertItem(26, "下", 4);
        insertItem(27, "中", 4);
        insertItem(28, "半", 4);
        //Lesson 5 Kanji
        insertItem(29, "山", 5);
        insertItem(30, "川", 5);
        insertItem(31, "元", 5);
        insertItem(32, "気", 5);
        insertItem(33, "天", 5);
        insertItem(34, "私", 5);
        insertItem(35, "今", 5);
        insertItem(36, "田", 5);
        insertItem(37, "女", 5);
        insertItem(38, "男", 5);
        insertItem(39, "見", 5);
        insertItem(40, "行", 5);
        insertItem(41, "食", 5);
        insertItem(42, "飲", 5);
        //Lesson 6 Kanji
        insertItem(43, "東", 6);
        insertItem(44, "西", 6);
        insertItem(45, "南", 6);
        insertItem(46, "北", 6);
        insertItem(47, "口", 6);
        insertItem(48, "出", 6);
        insertItem(49, "右", 6);
        insertItem(50, "左", 6);
        insertItem(51, "分", 6);
        insertItem(52, "先", 6);
        insertItem(53, "生", 6);
        insertItem(54, "大", 6);
        insertItem(55, "学", 6);
        insertItem(56, "外", 6);
        insertItem(57, "国", 6);
        //Lesson 7 Kanji
        insertItem(58, "京", 7);
        insertItem(59, "子", 7);
        insertItem(60, "小", 7);
        insertItem(61, "会", 7);
        insertItem(62, "社", 7);
        insertItem(63, "父", 7);
        insertItem(64, "母", 7);
        insertItem(65, "高", 7);
        insertItem(66, "校", 7);
        insertItem(67, "毎", 7);
        insertItem(68, "語", 7);
        insertItem(69, "文", 7);
        insertItem(70, "帰", 7);
        insertItem(71, "入", 7);
        //Lesson 8 Kanji
        insertItem(72, "員", 8);
        insertItem(73, "新", 8);
        insertItem(74, "聞", 8);
        insertItem(75, "作", 8);
        insertItem(76, "仕", 8);
        insertItem(77, "事", 8);
        insertItem(78, "電", 8);
        insertItem(79, "車", 8);
        insertItem(80, "休", 8);
        insertItem(81, "言", 8);
        insertItem(82, "読", 8);
        insertItem(83, "思", 8);
        insertItem(84, "次", 8);
        insertItem(85, "何", 8);
        //Lesson 9 Kanji
        insertItem(86, "午", 9);
        insertItem(87, "後", 9);
        insertItem(88, "前", 9);
        insertItem(89, "名", 9);
        insertItem(90, "白", 9);
        insertItem(91, "雨", 9);
        insertItem(92, "書", 9);
        insertItem(93, "友", 9);
        insertItem(94, "間", 9);
        insertItem(95, "家", 9);
        insertItem(96, "話", 9);
        insertItem(97, "少", 9);
        insertItem(98, "古", 9);
        insertItem(99, "知", 9);
        insertItem(100, "来", 9);
        //Lesson 10 Kanji
        insertItem(101, "住", 10);
        insertItem(102, "正", 10);
        insertItem(103, "年", 10);
        insertItem(104, "売", 10);
        insertItem(105, "買", 10);
        insertItem(106, "町", 10);
        insertItem(107, "長", 10);
        insertItem(108, "道", 10);
        insertItem(109, "雪", 10);
        insertItem(110, "立", 10);
        insertItem(111, "自", 10);
        insertItem(112, "夜", 10);
        insertItem(113, "朝", 10);
        insertItem(114, "持", 10);
        //Lesson 11 Kanji
        insertItem(115, "手", 11);
        insertItem(116, "紙", 11);
        insertItem(117, "好", 11);
        insertItem(118, "近", 11);
        insertItem(119, "明", 11);
        insertItem(120, "病", 11);
        insertItem(121, "院", 11);
        insertItem(122, "映", 11);
        insertItem(123, "画", 11);
        insertItem(124, "歌", 11);
        insertItem(125, "市", 11);
        insertItem(126, "所", 11);
        insertItem(127, "勉", 11);
        insertItem(128, "強", 11);
        insertItem(129, "有", 11);
        insertItem(130, "旅", 11);
        //Lesson 12 Kanji
        insertItem(131, "昔", 12);
        insertItem(132, "々", 12);
        insertItem(133, "神", 12);
        insertItem(134, "早", 12);
        insertItem(135, "起", 12);
        insertItem(136, "牛", 12);
        insertItem(137, "使", 12);
        insertItem(138, "働", 12);
        insertItem(139, "連", 12);
        insertItem(140, "別", 12);
        insertItem(141, "度", 12);
        insertItem(142, "赤", 12);
        insertItem(143, "青", 12);
        insertItem(144, "色", 12);
        //Lesson 13 Kanji
        insertItem(145, "物", 13);
        insertItem(146, "鳥", 13);
        insertItem(147, "料", 13);
        insertItem(148, "理", 13);
        insertItem(149, "特", 13);
        insertItem(150, "安", 13);
        insertItem(151, "飯", 13);
        insertItem(152, "肉", 13);
        insertItem(153, "悪", 13);
        insertItem(154, "体", 13);
        insertItem(155, "空", 13);
        insertItem(156, "港", 13);
        insertItem(157, "着", 13);
        insertItem(158, "同", 13);
        insertItem(159, "海", 13);
        insertItem(160, "昼", 13);
        //Lesson 14 Kanji
        insertItem(161, "彼", 14);
        insertItem(162, "代", 14);
        insertItem(163, "留", 14);
        insertItem(164, "族", 14);
        insertItem(165, "親", 14);
        insertItem(166, "切", 14);
        insertItem(167, "英", 14);
        insertItem(168, "店", 14);
        insertItem(169, "去", 14);
        insertItem(170, "急", 14);
        insertItem(171, "乗", 14);
        insertItem(172, "当", 14);
        insertItem(173, "音", 14);
        insertItem(174, "楽", 14);
        insertItem(175, "医", 14);
        insertItem(176, "者", 14);
        //Lesson 15 Kanji
        insertItem(177, "死", 15);
        insertItem(178, "意", 15);
        insertItem(179, "味", 15);
        insertItem(180, "注", 15);
        insertItem(181, "夏", 15);
        insertItem(182, "魚", 15);
        insertItem(183, "寺", 15);
        insertItem(184, "広", 15);
        insertItem(185, "転", 15);
        insertItem(186, "借", 15);
        insertItem(187, "走", 15);
        insertItem(188, "建", 15);
        insertItem(189, "地", 15);
        insertItem(190, "場", 15);
        insertItem(191, "足", 15);
        insertItem(192, "通", 15);
        //Lesson 16 Kanji
        insertItem(193, "供", 16);
        insertItem(194, "世", 16);
        insertItem(195, "界", 16);
        insertItem(196, "全", 16);
        insertItem(197, "部", 16);
        insertItem(198, "始", 16);
        insertItem(199, "週", 16);
        insertItem(200, "以", 16);
        insertItem(201, "考", 16);
        insertItem(202, "開", 16);
        insertItem(203, "屋", 16);
        insertItem(204, "方", 16);
        insertItem(205, "運", 16);
        insertItem(206, "動", 16);
        insertItem(207, "教", 16);
        insertItem(208, "室", 16);
        //Lesson 17 Kanji
        insertItem(209, "歳", 17);
        insertItem(210, "習", 17);
        insertItem(211, "主", 17);
        insertItem(212, "結", 17);
        insertItem(213, "婚", 17);
        insertItem(214, "集", 17);
        insertItem(215, "発", 17);
        insertItem(216, "表", 17);
        insertItem(217, "品", 17);
        insertItem(218, "字", 17);
        insertItem(219, "活", 17);
        insertItem(220, "写", 17);
        insertItem(221, "真", 17);
        insertItem(222, "歩", 17);
        insertItem(223, "野", 17);
        //Lesson 18 Kanji
        insertItem(224, "目", 18);
        insertItem(225, "的", 18);
        insertItem(226, "力", 18);
        insertItem(227, "洋", 18);
        insertItem(228, "服", 18);
        insertItem(229, "堂", 18);
        insertItem(230, "授", 18);
        insertItem(231, "業", 18);
        insertItem(232, "試", 18);
        insertItem(233, "験", 18);
        insertItem(234, "貸", 18);
        insertItem(235, "図", 18);
        insertItem(236, "館", 18);
        insertItem(237, "終", 18);
        insertItem(238, "宿", 18);
        insertItem(239, "題", 18);
        //Lesson 19 Kanji
        insertItem(240, "春", 19);
        insertItem(241, "秋", 19);
        insertItem(242, "冬", 19);
        insertItem(243, "花", 19);
        insertItem(244, "様", 19);
        insertItem(245, "不", 19);
        insertItem(246, "姉", 19);
        insertItem(247, "兄", 19);
        insertItem(248, "漢", 19);
        insertItem(249, "卒", 19);
        insertItem(250, "工", 19);
        insertItem(251, "研", 19);
        insertItem(252, "究", 19);
        insertItem(253, "質", 19);
        insertItem(254, "問", 19);
        insertItem(255, "多", 19);
        //Lesson 20 Kanji
        insertItem(256, "皿", 20);
        insertItem(257, "声", 20);
        insertItem(258, "茶", 20);
        insertItem(259, "止", 20);
        insertItem(260, "枚", 20);
        insertItem(261, "両", 20);
        insertItem(262, "無", 20);
        insertItem(263, "払", 20);
        insertItem(264, "心", 20);
        insertItem(265, "笑", 20);
        insertItem(266, "絶", 20);
        insertItem(267, "対", 20);
        insertItem(268, "痛", 20);
        insertItem(269, "最", 20);
        insertItem(270, "続", 20);
        //Lesson 21 Kanji
        insertItem(271, "信", 21);
        insertItem(272, "経", 21);
        insertItem(273, "台", 21);
        insertItem(274, "風", 21);
        insertItem(275, "犬", 21);
        insertItem(276, "重", 21);
        insertItem(277, "初", 21);
        insertItem(278, "若", 21);
        insertItem(279, "送", 21);
        insertItem(280, "幸", 21);
        insertItem(281, "計", 21);
        insertItem(282, "遅", 21);
        insertItem(283, "配", 21);
        insertItem(284, "弟", 21);
        insertItem(285, "妹", 21);
        //Lesson 22 Kanji
        insertItem(286, "記", 22);
        insertItem(287, "銀", 22);
        insertItem(288, "回", 22);
        insertItem(289, "夕", 22);
        insertItem(290, "黒", 22);
        insertItem(291, "用", 22);
        insertItem(292, "守", 22);
        insertItem(293, "末", 22);
        insertItem(294, "待", 22);
        insertItem(295, "残", 22);
        insertItem(296, "番", 22);
        insertItem(297, "駅", 22);
        insertItem(298, "説", 22);
        insertItem(299, "案", 22);
        insertItem(300, "内", 22);
        insertItem(301, "忘", 22);
        //Lesson 23 Kanji
        insertItem(302, "顔", 23);
        insertItem(303, "情", 23);
        insertItem(304, "怒", 23);
        insertItem(305, "変", 23);
        insertItem(306, "相", 23);
        insertItem(307, "横", 23);
        insertItem(308, "比", 23);
        insertItem(309, "化", 23);
        insertItem(310, "違", 23);
        insertItem(311, "悲", 23);
        insertItem(312, "調", 23);
        insertItem(313, "査", 23);
        insertItem(314, "果", 23);
        insertItem(315, "感", 23);
        insertItem(316, "答", 23);
    }
}
