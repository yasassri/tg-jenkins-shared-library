package org.wso2.tg.jenkins

class Log implements Serializable {

    void info (String message) {
        def time = getTimestamp()
        def msg = "[INFO] : " + time + " : " + message
        echo msg.toString()
    }

    void error (String message) {
        def time = getTimestamp()
       // echo "[ERROR] : " + time " : " + $message
    }

    void warn (String message) {
        def time = getTimestamp()
        //echo "[WARN] : $time : $message"
    }

    private def getTimestamp(Date date = new Date()){
        return date.format('yyyyMMddHHmmss',TimeZone.getTimeZone('GMT')) as String
    }
}

