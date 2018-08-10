package org.wso2.tg.jenkins.util

//import com.cloudbees.groovy.cps.NonCPS

class Common implements Serializable {
    def echoFunc(msg) {
        echo "Test Function 01 $msg"
    }
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