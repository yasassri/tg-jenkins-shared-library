package org.wso2.tg.jenkins.alert

/**
 *  Manages slack notifications for the build.
 */
class SlackNotifier implements Serializable {

    /**
     * Sends out a slack notification to a given channel.
     * @param buildStatus status of the build
     * @param phase build phase
     * @param channel channel
     */
    void sendNotificcationn (String buildStatus = 'STARTED', phase, channel) {
        // build status of null means successful
        buildStatus = buildStatus ?: 'SUCCESS'

        // Default values
        def colorName = 'RED'
        def colorCode = '#FF0000'
        def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        def summary = "${subject} (${env.RUN_DISPLAY_URL}) in phase : $phase"

        // Override default values based on build status
        if (buildStatus == 'STARTED') {
            color = 'BLUE'
            colorCode = '#003EFF'
        } else if (buildStatus == 'SUCCESS') {
            color = 'GREEN'
            colorCode = '#00FF00'
        } else if (buildStatus == 'UNSTABLE') {
            color = "YELLOW"
            colorCode = "#FFFF00"
        } else {
            color = 'RED'
            colorCode = '#FF0000'
        }

        // Send notifications
        slackSend(channel: channel, color: colorCode, message: summary)

    }
}

