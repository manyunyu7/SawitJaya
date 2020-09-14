package com.senjapagi.sawitjaya.util

object api {
    const val WEBSITE = "https://apps.senjapagi.my.id/sawitz/"
    const val BASE_URL = "https://apps.senjapagi.my.id/sawitz/index.php?/api/"

    const val FORGOT_PASSWORD = "${BASE_URL}/forgotpassword"

    const val UPLOAD = "${BASE_URL}/user/jual/create"
    const val REGISTER = "${BASE_URL}/register"
    const val LOGIN = "${BASE_URL}login"

    const val USER_EDIT_PROFILE="${BASE_URL}user/user_edit"
    const val USER_HOME="${BASE_URL}user/home"

    const val STAFF_EDIT_PROFILE="${BASE_URL}staff/user_edit"

    const val USER_DATA="${BASE_URL}user/user_profile"

    const val STAFF_DATA="${BASE_URL}staff/user_profile"


    const val PROFILE_PIC_URL = "${WEBSITE}/assets/uploads/photos_profile/"
    const val SIGNATURE_PIC_URL = "${WEBSITE}/assets/uploads/signs_invoice/"
    const val INVOICE_PIC_URL = "${WEBSITE}/assets/uploads/photos_invoice/"

    const val CURRENT_PRICE = "${BASE_URL}price";

    const val USER_ORDER_ALL = "$BASE_URL/user/jual"
    const val USER_ORDER_ACTIVE = "$BASE_URL/user/jual/status/active"
    const val USER_ORDER_SUCCESS = "$BASE_URL/user/jual/status/successed"
    const val USER_ORDER_FAILED = "$BASE_URL/user/jual/status/failed"
    const val USER_ORDER_PROCESSED = "$BASE_URL/user/jual/status/processed"
    const val USER_ORDER_PHOTO = "$WEBSITE/assets/uploads/photos_sellrequest/"
    const val USER_GET_INVOICE = "$BASE_URL/user/jual/" //INSERT OPERATION

    const val USER_ORDER_DELETE = "$BASE_URL/user/jual/"//{id}/edit_status

    const val GET_PRICE = "$BASE_URL/prices"//{id}/edit_status

//    http://apps.senjapagi.my.id/sawitz/assets/uploads/photos_invoice/
//    api/{user-level admin/staff}/jual/{id}/take
//    (post)
//    (header) token

    const val STAFF_ORDER_ALL = "$BASE_URL/staff/jual/status/available";
    const val STAFF_ORDER_SUCCESSED = "$BASE_URL/staff/jual/status/successed";
    const val STAFF_ORDER_FAILED = "$BASE_URL/staff/jual/status/failed";
    const val STAFF_ORDER_PROCESSED = "$BASE_URL/staff/jual/status/active";

    const val STAFF_ORDER_DETAIL = "$BASE_URL/staff/jual/";
    const val STAFF_TAKE_ORDER = "$BASE_URL/staff/jual/";



    const val ADMIN_ORDER_ALL = "$BASE_URL/admin/jual/status/all/staff/all";
    const val ADMIN_ORDER_SUCCESSED = "$BASE_URL/admin/jual/status/successed/staff/all";
    const val ADMIN_ORDER_FAILED = "$BASE_URL/admin/jual/status/failed/staff/all";
    const val ADMIN_ORDER_PROCESSED = "$BASE_URL/admin/jual/status/active/staff/all";

    const val ADMIN_ORDER_DETAIL = "$BASE_URL/admin/jual/";
    const val ADMIN_TAKE_ORDER = "$BASE_URL/admin/jual/";
//    http://103.253.27.21:10000/sawitz/index.php?/api/staff/jual/8/invoice
    const val STAFF_UPLOAD_INVOICE = "$BASE_URL/staff/jual/";
//    http://103.253.27.21:10000/sawitz/index.php?/api/staff/jual/9/invoice
    const val STAFF_GET_INVOICE = "$BASE_URL/staff/jual/";




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