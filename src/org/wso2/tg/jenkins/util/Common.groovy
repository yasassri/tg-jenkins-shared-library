package org.wso2.tg.jenkins.util

import com.cloudbees.groovy.cps.NonCPS

//class Common implements Serializable {
//    def echoFunc() {
//        //echo "Test Function 01"
//    }
//}
@NonCPS
def echoFunc() {
    echo "Test Function 01"
}

return this