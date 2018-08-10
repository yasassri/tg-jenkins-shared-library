package org.wso2.tg.jenkins.executors

import org.wso2.tg.jenkins.util.Common
import org.wso2.tg.jenkins.util.AWSUtils
import org.wso2.tg.jenkins.alert.Slack


def runPlan(tPlan, node) {
    def commonUtil = new Common()
    def notfier = new Slack()
    def awsHelper = new AWSUtils()
    name = commonUtil.getParameters("/testgrid/testgrid-home/jobs/${PRODUCT}/${tPlan}")
    notfier.notifyBuild("STARTED", "parallel \n Infra : " + name, "#build_status_verbose")
    echo "Executing Test Plan : ${tPlan} On node : ${node}"
    try {
        echo "Running Test-Plan: ${tPlan}"
        sh "java -version"
        unstash name: "${JOB_CONFIG_YAML}"
        dir("${PWD}") {
            unstash name: "test-plans"
        }
        sh """
      echo "Before PWD"
      pwd
      cd ${PWD}/${SCENARIOS_LOCATION}
      git clean -fd
      cd ${TESTGRID_HOME}/testgrid-dist/${TESTGRID_NAME}
      ./testgrid run-testplan --product ${PRODUCT} \
      --file "${PWD}/${tPlan}"
      """
        script {
            truncateTestRunLog()
        }
    } catch (Exception err) {
        echo "Error : ${err}"
        currentBuild.result = 'UNSTABLE'
    } finally {
        notfier.notifyBuild(currentBuild.result, "Parallel \n Infra : " + name, "#build_status_verbose")
    }
    echo "RESULT: ${currentBuild.result}"

    script {
        awsHelper.uploadToS3()
    }
}

