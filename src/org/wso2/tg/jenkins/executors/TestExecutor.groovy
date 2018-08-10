package org.wso2.tg.jenkins.executor

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

private String getParameters(file) {
    def tpyaml = readFile(file)
    def m = tpyaml =~ /(parameters:)([A-z \n:'0-9\.-]*)(provisioners)/
    // echo tpyaml
    def params = m[0][2].trim().split('\n')
    // echo Long.toString(params.size())
    def name = ""
    params = params.sort()
    for (String s : params) {
        name += s.split(":")[1]
    }
    //echo "This is the name" + name
    return name
}
