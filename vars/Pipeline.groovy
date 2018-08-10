import org.wso2.tg.jenkins.Log
import org.wso2.tg.jenkins.alert.Slack
import org.wso2.tg.jenkins.alert.Email
import org.wso2.tg.jenkins.util.Common
import org.wso2.tg.jenkins.executors.TestExecutor

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {
    def jobName = "test"
    if (jobName == "test") {
        pipeline {
            agent any
            stages {
                stage('Testing') {
                    steps {
                        script {
                            echo "This is a test"
                            def a = new Email()
                            a.send("This is a Test Email", "This is the Test Content")
                        }
                    }
                }
            }
        }
    } else {

        def alert = new Slack()
        def commonUtils = new Common()
        def testExecutor = new TestExecutor()
        properties = null

        pipeline {
            agent {
                node {
                    label ""
                    customWorkspace "/testgrid/testgrid-home/jobs/${JOB_BASE_NAME}"
                }
            }

            environment {
                TESTGRID_NAME = 'WSO2-TestGrid'
                TESTGRID_DIST_LOCATION = '/testgrid/testgrid-home/testgrid-dist/'
                TESTGRID_HOME = '/testgrid/testgrid-home/'

                PRODUCT = "${JOB_BASE_NAME}"

                TESTGRID_YAML_LOCATION = "${INFRA_LOCATION}/jobs/${PRODUCT}/testgrid.yaml"

                AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
                AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
                tomcatUsername = credentials('TOMCAT_USERNAME')
                tomcatPassword = credentials('TOMCAT_PASSWORD')
                PWD = pwd()
                JOB_CONFIG_YAML = "job-config.yaml"
                JOB_CONFIG_YAML_PATH = "${PWD}/${JOB_CONFIG_YAML}"

                PRODUCT_GIT_URL = "${PRODUCT_GIT_URL}"
                PRODUCT_GIT_BRANCH = "${PRODUCT_GIT_BRANCH}"
                PRODUCT_DIST_DOWNLOAD_API = "${PRODUCT_DIST_DOWNLOAD_API}"
            }

            tools {
                jdk 'jdk8'
            }

            stages {
                stage('Preparation') {
                    steps {
                        script {
                            try {
                                alert.sendNotification('STARTED', "Initiation", "#build_status_verbose")
                                alert.sendNotification('STARTED', "Initiation", "#build_status")
                                echo pwd()
                                deleteDir()

                                // Clone scenario repo
                                sh "mkdir -p ${SCENARIOS_LOCATION}"
                                dir("${SCENARIOS_LOCATION}") {
                                    git branch: 'master', url: "${SCENARIOS_REPOSITORY}"
                                }

                                // Clone infra repo
                                sh "mkdir -p ${INFRA_LOCATION}"
                                dir("${INFRA_LOCATION}") {
                                    git branch: 'master', url: "${INFRASTRUCTURE_REPOSITORY}"
                                }
                                writeFile file: "${INFRA_LOCATION}/deploy.sh", text: '#!/bin/sh'

                                sh """
              echo ${TESTGRID_NAME}
              cd ${TESTGRID_DIST_LOCATION}
              cd ${TESTGRID_NAME}

              sed -i 's/-Xms256m -Xmx1024m/-Xmx2G -Xms2G/g' testgrid
              """

                                // Get testgrid.yaml from jenkins managed files
                                configFileProvider(
                                        [configFile(fileId: "wso2am-intg-testgrid-yaml", targetLocation:
                                                "${TESTGRID_YAML_LOCATION}")]) {
                                }

                                configFileProvider([configFile(fileId: '3a63892b-06b8-483a-8a0d-74dffaf69c3d', targetLocation: 'workspace/testgrid-key.pem', variable: 'TESTGRIDKEY')]) {
                                    sh """
                echo 'keyFileLocation: workspace/testgrid-key.pem' > ${JOB_CONFIG_YAML_PATH}
                chmod 400 workspace/testgrid-key.pem
                """
                                }

                                sh """
              echo 'infrastructureRepository: ${INFRA_LOCATION}/' >> ${JOB_CONFIG_YAML_PATH}
              echo 'deploymentRepository: ${INFRA_LOCATION}/' >> ${JOB_CONFIG_YAML_PATH}
              echo 'scenarioTestsRepository: ${SCENARIOS_LOCATION}' >> ${JOB_CONFIG_YAML_PATH}
              echo 'testgridYamlLocation: ${TESTGRID_YAML_LOCATION}' >> ${JOB_CONFIG_YAML_PATH}
              echo 'properties:' >> ${JOB_CONFIG_YAML_PATH}
              echo '  PRODUCT_GIT_URL: ${PRODUCT_GIT_URL}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  PRODUCT_GIT_BRANCH: ${PRODUCT_GIT_BRANCH}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  PRODUCT_DIST_DOWNLOAD_API: ${PRODUCT_DIST_DOWNLOAD_API}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  SQL_DRIVERS_LOCATION_UNIX: ${SQL_DRIVERS_LOCATION_UNIX}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  SQL_DRIVERS_LOCATION_WINDOWS: ${SQL_DRIVERS_LOCATION_WINDOWS}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  SSH_KEY_LOCATION: ${PWD}/workspace/testgrid-key.pem' >> ${JOB_CONFIG_YAML_PATH}
              echo '  REMOTE_WORKSPACE_DIR_UNIX: ${REMOTE_WORKSPACE_DIR_UNIX}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  REMOTE_WORKSPACE_DIR_WINDOWS: ${REMOTE_WORKSPACE_DIR_WINDOWS}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  gitURL: ${PRODUCT_GIT_URL}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  gitBranch: ${PRODUCT_GIT_BRANCH}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  productDistDownloadApi: ${PRODUCT_DIST_DOWNLOAD_API}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  sqlDriversLocationUnix: ${SQL_DRIVERS_LOCATION_UNIX}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  sqlDriversLocationWindows: ${SQL_DRIVERS_LOCATION_WINDOWS}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  sshKeyFileLocation: ${PWD}/workspace/testgrid-key.pem' >> ${JOB_CONFIG_YAML_PATH}
              echo '  RemoteWorkspaceDirPosix: ${REMOTE_WORKSPACE_DIR_UNIX}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  LATEST_PRODUCT_RELEASE_API: ${LATEST_PRODUCT_RELEASE_API}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  LATEST_PRODUCT_BUILD_ARTIFACTS_API: ${LATEST_PRODUCT_BUILD_ARTIFACTS_API}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  TEST_MODE: ${TEST_MODE}' >> ${JOB_CONFIG_YAML_PATH}
              echo '  runOnBranch: "false"' >> ${JOB_CONFIG_YAML_PATH}

              echo The job-config.yaml:
              cat ${JOB_CONFIG_YAML_PATH}
              """

                                stash name: "${JOB_CONFIG_YAML}", includes: "${JOB_CONFIG_YAML}"

                                sh """
              cd ${TESTGRID_HOME}/testgrid-dist/${TESTGRID_NAME}
              ./testgrid generate-test-plan \
                  --product ${PRODUCT} \
                  --file ${JOB_CONFIG_YAML_PATH}
              """
                                dir("${PWD}") {
                                    stash name: "test-plans", includes: "test-plans/**"
                                }
                            } catch (e) {
                                currentBuild.result = "FAILED"
                            } finally {
                                alert.sendNotification(currentBuild.result, "preparation", "#build_status_verbose")
                            }
                        }
                    }

                }

                stage('parallel-run') {
                    steps {
                        script {
                            def name = "unknown"
                            try {
                                def parallelExecCount = 12
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
                                parallel tests
                            } catch (e) {
                                currentBuild.result = "FAILED"
                                alert.sendNotification(currentBuild.result, "Parallel", "#build_status_verbose")
                            }
                        }
                    }
                }
            }

            post {
                always {
                    script {
                        try {
                            sh """
            cd ${TESTGRID_HOME}/testgrid-dist/${TESTGRID_NAME}
            ./testgrid finalize-run-testplan \
            --product ${PRODUCT} --workspace ${PWD}
            """

                            sh """
             cd ${TESTGRID_HOME}/testgrid-dist/${TESTGRID_NAME}
            ./testgrid generate-report \
            --product ${PRODUCT} \
            --groupBy scenario
            """
                            // Generate email-able report
                            /* Prereq:
                           1. Needs TestSuit.txt and output.properties files in relevant scenario directory.
                           2. DB needs to be updated on integration test result statues.
                        */
                            def buckerName = getS3BucketName()
                            sh """
                export DISPLAY=:95.0
                cd ${TESTGRID_HOME}/testgrid-dist/${TESTGRID_NAME}
                ./testgrid generate-email \
                --product ${PRODUCT} \
                --workspace ${PWD}

                aws s3 sync ${TESTGRID_HOME}/jobs/${PRODUCT}/builds/ s3://${buckerName}/charts/${PRODUCT}/ --exclude "*" --include "*.png" --acl public-read
                """
                            script {

                                //Send email for failed results.
                                if (fileExists("${PWD}/builds/SummarizedEmailReport.html")) {
                                    def emailBody = readFile "${PWD}/builds/SummarizedEmailReport.html"
                                    emailext(to: "${EMAIL_TO_LIST},kasung@wso2.com,lasanthad@wso2.com,yasassri@wso2.com",
                                            subject: "'${env.JOB_NAME}' Integration Test Failure! #(${env.BUILD_NUMBER})",
                                            body: "${emailBody}", mimeType: 'text/html'
                                    )
                                } else {
                                    echo "No SummarizedEmailReport.html file found!!"
                                    emailext(to: 'sameeraw@wso2.com,lasanthad@wso2.com,yasassri@wso2.com,kasung@wso2.com',
                                            subject: "'${env.JOB_NAME}'#(${env.BUILD_NUMBER}) - SummarizedEmailReport.html file not found",
                                            body: "Could not find the summarized email report ${env.BUILD_URL}. This is an error in " +
                                                    "testgrid.",
                                            mimeType: 'text/html', attachmentsPattern: "**/builds/*.png"
                                    )
                                }
                            }
                            // Delete logs and reports after upload
                            //     dir("${TESTGRID_HOME}/jobs/${PRODUCT}") {
                            //         sh """
                            // find . -maxdepth 1 -type f \\( -name "*.log" -o -name "*.html" \\) -delete
                            // """
                            //     }
                        } catch (e) {
                            currentBuild.result = "FAILED"
                        } finally {
                            alert.sendNotification(currentBuild.result, "completed", "#build_status")
                            alert.sendNotification(currentBuild.result, "completed", "#build_status_verbose")
                        }
                    }
                }
            }
        }
    }
}
