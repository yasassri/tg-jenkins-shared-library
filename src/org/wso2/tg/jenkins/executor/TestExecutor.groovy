package org.wso2.tg.jenkins.executor


class TestExecutor implements Serializable {

    def runPlan(tPlan, node) {
        name = getParameters("/testgrid/testgrid-home/jobs/${PRODUCT}/${tPlan}")
        notifyBuild("STARTED", "parallel \n Infra : " + name, "#build_status_verbose")
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
            notifyBuild(currentBuild.result, "Parallel \n Infra : " + name, "#build_status_verbose")
        }
        echo "RESULT: ${currentBuild.result}"

        script {
            uploadToS3()
        }
    }
}

