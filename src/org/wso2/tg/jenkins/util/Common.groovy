package org.wso2.tg.jenkins.util

def getTimestamp(Date date = new Date()) {
    return date.format('yyyyMMddHHmmss', TimeZone.getTimeZone('GMT')) as String
}
//@NonCPS
//def echoFunc(msg) {
//    echo "Test Function 01 $msg"
//}

//return this

//def loadProperties() {
//    node {
//        properties = readProperties file: "${TESTGRID_HOME}/config.properties"
//    }
//}