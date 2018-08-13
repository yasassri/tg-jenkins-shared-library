/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.wso2.tg.jenkins.alert.Slack
import org.wso2.tg.jenkins.alert.Email
import org.wso2.tg.jenkins.util.Common
import org.wso2.tg.jenkins.util.AWSUtils
import org.wso2.tg.jenkins.executors.TestExecutor

// First we need to validate all the properties, variables for not null or empty in //vars

// The pipeline should resite in a call block
def call() {
    def jobName = "dev"
    if (jobName == "test") {
        pipeline {
            agent any
            stages {
                stage('Testing') {
                    steps {
                        script {
                            echo "This is a test"

                            def a = new Email()
                            if (fileExists("/home/ubuntu/tmp/a.html")) {
                                def emailBody = readFile "/home/ubuntu/tmp/a.html"
                                a.send("'${env.JOB_NAME}' Integration Test Failure! #(${env.BUILD_NUMBER})",
                                        "${emailBody}")
                            } else {
                                echo "No SummarizedEmailReport.html file found!!"
                                a.send("'${env.JOB_NAME}'#(${env.BUILD_NUMBER}) - SummarizedEmailReport.html " +
                                        "file not found", "Could not find the summarized email report ${env.BUILD_URL}. This is an error in " +
                                        "testgrid.")
                            }
                           // a.send("This is a Test Email", "This is the Test Content")
                        }
                    }
                }
            }
        }
    } else {

        def alert = new Slack()
        def email = new Email()
        def commonUtils = new Common()
        def awsHelper = new AWSUtils()
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


                stage('parallel-run') {
                    steps {
                        script {
                            def name = "unknown"
//                            try {
//                                def tests = testExecutor.getTestExecutionMap()
//                                parallel tests
//                            } catch (e) {
//                                currentBuild.result = "FAILED"
//                                alert.sendNotification(currentBuild.result, "Parallel", "#build_status_verbose")
//                            }
                        }
                    }
                }
            }

            post {
                always {
                    script {
                        try {

                            script {
                                //Send email for failed results.
                                if (fileExists("/home/ubuntu/tmp/a.html")) {
                                    def emailBody = readFile "/home/ubuntu/tmp/a.html"
                                    email.send("'${env.JOB_NAME}' Integration Test Failure! #(${env.BUILD_NUMBER})", "${emailBody}")
                                } else {
                                    echo "No SummarizedEmailReport.html file found!!"
                                    email.send("'${env.JOB_NAME}'#(${env.BUILD_NUMBER}) - SummarizedEmailReport.html " +
                                            "file not found", "Could not find the summarized email report ${env.BUILD_URL}. This is an error in " +
                                            "testgrid.")
                                }
                            }
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
