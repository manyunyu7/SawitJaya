package com.senjapagi.sawitjaya.util

object api {
    const val WEBSITE = "http://103.253.27.125:10000/sawitz/";
    const val BASE_URL = "http://103.253.27.125:10000/sawitz/index.php?/api/";
    const val UPLOAD = "$BASE_URL/user/jual/create";
    const val REGISTER = "$BASE_URL/register";
    const val LOGIN = "http://103.253.27.125:10000/sawitz/index.php?/api/login";

    const val USER_EDIT_PROFILE="http://103.253.27.125:10000/sawitz/index.php?/api/user/user_edit"

    const val STAFF_EDIT_PROFILE="http://103.253.27.125:10000/sawitz/index.php?/api/staff/user_edit"

    const val USER_DATA="${BASE_URL}user/user_profile"

    const val STAFF_DATA="${BASE_URL}staff/user_profile"


    const val PROFILE_PIC_URL = "http://103.253.27.125:10000/sawitz/assets/uploads/photos_profile/";
    const val CURRENT_PRICE = "${BASE_URL}price";

    const val USER_ORDER_ALL = "$BASE_URL/user/jual";
    const val USER_ORDER_ACTIVE = "$BASE_URL/user/jual/status/active";
    const val USER_ORDER_SUCCESS = "$BASE_URL/user/jual/status/successed";
    const val USER_ORDER_FAILED = "$BASE_URL/user/jual/status/failed";
    const val USER_ORDER_PROCESSED = "$BASE_URL/user/jual/status/processed";
    const val USER_ORDER_PHOTO = "$WEBSITE/assets/uploads/photos_sellrequest/";
//    api/{user-level admin/staff}/jual/{id}/take
//    (post)
//    (header) token

    const val STAFF_ORDER_ALL = "$BASE_URL/staff/jual/status/available";
    const val STAFF_ORDER_PROCESSED = "$BASE_URL/staff/jual/status/active";

    const val STAFF_ORDER_DETAIL = "$BASE_URL/staff/jual/";
    const val STAFF_TAKE_ORDER = "$BASE_URL/staff/jual/";


//    api/{user-level}/jual/{id}/{take/untake}
//    (post)
//    (header) token


    const val USER_ORDER_DETAIL = "$BASE_URL/user/jual/";


//    http://103.253.27.125:10000/sawitz/index.php?/api/user/jual/status/finished
//    api/user/jual/status/{status}
//    (header) token
//
//    status : waiting, processed, failed, status, active (waiting & processed)



}