package org.wso2.tg.jenkins.executors

import org.wso2.tg.jenkins.util.Common
import org.wso2.tg.jenkins.util.AWSUtils
import org.wso2.tg.jenkins.alert.Slack


def runPlan(tPlan, node) {
    def commonUtil = new Common()
    def notfier = new Slack()
    def awsHelper = new AWSUtils()
    name = commonUtil.getParameters("/testgrid/testgrid-home/jobs/${PRODUCT}/${tPlan}")
    notfier.sendNotification("STARTED", "parallel \n Infra : " + name, "#build_status_verbose")
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
        notfier.sendNotification(currentBuild.result, "Parallel \n Infra : " + name, "#build_status_verbose")
    }
    echo "RESULT: ${currentBuild.result}"

    script {
        awsHelper.uploadToS3()
    }
}

def getTestExecutionMap() {
    def parallelExecCount = 12
    def name = "unknown"
    def tests = [:]
    def files = findFiles(glob: '**/test-plans/*.yaml')
    for (int f = 1; f < parallelExecCount + 1 && f <= files.length; f++) {
        def executor = f
        name = commonUtils.getParameters("${PWD}/test-plans/" + files[f - 1].name)
        echo name
        tests["${name}"] = {
            node {
                stage("Parallel Executor : ${executor}") {
                    script {
                        int processFileCount = 0;
                        if (files.length < parallelExecCount) {
                            processFileCount = 1;
                        } else {
                            processFileCount = files.length / parallelExecCount;
                        }
                        if (executor == parallelExecCount) {
                            for (int i = processFileCount * (executor - 1); i < files.length; i++) {
                                // Execution logic
                                testExecutor.runPlan(files[i], "node1")
                            }
                        } else {
                            for (int i = 0; i < processFileCount; i++) {
                                int fileNo = processFileCount * (executor - 1) + i
                                testExecutor.runPlan(files[fileNo], "node1")
                            }
                        }
                    }
                }
            }
        }
    }
    return tests
}

