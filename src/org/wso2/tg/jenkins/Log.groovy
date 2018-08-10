package org.wso2.tg.jenkins

class Log implements Serializable {

    void info (message) {
        def time = getTimestamp()
        echo "[INFO] : $time : $message"
    }

    void error (message) {
        def time = getTimestamp()
        echo "[ERROR] : $time : $message"
    }

    void warn (message) {
        def time = getTimestamp()
        echo "[WARN] : $time : $message"
    }

    private def getTimestamp(Date date = new Date()){
        return date.format('yyyyMMddHHmmss',TimeZone.getTimeZone('GMT')) as String
    }

}

return this
