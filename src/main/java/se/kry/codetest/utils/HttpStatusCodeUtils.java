package se.kry.codetest.utils;

public class HttpStatusCodeUtils {

    public static ServiceStatus getStatus(int httpStatusCode) {
        if (String.valueOf(httpStatusCode).charAt(0) == '2' ||
                String.valueOf(httpStatusCode).charAt(0) == '3' ||
                String.valueOf(httpStatusCode).charAt(0) == '4'
        && (httpStatusCode != 404 && httpStatusCode != 429 && httpStatusCode != 408)) {
            return ServiceStatus.OK;
        } else if (httpStatusCode == 404 || String.valueOf(httpStatusCode).charAt(0) == '5') {
            return ServiceStatus.FAIL;
        }
        return ServiceStatus.FAIL;
    }


    /*

Service IS Created

NEW.  —— NEWLY CREATED — NEW WHEN

OK — Endpoint responded with OK / 4XX Except 404
FAIL = 5XX | 404

Helper method(HTTPCode) {
If (status code is Server Error || 404 }
Return FAIL
If (status code is 2XX, 3XX, 4xx && not (404 and 429, 408)
Return OK;
 */

}
