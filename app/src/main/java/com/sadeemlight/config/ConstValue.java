package com.sadeemlight.config;

/**
 * Created by Rajesh Dabhi on 9/8/2016.
 */
public class ConstValue {
    public static final String API_KEY = "AIzaSyB5KY1MmGiqKl82AvD_ySH46KpXPrQioL4";

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int DELETE = 3;

    public static final String PREFS_NAME = "PostyLoginPrefs";

    public static final String IS_LOGIN = "isLogin";

    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_LANG = "language";

    public static final String KEY_STUDENT_ID = "students_ID";

    public static final String KEY_BIRTHDATE = "students_birthDate";

    public static final String KEY_GENDER = "students_gender";

    public static final String KEY_IMAGELINK = "students_imagelink";

    public static final String KEY_CLASSID = "j_class_ID";

    public static final String KEY_LOGINAS = "login_as";

    public static final String KEY_CLASS_NAME = "classname";

    public static final String KEY_SCHOOL_ID = "school_id";

    public static final String KEY_SCHOOL_NAME = "school_name";

    public static final String KEY_DIVISION_NAME = "division_name";

    public static final String KEY_DIVISION_ID = "division_id";

    public static final String KEY_CITY_ID = "city_id";

    public static final String KEY_CITY_NAME = "city_name";

    /*public static final String KEY_ATTEND_POINT = "attend_point";

    public static final String KEY_HOMEWORK_POINT = "homework_point";

    public static final String KEY_EXAM_POINT = "exam_point";

    public static final String KEY_SCHOOL_POINT = "school_point";*/

    public static final String KEY_POINTS = "points";

    public static final String KEY_TOKEN = "gcm_token";
    public static final String KEY_ACCESSTOKEN = "access_token";

    public static final String KEY_SCHOOL_IMAGE = "school_image";

    public static final String KEY_PARENT_ID = "parent_id";


    public  static String BASE_URL_NEW = "https://api.sadeemlight.com/api/v2/";

    //public  static String BASE_URL = "http://2test2.alsadeem-systems.com/api/";
    //public  static String BASE_URL = "http://alsadeem-systems.com/app/";
    //public  static String BASE_URL = "http://api.sadeemlight.com/api/v2/";

    public  static String LOGIN_URL = BASE_URL_NEW+"login";

    //public  static String ATTEND_LIST_URL = BASE_URL+"student/attendance?student_id=";
    public  static String ATTEND_LIST_URL = BASE_URL_NEW+"student/attendance";

    public  static String ATTEND_TODAY_URL = BASE_URL_NEW+"student/attendance?attendance_type=Today";

    public  static String TOTAL_ATTEND_URL = BASE_URL_NEW+"student/attendance?attendance_type=Attend";

    public  static String TOTAL_ABSENT_URL = BASE_URL_NEW+"student/attendance?attendance_type=Absent";

    public  static String TOTAL_OFF_URL = BASE_URL_NEW+"student/attendance?attendance_type=Legally";

    //public  static String NEWS_URL = BASE_URL+"school/news?school_id=";
    public  static String NEWS_URL = BASE_URL_NEW+"feed";


    public  static String GET_MESSAGE_URL = BASE_URL_NEW+"messages";

    public  static String SEND_MESSAGE_URL = BASE_URL_NEW + "messages/send";

    public  static String HOMEWORK_URL = BASE_URL_NEW+"homework";

    //public  static String LIBRARY_URL = BASE_URL+"school/book?class_id=";

    public  static String LIBRARY_SUBJECT_URL = BASE_URL_NEW + "book/subjects";
    public  static String LIBRARY_PUBLIC_URL = BASE_URL_NEW + "public/books";
    public  static String LIBRARY_PRIVATE_URL = BASE_URL_NEW + "books/";
    public  static String LIBRARY_BOOK_PUBLIC_URL = BASE_URL_NEW + "public/book/";
    public  static String LIBRARY_BOOK_PRIVATE_URL = BASE_URL_NEW + "book/";

    public  static String FRIENDS_URL = BASE_URL_NEW+"student/friends/";

    public  static String NEWS_LIKE_URL = BASE_URL_NEW+"school/news/";

    //public  static String NEWS_COMMENT_URL = BASE_URL+"school/news_comment_insert";

    public  static String NEWS_VIEW_URL = BASE_URL_NEW+"school/news/";

    public  static String VOICE_LESSON_URL = BASE_URL_NEW+"voicelecture/";

    //public  static String COMMENT_LIST_URL = BASE_URL+"school/news_comment_list";

    //public  static String ABOUT_URL = BASE_URL+"about?lang=";

    public  static String PROFILE_IMG_URL = BASE_URL_NEW+"student/picture";

    public  static String NOTIFICATION_URL = BASE_URL_NEW+"notification";

    public  static String SUGGESTION_SEND_URL = BASE_URL_NEW + "suggestion/new";

    public  static String TEACHING_URL = BASE_URL_NEW+"lesson/subject";

    public  static String LESSON_URL = BASE_URL_NEW+"lesson/subject/";

    public  static String LESSON_LIST_URL = BASE_URL_NEW+"lesson/";

    public  static String EXAM_SUBJECT_URL = BASE_URL_NEW+"exam/subject/";

    public  static String EXAM_QUS_ANS_URL = BASE_URL_NEW+"exam/";

    public  static String DEGREE_URL = BASE_URL_NEW+"student/degree?degree_type=";

    public  static String EXAM_SUBMIT_RESULT_URL = BASE_URL_NEW+"exam";

    public  static String TIMETABLE_URL = BASE_URL_NEW +"weeklyschedule/";
    public  static String TIMETABLEALL_URL = BASE_URL_NEW +"weeklyschedule";

    public  static String ADDACCOUNT_URL = BASE_URL_NEW + "accounts/add";

    public  static String ACCOUNT_LIST_URL = BASE_URL_NEW+"accounts";

    public  static String NEWS_UNLIKE_URL = BASE_URL_NEW+"news/";

    public  static String SCORE_URL = BASE_URL_NEW+"student/score";

    public static String SCHOOL_INFO_URL = BASE_URL_NEW+"school";

    public static String ACCOUNT_DELETE = BASE_URL_NEW+"accounts/delete";

    public static String AUDIOLIBRARY_LIST = BASE_URL_NEW + "voicelecture/subjects";
    public static String VOIDLECTURE_LIST = BASE_URL_NEW + "voicelecture/subject/";

    //public static String PRIVACY_URL = BASE_URL+"about/privacy";

    public static String LOGOUT_URL = BASE_URL_NEW+"logout";

    public static String APPTITLE = "SAEEMLIGHT";



}
