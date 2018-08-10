package org.wso2.tg.jenkins

import org.wso2.tg.jenkins.util.Common



void info(message) {
    def a = new Common()
    def time = a.getTimestamp()
    echo "[INFO] : $time : $message"
}

void error(message) {
    def a = new Common()
    def time = a.getTimestamp()
    echo "[ERROR] : $time : $message"
}

void warn(message) {
    def a = new Common()
    def time = a.getTimestamp()
    echo "[WARN] : $time : $message"
}

//private def getTimestamp(Date date = new Date()) {
//    return date.format('yyyyMMddHHmmss', TimeZone.getTimeZone('GMT')) as String
//}

